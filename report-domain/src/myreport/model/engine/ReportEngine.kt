package myreport.model.engine

import myreport.model.IReportRenderer
import myreport.model.Page
import myreport.model.Point
import myreport.model.Report
import myreport.model.ReportContext
import myreport.model.SectionType
import myreport.model.Size
import myreport.model.controls.Control
import myreport.model.controls.IDataControl
import myreport.model.controls.Line
import myreport.model.controls.ReportHeaderSection
import myreport.model.controls.Section
import myreport.model.controls.SubReport
import myreport.model.controls.TextBlock
import myreport.model.data.Field
import myreport.model.data.FieldKind
import myreport.model.data.IDataSource

class ReportEngine {
    internal var reportRenderer: IReportRenderer
    private val report: Report
    private var source: IDataSource? = null
    internal var context: ReportContext? = null
    internal var currentPage: Page? = null
    private var beforeFistDetailSection = true
    private var currentSection: Section? = null
    private var currentSectionSpans: MutableList<SpanInfo>? = null

    private var currentSectionOrderedControls = mutableListOf<Control>()
    private var currentSectionControlsBuffer = mutableListOf<Control>()
    private var currentPageFooterSectionControlsBuffer = mutableListOf<Control>()
    private var subreportSectionControlsBuffer = mutableListOf<Control>()
    private var afterReportHeader = false

    private var currentSectionExtendedLines = mutableListOf<Line>()
    private var spanCorrection = 0f

    var isSubreport: Boolean = false

    private var dataSourceHasNextRow = true
    private var stop = false

    var parameters: MutableMap<String, Field>? = null

    val startingSection: Section?

    constructor(report: Report, reportRenderer: IReportRenderer) {
        this.report = report
        this.reportRenderer = reportRenderer
        source = report.DataSource
        context = ReportContext()

        nextPage()

        startingSection =
            report.sections.find { section -> section.sectionType == SectionType.PAGE_FOOTER }
                ?: report.sections.find { section -> section.sectionType == SectionType.REPORT_HEADER }
                        ?: report.sections.find { section -> section.sectionType == SectionType.PAGE_HEADER }
                        ?: report.sections.find { section -> section.sectionType == SectionType.DETAILS }
                        ?: report.sections.find { section -> section.sectionType == SectionType.REPORT_FOOTER }

        check(startingSection != null) { "Report without a starting section" }

        selectCurrentSectionByTemplateSection(startingSection)
    }

    /**
     *  Create a new page for report
     */
    fun nextPage() {
        val pageFooterSection = report.sections.find { it.sectionType == SectionType.PAGE_FOOTER }
        val pageFooterHeight = pageFooterSection?.height ?: 0f

        addControlsToCurrentPage(report.height - pageFooterHeight, currentPageFooterSectionControlsBuffer)

        spanCorrection = 0f
        context?.currentPageIndex++
        currentPage = Page().apply { pageNumber = context?.currentPageIndex ?: 0 }
        context?.heightLeftOnCurrentPage = report.height
        context?.heightUsedOnCurrentPage = 0f
        currentPageFooterSectionControlsBuffer.clear()

        selectCurrentSectionByTemplateSection(startingSection)
    }

    private val controlsFromPreviousSectionPage: MutableMap<String, MutableList<Control>> = mutableMapOf()

    /**
     *  Select current section by template
     */
    private inline fun <reified T : Section> selectCurrentSectionByTemplateSection(section: T?): T {

        var newSection: T? = null

        if (controlsFromPreviousSectionPage.contains(section?.name)) {
            currentSectionOrderedControls = controlsFromPreviousSectionPage[section?.name]!!.toMutableList()
            controlsFromPreviousSectionPage.remove(section?.name)
            newSection = currentSectionOrderedControls[0] as T
            currentSectionOrderedControls.removeAt(0)
        } else {
            newSection = section?.createControl() as T
            newSection.format()
            currentSectionOrderedControls = newSection.controls
            currentSectionOrderedControls.sortBy { ctl -> ctl.top }
        }

        currentSectionSpans?.clear()
        currentSectionExtendedLines.clear()
        newSection.location = Point(section?.location!!.x, 0f)
        currentSection = newSection

        currentSectionControlsBuffer.clear()

        return newSection
    }


    fun addControlsToCurrentPage(span: Float) {

        if (currentSection?.sectionType != SectionType.PAGE_FOOTER) {
            addControlsToCurrentPage(span + spanCorrection, currentSectionOrderedControls)
        } else {
            currentPageFooterSectionControlsBuffer.addAll(currentSectionControlsBuffer)
            spanCorrection -= currentSection!!.height
        }

        if (subreportSectionControlsBuffer.count() > 0) {
            addControlsToCurrentPage(span + spanCorrection, subreportSectionControlsBuffer)
        }

        currentSectionControlsBuffer.clear()
        subreportSectionControlsBuffer.clear()
    }

    fun addControlsToCurrentPage(span: Float, controls: List<Control>) {

        for (control in controls) {
            control.top += span
            currentPage?.controls?.add(control)
        }
    }

    fun process() {
        nextRecord()

        while (!processReportPage()) {
            nextPage()
        }

        // set NumberOfPages field wherever it appear
        for (i in 0..report.pages.indexOf(currentPage)) {

            for (item in report.pages[i].controls) {

                if (item is IDataControl) {
                    val dc: IDataControl = item as IDataControl
                    if (dc.fieldName == "#NumberOfPages")
                        dc.text = report.pages.size.toString()
                }
            }
        }

        //TODO: see if is necessary to reset the data source
        //if (source != null)
        //source.reset()

        //onAfterReportProcess()
    }

    fun processReportPage(): Boolean {
        var result: Boolean
        stop = false

        do {
            currentSection?.templateControl?.beforeControlProcessing?.invoke(context!!, currentSection!!)

            if (!currentSectionControlsBuffer.contains(currentSection!!))
                currentSectionControlsBuffer.add(currentSection!!)

            result = processSectionUptoHeightThreshold(context!!.heightLeftOnCurrentPage)

            if (!result && currentSection!!.keepTogether)
                currentSectionControlsBuffer.clear()

            addControlsToCurrentPage(context!!.heightUsedOnCurrentPage)

            context?.heightUsedOnCurrentPage -= currentSection!!.height
            context?.heightUsedOnCurrentPage += currentSection!!.height

            if (result)
                nextSection()
            else
                return false

        } while (!stop)

        return result
    }

    private var marginBottom = 0f

    /**
     * Processes the section up to heightTreshold
     *
     * @param heightThreshold
     * maximum height (starting from current section location.y) after which page will break
     *
     * @return Boolean,  *true* if finished pocessing section and *false* while not
     */
    private fun processSectionUptoHeightThreshold(heightThreshold: Float): Boolean {
        var span = 0f
        var x = 0f
        var maxHeight = 0f
        var maxControlBottom = 0f
        var tmpSpan = 0f
        var result = true
        var realBreak = 0f
        var breakControlMax = 0f
        var allKeeptogether = false
        var heightThresholdIncludingBottomMargin = 0f

        if (currentSectionOrderedControls.size > 0)
            maxControlBottom = currentSectionOrderedControls.maxOf { ctl -> ctl.bottom }

        marginBottom = currentSection!!.height - maxControlBottom
        if (marginBottom < 0) marginBottom = 0f

        heightThresholdIncludingBottomMargin = heightThreshold - marginBottom

        for (i in currentSectionOrderedControls.indices) {
            var control = currentSectionOrderedControls[i]
            tmpSpan = Float.MIN_VALUE
            if (!control.isVisible) continue

            if (control is Line && (control as Line).extendToBottom) {
                var line = control as Line
                currentSectionExtendedLines.add(line)
            }

            if (source != null && control is IDataControl) {
                val dc = control as IDataControl
                if (!dc.fieldName.isNullOrBlank()) {
                    when (dc.fieldKind) {
                        FieldKind.PARAMETER -> {
                            if (parameters!!.containsKey(dc.fieldName)) {
                                val parameter = parameters!![dc.fieldName]
                                dc.text = parameter?.getValue(parameter.defaultValue, dc.fieldTextFormat).toString()
                            }
                        }

                        FieldKind.EXPRESSION -> {
                            if (dc.fieldName == "#PageNumber")
                                dc.text = context?.currentPageIndex.toString()
                            else if (dc.fieldName == "#RowNumber")
                                dc.text = context?.rowIndex.toString()
                        }

                        FieldKind.DATA -> {
                            if (source!!.containsField(dc.fieldName!!))
                                dc.text = source?.getValue(dc.fieldName!!, dc.fieldTextFormat).toString()
                        }
                    }
                }
            }

            val y = control.top + span
            var controlSize = reportRenderer.measureControl(control)
            currentSectionSpans?.forEach { item ->
                if (y > item.threshold)
                    tmpSpan = Math.max(tmpSpan, item.span)
            }

            // adjust the top of the control if some control before grew the height
            span = if (tmpSpan == Float.MIN_VALUE) 0f else tmpSpan
            control.top += span

            if (control is SubReport) {
                val sr: SubReport = control
                var maxSubreportHeight = ((heightThreshold - span) - sr.top)
                sr.processUpToPage(reportRenderer, maxSubreportHeight)

                if (!(sr.engine.context?.heightUsedOnCurrentPage!! > maxSubreportHeight)) {
                    controlSize = Size(sr.width, sr.engine.context?.heightUsedOnCurrentPage!!)
                    subreportSectionControlsBuffer.addAll(sr.engine.currentPage?.controls!!)
                    sr.engine.currentPage?.controls?.clear()

                    if (!sr.finished && sr.canGrow) {
                        storeSectionForNextPage()

                        var subreportClone = sr.createControl() as SubReport
                        subreportClone.top -= 0
                        subreportClone.engine = sr.engine
                        subreportClone.height = subreportClone.height

                        storeControlForNextSection(subreportClone)
                        sr.engine.nextPage()
                    }

                    if (!sr.finished)
                        result = false
                } else
                    println("error:")
            }

            var heightBeforeGrow = control.height
            var bottomBeforeGrow = control.bottom
            control.size = controlSize!!

            if (control.bottom <= heightThreshold) {

                // something that happen that need to know if all conrols need
                // to be together in the current section or not.
                if (!allKeeptogether)
                    currentSectionControlsBuffer.add(control)
                else {
                    storeSectionForNextPage()
                    val controlToStore = control
                    controlToStore.top -= realBreak
                    controlToStore.height = heightBeforeGrow
                    storeControlForNextSection(controlToStore)
                }
            } else {
                result = false
                storeSectionForNextPage()
                if (!currentSection?.keepTogether!!) {
                    breakControlMax = control.height - ((control.top + control.height) - heightThreshold)

                    if (realBreak == 0f)
                        realBreak = heightThreshold

                    if (control.top > heightThreshold) {
                        val controlToStore = control
                        controlToStore.top -= realBreak
                        controlToStore.height = heightBeforeGrow
                        storeControlForNextSection(controlToStore)
                        continue
                    }

                    var brokenControl = reportRenderer.breakOffControlAtMostAtHeight(control, breakControlMax)
                    var size = reportRenderer.measureControl(control)
                    control.size = size!!
                    realBreak = heightThreshold - (breakControlMax - brokenControl[0]?.height!!)

                    if (control.bottom > heightThreshold) {
                        storeControlForNextSection(control)
                        if (brokenControl[0] is TextBlock)
                            if ((brokenControl[0] as TextBlock).fieldName.isNullOrBlank())
                                (brokenControl[0] as TextBlock).text += (brokenControl[1] as TextBlock).text
                    } else {
                        currentSectionControlsBuffer.add(brokenControl[0]!!)
                        storeControlForNextSection(brokenControl[1]!!)

                        if (brokenControl[1] is TextBlock)
                            (brokenControl[1] as TextBlock).fieldName = null
                    }

                } else {
                    var controlToStore = control
                    controlToStore.top -= realBreak
                    controlToStore.height = heightBeforeGrow
                    controlToStore.width = controlToStore.templateControl?.width!!

                    if (!allKeeptogether) {

                        for (w in 1..currentSectionControlsBuffer.size) {
                            currentSectionControlsBuffer[w].height =
                                currentSectionControlsBuffer[w].templateControl?.height!!
                            currentSectionControlsBuffer[w].width =
                                currentSectionControlsBuffer[w].templateControl?.width!!

                            controlsFromPreviousSectionPage[currentSection?.name]!!.add(currentSectionControlsBuffer[w])
                        }

                        allKeeptogether = true
                    }

                    storeControlForNextSection(controlToStore)

                    continue
                }
            }

            if (currentSection!!.canGrow && maxHeight <= control.bottom)
                maxHeight = Math.max(control.bottom, maxHeight)

            if (!result)
                if (realBreak > 0)
                    maxHeight = Math.max(realBreak, maxHeight)

            currentSectionSpans?.add(
                SpanInfo(threshold = bottomBeforeGrow, span = span + control.bottom - bottomBeforeGrow)
            )
        }

        var sectionHeightWithMargin: Float = maxHeight + marginBottom
        if (!result)
            currentSection?.height = heightThreshold
        else if ((currentSection?.canGrow!! && currentSection?.height!! < sectionHeightWithMargin) ||
            (currentSection?.canShrink!! && currentSection?.height!! > sectionHeightWithMargin)
        )
            currentSection?.height = sectionHeightWithMargin
        else
            currentSection?.height = Math.max(currentSection?.height!!, heightThreshold)

        for (lineItem in currentSectionExtendedLines) {

            if (lineItem.location.y == lineItem.end.y) {
                lineItem.location = Point(lineItem.location.x, currentSection?.height!! - lineItem.lineWidth / 2)
                lineItem.end = Point(lineItem.end.x, currentSection?.height!! - lineItem.lineWidth / 2)
            } else if (lineItem.location.y > lineItem.end.y)
                lineItem.location = Point(lineItem.location.x, currentSection?.height!!)

            if (!result) {
                var newCtrl = lineItem.createControl()

                if (lineItem.location.y > lineItem.end.y)
                    lineItem.isVisible = false
                newCtrl.top = 0f
                storeSectionForNextPage()
                controlsFromPreviousSectionPage[currentSection?.name]!!.add(newCtrl)
            }
        }

        sectionToStore = null

        if (!currentSection?.canGrow!!) {
            controlsFromPreviousSectionPage.remove(currentSection?.name)
            result = true
        }

        return result
    }

    private var sectionToStore: Section? = null

    private fun storeSectionForNextPage() {
        if (!controlsFromPreviousSectionPage.containsKey(currentSection?.name!!)) {
            sectionToStore = currentSection?.createControl() as Section
            val controlsToNextPage = mutableListOf<Control>()
            controlsToNextPage.add(sectionToStore!!)
            controlsFromPreviousSectionPage.put(currentSection?.name!!, controlsToNextPage)
            sectionToStore?.height = 0f
        }
    }

    private fun storeControlForNextSection(controlToStore: Control) {
        controlsFromPreviousSectionPage[currentSection?.name]!!.add(controlToStore)
        sectionToStore?.height = Math.max(sectionToStore!!.height, controlToStore.bottom + marginBottom)
    }

    private fun nextSection() {
        when (currentSection!!.sectionType) {

            SectionType.PAGE_HEADER -> {
                if (context!!.currentPageIndex > 1) {
                    val pageFooter = report.sections.find { it.sectionType == SectionType.PAGE_FOOTER }
                    selectCurrentSectionByTemplateSection(pageFooter)
                } else
                    setDetailsOrGroup()
            }

            SectionType.PAGE_FOOTER -> {
                if (!afterReportHeader) {
                    val reportHeader = report.sections.find { it.sectionType == SectionType.REPORT_HEADER }
                    selectCurrentSectionByTemplateSection(reportHeader)
                } else
                    setDetailsOrGroup()
            }

            SectionType.REPORT_HEADER -> {
                val reportHeader =
                    report.sections.find { it.sectionType == SectionType.REPORT_HEADER } as? ReportHeaderSection
                if (reportHeader?.breakPageAfter!!) {
                    nextPage()
                    stop = true
                } else {
                    if (context?.currentPageIndex == 1)
                        selectCurrentSectionByTemplateSection(reportHeader)
                    else
                        setDetailsOrGroup()
                }

                afterReportHeader = true

            }

            SectionType.DETAILS -> setDetailsOrGroup()

            SectionType.REPORT_FOOTER -> {
                val reportFooter = report.sections.find { it.sectionType == SectionType.REPORT_FOOTER }
                addControlsToCurrentPage(
                    report.height - reportFooter!!.height,
                    currentPageFooterSectionControlsBuffer
                )
                stop = true

            }

            else -> {
                //TODO: others section type like GROUPs sections
            }

        }

        if (!currentSection!!.isVisible)
            nextSection()
    }

    private fun setDetailsOrGroup() {
        val detailSection = report.sections.find { it.sectionType == SectionType.DETAILS }

        if (!controlsFromPreviousSectionPage.contains(detailSection?.name) && !beforeFistDetailSection)
            nextRecord()

        if (dataSourceHasNextRow || beforeFistDetailSection)
            selectCurrentSectionByTemplateSection(detailSection)
        else {
            val reportHeader = report.sections.find { it.sectionType == SectionType.REPORT_HEADER }

            selectCurrentSectionByTemplateSection(reportHeader)
        }

        beforeFistDetailSection = false
    }

    private fun nextRecord() {
        dataSourceHasNextRow = source!!.moveNext()
        context?.rowIndex++

    }
}

internal data class SpanInfo(internal var threshold: Float, internal var span: Float)
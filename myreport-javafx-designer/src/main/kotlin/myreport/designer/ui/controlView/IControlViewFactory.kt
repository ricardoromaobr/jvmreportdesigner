package myreport.designer.ui.controlView

import myreport.model.controls.Control

interface IControlViewFactory {
    fun createControlView(control: Control, sectionView: SectionView): ControlViewBase
}
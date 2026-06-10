package myreport.designer.ui.widgets.propertygrid

import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.control.cell.TreeItemPropertyValueFactory
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color as FxColor
import javafx.util.Callback
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaType

import myreport.model.Point
import myreport.model.Border
import myreport.model.Color
import myreport.model.PaperSize
import myreport.model.Report
import myreport.model.controls.Section
import myreport.model.controls.TextBlock
import myreport.model.controls.Image

class PropertyItem(
    val name: String,
    val value: Any?,
    val property: KProperty1<out Any, *>?,
    val ownerObject: Any?,
    val children: List<PropertyItem> = emptyList()
) {
    val nameProperty = SimpleStringProperty(name)
    val valueProperty = SimpleObjectProperty<Any?>(value)

    val isReadOnly: Boolean
        get() = property !is KMutableProperty1<*, *>

    val displayValue: String
        get() = formatValue(value)

    private fun formatValue(v: Any?): String {
        return when (v) {
            null -> ""
            is Point -> "(${v.x}, ${v.y})"
            is Border -> "[L=${v.leftWidth}, T=${v.topWidth}, R=${v.rightWidth}, B=${v.bottomWidth}]"
            is Color -> "Color(${v.r}, ${v.g}, ${v.b}, ${v.a})"
            is PaperSize -> v.paperSizeType.name
            is Section -> v.name
            is TextBlock -> "TextBlock"
            is Image -> "Image"
            is Report -> v.title ?: "Report"
            is Enum<*> -> v.name
            else -> v.toString()
        }
    }
}

class PropertyGrid : Region() {
    val treeTableView: TreeTableView<PropertyItem> = TreeTableView()
    var objectType: Class<*>? = null

    private var targetObject: Any? = null

    init {
        val nameColumn = TreeTableColumn<PropertyItem, String>("Propriedade").apply {
            prefWidth = 180.0
            setCellValueFactory { it.value.value?.nameProperty ?: SimpleStringProperty("") }
        }

        val valueColumn = TreeTableColumn<PropertyItem, PropertyItem>("Valor").apply {
            prefWidth = 250.0
            setCellValueFactory { cellData ->
                SimpleObjectProperty(cellData.value.value)
            }
            cellFactory = Callback { PropertyValueCell() }
        }

        treeTableView.columns.addAll(nameColumn, valueColumn)
        treeTableView.isShowRoot = false
        treeTableView.columnResizePolicy = TreeTableView.CONSTRAINED_RESIZE_POLICY

        children.add(treeTableView)
    }

    override fun layoutChildren() {
        treeTableView.resizeRelocate(0.0, 0.0, width, height)
    }

    fun setObject(obj: Any?) {
        targetObject = obj
        objectType = obj?.javaClass
        buildPropertyTree()
    }

    private fun buildPropertyTree() {
        val root = TreeItem<PropertyItem>(PropertyItem("root", null, null, null))
        val obj = targetObject ?: run {
            treeTableView.root = root
            return
        }

        val props = obj::class.memberProperties
            .sortedBy { it.name }

        for (prop in props) {
            @Suppress("UNCHECKED_CAST")
            val kProp = prop as KProperty1<Any, *>
            val value = try { kProp.get(obj) } catch (_: Exception) { null }
            val item = createPropertyItem(prop.name, value, kProp, obj)
            root.children.add(item)
        }

        treeTableView.root = root
    }

    private fun createPropertyItem(
        name: String,
        value: Any?,
        property: KProperty1<out Any, *>?,
        owner: Any?
    ): TreeItem<PropertyItem> {
        val childItems = mutableListOf<PropertyItem>()

        when (value) {
            is Point -> {
                childItems.add(PropertyItem("x", value.x, null, value))
                childItems.add(PropertyItem("y", value.y, null, value))
            }
            is Border -> {
                childItems.add(PropertyItem("leftWidth", value.leftWidth, null, value))
                childItems.add(PropertyItem("topWidth", value.topWidth, null, value))
                childItems.add(PropertyItem("rightWidth", value.rightWidth, null, value))
                childItems.add(PropertyItem("bottomWidth", value.bottomWidth, null, value))
                childItems.add(PropertyItem("color", value.color, null, value))
            }
            is Color -> {
                childItems.add(PropertyItem("r", value.r, null, value))
                childItems.add(PropertyItem("g", value.g, null, value))
                childItems.add(PropertyItem("b", value.b, null, value))
                childItems.add(PropertyItem("a", value.a, null, value))
            }
            is Section -> {
                for (p in value::class.memberProperties.sortedBy { it.name }) {
                    @Suppress("UNCHECKED_CAST")
                    val kp = p as KProperty1<Any, *>
                    val v = try { kp.get(value) } catch (_: Exception) { null }
                    childItems.add(PropertyItem(p.name, v, kp, value))
                }
            }
            is TextBlock -> {
                for (p in value::class.memberProperties.sortedBy { it.name }) {
                    @Suppress("UNCHECKED_CAST")
                    val kp = p as KProperty1<Any, *>
                    val v = try { kp.get(value) } catch (_: Exception) { null }
                    childItems.add(PropertyItem(p.name, v, kp, value))
                }
            }
            is Image -> {
                for (p in value::class.memberProperties.sortedBy { it.name }) {
                    @Suppress("UNCHECKED_CAST")
                    val kp = p as KProperty1<Any, *>
                    val v = try { kp.get(value) } catch (_: Exception) { null }
                    childItems.add(PropertyItem(p.name, v, kp, value))
                }
            }
            is Report -> {
                for (p in value::class.memberProperties.sortedBy { it.name }) {
                    @Suppress("UNCHECKED_CAST")
                    val kp = p as KProperty1<Any, *>
                    val v = try { kp.get(value) } catch (_: Exception) { null }
                    childItems.add(PropertyItem(p.name, v, kp, value))
                }
            }
        }

        val propItem = PropertyItem(name, value, property, owner, childItems)
        val treeItem = TreeItem(propItem)

        for (child in childItems) {
            val childTreeItem = createPropertyItem(child.name, child.value, child.property, child.ownerObject)
            treeItem.children.add(childTreeItem)
        }

        return treeItem
    }

    fun refresh() {
        buildPropertyTree()
    }

    // ─── Custom Cell Renderers / Editors ───────────────────────────────

    private inner class PropertyValueCell : TreeTableCell<PropertyItem, PropertyItem>() {

        override fun updateItem(item: PropertyItem?, empty: Boolean) {
            super.updateItem(item, empty)
            if (empty || item == null) {
                text = null
                graphic = null
                return
            }

            graphic = null
            text = null

            val value = item.value
            val propType = item.property?.returnType?.javaType

            when {
                value is Color -> {
                    graphic = createColorEditor(item)
                }
                value is Point -> {
                    graphic = createPointEditor(item)
                }
                value is Border -> {
                    text = item.displayValue
                }
                value is PaperSize -> {
                    text = item.displayValue
                }
                value is Section -> {
                    text = item.displayValue
                }
                value is TextBlock -> {
                    text = item.displayValue
                }
                value is Image -> {
                    text = item.displayValue
                }
                value is Report -> {
                    text = item.displayValue
                }
                value is Enum<*> -> {
                    graphic = createEnumEditor(item, value)
                }
                value is Int || propType == Int::class.java || propType == java.lang.Integer::class.java -> {
                    graphic = createIntEditor(item)
                }
                value is Float || propType == Float::class.java || propType == java.lang.Float::class.java -> {
                    graphic = createFloatEditor(item)
                }
                value is Boolean || propType == Boolean::class.java || propType == java.lang.Boolean::class.java -> {
                    graphic = createBooleanEditor(item)
                }
                value is String || propType == String::class.java -> {
                    graphic = createStringEditor(item)
                }
                else -> {
                    text = item.displayValue
                }
            }
        }

        private fun createStringEditor(item: PropertyItem): TextField {
            val tf = TextField(item.value?.toString() ?: "")
            tf.prefWidth = this.width - 20.0

            if (!item.isReadOnly) {
                tf.setOnAction {
                    applyValue(item, tf.text)
                }
                tf.focusedProperty().addListener { _, _, focused ->
                    if (!focused) applyValue(item, tf.text)
                }
            } else {
                tf.isEditable = false
            }
            return tf
        }

        private fun createIntEditor(item: PropertyItem): Spinner<Int> {
            val currentVal = (item.value as? Number)?.toInt() ?: 0
            val spinner = Spinner<Int>(Int.MIN_VALUE, Int.MAX_VALUE, currentVal)
            spinner.isEditable = true
            spinner.prefWidth = 200.0
            if (!item.isReadOnly) {
                spinner.valueProperty().addListener { _, _, newVal ->
                    applyValue(item, newVal)
                }
            } else {
                spinner.isDisable = true
            }
            return spinner
        }

        private fun createFloatEditor(item: PropertyItem): Spinner<Double> {
            val currentVal = (item.value as? Number)?.toDouble() ?: 0.0
            val spinner = Spinner<Double>(-999999.0, 999999.0, currentVal, 0.5)
            spinner.isEditable = true
            spinner.prefWidth = 200.0
            if (!item.isReadOnly) {
                spinner.valueProperty().addListener { _, _, newVal ->
                    applyValue(item, newVal?.toFloat())
                }
            } else {
                spinner.isDisable = true
            }
            return spinner
        }

        private fun createBooleanEditor(item: PropertyItem): CheckBox {
            val cb = CheckBox()
            cb.isSelected = item.value as? Boolean ?: false
            if (!item.isReadOnly) {
                cb.selectedProperty().addListener { _, _, newVal ->
                    applyValue(item, newVal)
                }
            } else {
                cb.isDisable = true
            }
            return cb
        }

        @Suppress("UNCHECKED_CAST")
        private fun createEnumEditor(item: PropertyItem, currentEnum: Enum<*>): ComboBox<String> {
            val enumClass = currentEnum::class.java
            val values = enumClass.enumConstants.map { it.name }
            val combo = ComboBox(FXCollections.observableArrayList(values))
            combo.value = currentEnum.name
            combo.prefWidth = 200.0
            if (!item.isReadOnly) {
                combo.setOnAction {
                    val selected = combo.value
                    val enumVal = enumClass.enumConstants.find { it.name == selected }
                    applyValue(item, enumVal)
                }
            } else {
                combo.isDisable = true
            }
            return combo
        }

        private fun createColorEditor(item: PropertyItem): HBox {
            val color = item.value as Color
            val fxColor = toFxColor(color)
            val colorPicker = ColorPicker(fxColor)
            colorPicker.prefWidth = 170.0
            val label = Label(item.displayValue)
            label.padding = Insets(0.0, 0.0, 0.0, 5.0)

            if (!item.isReadOnly) {
                colorPicker.setOnAction {
                    val c = colorPicker.value
                    val newColor = Color(
                        (c.red * 255).toFloat(),
                        (c.green * 255).toFloat(),
                        (c.blue * 255).toFloat(),
                        c.opacity.toFloat()
                    )
                    applyValue(item, newColor)
                    label.text = "Color(${newColor.r}, ${newColor.g}, ${newColor.b}, ${newColor.a})"
                }
            } else {
                colorPicker.isDisable = true
            }

            val hbox = HBox(5.0, colorPicker, label)
            hbox.alignment = Pos.CENTER_LEFT
            return hbox
        }

        private fun createPointEditor(item: PropertyItem): HBox {
            val point = item.value as Point
            val xField = TextField(point.x.toString()).apply { prefWidth = 80.0 }
            val yField = TextField(point.y.toString()).apply { prefWidth = 80.0 }

            if (!item.isReadOnly) {
                val applyPoint = {
                    val x = xField.text.toFloatOrNull() ?: point.x
                    val y = yField.text.toFloatOrNull() ?: point.y
                    val newPoint = Point(x, y)
                    applyValue(item, newPoint)
                }
                xField.setOnAction { applyPoint() }
                yField.setOnAction { applyPoint() }
                xField.focusedProperty().addListener { _, _, f -> if (!f) applyPoint() }
                yField.focusedProperty().addListener { _, _, f -> if (!f) applyPoint() }
            } else {
                xField.isEditable = false
                yField.isEditable = false
            }

            val hbox = HBox(3.0, Label("X:"), xField, Label("Y:"), yField)
            hbox.alignment = Pos.CENTER_LEFT
            return hbox
        }

        private fun toFxColor(c: Color): FxColor {
            val r = (c.r / 255f).coerceIn(0f, 1f).toDouble()
            val g = (c.g / 255f).coerceIn(0f, 1f).toDouble()
            val b = (c.b / 255f).coerceIn(0f, 1f).toDouble()
            val a = c.a.coerceIn(0f, 1f).toDouble()
            return FxColor(r, g, b, a)
        }

        @Suppress("UNCHECKED_CAST")
        private fun applyValue(item: PropertyItem, newValue: Any?) {
            val prop = item.property
            val owner = item.ownerObject
            if (prop is KMutableProperty1<*, *> && owner != null) {
                try {
                    (prop as KMutableProperty1<Any, Any?>).set(owner, newValue)
                    item.valueProperty.set(newValue)
                    refresh()
                } catch (e: Exception) {
                    println("Erro ao definir propriedade '${item.name}': ${e.message}")
                }
            }
        }
    }
}

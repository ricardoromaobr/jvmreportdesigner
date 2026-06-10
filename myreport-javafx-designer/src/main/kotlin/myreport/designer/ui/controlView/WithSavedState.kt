package myreport.designer.ui.controlView

import java.awt.Graphics2D

inline fun Graphics2D.withSavedState(block: Graphics2D.() -> Unit) {
    val savedTransform = transform
    val savedComposite = composite
    val savedColor = color
    val savedStroke = stroke
    val savedFont = this.font
    try {
        block()
    } finally {
        transform = savedTransform
        composite = savedComposite
        color = savedColor
        stroke = savedStroke
        font = savedFont
    }
}
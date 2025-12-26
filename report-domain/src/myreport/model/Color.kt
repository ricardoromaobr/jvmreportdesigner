package myreport.model

import kotlinx.serialization.Serializable

@Serializable
class Color (var r: Float, var g: Float, var b: Float, var a: Float) : ICloneable {
    constructor(r: Float, g: Float, b: Float): this(r, g, b, 1f)

    override fun clone(): Any {
        val c = Color(r,g,b,a)
        return c
    }
}
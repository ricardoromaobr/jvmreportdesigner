package myreport.model

class Border(var leftWidth: Float, var topWidth: Float, var rightWidth: Float, var bottomWidth: Float) : ICloneable {
    var  color = Color(0f,0f,0f,1f)
    constructor(all: Float) : this(all,all, all, all)

    override fun clone(): Any {
        val b = Border(leftWidth, topWidth, rightWidth, bottomWidth)
        return b
    }
}
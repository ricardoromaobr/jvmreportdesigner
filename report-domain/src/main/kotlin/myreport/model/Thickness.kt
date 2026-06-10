package myreport.model

import kotlinx.serialization.Serializable

@Serializable
class Thickness {

    private var _l: Float = 0f
    private var _t: Float = 0f
    private var _r: Float = 0f
    private var _b: Float = 0f

    constructor(all: Float) {
        _l = all
        _t = all
        _r = all
        _b = all
    }

    constructor(left: Float, top: Float, right: Float, bottom: Float) {
        _l = left
        _t = top
        _r = right
        _b = bottom
    }

    var left: Float
        get() = _l
        set(value) { _l = value }

    var top: Float
        get() = _t
        set(value) { _t = value }

    var right: Float
        get() = _r
        set(value) { _r = value }

    var bottom: Float
        get() = _b
        set(value) { _b = value}


    override fun toString(): String {
        return "[Padding: Left=$_l, Top=$_t, Right=$_r, Bottom=$_b]"
    }
}
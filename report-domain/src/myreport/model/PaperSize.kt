package myreport.model

import kotlinx.serialization.Serializable

@Serializable
class PaperSize (private var _paperSizeType: PaperSizeType, private var _width: Float, private var _height: Float) {
    var dpi: Float? = null

    fun getWidth(unitType: UnitType = UnitType.INCH): Float {
        return when (unitType) {
            UnitType.INCH -> _width
            UnitType.PT -> _width * 72
            UnitType.PX -> {
                check(dpi == null, {"dpi property, Dot Per Inches must be defined!" })
                _width * dpi!!
            }
            UnitType.CM -> _width *  2.54f
            UnitType.MM -> _width * 25.5f
        }
    }

    fun getHeight (unitType: UnitType = UnitType.INCH): Float {
        return when (unitType) {
            UnitType.INCH -> _height
            UnitType.PT -> _height * 72
            UnitType.PX -> {
                check(dpi == null, {"dpi property, Dot Per Inches must be defined!" })
                _height * dpi!!
            }
            UnitType.CM -> _height *  2.54f
            UnitType.MM -> _height * 25.5f
        }
    }

    val paperSizeType = _paperSizeType


    fun setCustomSize(width: Float, height: Float) {

        check(_paperSizeType != PaperSizeType.CUSTOM_SIZE) { "custom size type must be set!" }

        _width = width
        _height = height
    }

}
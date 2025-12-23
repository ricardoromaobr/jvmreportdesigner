package myreport.model

class PaperSize (sizeType: PaperSizeType, width: Float, height: Float) {

    private var _sizeType: PaperSizeType = sizeType
    private var _width: Float = width
    private var _height: Float = height

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

    val sizeType =  _sizeType

}
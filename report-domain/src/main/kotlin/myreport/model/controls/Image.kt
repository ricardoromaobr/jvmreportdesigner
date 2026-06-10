package myreport.model.controls

import kotlinx.serialization.Serializable
import myreport.model.Border
import myreport.model.Point

@Serializable
class Image: Control() {
    lateinit var border: Border
    var data: ByteArray? = null

    override fun createControl(): Control {
        val img = Image()
        copyBasicProperties(img)
        img.border = border.clone()  as Border
        img.offset = Point(offset.x, offset.y)
        img.data = data
        return img
    }

    lateinit var offset: Point

}
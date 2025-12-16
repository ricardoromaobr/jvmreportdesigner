package myreport.model.controls

import myreport.model.Border
import myreport.model.Point

class Image: Control() {

    lateinit var imageKey: String
    lateinit var border: Border
    lateinit var data: ByteArray

    override fun createControl(): Control {
        val img = Image()
        copyBasicProperties(img)
        img.imageKey = imageKey
        img.border = border.clone()  as Border
        img.offset = Point(offset.x, offset.y)
        img.data = data
        return img
    }

    lateinit var offset: Point

}
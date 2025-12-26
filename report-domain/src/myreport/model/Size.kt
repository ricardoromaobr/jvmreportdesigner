package myreport.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Size (var width: Float, var height: Float) {

    override fun toString(): String {
        return "[Size: Width=$width, Height=$height ]"
    }

}
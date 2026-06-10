package myreport.model

import myreport.model.controls.Control

class Page {
    val controls = mutableListOf<Control>()
    var pageNumber = 0
}
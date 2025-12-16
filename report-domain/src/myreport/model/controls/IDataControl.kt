package myreport.model.controls

import myreport.model.data.Field
import myreport.model.data.FieldKind

interface IDataControl {
    var fieldName: String

    var fieldKind: FieldKind

    var fieldTextFormat: String

    var text: String
}
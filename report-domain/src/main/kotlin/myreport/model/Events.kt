package myreport.model

import myreport.model.controls.Control

typealias BeforeControlProcessing = (reportContext: ReportContext, control: Control) -> Unit
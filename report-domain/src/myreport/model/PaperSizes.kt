package myreport.model

internal class PaperSizes  {

    companion object {

        // list of paper sizes with values in inches
        val paperSizes: List<PaperSize> = listOf(
            PaperSize(PaperSizeType.A0, 33.1f, 46.8f),
            PaperSize(PaperSizeType.A1, 23.4f, 33.1f),
            PaperSize(PaperSizeType.A2, 16.5f, 23.4f),
            PaperSize(PaperSizeType.A3, 11.7f, 16.5f),
            PaperSize(PaperSizeType.A4, 8.3f, 11.7f),
        )
    }
}
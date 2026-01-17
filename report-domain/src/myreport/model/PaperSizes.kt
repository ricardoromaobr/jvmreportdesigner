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

        val default = paperSizes.find { paperSize -> paperSize.paperSizeType == PaperSizeType.A4 }

        /**
         * Create a custom Paper Size
         * The values must be set in INCHES
         *
         * @param width The page width in inches
         * @param height The page height in inches
         */
        fun createACustomSize(width: Float, height: Float): PaperSize {
            return PaperSize(PaperSizeType.CUSTOM_SIZE, width, height)
        }


        fun createACustomSizeInPixels(width: Float, height: Float): PaperSize {
            val widthInInches = width / 96
            val heightInInches = height / 96
            return PaperSize(PaperSizeType.CUSTOM_SIZE, widthInInches, heightInInches)
        }

        fun createAcustomSizeInPoints(width: Float, height: Float): PaperSize {
            val widthInInches = width / 72
            val heightInInches = height / 72
            return PaperSize(PaperSizeType.CUSTOM_SIZE, widthInInches, heightInInches)
        }
    }
}
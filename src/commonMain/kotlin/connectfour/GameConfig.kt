package connectfour

data class GameConfig(
    val rows: Int,
    val cols: Int,
    val winLength: Int,
) {
    init {
        require(rows in MIN_DIMENSION..MAX_DIMENSION) {
            "Rows must be between $MIN_DIMENSION and $MAX_DIMENSION."
        }
        require(cols in MIN_DIMENSION..MAX_DIMENSION) {
            "Columns must be between $MIN_DIMENSION and $MAX_DIMENSION."
        }
        require(winLength >= MIN_WIN_LENGTH) {
            "Line length must be at least $MIN_WIN_LENGTH."
        }
        require(winLength <= maxOf(rows, cols)) {
            "Line length cannot be longer than the longer board side (${maxOf(rows, cols)})."
        }
    }

    companion object {
        const val MIN_DIMENSION = 1
        const val MAX_DIMENSION = 20
        const val MIN_WIN_LENGTH = 2

        fun parseOrNull(rowsText: String, colsText: String, winText: String): GameConfig? {
            val rows = rowsText.trim().toIntOrNull() ?: return null
            val cols = colsText.trim().toIntOrNull() ?: return null
            val win = winText.trim().toIntOrNull() ?: return null
            return runCatching { GameConfig(rows, cols, win) }.getOrNull()
        }
    }
}

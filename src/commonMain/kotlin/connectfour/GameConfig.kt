package connectfour

data class GameConfig(
    val rows: Int,
    val cols: Int,
    val winLength: Int,
) {
    init {
        require(rows in MIN_DIMENSION..MAX_DIMENSION) {
            "rows must be in $MIN_DIMENSION..$MAX_DIMENSION, got $rows"
        }
        require(cols in MIN_DIMENSION..MAX_DIMENSION) {
            "cols must be in $MIN_DIMENSION..$MAX_DIMENSION, got $cols"
        }
        require(winLength >= MIN_WIN_LENGTH) {
            "winLength must be >= $MIN_WIN_LENGTH, got $winLength"
        }
        require(winLength <= maxOf(rows, cols)) {
            "winLength must be <= max(rows, cols) so a line can fit"
        }
    }

    companion object {
        const val MIN_DIMENSION = 1
        const val MAX_DIMENSION = 20
        const val MIN_WIN_LENGTH = 2
    }
}

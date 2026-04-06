package connectfour

sealed class DropResult {
    data class Success(
        val newState: BoardState,
        val gameOver: GameOver?,
        val landingRow: Int,
    ) : DropResult()
    data object ColumnFull : DropResult()
    data object InvalidColumn : DropResult()
    data object GameAlreadyOver : DropResult()
}

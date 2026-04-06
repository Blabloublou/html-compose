package connectfour

sealed class DropResult {
    data class Success(val newState: BoardState, val gameOver: GameOver?) : DropResult()
    data object ColumnFull : DropResult()
    data object InvalidColumn : DropResult()
    data object GameAlreadyOver : DropResult()
}

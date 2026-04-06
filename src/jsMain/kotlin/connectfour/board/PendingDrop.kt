package connectfour.board

import connectfour.BoardState
import connectfour.GameOver
import connectfour.Player

internal data class PendingDrop(
    val column: Int,
    val landingRow: Int,
    val player: Player,
    val newState: BoardState,
    val gameOver: GameOver?,
)

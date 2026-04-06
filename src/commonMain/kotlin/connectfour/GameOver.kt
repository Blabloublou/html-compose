package connectfour

sealed class GameOver {
    data class Win(val winner: Player) : GameOver()
    data object Draw : GameOver()
}

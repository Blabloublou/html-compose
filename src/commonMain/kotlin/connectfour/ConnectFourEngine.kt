package connectfour

object ConnectFourEngine {

    fun drop(state: BoardState, player: Player, column: Int): DropResult {
        if (state.gameOver != null) return DropResult.GameAlreadyOver
        if (column !in 0 until state.config.cols) return DropResult.InvalidColumn

        val landingRow = findLandingRow(state, column) ?: return DropResult.ColumnFull

        val newCells = state.cells.toMutableList()
        newCells[landingRow * state.config.cols + column] = player

        val gameOver = when {
            hasWin(state.config, newCells, landingRow, column, player) -> GameOver.Win(player)
            isBoardFull(newCells) -> GameOver.Draw
            else -> null
        }

        val newState = BoardState(state.config, newCells, gameOver)
        return DropResult.Success(newState, gameOver)
    }

    private fun findLandingRow(state: BoardState, column: Int): Int? {
        for (row in state.config.rows - 1 downTo 0) {
            if (state.cell(row, column) == null) return row
        }
        return null
    }

    private fun isBoardFull(cells: List<Player?>): Boolean = cells.all { it != null }

    private fun hasWin(
        config: GameConfig,
        cells: List<Player?>,
        row: Int,
        col: Int,
        player: Player,
    ): Boolean {
        val directions = listOf(
            0 to 1,
            1 to 0,
            1 to 1,
            1 to -1,
        )
        for ((dr, dc) in directions) {
            var count = 1
            var r = row + dr
            var c = col + dc
            while (r in 0 until config.rows && c in 0 until config.cols &&
                cells[r * config.cols + c] == player
            ) {
                count++
                r += dr
                c += dc
            }
            r = row - dr
            c = col - dc
            while (r in 0 until config.rows && c in 0 until config.cols &&
                cells[r * config.cols + c] == player
            ) {
                count++
                r -= dr
                c -= dc
            }
            if (count >= config.winLength) return true
        }
        return false
    }
}

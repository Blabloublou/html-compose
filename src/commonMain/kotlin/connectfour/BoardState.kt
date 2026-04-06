package connectfour

data class BoardState(
    val config: GameConfig,
    val cells: List<Player?>,
    val gameOver: GameOver? = null,
) {
    init {
        require(cells.size == config.rows * config.cols) {
            "cells size ${cells.size} != ${config.rows * config.cols}"
        }
    }

    fun cell(row: Int, col: Int): Player? {
        require(row in 0 until config.rows && col in 0 until config.cols) {
            "row=$row col=$col out of bounds for ${config.rows}x${config.cols}"
        }
        return cells[row * config.cols + col]
    }
}

fun GameConfig.emptyBoard(): BoardState =
    BoardState(config = this, cells = List(rows * cols) { null })

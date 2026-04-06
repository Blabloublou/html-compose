package connectfour.app

import connectfour.BoardState
import connectfour.GameConfig
import connectfour.GameOver
import connectfour.Player
import kotlinx.browser.localStorage

private const val STORAGE_VERSION = 1
private const val STORAGE_KEY = "connectfour-state"

data class ConnectFourLoadedSnapshot(
    val config: GameConfig,
    val boardState: BoardState,
    val currentPlayer: Player,
)

private fun jsonStringify(value: Any?): String =
    js("JSON.stringify")(value).unsafeCast<String>()

private fun jsonParse(text: String): dynamic = js("JSON.parse")(text)

private fun cellsToTokenString(cells: List<Player?>): String =
    cells.joinToString("") { c ->
        when (c) {
            null -> "."
            Player.One -> "1"
            Player.Two -> "2"
        }
    }

private fun tokenStringToCells(token: String, expectedLen: Int): List<Player?>? {
    if (token.length != expectedLen) return null
    val out = ArrayList<Player?>(expectedLen)
    for (ch in token) {
        out.add(
            when (ch) {
                '.' -> null
                '1' -> Player.One
                '2' -> Player.Two
                else -> return null
            },
        )
    }
    return out
}

fun saveConnectFourSnapshot(
    config: GameConfig,
    boardState: BoardState,
    currentPlayer: Player,
) {
    val payload = js("{}")
    payload.v = STORAGE_VERSION
    payload.rows = config.rows
    payload.cols = config.cols
    payload.winLength = config.winLength
    payload.cells = cellsToTokenString(boardState.cells)
    payload.currentPlayer = currentPlayer.name
    when (val go = boardState.gameOver) {
        null -> {
            payload.gameOver = "none"
        }
        is GameOver.Win -> {
            payload.gameOver = "win"
            payload.winner = go.winner.name
        }
        is GameOver.Draw -> {
            payload.gameOver = "draw"
        }
    }
    localStorage.setItem(STORAGE_KEY, jsonStringify(payload))
}

fun loadConnectFourSnapshot(): ConnectFourLoadedSnapshot? {
    val raw = localStorage.getItem(STORAGE_KEY) ?: return null
    val d = try {
        jsonParse(raw)
    } catch (_: Throwable) {
        return null
    }
    val v = (d.v as? Number)?.toInt() ?: return null
    if (v != STORAGE_VERSION) return null

    val rows = (d.rows as? Number)?.toInt() ?: return null
    val cols = (d.cols as? Number)?.toInt() ?: return null
    val winLength = (d.winLength as? Number)?.toInt() ?: return null
    val config = runCatching { GameConfig(rows = rows, cols = cols, winLength = winLength) }.getOrNull()
        ?: return null

    val cellsStr = d.cells as? String ?: return null
    val cells = tokenStringToCells(cellsStr, config.rows * config.cols) ?: return null

    val currentPlayerName = d.currentPlayer as? String ?: return null
    val currentPlayer = runCatching { Player.valueOf(currentPlayerName) }.getOrNull() ?: return null

    val gameOverRaw = d.gameOver as? String ?: return null
    val gameOver = when (gameOverRaw) {
        "none" -> null
        "draw" -> GameOver.Draw
        "win" -> {
            val winnerName = d.winner as? String ?: return null
            val winner = runCatching { Player.valueOf(winnerName) }.getOrNull() ?: return null
            GameOver.Win(winner)
        }
        else -> return null
    }

    val boardState = runCatching { BoardState(config = config, cells = cells, gameOver = gameOver) }.getOrNull()
        ?: return null

    return ConnectFourLoadedSnapshot(
        config = config,
        boardState = boardState,
        currentPlayer = currentPlayer,
    )
}

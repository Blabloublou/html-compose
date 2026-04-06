package connectfour.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import connectfour.BoardState
import connectfour.ConnectFourEngine
import connectfour.DropResult
import connectfour.GameConfig
import connectfour.Player
import connectfour.emptyBoard
import connectfour.board.ConnectFourBoard
import connectfour.chrome.ConnectFourFeedback
import connectfour.chrome.ConnectFourNewGameButton
import connectfour.cursor.ConnectFourCursorPreview
import connectfour.modals.ConnectFourConfigModal
import connectfour.modals.ConnectFourGameOverModal
import kotlinx.browser.localStorage
import org.jetbrains.compose.web.dom.Div

private const val STORAGE_KEY = "connectfour-state"

@Composable
fun ConnectFourApp() {
    var config by remember { mutableStateOf(GameConfig(rows = 6, cols = 7, winLength = 4)) }
    var boardState by remember { mutableStateOf(config.emptyBoard()) }
    var currentPlayer by remember { mutableStateOf(Player.One) }
    var feedback by remember { mutableStateOf<String?>(null) }

    var showConfigModal by remember { mutableStateOf(true) }
    var hasConfirmedConfigOnce by remember { mutableStateOf(false) }
    var modalRows by remember { mutableStateOf(config.rows) }
    var modalCols by remember { mutableStateOf(config.cols) }
    var modalWin by remember { mutableStateOf(config.winLength) }
    var modalFeedback by remember { mutableStateOf<String?>(null) }

    var cursorX by remember { mutableStateOf(0.0) }
    var cursorY by remember { mutableStateOf(0.0) }
    var cursorOverBoard by remember { mutableStateOf(false) }

    fun clearStorage() {
        localStorage.removeItem(STORAGE_KEY)
    }

    fun openConfigModal() {
        modalRows = config.rows
        modalCols = config.cols
        modalWin = config.winLength
        modalFeedback = null
        showConfigModal = true
    }

    fun confirmConfig() {
        runCatching {
            val next = GameConfig(rows = modalRows, cols = modalCols, winLength = modalWin)
            config = next
            boardState = next.emptyBoard()
            currentPlayer = Player.One
            feedback = null
            clearStorage()
            modalFeedback = null
            showConfigModal = false
            hasConfirmedConfigOnce = true
        }.onFailure { e ->
            modalFeedback = e.message
        }
    }

    fun replaySameConfig() {
        boardState = config.emptyBoard()
        currentPlayer = Player.One
        feedback = null
        clearStorage()
    }

    fun onColumnClick(column: Int) {
        if (showConfigModal) return
        if (boardState.gameOver != null) return
        when (val result = ConnectFourEngine.drop(boardState, currentPlayer, column)) {
            is DropResult.Success -> {
                boardState = result.newState
                feedback = null
                if (result.gameOver == null) {
                    currentPlayer = currentPlayer.other()
                }
            }
            DropResult.ColumnFull -> feedback = "This column is full."
            DropResult.InvalidColumn -> { }
            DropResult.GameAlreadyOver -> { }
        }
    }

    val showCursorPreview =
        !showConfigModal && boardState.gameOver == null && cursorOverBoard

    Div(attrs = {
        classes("connect-four-app")
    }) {
        ConnectFourNewGameButton(onNewGame = { openConfigModal() })

        feedback?.let { msg ->
            ConnectFourFeedback(msg)
        }

        ConnectFourBoard(
            boardState = boardState,
            config = config,
            showCursorPreview = showCursorPreview,
            onCursorEnter = { cursorOverBoard = true },
            onCursorLeave = { cursorOverBoard = false },
            onCursorMove = { x, y ->
                cursorX = x
                cursorY = y
            },
            onColumnClick = { onColumnClick(it) },
        )

        if (showCursorPreview) {
            ConnectFourCursorPreview(
                x = cursorX,
                y = cursorY,
                player = currentPlayer,
            )
        }

        if (showConfigModal) {
            ConnectFourConfigModal(
                modalRows = modalRows,
                modalCols = modalCols,
                modalWin = modalWin,
                modalFeedback = modalFeedback,
                showCancel = hasConfirmedConfigOnce,
                onRowsChange = { modalRows = it },
                onColsChange = { modalCols = it },
                onWinChange = { modalWin = it },
                onConfirm = { confirmConfig() },
                onCancel = {
                    showConfigModal = false
                    modalFeedback = null
                },
            )
        } else {
            boardState.gameOver?.let { go ->
                ConnectFourGameOverModal(
                    gameOver = go,
                    onReplay = { replaySameConfig() },
                    onNewGameSettings = { openConfigModal() },
                )
            }
        }
    }
}

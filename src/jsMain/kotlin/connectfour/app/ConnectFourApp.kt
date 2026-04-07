package connectfour.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import connectfour.BoardState
import connectfour.ConnectFourEngine
import connectfour.DropResult
import connectfour.GameConfig
import connectfour.GameOver
import connectfour.Player
import connectfour.emptyBoard
import connectfour.board.ConnectFourBoard
import connectfour.board.PendingDrop
import connectfour.chrome.ConnectFourFeedback
import connectfour.chrome.ConnectFourFixedGameActions
import connectfour.cursor.ConnectFourCursorPreview
import connectfour.cursor.rememberConnectFourCursorPreviewActive
import connectfour.modals.ConnectFourConfigModal
import connectfour.modals.ConnectFourGameOverModal
import org.jetbrains.compose.web.dom.Div

@Composable
fun ConnectFourApp() {
    val restored = remember { loadConnectFourSnapshot() }
    val initialConfig = restored?.config ?: GameConfig(rows = 6, cols = 7, winLength = 4)
    var config by remember { mutableStateOf(initialConfig) }
    var boardState by remember {
        mutableStateOf(restored?.boardState ?: initialConfig.emptyBoard())
    }
    var currentPlayer by remember {
        mutableStateOf(restored?.currentPlayer ?: Player.One)
    }
    var winsPlayerOne by remember { mutableStateOf(restored?.winsPlayerOne ?: 0) }
    var winsPlayerTwo by remember { mutableStateOf(restored?.winsPlayerTwo ?: 0) }
    var feedback by remember { mutableStateOf<String?>(null) }

    var showConfigModal by remember { mutableStateOf(restored == null) }
    var hasConfirmedConfigOnce by remember { mutableStateOf(restored != null) }
    var modalRows by remember { mutableStateOf(config.rows) }
    var modalCols by remember { mutableStateOf(config.cols) }
    var modalWin by remember { mutableStateOf(config.winLength) }
    var modalFeedback by remember { mutableStateOf<String?>(null) }

    var cursorX by remember { mutableStateOf(0.0) }
    var cursorY by remember { mutableStateOf(0.0) }
    var cursorOverBoard by remember { mutableStateOf(false) }

    var pendingDrop by remember { mutableStateOf<PendingDrop?>(null) }
    var moveHistory by remember { mutableStateOf(emptyList<Pair<BoardState, Player>>()) }

    LaunchedEffect(
        config,
        boardState,
        currentPlayer,
        pendingDrop,
        showConfigModal,
        hasConfirmedConfigOnce,
        winsPlayerOne,
        winsPlayerTwo,
    ) {
        if (pendingDrop != null) return@LaunchedEffect
        if (showConfigModal) return@LaunchedEffect
        if (!hasConfirmedConfigOnce) return@LaunchedEffect
        saveConnectFourSnapshot(
            config,
            boardState,
            currentPlayer,
            winsPlayerOne,
            winsPlayerTwo,
        )
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
            modalFeedback = null
            showConfigModal = false
            hasConfirmedConfigOnce = true
            pendingDrop = null
            moveHistory = emptyList()
            winsPlayerOne = 0
            winsPlayerTwo = 0
        }.onFailure { e ->
            modalFeedback = (e as? IllegalArgumentException)?.message ?: "Invalid settings."
        }
    }

    fun replaySameConfig() {
        boardState = config.emptyBoard()
        currentPlayer = Player.One
        feedback = null
        pendingDrop = null
        moveHistory = emptyList()
    }

    fun undoLastMove() {
        if (pendingDrop != null || showConfigModal || moveHistory.isEmpty()) return
        when (val go = boardState.gameOver) {
            is GameOver.Win -> {
                when (go.winner) {
                    Player.One -> winsPlayerOne = maxOf(0, winsPlayerOne - 1)
                    Player.Two -> winsPlayerTwo = maxOf(0, winsPlayerTwo - 1)
                }
            }
            else -> Unit
        }
        val (prevBoard, prevPlayer) = moveHistory.last()
        moveHistory = moveHistory.dropLast(1)
        boardState = prevBoard
        currentPlayer = prevPlayer
        feedback = null
    }

    fun applyPendingDrop() {
        val p = pendingDrop ?: return
        boardState = p.newState
        pendingDrop = null
        when (val end = p.newState.gameOver) {
            is GameOver.Win -> {
                when (end.winner) {
                    Player.One -> winsPlayerOne++
                    Player.Two -> winsPlayerTwo++
                }
            }
            null -> currentPlayer = currentPlayer.other()
            is GameOver.Draw -> Unit
        }
    }

    fun onColumnClick(column: Int) {
        if (showConfigModal) return
        if (pendingDrop != null) return
        if (boardState.gameOver != null) return
        when (val result = ConnectFourEngine.drop(boardState, currentPlayer, column)) {
            is DropResult.Success -> {
                moveHistory = moveHistory + (boardState to currentPlayer)
                pendingDrop = PendingDrop(
                    column = column,
                    landingRow = result.landingRow,
                    player = currentPlayer,
                    newState = result.newState,
                )
                feedback = null
            }
            DropResult.ColumnFull -> feedback = "This column is full."
            DropResult.InvalidColumn -> { }
            DropResult.GameAlreadyOver -> { }
        }
    }

    val showCursorPreview = rememberConnectFourCursorPreviewActive(
        !showConfigModal && boardState.gameOver == null && cursorOverBoard,
    )

    val canUndoLastMove =
        !showConfigModal &&
            pendingDrop == null &&
            moveHistory.isNotEmpty()

    Div(attrs = {
        classes("connect-four-app")
    }) {
        ConnectFourFixedGameActions(
            onNewGame = { openConfigModal() },
            onUndoLastMove = { undoLastMove() },
            undoLastMoveEnabled = canUndoLastMove,
        )

        feedback?.let { msg ->
            ConnectFourFeedback(msg) { feedback = null }
        }

        ConnectFourBoard(
            boardState = boardState,
            pendingDrop = pendingDrop,
            showCursorPreview = showCursorPreview,
            onCursorEnter = { cursorOverBoard = true },
            onCursorLeave = { cursorOverBoard = false },
            onCursorMove = { x, y ->
                cursorX = x
                cursorY = y
            },
            onColumnClick = { onColumnClick(it) },
            onDropAnimationEnd = { applyPendingDrop() },
        )

        ConnectFourCursorPreview(
            x = cursorX,
            y = cursorY,
            player = currentPlayer,
            visible = showCursorPreview,
        )

        if (showConfigModal) {
            ConnectFourConfigModal(
                modalRows = modalRows,
                modalCols = modalCols,
                modalWin = modalWin,
                modalFeedback = modalFeedback,
                showCancel = hasConfirmedConfigOnce,
                onRowsChange = { newRows ->
                    modalRows = newRows
                    val hi = maxOf(newRows, modalCols)
                    val lo = GameConfig.MIN_WIN_LENGTH
                    if (lo <= hi) {
                        modalWin = modalWin.coerceIn(lo..hi)
                    }
                },
                onColsChange = { newCols ->
                    modalCols = newCols
                    val hi = maxOf(modalRows, newCols)
                    val lo = GameConfig.MIN_WIN_LENGTH
                    if (lo <= hi) {
                        modalWin = modalWin.coerceIn(lo..hi)
                    }
                },
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
                    winsPlayerOne = winsPlayerOne,
                    winsPlayerTwo = winsPlayerTwo,
                    onReplay = { replaySameConfig() },
                    onNewGameSettings = { openConfigModal() },
                )
            }
        }
    }
}

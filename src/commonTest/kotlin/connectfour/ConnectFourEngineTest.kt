package connectfour

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ConnectFourEngineTest {

    private val classic = GameConfig(rows = 6, cols = 7, winLength = 4)

    private fun playAll(
        config: GameConfig,
        moves: List<Pair<Player, Int>>,
    ): BoardState {
        var state = config.emptyBoard()
        for ((player, col) in moves) {
            val r = ConnectFourEngine.drop(state, player, col)
            state = (r as DropResult.Success).newState
        }
        return state
    }

    @Test
    fun gravity_empty_column_lands_on_bottom_row() {
        val state = classic.emptyBoard()
        val result = ConnectFourEngine.drop(state, Player.One, column = 0)
        val success = assertIs<DropResult.Success>(result)
        assertEquals(5, success.landingRow, "bottom row index for 6 rows is 5")
        assertEquals(Player.One, success.newState.cell(5, 0))
    }

    @Test
    fun gravity_multiple_drops_stack_in_column() {
        var state = classic.emptyBoard()
        state = (ConnectFourEngine.drop(state, Player.One, 2) as DropResult.Success).newState
        state = (ConnectFourEngine.drop(state, Player.Two, 2) as DropResult.Success).newState
        state = (ConnectFourEngine.drop(state, Player.One, 2) as DropResult.Success).newState

        assertEquals(Player.One, state.cell(5, 2))
        assertEquals(Player.Two, state.cell(4, 2))
        assertEquals(Player.One, state.cell(3, 2))
    }

    @Test
    fun horizontal_win_win_length_four() {
        val state = playAll(
            classic,
            listOf(
                Player.One to 0,
                Player.Two to 6,
                Player.One to 1,
                Player.Two to 6,
                Player.One to 2,
                Player.Two to 6,
                Player.One to 3,
            ),
        )
        assertEquals(GameOver.Win(Player.One), state.gameOver)
    }

    @Test
    fun vertical_win_win_length_four() {
        val state = playAll(
            classic,
            listOf(
                Player.One to 0,
                Player.Two to 1,
                Player.One to 0,
                Player.Two to 1,
                Player.One to 0,
                Player.Two to 1,
                Player.One to 0,
            ),
        )
        assertEquals(GameOver.Win(Player.One), state.gameOver)
    }

    @Test
    fun diagonal_up_right_win_win_length_four() {
        val state = playAll(
            classic,
            listOf(
                Player.One to 0,
                Player.Two to 1,
                Player.One to 1,
                Player.Two to 2,
                Player.Two to 2,
                Player.One to 2,
                Player.Two to 3,
                Player.Two to 3,
                Player.Two to 3,
                Player.One to 3,
            ),
        )
        assertEquals(GameOver.Win(Player.One), state.gameOver)
    }

    @Test
    fun horizontal_win_win_length_five_on_single_row_board() {
        val config = GameConfig(rows = 1, cols = 9, winLength = 5)
        val state = playAll(
            config,
            listOf(
                Player.One to 0,
                Player.Two to 5,
                Player.One to 1,
                Player.Two to 6,
                Player.One to 2,
                Player.Two to 7,
                Player.One to 3,
                Player.Two to 8,
                Player.One to 4,
            ),
        )
        assertEquals(GameOver.Win(Player.One), state.gameOver)
    }

    @Test
    fun contiguous_line_of_win_length_minus_one_is_not_a_win() {
        val config = GameConfig(rows = 1, cols = 6, winLength = 4)
        val state = playAll(
            config,
            listOf(
                Player.One to 0,
                Player.Two to 4,
                Player.One to 1,
                Player.Two to 3,
            ),
        )
        assertNull(state.gameOver)
        val last = ConnectFourEngine.drop(state, Player.One, 2)
        val success = assertIs<DropResult.Success>(last)
        assertNull(success.gameOver)
        assertTrue(success.newState.cell(0, 0) == Player.One)
        assertTrue(success.newState.cell(0, 1) == Player.One)
        assertTrue(success.newState.cell(0, 2) == Player.One)
    }

    @Test
    fun draw_when_board_full_without_aligning_win_length() {
        val config = GameConfig(rows = 1, cols = 4, winLength = 2)
        val state = playAll(
            config,
            listOf(
                Player.One to 0,
                Player.Two to 1,
                Player.One to 2,
                Player.Two to 3,
            ),
        )
        assertEquals(GameOver.Draw, state.gameOver)
    }

    @Test
    fun column_full_returns_column_full() {
        var state = classic.emptyBoard()
        for (row in 0 until classic.rows) {
            val p = if (row % 2 == 0) Player.One else Player.Two
            val r = ConnectFourEngine.drop(state, p, 0)
            state = (r as DropResult.Success).newState
        }
        assertEquals(DropResult.ColumnFull, ConnectFourEngine.drop(state, Player.One, 0))
    }

    @Test
    fun invalid_column_out_of_range() {
        val state = classic.emptyBoard()
        assertEquals(DropResult.InvalidColumn, ConnectFourEngine.drop(state, Player.One, -1))
        assertEquals(DropResult.InvalidColumn, ConnectFourEngine.drop(state, Player.One, classic.cols))
    }

    @Test
    fun drop_after_game_over_returns_game_already_over() {
        val won = playAll(
            classic,
            listOf(
                Player.One to 0,
                Player.Two to 6,
                Player.One to 1,
                Player.Two to 6,
                Player.One to 2,
                Player.Two to 6,
                Player.One to 3,
            ),
        )
        assertTrue(won.gameOver is GameOver.Win)
        assertEquals(DropResult.GameAlreadyOver, ConnectFourEngine.drop(won, Player.Two, 4))
    }
}

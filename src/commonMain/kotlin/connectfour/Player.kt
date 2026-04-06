package connectfour

enum class Player {
    One,
    Two;

    fun other(): Player = when (this) {
        One -> Two
        Two -> One
    }
}

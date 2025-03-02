package com.example.tetris

object TETROMINOES {

    val tetrominoType = listOf("I", "O", "T", "L", "J", "S", "Z")

    fun getTetro(tetroType: String): Array<IntArray> {

        return when (tetroType) {
            "I" -> arrayOf(
                intArrayOf(1),
                intArrayOf(1),
                intArrayOf(1),
                intArrayOf(1),
            )
            "O" -> arrayOf(
                intArrayOf(1, 1),
                intArrayOf(1, 1)
            )
            "T" -> arrayOf(
                intArrayOf(0, 1, 0),
                intArrayOf(1, 1, 1),
            )
            "L" -> arrayOf(
                intArrayOf(1, 1, 0),
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 0),
            )
            "J" -> arrayOf(
                intArrayOf(1, 1),
                intArrayOf(1, 0),
                intArrayOf(1, 0),
            )
            "S" -> arrayOf(
                intArrayOf(0, 1, 0),
                intArrayOf(0, 1, 1),
                intArrayOf(0, 0, 1),
            )
            "Z" -> arrayOf(
                intArrayOf(0, 1),
                intArrayOf(1, 1),
                intArrayOf(1, 0),
            )
            else -> arrayOf(
                intArrayOf(1),
                intArrayOf(1),
                intArrayOf(1),
                intArrayOf(1),
            )
        }
    }
}

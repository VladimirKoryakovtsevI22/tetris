package com.example.tetris

data class Tetromino(
    var shape: Array<IntArray>? = null,
    var isOccupied: Boolean = false,
    var blocks: Array<Cell> = emptyArray()
)
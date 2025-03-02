package com.example.tetris

class Tetromino(
    var shape: Array<IntArray>? = null,
    var x: Int = 0,
    var y: Int = 0,
    var isOccupied: Boolean = false
) {


//    fun rotate(): Tetromino {
//        val rotatedShape = Array(shape[0].size) { IntArray(shape.size) }
//        for (y in shape.indices) {
//            for (x in shape[y].indices) {
//                rotatedShape[x][shape.size - y - 1] = shape[y][x]
//            }
//        }
//        return Tetromino(rotatedShape)
//    }

//    // Получение текущего положения тетромино
//    fun getBounds(): Pair<Int, Int> {
//        return Pair(x, y)
//    }
}


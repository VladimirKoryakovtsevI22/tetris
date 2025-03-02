package com.example.tetris

data class Cell(
    var x: Int = -1,          // Координата по оси X
    var y: Int = -1,          // Координата по оси Y
    var isOccupied: Boolean = false // Состояние (занята или нет)
)

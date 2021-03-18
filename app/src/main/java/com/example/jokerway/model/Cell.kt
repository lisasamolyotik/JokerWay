package com.example.jokerway.model

class Cell(
    var visited: Boolean = false,
    var isCurrent: Boolean = false,
    val coordinates: IntArray,
    val id: Int
) {}
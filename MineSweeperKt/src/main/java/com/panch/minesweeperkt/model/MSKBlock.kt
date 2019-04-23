package com.panch.minesweeperkt.model

data class MSKBlock(
    val x: Int,
    val y: Int,
    var dangerLevel: Int,
    var flagged: Boolean,
    var cleared: Boolean
)
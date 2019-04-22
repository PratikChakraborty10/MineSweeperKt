package com.panch.minesweeperkt.listener

interface MSKViewListener {
    fun onFoundAllMines()
    fun onMineExploded()
    fun onGameStarted()
    fun onGameEnded()
}
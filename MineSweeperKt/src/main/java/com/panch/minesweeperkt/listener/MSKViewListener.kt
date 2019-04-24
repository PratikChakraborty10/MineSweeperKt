package com.panch.minesweeperkt.listener

import com.panch.minesweeperkt.view.MineView

interface MSKViewListener {
    fun onFoundAllMines()
    fun onMineExploded()
    fun onFlagMine(mineView: MineView)
    fun onClearMine(mineView: MineView)
}
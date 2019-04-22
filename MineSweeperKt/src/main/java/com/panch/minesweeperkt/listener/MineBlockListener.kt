package com.panch.minesweeperkt.listener

import com.panch.minesweeperkt.view.MineView


interface MineBlockListener {
    fun onMineBlockClear(mskBlock: MineView)
    fun onMineBlockFlag(mskBlock: MineView)
    fun onMineBlockExplode(mskBlock: MineView)
}
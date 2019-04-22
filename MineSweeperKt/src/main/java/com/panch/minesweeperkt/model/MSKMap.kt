package com.panch.minesweeperkt.model

import com.panch.minesweeperkt.view.MineView

data class MSKMap(
    val width: Int,
    val height: Int,
    val MSKBlocks: ArrayList<ArrayList<MineView>>
) {
    fun placeMines(count: Int, firstClearedMine: MineView) {
        var placedMines = 0
        while (placedMines != count) {
            val randX = (0 until width).random()
            val randY = (0 until height).random()
            if (randX != firstClearedMine.x && randY != firstClearedMine.y) {
                if (MSKBlocks[randY][randX].dangerLevel != -1) {
                    MSKBlocks[randY][randX].dangerLevel = -1
                    placedMines++
                }
            }
        }
    }

    fun setDangerLevels() {
        MSKBlocks.forEach { mineList ->
            mineList.forEach {
                if (it.dangerLevel != -1)
                    it.dangerLevel = it.surroundingBlocks().filter { b -> b.dangerLevel == -1 }.count()
            }
        }
    }

    fun foundAllMines(): Boolean {
        val allBlocks = ArrayList<MineView>()
        MSKBlocks.forEach { it.forEach { allBlocks.add(it) } }
        val flaggedBlocks = allBlocks.filter { it.flagged }
        val unclearedBlocks = allBlocks.filter { !it.cleared }
        if (flaggedBlocks.isNotEmpty() && (unclearedBlocks.none { !it.flagged })) {
            return flaggedBlocks.none { it.dangerLevel != -1 }
        }
        return false
    }

    fun clearBlock(x: Int, y: Int) {
        val mineBlock = MSKBlocks[y][x]
        if (!mineBlock.flagged) {
            if (!mineBlock.cleared) {
                mineBlock.cleared = true
                if (mineBlock.dangerLevel == 0)
                    mineBlock.surroundingBlocks().filter { !it.cleared }.forEach { clearBlock(it.x, it.y) }
            } else {
                val surroundingBlocks = mineBlock.surroundingBlocks()
                if (surroundingBlocks.filter { it.flagged }.size == mineBlock.dangerLevel && mineBlock.dangerLevel != 0) {
                    surroundingBlocks.filter { !it.cleared }.forEach {
                        clearBlock(it.x, it.y)
                    }
                }
            }
        }
    }

    fun setBlockFlagged(x: Int, y: Int, flagged: Boolean = true) {
        val mineBlock = MSKBlocks[y][x]
        if (!mineBlock.cleared)
            mineBlock.flagged = flagged
    }

    private fun MineView.surroundingBlocks(): ArrayList<MineView> {
        val tempList = ArrayList<MineView>()
        val NW = MSKBlocks.getOrNull(this.y - 1)?.getOrNull(this.x - 1)
        val N = MSKBlocks.getOrNull(this.y - 1)?.getOrNull(this.x)
        val NE = MSKBlocks.getOrNull(this.y - 1)?.getOrNull(this.x + 1)
        val W = MSKBlocks.getOrNull(this.y)?.getOrNull(this.x - 1)
        val E = MSKBlocks.getOrNull(this.y)?.getOrNull(this.x + 1)
        val SW = MSKBlocks.getOrNull(this.y + 1)?.getOrNull(this.x - 1)
        val SE = MSKBlocks.getOrNull(this.y + 1)?.getOrNull(this.x + 1)
        val S = MSKBlocks.getOrNull(this.y + 1)?.getOrNull(this.x)
        if (NW != null) {
            tempList.add(NW)
        }
        if (N != null) {
            tempList.add(N)
        }
        if (NE != null) {
            tempList.add(NE)
        }
        if (E != null) {
            tempList.add(E)
        }
        if (W != null) {
            tempList.add(W)
        }
        if (SE != null) {
            tempList.add(SE)
        }
        if (SW != null) {
            tempList.add(SW)
        }
        if (S != null) {
            tempList.add(S)
        }
        return tempList
    }
}
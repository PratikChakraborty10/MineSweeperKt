package com.panch.minesweeperkt.model

data class MSKNonViewMap(
    val width: Int,
    val height: Int,
    val MSKBlocks: ArrayList<ArrayList<MSKBlock>>
)
package com.panch.minesweeperkt.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.panch.minesweeperkt.R
import com.panch.minesweeperkt.listener.MSKViewListener
import com.panch.minesweeperkt.listener.MineBlockListener
import com.panch.minesweeperkt.model.MSKMap
import kotlinx.android.synthetic.main.layout_mskview.view.*


class MSKView : FrameLayout, MineBlockListener {
    private var view: View = inflate(context, R.layout.layout_mskview, this)
    private var generatedRealMap = false
    private var map: MSKMap? = null
    private var playable: Boolean = true
    var mineCount = 24
    var listener: MSKViewListener? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onMineBlockFlag(mskBlock: MineView) {
        if (!this.playable)
            return

        if (map != null) {
            map!!.setBlockFlagged(mskBlock.x, mskBlock.y, !mskBlock.flagged)
            if (map!!.foundAllMines()) {
                this.playable = false
                listener?.onFoundAllMines()
                listener?.onGameEnded()
            }
        }
    }

    override fun onMineBlockClear(mskBlock: MineView) {
        if (!this.playable)
            return

        if (map != null) {
            if (!generatedRealMap) {
                map!!.placeMines(mineCount, mskBlock)
                map!!.setDangerLevels()
                generatedRealMap = true
                listener?.onGameStarted()
            }
            map!!.clearBlock(mskBlock.x, mskBlock.y)
            if (map!!.foundAllMines()) {
                this.playable = false
                listener?.onFoundAllMines()
                listener?.onGameEnded()
            }
        }
    }

    override fun onMineBlockExplode(mskBlock: MineView) {
        this.playable = false
        listener?.onMineExploded()
        listener?.onGameEnded()
    }

    fun init(width: Int, height: Int) {
        val _map = MSKMap(width, height, ArrayList())
        for (y in 0 until height) {
            val horizontalBlocks = ArrayList<MineView>()
            for (x in 0 until width) {
                val mineView = MineView(this, context)
                mineView.x = x
                mineView.y = y
                horizontalBlocks.add(mineView)
            }
            _map.MSKBlocks.add(horizontalBlocks)
        }
        map = _map
        view.post {
            val totalWidth = view.layoutMap.width
            val totalHeight = view.layoutMap.height
            Log.d(MSKView::class.java.simpleName, "Total Width: $totalWidth | Total Height: $totalHeight")
            view.layoutMap.removeAllViews()
            val blockWidth = totalWidth / map!!.width
            val blockHeight = totalHeight / map!!.height
            map!!.MSKBlocks.forEach { mskLine ->
                val mineLine = LinearLayout(context)
                mineLine.orientation = LinearLayout.HORIZONTAL
                mskLine.forEach { mskBlock ->
                    if (mskBlock.parent != null) {
                        (mskBlock.parent as ViewGroup).removeView(mskBlock)
                    }
                    mineLine.addView(mskBlock)
                    mskBlock.layoutParams.width = blockWidth
                    mskBlock.layoutParams.height = blockHeight
                    (mskBlock.layoutParams as LinearLayout.LayoutParams).weight = 1f
                }
                view.layoutMap.addView(mineLine)
                mineLine.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
                mineLine.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                (mineLine.layoutParams as LinearLayout.LayoutParams).weight = 1f
            }
        }
        generatedRealMap = false
        playable = true
    }
}
package com.panch.minesweeperkt.view

import android.content.Context
import android.media.MediaPlayer
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.panch.minesweeperkt.R
import com.panch.minesweeperkt.listener.MSKViewListener
import com.panch.minesweeperkt.listener.MineBlockListener
import com.panch.minesweeperkt.model.MSKBlock
import com.panch.minesweeperkt.model.MSKMap
import com.panch.minesweeperkt.model.MSKNonViewMap
import kotlinx.android.synthetic.main.layout_mskview.view.*
import android.os.VibrationEffect
import android.os.Build
import android.os.Handler
import android.os.Vibrator


class MSKView : FrameLayout, MineBlockListener {
    //region non-editable or changed automatically
    private var view: View = inflate(context, R.layout.layout_mskview, this)
    private var generatedRealMap = false
    private var map: MSKMap? = null
    private var playable: Boolean = true
    private val tickHandler: Handler = Handler()
    private lateinit var tickRunnable: Runnable
    private var totalSeconds = 0
    private var timerStarted = false
    //endregion

    var resourceUnclearedMine = R.drawable.uncleared_mine
    var resourceClearedMine = R.drawable.cleared_mine
    var resourceFlag = R.drawable.ic_flag
    var resourceMineBomb = R.drawable.mine_bomb
    var resourceMineExplosionSound = R.raw.boom
    var resourceFlaggingSound = R.raw.beep
    var playSoundOnMineExplosion = true
    var playSoundOnFlagging = true
    var vibrateOnFlag = true
    var vibrateOnExplosion = true
    var vibrateDurationOnFlag = 500
    var vibrateDurationOnExplosion = 500
    var forceDrawingSquareBlocks = false
    var mineCount = 24
    var listener: MSKViewListener? = null
    var locked = false
        set(value) {
            if (value) {
                if (map != null) {
                    map!!.MSKBlocks.forEach {
                        it.forEach {
                            it.locked = true
                        }
                    }
                }
            } else {
                if (map != null) {
                    map!!.MSKBlocks.forEach {
                        it.forEach {
                            it.locked = false
                        }
                    }
                }
            }
            field = value
        }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        tickRunnable = Runnable {
            listener?.onGameTimerTick(totalSeconds)
            totalSeconds += 1
            if (timerStarted) {
                tickHandler.postDelayed(tickRunnable, 1000)
            }
        }
    }

    private fun vibrate(durationInMilliseconds: Long = 500) {
        val v = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v!!.vibrate(VibrationEffect.createOneShot(durationInMilliseconds, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            v!!.vibrate(durationInMilliseconds)
        }
    }

    private fun playSound(resource: Int) {
        val mediaPlayer = MediaPlayer.create(context, resource)
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.setOnCompletionListener {
                it.stop()
            }
            mediaPlayer.start()
        }
    }

    override fun onMineBlockFlag(mskBlock: MineView) {
        if (!this.playable || mskBlock.cleared || !generatedRealMap)
            return

        if (map != null) {
            listener?.onFlagMine(mskBlock)
            if (!mskBlock.flagged) {
                if (playSoundOnFlagging) {
                    playSound(resourceFlaggingSound)
                }
                if (vibrateOnFlag) {
                    vibrate(vibrateDurationOnFlag.toLong())
                }
            }
            map!!.setBlockFlagged(mskBlock.x, mskBlock.y, !mskBlock.flagged)
            listener?.onLeftMineCountChangedBasedOnFlags(map!!.leftMineCountBasedOnFlags())
            if (map!!.foundAllMines()) {
                this.playable = false
                listener?.onFoundAllMines()
            }
        }
    }

    override fun onMineBlockClear(mskBlock: MineView) {
        if (!this.playable)
            return

        if (map != null) {
            listener?.onClearMine(mskBlock)
            if (!timerStarted) {
                totalSeconds = 0
                tickHandler.postDelayed(tickRunnable, 1000)
                timerStarted = true
            }
            if (!generatedRealMap) {
                map!!.placeMines(mineCount, mskBlock)
                map!!.setDangerLevels()
                generatedRealMap = true
            }
            map!!.clearBlock(mskBlock.x, mskBlock.y)
            if (map!!.foundAllMines()) {
                this.playable = false
                listener?.onFoundAllMines()
                timerStarted = false
                tickHandler.removeCallbacks(tickRunnable)
            }
        }
    }

    override fun onMineBlockExplode(mskBlock: MineView) {
        this.playable = false
        timerStarted = false
        tickHandler.removeCallbacks(tickRunnable)
        if (playSoundOnMineExplosion) {
            playSound(resourceMineExplosionSound)
        }
        if (vibrateOnExplosion) {
            vibrate(vibrateDurationOnExplosion.toLong())
        }
        listener?.onMineExploded()
    }

    //Basic initialization.
    fun init(width: Int, height: Int, placeMinesAtStart: Boolean = false) {
        val _map = MSKMap(width, height, ArrayList())
        for (y in 0 until height) {
            val horizontalBlocks = ArrayList<MineView>()
            for (x in 0 until width) {
                val mineView = MineView(this, context)
                horizontalBlocks.add(mineView)
                mineView.resourceUnclearedMine = resourceUnclearedMine
                mineView.resourceClearedMine = resourceClearedMine
                mineView.resourceFlag = resourceFlag
                mineView.resourceMineBomb = resourceMineBomb
                mineView.x = x
                mineView.y = y
            }
            _map.MSKBlocks.add(horizontalBlocks)
        }
        map = _map
        view.post {
            val totalWidth = view.layoutSizeScale.width
            val totalHeight = view.layoutSizeScale.height
            //Log.d(MSKView::class.java.simpleName, "Total Width: $totalWidth | Total Height: $totalHeight")
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

                    if (forceDrawingSquareBlocks)
                        mskBlock.layoutParams.height = blockWidth
                    else
                        mskBlock.layoutParams.height = blockHeight

                    (mskBlock.layoutParams as LinearLayout.LayoutParams).weight = 1f
                }
                view.layoutMap.addView(mineLine)
                mineLine.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
                mineLine.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                (mineLine.layoutParams as LinearLayout.LayoutParams).weight = 1f
            }

            if (forceDrawingSquareBlocks)
                (view.layoutMap.layoutParams as FrameLayout.LayoutParams).height =
                    FrameLayout.LayoutParams.WRAP_CONTENT
            else
                (view.layoutMap.layoutParams as FrameLayout.LayoutParams).height =
                    FrameLayout.LayoutParams.MATCH_PARENT
        }
        generatedRealMap = false
        if (placeMinesAtStart) {
            map!!.placeMines(mineCount)
            map!!.setDangerLevels()
            generatedRealMap = true
        }
        playable = true
    }

    //Restarts game with given width, height and mine count.
    fun restart() {
        if (map != null) {
            init(map!!.width, map!!.height)
        }
    }

    //Can't import if real map was not generated.
    fun exportNonViewMap(): MSKNonViewMap? {
        if (map != null && generatedRealMap) {
            val tempList = ArrayList<ArrayList<MSKBlock>>()
            map!!.MSKBlocks.forEach {
                val horizontalMskBLocks = ArrayList<MSKBlock>()
                it.forEach { mineView ->
                    horizontalMskBLocks.add(
                        MSKBlock(
                            mineView.x,
                            mineView.y,
                            mineView.dangerLevel,
                            mineView.flagged,
                            mineView.cleared
                        )
                    )
                }
                tempList.add(horizontalMskBLocks)
            }
            return MSKNonViewMap(map!!.width, map!!.height, tempList)
        }
        return null
    }

    //Do not import before real map was actually generated.
    fun importNonViewMap(mskNonViewMap: MSKNonViewMap) {
        val _map = MSKMap(mskNonViewMap.width, mskNonViewMap.height, ArrayList())
        mskNonViewMap.MSKBlocks.forEach { mskBlockHorizontalLine ->
            val horizontalViewBlocks = ArrayList<MineView>()
            mskBlockHorizontalLine.forEach { singleMskBlock ->
                val mineView = MineView(this, context)
                horizontalViewBlocks.add(mineView)
                mineView.resourceUnclearedMine = resourceUnclearedMine
                mineView.resourceClearedMine = resourceClearedMine
                mineView.resourceFlag = resourceFlag
                mineView.resourceMineBomb = resourceMineBomb
                mineView.flagged = singleMskBlock.flagged
                mineView.dangerLevel = singleMskBlock.dangerLevel
                mineView.cleared = singleMskBlock.cleared
                mineView.x = singleMskBlock.x
                mineView.y = singleMskBlock.y
            }
            _map.MSKBlocks.add(horizontalViewBlocks)
        }
        map = _map
        view.post {
            val totalWidth = view.layoutSizeScale.width
            val totalHeight = view.layoutSizeScale.height
            //Log.d(MSKView::class.java.simpleName, "Total Width: $totalWidth | Total Height: $totalHeight")
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

                    if (forceDrawingSquareBlocks)
                        mskBlock.layoutParams.height = blockWidth
                    else
                        mskBlock.layoutParams.height = blockHeight

                    (mskBlock.layoutParams as LinearLayout.LayoutParams).weight = 1f
                }
                view.layoutMap.addView(mineLine)
                mineLine.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
                mineLine.layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
                (mineLine.layoutParams as LinearLayout.LayoutParams).weight = 1f
            }

            if (forceDrawingSquareBlocks)
                (view.layoutMap.layoutParams as FrameLayout.LayoutParams).height =
                    FrameLayout.LayoutParams.WRAP_CONTENT
            else
                (view.layoutMap.layoutParams as FrameLayout.LayoutParams).height =
                    FrameLayout.LayoutParams.MATCH_PARENT
        }
        generatedRealMap = true
        playable = true
    }

    //Clears the block at given coords. Use this if you need to. Otherwise, not necessary.
    fun clearBlockAt(x: Int, y: Int) {
        if (!this.playable)
            return

        if (map != null) {
            if (!timerStarted) {
                totalSeconds = 0
                tickHandler.postDelayed(tickRunnable, 1000)
                timerStarted = true
            }
            map!!.clearBlock(x, y)
            if (map!!.foundAllMines()) {
                this.playable = false
                listener?.onFoundAllMines()
            }
        }
    }

    //Flags the block at given coords. Use this if you need to. Otherwise, not necessary.
    fun flagBlockAt(x: Int, y: Int, flag: Boolean = true) {
        if (!this.playable || !generatedRealMap)
            return

        if (map != null) {
            if (flag) {
                if (playSoundOnFlagging) {
                    playSound(resourceFlaggingSound)
                }
                if (vibrateOnFlag) {
                    vibrate(vibrateDurationOnFlag.toLong())
                }
            }
            map!!.setBlockFlagged(x, y, flag)
            listener?.onLeftMineCountChangedBasedOnFlags(map!!.leftMineCountBasedOnFlags())
            if (map!!.foundAllMines()) {
                this.playable = false
                listener?.onFoundAllMines()
            }
        }
    }
}
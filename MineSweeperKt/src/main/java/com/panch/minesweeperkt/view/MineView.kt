package com.panch.minesweeperkt.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.panch.minesweeperkt.R
import com.panch.minesweeperkt.listener.MineBlockListener
import kotlinx.android.synthetic.main.layout_block.view.*

class MineView : FrameLayout {
    private var view: View = inflate(context, R.layout.layout_block, this)
    private lateinit var listener: MineBlockListener
    var resourceUnclearedMine = R.drawable.uncleared_mine
    var resourceClearedMine = R.drawable.cleared_mine
    var resourceFlag = R.drawable.ic_flag
    var resourceMineBomb = R.drawable.mine_bomb

    var x = 0
    var y = 0
    var dangerLevel: Int = 0 // 0->none, -1 -> mined
    var flagged: Boolean = false
        set(value) {
            field = value
            view.imageViewFlag.setImageResource(resourceFlag)
            if (field) {
                view.imageViewFlag.visibility = View.VISIBLE
            } else {
                view.imageViewFlag.visibility = View.GONE
            }
        }
    var cleared: Boolean = false
        set(value) {
            field = value
            if (field) {
                view.imageViewBlock.setImageResource(resourceClearedMine)
                view.imageViewFlag.visibility = View.GONE

                view.textViewBlock.text = dangerLevel.toString()
                when (dangerLevel) {
                    0 -> {
                        view.textViewBlock.visibility = View.GONE
                    }
                    -1 -> {
                        view.textViewBlock.visibility = View.GONE
                        view.imageViewFlag.setImageResource(resourceMineBomb)
                        view.imageViewFlag.visibility = View.VISIBLE
                        listener.onMineBlockExplode(this)
                    }
                    else -> {
                        when {
                            dangerLevel <= 3 -> view.textViewBlock.setTextColor(context.resources.getColor(R.color.colorDangerLevelLow))
                            dangerLevel <= 5 -> view.textViewBlock.setTextColor(context.resources.getColor(R.color.colorDangerLevelMedium))
                            dangerLevel <= 8 -> view.textViewBlock.setTextColor(context.resources.getColor(R.color.colorDangerLevelHigh))
                        }
                        view.textViewBlock.visibility = View.VISIBLE
                    }
                }

            } else {
                view.imageViewBlock.setImageResource(resourceUnclearedMine)
            }
        }

    constructor(_listener: MineBlockListener? = null, context: Context) : super(context) {
        if (_listener != null)
            listener = _listener
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        view.imageViewBlock.scaleType = ImageView.ScaleType.FIT_XY
        view.imageViewBlock.setImageResource(resourceUnclearedMine)
        view.setOnClickListener {
            listener.onMineBlockClear(this)
        }
        view.setOnLongClickListener {
            listener.onMineBlockFlag(this)

            true
        }
        flagged = false
        cleared = false
        //dangerLevel = 0
    }
}
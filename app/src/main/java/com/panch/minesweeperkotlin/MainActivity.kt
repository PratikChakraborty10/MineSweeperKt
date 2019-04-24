package com.panch.minesweeperkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.panch.minesweeperkt.listener.MSKViewListener
import com.panch.minesweeperkt.view.MineView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mskView.mineCount = 20
        mskView.resourceMineBomb = R.drawable.mine_bomb
        mskView.forceDrawingSquareBlocks = false
        mskView.listener = object : MSKViewListener {
            override fun onFoundAllMines() {
                Toast.makeText(this@MainActivity, "Victory", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({ mskView.restart() }, 1000)
            }

            override fun onFlagMine(mineView: MineView) {
                Toast.makeText(this@MainActivity,mineView.flagged.toString(),Toast.LENGTH_LONG).show()
            }

            override fun onMineExploded() {
                Toast.makeText(this@MainActivity, "Boom!", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({ mskView.restart() }, 1000)
            }

            override fun onClearMine(mineView: MineView) {

            }
        }
        mskView.init(6, 8, true)

    }
}

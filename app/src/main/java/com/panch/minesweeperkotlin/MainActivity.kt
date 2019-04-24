package com.panch.minesweeperkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import com.panch.minesweeperkt.listener.MSKViewListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mskView.mineCount = 55
        mskView.resourceMineBomb = R.drawable.mine_bomb
        mskView.forceDrawingSquareBlocks = false
        mskView.listener = object : MSKViewListener {
            override fun onFoundAllMines() {
                Toast.makeText(this@MainActivity, "Victory", Toast.LENGTH_SHORT).show()
            }

            override fun onGameStarted() {

            }

            override fun onMineExploded() {
                Toast.makeText(this@MainActivity, "Boom!", Toast.LENGTH_SHORT).show()
            }

            override fun onGameEnded() {
                Handler().postDelayed({ mskView.restart() }, 1000)
            }
        }
        mskView.init(6, 10, true)


        testButton.setOnClickListener {
            alternativeMskView.listener = object : MSKViewListener {
                override fun onFoundAllMines() {
                    Toast.makeText(this@MainActivity, "Victory", Toast.LENGTH_SHORT).show()
                }

                override fun onGameStarted() {

                }

                override fun onMineExploded() {
                    Toast.makeText(this@MainActivity, "Boom!", Toast.LENGTH_SHORT).show()
                }

                override fun onGameEnded() {
                    Handler().postDelayed({ alternativeMskView.restart() }, 1000)
                }
            }
            val importableMap = mskView.exportNonViewMap()
            if (importableMap != null)
                alternativeMskView.importNonViewMap(importableMap)
            mskView.visibility = View.GONE
            alternativeMskView.visibility = View.VISIBLE
        }

    }
}

# MineSweeperKt

An embedded mine sweeper game library for Android.


### Installing

Step 1. Add the JitPack repository to your project level build.gradle file. (MyAwesomeProject/build.gradle)
```
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Step 2. Add the dependency to app level build.gradle file. (MyAwesomeProject/app/build.gradle)


```
dependencies {
          ...
	        implementation 'com.github.panchy:MineSweeperKt:1.6.8'
	}
```

That's it!

### How to use

Step 1. Include MSKView in your Activity's or Fragment's layout.

```
...
  <com.panch.minesweeperkt.view.MSKView
      android:id="@+id/mskView"
      android:layout_width="match_parent"
      android:layout_height="match_parent"/>
...
```

Step 2. Bind the MSKViewListener. It's function names are pretty self explanatory.

```
 mskView.listener = object : MSKViewListener {
            override fun onFoundAllMines() {
	    	//This will be triggered when player finds all the mines on the field.
	    }

            override fun onFlagMine(mineView: MineView) {
	    	//This will be triggered on flagging a block. It will also pass the cleared block's view.
	    }

            override fun onMineExploded() {
	    	//This will be triggered on mine explosion.
	    }

            override fun onClearMine(mineView: MineView) {
	    	//This will be triggered on clearing a block. It will also pass the cleared block's view.
	    }

            override fun onGameTimerTick(totalSeconds: Int) {
	    	//This will be triggered each second passes during game time. You can get the total seconds from totalSeconds parameter.
	    }

            override fun onLeftMineCountChangedBasedOnFlags(leftMinesBasedOnFlagsCount: Int) {
		//This will be triggered whenever a block is flagged. It will give you the amount of mines left based on the flags on the      	map. However it's only assumption, it doesn't mean that flag locations are correct.
	    }
}
```
Step 3. Set customizations from a set of rules.
```
mskView.resourceUnclearedMine 
mskView.resourceClearedMine
mskView.resourceFlag
mskView.resourceMineBomb
mskView.resourceMineExplosionSound
mskView.resourceFlaggingSound
mskView.playSoundOnMineExplosion
mskView.playSoundOnFlagging
mskView.vibrateOnFlag
mskView.vibrateOnExplosion
mskView.vibrateDurationOnFlag
mskView.vibrateDurationOnExplosion
mskView.forceDrawingSquareBlocks
mskView.mineCount
mskView.locked
```

Step 3. Initialize the view. Width and height are how many blocks should be created horizontally and vertically. Third parameter determines whether user might find a mine on the first cleared block or not. If set to false, there is no way to find a mine on first block.
```
mskView.init(width = 10, height = 15, placeMinesAtStart = false)
```
## License

MineSweeperKt is licensed under the
MIT License.
A short and simple permissive license with conditions only requiring preservation of copyright and license notices. Licensed works, modifications, and larger works may be distributed under different terms and without source code.



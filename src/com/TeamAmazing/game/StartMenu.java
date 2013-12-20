package com.TeamAmazing.game;

// TODO add code for onResume, onStop, onPause etc...

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.TeamAmazing.Maze.GameOfLife;
import com.TeamAmazing.drawing.StartMenuBackground;

public class StartMenu extends Activity {
	private Handler frame = new Handler();
	// The delay in milliseconds between frame updates
	private static final int FRAME_DELAY = 50;
	private static final int NUM_MAX_GENERATIONS = 200;
	private static final GameOfLife GAME = new GameOfLife();
	private int numCurrentGenerations = 0;
	private static final int RESTART_DELAY = 9000;
	public final static int PERFECT_MAZE = 0;
	public final static int DFS_MAZE = 1;
	public final static String MAZE_TYPE = "com.TeamAmazing.game.StartMenu.MAZE_TYPE";

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_menu);
		final StartMenuBackground smb = (StartMenuBackground) findViewById(R.id.start_menu_background);
		// Check if the View has been measured.
		// TODO use a more sophisticated method for checking if the view has
		// been measured.
		if (smb.getWidth() == 0 || smb.getHeight() == 0) {
			ViewTreeObserver vto = smb.getViewTreeObserver();
			vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				@Override
				@SuppressLint("NewApi")
				@SuppressWarnings("deprecation")
				public void onGlobalLayout() {
					// Initialize stuff that is dependent on the view already
					// having been measured.
					initializeGameOfLife();

					// Remove this ViewTreeObserver
					ViewTreeObserver obs = smb.getViewTreeObserver();
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
						obs.removeOnGlobalLayoutListener(this);
					} else {
						obs.removeGlobalOnLayoutListener(this);
					}
				}

			});
		} else {
			// initialize
			initializeGameOfLife();
		}

		frame.removeCallbacksAndMessages(frameUpdate);
		smb.invalidate();
		frame.postDelayed(frameUpdate, FRAME_DELAY);
	}

	private void initializeGameOfLife(){
		final StartMenuBackground smb = (StartMenuBackground) findViewById(R.id.start_menu_background);
		int width = smb.getWidth() / StartMenuBackground.CELL_WIDTH;
		int height = smb.getHeight() / StartMenuBackground.CELL_HEIGHT;
		GAME.initializeCells(width, height);
		smb.setBoard(GAME.getCurrentBoard());		
	}


	private Runnable frameUpdate = new Runnable() {
		@Override
		synchronized public void run() {
			final StartMenuBackground smb = ((StartMenuBackground) findViewById(R.id.start_menu_background));
			frame.removeCallbacksAndMessages(frameUpdate);

			if (numCurrentGenerations > NUM_MAX_GENERATIONS) {
				numCurrentGenerations = 0;
				initializeGameOfLife();
				frame.postDelayed(frameUpdate, FRAME_DELAY + RESTART_DELAY);
			} else {
				// Compute the next generation
				GAME.nextGeneration();
				numCurrentGenerations++;

				// Redraw the canvas
				smb.setBoard(GAME.getCurrentBoard());		
				smb.invalidate();

				// Loop, after FRAME_DELAY milliseconds.
				frame.postDelayed(frameUpdate, FRAME_DELAY);
			}
		}
	};

	// Kruskal's algorithm
	public void startKruskalsMaze(View v) {
		Intent intent = new Intent(this, MazeGame.class);
		intent.putExtra(MAZE_TYPE, PERFECT_MAZE);
		startActivity(intent);
	}

	// Recursive backtracker algorithm
	public void startRecursiveBacktrackerMaze(View v) {
		Intent intent = new Intent(this, MazeGame.class);
		intent.putExtra(MAZE_TYPE, DFS_MAZE);
		startActivity(intent);
	}

}
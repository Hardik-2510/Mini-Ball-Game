package com.hackyboy.miniballgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        int levelNumber = getIntent().getIntExtra("LEVEL_NUMBER", 1);
        gameView = new GameView(this, levelNumber);
        setContentView(gameView);
    }

    /**
     * FIX: Intercept the hardware/system back button.
     *
     * Instead of finishing the Activity immediately (which would confuse the
     * user or do nothing because SurfaceView consumes key events), we delegate
     * to GameView.handleBackPress(), which:
     *   - PLAYING  → pauses the game and shows a pause overlay
     *   - PAUSED   → resumes the game (back acts as a toggle)
     *   - LEVEL_COMPLETE / GAME_OVER → navigates to LevelSelectActivity
     */
    @Override
    public void onBackPressed() {
        if (gameView != null) {
            gameView.handleBackPress();
        }
        // Do NOT call super.onBackPressed() — we handle navigation ourselves
    }

    /**
     * Also handle the physical back key via dispatchKeyEvent as a safety net,
     * because SurfaceView sometimes consumes KeyEvents before the Activity sees them.
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_UP) {
            if (gameView != null) {
                gameView.handleBackPress();
                return true; // consumed
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Auto-pause when the app loses focus (e.g. notification pull-down)
        if (gameView != null) {
            gameView.pauseGame();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Don't auto-resume — let the player tap Resume intentionally
    }
}

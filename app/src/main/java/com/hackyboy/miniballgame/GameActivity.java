// app/src/main/java/com/hackyboy/miniballgame/GameActivity.java
package com.hackyboy.miniballgame;

import android.content.Intent;
import android.os.Bundle;
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

        // Get level number from Intent (default = 1)
        int levelNumber = getIntent().getIntExtra("LEVEL_NUMBER", 1);

        gameView = new GameView(this, levelNumber);
        setContentView(gameView);
    }

    @Override protected void onPause()  { super.onPause(); }
    @Override protected void onResume() { super.onResume(); }
}
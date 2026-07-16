// app/src/main/java/com/hackyboy/miniballgame/LevelResultActivity.java
package com.hackyboy.miniballgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class LevelResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_level_result);

        int levelNumber  = getIntent().getIntExtra("LEVEL_NUMBER", 1);
        int starsEarned  = getIntent().getIntExtra("STARS_EARNED", 0);
        int score        = getIntent().getIntExtra("SCORE", 0);
        int target       = getIntent().getIntExtra("TARGET", 500);

        // Bind views
        TextView tvTitle      = findViewById(R.id.tvResultTitle);
        TextView tvScore      = findViewById(R.id.tvResultScore);
        LinearLayout starRow  = findViewById(R.id.resultStarRow);
        Button btnNext        = findViewById(R.id.btnNextLevel);
        Button btnRetry       = findViewById(R.id.btnRetry);
        Button btnMenu        = findViewById(R.id.btnMenu);

        tvTitle.setText("Level " + levelNumber + " Complete!");
        tvScore.setText("Score: " + score + " / " + target);

        // Draw stars
        for (int s = 1; s <= 3; s++) {
            ImageView star = new ImageView(this);
            star.setImageResource(s <= starsEarned ? R.drawable.star_filled : R.drawable.star_empty);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(96, 96);
            lp.setMargins(8, 0, 8, 0);
            star.setLayoutParams(lp);
            starRow.addView(star);
        }

        LevelProgressManager pm = new LevelProgressManager(this);
        boolean nextUnlocked = pm.isUnlocked(levelNumber + 1);

        btnNext.setEnabled(nextUnlocked);
        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("LEVEL_NUMBER", levelNumber + 1);
            startActivity(intent);
            finish();
        });

        btnRetry.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.putExtra("LEVEL_NUMBER", levelNumber);
            startActivity(intent);
            finish();
        });

        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, LevelSelectActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
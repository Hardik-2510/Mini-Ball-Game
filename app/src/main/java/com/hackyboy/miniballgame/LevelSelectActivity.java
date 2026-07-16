// app/src/main/java/com/hackyboy/miniballgame/LevelSelectActivity.java
package com.hackyboy.miniballgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LevelSelectActivity extends AppCompatActivity {

    private LevelProgressManager progressManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_level_select);

        progressManager = new LevelProgressManager(this);
        buildLevelGrid();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh grid when returning from a completed level
        GridLayout grid = findViewById(R.id.gridLevels);
        grid.removeAllViews();
        buildLevelGrid();
    }

    private void buildLevelGrid() {
        GridLayout grid = findViewById(R.id.gridLevels);
        int total = progressManager.getTotalLevels();

        for (int i = 1; i <= total; i++) {
            final int levelNum = i;
            boolean unlocked = progressManager.isUnlocked(levelNum);
            int stars = progressManager.getStars(levelNum);

            // Inflate a level card view programmatically
            View card = getLayoutInflater().inflate(R.layout.item_level_card, grid, false);

            TextView tvLevelNum = card.findViewById(R.id.tvLevelNum);
            LinearLayout starRow = card.findViewById(R.id.starRow);
            ImageView lockIcon = card.findViewById(R.id.lockIcon);

            tvLevelNum.setText(String.valueOf(levelNum));

            if (unlocked) {
                card.setBackgroundResource(R.drawable.level_card_unlocked);
                lockIcon.setVisibility(View.GONE);
                starRow.setVisibility(View.VISIBLE);
                drawStars(starRow, stars);

                card.setOnClickListener(v -> {
                    Intent intent = new Intent(LevelSelectActivity.this, GameActivity.class);
                    intent.putExtra("LEVEL_NUMBER", levelNum);
                    startActivity(intent);
                });
            } else {
                card.setBackgroundResource(R.drawable.level_card_locked);
                lockIcon.setVisibility(View.VISIBLE);
                starRow.setVisibility(View.GONE);
                card.setOnClickListener(v ->
                        Toast.makeText(this, "Complete level " + (levelNum - 1) + " first!", Toast.LENGTH_SHORT).show()
                );
            }

            grid.addView(card);
        }
    }

    /** Fills 3 ImageView stars in the card based on earned count. */
    private void drawStars(LinearLayout row, int earned) {
        row.removeAllViews();
        for (int s = 1; s <= 3; s++) {
            ImageView star = new ImageView(this);
            star.setImageResource(s <= earned ? R.drawable.star_filled : R.drawable.star_empty);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(48, 48);
            lp.setMargins(4, 0, 4, 0);
            star.setLayoutParams(lp);
            row.addView(star);
        }
    }
}
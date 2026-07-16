// app/src/main/java/com/hackyboy/miniballgame/LevelProgressManager.java
package com.hackyboy.miniballgame;

import android.content.Context;
import android.content.SharedPreferences;

public class LevelProgressManager {

    private static final String PREFS_NAME   = "MiniballProgress";
    private static final String KEY_STARS    = "stars_level_";
    private static final String KEY_UNLOCKED = "unlocked_level_";
    private static final int    TOTAL_LEVELS = 10; // change as needed

    private final SharedPreferences prefs;

    public LevelProgressManager(Context ctx) {
        prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        // Level 1 is always unlocked from the start
        if (!prefs.contains(KEY_UNLOCKED + 1)) {
            prefs.edit().putBoolean(KEY_UNLOCKED + 1, true).apply();
        }
    }

    public int getTotalLevels() { return TOTAL_LEVELS; }

    /** Returns true if the player has unlocked this level. */
    public boolean isUnlocked(int level) {
        return prefs.getBoolean(KEY_UNLOCKED + level, false);
    }

    /** Returns stars earned (0-3). 0 = not yet completed. */
    public int getStars(int level) {
        return prefs.getInt(KEY_STARS + level, 0);
    }

    /**
     * Called when a level is passed.
     * Saves stars and unlocks the next level.
     * Only updates if new stars > old stars.
     */
    public void saveLevelResult(int level, int starsEarned) {
        SharedPreferences.Editor editor = prefs.edit();

        // Save best star count
        int existing = getStars(level);
        if (starsEarned > existing) {
            editor.putInt(KEY_STARS + level, starsEarned);
        }

        // Unlock next level
        int nextLevel = level + 1;
        if (nextLevel <= TOTAL_LEVELS) {
            editor.putBoolean(KEY_UNLOCKED + nextLevel, true);
        }

        editor.apply();
    }

    /** Resets ALL progress (useful for a "Reset" button in settings). */
    public void resetAll() {
        prefs.edit().clear().apply();
        prefs.edit().putBoolean(KEY_UNLOCKED + 1, true).apply();
    }
}
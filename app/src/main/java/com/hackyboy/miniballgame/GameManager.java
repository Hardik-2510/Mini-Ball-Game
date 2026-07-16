// app/src/main/java/com/hackyboy/miniballgame/GameManager.java
package com.hackyboy.miniballgame;

public class GameManager {
    public int   currentLevel;
    public int   currentScore;
    public int   targetScore;
    public int   lives;
    public float ballSpeed;
    public double difficultyMultiplier;

    // Track lives lost THIS level (not total lives remaining)
    private int livesLostThisLevel = 0;

    private static final int   BASE_TARGET = 500;
    private static final int   BASE_LIVES  = 3;
    private static final float BASE_SPEED  = 25f;

    public GameManager() { reset(); }

    public void reset() {
        currentLevel         = 1;
        currentScore         = 0;
        targetScore          = BASE_TARGET;
        lives                = BASE_LIVES;
        livesLostThisLevel   = 0;
        ballSpeed            = BASE_SPEED;
        difficultyMultiplier = 1.15;
    }

    /**
     * Call this when starting a specific level from LevelSelect.
     * Resets per-level state but keeps the level number.
     */
    public void setupLevel(int levelNumber) {
        currentLevel       = levelNumber;
        currentScore       = 0;
        lives              = BASE_LIVES;
        livesLostThisLevel = 0;

        // Recompute target & speed for this level
        targetScore = BASE_TARGET;
        difficultyMultiplier = 1.15;
        for (int i = 1; i < levelNumber; i++) {
            targetScore = (int)(targetScore * difficultyMultiplier);
            if (i % 5 == 0)
                difficultyMultiplier = Math.min(difficultyMultiplier + 0.05, 2.0);
        }
        ballSpeed = Math.min(BASE_SPEED + (levelNumber - 1) * 0.7f, 35f);
    }

    public boolean addScore(int pts) {
        currentScore += pts;
        return currentScore >= targetScore;
    }

    public boolean isLevelComplete() { return currentScore >= targetScore; }
    public boolean isGameOver()      { return lives <= 0; }

    /** Returns true when game over. */
    public boolean loseLife() {
        lives--;
        livesLostThisLevel++;
        return lives <= 0;
    }

    /**
     * Call ONLY after the level is completed (not on failure).
     * Returns 1, 2, or 3 based on lives lost.
     *
     * 3 Stars → 0 lives lost
     * 2 Stars → 1 life lost
     * 1 Star  → 2 lives lost
     * 0       → failed (called only if game over before target)
     */
    public int calculateStars() {
        if (!isLevelComplete()) return 0; // failed
        if (livesLostThisLevel == 0) return 3;
        if (livesLostThisLevel == 1) return 2;
        return 1; // 2 lives lost (with 3 lives base, you can't lose all 3 and still complete)
    }

    public void levelUp() {
        currentLevel++;
        currentScore       = 0;
        livesLostThisLevel = 0;
        lives              = BASE_LIVES;
        targetScore        = (int)(targetScore * difficultyMultiplier);
        if (currentLevel % 5 == 0)
            difficultyMultiplier = Math.min(difficultyMultiplier + 0.05, 2.0);
        ballSpeed = Math.min(BASE_SPEED + (currentLevel - 1) * 0.7f, 35f);
    }
}
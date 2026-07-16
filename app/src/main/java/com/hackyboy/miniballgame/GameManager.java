package com.hackyboy.miniballgame;

public class GameManager {
    public int  currentLevel;
    public int  currentScore;
    public int  targetScore;
    public int  lives;
    public float ballSpeed;
    public double difficultyMultiplier;

    private static final int   BASE_TARGET = 500;
    private static final int   BASE_LIVES  = 3;
    private static final float BASE_SPEED  = 25f;

    public GameManager() { reset(); }

    public void reset() {
        currentLevel = 1;
        currentScore = 0;
        targetScore  = BASE_TARGET;
        lives        = BASE_LIVES;
        ballSpeed    = BASE_SPEED;
        difficultyMultiplier = 1.15;
    }

    /** Returns true when the level is complete. */
    public boolean addScore(int pts) {
        currentScore += pts;
        return currentScore >= targetScore;
    }

    public boolean isLevelComplete() { return currentScore >= targetScore; }
    public boolean isGameOver()      { return lives <= 0; }

    /** Call after level-complete screen is dismissed. */
    public void levelUp() {
        currentLevel++;
        targetScore = (int)(targetScore * difficultyMultiplier);
        if (currentLevel % 5 == 0)
            difficultyMultiplier = Math.min(difficultyMultiplier + 0.05, 2.0);
        ballSpeed = Math.min(BASE_SPEED + (currentLevel - 1) * 0.7f, 22f);
    }

    /** Returns true when game over. */
    public boolean loseLife() {
        lives--;
        return lives <= 0;
    }
}

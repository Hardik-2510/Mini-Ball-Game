package com.hackyboy.miniballgame;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    // ── Objects ──────────────────────────────────────────────────
    private Ball         ball;
    private Paddle       paddle;
    private List<Brick>  bricks;
    private GameManager  gm;
    private GameThread   gameThread;
    private final Random rng = new Random();

    // ── Screen ───────────────────────────────────────────────────
    private int screenW, screenH;
    private static final int HUD_H = 120;

    // ── Level number passed from GameActivity via Intent ─────────
    private int levelNumber;

    // ── Input / State ────────────────────────────────────────────
    private volatile float   touchX;
    private volatile boolean ballLaunched = false;

    private enum State { PLAYING, LEVEL_COMPLETE, GAME_OVER }
    private volatile State state = State.PLAYING;

    // ── Paints ───────────────────────────────────────────────────
    private final Paint bgPaint      = new Paint();
    private final Paint hudBgPaint   = new Paint();
    private final Paint hudLinePaint = new Paint();
    private final Paint labelPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint valuePaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint overlayPaint = new Paint();
    private final Paint titlePaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint accentPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint hintPaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint lifePaint    = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint starPaint    = new Paint(Paint.ANTI_ALIAS_FLAG); // NEW: for star rating

    // ── NEW Constructor: accepts levelNumber from GameActivity ────
    public GameView(Context ctx, int levelNumber) {
        super(ctx);
        this.levelNumber = levelNumber;
        getHolder().addCallback(this);
        setFocusable(true);
        gm     = new GameManager();
        gm.setupLevel(levelNumber);   // use per-level setup instead of reset()
        bricks = new ArrayList<>();
        setupPaints();
    }

    private void setupPaints() {
        bgPaint.setColor(0xFF0D0D1A);

        hudBgPaint.setColor(0xEE08081A);

        hudLinePaint.setColor(0xFF00D4FF);
        hudLinePaint.setStrokeWidth(1.5f);

        labelPaint.setColor(0xAAFFFFFF);
        labelPaint.setTextSize(24f);

        valuePaint.setColor(Color.WHITE);
        valuePaint.setFakeBoldText(true);
        valuePaint.setTextSize(38f);

        overlayPaint.setColor(0xCC080814);

        titlePaint.setColor(Color.WHITE);
        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setFakeBoldText(true);
        titlePaint.setTextSize(68f);

        accentPaint.setColor(0xFFFFD700);
        accentPaint.setTextAlign(Paint.Align.CENTER);
        accentPaint.setFakeBoldText(true);
        accentPaint.setTextSize(46f);

        hintPaint.setColor(0xBBFFFFFF);
        hintPaint.setTextAlign(Paint.Align.CENTER);
        hintPaint.setTextSize(34f);

        lifePaint.setAntiAlias(true);

        // Star paint setup
        starPaint.setTextAlign(Paint.Align.CENTER);
        starPaint.setTextSize(72f);
        starPaint.setFakeBoldText(true);
    }

    // ── Surface Callbacks ────────────────────────────────────────
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        screenW = getWidth();
        screenH = getHeight();
        touchX  = screenW / 2f;

        paddle = new Paddle(screenW, screenH);
        ball   = new Ball(screenW / 2f, paddle.y - 30f, gm.ballSpeed);
        generateBricks();

        gameThread = new GameThread(getHolder(), this);
        gameThread.setRunning(true);
        gameThread.start();
    }

    @Override public void surfaceChanged(SurfaceHolder h, int f, int w, int ht) {}

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameThread.setRunning(false);
        boolean stopped = false;
        while (!stopped) {
            try { gameThread.join(); stopped = true; }
            catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
    }

    // ── Brick Generation ─────────────────────────────────────────
    private void generateBricks() {
        bricks.clear();
        int   maxRows = Math.min(4 + gm.currentLevel, 10);
        float pad     = 6f;
        float curY    = HUD_H + 18f;

        for (int row = 0; row < maxRows; row++) {
            int   cols   = 3 + rng.nextInt(4);          // 3–6 cols
            float rowH   = 28f + rng.nextInt(26);        // 28–53 px
            float brickW = (screenW - pad * (cols + 1)) / cols;

            for (int col = 0; col < cols; col++) {
                float bx = pad + col * (brickW + pad);
                bricks.add(new Brick(bx, curY, brickW, rowH, rng));
            }
            curY += rowH + pad;
        }
    }

    // ── Update ───────────────────────────────────────────────────
    public void update() {
        if (state != State.PLAYING) return;

        paddle.setPosition(touchX);

        if (!ballLaunched) {
            ball.x = paddle.x + paddle.width / 2f;
            ball.y = paddle.y - ball.radius - 2f;
            return;
        }

        ball.update();

        // Wall collisions
        if (ball.x - ball.radius < 0) {
            ball.x = ball.radius;
            ball.velocityX = Math.abs(ball.velocityX);
        }
        if (ball.x + ball.radius > screenW) {
            ball.x = screenW - ball.radius;
            ball.velocityX = -Math.abs(ball.velocityX);
        }
        if (ball.y - ball.radius < HUD_H) {
            ball.y = HUD_H + ball.radius;
            ball.velocityY = Math.abs(ball.velocityY);
        }

        // Ball lost (fell below screen)
        if (ball.y - ball.radius > screenH) {
            ballLaunched = false;
            if (gm.loseLife()) {
                state = State.GAME_OVER;
                return;
            }
            resetBall();
            return;
        }

        // Paddle collision
        RectF pb = paddle.getBounds(), bb = ball.getBounds();
        if (ball.velocityY > 0 && RectF.intersects(bb, pb)) {
            float hit = (ball.x - paddle.x) / paddle.width;   // 0..1
            ball.velocityX = gm.ballSpeed * 1.6f * (hit - 0.5f);
            ball.velocityY = -gm.ballSpeed;
            if (Math.abs(ball.velocityX) < 2.5f)
                ball.velocityX = (ball.x < screenW / 2f) ? -2.5f : 2.5f;
            ball.y = paddle.y - ball.radius;
        }

        // Brick collisions
        for (Brick brick : bricks) {
            if (brick.isDestroyed) continue;
            RectF brb = brick.getBounds();
            if (!RectF.intersects(ball.getBounds(), brb)) continue;

            brick.isDestroyed = true;
            if (gm.addScore(brick.points)) {
                state = State.LEVEL_COMPLETE;
                return;
            }

            // Overlap-based bounce
            bb = ball.getBounds();
            float oL = bb.right  - brb.left,  oR = brb.right  - bb.left;
            float oT = bb.bottom - brb.top,   oB = brb.bottom - bb.top;
            if (Math.min(oL, oR) < Math.min(oT, oB))
                ball.velocityX = (oL < oR) ? -Math.abs(ball.velocityX) : Math.abs(ball.velocityX);
            else
                ball.velocityY = (oT < oB) ? -Math.abs(ball.velocityY) : Math.abs(ball.velocityY);
            break;
        }
    }

    private void resetBall() {
        ball.x = paddle.x + paddle.width / 2f;
        ball.y = paddle.y - ball.radius - 2f;
        ball.velocityX = gm.ballSpeed * (rng.nextBoolean() ? 0.6f : -0.6f);
        ball.velocityY = -gm.ballSpeed;
    }

    // ── Render ───────────────────────────────────────────────────
    public void render(Canvas canvas) {
        if (canvas == null) return;

        canvas.drawRect(0, 0, screenW, screenH, bgPaint);
        for (Brick b : bricks) b.draw(canvas);
        paddle.draw(canvas);
        ball.draw(canvas);
        drawHUD(canvas);

        if (!ballLaunched && state == State.PLAYING)
            canvas.drawText("TAP TO LAUNCH", screenW / 2f, paddle.y - 55f, hintPaint);

        if (state == State.LEVEL_COMPLETE) drawLevelComplete(canvas);
        if (state == State.GAME_OVER)      drawGameOver(canvas);
    }

    // ── HUD ──────────────────────────────────────────────────────
    private void drawHUD(Canvas canvas) {
        canvas.drawRect(0, 0, screenW, HUD_H, hudBgPaint);
        canvas.drawLine(0, HUD_H, screenW, HUD_H, hudLinePaint);

        float cx   = screenW / 2f;
        float col2 = screenW * 0.73f;

        // Labels
        labelPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("SCORE", 18, 34, labelPaint);
        labelPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("LEVEL", cx, 34, labelPaint);
        labelPaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText("LIVES", col2, 34, labelPaint);

        // Values
        valuePaint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(String.valueOf(gm.currentScore), 18, 80, valuePaint);
        valuePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(gm.currentLevel), cx, 80, valuePaint);

        // Life dots
        drawLives(canvas, col2, 68f);

        // Progress bar
        drawProgressBar(canvas);
    }

    private void drawLives(Canvas canvas, float startX, float cy) {
        float r = 9f, gap = 24f;
        for (int i = 0; i < 3; i++) {
            lifePaint.setColor(i < gm.lives ? 0xFFFF4455 : 0x33FFFFFF);
            canvas.drawCircle(startX + i * gap, cy, r, lifePaint);
        }
    }

    private void drawProgressBar(Canvas canvas) {
        float bx = 18f, by = HUD_H - 13f, bw = screenW - 36f, bh = 9f;
        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setColor(0x33FFFFFF);
        canvas.drawRoundRect(new RectF(bx, by, bx + bw, by + bh), 5, 5, bg);

        float pct = Math.min(1f, (float) gm.currentScore / gm.targetScore);
        if (pct > 0) {
            Paint fill = new Paint(Paint.ANTI_ALIAS_FLAG);
            fill.setShader(new LinearGradient(bx, by, bx + bw, by,
                    0xFF00FF88, 0xFF00AAFF, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(new RectF(bx, by, bx + bw * pct, by + bh), 5, 5, fill);
        }

        // Target label
        hintPaint.setTextAlign(Paint.Align.RIGHT);
        hintPaint.setTextSize(20f);
        canvas.drawText("TARGET " + gm.targetScore, screenW - 18f, by - 4f, hintPaint);
        hintPaint.setTextAlign(Paint.Align.CENTER);
        hintPaint.setTextSize(34f);
    }

    // ── Overlays ─────────────────────────────────────────────────

    /**
     * NEW: Draws stars (★ filled / ☆ empty) on the Level Complete overlay.
     * Uses Unicode text characters — no image assets needed.
     *
     * Positions 3 stars centered horizontally at the given Y.
     */
    private void drawStarRating(Canvas canvas, int starsEarned, float centerY) {
        float spacing = 90f;
        float startX  = screenW / 2f - spacing; // center of first star

        for (int i = 1; i <= 3; i++) {
            starPaint.setColor(i <= starsEarned ? 0xFFFFD700 : 0x55FFFFFF);
            canvas.drawText(i <= starsEarned ? "★" : "☆",
                    startX + (i - 1) * spacing, centerY, starPaint);
        }
    }

    /**
     * MODIFIED: Now shows star rating on the Level Complete screen.
     * Stars are calculated from gm.calculateStars() which reads livesLostThisLevel.
     */
    private void drawLevelComplete(Canvas canvas) {
        canvas.drawRect(0, 0, screenW, screenH, overlayPaint);

        float cy = screenH / 2f;
        int starsEarned = gm.calculateStars();

        // Title
        titlePaint.setColor(0xFF00FF88);
        canvas.drawText("LEVEL CLEAR!", screenW / 2f, cy - 160f, titlePaint);

        // Star rating
        drawStarRating(canvas, starsEarned, cy - 60f);

        // Star label text
        accentPaint.setColor(0xFFFFD700);
        accentPaint.setTextSize(38f);
        String starLabel;
        switch (starsEarned) {
            case 3:  starLabel = "Perfect! No lives lost!"; break;
            case 2:  starLabel = "Great! 1 life lost";      break;
            default: starLabel = "Done! 2 lives lost";      break;
        }
        canvas.drawText(starLabel, screenW / 2f, cy + 10f, accentPaint);

        // Score
        accentPaint.setColor(Color.WHITE);
        accentPaint.setTextSize(36f);
        canvas.drawText("Score: " + gm.currentScore, screenW / 2f, cy + 70f, accentPaint);

        // Hint
        accentPaint.setTextSize(46f);
        canvas.drawText("Tap to continue →", screenW / 2f, cy + 150f, hintPaint);
    }

    /**
     * MODIFIED: GAME OVER now shows "Tap to retry" instead of resetting the
     * whole game — it restarts the current level only.
     */
    private void drawGameOver(Canvas canvas) {
        canvas.drawRect(0, 0, screenW, screenH, overlayPaint);

        float cy = screenH / 2f;

        titlePaint.setColor(0xFFFF3344);
        canvas.drawText("GAME OVER", screenW / 2f, cy - 120f, titlePaint);

        accentPaint.setColor(Color.WHITE);
        accentPaint.setTextSize(46f);
        canvas.drawText("Score: "  + gm.currentScore, screenW / 2f, cy - 20f,  accentPaint);
        accentPaint.setTextSize(34f);
        canvas.drawText("Level: "  + gm.currentLevel, screenW / 2f, cy + 50f,  accentPaint);
        canvas.drawText("Target: " + gm.targetScore,  screenW / 2f, cy + 100f, accentPaint);

        canvas.drawText("Tap to retry level", screenW / 2f, cy + 170f, hintPaint);
    }

    // ── Touch ─────────────────────────────────────────────────────
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        int action = e.getActionMasked();
        if (action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_MOVE)
            return true;

        touchX = e.getX();

        if (state == State.LEVEL_COMPLETE) {
            handleLevelCompleteTap();
            return true;
        }

        if (state == State.GAME_OVER) {
            handleGameOverTap();
            return true;
        }

        // Launch ball on first tap during PLAYING
        if (!ballLaunched) ballLaunched = true;
        return true;
    }

    // ── NEW: Level Complete handler ───────────────────────────────
    /**
     * Called when the player taps on the LEVEL_COMPLETE overlay.
     *
     * 1. Calculates stars from lives lost this level.
     * 2. Saves progress (stars + unlock next level) via LevelProgressManager.
     * 3. Launches LevelResultActivity with results, then finishes GameActivity.
     */
    private void handleLevelCompleteTap() {
        int starsEarned = gm.calculateStars();

        // Persist progress
        LevelProgressManager pm = new LevelProgressManager(getContext());
        pm.saveLevelResult(levelNumber, starsEarned);

        // Go to result screen
        Intent intent = new Intent(getContext(), LevelResultActivity.class);
        intent.putExtra("LEVEL_NUMBER", levelNumber);
        intent.putExtra("STARS_EARNED", starsEarned);
        intent.putExtra("SCORE",        gm.currentScore);
        intent.putExtra("TARGET",       gm.targetScore);
        ((Activity) getContext()).startActivity(intent);
        ((Activity) getContext()).finish();
    }

    // ── NEW: Game Over handler ────────────────────────────────────
    /**
     * Called when the player taps on the GAME_OVER overlay.
     *
     * Restarts the SAME level (not the whole game).
     * GameActivity receives LEVEL_NUMBER and calls gm.setupLevel() again.
     */
    private void handleGameOverTap() {
        Intent intent = new Intent(getContext(), GameActivity.class);
        intent.putExtra("LEVEL_NUMBER", levelNumber);
        // Clear the back stack so pressing Back doesn't return to a dead game
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ((Activity) getContext()).startActivity(intent);
        ((Activity) getContext()).finish();
    }
}
package com.hackyboy.miniballgame;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class Ball {
    public float x, y;
    public float velocityX, velocityY;
    public final float radius = 20f;

    private final Paint ballPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Ball(float x, float y, float speed) {
        this.x = x;
        this.y = y;
        this.velocityX = speed;
        this.velocityY = -speed;
        ballPaint.setColor(Color.WHITE);
        shinePaint.setColor(0xAADDEEFF);
    }

    public void update() {
        x += velocityX;
        y += velocityY;
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle(x, y, radius, ballPaint);
        canvas.drawCircle(x - radius * 0.3f, y - radius * 0.3f, radius * 0.35f, shinePaint);
    }

    public RectF getBounds() {
        return new RectF(x - radius, y - radius, x + radius, y + radius);
    }
}

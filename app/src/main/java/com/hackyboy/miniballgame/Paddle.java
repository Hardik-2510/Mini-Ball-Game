package com.hackyboy.miniballgame;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

public class Paddle {
    public float x, y;
    public final float width = 280f;
    public final float height = 26f;
    private final int screenWidth;

    private final Paint gradPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint shinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public Paddle(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.x = (screenWidth - width) / 2f;
        this.y = screenHeight - 160f;

        gradPaint.setShader(new LinearGradient(
                0, y, 0, y + height,
                0xFF00FFFF, 0xFF006699,
                Shader.TileMode.CLAMP));

        shinePaint.setColor(0x55FFFFFF);
    }

    public void setPosition(float touchX) {
        x = touchX - width / 2f;
        if (x < 0) x = 0;
        if (x + width > screenWidth) x = screenWidth - width;
    }

    public void draw(Canvas canvas) {
        RectF rect = new RectF(x, y, x + width, y + height);
        canvas.drawRoundRect(rect, 13, 13, gradPaint);
        canvas.drawRoundRect(new RectF(x + 6, y + 3, x + width - 6, y + height / 2f), 8, 8, shinePaint);
    }

    public RectF getBounds() {
        return new RectF(x, y, x + width, y + height);
    }
}

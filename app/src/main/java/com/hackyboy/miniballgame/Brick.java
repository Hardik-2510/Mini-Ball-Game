package com.hackyboy.miniballgame;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import java.util.Random;

public class Brick {
    public float x, y, width, height;
    public int points;
    public boolean isDestroyed = false;

    private final Paint fillPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint  = new Paint(Paint.ANTI_ALIAS_FLAG);

    private static final int[] COLORS = {
            0xFFE74C3C, 0xFF27AE60, 0xFF2980B9,
            0xFFF39C12, 0xFF8E44AD, 0xFF16A085,
            0xFFE91E63, 0xFFFF5722, 0xFF00BCD4
    };
    private static final int[] POINT_VALUES = {10, 15, 20, 25, 30};

    public Brick(float x, float y, float width, float height, Random rng) {
        this.x = x;  this.y = y;
        this.width = width; this.height = height;

        int color  = COLORS[rng.nextInt(COLORS.length)];
        this.points = POINT_VALUES[rng.nextInt(POINT_VALUES.length)];

        fillPaint.setShader(new LinearGradient(
                x, y, x, y + height,
                lighten(color, 0.3f), darken(color, 0.25f),
                Shader.TileMode.CLAMP));

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1.5f);
        borderPaint.setColor(0x66FFFFFF);

        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(Math.min(height * 0.52f, 20f));
        textPaint.setFakeBoldText(true);
    }

    public void draw(Canvas canvas) {
        if (isDestroyed) return;
        RectF r = new RectF(x + 2, y + 2, x + width - 2, y + height - 2);
        canvas.drawRoundRect(r, 6, 6, fillPaint);
        canvas.drawRoundRect(r, 6, 6, borderPaint);
        canvas.drawText("+" + points,
                x + width / 2f,
                y + height / 2f + textPaint.getTextSize() * 0.38f,
                textPaint);
    }

    public RectF getBounds() {
        return new RectF(x, y, x + width, y + height);
    }

    private int lighten(int c, float f) {
        return Color.rgb(
                Math.min(255, (int)(Color.red(c)   + 255 * f)),
                Math.min(255, (int)(Color.green(c) + 255 * f)),
                Math.min(255, (int)(Color.blue(c)  + 255 * f)));
    }
    private int darken(int c, float f) {
        return Color.rgb(
                Math.max(0, (int)(Color.red(c)   * (1 - f))),
                Math.max(0, (int)(Color.green(c) * (1 - f))),
                Math.max(0, (int)(Color.blue(c)  * (1 - f))));
    }
}

package com.hackyboy.miniballgame;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

public class GameThread extends Thread {
    private final SurfaceHolder holder;
    private final GameView      gameView;
    private volatile boolean    running = false;

    private static final long FRAME_NS = 1_000_000_000L / 60;

    public GameThread(SurfaceHolder holder, GameView view) {
        super("GameThread");
        this.holder   = holder;
        this.gameView = view;
    }

    public void setRunning(boolean r) { running = r; }

    @Override
    public void run() {
        while (running) {
            long start  = System.nanoTime();
            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    synchronized (holder) {
                        gameView.update();
                        gameView.render(canvas);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try { holder.unlockCanvasAndPost(canvas); }
                    catch (Exception e) { e.printStackTrace(); }
                }
            }
            long sleepMs = (FRAME_NS - (System.nanoTime() - start)) / 1_000_000;
            if (sleepMs > 0) {
                try { Thread.sleep(sleepMs); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            }
        }
    }
}


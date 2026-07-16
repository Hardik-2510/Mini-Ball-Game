# Mini Ball Game

A classic arcade-style brick breaker game for Android, built using Java and `SurfaceView`.

## Features
- **Dynamic Level Generation**: Each level generates a new set of bricks with varying sizes and point values.
- **Progression System**: Difficulty increases as you level up, with higher target scores and faster ball speeds.
- **Classic Gameplay**: Paddle movement controlled by touch, physics-based ball bouncing, and life management.
- **HUD & UI**: Real-time score tracking, level indicator, life count, and a level progress bar.

## Technical Details
- **Engine**: Custom game loop implemented using `SurfaceView` and a dedicated `GameThread`.
- **Graphics**: 2D graphics rendered using Android's `Canvas` API with custom `Paint` styles and gradients.
- **State Management**: Managed via a `GameManager` class handling scores, levels, and game states (Playing, Level Complete, Game Over).

## How to Play
1. Launch the app.
2. Tap the screen to launch the ball.
3. Move the paddle by dragging your finger horizontally.
4. Break bricks to increase your score.
5. Reach the **Target Score** to complete the level.
6. Don't let the ball fall below the paddle! You have 3 lives to start.

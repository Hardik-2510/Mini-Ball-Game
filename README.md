
---

## 🎮 Game Mechanics

###1. Physics & Collisions
The game uses **RectF-based AABB collision detection**.
- **Wall Bouncing**: Velocity is inverted when the ball's radius exceeds the screen bounds.
- **Paddle Influence**: The bounce angle is calculated based on the horizontal distance from the paddle center.
    - *Formula*: `ball.velocityX = ballSpeed * 1.6 * (hitPercentage - 0.5)`
- **Brick Response**: Overlap-based resolution ensures the ball bounces off the narrowest side of the brick for realistic physics.

### 2. Progression System
The difficulty scales dynamically as the player advances through levels.

| Attribute | Base Value | Scaling Logic | Max Cap |
| :--- | :--- | :--- | :--- |
| **Target Score** | 500 | `Target * Multiplier` | N/A |
| **Ball Speed** | 25.0 | `+0.7 per level` | 35.0 (capped) |
| **Difficulty Multiplier** | 1.15 | `+0.05 every 5 levels` | 2.0 |
| **Max Rows** | 5 | `4 + currentLevel` | 10 |

---

## 💎 Entity Breakdown

### Bricks
Each level generates a procedural grid of bricks with randomized attributes:
- **Points**: Randomly assigned from `{10, 15, 20, 25, 30}`.
- **Visuals**: Dynamic colors with `LinearGradient` shading and "lighten/darken" border effects.
- **Layout**: Adaptive columns (3-6) based on screen width.

### HUD (Heads-Up Display)
The HUD provides real-time feedback using a custom glass-morphism style:
- **Neon Progress Bar**: Uses a `LinearGradient` from Green (`#00FF88`) to Cyan (`#00AAFF`).
- **Life Indicators**: Heart-style dots that dim as lives are lost.
- **Target Indicator**: Shows the score required to clear the current level.

---

## 🛠️ Technical Stack

- **Language**: Java 8+
- **Graphics**: Android `Canvas` API (Hardware Accelerated)
- **Threading**: Custom `GameThread` with a 60 FPS target (`16.6ms` frame time).
- **UI Architecture**: Single Activity with a custom `SurfaceView`.

---

## 🚀 How to Run
1. Clone the repository.
2. Open in **Android Studio**.
3. Sync Gradle and run on a device/emulator with **API 21+**.
4. **Controls**: Swipe horizontally to move the paddle; tap to launch the ball.
   """
   )
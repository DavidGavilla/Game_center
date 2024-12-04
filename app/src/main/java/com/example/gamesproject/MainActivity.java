package com.example.gamesproject;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;
    private TextView[][] tiles;
    private int[][] grid;
    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game_grid), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize tiles and grid
        tiles = new TextView[4][4];
        grid = new int[4][4];

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int resId = getResources().getIdentifier("tile_" + i + j, "id", getPackageName());
                tiles[i][j] = findViewById(resId);

                if (tiles[i][j] == null) {
                    throw new IllegalStateException("Tile not found for ID: tile_" + i + j);
                }
                grid[i][j] = 0; // Initialize the grid
            }
        }

        updateRandomTile();
        updateUI();

        // Gesture Detector for swipe handling
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float deltaX = e2.getX() - e1.getX();
                float deltaY = e2.getY() - e1.getY();

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (deltaX > 0) onSwipe(1); // Right
                    else onSwipe(2); // Left
                } else {
                    if (deltaY > 0) onSwipe(4); // Down
                    else onSwipe(3); // Up
                }
                return true;
            }
        });

        findViewById(R.id.game_grid).setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private void onSwipe(int direction) {
        if (CheckGameOver()) {
            showGameOverMessage();
            return; // Stop if the game is over
        }

        moveTiles(direction);
        updateRandomTile();
        updateUI();

        if (CheckGameOver()) {
            showGameOverMessage();
        }
    }

    private void moveTiles(int direction) {
        switch (direction) {
            case 1: // Swipe Right
                for (int i = 0; i < 4; i++) {
                    int[] newRow = new int[4];
                    int pos = 3;

                    for (int j = 3; j >= 0; j--) {
                        if (grid[i][j] != 0) {
                            if (pos < 3 && newRow[pos + 1] == grid[i][j]) {
                                newRow[pos + 1] *= 2;
                                score += newRow[pos + 1];
                            } else {
                                newRow[pos--] = grid[i][j];
                            }
                        }
                    }
                    grid[i] = newRow;
                }
                break;

            case 2: // Swipe Left

                break;

            case 3: // Swipe Up

                break;

            case 4: // Swipe Down

                break;
        }
    }

    private void updateRandomTile() {
        Random random = new Random();
        int row, col;

        // Find a random empty tile
        do {
            row = random.nextInt(4);
            col = random.nextInt(4);
        } while (grid[row][col] != 0);

        grid[row][col] = (random.nextInt(100) < 75) ? 2 : 4;
    }

    private void updateUI() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int value = grid[i][j];
                tiles[i][j].setText(value == 0 ? "" : String.valueOf(value));

                GradientDrawable background = (GradientDrawable) getResources().getDrawable(R.drawable.rounded_tile, null);
                int colorResource = getColorForTile(value);
                background.setColor(getResources().getColor(colorResource, null));
                tiles[i][j].setBackground(background);
            }
        }

        TextView scoreText = findViewById(R.id.score);
        scoreText.setText("Score: " + score);
    }

    private int getColorForTile(int value) {
        switch (value) {
            case 2: return R.color.background_2;
            case 4: return R.color.background_4;
            case 8: return R.color.background_8;
            case 16: return R.color.background_16;
            case 32: return R.color.background_32;
            case 64: return R.color.background_64;
            case 128: return R.color.background_128;
            case 256: return R.color.background_256;
            case 512: return R.color.background_512;
            case 1024: return R.color.background_1024;
            case 2048: return R.color.background_2048;
            default: return R.color.background_empty;
        }
    }

    private boolean CheckGameOver() {
        boolean end_game = true;
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                if (grid[i][j] == 0){
                    end_game = false;

                };
                if (j < 3 && grid[i][j] == grid[i][j + 1]){
                    end_game = false;

                };
                if (i < 3 && grid[i][j] == grid[i + 1][j]){
                    end_game = false;

                };
                if (i > 0 && grid[i][j] == grid[i - 1][j]){
                    end_game = false;

                };
                if (j > 0 && grid[i][j] == grid[i][j - 1]){
                    end_game = false;

                };
            }
        }
        return end_game;
    }

    private void showGameOverMessage() {
        Toast.makeText(this, "Game Over! Final score: " + score, Toast.LENGTH_LONG).show();
        restartGame(); // Automatically restart the game after showing the toast
    }


    private void restartGame() {
        grid = new int[4][4];
        score = 0;
        updateRandomTile();
        updateUI();
    }
}

package com.example.gamescenter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class activity_2048 extends AppCompatActivity {

    private com.example.gamescenter.GameManager_2048 gameManager;
    private GestureDetector gestureDetector;
    private TextView timerTextView;
    private Handler timerHandler = new Handler();
    private long startTime = 0;
    private boolean timerRunning = false;
    private Button undoButton;

    // Runnable to update the timer every second
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            // Update the timer TextView
            timerTextView.setText(String.format("%d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 1000); // Run again in 1 second
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Lock screen in portrait mode
        EdgeToEdge.enable(this); // Enable full-screen experience
        setContentView(R.layout.activity_2048);

        // Adjust UI padding for system bars (notch, navigation bar, etc.)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game_grid), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize timer TextView
        timerTextView = findViewById(R.id.time_played);
        if (timerTextView == null) {
            throw new IllegalStateException("Timer TextView not found. Make sure to add it to your layout.");
        }

        // Initialize the undo button
        undoButton = findViewById(R.id.undo_button);
        if (undoButton == null) {
            throw new IllegalStateException("Undo button not found. Make sure to add it to your layout.");
        }

        // Back button to return to the main menu
        Button back = findViewById(R.id.Back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopTimer(); // Stop timer before switching activities
                Intent intent = new Intent(activity_2048.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Restart button to reset the game
        Button restartButton = findViewById(R.id.restart_button);
        restartButton.setOnClickListener(view -> {
            gameManager.restartGame(); // Reset game state
            resetTimer(); // Reset timer display
            startTimer(); // Restart the timer
        });

        // Set up undo button click listener
        undoButton.setOnClickListener(view -> gameManager.undoLastMove());

        // Initialize game board (4x4 grid of tiles)
        TextView[][] tiles = new TextView[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int resId = getResources().getIdentifier("tile_" + i + j, "id", getPackageName());
                tiles[i][j] = findViewById(resId);
                if (tiles[i][j] == null) {
                    throw new IllegalStateException("Tile not found for ID: tile_" + i + j);
                }
            }
        }

        // Initialize the game manager with the tiles and score display
        TextView scoreView = findViewById(R.id.score);
        gameManager = new com.example.gamescenter.GameManager_2048(tiles, scoreView, this);

        // Set up gesture detection for swipe controls
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float deltaX = e2.getX() - e1.getX();
                float deltaY = e2.getY() - e1.getY();

                // Determine swipe direction
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (deltaX > 0) {
                        try {
                            gameManager.onSwipe(Direction.RIGHT);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            gameManager.onSwipe(Direction.LEFT);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    if (deltaY > 0) {
                        try {
                            gameManager.onSwipe(Direction.DOWN);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            gameManager.onSwipe(Direction.UP);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                return true;
            }
        });

        // Attach gesture detector to the game grid
        findViewById(R.id.game_grid).setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        // Start the timer when the game starts
        startTimer();
    }

    // Start the game timer
    private void startTimer() {
        if (!timerRunning) {
            startTime = System.currentTimeMillis();
            timerHandler.postDelayed(timerRunnable, 0);
            timerRunning = true;
        }
    }

    // Stop the game timer
    private void stopTimer() {
        timerHandler.removeCallbacks(timerRunnable);
        timerRunning = false;
    }

    // Reset the game timer to 0:00
    private void resetTimer() {
        stopTimer();
        timerTextView.setText("0:00");
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer(); // Stop the timer when the app is paused
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!timerRunning) {
            startTimer(); // Resume the timer if it was running before pausing
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    // Save game state before activity is destroyed (e.g., screen rotation)
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        gameManager.saveGameState(outState); // Save game state
        outState.putLong("startTime", startTime); // Save timer start time
        outState.putBoolean("timerRunning", timerRunning); // Save timer state
    }

    // Restore game state when activity is recreated
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        gameManager.restoreGameState(savedInstanceState); // Restore game state
        startTime = savedInstanceState.getLong("startTime"); // Restore timer start time
        timerRunning = savedInstanceState.getBoolean("timerRunning"); // Restore timer state
        if (timerRunning) {
            timerHandler.postDelayed(timerRunnable, 0); // Resume timer if it was running
        }
    }
}

// Enum for swipe directions in the 2048 game
enum Direction {
    UP, DOWN, LEFT, RIGHT
}

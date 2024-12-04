package com.example.gamesproject;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private GestureDetector gestureDetector;
    private TextView[][] tiles;

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

        // Initialize tiles array
        tiles = new TextView[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int resId = getResources().getIdentifier("tile_" + i + j, "id", getPackageName());
                tiles[i][j] = findViewById(resId);
            }
        }

        // Gesture Detector to handle swipe
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float deltaX = e2.getX() - e1.getX();
                float deltaY = e2.getY() - e1.getY();
                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (deltaX > 0) {
                        onSwipeRight();
                    } else {
                        onSwipeLeft();
                    }
                } else {
                    if (deltaY > 0) {
                        onSwipeDown();
                    } else {
                        onSwipeUp();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private void onSwipeRight() {
        move_tiles(1);
        updateRandomTile();
    }

    private void onSwipeLeft() {
        move_tiles(2);
        updateRandomTile();
    }

    private void onSwipeUp() {
        move_tiles(3);
        updateRandomTile();
    }

    private void onSwipeDown() {
        move_tiles(4);
        updateRandomTile();
    }

    private void updateRandomTile() {
        Random random = new Random();
        int row, col;

        // Find a random empty tile
        do {
            row = random.nextInt(4);
            col = random.nextInt(4);
        } while (!tiles[row][col].getText().toString().isEmpty());

        // Set the tile value to 2 or 4 randomly
        int newValue = random.nextBoolean() ? 2 : 4;

        // Use getResources().getColor() to fetch the background color
        int backgroundColor;
        if (newValue == 4) {
            backgroundColor = getResources().getColor(R.color.background_4, null); // Fetch color for value 4
        } else {
            backgroundColor = getResources().getColor(R.color.background_2, null); // Fetch color for value 2
        }

        // Set the tile background color and text
        tiles[row][col].setBackgroundColor(backgroundColor);
        tiles[row][col].setText(String.valueOf(newValue));
    }


    public void move_tiles(int x){



    }
}

package com.example.gamescenter;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.Random;

public class activity_2048 extends AppCompatActivity {

    private com.example.gamescenter.GameManager_2048 gameManager;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_2048);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.game_grid), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button back = findViewById(R.id.Back_button);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_2048.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button restartButton = findViewById(R.id.restart_button);
        restartButton.setOnClickListener(view -> gameManager.restartGame());

        // Initialize GameManager
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

        TextView scoreView = findViewById(R.id.score);
        gameManager = new com.example.gamescenter.GameManager_2048(tiles, scoreView, this);

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float deltaX = e2.getX() - e1.getX();
                float deltaY = e2.getY() - e1.getY();

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    if (deltaX > 0) gameManager.onSwipe(Direction.RIGHT);
                    else gameManager.onSwipe(Direction.LEFT);
                } else {
                    if (deltaY > 0) gameManager.onSwipe(Direction.DOWN);
                    else gameManager.onSwipe(Direction.UP);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        gameManager.saveGameState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        gameManager.restoreGameState(savedInstanceState);
    }


}

enum Direction {
    UP, DOWN, LEFT, RIGHT
}
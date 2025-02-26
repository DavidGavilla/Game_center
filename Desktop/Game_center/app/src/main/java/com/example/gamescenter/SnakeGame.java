package com.example.gamescenter;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SnakeGame extends AppCompatActivity {

    private GameManager_SnakeGame snakeGame;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snake_game);

        LinearLayout topContainer = findViewById(R.id.TopContainer);
        LinearLayout bottomContainer = findViewById(R.id.BottomContainer);
        snakeGame = findViewById(R.id.SnakeGame);

        TextView score = findViewById(R.id.scoreText);

        // Wait for the layout to be fully rendered
        snakeGame.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                snakeGame.getScoreView(score);
                // Get accurate top and bottom container heights
                int topHeight = topContainer.getHeight();
                int bottomHeight = bottomContainer.getHeight();

                // Pass margins to SnakeGame
                snakeGame.setScreenMargins(topHeight, bottomHeight);

                // Remove the listener to prevent redundant calls
                snakeGame.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // Start game logic explicitly
                snakeGame.startGame();
            }
        });

        // Set up controls
        Button buttonRight = findViewById(R.id.buttonRight);
        Button buttonLeft = findViewById(R.id.buttonLeft);
        Button buttonUp = findViewById(R.id.buttonTop);
        Button buttonDown = findViewById(R.id.buttonBottom);
        Button pause = findViewById(R.id.Pause_button);
        Button back = findViewById(R.id.Back_button);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SnakeGame.this, MainActivity.class);
                startActivity(intent);
            }
        });


        buttonRight.setOnClickListener(view -> snakeGame.moveRight());
        buttonLeft.setOnClickListener(view -> snakeGame.moveLeft());
        buttonUp.setOnClickListener(view -> snakeGame.moveUp());
        buttonDown.setOnClickListener(view -> snakeGame.moveDown());
        pause.setOnClickListener(view -> snakeGame.pause_unpause());
    }

    public String getUserName() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("USER_NAME", "Guest"); // Default to "Guest"
    }



}

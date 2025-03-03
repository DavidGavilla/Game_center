package com.example.gamescenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_menu), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });




        Button button_2048 = findViewById(R.id.btn2048);
        button_2048.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, activity_2048.class);
                startActivity(intent);
            }
        });

        Button button_snake = findViewById(R.id.btnSnakeGame);

        button_snake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SnakeGame.class);
                startActivity(intent);
            }
        });


        Button log_in = findViewById(R.id.btnLogin);

        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoginPopupMenu(view);
            }
        });

        Button scores = findViewById(R.id.btnScores);
        scores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GameScoresActivity.class);
                startActivity(intent);


            }
        });

    }

    private void showLoginPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.login_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.option_sign_in) {
                Intent intent = new Intent(MainActivity.this, LogInScreen.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.option_sign_up) {

                SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("USER_NAME", "Guest");
                editor.apply();


                return true;
            } else {
                Intent intent = new Intent(MainActivity.this, GameScoresActivity.class);
                startActivity(intent);
                return true;
            }
        });
        popupMenu.show();
    }


}
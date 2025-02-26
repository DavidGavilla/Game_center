package com.example.gamescenter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


public class LogInScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_in_screen);
        Button back = findViewById(R.id.Back_button);
        Button log_in = findViewById(R.id.btnSubmit);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogInScreen.this, MainActivity.class);
                startActivity(intent);
            }
        });

        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = findViewById(R.id.etName);
                String name = input.getText().toString();

                if ((name.length() < 4) || (name.length() > 20)) {
                    showAlert();
                } else {
                    // Passing the name to the next activity

                    SharedPreferences sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("USER_NAME", name);
                    editor.apply(); // Save the data


                    Intent intent = new Intent(LogInScreen.this, MainActivity.class);

                    startActivity(intent);  // Launch the next activity
                }
            }
        });

    }




    private void showAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Incorrect name")
                .setMessage("Names must be longer than 4 letters and shorter than 20")
                .setCancelable(false)
                .setPositiveButton("Okay", (dialog, id) -> {

                    dialog.dismiss(); // Close the dialog
                });


        AlertDialog alert = builder.create();
        alert.show();
    }
}
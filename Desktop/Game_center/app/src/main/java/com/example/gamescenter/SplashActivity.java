package com.example.gamescenter; // Package where this class is located

import android.content.Intent; // Imports the Intent class to switch between activities
import android.os.Bundle; // Imports Bundle to handle activity state data
import android.os.Handler; // Imports Handler to schedule delayed tasks
import androidx.appcompat.app.AppCompatActivity; // Imports AppCompatActivity for backward compatibility with older Android versions

// SplashActivity class extends AppCompatActivity, used to display a splash screen
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // Calls the onCreate method of the superclass
        setContentView(R.layout.activity_splash); // Sets the layout for this activity using the specified XML file

        // Creates a new Handler to execute a code block after a delay
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Creates an Intent to open MainActivity
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent); // Starts the new activity
                finish(); // Closes the current activity so the user cannot return to it
            }
        }, 3000); // Delay of 3 seconds before executing the code inside run()
    }
}

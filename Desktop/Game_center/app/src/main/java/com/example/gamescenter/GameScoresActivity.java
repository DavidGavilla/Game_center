package com.example.gamescenter;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class GameScoresActivity extends AppCompatActivity {
    private ListView listViewScores; // ListView to display game scores
    private Spinner spinnerGameFilter, spinnerPlayerFilter, spinnerSortFilter; // Spinners for filtering
    private GameScoreDbHelper dbHelper; // Database helper for retrieving scores
    private String selectedGame = null; // Stores the selected game for filtering
    private String selectedPlayer = null; // Stores the selected player for filtering
    private boolean sortByHighestScore = true; // Default sorting order (highest scores first)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.games_scores); // Set the layout for the activity

        try {
            initializeUIElements(); // Initialize UI components
            dbHelper = GameScoreDbHelper.getInstance(this); // Get database instance

            // Display scores initially
            dbHelper.displayScores(this, listViewScores, spinnerGameFilter, spinnerPlayerFilter);
            setupFilterListeners(); // Set up event listeners for filter changes
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    // Initializes UI elements and checks if they exist in the layout
    private void initializeUIElements() {
        listViewScores = findViewById(R.id.list_view_scores);
        spinnerGameFilter = findViewById(R.id.spinner_game_filter);
        spinnerPlayerFilter = findViewById(R.id.spinner_player_filter);
        spinnerSortFilter = findViewById(R.id.spinner_score_filter);

        // Ensure UI elements were found in the layout
        if (listViewScores == null || spinnerGameFilter == null || spinnerPlayerFilter == null || spinnerSortFilter == null) {
            throw new IllegalStateException("Unable to find UI elements. Check your layout file.");
        }
    }

    // Sets up listeners for filter selection changes
    private void setupFilterListeners() {
        // Listener for game filter spinner
        spinnerGameFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedGame = position == 0 ? null : (String) parent.getItemAtPosition(position);
                filterScores(); // Update the displayed scores
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedGame = null;
                filterScores();
            }
        });

        // Listener for player filter spinner
        spinnerPlayerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPlayer = position == 0 ? null : (String) parent.getItemAtPosition(position);
                filterScores(); // Update the displayed scores
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedPlayer = null;
                filterScores();
            }
        });

        // Listener for sorting filter (Highest or Lowest Score)
        spinnerSortFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortByHighestScore = position == 0; // If position is 0, sort by highest score, otherwise by lowest
                filterScores(); // Update the displayed scores
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                sortByHighestScore = true; // Default to sorting by highest score
                filterScores();
            }
        });
    }

    // Filters and updates the score list based on the selected filters
    private void filterScores() {
        try {
            dbHelper.updateScoresList(this, listViewScores, selectedGame, selectedPlayer, sortByHighestScore);
        } catch (Exception e) {
            Toast.makeText(this, "Error filtering scores: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh the score list when the activity is resumed
        if (dbHelper != null && listViewScores != null) {
            filterScores();
        }
    }
}

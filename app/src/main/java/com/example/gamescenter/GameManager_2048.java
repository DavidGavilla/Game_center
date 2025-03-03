package com.example.gamescenter;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

class GameManager_2048 {
    private final int[][] grid;
    private final TextView[][] tiles;
    private final TextView scoreView;
    private final AppCompatActivity activity;
    private int score;
    private GameScoreDbHelper DataBase;

    private int[][] previousGrid;
    private int previousScore;


    public GameManager_2048(TextView[][] tiles, TextView scoreView, AppCompatActivity activity) {
        this.tiles = tiles;
        this.scoreView = scoreView;
        this.activity = activity;
        this.grid = new int[4][4];
        this.previousGrid = new int[4][4];
        restartGame();
    }

    public void onSwipe(Direction direction) throws InterruptedException {
        if (!isMoveLegal(direction)) {
            return;
        }
        saveCurrentState();
        moveTiles(direction);
        addRandomTile();
        updateUI();


        if (!canMakeAnyMove()) {

            DataBase = new GameScoreDbHelper(activity);
            showGameOverMessage();
            DataBase.insertScore(getUserName(),"2048",score);


        }
    }

    private void moveTiles(Direction direction) {
        switch (direction) {
            case RIGHT:
                moveRight();
                break;
            case LEFT:
                moveLeft();
                break;
            case UP:
                moveUp();
                break;
            case DOWN:
                moveDown();
                break;
        }
    }

    /**
     * Moves the tiles to the right in a 2048 game grid.
     * Combines tiles with the same value and adds their score.
     */
    private void moveRight() {
        // Loop through each row of the grid
        for (int i = 0; i < 4; i++) {

            // Create a new row to store the modified values after the move
            int[] newRow = new int[4];

            // Position to insert the next tile (start from the rightmost position)
            int pos = 3;

            // Traverse the row from right to left
            for (int j = 3; j >= 0; j--) {

                // Check if the current tile is not empty
                if (grid[i][j] != 0) {

                    // If there is a tile to the right that can be merged with the current tile
                    if (pos < 3 && newRow[pos + 1] == grid[i][j]) {

                        // Merge the tiles by doubling the value
                        newRow[pos + 1] *= 2;

                        // Increase the score with the new merged value
                        score += newRow[pos + 1];

                        // Animation for merging tiles

                        tiles[i][pos + 1].startAnimation(AnimationUtils.loadAnimation(tiles[i][pos + 1].getContext(), R.anim.big_to_small)); // Effect on merged tile

                    } else if (j != pos) { // If the tile is moving and not already in the rightmost position



                        // Place it in the current 'pos' position
                        newRow[pos--] = grid[i][j];

                    } else {
                        // If tile is already in the right position, just copy it
                        newRow[pos--] = grid[i][j];
                    }
                }
            }

            // Update the grid with the modified row after moving right
            grid[i] = newRow;
        }
    }



    private void moveLeft() {
        for (int i = 0; i < 4; i++) {
            int[] newRow = new int[4];
            int pos = 0;
            for (int j = 0; j < 4; j++) {
                if (grid[i][j] != 0) {
                    if (pos > 0 && newRow[pos - 1] == grid[i][j]) {
                        newRow[pos - 1] *= 2;
                        score += newRow[pos - 1];
                        Animation fadeIn = AnimationUtils.loadAnimation(tiles[i][pos - 1].getContext(), R.anim.big_to_small);
                        tiles[i][pos - 1].startAnimation(fadeIn);
                    } else {
                        newRow[pos++] = grid[i][j];

                    }
                }
            }
            grid[i] = newRow;
        }
    }

    private void moveUp() {
        for (int j = 0; j < 4; j++) {
            int[] newColumn = new int[4];
            int pos = 0;
            for (int i = 0; i < 4; i++) {
                if (grid[i][j] != 0) {
                    if (pos > 0 && newColumn[pos - 1] == grid[i][j]) {
                        newColumn[pos - 1] *= 2;
                        score += newColumn[pos - 1];



                        Animation fadeIn = AnimationUtils.loadAnimation(tiles[pos - 1][j].getContext(), R.anim.big_to_small);
                        tiles[pos - 1][j].startAnimation(fadeIn);
                    } else {

                        newColumn[pos++] = grid[i][j];



                    }
                }
            }
            for (int i = 0; i < 4; i++) {
                grid[i][j] = newColumn[i];
            }
        }
    }

    private void moveDown() {
        for (int j = 0; j < 4; j++) {
            int[] newColumn = new int[4];
            int pos = 3;
            for (int i = 3; i >= 0; i--) {
                if (grid[i][j] != 0) {
                    if (pos < 3 && newColumn[pos + 1] == grid[i][j]) {
                        newColumn[pos + 1] *= 2;
                        score += newColumn[pos + 1];
                        Animation fadeIn = AnimationUtils.loadAnimation(tiles[pos + 1][j].getContext(), R.anim.big_to_small);
                        tiles[pos + 1][j].startAnimation(fadeIn);
                    } else {
                        newColumn[pos--] = grid[i][j];
                    }
                }
            }
            for (int i = 0; i < 4; i++) {
                grid[i][j] = newColumn[i];
            }
        }
    }

    private void addRandomTile() {
        Random random = new Random();
        int row, col;
        do {
            row = random.nextInt(4);
            col = random.nextInt(4);
        } while (grid[row][col] != 0);

        grid[row][col] = random.nextInt(100) < 75 ? 2 : 4;
        Animation fadeIn = AnimationUtils.loadAnimation(tiles[row][col].getContext(), R.anim.fade_in);
        tiles[row][col].startAnimation(fadeIn);
    }

    private void updateUI() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                int value = grid[i][j];
                tiles[i][j].setText(value == 0 ? "" : String.valueOf(value));

                GradientDrawable background = (GradientDrawable) activity.getResources().getDrawable(R.drawable.rounded_tile, null);
                int colorResource = getColorForTile(value);
                background.setColor(activity.getResources().getColor(colorResource, null));
                tiles[i][j].setBackground(background);
            }
        }
        scoreView.setText("Score: " + score);
    }

    private int getColorForTile(int value) {
        switch (value) {
            case 2:
                return R.color.background_2;
            case 4:
                return R.color.background_4;
            case 8:
                return R.color.background_8;
            case 16:
                return R.color.background_16;
            case 32:
                return R.color.background_32;
            case 64:
                return R.color.background_64;
            case 128:
                return R.color.background_128;
            case 256:
                return R.color.background_256;
            case 512:
                return R.color.background_512;
            case 1024:
                return R.color.background_1024;
            case 2048:
                return R.color.background_2048;
            default:
                return R.color.background_empty;
        }
    }

    private boolean isMoveLegal(Direction direction) {
        switch (direction) {
            case RIGHT:
                return canMoveRight();
            case LEFT:
                return canMoveLeft();
            case UP:
                return canMoveUp();
            case DOWN:
                return canMoveDown();
            default:
                return false;
        }
    }

    private boolean canMoveRight() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (grid[i][j] != 0 && (grid[i][j] == grid[i][j + 1] || grid[i][j + 1] == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canMoveLeft() {
        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 4; j++) {
                if (grid[i][j] != 0 && (grid[i][j] == grid[i][j - 1] || grid[i][j - 1] == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canMoveUp() {
        for (int j = 0; j < 4; j++) {
            for (int i = 1; i < 4; i++) {
                if (grid[i][j] != 0 && (grid[i][j] == grid[i - 1][j] || grid[i - 1][j] == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canMoveDown() {
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 3; i++) {
                if (grid[i][j] != 0 && (grid[i][j] == grid[i + 1][j] || grid[i + 1][j] == 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean canMakeAnyMove() {
        return isMoveLegal(Direction.RIGHT) || isMoveLegal(Direction.LEFT) || isMoveLegal(Direction.UP) || isMoveLegal(Direction.DOWN);
    }

    private void showGameOverMessage() {
        // Creating an AlertDialog to show the game over message
        new androidx.appcompat.app.AlertDialog.Builder(activity)
                .setTitle("Game Over!")
                .setMessage("Final Score: " + score)
                .setCancelable(false)  // Prevent closing the dialog by tapping outside
                .setPositiveButton("Restart", (dialog, which) -> {
                    restartGame();  // Restart the game when the button is pressed
                })
                .setNegativeButton("Exit", (dialog, which) -> {
                    activity.finish();  // Close the activity if the user chooses to exit
                })
                .show();  // Display the dialog
    }


    public void restartGame() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                grid[i][j] = 0;
            }
        }
        score = 0;
        addRandomTile();
        updateUI();
    }

    public void saveGameState(Bundle outState) {
        int[] flattenedGrid = new int[16];
        for (int i = 0; i < 4; i++) {
            System.arraycopy(grid[i], 0, flattenedGrid, i * 4, 4);
        }
        outState.putIntArray("grid_array", flattenedGrid);
        outState.putInt("score_saved", score);
    }

    public void restoreGameState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int[] flattenedGrid = savedInstanceState.getIntArray("grid_array");
            if (flattenedGrid != null) {
                for (int i = 0; i < 4; i++) {
                    System.arraycopy(flattenedGrid, i * 4, grid[i], 0, 4);
                }
            }
            score = savedInstanceState.getInt("score_saved", 0);
        }
        updateUI();
    }

    public String getUserName() {
        SharedPreferences sharedPreferences = activity.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("USER_NAME", "Guest");  // Default to "Guest" if not found
    }

    public void undoLastMove() {
        // Revert to the previous state
        for (int i = 0; i < 4; i++) {
            System.arraycopy(previousGrid[i], 0, grid[i], 0, 4);
        }
        score = previousScore;
        updateUI();
    }
    private void saveCurrentState() {
        // Save the current grid and score before making any changes
        for (int i = 0; i < 4; i++) {
            System.arraycopy(grid[i], 0, previousGrid[i], 0, 4);
        }
        previousScore = score;
    }






}





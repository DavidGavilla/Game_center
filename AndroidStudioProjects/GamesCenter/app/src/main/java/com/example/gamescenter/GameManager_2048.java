package com.example.gamescenter;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

class GameManager_2048 {
    private final int[][] grid;
    private final TextView[][] tiles;
    private final TextView scoreView;
    private final AppCompatActivity activity;
    private int score;

    public GameManager_2048(TextView[][] tiles, TextView scoreView, AppCompatActivity activity) {
        this.tiles = tiles;
        this.scoreView = scoreView;
        this.activity = activity;
        this.grid = new int[4][4];
        restartGame();
    }

    public void onSwipe(Direction direction) {
        if (!isMoveLegal(direction)) {
            return;
        }

        moveTiles(direction);
        addRandomTile();
        updateUI();

        if (!canMakeAnyMove()) {
            showGameOverMessage();
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

    private void moveRight() {
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
        Toast.makeText(activity, "Game Over! Final score: " + score, Toast.LENGTH_LONG).show();
        restartGame();
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
}
package com.example.gamescenter;


import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import java.util.Random;
import java.util.Vector;

public class GameManager_SnakeGame extends View {

    // Fields

    private int[] screenSize; // [width, height]
    private boolean game_paused = false;
    private boolean gameStarted = false; // Flag to start the game
    private boolean isGameOver = false; // Flag to indicate game over state
    private Paint paint;
    private final float squareSize = 20; // Grid size and movement step
    private float speed = 20;
    private String currentDirection = "RIGHT"; // Snake's direction
    private int score = 0;
    private TextView scoreView;
    private Vector<Integer[]> SnakePosition = new Vector<>(); // Snake body segments
    private float foodX; // Food position
    private float foodY;
    private GameScoreDbHelper DataBase;
    private Context context;

    // Constructor
    public GameManager_SnakeGame(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        screenSize = new int[]{0, 0}; // Initialize screen size
    }

    // Game Lifecycle Methods
    @Override
    protected void onDraw(Canvas canvas) {
        if (!gameStarted || isGameOver) {

            displayGameOverOrPausedScreen(canvas);
            return;
        }

        if (game_paused) {
            displayPausedScreen(canvas);
            return;
        }
        moveSnake();
        super.onDraw(canvas);



        if (checkCollision() || checkSelfCollision()) {
            isGameOver = true;
            invalidate(); // Trigger one last draw to show "Game Over"
            DataBase = new GameScoreDbHelper(context);
            DataBase.insertScore(getUserName(),"Snake Game",score);
            return;
        }

        if (checkFoodCollision()) {

            handleFoodCollision();



        }

        drawSnake(canvas);
        drawFood(canvas);

        postInvalidateDelayed(200); // Slower frame updates for smoother gameplay
    }

    public void startGame() {
        gameStarted = true;
        isGameOver = false; // Reset the game-over flag
        postInvalidate();
    }

    public void pause_unpause() {
        game_paused = !game_paused;
        invalidate(); // Force a redraw to show the pause message
    }

    public void setScreenMargins(int topHeight, int bottomHeight) {
        screenSize[0] = getWidth();
        screenSize[1] = getHeight();

        Integer[] initialPosition = new Integer[]{screenSize[0] / 4, topHeight + 100};
        SnakePosition.add(initialPosition);

        generateFood();
    }

    public void getScoreView(TextView view) {
        scoreView = view;
    }

    // Movement Methods
    public void moveRight() {
        if (!currentDirection.equals("LEFT")) {
            currentDirection = "RIGHT";
        }
    }

    public void moveLeft() {
        if (!currentDirection.equals("RIGHT")) {
            currentDirection = "LEFT";
        }
    }

    public void moveUp() {
        if (!currentDirection.equals("DOWN")) {
            currentDirection = "UP";
        }
    }

    public void moveDown() {
        if (!currentDirection.equals("UP")) {
            currentDirection = "DOWN";
        }
    }

    private void moveSnake() {
        Integer[] head = SnakePosition.get(0);
        Integer[] newHead = new Integer[2];

        switch (currentDirection) {
            case "RIGHT":
                newHead[0] = head[0] + (int) speed;
                newHead[1] = head[1];
                break;
            case "LEFT":
                newHead[0] = head[0] - (int) speed;
                newHead[1] = head[1];
                break;
            case "UP":
                newHead[0] = head[0];
                newHead[1] = head[1] - (int) speed;
                break;
            case "DOWN":
                newHead[0] = head[0];
                newHead[1] = head[1] + (int) speed;
                break;
        }

        SnakePosition.add(0, newHead);
        SnakePosition.remove(SnakePosition.size() - 1);
    }

    private void growSnake() {
        if (SnakePosition.size() < 2) {
            Integer[] head = SnakePosition.get(0);
            SnakePosition.add(new Integer[]{head[0] - (int) squareSize, head[1]});
            return;
        }

        Integer[] lastSegment = SnakePosition.lastElement();
        Integer[] secondLastSegment = SnakePosition.get(SnakePosition.size() - 2);

        int offsetX = lastSegment[0] - secondLastSegment[0];
        int offsetY = lastSegment[1] - secondLastSegment[1];

        offsetX = offsetX != 0 ? (offsetX > 0 ? (int) squareSize : -(int) squareSize) : 0;
        offsetY = offsetY != 0 ? (offsetY > 0 ? (int) squareSize : -(int) squareSize) : 0;

        SnakePosition.add(new Integer[]{lastSegment[0] + offsetX, lastSegment[1] + offsetY});
    }

    // Collision Methods
    private boolean checkCollision() {
        Integer[] head = SnakePosition.get(0);
        return (head[0] < 0 || head[0] + squareSize > screenSize[0] ||
                head[1] < 0 || head[1] + squareSize > screenSize[1]);
    }

    private boolean checkSelfCollision() {
        Integer[] head = SnakePosition.get(0);
        for (int i = 1; i < SnakePosition.size(); i++) {
            Integer[] segment = SnakePosition.get(i);
            if (head[0].equals(segment[0]) && head[1].equals(segment[1])) {
                return true;
            }
        }
        return false;
    }

    private boolean checkFoodCollision() {
        Integer[] head = SnakePosition.get(0);
        return (head[0] < foodX + squareSize && head[0] + squareSize > foodX &&
                head[1] < foodY + squareSize && head[1] + squareSize > foodY);
    }

    // Food Methods
    private void generateFood() {
        Random random = new Random();
        int margin = (int) squareSize;
        boolean validPosition;

        do {
            foodX = margin + random.nextInt(screenSize[0] - margin * 2);
            foodY = margin + random.nextInt(screenSize[1] - margin * 2);

            foodX = (int) (foodX / squareSize) * squareSize;
            foodY = (int) (foodY / squareSize) * squareSize;

            validPosition = true;
            for (Integer[] position : SnakePosition) {
                if (foodX == position[0] && foodY == position[1]) {
                    validPosition = false;
                    break;
                }
            }
        } while (!validPosition);
    }

    private void handleFoodCollision() {
        score += 1;
        scoreView.setText("Score: " + score);
        growSnake();
        generateFood();
    }

    // Drawing Methods
    private void drawSnake(Canvas canvas) {
        paint.setColor(Color.GREEN);
        for (Integer[] position : SnakePosition) {
            canvas.drawRect(position[0], position[1], position[0] + squareSize, position[1] + squareSize, paint);
        }
    }

    private void drawFood(Canvas canvas) {
        paint.setColor(Color.RED);
        canvas.drawRect(foodX, foodY, foodX + squareSize, foodY + squareSize, paint);
    }

    private void displayGameOverOrPausedScreen(Canvas canvas) {
        paint.setTextSize(100);
        paint.setColor(isGameOver ? Color.RED : Color.YELLOW);
        String message = isGameOver ? "Game Over" : "Paused";
        canvas.drawText(message, screenSize[0] / 4f, screenSize[1] / 2f, paint);
    }

    private void displayPausedScreen(Canvas canvas) {
        paint.setColor(Color.YELLOW);
        paint.setTextSize(80);
        canvas.drawText("Paused", screenSize[0] / 3f, screenSize[1] / 2f, paint);
    }

    public String getUserName() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("USER_NAME", "Guest");  // Default to "Guest" if not found
    }




}

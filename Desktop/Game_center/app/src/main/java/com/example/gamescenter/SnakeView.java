package com.example.gamescenter; // Package where this class is located

import android.content.Context; // Import for accessing application context
import android.graphics.Bitmap; // Import for handling images
import android.graphics.BitmapFactory; // Import for decoding image resources
import android.graphics.Canvas; // Import for drawing on the view
import android.graphics.Color; // Import for setting colors
import android.graphics.Paint; // Import for drawing elements
import android.graphics.RectF; // Import for creating rectangular shapes
import android.graphics.Typeface; // Import for setting font styles
import android.util.AttributeSet; // Import for handling XML attributes
import android.view.View; // Import for creating custom views
import android.animation.ValueAnimator; // Import for animating values over time

// Custom view class for displaying a snake animation
public class SnakeView extends View {

    private Paint paint; // Paint object for drawing the snake
    private Paint textPaint; // Paint object for drawing text
    private RectF snakeHead; // Rectangle representing the snake's head
    private float snakeX = 0; // Initial X position of the snake
    private float snakeY; // Y position of the snake
    private float segmentWidth = 50;  // Width of each segment of the snake
    private int snakeLength = 5;  // Initial length of the snake
    private float[] snakePositionsX; // Array to store X positions of each snake segment
    private boolean isFinished = false; // Flag to track if animation is finished
    private Bitmap snakeImage;  // Bitmap for the snake image

    // Constructor for the custom view
    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Load the snake image from resources
        snakeImage = BitmapFactory.decodeResource(getResources(), R.drawable.snake_image); // Replace with actual image name

        // Initialize paint for drawing the snake
        paint = new Paint();
        paint.setColor(Color.GREEN); // Set snake color to green
        paint.setStyle(Paint.Style.FILL); // Set fill style for the paint

        // Initialize paint for text (Loading and Finished messages)
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE); // Set text color to white
        textPaint.setTextSize(80); // Set text size
        textPaint.setTypeface(Typeface.DEFAULT_BOLD); // Set bold font
        textPaint.setTextAlign(Paint.Align.CENTER); // Center-align the text

        // Initialize the array to store X positions of the snake segments
        snakePositionsX = new float[snakeLength];

        // Set initial positions for the snake segments (starting off-screen)
        for (int i = 0; i < snakeLength; i++) {
            snakePositionsX[i] = -segmentWidth * (snakeLength - i); // Moves the snake off-screen initially
        }

        // Start the snake animation
        startSnakeAnimation();
    }

    // Method to start the snake movement animation
    private void startSnakeAnimation() {
        // Create a ValueAnimator that animates from 0 to 1
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(5000); // Duration of 5 seconds for the animation

        // Listener to update the snake's position during the animation
        animator.addUpdateListener(animation -> {
            if (!isFinished) { // Only update if the animation is not finished
                float value = (float) animation.getAnimatedValue();

                // Move all segments of the snake
                for (int i = 0; i < snakeLength; i++) {
                    snakePositionsX[i] = value * getWidth() - segmentWidth * i; // Calculate new X positions
                }

                invalidate(); // Redraw the view to reflect changes
            }
        });

        animator.start(); // Start the animation
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Set the Y position for the snake, leaving a margin from the bottom
        snakeY = getHeight() - 150; // 150px margin from the bottom

        // Draw the "Loading" text if the animation is not finished
        if (!isFinished) {
            canvas.drawText("Loading", getWidth() / 2, snakeY - 100, textPaint);
        }

        // Draw the snake body segments
        for (int i = 0; i < snakeLength; i++) {
            // Create a rectangle representing the segment
            snakeHead = new RectF(snakePositionsX[i], snakeY, snakePositionsX[i] + segmentWidth, snakeY + segmentWidth);
            canvas.drawOval(snakeHead, paint); // Draw an oval for each segment
        }

        // Check if the snake has reached the end of the screen
        if (!isFinished && snakePositionsX[snakeLength - 1] >= getWidth()) {
            isFinished = true; // Mark the animation as finished
            canvas.drawText("Finished", getWidth() / 2, snakeY - 100, textPaint); // Display "Finished" text
        }

        // Calculate the center position for the image
        float imageX = (getWidth() - snakeImage.getWidth()) / 2;
        float imageY = (getHeight() - snakeImage.getHeight()) / 2;

        // Draw the snake image in the center of the canvas
        canvas.drawBitmap(snakeImage, imageX, imageY, null);
    }
}

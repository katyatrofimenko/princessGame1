package com.example.princessgame1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.button.MaterialButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int COLS = 3;
    private static final int ROWS = 8;
    private static final int DELAY = 1000;


    private GameManager gameManager;
    private ImageView[][] princeMatrix;
    private ImageView[] princessPlace;
    private AppCompatImageView[] game_IMG_hearts;
    private int[][] gameBoard;
    private int princessStart = 1;
    private final Handler gameHandler = new Handler();
    private MaterialButton leftArrow;
    private MaterialButton rightArrow;
    private boolean generatePrince = true;

    private final Runnable gameRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                if (checkCollision()) {
                    collision();
                }
                updateGameMatrix();
                updateUI();
                if (generatePrince) {
                    generatePrince();
                }
                generatePrince = !generatePrince;
            } catch (Exception e) {
                e.printStackTrace();
            }
            gameHandler.postDelayed(this, DELAY);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameManager = new GameManager(3);
        princeMatrix = new ImageView[ROWS][COLS];
        princessPlace = new ImageView[COLS];
        initializeViews();

        gameBoard = new int[ROWS][COLS];
        initializeGameMatrix();

        leftArrow = findViewById(R.id.left_arrow);
        rightArrow = findViewById(R.id.right_arrow);

        game_IMG_hearts = new AppCompatImageView[] {
                findViewById(R.id.heart1),
                findViewById(R.id.heart2),
                findViewById(R.id.heart3),
        };

        leftArrow.setOnClickListener(v -> moveLeft());
        rightArrow.setOnClickListener(v -> moveRight());

        startGame();
    }

    @Override
    protected void onStart() {
        super.onStart();
        startGame();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        startGame();
    }

    @Override
    protected void onStop() {
        super.onStop();
        gameHandler.removeCallbacksAndMessages(null);
    }

    private void initializeViews() {
        for (int i = 0; i < ROWS - 1; i++) {
            for (int j = 0; j < COLS; j++) {
                String princePlace = "prince_" + i +"_" + j;
                @SuppressLint("DiscouragedApi") int resId = getResources().getIdentifier(princePlace, "id", getPackageName());
                princeMatrix[i][j] = findViewById(resId);
            }
        }
        for (int i = 0; i < COLS; i++) {
            String princess = "princess_7_" + i;
            @SuppressLint("DiscouragedApi") int resId = getResources().getIdentifier(princess, "id", getPackageName());
            princessPlace[i] = findViewById(resId);
        }
    }

    private void initializeGameMatrix() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                gameBoard[i][j] = 0; // Initialize with 0
            }
        }
        gameBoard[ROWS - 1][princessStart] = 1;
    }

    private void generatePrince() {
        Random rand = new Random();
        int lane = rand.nextInt(COLS);
        gameBoard[0][lane] = 2;
        princeMatrix[0][lane].setVisibility(View.VISIBLE);
    }

    private void startGame() {
        gameHandler.removeCallbacks(gameRunnable);
        gameHandler.post(gameRunnable);
    }

    private void updateGameMatrix() {
        for (int row = ROWS - 1; row >= 0; row--) {
            for (int col = 0; col < COLS; col++) {
                if (gameBoard[row][col] == 2) {
                    if (row < ROWS - 2 && gameBoard[row + 1][col] == 0) {
                        gameBoard[row + 1][col] = 2;
                        gameBoard[row][col] = 0;
                        princeMatrix[row + 1][col].setVisibility(View.VISIBLE);
                        princeMatrix[row][col].setVisibility(View.INVISIBLE);
                    } else if (row >= ROWS - 2) {
                        gameBoard[row][col] = 0;
                        princeMatrix[row][col].setVisibility(View.INVISIBLE);
                    }
                }
            }
        }
    }

    private boolean checkCollision() {
        return gameBoard[ROWS - 2][princessStart] == 2;
    }

    private void collision() {
        Vibrator vibrationService = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrationService != null) {
            vibrationService.vibrate(500);
        }
        gameManager.decrementLives();
        updateLivesUI();

        if (gameManager.getLives() <= 0) {
            Toast.makeText(this, "Game Over, Restarting", Toast.LENGTH_SHORT).show();
            restartGame();
        }
    }

    private void updateLivesUI() {
//        for (int i = 1; i <= MAX_LIVES; i++) {
//            String resourceName = "heart" + i;
//            int resId = getResources().getIdentifier(resourceName, "id", getPackageName());
//            if (resId != 0) {
//                findViewById(resId).setVisibility(gameManager.getLives() >= i ? View.VISIBLE : View.GONE);
//            }
//        }

        int SZ = game_IMG_hearts.length;

        for (int i = 0; i < SZ; i++) {
            game_IMG_hearts[i].setImageResource(R.drawable.love);
        }

        for (int i = 0; i < SZ - gameManager.getLives(); i++) {
            game_IMG_hearts[SZ - i - 1].setImageResource(R.drawable.broken_heart);
        }
    }

    private void restartGame() {
        gameManager.resetLives();
        updateLivesUI();
        initializeGameMatrix();
    }

    private void updateUI() {
        for (int i = 0; i < ROWS - 1; i++) {
            for (int j = 0; j < COLS; j++) {
                if (gameBoard[i][j] == 2) {
                    princeMatrix[i][j].setImageResource(R.drawable.prince);
                } else {
                    princeMatrix[i][j].setImageResource(0);
                }
            }
        }

        for (int i = 0; i < COLS; i++) {
            if (i == princessStart) {
                princessPlace[i].setVisibility(View.VISIBLE);
            } else {
                princessPlace[i].setVisibility(View.INVISIBLE);
            }
        }
    }

    private void movePrincess(int direction) {
        int newLane = princessStart + direction;
        if (newLane >= 0 && newLane < COLS) {
            gameBoard[ROWS - 1][princessStart] = 0;
            princessStart = newLane;
            gameBoard[ROWS - 1][princessStart] = 1;
            updateUI();
        }
    }

    private void moveLeft() {
        movePrincess(-1);
    }

    private void moveRight() {
        movePrincess(1);
    }

}

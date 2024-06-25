package com.example.princessgame1;

public class GameManager {

    private int lives;

    public GameManager(int initialLives) {
        if (initialLives > 0 && initialLives <= 3) {
            lives = initialLives;
        } else {
            lives=3;
        }
    }

    public void decrementLives() {
        if (lives > 0) {
            lives--;
        }
    }
    public int getLives() {
        return lives;
    }

    public void resetLives() {
        lives = 3;
    }


}

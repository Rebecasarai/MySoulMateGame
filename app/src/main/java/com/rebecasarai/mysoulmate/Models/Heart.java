package com.rebecasarai.mysoulmate.Models;

/**
 * Created by rsgonzalez on 13/02/18.
 * Esta clase representa los corazones para los filtros.
 * Cada corazón tiene posiciones XY y Velocidades
 */

public class Heart {
    private int positionX, positionY,speedX,speedY;
    private boolean devueltaX,devueltaY;

    public Heart(int positionX, int positionY, int speedX, int speedY) {
        this.positionX = positionX;
        this.positionY = positionY;
        this.speedX = speedX;
        this.speedY = speedY;
        devueltaX=false;
        devueltaY = false;
    }

    public boolean isDevueltaX() {
        return devueltaX;
    }

    public void setDevueltaX(boolean devueltaX) {
        this.devueltaX = devueltaX;
    }

    public boolean isDevueltaY() {
        return devueltaY;
    }

    public void setDevueltaY(boolean devueltaY) {
        this.devueltaY = devueltaY;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public int getSpeedX() {
        return speedX;
    }

    public void setSpeedX(int speedX) {
        this.speedX = speedX;
    }

    public int getSpeedY() {
        return speedY;
    }

    public void setSpeedY(int speedY) {
        this.speedY = speedY;
    }
}

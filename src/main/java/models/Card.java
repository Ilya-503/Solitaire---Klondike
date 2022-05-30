package models;

import javax.imageio.*;
import java.awt.*;
import java.io.*;

public class Card {

    private static Image backImg;

    private Image frontImg;
    private int x, y;
    private final int suit;
    private final int cardType;
    private boolean chosen;
    private final boolean redSuit;
    private boolean turnedOver;


    public Card(String path, int num) {
        try {
            frontImg = ImageIO.read(new File(path));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        x = 30;
        y = 15;
        turnedOver = true;
        suit = (num - 1) % 4;   // крести - пики - черви - бубны
        cardType = (num - 1) / 4;
        redSuit = suit > 1;    // 1,2 - черные, 3,4 - красные, 5,6 - черные ...
    }

    public static Image getBackImg() {
        return backImg;
    }

    public static void setBackImg(String path) {
        try {
            backImg = ImageIO.read(new File(path + "k0.png"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Image getFrontImg() {
        return frontImg;
    }

    public int getType() {
        return cardType;
    }

    public int getSuit() {
        return suit;
    }

    public boolean isRed() {
        return redSuit;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isTurnedOver() {
        return turnedOver;
    }

    public void setTurnedOver(boolean turnedOver) {
        this.turnedOver = turnedOver;
    }

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

}
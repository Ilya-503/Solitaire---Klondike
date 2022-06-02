package models;

import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.imageio.*;
import java.awt.*;
import java.io.*;

public class Card {

    private static Image backImg;
    private final Image frontImg;
    private final boolean redSuit;
    private final int suit;
    private final int type;
    private int x, y;
    private boolean chosen;
    private boolean turnedOver;

    public Card(String path, int num) throws Exception {
        frontImg = ImageIO.read(new File(path));
        x = 30;
        y = 15;
        turnedOver = true;
        suit = (num - 1) % 4;   // крести - пики - черви - бубны
        type = (num - 1) / 4;
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

    public boolean isRed() {
        return redSuit;
    }

    public int getSuit() {
        return suit;
    }

    public int getType() {
        return type;
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

    public boolean isChosen() {
        return chosen;
    }

    public void setChosen(boolean chosen) {
        this.chosen = chosen;
    }

    public boolean isTurnedOver() {
        return turnedOver;
    }

    public void setTurnedOver(boolean turnedOver) {
        this.turnedOver = turnedOver;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(redSuit)
                .append(suit)
                .append(type)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj instanceof Card) {
            Card card = (Card) obj;
            return card.frontImg == frontImg && card.redSuit == redSuit
                    && card.suit == suit && card.type == type
                    && card.chosen == chosen && card.turnedOver == turnedOver;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Suit: " + suit + ", type: " + type
                + ", is" + (chosen ? " " : "n't ") + "chosen"
                + ", is" + (isTurnedOver() ? " " : "n't ") + "turned over";
    }
}
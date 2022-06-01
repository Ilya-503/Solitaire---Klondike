package view;

import models.Card;
import models.Constants;
import models.Game;

import java.awt.*;
import java.util.ArrayList;

public class GameInterface {

    private final Game game;

    public GameInterface(Game game) {
        this.game = game;
        game.start();
            }

    public void restartGame() {
        game.start();
    }

    public void drawStacks(Graphics gr) {
        drawUpperStacks(gr);
        drawLowerStacks(gr);
        drawChosenStack(gr);
    }

    private void drawUpperStacks(Graphics gr) {
        drawDeck(gr);
        for (int i = 1; i < 6; i++) {
            ArrayList<Card> stack = game.getCardStack(i);
            int size = stack.size();
            if (size > 1) {
                drawCard(stack.get(size - 2), gr);
                drawCard(stack.get(size - 1), gr);
            } else if (size == 1) {
                drawCard(stack.get(0), gr);
            }
        }
    }

    private void drawDeck(Graphics gr) {
        ArrayList<Card> deck = game.getCardStack(0);
        int size = deck.size();
        if (size > 0) {
            drawCard(deck.get(size - 1), gr);
        }
    }

    private void drawLowerStacks(Graphics gr) {
        for (int i = 6; i < 13; i++) {
            ArrayList<Card> stack = game.getCardStack(i);
            if (stack != null) {
                for (Card card: stack) {
                    if (card.isChosen()) {
                        break;
                    }
                    drawCard(card, gr);
                }
            }
        }
    }

    private void drawChosenStack(Graphics gr) {
        int chosenStackNum = game.getChosenStackNum();
        if (chosenStackNum != -1) {
            ArrayList<Card> stack = game.getCardStack(chosenStackNum);
            for (int i = game.getChosenCardNum(); i < stack.size(); i++) {
                drawCard(stack.get(i), gr);
            }
        }
    }

    private void drawCard(Card card, Graphics gr) {
        if (card.isTurnedOver()) {
            gr.drawImage(Card.getBackImg(), card.getX() , card.getY(),
                    Constants.cardWidth, Constants.cardHeight, null);
        } else {
            gr.drawImage(card.getFrontImg(), card.getX(), card.getY(),
                    Constants.cardWidth, Constants.cardHeight, null);
        }
        if (card.isChosen()) {
            gr.setColor(Color.YELLOW);
            gr.drawRect(card.getX(), card.getY(), Constants.cardWidth, Constants.cardHeight);
        }
    }
}

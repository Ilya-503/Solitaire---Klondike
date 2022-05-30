package view;

import models.Card;
import models.Constants;
import models.Game;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameInterface {

    private final Timer tmEndGame;
    private final Game game;
    private boolean gameEnded;

    public GameInterface(Game game) {
        this.game = game;
        game.start();


        tmEndGame = new Timer(100, arg0 -> {
            gameEnded = game.isEndGame();
            if (gameEnded) {
            for (int i = 2; i <= 5; i++) {
                ArrayList<Card> cardStack = game.getCardStack(i);
                Card card = cardStack.get(0);
                cardStack.add(card);
                cardStack.remove(0);
            }
            }
        });
        tmEndGame.start();
    }

    public void restartGame() {
        game.start();
    }

    public void drawStack(Graphics gr) {
        ArrayList<Card> firstCardStack = game.getCardStack(0),
                       secondCardStack = game.getCardStack(1);
        int secondCSSize = secondCardStack.size();

        if (firstCardStack.size() > 0) {    // ВЕРХНЯЯ ЛЕВАЯ СТОПКА - если в стопке есть карты
            drawCard(firstCardStack.get(firstCardStack.size() - 1), gr);   // Получаем и рисуем самую верхнюю карту
        }
        if (secondCSSize > 1) {   // ВТОРАЯ СЛЕВА ВЕРХНЯЯ СТОПКА - если в стопке более одной карты
            drawCard(secondCardStack.get(secondCSSize - 2), gr);   // Получаем и рисуем вторую сверху карту
            drawCard(secondCardStack.get(secondCSSize - 1), gr);            // Получаем и рисуем самую верхнюю карту
        } else if (secondCSSize == 1) {        // если в стопке одна карта
            drawCard(secondCardStack.get(0), gr);         // Получаем и рисуем самую верхнюю карту
        }

        for (int i = 2; i <= 5; i++) {  // ЧЕТЫРЕ ДОМАШНИЕ СТОПКИ
            ArrayList<Card> cardStack = game.getCardStack(i);
            int size = cardStack.size();
            if (size > 1) { // Если в стопке более одной карты
                drawCard(cardStack.get(size - 2), gr); // Получаем и рисуем вторую сверху карту
                drawCard(cardStack.get(size - 1), gr); // Получаем и рисуем самую верхнюю карту
            } else if (size == 1) {        // если в стопке одна карта
                drawCard(cardStack.get(0), gr);// Получаем и рисуем самую верхнюю карту
            }
        }

        for (int i = 6; i < 13; i++) {  // НИЖНИЕ СЕМЬ СТОПОК
            ArrayList<Card> cardStack = game.getCardStack(i);
            if (cardStack != null) {  // Если в стопке есть карты
                for (Card card: cardStack) {  // Перебираем все карты из стопки
                    if (card.isChosen()) { // находим выбранную карту - прерываем цикл
                        break;
                    }
                    drawCard(card, gr); // Рисуем карты
                }
            }
        }

        int chosenStackNum = game.getChosenStackNum();
        if (chosenStackNum != -1) { // ПЕРЕНОСИМЫЕ МЫШЬЮ КАРТЫ - если имеется выбранная стопка
            for (int i = game.getChosenCardNum(); i < game.getCardStack(chosenStackNum).size(); i++) {
                drawCard(game.getCardStack(chosenStackNum).get(i), gr);
            }
        }
    }

    public void drawCard(Card card, Graphics gr) {
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

package models;

import java.util.Stack;

public class Game {

    private final Stack<Card>[] cardStacks;
    private boolean firstDeckDistribution;
    private int chosenStackNum;
    private int chosenCardNum;
    private boolean endGame;

    public Game() {
        cardStacks = new Stack[13];
        for (int i = 0; i < 13; i++) {
            cardStacks[i] = new Stack<>();
        }
        start();
    }

    public void start() {
        for (int i = 0; i < 13; i++) {
            cardStacks[i].clear();
        }
        loadDeck();
        distribution();
        endGame = false;
        firstDeckDistribution = true;
        chosenCardNum = -1;
        chosenStackNum = -1;
    }

    private void loadDeck() {
        for (int i = 1; i <= 52; i++) {
            cardStacks[0].push(new Card(Constants.path + "k" + (i) + ".png", i));
        }
            Card.setBackImg(Constants.path);
    }

    private void distribution() {
        int x = 30;
        for (int i = 6; i < 13; i++) {
            for (int j = 6; j <= i; j++) {
                int rnd = (int)(Math.random() * cardStacks[0].size());
                Card card = cardStacks[0].get(rnd);
                card.setTurnedOver(j < i);
                card.setX(x);
                card.setY(130 + cardStacks[i].size() * 20); // Каждую следующую карту располагаем ниже на 20 пикселей
                cardStacks[i].push(card);
                cardStacks[0].remove(rnd);
            }
            x += Constants.betweenCardStacks;
        }
    }

    public void openTopCard(int num) {
            if (num > 0 && cardStacks[num].size() > 0) {
                int lastCardNum = cardStacks[num].size() - 1;
                Card card = cardStacks[num].get(lastCardNum);
                if (card.isTurnedOver()) {
                    card.setTurnedOver(false);
            }
        }
    }

    public boolean checkCardTransfer(int fromStackNum, int toStackNum) {
        boolean isPossible = false;
        Card transferringCard = cardStacks[fromStackNum].get(chosenCardNum);
        int transferCardType = transferringCard.getType();
        int transferCardSuit = transferringCard.getSuit();
        Card toStackTopCard = null;
        if (toStackNum != -1 && cardStacks[toStackNum].size() > 0) {
            toStackTopCard = cardStacks[toStackNum].get(cardStacks[toStackNum].size() - 1);
        }
        if ((toStackNum >= 2) && (toStackNum <= 5)) {  // Если четыре домашние стопки
            if (chosenCardNum ==  (cardStacks[fromStackNum].size() - 1)) {
                if (toStackTopCard == null) {      // Если стопка была пустая
                    if (transferringCard.getType() == 12) {  // Если переносимая карта ТУЗ
                        isPossible = true;
                    }
                }
                else if ((toStackTopCard.getType() == 12) &&            // Если в домашней стопке ТУЗ, переносится
                        (transferringCard.getSuit() == toStackTopCard.getSuit()) && // ДВОЙКА и масти совпадают
                        (transferringCard.getType() == 0)) {
                    isPossible = true;
                }
                else if ((toStackTopCard.getType() >= 0) && // Если в домашней стопке не ТУЗ, но масти совпадают
                        (toStackTopCard.getType() < 11) &&
                        (transferringCard.getSuit() == toStackTopCard.getSuit())) {
                    if ((toStackTopCard.getType() + 1 == transferringCard.getType())) { // Если перенос. к. выше на 1
                        isPossible = true;
                    }
                }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////
                if (isPossible) {   // Если результат проверки положительный
                    transferringCard.setX((Constants.betweenCardStacks * (toStackNum + 1)) + 30); // Переносим карту в домашнюю стопку
                    transferringCard.setY(15);
                    cardStacks[toStackNum].add(transferringCard);
                    cardStacks[fromStackNum].remove(chosenCardNum);
                    checkEndGame();
                }
            }
        }
        if ((toStackNum >= 6) && (toStackNum <= 12)) {    // Если перенос в нижние стопки
            int x = 30 + (toStackNum - 6) * Constants.betweenCardStacks;
            int y = 130;
            if (toStackTopCard == null) {  // Если нижняя стопка была пустая
                if (transferringCard.getType() == 11) {   // Если переносится КОРОЛЬ
                    isPossible = true;
                }
            }
            else {   // Если была НЕ пустая
                if (!toStackTopCard.isTurnedOver()) {     // Если верхняя карта открыта
                    if (toStackTopCard.getType() != 12) {  // Если переносим НЕ на ТУЗА
                        if ((toStackTopCard.getType() == transferringCard.getType() + 1) ||   // Если переносимая карта
                                ((toStackTopCard.getType() == 0) &&                        // на один младше или
                                        (transferringCard.getType() == 12))) {          // ТУЗ переносится на двойку
                            if (toStackTopCard.isRed() != transferringCard.isRed()) {  // Если одна масть ЧЕРНАЯ, а другая КРАСНАЯ
                                y = toStackTopCard.getY() + 20;
                                isPossible = true;
                            }
                        }
                    }
                }
            }
            if (isPossible) {
                for (int i = chosenCardNum; i < cardStacks[fromStackNum].size(); i++) {
                    Card card_ = cardStacks[fromStackNum].get(i); // Добавляем все карты в новую стопу
                    card_.setX(x);
                    card_.setY(y);
                    cardStacks[toStackNum].add(card_);
                    y += 20;
                }
                for (int i = cardStacks[fromStackNum].size() - 1; i >= chosenCardNum; i--) {
                    cardStacks[fromStackNum].remove(i);   // Удалаяем все карты из старой стопки
                }
            }
        }
        return isPossible;
    }

    public void getCardFromDeck() {
        int num;
        Card card;
        if (cardStacks[0].size() > 0) {
            if (firstDeckDistribution) {
                num = (int)(Math.random() * cardStacks[0].size());
                card = cardStacks[0].get(num);
            } else {
                card = cardStacks[0].pop();
            }
            card.setTurnedOver(false);
            card.setX(card.getX() + Constants.betweenCardStacks);
            cardStacks[1].push(card);
        } else {
            for (int i = 0; i < cardStacks[1].size(); i++) {
                card = cardStacks[1].pop();
                card.setTurnedOver(true);
                card.setX(card.getX() - Constants.betweenCardStacks);
                cardStacks[0].push(card);
            }
            firstDeckDistribution = false;
        }
    }

    public void checkEndGame() {
        for (int i = 2; i <= 5; i++) {
            endGame = cardStacks[i].size() == 13;
        }
    }

    //region Геттеры и сеттеры

    public Stack<Card> getCardStack(int i) {
        return cardStacks[i];
    }

    public boolean isEndGame() {
        return endGame;
    }

    public int getChosenStackNum() {
        return chosenStackNum;
    }

    public void setChosenStackNum(int stackNum) {
        chosenStackNum = stackNum;
    }

    public int getChosenCardNum() {
        return chosenCardNum;
    }

    public void setChosenCardNum(int cardNum) {
        chosenCardNum = cardNum;
    }

    //endregion

}



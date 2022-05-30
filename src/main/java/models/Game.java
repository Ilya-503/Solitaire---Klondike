package models;

import java.util.ArrayList;

public class Game {

    private final  ArrayList<Card>[] cardStacks;
    private boolean firstDeckDistribution;     // Признак первой выдачи карт из верхней левой стопки
    private int chosenStackNum;
    private int chosenCardNum;
    private boolean endGame;

    public Game() {
        cardStacks = new ArrayList[13];
        for (int i = 0; i < 13; i++) {
            cardStacks[i] = new ArrayList<>();
        }
        start();
    }

    // region Старт игры

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
            cardStacks[0].add(new Card(Constants.path + "k" + (i) + ".png", i));
        }
        try {
            Card.setBackImg(Constants.path);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void distribution() {
        int x = 30;
        for (int i = 6; i < 13; i++) {  // Перебираем все стопки нижние семь стопок
            for (int j = 6; j <= i; j++) {  // Добавление карт в стопку
                int rnd = (int)(Math.random() * cardStacks[0].size()); // номер случайной карты из верхней левой стопки
                Card card = cardStacks[0].get(rnd);
                card.setTurnedOver(j < i);  // Если карта не самая верхняя,то показываем ее рубашкой
                card.setX(x);
                card.setY(130 + cardStacks[i].size() * 20);  // Каждую следующую карту располагаем ниже на 20 пикселей
                cardStacks[i].add(card);  // Добавляем карту в нижнюю стопку
                cardStacks[0].remove(rnd); // Удаляем карту из верхней левой стопки
            }
            x += Constants.betweenCardStacks; //смещаемся правее
        }
    }

    // endregion


    public void openTopCard() {
        for (int i = 6; i <= 12; i++) {
            if (cardStacks[i].size() > 0) {
                int lastCardNum = cardStacks[i].size() - 1;
                Card card = cardStacks[i].get(lastCardNum);
                if (card.isTurnedOver()) {
                    card.setTurnedOver(false);
                }
            }
        }
    }

    public boolean checkCardTransfer(int fromStackNum, int toStackNum) {
        boolean isPossible = false;
        Card transferringCard = cardStacks[fromStackNum].get(chosenCardNum); // Карта, которая переносится
        Card toStackTopCard = null;
        if (toStackNum != -1 && cardStacks[toStackNum].size() > 0) {  // Если есть карты в стопке
            toStackTopCard = cardStacks[toStackNum].get(cardStacks[toStackNum].size() - 1); // Получаем верхнюю карту
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
        if (cardStacks[0].size() > 0) {
            int num;
            if (firstDeckDistribution) {
                num = (int)(Math.random() * cardStacks[0].size());
            } else {
                num = cardStacks[0].size() - 1;
            }
            Card card = cardStacks[0].get(num);   // Получаем карту из стопки с номером 0
            card.setTurnedOver(false);
            card.setX(card.getX() + Constants.betweenCardStacks);  //сдвигаем в стопку правее
            cardStacks[1].add(card);
            cardStacks[0].remove(num);
        } else {        // Если карт уже нет
            int lastCardNum = cardStacks[1].size() - 1; // Вычисляем номер последней карты в стопке номером 1
            for (int i = lastCardNum; i >= 0; i--) {  // перенос карт из стопки 1 в колоду
                Card card = cardStacks[1].get(i);
                card.setTurnedOver(true);
                card.setX(card.getX() - Constants.betweenCardStacks);   // сдвигаем левее
                cardStacks[0].add(card);
            }
            cardStacks[1].clear();
            firstDeckDistribution = false;
        }
    }

    public void checkEndGame() {
        if ((cardStacks[2].size() == 13) &&
                (cardStacks[3].size() == 13) &&
                (cardStacks[4].size() == 13) &&
                (cardStacks[5].size() == 13)) {
            endGame = true;
        }
    }

    //region Геттеры и сеттеры

    public ArrayList<Card> getCardStack(int i) {
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



package models;

import java.util.ArrayList;

public class Game {

    private final ArrayList<Card>[] cardStacks;
    private boolean firstDeckDistribution;
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
                cardStacks[i].add(card);
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
        int toCardType = 100, toCardSuit = 100;

        if (toStackNum != -1 && cardStacks[toStackNum].size() > 0) {
            toStackTopCard = cardStacks[toStackNum].get(cardStacks[toStackNum].size() - 1);
            toCardType = toStackTopCard.getType();
            toCardSuit = toStackTopCard.getSuit();
        }

        if (toStackNum >= 2 && toStackNum <= 5 &&
                chosenCardNum == (cardStacks[fromStackNum].size() - 1)) {  // перенос top карты в дом. стопку
            boolean puttingAce = toStackTopCard == null && transferCardType == 12;
            boolean puttingTwoOnAce = toCardType == 12 && transferCardType == 0;
            boolean puttingOther = toCardType <= 11 && toCardType + 1 == transferCardType;
            isPossible = puttingAce || (puttingTwoOnAce || puttingOther) && toCardSuit == transferCardSuit;

            if (isPossible) { /** мб на один уровень глубже ??? **/
                transferCardToHome(transferringCard, toStackNum, fromStackNum);
            }
        }

        if ((toStackNum >= 6) && (toStackNum <= 12)) {
            boolean puttingKing = toStackTopCard == null && transferringCard.getType() == 11;
            boolean puttingAceOnTwo = transferCardType == 12 && toCardType == 0;
            boolean puttingOther = toCardType <= 11 && toCardType == transferCardType + 1;

            isPossible = puttingKing || (puttingAceOnTwo || puttingOther) &&
                    toStackTopCard.isRed() != transferringCard.isRed();     /** не добавил !toStackTopCard.isTurnedOver()**/

            if (isPossible) {
                transferCardsToLowerStack(toStackTopCard, toStackNum, fromStackNum);
            }
        }
        return isPossible;
    }

    private void transferCardToHome(Card transferringCard, int toStackNum, int fromStackNum) {
        transferringCard.setX((Constants.betweenCardStacks * (toStackNum + 1)) + 30); // Переносим карту в домашнюю стопку
        transferringCard.setY(15);
        cardStacks[toStackNum].add(transferringCard);
        cardStacks[fromStackNum].remove(chosenCardNum);
        checkEndGame();
    }

    private void transferCardsToLowerStack(Card toStackTopCard, int toStackNum, int fromStackNum) {
        int y = toStackTopCard == null ? 130 : toStackTopCard.getY() + 20;
        int x = 30 + (toStackNum - 6) * Constants.betweenCardStacks;
        for (int i = chosenCardNum; i < cardStacks[fromStackNum].size(); i++) {
            Card card_ = cardStacks[fromStackNum].get(i); // Добавляем все карты в новую стопку
            card_.setX(x);
            card_.setY(y);
            cardStacks[toStackNum].add(card_);
            y += 20;
        }
        for (int i = cardStacks[fromStackNum].size() - 1; i >= chosenCardNum; i--) {
            cardStacks[fromStackNum].remove(i);   // Удалаяем все карты из старой стопки
        }

    }

    public void getCardFromDeck() {
        int num;
        if (cardStacks[0].size() > 0) {
            num = firstDeckDistribution ?
                    (int)(Math.random() * cardStacks[0].size()) :
                    cardStacks[0].size() - 1;
            Card card = cardStacks[0].get(num);
            card.setTurnedOver(false);
            card.setX(card.getX() + Constants.betweenCardStacks);
            cardStacks[1].add(card);
            cardStacks[0].remove(num);
        } else {
            num = cardStacks[1].size() - 1;
            for (int i = num; i >= 0; i--) {
                Card card = cardStacks[1].get(i);
                card.setTurnedOver(true);
                card.setX(card.getX() - Constants.betweenCardStacks);
                cardStacks[0].add(card);
            }
            cardStacks[1].clear();
            firstDeckDistribution = false;
        }
    }


    public void checkEndGame() {
        for (int i = 2; i <= 5; i++) {
            endGame = cardStacks[i].size() == 13;
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



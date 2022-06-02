package models;

import java.util.ArrayList;
import javax.swing.Timer;

public class Game {

    private final ArrayList<Card>[] cardStacks;
    private boolean firstDeckDistribution;
    private int chosenStackNum;
    private int chosenCardNum;
    private boolean endGame;
    private final Timer tmEndGame;

    public Game() {
        cardStacks = new ArrayList[13];
        for (int i = 0; i < 13; i++) {
            cardStacks[i] = new ArrayList<>();
        }

        tmEndGame = new Timer(100, arg0 -> {
                for (int i = 2; i <= 5; i++) {
                    Card card = cardStacks[i].get(0);
                    cardStacks[i].add(card);
                    cardStacks[i].remove(0);
                }
        });

        start();
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

    //region Старт игры

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
        try {
            for (int i = 1; i <= 52; i++) {
                cardStacks[0].add(new Card(Constants.path + "k" + (i) + ".png", i));
            }
            Card.setBackImg(Constants.path);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    //endregion

    // region Проверка переноса карт и сам перенос

    public boolean checkCardTransfer(int fromStackNum, int toStackNum) {
        boolean isPossible = false;
        int fromStackSize = cardStacks[fromStackNum].size();
        Card transferringCard = chosenCardNum >= 0 && fromStackSize > 0 ?
                cardStacks[fromStackNum].get(chosenCardNum) : null;

        if (toStackNum != -1) {
            ArrayList<Card> toStack = cardStacks[toStackNum];
            if (toStackNum >= 2 && toStackNum <= 5 && chosenCardNum == (fromStackSize - 1)) {
                isPossible = checkTransferToHome(toStack, transferringCard);
                if (isPossible) {
                    transferCardToHome(transferringCard, toStackNum, fromStackNum);
                }
            } else if ((toStackNum >= 6) && (toStackNum <= 12)) {
                isPossible = checkTransferToLowerStack(toStack, transferringCard);
                if (isPossible) {
                    transferCardsToLowerStack(toStack, toStackNum, fromStackNum);
                }
            }
        }
        return isPossible;
    }

    public boolean checkTransferToHome(ArrayList<Card> toStack, Card transferringCard) {
        int toStackSize = toStack.size(), toCardType = 100, toCardSuit = 100;
        if (toStackSize > 0) {
            Card toStackTopCard = toStack.get(toStackSize - 1);
            toCardType = toStackTopCard.getType();
            toCardSuit = toStackTopCard.getSuit();
        }
        int transferCardType = transferringCard.getType(), transferCardSuit = transferringCard.getSuit();
        boolean puttingAce = toStackSize == 0 && transferCardType == 12;
        boolean puttingTwoOnAce = toCardType == 12 && transferCardType == 0;
        boolean puttingOther = toCardType < 11 && toCardType + 1 == transferCardType;

        return puttingAce || (puttingTwoOnAce || puttingOther) && toCardSuit == transferCardSuit;
    }

    private boolean checkTransferToLowerStack(ArrayList<Card> toStack, Card transferringCard) {
        int toStackSize = toStack.size(), toCardType = 100;
        boolean toCardIsRed = false;
        if (toStackSize > 0) {
            Card toStackTopCard = toStack.get(toStackSize - 1);
            toCardType = toStackTopCard.getType();
            toCardIsRed = toStackTopCard.isRed();
        }
        int transferCardType = transferringCard.getType();
        boolean puttingKing = toStackSize == 0 && transferCardType == 11;
        boolean puttingAceOnTwo = transferCardType == 12 && toCardType == 0;
        boolean puttingOther = toCardType <= 11 && toCardType == transferCardType + 1;

        return puttingKing || (puttingAceOnTwo || puttingOther) &&
                toCardIsRed != transferringCard.isRed();  /** !toStackTopCard.isTurnedOver() **/
    }

    public void transferCardToHome(Card transferringCard, int toStackNum, int fromStackNum) {
        transferringCard.setX((Constants.betweenCardStacks * (toStackNum + 1)) + 30); // в домашнюю стопку
        transferringCard.setY(15);
        cardStacks[toStackNum].add(transferringCard);
        cardStacks[fromStackNum].remove(chosenCardNum);
        checkEndGame();
    }

    private void transferCardsToLowerStack(ArrayList<Card> toStack, int toStackNum, int fromStackNum) {
        int y = toStack.size() == 0 ? 130 : toStack.get(toStack.size() - 1).getY() + 20;
        int x = 30 + (toStackNum - 6) * Constants.betweenCardStacks;
        for (int i = chosenCardNum; i < cardStacks[fromStackNum].size(); i++) {
            Card card_ = cardStacks[fromStackNum].get(i); // все карты в новую стопку
            card_.setX(x);
            card_.setY(y);
            cardStacks[toStackNum].add(card_);
            y += 20;
        }
        for (int i = cardStacks[fromStackNum].size() - 1; i >= chosenCardNum; i--) {
            cardStacks[fromStackNum].remove(i);   // Удалаяем все карты из старой стопки
        }
    }

    // endregion

    public void openTopCard(int num) {
            if (num > 0 && cardStacks[num].size() > 0) {
                int lastCardNum = cardStacks[num].size() - 1;
                Card card = cardStacks[num].get(lastCardNum);
                if (card.isTurnedOver()) {
                    card.setTurnedOver(false);
            }
        }
    }

    public void putCardFromDeck() {
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
        if ((cardStacks[2].size() == 13) &&
                (cardStacks[3].size() == 13) &&
                (cardStacks[4].size() == 13) &&
                (cardStacks[5].size() == 13)) {
            endGame = true;
            tmEndGame.start();
        }
    }
}



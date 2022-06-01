package controllers;

import models.Card;
import models.Constants;
import models.Game;

import java.awt.event.*;
import java.util.ArrayList;


public class MouseListener {

    private final Game game;
    private int dx, dy;           // Смещения координат курсора мыши относительно координат карты
    private int oldX ,oldY;       // Координаты карты до начала переноса мышью

    public MouseListener(Game game) {
        this.game = game;
    }

    public java.awt.event.MouseListener getMouseListener() {
        return new myMouseListener();
    }

    public MouseMotionListener getMouseActionListener() {
        return new myMouseActionListener();
    }


    class myMouseListener extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            if (!game.isEndGame()) {
                int mX = e.getX();
                int mY = e.getY();
                if (e.getButton() == 1) {
                    if (e.getClickCount() == 1) {
                        mousePressed(mX, mY);
                    } else if (e.getClickCount() == 2) {
                        mouseDoublePressed(mX, mY);
                    }
                }
            }
        }

        private void mousePressed(int mX, int mY) {
            int num = getPressedStackNum(mX, mY);
            setChosenCard(num, mX, mY);
        }

        private int getPressedStackNum(int mX, int mY) {    // Определение стопки на которую нажали мышью
            int num = -1;  // Если стопка не выбрана
            if ((mY >= 15) && (mY <= (15 + Constants.cardHeight))) {  // Если курсор находится в зоне верхних стопок
                if ((mX >= 30) && (mX <= (30 + Constants.cardWidth))) num = 0;
                if ((mX >= 140) && (mX <= (140 + Constants.cardWidth))) num = 1;
                if ((mX >= 360) && (mX <= (360 + Constants.cardWidth))) num = 2;
                if ((mX >= 470) && (mX <= (470 + Constants.cardWidth))) num = 3;
                if ((mX >= 580) && (mX <= (580 + Constants.cardWidth))) num = 4;
                if ((mX >= 690) && (mX <= (690 + Constants.cardWidth))) num = 5;
            }
            else if ((mY >= 130) && (mY <= 700)) {   // Если курсор находится в зоне нижних стопок
                if ((mX >= 30) && (mX <= Constants.betweenCardStacks * 7)) {
                    if (((mX - 30) % Constants.betweenCardStacks) <= Constants.cardWidth) {
                        num = (mX - 30) / Constants.betweenCardStacks;
                        num += 6;
                    }
                }
            }
            return num;
        }

        private void setChosenCard(int cardStackNum, int mX, int mY) {
            if (cardStackNum > 0) {
                ArrayList<Card> cardStack = game.getCardStack(cardStackNum);
                int stackSize = cardStack.size();
                if (stackSize > 0) {
                    int lastCardNum = stackSize - 1;
                    Card card = cardStack.get(lastCardNum);

                if ((cardStackNum >= 1) && (cardStackNum <= 5)) {
                    setCard(card, lastCardNum, cardStackNum, mX, mY);

                } else if ((cardStackNum >= 6) && (cardStackNum <= 12)) {

                    // Если выбрана верхня карта - присваиваем ее номер, иначе - находим координаты выбран.
                    // карты и проверяем, не лежит ли она рубашкой.
                    int chosenNum = mY >= card.getY() && mY <= (card.getY() + Constants.cardHeight) ? lastCardNum :
                            mY < card.getY() && game.getCardStack(cardStackNum).get((mY - 130) / 20).isTurnedOver() ?
                                    (mY - 130) / 20 : -1;

                        if (chosenNum != -1) {
                            Card chosenCard = game.getCardStack(cardStackNum).get(chosenNum);
                            if (!chosenCard.isTurnedOver()) {
                                setCard(chosenCard, chosenNum, cardStackNum, mX, mY);
                            }
                        }
                    }
                }
            }
        }

        private void setCard(Card card, int cardNum, int stackNum, int mX, int mY) {
            card.setChosen(true);
            game.setChosenCardNum(cardNum);
            game.setChosenStackNum(stackNum);
            dx = mX - card.getX();
            dy = mY - card.getY();
            oldX = card.getX();
            oldY = card.getY();
        }

        private void mouseDoublePressed(int mX, int mY) {
            int num = getPressedStackNum(mX, mY);
            if ((num == 1) || ((num >= 6) && (num <= 12))) {
                if (game.getCardStack(num).size() > 0) {
                    int topCardNum = game.getCardStack(num).size() - 1;
                    Card topCard = game.getCardStack(num).get(topCardNum);
                    if ((mY >= topCard.getY()) && (mY <= (topCard.getY() + Constants.cardHeight))) {      /** А это что ????? **/
                        for (int i = 2; i <= 5; i++) {
                            int availStackNum;
                            boolean result = game.checkTransferToHome(game.getCardStack(i), topCard);
                            availStackNum = result ? i : -1;
                            if (availStackNum >= 0) { // Если удалось найти подходящую домашнюю стопку
                                topCard.setX (Constants.betweenCardStacks * (availStackNum + 1)
                                        + 30);  // Изменяем координаты на домашнюю стопку
                                topCard.setY(15);
                                game.getCardStack(availStackNum).add(topCard); // Добавляем в домашнюю стопку
                                game.getCardStack(num).remove(topCardNum); // Удалаяем из старой стопки
                                game.checkEndGame();
                                break;
                            }
                        }
                    }
                }
            }
            game.openTopCard(num);
        }

        @Override
        public void mouseReleased(MouseEvent e) {  // При отпускании кнопки мыши
            if (!game.isEndGame()) {
                int mX = e.getX();
                int mY = e.getY();
                if (e.getButton() == 1) {
                    mouseReleased(mX, mY);
                }
            }
        }

        private void mouseReleased(int mX, int mY) {
            int num = getPressedStackNum(mX, mY);
            int chosenStackNum = game.getChosenStackNum();
            int chosenCardNum = game.getChosenCardNum();

            if (chosenStackNum != -1) {       // Если какая-то стопка выбрана в режиме переноса
                ArrayList<Card> chosenCardStack = game.getCardStack(chosenStackNum);
                chosenCardStack.get(chosenCardNum).setChosen(false);     // Убираем признак у выбранной карты
                // boolean isPossible =
                if ((num == -1) || (!game.checkCardTransfer(num, chosenStackNum))) {   // Если после переноса стопка не выбрана | перенос оказался ошибочным
                    int y = 0;
                    for (int i = chosenCardNum; i < game.getCardStack(chosenStackNum).size(); i++) {   // Возвращаем все переносимые карты назад
                        Card card = game.getCardStack(chosenStackNum).get(i);
                        card.setX(oldX);
                        card.setY(oldY + y);
                        y += 20;
                    }
                }
                game.setChosenCardNum(-1);
                game.setChosenStackNum(-1);
                game.openTopCard(chosenStackNum);
            } else {
                if (num == 0) {
                    game.getCardFromDeck();
                }
            }
        }
    }

    class myMouseActionListener extends MouseMotionAdapter {

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!game.isEndGame()) {
                int mX = e.getX();
                int mY = e.getY();
                mouseDragged(mX, mY);
            }
        }

        private void mouseDragged(int mX, int mY) {
            int chosenStackNum = game.getChosenStackNum();
            int chosenCardNum = game.getChosenCardNum();

            if (chosenStackNum >= 0) {
                ArrayList<Card> cardStack = game.getCardStack(chosenStackNum);
                Card card = cardStack.get(chosenCardNum);
                card.setX(mX - dx);
                card.setY(mY - dy);

                if (card.getX() < 0)    card.setX(0);
                if (card.getX() > 720)  card.setX(720);
                if (card.getY() < 0)    card.setY(0);
                if (card.getY() > 650)  card.setY(650);

                int y = 20;
                for (int i = game.getChosenCardNum() + 1; i < cardStack.size(); i++) {
                    int cX = card.getX(), cY = card.getY();
                    cardStack.get(i).setX(cX);
                    cardStack.get(i).setY(cY + y);
                    y += 20;
                }
            }
        }
    }
}

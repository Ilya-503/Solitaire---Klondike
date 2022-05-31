package controllers;

import models.Card;
import models.Constants;
import models.Game;

import java.awt.event.*;
import java.util.ArrayList;
import java.util.Stack;

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

        private void mousePressed(int mX, int mY) {   // При одиночном нажатии левой кнопки мыши
            int num = getPressedStackNum(mX, mY); // Определяем номер стопки
            setChosenCard(num, mX, mY);  // Устанавливаем выбранную карту
        }

        private void setChosenCard(int cardStackNum, int mX, int mY) {     // Установка выбранной карты
            if ((cardStackNum >= 1) && (cardStackNum <= 5)) {  // Если верхние стопки (1,2,3,4,5)
                ArrayList<Card> cardStack = game.getCardStack(cardStackNum);
                if (cardStack.size() > 0) {
                    int lastCardNum = cardStack.size() - 1;  // Получаем номер верхней карты
                    Card card = cardStack.get(lastCardNum);  // Получаем верхнюю карту
                    card.setChosen(true);
                    game.setChosenCardNum(lastCardNum);
                    game.setChosenStackNum(cardStackNum);
                    dx = mX - card.getX();
                    dy = mY - card.getY();
                    oldX = card.getX();        // Запоминаем текущие
                    oldY = card.getY();        //  координаты карты
                }
            }
            else if ((cardStackNum >= 6) && (cardStackNum <= 12)) {
                if (game.getCardStack(cardStackNum).size() > 0) {
                    int lastCardNum = game.getCardStack(cardStackNum).size() - 1;
                    Card card = game.getCardStack(cardStackNum).get(lastCardNum);
                    int chosenNum = -1;
                    if ((mY >= card.getY()) && (mY <= (card.getY() + Constants.cardHeight))) {   // Если выбрана самая верхняя карта
                        chosenNum = lastCardNum;
                    }
                    else if (mY < card.getY()) {   // Если выбрана НЕ самая верхняя карта
                        chosenNum = (mY - 130) / 20;                      // Вычисляем номер выбранной карты
                        if (game.getCardStack(cardStackNum).get(chosenNum).isTurnedOver()) {
                            chosenNum = -1;
                        }
                    }
                    if (chosenNum != -1) {   // Если карта выбрана
                        Card chosenCard = game.getCardStack(cardStackNum).get(chosenNum); // получ. выбр. карту
                        if (!chosenCard.isTurnedOver()) {
                            chosenCard.setChosen(true);
                            game.setChosenCardNum(chosenNum);
                            game.setChosenStackNum(cardStackNum);
                            dx = mX - chosenCard.getX();
                            dy = mY - chosenCard.getY();
                            oldX = chosenCard.getX();
                            oldY = chosenCard.getY();
                        }
                    }
                }
            }
        }

        private void mouseDoublePressed(int mX, int mY) {    // автоматический перенос карты в дом. стопку
            int num = getPressedStackNum(mX, mY);
            if ((num == 1) || ((num >= 6) && (num <= 12))) {    // Если это нижняя стопка или с номером 1
                if (game.getCardStack(num).size() > 0) {
                    int topCardNum = game.getCardStack(num).size() - 1;   // номер верхней карты
                    Card topCard = game.getCardStack(num).get(topCardNum);  // верхняя карта
                    if ((mY >= topCard.getY()) && (mY <= (topCard.getY() + Constants.cardHeight))) {
                        for (int i = 2; i <= 5; i++) {  // перебираем 4 дом. стопки
                            int homeStackNum = -1; // рез. поиска подх. дом. стопки
                            if (game.getCardStack(i).size() == 0) { // если дом. стопка пустая
                                if (topCard.getType() == 12) { // если туз
                                    homeStackNum = i; // номер домашней стопки
                                }
                            }
                            else {      // елси домшняя стопка уже не пустая
                                int topCardHomeNum = game.getCardStack(i).size() - 1; // ном. карты в дом. стопке
                                Card topCardHome = game.getCardStack(i).get(topCardHomeNum);
                                if ((topCardHome.getType() == 12) &&                  // Если эта карта
                                        (topCard.getSuit() == topCardHome.getSuit()) &&     // в домашней стопке - туз,
                                        (topCard.getType() == 0)) {               // а переносим двойку и их масти совпадают
                                    homeStackNum = i;  //  номер домашней стопки
                                }
                                else if ((topCardHome.getType() >= 0) &&      // Если эта карта в домашней стопке НЕ туз,
                                        (topCardHome.getType() < 11) &&       // а их масти совпадают
                                        (topCard.getSuit() == topCardHome.getSuit())) {
                                    if ((topCardHome.getType() + 1 == topCard.getType())) {  // переносимая к. выше на 1 лвл
                                        homeStackNum = i;
                                    }
                                }
                            }
                            if (homeStackNum >= 0) { // Если удалось найти подходящую домашнюю стопку
                                topCard.setX (Constants.betweenCardStacks * (homeStackNum + 1)
                                        + 30);  // Изменяем координаты на домашнюю стопку
                                topCard.setY(15);
                                game.getCardStack(homeStackNum).add(topCard); // Добавляем в домашнюю стопку
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
                ArrayList<Card> cardStack = game.getCardStack(chosenStackNum);
                cardStack.get(chosenCardNum).setChosen(false);     // Убираем признак у выбранной карты
                boolean isPossible = game.checkCardTransfer(chosenStackNum, num);
                if ((num == -1) || (!isPossible)) {   // Если после переноса стопка не выбрана | перенос оказался ошибочным
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
                if (num == 0) {  // Если верхняя левая стопка
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





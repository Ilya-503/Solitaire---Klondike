import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;

public class game {

    private final String path = "src\\main\\resources\\images\\";
    private final CardStack[] cardStacks;
    private boolean firstDeckDistribution;     // Признак первой выдачи карт из верхней левой стопки
    private int chosenStackNum;               // Номер стопки захваченной пользователем
    private int chosenCardNum;                // Номер карты в стопке захваченной пользователем
    private int dx, dy;                       // Смещения координат курсора мыши относительно координат карты
    private int oldX ,oldY;                   // Координаты карты до начала переноса мышью
    private final Timer tmEndGame;            // Таймер для эффекта при окончании игры
    public boolean endGame;
    public Image backImg;

    public game() {
        try {
            backImg = ImageIO.read(new File(path + "k0.png"));
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

        cardStacks = new CardStack[13];      // каждый элемент массива - это список значений (стопка карт)
        for (int i = 0; i < 13; i++) {
            cardStacks[i] = new CardStack();
        }

        tmEndGame = new Timer(100, arg0 -> {
            for (int i = 2; i <= 5; i++) {   // Перебираем четыре домашние стопки
                Card card = cardStacks[i].get(0);  // Получаем самую нижнюю карту
                cardStacks[i].add(card); // Нижнюю карту добавляем наверх
                cardStacks[i].remove(0);  // Удаляем нижнюю карту
            }
        });
        start();
    }

    // region Старт игры
    public void start() {     // Старт игры - Новая игра
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

    private void distribution() {     // Раздача карт в нижние семь стопок
        int x = 30;
        for (int i = 6; i < 13; i++) {  // Перебираем все стопки нижние семь стопок
            for (int j = 6; j <= i; j++) {  // Добавление карт в стопку
                int rnd = (int)(Math.random() * cardStacks[0].size()); // номер случайной карты из верхней левой стопки
                Card card = cardStacks[0].get(rnd);
                card.turnedOver = j < i;  // Если карта не самая верхняя,то показываем ее рубашкой
                card.x = x;
                card.y = 130 + cardStacks[i].size() * 20;  // Каждую следующую карту располагаем ниже на 20 пикселей
                cardStacks[i].add(card);  // Добавляем карту в нижнюю стопку
                cardStacks[0].remove(rnd); // Удаляем карту из верхней левой стопки
            }
            x += Constants.betweenCardStacks; //смещаемся правее
        }
    }

    private void loadDeck() {     // Загрузка изображений колоды
        for (int i = 1; i <= 52; i++) {
            cardStacks[0].add(new Card(path + "k" + (i) + ".png", backImg, i));
        }
    }

    // endregion

    // region Работа с картами

    private void openTopCard() {    // Автоматическое открытие верхней карт в нижних стопках
        for (int i = 6; i <= 12; i++) {     // Перебираем все нижние стопки карт
            if (cardStacks[i].size() > 0) {   // Если в стопке есть карты
                int lastCardNum = cardStacks[i].size() - 1;
                Card card = cardStacks[i].get(lastCardNum);
                if (card.turnedOver) {
                    card.turnedOver = false;
                }
            }
        }
    }

    private void setChosenCard(int cardStackNum, int mX, int mY) {     // Установка выбранной карты
        if ((cardStackNum >= 1) && (cardStackNum <= 5)) {  // Если верхние стопки (1,2,3,4,5)
            if (cardStacks[cardStackNum].size()>0) {
                int lastCardNum = cardStacks[cardStackNum].size() - 1;  // Получаем номер верхней карты
                Card card = cardStacks[cardStackNum].get(lastCardNum);  // Получаем верхнюю карту
                card.chosen = true;
                chosenCardNum = lastCardNum;
                chosenStackNum = cardStackNum;
                dx = mX - card.x;
                dy = mY - card.y;
                oldX = card.x;        // Запоминаем текущие
                oldY = card.y;        //  координаты карты
            }
        }
        else if ((cardStackNum >= 6) && (cardStackNum <= 12)) {
            if (cardStacks[cardStackNum].size() > 0) {
                int lastCardNum = cardStacks[cardStackNum].size() - 1;
                Card card = cardStacks[cardStackNum].get(lastCardNum);
                int chosenNum = -1;
                if ((mY >= card.y) && (mY <= (card.y + Constants.cardHeight))) {   // Если выбрана самая верхняя карта
                    chosenNum= lastCardNum;
                }
                else if (mY < card.y) {   // Если выбрана НЕ самая верхняя карта
                    chosenNum = (mY - 130) / 20;                      // Вычисляем номер выбранной карты
                    if (cardStacks[cardStackNum].get(chosenNum).turnedOver) {
                        chosenNum = -1;
                    }
                }
                if (chosenNum != -1) {   // Если карта выбрана
                    Card chosenCard = cardStacks[cardStackNum].get(chosenNum); // получ. выбр. карту
                    if (!chosenCard.turnedOver) {
                        chosenCard.chosen = true;
                        chosenCardNum = chosenNum;
                        chosenStackNum = cardStackNum;
                        dx = mX - chosenCard.x;
                        dy = mY - chosenCard.y;
                        oldX = chosenCard.x;
                        oldY = chosenCard.y;
                    }
                }
            }
        }
    }

    /**
     * Проверка возможности переноса и перенос,
     * если возможно это сделать
     * @param fromStackNum стопка ИЗ которой перенос
     * @param toStackNum стопка В которую перенос
     **/
    private boolean checkCardTransfer(int fromStackNum, int toStackNum) {
        boolean isPossible = false;
        Card transferringCard = cardStacks[fromStackNum].get(chosenCardNum); // Карта, которая переносится
        Card toStackTopCard = null;
        if (toStackNum != -1 && cardStacks[toStackNum].size() > 0) {  // Если есть карты в стопке
            toStackTopCard = cardStacks[toStackNum].get(cardStacks[toStackNum].size() - 1); // Получаем верхнюю карту
        }
        if ((toStackNum >= 2) && (toStackNum <= 5)) {  // Если четыре домашние стопки
            if (chosenCardNum ==  (cardStacks[fromStackNum].size() - 1)) {
                if (toStackTopCard == null) {      // Если стопка была пустая
                    if (transferringCard.cardType == 12) {  // Если переносимая карта ТУЗ
                        isPossible = true;
                    }
                }
                else if ((toStackTopCard.cardType == 12) &&                // Если в домашней стопке ТУЗ, переносится
                        (transferringCard.suit == toStackTopCard.suit) && // ДВОЙКА и масти совпадают
                        (transferringCard.cardType == 0)) {
                    isPossible = true;
                }
                else if ((toStackTopCard.cardType >= 0) && // Если в домашней стопке не ТУЗ, но масти совпадают
                        (toStackTopCard.cardType < 11) &&
                        (transferringCard.suit == toStackTopCard.suit)) {
                    if ((toStackTopCard.cardType + 1 == transferringCard.cardType)) { // Если перенос. к. выше на 1
                        isPossible = true;
                    }
                }

                if (isPossible) {   // Если результат проверки положительный
                    transferringCard.x = (Constants.betweenCardStacks * (toStackNum + 1)) + 30;  // Переносим карту в домашнюю стопку
                    transferringCard.y = 15;
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
                if (transferringCard.cardType == 11) {   // Если переносится КОРОЛЬ
                    isPossible = true;
                }
            }
            else {   // Если была НЕ пустая
                if (!toStackTopCard.turnedOver) {     // Если верхняя карта открыта
                    if (toStackTopCard.cardType != 12) {  // Если переносим НЕ на ТУЗА
                        if ((toStackTopCard.cardType == transferringCard.cardType + 1) ||   // Если переносимая карта
                                ((toStackTopCard.cardType == 0) &&                        // на один младше или
                                        (transferringCard.cardType == 12))) {          // ТУЗ переносится на двойку
                            if (toStackTopCard.redSuit != transferringCard.redSuit) {  // Если одна масть ЧЕРНАЯ, а другая КРАСНАЯ
                                y = toStackTopCard.y + 20;
                                isPossible = true;
                            }
                        }
                    }
                }
            }
            if (isPossible) {
                for (int i = chosenCardNum; i < cardStacks[fromStackNum].size(); i++) {
                    Card card_ = cardStacks[fromStackNum].get(i); // Добавляем все карты в новую стопу
                    card_.x = x;
                    card_.y = y;
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

    // endregion

    // region Обработка действий с мышью

    public void mouseDragged(int mX, int mY) {   // При захвате карты мышью
        if (chosenStackNum >= 0) {
            Card card = cardStacks[chosenStackNum].get(chosenCardNum);
            card.x = mX - dx;
            card.y = mY - dy;
            if (card.x < 0)    card.x = 0;
            if (card.x > 720)  card.x = 720;
            if (card.y < 0)    card.y = 0;
            if (card.y > 650)  card.y = 650;
            int y = 20;  // Все остальные карты в переносимой группе карт размещаем со сдвигом вниз на 20 пикселей
            for (int i = chosenCardNum + 1; i < cardStacks[chosenStackNum].size(); i++) {
                cardStacks[chosenStackNum].get(i).x = card.x;
                cardStacks[chosenStackNum].get(i).y = card.y + y;
                y += 20;
            }
        }
    }

    public void mousePressed(int mX, int mY) {   // При одиночном нажатии левой кнопки мыши
        int num = getPressedStackNum(mX, mY); // Определяем номер стопки
        setChosenCard(num, mX, mY);  // Устанавливаем выбранную карту
    }

    public void mouseDoublePressed(int mX, int mY) {    // автоматический перенос карты в дом. стопку
        int num = getPressedStackNum(mX, mY);
        if ((num == 1) || ((num >= 6) && (num <= 12))) {    // Если это нижняя стопка или с номером 1
            if (cardStacks[num].size() > 0) {
                int topCardNum = cardStacks[num].size() - 1;   // номер верхней карты
                Card topCard = cardStacks[num].get(topCardNum);  // верхняя карта
                if ((mY >= topCard.y) && (mY <= (topCard.y + Constants.cardHeight))) {
                    for (int i = 2; i <= 5; i++) {  // перебираем 4 дом. стопки
                        int homeStackNum = -1; // рез. поиска подх. дом. стопки
                        if (cardStacks[i].size() == 0) { // если дом. стопка пустая
                            if (topCard.cardType == 12) { // если туз
                                homeStackNum = i; // номер домашней стопки
                            }
                        }
                        else {      // елси домшняя стопка уже не пустая
                            int topCardHomeNum = cardStacks[i].size() - 1; // ном. карты в дом. стопке
                            Card topCardHome = cardStacks[i].get(topCardHomeNum);
                            if ((topCardHome.cardType == 12) &&                  // Если эта карта
                                    (topCard.suit == topCardHome.suit) &&     // в домашней стопке - туз,
                                    (topCard.cardType == 0)) {               // а переносим двойку и их масти совпадают
                                homeStackNum = i;  //  номер домашней стопки
                            }
                            else if ((topCardHome.cardType >= 0) &&      // Если эта карта в домашней стопке НЕ туз,
                                    (topCardHome.cardType < 11) &&       // а их масти совпадают
                                    (topCard.suit == topCardHome.suit)) {
                                if ((topCardHome.cardType + 1 == topCard.cardType)) {  // переносимая к. выше на 1 лвл
                                    homeStackNum = i;
                                }
                            }
                        }
                        if (homeStackNum >= 0) { // Если удалось найти подходящую домашнюю стопку
                            topCard.x = (Constants.betweenCardStacks * (homeStackNum + 1))
                                    + 30;  // Изменяем координаты на домашнюю стопку
                            topCard.y = 15;
                            cardStacks[homeStackNum].add(topCard); // Добавляем в домашнюю стопку
                            cardStacks[num].remove(topCardNum); // Удалаяем из старой стопки
                            checkEndGame();
                            break;
                        }
                    }
                }
            }
        }
        openTopCard();
    }

    public void mouseReleased(int mX, int mY) {  // При отпускании левой кнопки мыши
        int num = getPressedStackNum(mX, mY);  // Определяем номер стопки
        if (chosenStackNum != -1) {       // Если какая-то стопка выбрана в режиме переноса
            cardStacks[chosenStackNum].get(chosenCardNum).chosen = false;     // Убираем признак у выбранной карты
            boolean isPossible = checkCardTransfer(chosenStackNum, num);
            if ((num == -1) || (!isPossible)) {   // Если после переноса стопка не выбрана | перенос оказался ошибочным
                int y = 0;
                for (int i = chosenCardNum; i < cardStacks[chosenStackNum].size(); i++) {   // Возвращаем все переносимые карты назад
                    Card card = cardStacks[chosenStackNum].get(i);
                    card.x = oldX;
                    card.y = oldY + y;
                    y += 20;
                }
            }
            chosenStackNum = -1;   // Сброс выбранной карты
            chosenCardNum = -1;
            openTopCard();
        } else {
            if (num == 0) {  // Если верхняя левая стопка
                getCardFromDeck();
            }
        }
    }

    // endregion

    private void checkEndGame() {
        if ((cardStacks[2].size() == 13) &&       // во всех домашних стопках есть карты
                (cardStacks[3].size() == 13) &&
                (cardStacks[4].size() == 13) &&
                (cardStacks[5].size() == 13)) {
            endGame = true;
            tmEndGame.start();
        }
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

    private void getCardFromDeck() {  // Выдача карт из верхней левой стопки
        if (cardStacks[0].size() > 0) {  // Если в стопке есть карты
            int num;
            if (firstDeckDistribution) { // Если это первая выдача
                num = (int)(Math.random() * cardStacks[0].size());  // Получаем номер случайной карты в стопке
            } else { // Если повторная выдача
                num = cardStacks[0].size() - 1;
            }
            Card card = cardStacks[0].get(num);   // Получаем карту из стопки с номером 0
            card.turnedOver = false;             // Делаем отображение картинкой
            card.x += Constants.betweenCardStacks;                       //сдвигаем в стопку правее
            cardStacks[1].add(card);
            cardStacks[0].remove(num);
        } else {        // Если карт уже нет
            int lastCardNum = cardStacks[1].size() - 1; // Вычисляем номер последней карты в стопке номером 1
            for (int i = lastCardNum; i >= 0; i--) {  // перенос карт из стопки 1 в колоду
                Card card = cardStacks[1].get(i);
                card.turnedOver = true;
                card.x -= Constants.betweenCardStacks; // сдвигаем левее
                cardStacks[0].add(card);
            }
            cardStacks[1].clear();
            firstDeckDistribution = false;
        }
    }

    public void drawStack(Graphics gr) {  // Метод отрисовки всех стопок карт
        if (cardStacks[0].size() > 0) {    // ВЕРХНЯЯ ЛЕВАЯ СТОПКА - если в стопке есть карты
            cardStacks[0].get(cardStacks[0].size() - 1).draw(gr); // Получаем и рисуем самую верхнюю карту
        }
        if (cardStacks[1].size() > 1) {   // ВТОРАЯ СЛЕВА ВЕРХНЯЯ СТОПКА - если в стопке более одной карты
            cardStacks[1].get(cardStacks[1].size() - 2).draw(gr);   // Получаем и рисуем вторую сверху карту
            cardStacks[1].get(cardStacks[1].size() - 1).draw(gr);  // Получаем и рисуем самую верхнюю карту
        }
        else if (cardStacks[1].size() == 1) { // если в стопке одна карта
            cardStacks[1].get(cardStacks[1].size() - 1).draw(gr); // Получаем и рисуем самую верхнюю карту
        }
        for (int i = 2; i <= 5; i++) {  // ЧЕТЫРЕ ДОМАШНИЕ СТОПКИ
            if (cardStacks[i].size() > 1) { // Если в стопке более одной карты
                cardStacks[i].get(cardStacks[i].size() - 2).draw(gr);  // Получаем и рисуем вторую сверху карту
                cardStacks[i].get(cardStacks[i].size() - 1).draw(gr); // Получаем и рисуем самую верхнюю карту
            }
            else if (cardStacks[i].size() == 1) // если в стопке одна карта
            {
                cardStacks[i].get(cardStacks[i].size() - 1).draw(gr); // Получаем и рисуем самую верхнюю карту
            }
        }
        for (int i = 6; i < 13; i++) {  // НИЖНИЕ СЕМЬ СТОПОК
            if (cardStacks[i].size() > 0) {  // Если в стопке есть карты
                for (int j = 0; j < cardStacks[i].size(); j++) {  // Перебираем все карты из стопки
                    if (cardStacks[i].get(j).chosen) // находим выбранную карту - прерываем цикл
                        break;
                    cardStacks[i].get(j).draw(gr); // Рисуем карты
                }
            }
        }
        if (chosenStackNum != -1) { // ПЕРЕНОСИМЫЕ МЫШЬЮ КАРТЫ - если имеется выбранная стопка
            for (int i = chosenCardNum; i < cardStacks[chosenStackNum].size(); i++) {
                cardStacks[chosenStackNum].get(i).draw(gr); // Рисуем карты
            }
        }
    }
}

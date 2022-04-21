import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;

public class game {

    private final String path = "src\\main\\resources\\images\\";
    private final CardStack[] cardStacks;
    private boolean firstDistribution;     // Признак первой выдачи карт из верхней левой стопки
    private int chosenStackNum;             // Номер стопки захваченной пользователем
    private int chosenCardNum;              // Номер карты в стопке захваченной пользователем
    private int dx, dy;                    // Смещения координат курсора мыши относительно координат карты
    private int oldX ,oldY;               // Координаты карты до начала переноса мышью
    private final Timer tmEndGame;               // Таймер для эффекта окончания игры
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

        tmEndGame = new Timer(100, arg0 -> {  // будет запускаться при успешном завершении игры, скорость работы - 10 раз в секунду.
            for (int i = 2; i <= 5; i++) {   // Перебираем четыре домашние стопки
                Card card = cardStacks[i].get(0);  // Получаем самую нижнюю карту
                cardStacks[i].add(card); // Нижнюю карту добавляем наверх
                cardStacks[i].remove(0);  // Удаляем нижнюю карту
            }
        });
        start();
    }

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
                int chosenNum = -1;  // What does it mean?
                if ((mY >= card.y) && (mY <= (card.y + 97))) {   // Если выбрана самая верхняя карта
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

    private void checkEndGame() {
        if ((cardStacks[2].size() == 13) &&                 // во всех домашних стопках есть карты
                (cardStacks[3].size() == 13) &&
                (cardStacks[4].size() == 13) &&
                (cardStacks[5].size() == 13)) {
            endGame = true;
            tmEndGame.start();
        }
    }

    public void mouseDragged(int mX, int mY) {   // При захвате карты мышью
        if (chosenStackNum >= 0) {
            Card card = cardStacks[chosenStackNum].get(chosenCardNum);
            card.x = mX-dx;
            card.y = mY-dy;
            if (card.x < 0) {
                card.x = 0;
            }
            if (card.x > 720) {
                card.x = 720;
            }
            if (card.y < 0) {
                card.y = 0;
            }
            if (card.y > 650) {
                card.y = 650;
            }
            int y = 20;  // Все остальные карты в переносимой группе карт размещаем со сдвигом вниз на 20 пикселей
            for (int i = chosenCardNum + 1; i < cardStacks[chosenStackNum].size(); i++) {
                cardStacks[chosenStackNum].get(i).x = card.x;
                cardStacks[chosenStackNum].get(i).y = card.y + y;
                y += 20;
            }
        }
    }

    public void mousePressed(int mX, int mY) {   // При одиночном нажатии левой кнопки мыши
        int num = getPressedStackNom(mX, mY); // Определяем номер стопки
        setChosenCard(num, mX, mY); // Устанавливаем выбранную карту
    }

    public void mouseDoublePressed(int mX, int mY) {
        int num = getPressedStackNom(mX, mY);
        if ((num == 1) || ((num >= 6) && (num <= 12))) {    // Если это нижняя стопка или с номером 1
            if (cardStacks[num].size() > 0) {
                int topCardNum = cardStacks[num].size() - 1;   // номер верхней карты
                Card topCard = cardStacks[num].get(topCardNum);  // верхняя карта
                if ((mY >= topCard.y) && (mY <= (topCard.y + 97))) {
                    for (int i = 2; i <= 5; i++) {  // перебираем 4 дом. стопки
                        int rez = -1; // рез. поиска подх. дом. стопки
                        if (cardStacks[i].size() == 0) { // если дом. стопка пустая
                            if (topCard.cardType == 12) { // если туз
                                rez = i; // номер домашней стопки
                            }
                        }
                        else { // елси домшняя стопка уже не пустая
                            int nomPosled2 = cardStacks[i].size() - 1; // номер последней карты в домашней стопке
                            Card getKarta2 = cardStacks[i].get(nomPosled2);
                            // Если эта карта в домашней стопке - туз, а переносим
                            // двойку и их масти совпадают
                            if ((getKarta2.cardType == 12) &&
                                    (topCard.suit == getKarta2.suit) &&
                                    (topCard.cardType == 0)) {
                                rez = i;  // Запоминаем номер домашней стопки
                            }
                            // Если эта карта в домашней стопке НЕ туз,
                            // а их масти совпадают
                            else if ((getKarta2.cardType>=0)&&
                                    (getKarta2.cardType<11)&&
                                    (topCard.suit==getKarta2.suit))
                            {
                                // Если переносимая карта на один уровень старше
                                if ((getKarta2.cardType + 1 == topCard.cardType))
                                {
                                    // Запоминаем номер домашней стопки
                                    rez = i;
                                }
                            }
                        }
                        // Если удалось найти подходящую домашнюю стопку
                        if (rez>=0)
                        {
                            // Изменяем координаты на домашнюю стопку
                            topCard.x = (110*(rez+1))+30;
                            topCard.y = 15;
                            // Добавляем в домашнюю стопку
                            cardStacks[rez].add(topCard);
                            // Удалаяем из старой стопки
                            cardStacks[num].remove(topCardNum);
                            // Провеярем конец игры
                            checkEndGame();
                            // Прерываем цикл
                            break;
                        }
                    }
                }
            }
        }
        openTopCard();
    }

    // Проверка возможности переноса и перенос,
    // если возможно это сделать
    // nom1 - стопка ИЗ которой перенос
    // nom2 - стопка В которую перенос
    private boolean testPerenos(int nom1, int nom2)
    {
        // Результат проверки
        boolean rez = false;
        // Карта, которая переносится
        Card getKarta1 = cardStacks[nom1].get(chosenCardNum);
        Card getKarta2 = null;
        // Если есть карты в стопке
        if (cardStacks[nom2].size()>0)
        {
            // Получаем верхнюю карту
            getKarta2 = cardStacks[nom2].get(cardStacks[nom2].size()-1);
        }

        // Если четыре домашние стопки
        if ((nom2>=2)&&(nom2<=5))
        {
            if (chosenCardNum==(cardStacks[nom1].size()-1))
            {
                // Если стопка была пустая
                if (getKarta2==null)
                {
                    // Если переносимая карта ТУЗ
                    if (getKarta1.cardType==12) rez = true;
                }
                // Если в домашней стопке ТУЗ, переносится
                // ДВОЙКА и масти совпадают
                else if ((getKarta2.cardType==12)
                        &&(getKarta1.suit==getKarta2.suit)
                        &&(getKarta1.cardType==0))
                {
                    rez = true;
                }
                // Если в домашней стопке не ТУЗ,
                // но масти совпадают
                else if ((getKarta2.cardType>=0)
                        &&(getKarta2.cardType<11)
                        &&(getKarta1.suit==getKarta2.suit))
                {
                    // Если переносимая карта по рангу выше на один
                    if ((getKarta2.cardType+1==getKarta1.cardType))
                    {
                        rez = true;
                    }
                }
                // Если результат проверки положительный
                if (rez == true)
                {
                    // Переносим карту в домашнюю стопку
                    getKarta1.x = (110*(nom2+1))+30;
                    getKarta1.y = 15;
                    cardStacks[nom2].add(getKarta1);
                    cardStacks[nom1].remove(chosenCardNum);
                    checkEndGame();
                }
            }
        }
        // Если перенос в нижние стопки
        if ((nom2>=6)&&(nom2<=12))
        {
            int x = 30 + (nom2-6)*110;
            int y = 130;
            // Если нижняя стопка была пустая
            if (getKarta2==null)
            {
                // Если переносится КОРОЛЬ
                if (getKarta1.cardType==11) rez = true;
            }
            else // Если была НЕ пустая
            {
                // Если верхняя карта открыта
                if (getKarta2.turnedOver==false)
                {
                    // Если переносим НЕ на ТУЗА
                    if (getKarta2.cardType!=12)
                    {
                        // Если переносимая карта на один младше или
                        // ТУЗ переносится на двойку
                        if ((getKarta2.cardType==getKarta1.cardType+1)||
                                ((getKarta2.cardType==0)&&(getKarta1.cardType==12)))
                        {
                            // Если одна масть ЧЕРНАЯ, а другая КРАСНАЯ
                            if (getKarta2.redSuit!=getKarta1.redSuit)
                            {
                                y = getKarta2.y+20;
                                rez = true;
                            }
                        }
                    }
                }
            }
            // Если результат проверки положительный
            if (rez==true)
            {
                // Добавляем все карты в новую стопку
                for (int i=chosenCardNum; i<cardStacks[nom1].size();i++)
                {
                    Card getKarta_ = cardStacks[nom1].get(i);
                    getKarta_.x = x;
                    getKarta_.y = y;
                    cardStacks[nom2].add(getKarta_);
                    y += 20;
                }
                // Удалаяем все карты из старой стопки
                for (int i=cardStacks[nom1].size()-1; i>=chosenCardNum;i--)
                {
                    cardStacks[nom1].remove(i);
                }
            }
        }
        // Возвращаем результат
        return rez;
    }

    // При отпускании левой кнопки мыши
    public void mouseReleased(int mX, int mY)
    {
        // Определяем номер стопки
        int nom = getPressedStackNom(mX, mY);

        // Если какая-то стопка выбрана в режиме переноса
        if (chosenStackNum!=-1)
        {
            // Убираем признак у выбранной карты
            cardStacks[chosenStackNum].get(chosenCardNum).chosen = false;

            // Если после переноса стопка не выбрана или перенос
            // оказался ошибочным
            if ((nom==-1)||(testPerenos(chosenStackNum, nom)==false))
            {
                int y = 0;
                // Возвращаем все переносимые карты назад
                for (int i=chosenCardNum;i<cardStacks[chosenStackNum].size();i++)
                {
                    // Получаем карту
                    Card getKarta = cardStacks[chosenStackNum].get(i);
                    // Устанавливаем координаты X,Y до переноса
                    getKarta.x = oldX;
                    getKarta.y = oldY + y;
                    y += 20;
                }
            }
            // Сброс выбранной карты
            chosenStackNum = -1;
            chosenCardNum = -1;

            // Открытие верхней карты
            openTopCard();
        }
        else
        {
            // Если верхняя левая стопка
            if (nom==0)
            {
                // Делаем выдачу карты
                vidacha();
            }
        }
    }

    private int getPressedStackNom(int mX, int mY) {    // Определение стопки на которую нажали мышью
        // Если стопка не выбрана
        int nom=-1;

        // Если курсор находится в зоне верхних стопок
        if ((mY>=15) && (mY<=(15+97)))
        {
            if ((mX>=30) && (mX<=(30+72))) nom = 0;
            if ((mX>=140) && (mX<=(140+72))) nom = 1;
            if ((mX>=360) && (mX<=(360+72))) nom = 2;
            if ((mX>=470) && (mX<=(470+72))) nom = 3;
            if ((mX>=580) && (mX<=(580+72))) nom = 4;
            if ((mX>=690) && (mX<=(690+72))) nom = 5;
        }
        // Если курсор находится в зоне нижних стопок
        else if ((mY>=130) && (mY<=(700)))
        {
            if ((mX>=30) && (mX<=110*7))
            {
                if (((mX-30)%110)<=72)
                {
                    nom = (mX-30)/110;
                    nom += 6;
                }
            }
        }

        // Возврат результата
        return nom;
    }

    // Выдача карт из верхней левой стопки
    private void vidacha()
    {
        // Если в стопке есть карты
        if (cardStacks[0].size()>0)
        {
            int nom;
            // Если это первая выдача
            if (firstDistribution==true)
            {
                // Получаем номер случайной карты в стопке
                nom = (int)(Math.random()*cardStacks[0].size());
            }
            else // Если повторная выдача
            {
                // Получаем самую верхнюю карту
                nom = cardStacks[0].size()-1;
            }
            // Получаем карту из стопки с номером 0
            Card getKarta = cardStacks[0].get(nom);
            // Делаем отображение картинкой
            getKarta.turnedOver = false;
            // Увеличиваем координату на 110 -
            // сдвигаем в стопку правее
            getKarta.x += 110;
            // Додавляем карту в стопку с номером 1
            cardStacks[1].add(getKarta);
            // Удалем карту из стопки с номером 0
            cardStacks[0].remove(nom);
        }
        else // Если карт уже нет
        {
            // Вычисляем номер последней карты
            // в стопке номером 1
            int nomPosled = cardStacks[1].size()-1;

            // Переносим карты из стопки с номером 1
            // в стопку с номером 0
            for (int i=nomPosled;i>=0;i--)
            {
                Card getKarta = cardStacks[1].get(i);
                // Делаем отображение рубашкой
                getKarta.turnedOver = true;
                // Уменьшаем координату на 110 -
                // сдвигаем в стопку левее
                getKarta.x -= 110;
                // Добавляем в стопку с номером 0
                cardStacks[0].add(getKarta);
            }
            // Очищаем стопку с номером 1
            cardStacks[1].clear();
            // Признак первой выдачи меняем на false
            firstDistribution=false;
        }
    }

    // Старт игры - Новая игра
    public void start()
    {
        // Очищаем все тринадцать списков
        for (int i=0;i<13;i++)
        {
            // Удаление всех элементов списка
            cardStacks[i].clear();
        }

        // Производим загрузку
        load();

        // Раздача карт в нижние семь стопок
        razdacha();

        // Признак конца игры - false
        endGame = false;

        // Признак первой выдачи - true
        firstDistribution = true;

        // Номер выбранной карты
        chosenCardNum = -1;
        // Номер выбранной стопки
        chosenStackNum = -1;
    }

    // Раздача карт в нижние семь стопок
    private void razdacha()
    {
        // Начальная координата по X
        int x = 30;

        // Перебираем все стопки нижние семь стопок
        for (int i=6;i<13;i++)
        {
            // Добавление карт в стопку
            for (int j=6;j<=i;j++)
            {
                // Получаем номер случайной карты из верхней левой стопки
                int rnd = (int)(Math.random()*cardStacks[0].size());
                // Получаем эту карту
                Card getKarta = cardStacks[0].get(rnd);
                // Если карта не самая верхняя,
                // то показываем ее рубашкой
                if (j<i) getKarta.turnedOver = true;
                else getKarta.turnedOver = false; // Если карта верхняя
                // Координата по X
                getKarta.x = x;
                // Каждую следующую карту располагаем ниже на 20 пикселей
                getKarta.y = 130+cardStacks[i].size()*20;
                // Добавляем карту в нижнюю стопку
                cardStacks[i].add(getKarta);
                // Удаляем карту из верхней левой стопки
                cardStacks[0].remove(rnd);
            }
            // Увеличиваем координату по X
            // (смещаемся правее)
            x+=110;
        }
    }

    // Загрузка изображений колоды
    private void load()
    {
        // Цикл делает 52 шага
        for (int i=1;i<=52;i++)
        {
            // В верхнюю левую стопку загружаем карты
            cardStacks[0].add(new Card(path + "k"+(i)+".png", backImg, i));
        }
    }

    // Метод отрисовки всех стопок карт
    public void drawKoloda(Graphics gr)
    {
        // ВЕРХНЯЯ ЛЕВАЯ СТОПКА
        // Если в стопке есть карты
        if (cardStacks[0].size()>0)
        {
            // Получаем и рисуем самую верхнюю карту
            cardStacks[0].get(cardStacks[0].size()-1).draw(gr);
        }

        // ВТОРАЯ СЛЕВА ВЕРХНЯЯ СТОПКА
        // Если в стопке более одной карты
        if (cardStacks[1].size()>1)
        {
            // Получаем и рисуем вторую сверху карту
            cardStacks[1].get(cardStacks[1].size()-2).draw(gr);
            // Получаем и рисуем самую верхнюю карту
            cardStacks[1].get(cardStacks[1].size()-1).draw(gr);
        }
        else if (cardStacks[1].size()==1) // если в стопке одна карта
        {
            // Получаем и рисуем самую верхнюю карту
            cardStacks[1].get(cardStacks[1].size()-1).draw(gr);
        }

        // ЧЕТЫРЕ ДОМАШНИЕ СТОПКИ
        for (int i=2;i<=5;i++)
        {
            // Если в стопке более одной карты
            if (cardStacks[i].size()>1)
            {
                // Получаем и рисуем вторую сверху карту
                cardStacks[i].get(cardStacks[i].size()-2).draw(gr);
                // Получаем и рисуем самую верхнюю карту
                cardStacks[i].get(cardStacks[i].size()-1).draw(gr);
            }
            else if (cardStacks[i].size()==1) // если в стопке одна карта
            {
                // Получаем и рисуем самую верхнюю карту
                cardStacks[i].get(cardStacks[i].size()-1).draw(gr);
            }
        }

        // НИЖНИЕ СЕМЬ СТОПОК
        for (int i=6;i<13;i++)
        {
            // Если в стопке есть карты
            if (cardStacks[i].size()>0)
            {
                // Перебираем все карты из стопки
                for (int j=0;j<cardStacks[i].size();j++)
                {
                    // Если находим выбранную карту,
                    // то прерываем цикл
                    if (cardStacks[i].get(j).chosen==true) break;
                    // Рисуем карты
                    cardStacks[i].get(j).draw(gr);
                }
            }
        }

        // ПЕРЕНОСИМЫЕ МЫШЬЮ КАРТЫ
        // Если имеется выбранная стопка
        if (chosenStackNum!=-1)
        {
            // Перебираем карты от выбранной и до конца стопки
            for (int i=chosenCardNum;i<cardStacks[chosenStackNum].size();i++)
            {
                // Рисуем карты
                cardStacks[chosenStackNum].get(i).draw(gr);
            }
        }

    }
}

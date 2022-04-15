import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;

public class game {

    private String path = "src\\main\\resources\\images\\";
    public Image backImg;
    private CardStack[] cardStacks;
    private boolean firstDistribution;     // Признак первой выдачи карт из верхней левой стопки
    public boolean endGame;
    private int chosenStackNum;             // Номер стопки захваченной пользователем
    private int chosenCardNum;              // Номер карты в стопке захваченной пользователем
    private int dx, dy;                    // Смещения координат курсора мыши относительно координат карты
    private int oldX ,oldY;               // Координаты карты до начала переноса мышью
    private Timer tmEndGame;               // Таймер для эффекта окончания игры

    public game() {
        try {
            backImg = ImageIO.read(new File(path + "k0.png"));
        }
        catch (Exception ex) {}

        // Создаение массива из 13 элементов,
        // каждый элемент массива - это список значений (стопка карт)
        cardStacks = new CardStack[13];

        // Для каждого элемента массива в цикле
        // создаем новый объект
        for (int i=0;i<13;i++)
        {
            // Создание нового объекта (нового списка значений)
            cardStacks[i] = new CardStack();
        }


        // Таймер для эффекта при окончании игры
        // будет запускаться при успешном завершении игры
        // скорость работы - 10 раз в секунду.
        tmEndGame = new Timer(100,new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Перебираем четыре домашние стопки
                for (int i=2;i<=5;i++)
                {
                    // Получаем самую нижнюю карту
                    Card getKarta = cardStacks[i].get(0);
                    // Нижнюю карту добавляем наверх
                    cardStacks[i].add(getKarta);
                    // Удаляем нижнюю карту
                    cardStacks[i].remove(0);
                }
            }
        });

        // Запуск игры - старт игры
        start();
    }

    // Автоматическое открытие верхней карт
    // в нижних стопках
    private void openKarta()
    {
        // Перебираем все нижние стопки карт
        for (int i=6;i<=12;i++)
        {
            // Если в стопке есть карты
            if (cardStacks[i].size()>0)
            {
                // Номер последней карты в стопке
                int nomPoseld = cardStacks[i].size()-1;
                // Получаем последнюю карту
                Card getKarta = cardStacks[i].get(nomPoseld);
                // Если карты отображается рубашкой,
                // то открываем ее
                if (getKarta.turnedOver==true) getKarta.turnedOver = false;
            }
        }
    }

    // Установка выбранной карты
    private void setVibrana(int nom, int mX, int mY)
    {
        // Если верхние стопки (1,2,3,4,5)
        if ((nom>=1) && (nom<=5))
        {
            // Если в стопке есть карты
            if (cardStacks[nom].size()>0)
            {
                // Получаем номер верхней карты
                int nomPosled = cardStacks[nom].size()-1;
                // Получаем верхнюю карту
                Card getKarta = cardStacks[nom].get(nomPosled);
                // Устанавливаем признак выбранной карты
                getKarta.chosen = true;
                // Номер выбранной карты
                chosenCardNum = nomPosled;
                // Номер выбранной стопки
                chosenStackNum = nom;
                // Смещения курсора мыши
                dx = mX - getKarta.x;
                dy = mY - getKarta.y;

                // Запоминаем текущие координаты карты
                oldX = getKarta.x;
                oldY = getKarta.y;
            }
        }
        // Если нижние семь стопок
        else if ((nom>=6) && (nom<=12))
        {
            // Если в стопке есть карты
            if (cardStacks[nom].size()>0)
            {
                // Получаем номер верхней карты
                int nomPosled = cardStacks[nom].size()-1;
                // Получаем верхнюю карту
                Card getKarta = cardStacks[nom].get(nomPosled);
                int nomVibrana = -1;
                // Если выбрана самая верхняя карта
                if ((mY>=getKarta.y)&&(mY<=(getKarta.y+97)))
                {
                    nomVibrana = nomPosled;
                }
                // Если выбрана НЕ самая верхняя карта
                else if (mY<getKarta.y)
                {
                    // Вычисляем номер выбранной карты
                    nomVibrana = (mY-130)/20;
                    if (cardStacks[nom].get(nomVibrana).turnedOver==true)
                    {
                        nomVibrana = -1;
                    }
                }
                // Если карта выбрана
                if (nomVibrana!=-1)
                {
                    // Получаем выбранную карту
                    Card getKartaVibrana = cardStacks[nom].get(nomVibrana);
                    // Если карта открыта рубашкой
                    if (getKartaVibrana.turnedOver==false)
                    {
                        // Устанавливаем признак выбранной
                        getKartaVibrana.chosen = true;
                        // Номер выбранной карты
                        chosenCardNum = nomVibrana;
                        // Номер выбранной стопки
                        chosenStackNum = nom;
                        // Смещения курсора мыши
                        dx = mX - getKartaVibrana.x;
                        dy = mY - getKartaVibrana.y;

                        // Запоминаем текущие координаты карты
                        oldX = getKartaVibrana.x;
                        oldY = getKartaVibrana.y;
                    }
                }
            }
        }
    }


    // Проверка окончания игры
    private void testEndGame()
    {
        // Проверяем, что во всех четырех
        // домашних стопках по 13 карт
        if ((cardStacks[2].size()==13) &&
                (cardStacks[3].size()==13) &&
                (cardStacks[4].size()==13) &&
                (cardStacks[5].size()==13))
        {
            // Признак окочания игры
            endGame = true;
            // Запускаем таймер
            tmEndGame.start();
        }
    }

    // При захвате карты мышью
    public void mouseDragged(int mX, int mY)
    {
        // Если стопка выбрана
        if (chosenStackNum>=0)
        {
            // Получаем выбранную карту
            Card getKarta = cardStacks[chosenStackNum].get(chosenCardNum);
            // Изменяем координаты карты по курсору мыши
            getKarta.x = mX-dx;
            getKarta.y = mY-dy;

            // Ограничение области переноса карт
            if (getKarta.x<0) getKarta.x = 0;
            if (getKarta.x>720) getKarta.x = 720;
            if (getKarta.y<0) getKarta.y = 0;
            if (getKarta.y>650) getKarta.y = 650;

            // Все остальные карты в переносимой группе карт
            // размещаем со сдвигом вниз на 20 пикселей
            int y=20;
            for (int i=chosenCardNum+1;i<cardStacks[chosenStackNum].size();i++)
            {
                cardStacks[chosenStackNum].get(i).x = getKarta.x;
                cardStacks[chosenStackNum].get(i).y = getKarta.y + y;
                y += 20;
            }
        }
    }

    // При одиночном нажатии левой кнопки мыши
    public void mousePressed(int mX, int mY)
    {
        // Определяем номер стопки
        int nom = getNomKolodaPress(mX, mY);
        // Устанавливаем выбранную карту
        setVibrana(nom, mX, mY);
    }

    // При двойном щелчке левой клавишей мыши
    public void mouseDoublePressed(int mX, int mY)
    {
        // Определяем номер стопки
        int nom = getNomKolodaPress(mX, mY);
        // Если это нижняя стопка или с номером 1
        if ((nom==1) || ((nom>=6)&&(nom<=12)))
        {
            // Если в стопке есть карты
            if (cardStacks[nom].size()>0)
            {
                // Номер верхней карты
                int nomPosled = cardStacks[nom].size()-1;
                // Получаем верхнюю карту
                Card getKarta = cardStacks[nom].get(nomPosled);

                if ((mY>=getKarta.y)&&(mY<=(getKarta.y+97)))
                {
                    // Перебираем четыре домашние стопки
                    for (int i=2;i<=5;i++)
                    {
                        // Результат поиска подходящей
                        // домашней стопки
                        int rez = -1;
                        // Если домашняя стопка пустая
                        if (cardStacks[i].size()==0)
                        {
                            // Если переносимая карта - туз
                            if (getKarta.cardType==12)
                            {
                                // Запоминаем номер домашней стопки
                                rez = i;
                            }
                        }
                        // Если домашняя стопка уже не пустая
                        else
                        {
                            // Получаем номер последней карты в
                            // домашней стопке
                            int nomPosled2 = cardStacks[i].size()-1;
                            // Получаем саму карту
                            Card getKarta2 = cardStacks[i].get(nomPosled2);
                            // Если эта карта в домашней стопке - туз, а переносим
                            // двойку и их масти совпадают
                            if ((getKarta2.cardType==12)&&
                                    (getKarta.suit==getKarta2.suit)&&
                                    (getKarta.cardType==0))
                            {
                                // Запоминаем номер домашней стопки
                                rez = i;
                            }
                            // Если эта карта в домашней стопке НЕ туз,
                            // а их масти совпадают
                            else if ((getKarta2.cardType>=0)&&
                                    (getKarta2.cardType<11)&&
                                    (getKarta.suit==getKarta2.suit))
                            {
                                // Если переносимая карта на один уровень старше
                                if ((getKarta2.cardType+1==getKarta.cardType))
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
                            getKarta.x = (110*(rez+1))+30;
                            getKarta.y = 15;
                            // Добавляем в домашнюю стопку
                            cardStacks[rez].add(getKarta);
                            // Удалаяем из старой стопки
                            cardStacks[nom].remove(nomPosled);
                            // Провеярем конец игры
                            testEndGame();
                            // Прерываем цикл
                            break;
                        }
                    }
                }
            }
        }
        // Открываем верхнюю карту
        openKarta();
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
                if (rez==true)
                {
                    // Переносим карту в домашнюю стопку
                    getKarta1.x = (110*(nom2+1))+30;
                    getKarta1.y = 15;
                    cardStacks[nom2].add(getKarta1);
                    cardStacks[nom1].remove(chosenCardNum);
                    testEndGame();
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
        int nom = getNomKolodaPress(mX, mY);

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
            openKarta();
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

    // Определение стопки на которую нажали мышью
    private int getNomKolodaPress(int mX, int mY)
    {
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

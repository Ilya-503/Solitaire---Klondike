import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;

public class Field extends JPanel {

    private final Timer tmDraw;      // Таймер для отрисовки игрового поля
    private final game newGame;      // Переменная для реализации игры
    private Image background;

    public class myMouse1 extends MouseAdapter {    // MA - пустая реализация (I) MouseListener

        @Override
        public void mousePressed(MouseEvent e) {
            if (!newGame.endGame) {
                int mX = e.getX();
                int mY = e.getY();
                if (e.getButton() == 1) {
                    if (e.getClickCount() == 1) {
                        newGame.mousePressed(mX, mY);
                    } else if (e.getClickCount() == 2) {
                        newGame.mouseDoublePressed(mX, mY);
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {  // При отпускании кнопки мыши
            if (!newGame.endGame) {
                int mX = e.getX();
                int mY = e.getY();
                if (e.getButton() == 1) {
                    newGame.mouseReleased(mX, mY);
                }
            }
        }
    }

    public class myMouse2 extends MouseMotionAdapter {

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!newGame.endGame) {
                int mX = e.getX();
                int mY = e.getY();
                newGame.mouseDragged(mX, mY);
            }
        }
    }

    public Field() {
        addMouseListener(new myMouse1());         // Подключаем обработчики событий для мыши
        addMouseMotionListener(new myMouse2());

        newGame = new game();
        try {
            background = ImageIO.read(new File("src\\main\\resources\\images\\background.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setLayout(null);  // Включаем возможность произвольного размещения элементов формы

        createBtn("Новая игра", Color.BLUE, false);
        createBtn("Выход", Color.RED, true);

        tmDraw = new Timer(20, arg0 -> repaint());
        tmDraw.start();
    }

    private void createBtn(String text, Color color, boolean exitBtn) {
        JButton btn = new JButton();
        btn.setText(text);
        btn.setForeground(color);
        btn.setFont(new Font("serif", 0, 20));
        btn.setBounds(820,  exitBtn ? 150 : 50, 150, 50);
        if (exitBtn) {
            btn.addActionListener(arg0 -> System.exit(0));
        } else {
            btn.addActionListener(arg0 -> newGame.start());
        }
        this.add(btn);
    }

    public void paintComponent(Graphics gr) {    // Метод отрисовки элементов игрового поля
        super.paintComponent(gr);   // Очищение игрового поля
        gr.drawImage(background,0,0,1000,700,null);
        gr.setColor(Color.WHITE);        // Рисование белых контуров
        for (int i = 0; i < 7; i++) {
            gr.drawRect(30 + i * Constants.betweenCardStacks, 130, Constants.cardWidth, Constants.cardHeight);
            if (i != 2) {
                gr.drawRect(30 + i * Constants.betweenCardStacks, 15, Constants.cardWidth, Constants.cardHeight);
            }
        }
        newGame.drawStack(gr);     // Метод отрисовки всех стопок карт
    }
}

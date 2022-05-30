package view;

import controllers.MouseListeners;
import models.Constants;

import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;

public class Field extends JPanel {

    private final MouseListeners mouseListeners;
    private static Image background;
    private final GameInterface gameInterface;

    public Field(MouseListeners mouseListeners, GameInterface gameInterface) {
        this.mouseListeners = mouseListeners;
        addMouseListener(mouseListeners.getMouseListener());
        addMouseMotionListener(mouseListeners.getMouseActionListener());

        this.gameInterface = gameInterface;

        try {
            background = ImageIO.read(new File(Constants.path + "background.png"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setLayout(null);

        createBtn("Новая игра", Color.BLUE, false);
        createBtn("Выход", Color.RED, true);

        new Timer(20, arg0 -> repaint()).start();
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
            btn.addActionListener(arg0 -> mouseListeners.restartGame());
        }
        this.add(btn);
    }

    @Override
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        gr.drawImage(background,0,0,1000,700,null);
        gr.setColor(Color.WHITE);
        for (int i = 0; i < 7; i++) {
            gr.drawRect(30 + i * Constants.betweenCardStacks, 130, Constants.cardWidth, Constants.cardHeight);
            if (i != 2) {
                gr.drawRect(30 + i * Constants.betweenCardStacks, 15, Constants.cardWidth, Constants.cardHeight);
            }
        }
        gameInterface.drawStack(gr);
    }
}

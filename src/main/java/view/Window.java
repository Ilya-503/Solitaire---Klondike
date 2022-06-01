package view;

import controllers.GameLogic;
import controllers.MouseListener;
import models.Game;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    public Window() {
        Game game = new Game();
        Field panel = new Field(new MouseListener(game), new GameInterface(game));
        Container container = getContentPane();
        container.add(panel);
        setTitle("Игра \"Пасьянс-Косынка\"");
        setBounds(0, 0, 1000, 700);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    public Window() {
        Field panel = new Field();
        Container container = getContentPane();
        container.add(panel);
        setTitle("Игра \"Пасьянс-Косынка\"");
        setBounds(0, 0, 1000, 700);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}

import javax.imageio.*;
import java.awt.*;
import java.io.*;

public class Card {

    public int x, y;
    public Image frontImg;
    public boolean turnedOver;
    public Image backImg;
    public int suit;      // Масть карты
    public int cardType;
    public boolean chosen;    // Признак захвата карты мышью
    public boolean redSuit;   // Признак красной или черной масти

    public Card(String path, Image backImg, int num) {
        chosen = false;
        this.backImg = backImg;
        try {
            frontImg = ImageIO.read(new File(path));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        x = 30;
        y = 15;
        turnedOver = true;
        suit = (num - 1) % 4;
        cardType = (num - 1) / 4;
        redSuit = suit <= 1 ? false : true;
    }

    public void draw(Graphics gr) {                // Метод отображения (рисования карты)
        if (turnedOver) {
            gr.drawImage(backImg, x , y, 72, 97, null);
        } else {
            gr.drawImage(frontImg, x , y, 72, 97, null);
        }
        if (chosen) {
            gr.setColor(Color.YELLOW);
            gr.drawRect(x, y, 72, 97);
        }
    }
}
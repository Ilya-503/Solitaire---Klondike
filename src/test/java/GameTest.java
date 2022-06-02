import models.Card;
import models.Constants;
import models.Game;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GameTest {

    private Game game;

    @Before
    public void init() {
        game = new Game();
    }

    @Test
    public void testStartGame() {
        assertFalse(game.isEndGame());
        assertEquals(24, game.getCardStack(0).size());
        for (int i = 1; i < 6; i++) {
            assertEquals(0, game.getCardStack(i).size());
        }
        for (int i = 6; i < 13; i++) {
            assertEquals(i - 5, game.getCardStack(i).size());
        }
        game.start();
        assertFalse(game.isEndGame());
    }

    @Test
    public void testOpenTopCard() {
        int cardStackNum = (int) (Math.random() * 6 + 6);
        ArrayList<Card> cardStack = game.getCardStack(cardStackNum);
        int size = cardStack.size();

        if (cardStackNum == 6) {
            assertEquals(1, size);
            assertFalse(cardStack.get(0).isTurnedOver());
            cardStack.remove(0);
            game.openTopCard(cardStackNum);
        } else {
            Card closedCard = cardStack.get(size - 2);
            int suit = closedCard.getSuit(), type = closedCard.getType();
            boolean isTurnedOved = closedCard.isTurnedOver();
            cardStack.remove(--size);
            game.openTopCard(cardStackNum);
            Card openedCard = cardStack.get(size - 1);
            assertEquals(suit, openedCard.getSuit());
            assertEquals(type, openedCard.getType());
            assertNotEquals(isTurnedOved, openedCard.isTurnedOver());
            assertFalse(openedCard.isTurnedOver());
        }




    }

    @Test
    public void testCardTransferToLowerStacks() throws Exception {
        ArrayList<Card> toCardStack = game.getCardStack(6),
        fromCardStack = game.getCardStack(7);
        toCardStack.remove(0);
        fromCardStack.add(new Card(Constants.path + "k0.png", 47)); // красный король на пустое место
        game.setChosenCardNum(fromCardStack.size() - 1);
        assertTrue(game.checkCardTransfer(7, 6));

        fromCardStack.add(new Card(Constants.path + "k0.png", 43)); // красная дама на красного короля
        game.setChosenCardNum(fromCardStack.size() - 1);
        assertFalse(game.checkCardTransfer(7, 6));

        fromCardStack.add(new Card(Constants.path + "k0.png", 42)); // черная дама на красного короля
        game.setChosenCardNum(fromCardStack.size() - 1);
        assertTrue(game.checkCardTransfer(7, 6));

        fromCardStack.add(new Card(Constants.path + "k0.png", 49)); // черный туз на красную двойку
        toCardStack.add(new Card(Constants.path + "k0.png", 3));
        game.setChosenCardNum(fromCardStack.size() - 1);
        assertTrue(game.checkCardTransfer(7, 6));
    }

    @Test
    public void testCardTransferToHome() throws Exception {
        ArrayList<Card> toCardStack = game.getCardStack(2);
        assertFalse(game.checkTransferToHome(toCardStack,
                new Card(Constants.path + "k0.png", 2))); // двойка на пустое место

        assertTrue(game.checkTransferToHome(toCardStack,
                new Card(Constants.path + "k0.png", 50))); // туз-пики на пустое место

        toCardStack.add(new Card(Constants.path + "k0.png", 50));
        assertTrue(game.checkTransferToHome(toCardStack,
                new Card(Constants.path + "k0.png", 2))); // двойка-пики на туз

        assertFalse(game.checkTransferToHome(toCardStack,
                new Card(Constants.path + "k0.png", 3))); // красная двойка на черного туза
    }


}

import java.util.ArrayList;

public class CardStack {

    private ArrayList<Card> cardList;

    public CardStack() {
        cardList = new ArrayList<Card>();
    }

    public Card get(int num) {
        return cardList.get(num);
    }      // Получение карты из списка по номеру

    public void add(Card card) {
        cardList.add(card);
    }

    public void remove(int num) {
        cardList.remove(num);
    }

    public int size() {
        return cardList.size();
    }

    public void clear() {
        cardList.clear();
    }
}
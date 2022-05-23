import java.util.ArrayList;
import java.util.List;

public class CardStack {

    private List<Card> cardList;

    public CardStack() {
        cardList = new ArrayList<>();
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
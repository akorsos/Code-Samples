package blackjack;

import java.util.*;

public class Hand {

    ArrayList<Card> hand = new ArrayList<Card>();

    public Hand(){
    }

    public void clear() {
        hand.clear();
    }

    public void add(Card c) {
        if (c != null)
            hand.add(c);
    }

    public int getHandSize() {
        return hand.size();
    }

    public Card getCard(int position) {
        if (position >= 0 && position < hand.size())
            return (Card)hand.get(position);
        else
            return null;
    }
}


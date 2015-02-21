package blackjack;

public class BlackjackHand extends Hand {

    public int getValue() {

        int handVal;
        boolean hasAce;
        int cards;

        handVal = 0;
        hasAce = false;
        cards = getHandSize();

        for ( int i = 0;  i < cards;  i++ ) {
            Card card;
            int cardVal;
            card = getCard(i);
            cardVal = card.getValue();
            if (cardVal >= 10) {
                cardVal = 10;
            }
            if (cardVal == 1) {
                hasAce = true;
            }
            handVal = handVal + cardVal;
        }

        //takes care of hard and soft aces
        if (hasAce == true  &&  handVal + 10 <= 21 )
            handVal = handVal + 10;
        return handVal;
    }
}

package blackjack;

public class Deck {

    private Card[] deck;
    private int dealt;

    public Deck(){
        deck = new Card[52];
        int created = 0;
        for (int i = 0; i <= 3; i++){
            for (int j = 1; j <= 13; j++ ){
                deck[created] = new Card(j,i);
                created++;
            }
        }
        dealt = 0;
    }

    public void shuffleDeck() {
        for (int i = 51; i > 0; i--){
            int r = (int)(Math.random()*(i+1));
            Card temp = deck[i];
            deck[i] = deck[r];
            deck[r] = temp;
        }
        dealt = 0;
    }

    public Card dealCard() {
        if (dealt == 52)
            shuffleDeck();
        dealt++;
        return deck[dealt - 1];
    }
}

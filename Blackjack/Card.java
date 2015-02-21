package blackjack;

public class Card {

    private int HEARTS = 0;
    private int DIAMONDS = 1;
    private int SPADES = 2;
    private int CLUBS = 3;

    private int ACE = 1;
    private int JACK = 11;
    private int QUEEN = 12;
    private int KING = 13;

    private int suit;
    private int value;

    public Card(int value, int suit){
        this.value = value;
        this.suit = suit;
    }

    public String suitToString(){
        switch (suit){
            case 0:     return "Hearts";
            case 1:     return "Diamonds";
            case 2:     return "Spades";
            case 3:     return "Clubs";
            default:    return "Not a valid suit";
        }
    }

    public String valueToString() {
        switch ( value ) {
            case 1:   return "Ace";
            case 2:   return "2";
            case 3:   return "3";
            case 4:   return "4";
            case 5:   return "5";
            case 6:   return "6";
            case 7:   return "7";
            case 8:   return "8";
            case 9:   return "9";
            case 10:  return "10";
            case 11:  return "Jack";
            case 12:  return "Queen";
            case 13:  return "King";
            default:  return "Not a valid kind";
        }
    }

    public String toString() {
        return valueToString() + " of " + suitToString();
    }

    public int getSuit(){
        return suit;
    }

    public int getValue(){
        return value;
    }

}       
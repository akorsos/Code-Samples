package blackjack;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

public class Play {

    Deck deck;
    BlackjackHand dealer;
    BlackjackHand player;
    private int bet;
    private int chips = 1000;

    private JPanel dealerCards;
    private JPanel playerCards;

    private JTextArea status;
    private JLabel money;
    private ButtonGroup group;

    public Play(JPanel dealerCards, JPanel playerCards, JTextArea status, ButtonGroup group, JLabel money){
        deck = new Deck();
        dealer = new BlackjackHand();
        player = new BlackjackHand();
        this.money = money;
        this.group = group;

        this.dealerCards = dealerCards;
        this.playerCards = playerCards;
        this.status = status;
    }

    public void deal(){
        dealer.clear();
        player.clear();
        status.setText("Cards dealt! Good Luck!\n");
        pCard = 0;
        cCard = 0;
        deck.shuffleDeck();

        Enumeration<AbstractButton> betButtons = group.getElements();
        for (int i = 100; i <= 300; i+=100) {
            JRadioButton rb = (JRadioButton)betButtons.nextElement();
            if (rb.isSelected()) {
                bet = i;
                break;
            }
        }

        chips -= bet;
        money.setText("Player Money: $" + chips);

        Card newCard1 = deck.dealCard();
        String card1 = newCard1.toString();
        playerCard(newCard1.toString());
        player.add(newCard1);

        Card newCard2 = deck.dealCard();
        String card2 = newCard2.toString();
        playerCard(newCard2.toString());
        player.add(newCard2);

        Card newCard3 = deck.dealCard();
        String card3 = newCard3.toString();
        dealerCard(newCard3.toString());
        dealer.add(newCard3);
        Card newCard4 = deck.dealCard();
        String card4 = newCard4.toString();
        dealerCard(newCard4.toString());
        dealer.add(newCard4);

        status.append("\nPlayer has " +player.getValue());
        status.append("\n" +card1+ " and " +card2);

        status.append("\nDealer has " +dealer.getValue());
        status.append("\n" +card3+ " and " +card4);
    }

    public void hit(){
        Card newCard = deck.dealCard();
        playerCard(newCard.toString());
        player.add(newCard);
        status.append("\n");
        status.append("\nPlayer hits");
        status.append("\nYour drew the " + newCard);
        status.append("\nYour total is now " + player.getValue());
        if(player.getValue() > 21) {
            evalTurn();
        }
    }

    public boolean doubleDown(){
        boolean allow = true;
        if(chips >= bet*2){
            Card newCard = deck.dealCard();
            player.add(newCard);
            status.append("\nPlayer hits");
            status.append("\nYour drew the " + newCard);
            status.append("\nYour total is now " + player.getValue());
            bet += bet;
            chips -= bet;
            money.setText("Player Money: $" + chips);
            if (player.getValue() > 21) {
                evalTurn();
            }
        } else {
            status.append("\nYou don't have the required chips to double down!");
            status.append("\nPlease choose a different option.");
            allow = false;
        }
        return allow;
    }

    public void stand(){
       dealerTurn();
    }

    public void surrender(){
        status.append("\nPlayer Surrenders. \n$" +bet/2+ " has been returned to player.");
        chips += bet/2;
        money.setText("Player Money: $" + chips);
        evalTurn();
    }

    public void play(){
        status.append("Welcome to Blackjack!\n");
        status.append("You start with $1000 in chips\n");
        status.append("Select a bet amount then press 'Deal' to start\n");
        money.setText("Player Money: $" + chips);
    }

    public void evalTurn() {
        status.append("\n");
        if (dealer.getValue() == player.getValue()){
            status.append("\nDealer wins on a tie. You lose.");
            chips -= bet;
        } else {
            if (dealer.getValue() == 21) {
                status.append("\nDealer has Blackjack. Dealer wins.");
                chips -= bet;
            } else {
                if (player.getValue() == 21) {
                    status.append("\nYou have Blackjack. You win.");
                    chips += bet*2;
                } else {
                    if(player.getValue() > 21){
                        status.append("\nPlayer busted by going over 21. Dealer wins.");
                        chips -= bet*2;
                    } else {
                        if(dealer.getValue() > 21){
                            status.append("\nDealer busted by going over 21. You win.");
                            chips += bet*2;
                        } else {
                            if(player.getValue() > dealer.getValue()){
                                status.append("\nPlayer has higher value. You win.");
                                chips += bet*2;
                            } else {
                                status.append("\nDealer wins.");
                            }
                        }
                    }
                }
            }
        }
        money.setText("Player Money: $" + chips);

        evalChips();
    }

    public void evalChips(){
        if (chips < 100) {
            status.append("\nYou don't have enough chips to continue playing!");
        }
    }

    public void dealerTurn(){
        status.append("\n");
        status.append("\nPlayer stands.");
        status.append("\nDealer's cards are");
        while (dealer.getValue() < 17) {
            Card newCard = deck.dealCard();
            dealerCard(newCard.toString());
            status.append("\nDealer hits and gets the " + newCard);
            dealer.add(newCard);
            status.append("\nDealer's total is " + dealer.getValue());
        }

        if(dealer.getValue() >= 17 && dealer.getValue() <= 21){
            status.append("\nDealer stands at " + dealer.getValue());
        }
        evalTurn();
    }

    /*
    The following two functions would have been one function capable of drawing cards for both players except that I
    couldn't get it to draw anything unless they were separate. In addition, the cards will only print for
    every other turn for some reason. I'd really love some feedback as to why that's happening because I feel like
    I've exhausted ever possible option for repairing that and nothings seemed to work.
     */

    int pCard = 10;
    public void playerCard(String name){
        try{
            BufferedImage pic = ImageIO.read(new File("/Users/ak/problackjack/src/blackjack/cards/" +name+ ".png"));
            playerCards.getGraphics().drawImage(pic, pCard, 10, 85, 100, null); //x y height width
        }catch(Exception e){

        }
        pCard += 30;
    }

    int cCard = 10;
    public void dealerCard(String name){
        try{
            BufferedImage pic = ImageIO.read(new File("/Users/ak/problackjack/src/blackjack/cards/" +name+ ".png"));
            dealerCards.getGraphics().drawImage(pic, cCard, 10, 85, 100, null); //x y height width
        }catch(Exception e){

        }
        cCard += 30;
    }
}
package blackjack;

import java.awt.event.ActionEvent;

public class DealListener extends PlayListener {

    public DealListener(Play play) {
        super(play);
    }

    public void actionPerformed(ActionEvent event){
        game.deal();
        game.evalChips();
    }

}

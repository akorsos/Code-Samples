package blackjack;

import java.awt.event.ActionEvent;

public class StandListener extends PlayListener{

    public StandListener(Play play) {
        super(play);
    }

    public void actionPerformed(ActionEvent event){
        game.stand();
    }
}

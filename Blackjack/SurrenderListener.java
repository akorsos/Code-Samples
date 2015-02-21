package blackjack;

import java.awt.event.ActionEvent;

public class SurrenderListener extends PlayListener{

    public SurrenderListener(Play play) {
        super(play);
    }

    public void actionPerformed(ActionEvent event){
        game.surrender();
    }
}

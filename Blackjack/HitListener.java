package blackjack;

import java.awt.event.ActionEvent;

public class HitListener extends PlayListener{

    public HitListener(Play play) {
        super(play);
    }

    public void actionPerformed(ActionEvent event){
        game.hit();
    }

}

package blackjack;

import java.awt.event.ActionEvent;

public class DoubleDownListener extends PlayListener{

    public DoubleDownListener(Play play) {
        super(play);
    }

    public void actionPerformed(ActionEvent event){
        if(game.doubleDown()){
            game.dealerTurn();
            game.evalTurn();
        }
    }

}

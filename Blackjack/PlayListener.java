package blackjack;

import java.awt.event.ActionListener;

public abstract class PlayListener implements ActionListener {

    Play game;

    public PlayListener(Play game){
        this.game = game;
    }

}

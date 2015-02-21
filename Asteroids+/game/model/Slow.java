package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Slow extends Sprite {

    CopyOnWriteArrayList<Movable> slowed = new CopyOnWriteArrayList<Movable>();

    private final int MAX_EXPIRE = 100;
    public Slow(){

        setExpire(MAX_EXPIRE);

        CopyOnWriteArrayList<Movable> foes = CommandCenter.getMovFoes();
        for(int i = 0; i < foes.size(); i++){
            Sprite foe = (Sprite)foes.get(i);
            foe.setDeltaX(foe.getDeltaX()/8);
            foe.setDeltaY(foe.getDeltaY()/8);
            if(foe instanceof Asteroid){
                ((Asteroid)foe).setSpin(((Asteroid) foe).getSpin()/8);
            }

            slowed.add(foe);
        }

    }

    @Override
    public void move(){
        // nothing...
    }

    @Override
    public void draw(Graphics g){

    }

    @Override
    public void expire() {
        if (getExpire() == 0) {
            for(int i = 0; i < slowed.size(); i++){
                Sprite foe = (Sprite)slowed.get(i);
                foe.setDeltaX(foe.getDeltaX()*8);
                foe.setDeltaY(foe.getDeltaY()*8);
                if(foe instanceof Asteroid){
                    ((Asteroid)foe).setSpin(((Asteroid) foe).getSpin()*8);
                }
            }
            CommandCenter.movFriends.remove(this);
        }
        else {
            setExpire(getExpire() - 1);
        }
    }

}


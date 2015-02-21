package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;

public class ForcePush extends Sprite {

    int numPoints;
    private final int MAX_RADIUS = 100;
    private final int MAX_EXPIRE = 25;
    public ForcePush(){
        super();

        numPoints = 32;

        setExpire(MAX_EXPIRE);
        setRadius(1);
        genCircle();
        setColor(Color.CYAN);


        setCenter(CommandCenter.getFalcon().getCenter());
    }

    @Override
    public void move() {
        setCenter(CommandCenter.getFalcon().getCenter());
        setRadius(getRadius() + MAX_RADIUS/MAX_EXPIRE);

    }

    public void genCircle(){
        double[] angles = new double[numPoints];
        double[] lengths = new double[numPoints];

        for(int i = 0; i < numPoints; i++){
            angles[i] = (2*Math.PI / numPoints) * i;
            lengths[i] = getRadius();

        }

        setDegrees(angles);
        setLengths(lengths);

    }

    public void expire(){
        if (getExpire() == 0)
            CommandCenter.movFriends.remove(this);
        else
            setExpire(getExpire() - 1);
    }


}

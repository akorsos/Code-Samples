package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.*;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.game.model.CommandCenter;
import edu.uchicago.cs.java.finalproject.game.model.Falcon;
import edu.uchicago.cs.java.finalproject.game.model.Sprite;


public class EnemyBullet extends Sprite {

    private final double FIRE_POWER = 5.0;



    public EnemyBullet(Enemy enemy){

        super();


        //defined the points on a cartesean grid
        ArrayList<Point> pntCs = new ArrayList<Point>();

        pntCs.add(new Point(0,3)); //top point
        pntCs.add(new Point(1,-1));
        pntCs.add(new Point(0,-2));
        pntCs.add(new Point(-1,-1));

        assignPolarPoints(pntCs);

        //a bullet expires after 75 frames
        setExpire(75);
        setRadius(6);
        setColor(Color.ORANGE);

        //i want to shoot the bullet at the falcon
        ArrayList<Double> deltas = CommandCenter.getFalcon().getDeltas(enemy);

        //everything is relative to the falcon ship that fired the bullet
        setDeltaX(deltas.get(0) * FIRE_POWER);
        setDeltaY(deltas.get(1) * FIRE_POWER);
        setCenter( enemy.getCenter() );

        //set the bullet orientation to the falcon (ship) orientation
        int bulletAngle = (int)(Math.atan2(deltas.get(1), deltas.get(0))*360/(2*Math.PI));
        setOrientation(bulletAngle);

    }

    //override the expire method - once an object expires, then remove it from the arrayList. 
    public void expire(){
        if (getExpire() == 0)
            CommandCenter.movFoes.remove(this);
        else
            setExpire(getExpire() - 1);
    }

}

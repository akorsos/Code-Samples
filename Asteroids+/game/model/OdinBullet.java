package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Point;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;
import edu.uchicago.cs.java.finalproject.game.model.CommandCenter;
import edu.uchicago.cs.java.finalproject.game.model.Falcon;
import edu.uchicago.cs.java.finalproject.game.model.Sprite;


public class OdinBullet extends Sprite {

    private final double FIRE_POWER = 35.0;



    public OdinBullet(Odin odinSat){

        super();


        //defined the points on a cartesean grid
        ArrayList<Point> pntCs = new ArrayList<Point>();

        pntCs.add(new Point(0,3)); //top point

        pntCs.add(new Point(1,-1));
        pntCs.add(new Point(0,-2));
        pntCs.add(new Point(-1,-1));

        assignPolarPoints(pntCs);

        //a bullet expires after 20 frames
        setExpire( 20 );
        setRadius(6);


        //everything is relative to the falcon ship that fired the bullet
        setDeltaX( odinSat.getDeltaX() +
                Math.cos( Math.toRadians( odinSat.getOrientation() ) ) * FIRE_POWER );
        setDeltaY( odinSat.getDeltaY() +
                Math.sin( Math.toRadians( odinSat.getOrientation() ) ) * FIRE_POWER );
        setCenter( odinSat.getCenter() );

        //set the bullet orientation to the falcon (ship) orientation
        setOrientation(odinSat.getOrientation());


    }

    //override the expire method - once an object expires, then remove it from the arrayList. 
    public void expire(){
        if (getExpire() == 0)
            CommandCenter.movFriends.remove(this);
        else
            setExpire(getExpire() - 1);
    }

}

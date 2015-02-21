package edu.uchicago.cs.java.finalproject.game.model;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

import edu.uchicago.cs.java.finalproject.controller.Game;

public class Odin extends Sprite {

    private final double FIRE_POWER = 15.0;
    private final int MAX_EXPIRE = 50;

    //for drawing alternative shapes
    //you could have more than one of these sets so that your sprite morphs into various shapes
    //throughout its life
    public double[] dLengthsAlts;
    public double[] dDegreesAlts;
    private int nSpin;
    private Falcon fal;

    public Odin(Falcon fal) {

        super();

        //defined the points on a cartesean grid
        ArrayList<Point> pntCs = new ArrayList<Point>();


        pntCs.add(new Point(0, 5));
        pntCs.add(new Point(1, 3));
        pntCs.add(new Point(1, 0));
        pntCs.add(new Point(6, 0));
        pntCs.add(new Point(6, -1));
        pntCs.add(new Point(1, -1));
        pntCs.add(new Point(1, -2));

        pntCs.add(new Point(-1, -2));
        pntCs.add(new Point(-1, -1));
        pntCs.add(new Point(-6, -1));
        pntCs.add(new Point(-6, 0));
        pntCs.add(new Point(-1, 0));
        pntCs.add(new Point(-1, 3));
        assignPolarPoints(pntCs);


        //these are alt points
        ArrayList<Point> pntAs = new ArrayList<Point>();
        pntAs.add(new Point(0, 5));
        pntAs.add(new Point(1, 3));
        pntAs.add(new Point(1, -2));
        pntAs.add(new Point(-1, -2));
        pntAs.add(new Point(-1, 3));
        assignPolorPointsAlts(pntAs);

        //a Odin satellite expires after 50 frames
        setExpire(MAX_EXPIRE);
        setRadius(20);

        int nSpin = Game.R.nextInt(10);
        if(nSpin %2 ==0)
            nSpin = -nSpin;
        setSpin(nSpin);

        //random delta-x
        int nDX = Game.R.nextInt(10);
        if(nDX %2 ==0)
            nDX = -nDX;
        setDeltaX(fal.getDeltaX() + nDX);

        //random delta-y
        int nDY = Game.R.nextInt(10);
        if(nDY %2 ==0)
            nDY = -nDY;
        setDeltaY(fal.getDeltaY() + nDY);
        setCenter(fal.getCenter());

        //set the bullet orientation to the falcon (ship) orientation
        setOrientation(fal.getOrientation());
        setColor(Color.RED);
        CommandCenter.setOdinSat(this);


    }


    //assign for alt imag
    protected void assignPolorPointsAlts(ArrayList<Point> pntCs) {
        dDegreesAlts = convertToPolarDegs(pntCs);
        dLengthsAlts = convertToPolarLens(pntCs);

    }

    @Override
    public void move(){
        super.move();
        if (getExpire() % 1 == 0) {
            CommandCenter.movFriends.add(new OdinBullet(this));
        }

        //an asteroid spins, so you need to adjust the orientation at each move()
        setOrientation(getOrientation() + getSpin());

    }

    public void setSpin(int nSpin) {
        this.nSpin = nSpin;
    }

    public int getSpin() {
        return this.nSpin;
    }


    @Override
    public void draw(Graphics g){

        if (getExpire() < MAX_EXPIRE -5)
            super.draw(g);
        else{
            drawAlt(g);
        }

    }



    public void drawAlt(Graphics g) {
        setXcoords( new int[dDegreesAlts.length]);
        setYcoords( new int[dDegreesAlts.length]);
        setObjectPoints( new Point[dDegrees.length]);

        for (int nC = 0; nC < dDegreesAlts.length; nC++) {

            setXcoord((int) (getCenter().x + getRadius()
                    * dLengthsAlts[nC]
                    * Math.sin(Math.toRadians(getOrientation()) + dDegreesAlts[nC])), nC);


            setYcoord((int) (getCenter().y - getRadius()
                    * dLengthsAlts[nC]
                    * Math.cos(Math.toRadians(getOrientation()) + dDegreesAlts[nC])), nC);
            //need this line of code to create the points which we will need for debris
            setObjectPoint( new Point(getXcoord(nC), getYcoord(nC)), nC);
        }

        g.setColor(Color.DARK_GRAY);
        g.drawPolygon(getXcoords(), getYcoords(), dDegreesAlts.length);
    }


    //override the expire method - once an object expires, then remove it from the arrayList.
    @Override
    public void expire() {
        if (getExpire() == 0)
            CommandCenter.movFriends.remove(this);
        else
            setExpire(getExpire() - 1);
    }

}

package edu.uchicago.cs.java.finalproject.game.model;

import edu.uchicago.cs.java.finalproject.controller.Game;

import java.awt.*;
import java.util.ArrayList;

public class Enemy extends Sprite{

    public Enemy() {
        super();

        ArrayList<Point> pntCs = new ArrayList<Point>();

        // top of ship
        pntCs.add(new Point(6, 0));

        //right points
        pntCs.add(new Point(-4, 10));
        pntCs.add(new Point(-5, 9));
        pntCs.add(new Point(-5, 5));
        pntCs.add(new Point(-3, 3));
        pntCs.add(new Point(-2, 0));

        //left points
        pntCs.add(new Point(-3, -3));
        pntCs.add(new Point(-5, -5));
        pntCs.add(new Point(-5, -9));
        pntCs.add(new Point(-4, -10));


        assignPolarPoints(pntCs);

        setColor(Color.ORANGE);

        int nX = Game.R.nextInt(10);
        int nY = Game.R.nextInt(10);

        if (nX % 2 == 0)
            setDeltaX(nX);
        else
            setDeltaX(-nX);

        //set random DeltaY
        if (nY % 2 == 0)
            setDeltaX(nY);
        else
            setDeltaX(-nY);

        setRadius(35);
        //random point on the screen
        setCenter(new Point(Game.R.nextInt(Game.DIM.width),
                Game.R.nextInt(Game.DIM.height)));

        setExpire(200);
    }

    public void move() {
        super.move();
        if (getExpire() % 50 == 0) {
            CommandCenter.movFoes.add(new EnemyBullet(this));
        }
    }

    public void expire() {
        if (getExpire() == 0)
            CommandCenter.movFoes.remove(this);
        else
            setExpire(getExpire() - 1);
    }

}

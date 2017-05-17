/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electrichorseman2;

import java.awt.Image;

/**
 *
 * @author Malik
 */
public class Enemy extends Bosh {
    protected boolean alive;
    protected char state,type;
    public double vx, vy;
    protected boolean ground;
    protected int hitPoints;
    protected boolean jumper;
// Type: (c)hompy, (g)reaper, (r)obob, g(a)tor, (B)oss    

    public Enemy(double xs, double ys, double ws, double hs)
    {
        super(xs, ys, ws, hs);
        alive = true;
        state = 'r';
        vx = 0.5;
        vy = 0;
        ground = false;
        hitPoints = 0;
        jumper = false;
    }

    public boolean isAlive()
    {
        return alive;
    }

    public void setAlive(boolean b)
    {
        alive = b;
    }

    public boolean addImage(Image i)
    {
        return false;
    }

    public boolean setState(char c)
    {
        state = c;
        return true;
    }

    public char getState()
    {
        return state;
    }
// called child class?

    public void iterate(double d, boolean collH, boolean collV, boolean willFall, double dVX, double dVY)
    {
        if(isAlive())
        {
            vx += dVX;
            vy += dVY;
            if (!jumper)
            {
                if (collH ^ willFall) // ^ is Java's XOR operator. Had to try it.
                {
                    vx = -vx;
                }

                if (collV)
                {
                    if ((vy > 0) && !willFall)
                    {
                        ground = true;
                        vy = 0;
                    }
                }
            } else
            {
                if (collH) // ^ is Java's XOR operator. Had to try it.
                {
                    vx = -vx;
                }

                if (collV)
                {
                    if (vy > 0)
                    {
                        ground = true;
                        vy = 0;
                    }
                }
            }


            x += vx;
            y += vy;
        }
    }

    public void kill()
    {
        hitPoints--;
        if (hitPoints < 0)
        {
            alive = false;
            state = 'd';
        }
    }

    public int getHP()
    {
        return hitPoints;
    }
    public char getType()
    {
        return type;
    }
}

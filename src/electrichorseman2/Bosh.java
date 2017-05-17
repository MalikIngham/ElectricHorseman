/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electrichorseman2;

import java.awt.Graphics;
import java.awt.Image;

/**
 *
 * @author Malik
 */
public class Bosh {
     public double x, y, w, h; // Bounding box for drawing
    public double dCx, dCy, dCw, dCh; // Bounding box for collisions
    public Image im;
    public char type;
    public String path;

    public Bosh(double xs, double ys, double ws, double hs)
    {
        x = xs;
        y = ys;
        w = ws;
        h = hs;
        dCx = 0;
        dCy = 0;
        dCw = w;
        dCh = h;
        type = 't';
        path = "";
    }

    public Bosh(int xs, int ys, int ws, int hs, char tp)
    {
        x = xs;
        y = ys;
        w = ws;
        h = hs;
        dCx = 0;
        dCy = 0;
        dCw = w;
        dCh = h;        
        type = tp;
    }
    public Bosh(double xs, double ys, double ws, double hs,Image imgur)
    {
        x = xs;
        y = ys;
        w = ws;
        h = hs;
        dCx = 0;
        dCy = 0;
        dCw = w;
        dCh = h;
        type = 't';
        path = "";
        im = imgur;
    }
    public void draw(Graphics g, double x0, double y0)
    {
        g.drawImage(im, (int) (x - x0), (int) (y - y0), (int) w, (int) h, null);
    }

    public void copy(Bosh b)
    {
        x = b.x;
        y = b.y;
        w = b.w;
        h = b.h;
        dCx = b.dCx;
        dCy = b.dCy;
        dCw = b.dCw;
        dCh = b.dCh;        
        type = b.type;
        path = b.path;
        im = b.im;
    }

    public boolean isIn(Bosh b)
    {
        //if ((s.x + s.w > x) && (s.x < x + w) && (s.y + s.h > y) && (s.y < y + h))
        if (( b.x+b.dCx + b.dCw > x+dCx) && (b.x+b.dCx < dCx + dCw) && (b.y+b.dCy + b.dCh > y+dCy) && (b.y+b.dCy < y+dCy + dCh))
        {
            return true;
        } else
        {
            return false;
        }

    }

    public boolean isIn(Bosh b, double dx, double dy)
    {        
        //if (( s.x + s.w +dx> x) && (s.x +dx < x + w) && (s.y+ s.dCh +dy > y) && (s.y + dy< y+h))
        if (
                (b.x+b.dCx + b.dCw +dx > x+dCx) &&
                (b.x+b.dCx +dx < x +dCx + dCw) && 
                (b.y+ b.dCh +dy > y + dCy) && 
                (b.y + b.dCy + dy< y+dCy+dCh))
        {
            return true;
        } 
        else
        {
            return false;
        }
    }
}

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
public class Tile extends Bosh {
    
    int gid = -1;
//    TileManager tm;
    public Tile(int xs, int ys, int ws, int hs, int g, Image img)
    {
        super(xs,ys,ws,hs,img);
        gid = g;
        im = img;
    }
    
    public void draw(Graphics g, double x0, double y0)
    {
        g.drawImage(im,(int)(x-x0),(int)(y-y0),(int)w,(int)h,null);
    }
    public Image getImage()
    {
        return im;        
    }
    public String toString()
    {
        String tmp = "(x,y,w,h) = ("+x+","+y+","+w+","+h+")\n Image: "+im.toString();
        return tmp;        
    }
}

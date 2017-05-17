/* Malik Ingham
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electrichorseman2;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 *
 * @author Malik
 */
public class TileManager {
    private TilePoint[] tp;
    private int max;
    private int sz;
    public TileManager(int max)
    {
        tp = new TilePoint[max];
        sz = 0;
    }
    public void add(BufferedImage im,int startGID,int nTiles, int w, int h)
    {
        for(int i = 0;i<nTiles;i++)// for now assume 1D array
        {
            Image tmpImg = im.getSubimage(i*w, 0, w, h);
            tp[startGID+i] = new TilePoint(tmpImg,w,h,startGID+i);
        }
        sz+=nTiles;
    }
    public Image get(int id)
    {
        return tp[id].getImage();
    }
    public TilePoint getTile(int id)
    {
        return tp[id];
    }
    public int getSize()
    {
        return sz;
    } 
}

/* Malik Ingham
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
public class TilePoint {
    private Image im;
    public int gid;
    public int width,height;
    
    public TilePoint(Image img,int w, int h, int g)
    {
        width = w;
        height = h;
        gid = g;
        im = img;
    }
    
    public Image getImage()
    {
        return im;
    }
}

/* Malik Ingham
 * 
 * 12-4-15
 * 
 */
package electrichorseman2;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;

/**
 *
 * @author Malik
 */
public class ElectricHorseman2 extends JFrame implements KeyListener{

    gamePanel gPanel = new gamePanel();

    public void init()
    {        
        gPanel.setFocusable(true);
        gPanel.addKeyListener(this);

    }

    public ElectricHorseman2()
    {
//Settings for frame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900-30, 600-15);
        setTitle("Test");
        Container content = getContentPane();

        content.add(gPanel, BorderLayout.CENTER);
    }
// Action Event Handlers

    public void keyPressed(KeyEvent k)
    {
        gPanel.handleInput('p',k);
    }

    public void keyReleased(KeyEvent k)
    {
        gPanel.handleInput('r',k);
    }

    public void keyTyped(KeyEvent k)
    {
        gPanel.handleInput('t',k);
        repaint();       
        gPanel.update();
    }
    
    public static void main(String[] args)
    {
        ElectricHorseman2 mf = new ElectricHorseman2();
        mf.setVisible(true);
        mf.init();
    }
    
}

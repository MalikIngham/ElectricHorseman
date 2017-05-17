/* Malik Ingham
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package electrichorseman2;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 *
 * @author Malik
 */
public class gamePanel extends JPanel implements Runnable
{

    final int HOME = 0;
    final int OFFICE = 1;
    final int LAPTOP = 2;
    final int JAR = 3;
    int workLoc = JAR;
    boolean sound = true;
    public static final int INTRO = 0, PLAYING = 1, CUTSCENE = 2, GAMEOVER = 3, JUSTDIED = 4, LOADINTRO = 5, LOADLEVEL = 6, BOSS = 7, PASSEDBOSS = 8, WON = 9;    //temporary variables
    Thread fred;
    double t = 0;
    private int W = 900; //width of screen
    private int drawW = 865; //
    private int H = 600; // height of screen
    private int drawH = 570; //screen width
    private boolean left, right, up, down, jump, ground;
    private double x, y, playerStartX, playerStartY, pw, ph;
    private double vx, vy;
    private double bossXL,bossXR, bossYU,bossYD;
    private double bossScreenX,bossScreenY;
    private double grav;
    private double CX, CY;
    private double screenX, screenY, tmpScreenX;
    private int skyeY = 0;    
    private double dVx = 0.3;
    private double friction = 0.2;
    private int bounce = 9;
    private int spriteRate;
    private int lives;
    private int tickCounter;
    private int gameState;
    private int bossIndex;
    private int level;
    private int levelMax;
    boolean network; // Load files locally(F) or from server(T)?
    private int brickWidth;
    private Graphics2D g2D;
    private GradientPaint skye;
    private Color gnd;
    
    private Image introScreen, gameOverScreen, loadScreen, level1Screen, level2Screen,youWinScreen;
    private String status = "";
    private TileLayer tiles, fg1, bg1, bg2, bg3, spikes;
    private TileManager tm;
    private Player sp;
    private Enemy[] enemies;
    private int numEnemies;
    private String spritePath, soundPath, screenPath;
    private String levelBase, pathFile;
    private double levelMinX, levelMinY, levelMaxX, levelMaxY;
    private int sleepTime;
    private int mapW, mapH;
    private int mapTileW, mapTileH;
    private int spriteGID;
    

    public gamePanel()
    {
//  INITIALIZE VARIABLES
        //constants
        grav = 0.4;
        // game timers
        sleepTime = 14;
        tickCounter = 100;
        // player variables
        dVx = 0.25;
        friction = 0.2;
        bounce = 9;
        spriteRate = 16; // lower is faster :0

        x = y = 25;
        vx = vy = 0;
        lives = 2;
        jump = false;
        ground = true;
        pw = 30;
        ph = 45;
        // Dimensions
        CX = 450;
        CY = 300;
        brickWidth = 30;
        // Keylistener booleans
        up = down = false;
        left = right = false;
        // Game state variables
        bossIndex = 0;
        level = 1;
        levelMax = 2;
        gameState = LOADINTRO;

        right = left = up = down = false;
        screenX = x;
        screenY = y;
        tmpScreenX = 25;

        switch (workLoc)
        {
            case HOME:
                levelBase = "/Users/Malik/Projects/ElectricHorseman2/Levels";
                pathFile = "/Users/Malik/Projects/ElectricHorseman2";
                break;
            case OFFICE:
                levelBase = "/Users/Malik/Projects/ElectricHorseman2/Levels";
                pathFile = "/Users/Malik/Projects/ElectricHorseman2";
                break;
            case LAPTOP:
                levelBase = "/Users/Malik/Projects/ElectricHorseman2/Levels";
                pathFile = "/Users/Malik/Projects/ElectricHorseman2";
                break;
            case JAR:
                levelBase = "Levels/Dev/";
                pathFile = "";
                break;                
        }

        screenPath = pathFile + "Images/Screens/";
        spritePath = pathFile + "Images/Sprites/";
        soundPath = pathFile + "Sounds/";

        

        resetTiles();

        try
        {
            System.out.println("Adding Image: " + screenPath + "Mark1.png");
            introScreen = ImageIO.read(new File(screenPath + "Mark1.png"));
            loadScreen = ImageIO.read(new File(screenPath + "LoadMark1.png"));
            level1Screen = ImageIO.read(new File(screenPath + "level1.png"));
            level2Screen = ImageIO.read(new File(screenPath + "level2.png"));
            gameOverScreen = ImageIO.read(new File(screenPath + "GameOverMark1.png"));
            youWinScreen = ImageIO.read(new File(screenPath + "YouWin.png"));
        } catch (IOException e)
        {
            System.out.println("Error loading screen images");
            e.printStackTrace();
        }

        tm = new TileManager(250);
        fred = new Thread(this);
        fred.start();
    }

    @Override
    public void paintComponent(Graphics g)
    {
        //super.paintComponents(g);
        if (gameState == LOADINTRO)
        {
            g.drawImage(loadScreen, 0, 0, drawW, drawH, null);
            g.setColor(Color.white);
            g.drawString(status, 10, H - 10);
        } else if (gameState == INTRO)
        {
            g.drawImage(introScreen, 0, 0, drawW, drawH, null);
        } else if (gameState == LOADLEVEL)
        {
            Image toDraw = null;
            switch (level)
            {
                case 1:
                    toDraw = level1Screen;
                    break;
                case 2:
                    toDraw = level2Screen;
                    break;
            }
            g.drawImage(toDraw, 0, 0, drawW, drawH, null);
        } else if (gameState == PLAYING)
        {
            g2D = (Graphics2D) g;
            g2D.setPaint(skye);
            g2D.fill(new Rectangle(0, 0, W, Math.max(skyeY-(int)screenY,0)));
            g.setColor(gnd);
            g.fillRect(0,Math.max(skyeY-(int)screenY,0),W,H-Math.max(skyeY-(int)screenY,0));
            drawLayer(g, bg3, 4);
            drawLayer(g, bg2, 2);
            drawLayer(g, bg1, 1);
            drawLayer(g, tiles, 1);
            drawLayer(g, spikes, 1);
            sp.draw(g, screenX - CX, screenY - CY);
            for (int i = 0; i < numEnemies; i++)
            {
                enemies[i].draw(g, screenX - CX, screenY - CY);
            }
            drawLayer(g, fg1, 1);
            for (int i = 0; i < lives; i++)
            {
                sp.drawStatic(g, (int) (2 * CX - 150 + i * (sp.w + 5)), 5);
            }
        } else if (gameState == BOSS)
        {
            g2D = (Graphics2D) g;
            g2D.setPaint(skye);
            g2D.fill(new Rectangle(0, 0, W, Math.max(skyeY-(int)screenY,0)));
            g.setColor(gnd);
            g.fillRect(0,Math.max(skyeY-(int)screenY,0),W,H-Math.max(skyeY-(int)screenY,0));
            drawLayer(g, bg3, 4,bossScreenX,bossScreenY);
            drawLayer(g, bg2, 2,bossScreenX,bossScreenY);
            drawLayer(g, bg1, 1,bossScreenX,bossScreenY);
            drawLayer(g, tiles, 1,bossScreenX,bossScreenY);
            drawLayer(g, spikes, 1,bossScreenX,bossScreenY);
            sp.draw(g,bossScreenX-CX, bossScreenY-CY);

            for (int i = 0; i < numEnemies; i++)
            {
                enemies[i].draw(g, bossScreenX - CX, bossScreenY - CY);
            }

            for (int i = 0; i < lives; i++)
            {
                sp.drawStatic(g, (int) (2 * CX - 200 + i * (sp.w + 5)), 5);
            }
            for (int i = 0; i < enemies[bossIndex].getHP(); i++)
            {
                g.setColor(Color.red);
                g.fillRect((int) (2 * CX - 100), 20 + i * 20, 40, 20);
            }
            if (!enemies[bossIndex].isAlive())
            {
                level++;
                gameState = PASSEDBOSS;
                tickCounter = 250;
                skye = new GradientPaint(W / 2, H, Color.YELLOW, W / 2, 0, Color.WHITE);
                           
            }
        } else if (gameState == PASSEDBOSS)
        {
            g2D = (Graphics2D) g;
            g2D.setPaint(skye);
            g2D.fill(new Rectangle(0, 0, W, Math.max(skyeY-(int)screenY,0)));
            g.setColor(gnd);
            g.fillRect(0,Math.max(skyeY-(int)screenY,0),W,H-Math.max(skyeY-(int)screenY,0));
            drawLayer(g, bg3, 4,bossScreenX,bossScreenY);
            drawLayer(g, bg2, 2,bossScreenX,bossScreenY);
            drawLayer(g, bg1, 1,bossScreenX,bossScreenY);
            drawLayer(g, tiles, 1,bossScreenX,bossScreenY);
            drawLayer(g, spikes, 1,bossScreenX,bossScreenY);
            sp.draw(g,bossScreenX-CX, bossScreenY-CY);

            for (int i = 0; i < numEnemies; i++)
            {
                enemies[i].draw(g, bossScreenX - CX, bossScreenY - CY);
            }

            for (int i = 0; i < lives; i++)
            {
                sp.drawStatic(g, (int) (2 * CX - 150 + i * (sp.w + 5)), 5);
            }
            for (int i = 0; i < enemies[bossIndex].getHP(); i++)
            {
                g.setColor(Color.red);
                g.fillRect((int) (2 * CX - 100), 20 + i * 20, 40, 20);
            }
        } else if (gameState == GAMEOVER)
        {
            g.drawImage(gameOverScreen, 0, 0, drawW, drawH, null);
        } else if (gameState == JUSTDIED)
        {
            if ((tickCounter / 8) % 2 == 0)
            {
                g.setColor(Color.black);
            } else
            {
                g.setColor(Color.red);
            }
            g.fillRect(0, 0, W, H);
            for (int i = 0; i < lives; i++)
            {
                sp.drawStatic(g, (int) (CX + i * (sp.w + 5)), (int) CY - 10);
            }
        }
        else if (gameState == WON)
        {
            g.drawImage(youWinScreen, 0, 0, drawW, drawH, null); 
        }

    }

    public void update()
    {
        repaint();
    }

// Thread Stuff
    public void start()
    {
        fred = new Thread(this);
        fred.start();
    }

    public void run()
    {
        while (true)
        {
//            move();
            update();

            if (gameState == LOADINTRO)
            {
                System.out.print("Loading sprite Images ...");
                status = "Loading sprite Images ...";
                repaint();

                String[] fNames_r =
                {
                    spritePath + "smiley_r_1.PNG", spritePath + "smiley_r_2.PNG", spritePath + "smiley_r_3.PNG", spritePath + "smiley_r_4.PNG", spritePath + "smiley_r_5.PNG", spritePath + "smiley_r_6.PNG", spritePath + "smiley_r_7.PNG", spritePath + "smiley_r_8.PNG", spritePath + "smiley_r_9.PNG", spritePath + "smiley_r_10.PNG", spritePath + "smiley_r_11.PNG"
                };
                String[] fNames_l =
                {
                    spritePath + "smiley_l_1.PNG", spritePath + "smiley_l_2.PNG", spritePath + "smiley_l_3.PNG", spritePath + "smiley_l_4.PNG", spritePath + "smiley_l_5.PNG", spritePath + "smiley_l_6.PNG", spritePath + "smiley_l_7.PNG", spritePath + "smiley_l_8.PNG", spritePath + "smiley_l_9.PNG", spritePath + "smiley_l_10.PNG", spritePath + "smiley_l_11.PNG"
                };
                String[] fNames_ju =
                {
                    spritePath + "smiley_j_r_u.PNG"
                };
                String[] fNames_Ju =
                {
                    spritePath + "smiley_j_l_u.PNG"
                };
                String[] fNames_jd =
                {
                    spritePath + "smiley_j_r_d.PNG"
                };
                String[] fNames_Jd =
                {
                    spritePath + "smiley_j_l_d.PNG"
                };
                String[] fNames_s =
                {
                    spritePath + "smiley_stand_r_1.PNG"
                };
                String[] fNames_S =
                {
                    spritePath + "smiley_stand_l_1.PNG"
                };

                sp = new Player(x, y, pw, ph, fNames_r, fNames_l, fNames_ju, fNames_Ju, fNames_jd, fNames_Jd, fNames_s, fNames_S, spriteRate);
                System.out.print("done.\n");
                System.out.print("Loading tile Images ...");
                status = "Loading tile Images ...";
                repaint();

                loadLevel(levelBase + "level1.tmx");
                System.out.print("done.\n");

                levelMinX = levelMinY = 0;
                levelMaxX = tiles.get(tiles.size() - 1).x + brickWidth;
                levelMaxY = tiles.get(tiles.size() - 1).y + 2 * brickWidth;

                System.out.print("Loading background ...");
                status = "Loading background ...";
                repaint();

                System.out.print("done.\n");
               

                gameState = INTRO;
//                System.out.print("INTRO.\n");
                repaint();
            } else if (gameState == LOADLEVEL)
            {
                //              System.out.print("LOADLEVEL.\n");
                String lPath;
                switch (level)
                {
                    case 1:
                        skye = new GradientPaint(W / 2, H, Color.BLUE, W / 2, 0, Color.WHITE);
                        gnd = Color.green;
                        skyeY = 820;
                        lPath = levelBase + "level1.tmx";
                        bossScreenX = 135*brickWidth;
                        bossScreenY = 11*brickWidth;
                        bossXL = 128*brickWidth;
                        bossXR = bossXL+30*brickWidth;
                        bossYU = 11*brickWidth;
                        bossYD = bossYU+ 15*brickWidth;
                        
                        break;
                    case 2:
                        skye = new GradientPaint(W / 2, H, Color.RED, W / 2, 0, Color.WHITE);
                        gnd = new Color(12,134,61);
                        skyeY = 1020;
                        lPath = levelBase + "level2.tmx";
                        bossScreenX = 164*brickWidth;
                        bossScreenY = 21*brickWidth;
                        bossXR = 171*brickWidth;
                        bossXL = bossXR-30*brickWidth;
                        bossYU = 16*brickWidth;
                        bossYD= bossYU+25*brickWidth;
                        
                        break;
                    default:
                        skye = new GradientPaint(W / 2, H, Color.BLUE, W / 2, 0, Color.WHITE);
                        lPath = levelBase + "level1.tmx";
                        bossScreenX = 135*brickWidth;
                        bossScreenY = 11*brickWidth;
                        bossXL = 128*brickWidth;
                        bossXR = bossXL+30*brickWidth;
                        bossYU = 11*brickWidth;
                        bossYD = bossYU+ 15*brickWidth;                        
                        lPath = levelBase + "level1.tmx";
                        
                        break;
                }
                loadLevel(lPath);
                System.out.print("done.\n");

                levelMinX = levelMinY = 0;
                levelMaxX = tiles.get(tiles.size() - 1).x + brickWidth;
                levelMaxY = tiles.get(tiles.size() - 1).y + 2 * brickWidth;

                System.out.print("done.\n");
                try
                {
                    Thread.sleep(2000);
                } catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

                gameState = PLAYING;
                System.out.print("Playing.\n");
                repaint();
            } else if (gameState == PLAYING || gameState == BOSS)
            {
                vx *= (1 - .1);// friction
                if (Math.abs(vx) < friction)
                {
                    vx = 0;
                }
                if (right)
                {
                    vx += dVx;
                }
                if (left)
                {
                    vx -= dVx;
                }
                if(up)
                {
                    vy-=0.2;
                }
                if (jump && ground)
                {
                    vy -= bounce;
                    jump = false;
                    ground = false;
                }
                vy += grav;
                if (willCollide(0, vy))
                {
                    vy = 0;
                    ground = true;
                }
                if (willCollide(vx, 0))
                {
                    vx = 0;
                }

                for (int i = 0; i < numEnemies; i++)
                {
                    if (enemies[i].isAlive())
                    {
                        if (enemies[i].isIn(sp, vx, 0))
                        {
                            killPlayer();
                            break;
                        }
                        if (enemies[i].isIn(sp, 0, vy))
                        {
                            vy = -2 * bounce / 3;
                            enemies[i].kill();
                           
                        }
                    }
                }
                x += vx;
                y += vy;

                sp.iterate(Math.abs(vx));

                boolean tWCH, tWCV, tWF;
                for (int i = 0; i < numEnemies; i++)
                {
                    if (isOnScreen(enemies[i]))                    
                    {
                        if(enemies[i].isAlive())
                        {
                            tWCH = tWCV = tWF = false;
                            if (willCollideNPC(enemies[i].vx, -2, enemies[i]))
                            {
                                tWCH = true;
                            }
                            for (int j = 0; j < numEnemies; j++)
                            {
                                if ((i != j) && enemies[j].isAlive())
                                {
                                    if (enemies[j].isIn(enemies[i], 0, 0))
                                    {
                                        tWCH = true;
                                    }
                                }
                            }
                            if (willCollideNPC(0, enemies[i].vy, enemies[i]))
                            {
                                tWCV = true;
                            }
                            if (enemies[i].ground)
                            {
                                if (willFall(enemies[i]))
                                {
                                    tWF = true;
                                }                            
                            }
                            enemies[i].iterate(.5, tWCH, tWCV, tWF, 0, 0.1);
                        }
                        else
                        {
                            enemies[i].iterate(.5, false, true, false, 0, 0);
                        }
                    }
                }
                if (gameState == BOSS)
                {
                    switch(level)
                    {
                        case 1: 
                            skye = new GradientPaint(W / 2, H, Color.BLUE, W / 2, 0, Color.BLACK);
                            gnd = new Color(12,134,61);
                            skyeY = 820;
                            break;
                        case 2:
                            skye = new GradientPaint(W / 2, H, Color.RED, W / 2, 0, Color.BLACK);
                            gnd = new Color(12,134,61);
                            skyeY = 1020;
                            break;
                        default:
                            skye = new GradientPaint(W / 2, H, Color.BLUE, W / 2, 0, Color.BLACK);
                            break;
                    }
                    tWCH = tWCV = tWF = false;
                    if (willCollideNPC(enemies[bossIndex].vx, -2, enemies[bossIndex]))
                    {
                        tWCH = true;
                    }
                    for (int j = 0; j < numEnemies; j++)
                    {
                        if ((bossIndex != j) && enemies[j].isAlive())
                        {
                            if (enemies[j].isIn(enemies[bossIndex], 0, 0))
                            {
                                tWCH = true;
                            }
                        }
                    }
                    if (willCollideNPC(0, enemies[bossIndex].vy, enemies[bossIndex]))
                    {
                        tWCV = true;
                    }
                    if (enemies[bossIndex].ground)
                    {
                        if (willFall(enemies[bossIndex]))
                        {
                            tWF = true;
                        }
                    }
                    enemies[bossIndex].iterate(.5, tWCH, tWCV, tWF, 0, 0.1);
                }
                char tC = sp.getState();
                if (!ground)
                {
                    if ((tC == 'r') || (tC == 'j') || (tC == 'g'))
                    {
                        if (vy < 0)
                        {
                            sp.setState('j');
                        } else
                        {
                            sp.setState('g');
                        }
                    } else
                    {
                        if (vy < 0)
                        {
                            sp.setState('J');
                        } else
                        {
                            sp.setState('G');
                        }
                    }
                } else if (vx > 0)
                {
                    sp.setState('r');
                } else if (vx < 0)
                {
                    sp.setState('l');
                } else if (tC == 'r' || tC == 's' || tC == 'j')
                {
                    sp.setState('s');
                } else
                {
                    sp.setState('S');
                }
                sp.x = x;
                sp.y = y;

                if (x >= bossXL && x < bossXR && gameState != BOSS && y >= bossYU && y < bossYD)
                {
                    gameState = BOSS;
                    
                }
                repaint();
            } else if (gameState == JUSTDIED)
            {
                if (tickCounter > 0)
                {
                    tickCounter--;
                } else
                {
                    
                    gameState = PLAYING;
                    x = playerStartX;
                    y = playerStartY;
                    vx = vy = 0;
                    jump = false;
                }
                repaint();
            } else if (gameState == PASSEDBOSS)
            {
                if (tickCounter > 0)
                {
                    tickCounter--;
                } else
                {
                    if(level<=levelMax)
                    {
                        gameState = LOADLEVEL;
                        x = playerStartX;
                        y = playerStartY;
                        vx = vy = 0;
                        jump = false;
                    }
                    else
                    {
                        tickCounter = 300;
                        gameState = WON;
                                                                
                    }
                }
                repaint();
            } else if (gameState == GAMEOVER)
            {
                if (tickCounter > 0)
                {
                    tickCounter--;
                } else
                {
                                
                    loadLevel(levelBase + "level1.tmx");
                    gameState = INTRO;
                    lives = 2;
                }
                repaint();
            }
            else if (gameState == WON)
            {
                if (tickCounter > 0)
                {
                    tickCounter--;
                } else
                {
                    loadLevel(levelBase + "level1.tmx");
                    gameState = INTRO;
                    lives = 2;
                    level =1;
                }
                repaint();
            }            
            try
            {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e)
            {
                System.out.println(e.toString());
            }
        }
    }
// For now, only keyboard. Could generalize to mouse, etc.    

    public void handleInput(char code, KeyEvent e)
    {
        switch (code)
        {
            case 'p': // Key Pressed
                if (e.getKeyCode() == KeyEvent.VK_UP)
                {
                    up = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN)
                {
                    down = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                {
                    right = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT)
                {
                    left = true;
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    if (ground)
                    {
                        jump = true;
                    }
                }
                break;
            case 'r':  // Released
                if (e.getKeyCode() == KeyEvent.VK_UP)
                {
                    up = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_DOWN)
                {
                    down = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                {
                    right = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_LEFT)
                {
                    left = false;
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    if (ground)
                    {
                        jump = false;
                    }
                }
                break;
            case ('t'): // Typed
                if (gameState == INTRO)
                {
                    gameState = LOADLEVEL;
                }
                if (e.getKeyChar() == 'd')
                {
                    System.out.println("Player Coords ("+sp.x+","+sp.y+")");
                }                
                break;
        }
    }
// ********************** Level Loader ***************************

    public void loadLevel(String fName)
    {
        resetTiles();
        int currTileSetWidth = 0;
        int currTileSetHeight = 0;
        int currGID = 0;
        int currLayerIndex = -3;

        int tileSetCounter = 0;
        int layerCounter = 0;       

        XMLEvent event;
        try
        {
            XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream in = new FileInputStream(fName);
            XMLEventReader eventReader = inputFactory.createXMLEventReader(in);

            while (eventReader.hasNext())
            {
                event = eventReader.nextEvent();
                if (event.isStartElement())
                {
                    StartElement startElement = event.asStartElement();
                    Iterator<Attribute> attributes = startElement.getAttributes();
                    if (startElement.getName().getLocalPart() == "map")
                    {
                        System.out.println("Map File Detected...");
                        while (attributes.hasNext())
                        {
                            Attribute attribute = attributes.next();
                            switch (attribute.getName().toString())
                            {
                                case ("version"):
                                    break;
                                case ("orientation"):
                                    break;
                                case ("width"):
                                    mapW = Integer.parseInt(attribute.getValue());
                                    break;
                                case ("height"):
                                    mapH = Integer.parseInt(attribute.getValue());
                                    break;
                                case ("tilewidth"):
                                    mapTileW = Integer.parseInt(attribute.getValue());
                                    break;
                                case ("tileheight"):
                                    mapTileH = Integer.parseInt(attribute.getValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        System.out.println("Found " + mapW + "x" + mapH + " tile map with " + mapTileW + "x" + mapTileH + " pixel tiles.");
                    }
                    if (startElement.getName().getLocalPart() == "tileset")
                    {
                        System.out.println("Parsing Tileset " + tileSetCounter);
                        String tName = "";
                        while (attributes.hasNext())
                        {
                            Attribute attribute = attributes.next();
                            switch (attribute.getName().toString())
                            {
                                case ("firstgid"):
                                    currGID = Integer.parseInt(attribute.getValue());
                                    break;
                                case ("name"):
                                    tName = attribute.getValue();
                                    System.out.println("\tTileset Name = " + tName);
                                    break;
                                case ("tilewidth"):
                                    currTileSetWidth = Integer.parseInt(attribute.getValue());
                                    break;
                                case ("tileheight"):
                                    currTileSetHeight = Integer.parseInt(attribute.getValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        if (tName.equals("Sprites"))
                        {
                            spriteGID = currGID;
                        }
                    }
                    if (startElement.getName().getLocalPart() == "image")
                    {
                        System.out.println("Parsing image for tileset " + tileSetCounter);
                        String tileName = "";
                        int imageWidth = 480;
                        int imageHeight = 30;
                        while (attributes.hasNext())
                        {
                            Attribute attribute = attributes.next(); 
                            switch (attribute.getName().toString())
                            {
                                case ("source"):
                                    tileName = levelBase + attribute.getValue();
                                    break;
                                case ("width"):
                                    imageWidth = Integer.parseInt(attribute.getValue());
                                    break;
                                case ("height"):
                                    imageHeight = Integer.parseInt(attribute.getValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        try
                        {
                            BufferedImage tIm = ImageIO.read(new File(tileName));
                            tm.add(tIm, currGID - 1, imageWidth / currTileSetWidth, currTileSetWidth, currTileSetHeight);
                            System.out.println("\tAdded tileset " + tileName + ".\n\t" + imageWidth / currTileSetWidth + " tiles of width " + currTileSetWidth);
                        } catch (IOException e)
                        {
                            e.printStackTrace();
                        }
                        tileSetCounter++;
                    }
                    if (startElement.getName().getLocalPart() == "layer")
                    {
                        System.out.println("Parsing Layer " + layerCounter);
                        String tName = "";
                        while (attributes.hasNext())
                        {
                            Attribute attribute = attributes.next(); 
                            switch (attribute.getName().toString())
                            {
                                case ("name"):
                                    tName = attribute.getValue();
                                    switch (tName)
                                    {
                                        case "tiles":
                                            currLayerIndex = 0;
                                            break;
                                        case "bg1":
                                            currLayerIndex = 1;
                                            break;
                                        case "bg2":
                                            currLayerIndex = 2;
                                            break;
                                        case "bg3":
                                            currLayerIndex = 3;
                                            break;
                                        case "fg1":
                                            currLayerIndex = 4;
                                            break;
                                        case "spikes":
                                            currLayerIndex = 5;
                                            break;
                                        case "spriteLayer":
                                            currLayerIndex = -1;
                                            break;
                                        default:
                                            currLayerIndex = -2;
                                    }
                                    System.out.println("\tName of Layer = " + tName + " with index " + currLayerIndex);
                                    break;
                                case ("width"):
                                    System.out.println("\tWidth of Layer = " + attribute.getValue() + " units");
                                    break;
                                case ("height"):
                                    System.out.println("\tHeight of Layer = " + attribute.getValue() + " units");
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                    if (startElement.getName().getLocalPart() == "data")
                    {
                        System.out.println("Parsing Data for Layer " + layerCounter);
                        while (attributes.hasNext())
                        {
                            Attribute attribute = attributes.next(); 
                            switch (attribute.getName().toString())
                            {
                                case ("encoding"):
                                    System.out.println("\tEncoding = " + attribute.getValue());
                                    break;
                                default:
                                    break;
                            }
                        }
                        event = eventReader.nextEvent();
                        String stEvent = "";
                        while (!event.isEndElement())
                        {
                            stEvent = stEvent + event.toString().replace("\n", "").replace("\r", ""); // Tiled places extra newlines in csv portion
                            event = eventReader.nextEvent();
                        }
                        if (currLayerIndex == -1)
                        {
                            loadSprites(stEvent, mapW, mapH, mapTileW, mapTileH);
                        } else
                        {
                            System.out.println("Loading Layer with index "+currLayerIndex);
                            loadLayer(currLayerIndex, stEvent, mapW, mapH, mapTileW, mapTileH);
                        }
                        layerCounter++;
                    }
                }
            }
            tiles.sort();
            bg3.sort();
            bg2.sort();
            bg1.sort();
            fg1.sort();
            spikes.sort();
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (XMLStreamException e)
        {
            e.printStackTrace();
        }
// End of XML parsing        
    }

    public boolean loadLayer(int index, String data, int nX, int nY, int tW, int tH)
    {
        String[] tokens = data.split(",");
        if (tokens.length != nX * nY)
        {
            System.out.println("Error decoding file");
            return false;
        } else
        {
            int cnt = 0;
            int tmp = 0;
            for (int j = 0; j < nY; j++)
            {
                for (int i = 0; i < nX; i++)
                {
                    tmp = Integer.parseInt(tokens[cnt]);
                    if (tmp != 0)
                    {
                        //System.out.println("Added Tile "+(tmp-1)+" at (x,y) = ("+i+","+j+")"); 
                        TilePoint ttp = tm.getTile(tmp - 1);

                        Image tIm = createImage(ttp.getImage().getSource());
                        Tile tTile = new Tile(i * mapTileW, j * mapTileH, ttp.width, ttp.height, tmp - 1, tIm);
                        switch (index)
                        {
                            case 0:
                                tiles.add(tTile);
                                break;
                            case 1:
                                bg1.add(tTile);
                                break;
                            case 2:
                                bg2.add(tTile);
                                break;
                            case 3:
                                bg3.add(tTile);
                                break;
                            case 4:
                                fg1.add(tTile);
                                break;
                            case 5:
                                spikes.add(tTile);
                                break;
                            default:
                                
                                break;
                        }
                    }
                    cnt++;
                }
            }
            return true;
        }
    }

    public boolean loadSprites(String data, int nX, int nY, int tW, int tH)
    {
        // start sprite 
        System.out.println("Sprite GID = " + spriteGID);
        String[] tokens = data.split(",");
        if (tokens.length != nX * nY)
        {
            System.out.println("Error decoding file");
            return false;
        } else
        {
            int cnt = 0;
            int cntE = 0;
            numEnemies = 0;
            int tmp = 0;
            for (int k = 0; k < tokens.length; k++)
            {
                if (Integer.parseInt(tokens[cnt]) - spriteGID > 0)
                {
                    numEnemies++;
                }
                cnt++;
            }
            cnt = 0;

            enemies = new Enemy[numEnemies];
            for (int j = 0; j < nY; j++)
            {
                for (int i = 0; i < nX; i++)
                {
                    tmp = Integer.parseInt(tokens[cnt]) - spriteGID;

                    switch (tmp)
                    {
                        case 0:
                            x = playerStartX = i * mapTileW;
                            y = playerStartY = j * mapTileH;
                            break;
                        case 1:
                            enemies[cntE] = new Ghost(i * mapTileW, j * mapTileH, mapTileW, mapTileH, spritePath);
                            cntE++;
                            break;
                        case 2:
                            enemies[cntE] = new Dragon(i * mapTileW, j * mapTileH, 40, mapTileH, spritePath);
                            cntE++;
                            break;
                        case 3:
                            enemies[cntE] = new Mouse(i * mapTileW, j * mapTileH, 30, mapTileH, spritePath);
                            cntE++;
                            break;
                        case 4:
                            bossIndex = cntE;
                            enemies[cntE] = new Boss(i * mapTileW, j * mapTileH, 4*mapTileW, 2*mapTileH, spritePath + "Boss/");
                            cntE++;
                            break;
                        default:
                            break;
                    }
                    cnt++;
                }
            }
            return true;
        }
    }

// ******************* HELPER FUNCTIONS **********************
    public boolean willCollide(double dx, double dy)
    {
        
        int N = 3;
        
        int xPlayerLeft = Math.max(((int) (x / brickWidth)) - N, 0);
        int xPlayerRight = Math.min(((int) (x / brickWidth)) + N, (int) (tiles.get(tiles.size() - 1).x) / brickWidth);
       
        int uL = 0;
        while ((int) (tiles.get(uL).x / brickWidth) < xPlayerLeft)
        {
            uL++;
        }
        int uR = uL; 
        while (tiles.get(uR).x / brickWidth < xPlayerRight)
        {
            uR++;
        }
        uR = Math.min(uR, tiles.size() - 1); 

        for (int i = uL; i < uR; i++)
        {
            if (tiles.get(i).isIn(sp, dx, dy))
            {
                return true;
            }
        }

        xPlayerRight = Math.min(((int) (x / brickWidth)) + N, (int) (spikes.get(spikes.size() - 1).x) / brickWidth);
        uL = 0;
        while ((spikes.get(uL).x / brickWidth < xPlayerLeft) && (uL < spikes.size() - 1))
        
        {
            uL++;
        }
        uR = uL;
        while (spikes.get(uR).x / brickWidth < xPlayerRight)
        {
            uR++;
        }

        for (int i = uL; i < uR + 1; i++)
        {
            if (spikes.get(i).isIn(sp, dx, dy))
            {
                killPlayer();
                return true;
            }
        }
        return false;
    }

    public boolean willCollideNPC(double dx, double dy, Enemy e)
    {
        double scX = 0;
        double eX = e.x;

        if (eX < CX)
        {
            scX = CX;
        } else if (eX > levelMaxX - CX)
        {
            scX = levelMaxX - CX;
        } else
        {
            scX = eX;
        }

        int bSX = Math.min((int) ((scX - CX) / brickWidth), 0);
        int bFX = Math.min((int) ((scX + CX) / brickWidth) + 1, (int) (tiles.get(tiles.size() - 1).x) / brickWidth);
        int sX = 0;
        int fX = 0;
        while (tiles.get(sX).x / brickWidth < bSX)
        {
            sX++;
        }
        while (tiles.get(fX).x / brickWidth < bFX)
        {
            fX++;
        }

        for (int i = sX; i < fX; i++)
        {
            if (tiles.get(i).isIn(e, dx, dy))
            {
                return true;
            }
        }
        return false;
    }

    private void killPlayer()
    {
        if (sound)
        {
            
            System.out.println("DIE!");
        }
        lives--;
        x = playerStartX;
        y = playerStartY;
        if (lives >= 0)
        {
            gameState = JUSTDIED;
			
            tickCounter = 250;
        } else
        {
            gameState = GAMEOVER;
            level = 1;            
            tickCounter = 400;
            
        }
    }

    private void winPlayer()
    {
        x = y = 25;
        vx = vy = 0;
        jump = false;
        lives++;
    }

    private boolean willFall(Enemy e)
    {
        double d = e.w;
        if (e.vx < 0)
        {
            d *= -1;
        }
        if (!willCollideNPC(e.vx + d, 9, e))
        {
            return true;
        } else
        {
            return false;
        }
    }

    private boolean isOnScreen(Bosh b)
    {
        if ((b.x > x - 2 * CX) && (b.x < x + 2 * CX))
        {
            return true;
        } else
        {
            return false;
        }
    }

    private void drawLayer(Graphics g, TileLayer tl, int depth)
    {
        if (tl.size() > 0)
        {
            if (x < CX)
            {
                screenX = CX;
            } else if (x > levelMaxX - CX)
            {
                screenX = (levelMaxX - CX*(2-depth)) / depth; 
            } else
            {
                screenX = (x-CX*(1-depth)) / depth;
            }

            if (y > levelMaxY - CY)
            {
                screenY = Math.max(levelMaxY - CY, 0);
            } else
            {
                screenY = y;
            }
            int bSX = Math.min((int) ((screenX - CX) / brickWidth), 0);
            int bFX = Math.min((int) ((screenX + CX) / brickWidth) + 1, (int) (tl.get(tl.size() - 1).x) / brickWidth);
            int sX = 0;
            int fX = 0;
            while (tl.get(sX).x / brickWidth < bSX)
            {
                sX++;
            }
            while (tl.get(fX).x / brickWidth < bFX)
            {
                fX++;
            }
            //for (int i = sX; i <= fX; i++)
            for (int i = sX; i <= fX; i++)
            {
                tl.get(i).draw(g, screenX - CX, screenY - CY);
            }
        }
    }
    private void drawLayer(Graphics g, TileLayer tl, int depth,double x0, double y0)
    {
        if (tl.size() > 0)
        {
            screenX = (x0-CX*(1-depth)) / depth;
            screenY = y0;
            int bSX = Math.min((int) ((screenX - CX) / brickWidth), 0);
            int bFX = Math.min((int) ((screenX + CX) / brickWidth) + 1, (int) (tl.get(tl.size() - 1).x) / brickWidth);
            int sX = 0;
            int fX = 0;
            while (tl.get(sX).x / brickWidth < bSX)
            {
                sX++;
            }
            while (tl.get(fX).x / brickWidth < bFX)
            {
                fX++;
            }
            
            for (int i = sX; i <= fX; i++)
            {
                tl.get(i).draw(g, screenX - CX, screenY - CY);
            }
        }
    }
    
    private void resetTiles()
    {
        tiles = new TileLayer(10000);
        fg1 = new TileLayer(200);
        bg1 = new TileLayer(200);
        bg2 = new TileLayer(200);
        bg3 = new TileLayer(200);
        spikes = new TileLayer(200);
        tm = new TileManager(250);
    }
    
    
}
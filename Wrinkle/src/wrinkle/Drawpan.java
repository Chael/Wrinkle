/*
 * Drawpan.java
 *
 * Created on February 8, 2011, 9:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package wrinkle;

import java.awt.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.image.BufferedImage;
import java.awt.event.*;
//import java.awt.geom.*;
import java.awt.Graphics2D;
import java.util.Vector;


/**
 *
 * @author a.bresee
 */
class Drawpan extends JPanel implements KeyListener {

    
    Wrinkle wrinkle;
    Vector<Terrain> terrains;
    
    BufferedImage foreground;
    BufferedImage background1;
    BufferedImage background2;
    BufferedImage background3;
    BufferedImage buff;
    public Drawpan()
    {
        setIgnoreRepaint(true);
        addKeyListener(this);
        setFocusable(true);
        
        terrains=new Vector<Terrain>();
        
        background1 = new BufferedImage(Global.WinX,Global.WinY,BufferedImage.TYPE_INT_RGB);
        background2 = new BufferedImage(Global.WinX,Global.WinY,BufferedImage.TYPE_INT_RGB);
        background3 = new BufferedImage(Global.WinX,Global.WinY,BufferedImage.TYPE_INT_RGB);        
        foreground =  new BufferedImage(Global.WinX,Global.WinY,BufferedImage.TYPE_INT_RGB);
        buff =        new BufferedImage(Global.WinX,Global.WinY,BufferedImage.TYPE_INT_RGB);
        
        wrinkle=new Wrinkle(0,Global.WinY-200);
        
        terrains.add(new Terrain(0,Global.WinY-200+wrinkle.getHeight(),
                            400,400,Color.GREEN));
        terrains.add(new Terrain(500,Global.WinY-200+wrinkle.getHeight(),
                            300,400,Color.RED));
        terrains.add(new Terrain(300,400,100,100));
        terrains.add(new Terrain(200,200,50,50));
    }

    public void keyTyped(KeyEvent e)
    {
        
    }

    public void keyPressed(KeyEvent e)
    {
        int key=e.getKeyCode();
        if (key==KeyEvent.VK_SPACE)
        {
            wrinkle.jump();

        }
        else if(key == KeyEvent.VK_RIGHT)
        {
            wrinkle.goRight();
        }
        else if(key == KeyEvent.VK_LEFT)
        {
            wrinkle.goLeft();
        }

    }

    public void keyReleased(KeyEvent e)
    {
        int key=e.getKeyCode();
        if (key==KeyEvent.VK_RIGHT)
        {
            wrinkle.unGoRight();
        }
        else if(key == KeyEvent.VK_LEFT)
        {
            wrinkle.unGoLeft();
        }

    }

    void drawToBuff()
    {
        
        Graphics2D g = (Graphics2D)buff.createGraphics();
        //g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setStroke(new BasicStroke(5));
        g.setColor(Color.CYAN);
        
        g.fillRect(0,0,Global.WinX,Global.WinY);
        for(int i=0;i<terrains.size();++i)
        {
            terrains.get(i).draw(g);
        }
        wrinkle.draw(g);
        
    }

    void drawToPanel()
    {
        Graphics2D g=(Graphics2D)this.getGraphics();
        g.drawImage(buff, 0,0, this);
    }
    boolean go()
    {

        wrinkle.update(terrains);
        drawToBuff();
        drawToPanel();
        try
        {
            Thread.sleep(Global.timeStep);
        }

        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}


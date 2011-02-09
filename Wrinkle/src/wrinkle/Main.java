/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//A WET CUNT IS IN AN ELEPHANT
package wrinkle;
//import java.util.Random;
import java.awt.*;
//import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
//import java.lang.Math;

//import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.*;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.event.*;
import java.io.File;
import java.util.Vector;

import javax.sound.sampled.*;


class Global{
    static final int timeStep=16;
    static final int WinX=800;
    static final int WinY=600;
    static final int framecount=2;
    static final float gravity=0.005f;
    static Clip makeClip (String str) throws Exception
    {
        try
        {
            Clip clip;
            AudioInputStream as=AudioSystem.getAudioInputStream(
                                         new File(str));
            DataLine.Info info = new DataLine.Info(Clip.class, as.getFormat());
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(as);
            return clip;
        }
        catch(Exception e)
        {
            throw e;
        }
    }
    
}

abstract class Collidable
{
    float x;
    float y;
    float getX(){return x;}
    float getY(){return y;}
    abstract int getWidth();
    abstract int getHeight();
    
    
    Rectangle2D.Float bBox;
    public Rectangle2D.Float getbBox(){return bBox;}
    abstract void generateBoundingBox();
    abstract void draw(Graphics2D g);
    public boolean collidesWith(Vector<Collidable> v)
    {
        for(int i=0;i<v.size();++i)
        {
            if(collidesWith(v.get(i)))
            {
                return true;
            }
        }
        return false;
    }
    public boolean collidesWith(Collidable c)
    {
        generateBoundingBox();
        c.generateBoundingBox();
        boolean b=bBox.intersects(c.getbBox());
        //System.out.println(b);
        return b;
        
    }
            
}
class Terrain extends Collidable
{
   int width;
   int height;
   
   Color color;
   
   int getWidth(){return width;}
   int getHeight(){return height;}
    
   void generateBoundingBox(){}
   void draw(Graphics2D g)
   {
       g.setColor(color); 
       g.setStroke(new BasicStroke(5));
       g.fillRect((int)x,(int)y,width,height);     
   }
   Terrain()
   {
       this(0,0,10,10,Color.GRAY);
   }
   Terrain(int X, int Y, int Width, int Height)
   {
       this(X,Y,Width,Height,Color.GRAY);
   }
   
   Terrain(int X, int Y, int Width, int Height, Color c)
   {
       x=X;
       y=Y;
       width=Width;
       height=Height;
       color=c;
       bBox=new Rectangle2D.Float(x,y,width,height);
   }
}

abstract class Actor extends Collidable{
    
    
    float velX;
    float velY;
    float accelX;
    float accelY;
    float maxVelX;
    float maxVelY;
    
    protected BufferedImage cursprite;
    protected BufferedImage rightwalk[];
    protected BufferedImage leftwalk[];
    protected BufferedImage rightidle;
    protected BufferedImage leftidle;
    protected BufferedImage rightjump;
    protected BufferedImage leftjump;
    
    void generateBoundingBox()
    {
      bBox=new Rectangle2D.Float(x,y,cursprite.getWidth(),cursprite.getHeight());  
    }
}


   
class Wrinkle extends Actor{
    float velX;
    float velY;
    float accelX;
    float accelY;
    float maxVelX;
    float maxVelY;
    
    int frametime=5;
    int timecount=0;
    int frame=0;

    boolean onTheGround;
    boolean goingRight;
    boolean goingLeft;
    boolean facingLeft;
    boolean insideOkay;
    
    boolean soundImplemented;

    Clip jumpsnd;
    Clip walk1;
    Clip walk2;
    Clip land;
    
    ///////////////////////
    //INITIALIZATION CODE//
    ///////////////////////    
    
    //default ctor calls parametered ctor//    
    Wrinkle(){this(0,0);}
    
    //parametered ctor takes pixel coords on where top left corner should be//
    Wrinkle(int X, int Y)
    {
        initPhys(X,Y);
        
        onTheGround=true;
        insideOkay=false;
        goingRight=false;
        goingLeft=false;
        facingLeft=false;
        
        initSounds(); 
        initImages();
                    
        cursprite=rightidle;
    }
    
    //called by ctor to init position, velocity, and acceleration
    void initPhys(int X, int Y)
    {
        x=X;
        y=Y;

        velX=0;
        velY=0;

        maxVelX=0.5f;
        maxVelY=0.6f;

        accelX=0;
        accelY=Global.gravity;     
    }
    
    //called by ctor to init sprites
    void initImages()
    {
        String prefix="Data/images/hero/";
        rightwalk=new BufferedImage[Global.framecount];
        leftwalk=new BufferedImage[Global.framecount];
        try
        {
            rightidle=ImageIO.read(new File(prefix+"rightidle.png"));
            leftidle=ImageIO.read(new File(prefix+"leftidle.png"));
            String name="rightwalk";
            
            for(int i=0;i<Global.framecount;++i)
            {              
              rightwalk[i]=ImageIO.read(new File(prefix+name+i+".png"));
            }
            
            name="leftwalk";
            for(int i=0;i<Global.framecount;++i)
            {
                leftwalk[i]=ImageIO.read(new File(prefix+name+i+".png"));
            }
            
            rightjump=ImageIO.read(new File(prefix+"rightjump.png"));
            leftjump=ImageIO.read(new File(prefix+"leftjump.png"));
        
        }
        catch(Exception e){e.printStackTrace();}
    }
    void initSounds()
    {
        
        String prefix="Data/audio/";
        
        try
        {
            jumpsnd=Global.makeClip(prefix+"jump.wav");
            walk1=Global.makeClip(prefix+"walk1.wav");
            walk2=Global.makeClip(prefix+"walk2.wav");
            land=Global.makeClip(prefix+"land.wav");
            soundImplemented=true;
        }
        catch(Exception e)
        {
        e.printStackTrace();
        }
        
    }
    void jump()
    {
       
        if(onTheGround)
        {
            playClip(jumpsnd);
            velY=-1.5f;
            accelY=Global.gravity;
            onTheGround=false;
            if(facingLeft)
            {
                cursprite=leftjump;
            }
            else
            {
                cursprite=rightjump;
            }
        }
    }

    void goRight()
    {
        
        if(!goingRight)
        {
            cursprite=rightwalk[0];
            frame=0;
            timecount=0;
            facingLeft=false;
            goingRight=true;
            goingLeft=false;
                       
        }     

    }
    void unGoRight()
    {
        goingRight=false;
       
    }
    void goLeft()
    {
        if(!goingLeft)
        {
            cursprite=leftwalk[0];
            frame=0;
            timecount=0;
            facingLeft=true;
            goingLeft=true;
            goingRight=false;
            
            
        }      
    }
    void unGoLeft()
    {
        goingLeft=false;
    }
    void playClip(Clip clip)
    {
        if(soundImplemented)
        {
        
        clip.setFramePosition(0);
        clip.start();
        }
        
    }
    void updateVel()
    {
        //user is holding right, so
        //accelerate the character right
        if(goingRight)
        {
            accelX=(velX<0)?0.002f:0.001f;            
        }
        
        //user is holding left, so
        //accelerate the character left
        else if(goingLeft)
        {
           accelX=(velX>0)?-0.002f:-0.001f;          
        }
        else
        {
            //user is not holding an arrow key, apply horizontal friction
            if(Math.abs(velX*Global.timeStep)<=1.0f)
            {
                //velocity amounts to less than 1 pixel change per time step
                //so lets just forget about velocity shall we?
                accelX=0;
                velX=0;
            }
            else if(onTheGround)
            {
                accelX=-0.009f*velX;
            }
            else
            {
                accelX=-0.0009f*velX;
            }                
        }
        if(Math.abs(velX)>maxVelX)
        {
            velX=(velX<0)?-maxVelX:maxVelX;
        }
                
        velX+=accelX*Global.timeStep;
        
        velY+=accelY*Global.timeStep;     
    }
          
    void update(Vector<Terrain> terrains)
    {
        
        updateVel();       
        
        float delx=velX*Global.timeStep;        
        x+=delx;
        
       
        //if(collidesWith(terrains))
        if(!insideOkay)
        {
            for(int i=0;i<terrains.size();++i)
            {              
                if(collidesWith(terrains.get(i)))
                {
                    x-=delx;
                    if(onTheGround)
                    {
                        float otherx=terrains.get(i).getX();
                        x=terrains.get(i).getX()+((x<otherx)?-cursprite.getWidth()
                                    :terrains.get(i).getWidth());
                    }
                    break;
                }
            }
        }
        
        float dely=velY*Global.timeStep;        
        y+=dely;
        boolean bk=false;
        for(int i=0;i<terrains.size();++i)
        {
            if(collidesWith(terrains.get(i)))
            {
                
                if(velY>0)
                {
                  if(!insideOkay)
                  {
                       if(!onTheGround)
                       {
                        onTheGround=true;
                        playClip(land);
                       }
                       velY = 0;
                       y=terrains.get(i).getY()-cursprite.getHeight();
                  }
                  else
                  {
                      bk=true;
                      break;
                  }
                }
                else
                {
                    insideOkay=true;
                    bk=true;
                    break;
                }               

            }
        }
        if(!bk)
        {
            insideOkay=false;
        }
        
        
        
        if((Math.abs(velX)>.001f)&&onTheGround)
        {
                   
            if(timecount==frametime)
            {
                
                timecount=0;
                if(frame%2==0)
                {
                    playClip(walk1);
                   
                }
                else
                {
                    playClip(walk2);
                    
                }
                cursprite=(facingLeft)?leftwalk[frame]:rightwalk[frame]; 
                
                frame=(frame<Global.framecount-1)?(frame+1):0;
               
            }
            else
            {
                ++timecount;
            }
        }
        if(velX==0&&onTheGround)
        {
            cursprite=(facingLeft)?leftidle:rightidle;
        }
       //System.out.println("x: "+x+"\ny: "+y);
       //System.out.println("velX:"+velX+"\nvelY: "+velY);
    }
    
    void draw(Graphics2D g)
    {
        g.drawImage(cursprite,(int)Math.round(x),(int)Math.round(y),null);
    }
    int getHeight(){return cursprite.getHeight();}
    int getWidth(){return cursprite.getWidth();}
    BufferedImage getImage(){return cursprite;}

}

class Block{}


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
        
        background1=new BufferedImage(Global.WinX,Global.WinY,BufferedImage.TYPE_INT_RGB);
        background2=new BufferedImage(Global.WinX,Global.WinY,BufferedImage.TYPE_INT_RGB);
        background3=new BufferedImage(Global.WinX,Global.WinY,BufferedImage.TYPE_INT_RGB);        
        foreground=new BufferedImage(Global.WinX,Global.WinY,BufferedImage.TYPE_INT_RGB);
        buff=new BufferedImage(Global.WinX,Global.WinY,BufferedImage.TYPE_INT_RGB);
        
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


class Game
    {
    JFrame window;
    Drawpan pan;
   

    
    Game()
    {
        
        window=new JFrame("Wrinkle... th-t-the Dinosaur!");

        pan=new Drawpan();
        
        window.addKeyListener(pan);
        window.add(pan);
        window.getContentPane().add(pan);
        window.setSize(Global.WinX,Global.WinY);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);    
      
        
     
    }
    void loop()
    {
        while(pan.go())
        {
           
        }
    }

}
public class Main {  
    
    public static void main(String[] args) {
       Game g=new Game();
       g.loop();
    }

}

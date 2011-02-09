/*
 * Wrinkle.java
 *
 * Created on February 8, 2011, 8:56 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package wrinkle;

import java.awt.image.BufferedImage;
import java.awt.geom.*;
import java.awt.Graphics2D;

import javax.sound.sampled.*;
import java.io.File;
import java.util.Vector;
import javax.imageio.ImageIO;



/**
 *
 * @author a.bresee
 */
class Wrinkle extends Actor{
    
    
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


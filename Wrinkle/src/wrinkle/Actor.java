/*
 * Actor.java
 *
 * Created on February 8, 2011, 8:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package wrinkle;

import java.awt.image.BufferedImage;
import java.awt.geom.*;
/**
 *
 * @author a.bresee
 */
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

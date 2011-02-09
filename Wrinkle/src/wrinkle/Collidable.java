/*
 * Collidable.java
 *
 * Created on February 8, 2011, 8:38 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package wrinkle;

import java.awt.geom.*;
import java.awt.Graphics2D;

/**
 *
 * @author a.bresee
 */
abstract class Collidable
{
    float x;
    float y;
    float getX(){return x;}
    float getY(){return y;}
    abstract int getWidth();
    abstract int getHeight();
    
    
    Rectangle2D bBox;
    public Rectangle2D getbBox(){return bBox;}
    abstract void generateBoundingBox();
    abstract void draw(Graphics2D g);
    
    public boolean collidesWith(Collidable c)
    {
        generateBoundingBox();
        c.generateBoundingBox();
        boolean b=bBox.intersects(c.getbBox());
        return b;
        
    }
            
}

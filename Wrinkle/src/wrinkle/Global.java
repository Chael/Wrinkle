/*
 * Global.java
 *
 * Created on February 8, 2011, 8:36 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package wrinkle;
import javax.sound.sampled.*;
import java.io.File;



/**
 *
 * @author a.bresee
 */
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

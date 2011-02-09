/*
 * Game.java
 *
 * Created on February 8, 2011, 9:12 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */


package wrinkle;


import javax.swing.JFrame;
/**
 *
 * @author a.bresee
 */
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
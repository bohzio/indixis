package server;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 * Classe per gestire la scroolbar - not use - future implementation
 * @author Francesco-Taioli
 */
public class MyScrollBarUI  extends BasicScrollBarUI {

     @Override
     
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
       g.setColor(Color.green);
       c.setPreferredSize(new Dimension(20,20));
       trackBounds.setBounds(thumbRect);
         System.out.println("sfsafsf");
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
           Color newColor = new Color(144,144,144);
    }
    
     @Override
     
    protected JButton createDecreaseButton(int orientation) {
        JButton button = super.createDecreaseButton(orientation);
         Color newColor = new Color(144,144,144);
        button.setBackground(newColor);
        return button;
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        JButton button = super.createIncreaseButton(orientation);
         Color newColor = new Color(144,144,144);
        button.setBackground(newColor);
        return button;
    }
     
}

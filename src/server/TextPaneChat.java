/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

public class TextPaneChat
{
    private static void createAndShowGUI()
    {
        JTextPane textPane = new JTextPane();

        StyledDocument doc = textPane.getStyledDocument();

        SimpleAttributeSet left = new SimpleAttributeSet();
        StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(left, Color.RED);

        SimpleAttributeSet right = new SimpleAttributeSet();
        StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setForeground(right, Color.BLUE);

        try
        {
            doc.insertString(doc.getLength(), "Are you busy tonight?", left );
            doc.setParagraphAttributes(doc.getLength(), 1, left, false);
            doc.insertString(doc.getLength(), "\nNo", right );
            doc.setParagraphAttributes(doc.getLength(), 1, right, false);
            doc.insertString(doc.getLength(), "\nFeel like going to a movie?", left );
            doc.setParagraphAttributes(doc.getLength(), 1, left, false);
            doc.insertString(doc.getLength(), "\nSure", right );
            doc.setParagraphAttributes(doc.getLength(), 1, right, false);
        }
        catch(Exception e) { System.out.println(e); }

        JFrame frame = new JFrame("Text Pane Chat");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add( new JScrollPane( textPane ) );
        frame.setLocationByPlatform( true );
        frame.setSize(200, 200);
        frame.setVisible( true );
    }

    public static void main(String[] args)
    {
        EventQueue.invokeLater( () -> createAndShowGUI() );
/*
        EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                createAndShowGUI();
            }
        });
*/
    }
}
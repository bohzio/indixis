package server;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

class ChatPaneMsgBox extends JPanel {

    boolean position = true;
    private String mexOrPath = "";
    private JLabel jl;
    private ImageIcon newIcon;

    public ChatPaneMsgBox(String mexOrPath, boolean position, TypeMessage type) {
        this.position = position;
        this.mexOrPath = mexOrPath;
        System.out.println("mexOrPath" + mexOrPath);
        if (type == TypeMessage.MESSAGGIO) {
            messagePanel();
        }
        else if (type == TypeMessage.FOTO) {
            ImageIcon img = new ImageIcon(mexOrPath);
            Image Imageimg = img.getImage();
            Image newimg = Imageimg.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH);
            newIcon = new ImageIcon(newimg);
            fotoPanel();
        }
        else if (type == TypeMessage.FILE){
            filePanel();
        }
    }

    private void messagePanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        if (position) {
            jl = new JLabel(mexOrPath, SwingConstants.RIGHT);

            jl.setOpaque(true);
            jl.setBorder(new TextBubbleBorder(Color.LIGHT_GRAY, 1, 10, 7, position));

            jl.setBackground(new Color(238, 238, 238));
            jl.setAlignmentX(1);

        } else {
            jl = new JLabel(mexOrPath);
            jl.setOpaque(true);
            jl.setBorder(new TextBubbleBorder(Color.LIGHT_GRAY, 1, 10, 7, position));

            jl.setBackground(new Color(129, 199, 132));
            jl.setAlignmentX(0);
        }
        this.add(jl);
        this.revalidate();
        this.repaint();
    }

    private void fotoPanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        if (position) {

            jl = new JLabel(newIcon, SwingConstants.RIGHT);

            jl.setOpaque(true);
            jl.setBorder(new TextBubbleBorder(Color.LIGHT_GRAY, 1, 10, 7, position));

            jl.setBackground(new Color(238, 238, 238));
            jl.setAlignmentX(1);

        } else {
            jl = new JLabel(newIcon);
            jl.setOpaque(true);
            jl.setBorder(new TextBubbleBorder(Color.LIGHT_GRAY, 1, 10, 7, position));

            jl.setBackground(new Color(129, 199, 132));
            jl.setAlignmentX(0);
        }
        this.add(jl);
        this.revalidate();
        this.repaint();
    }

    private void filePanel() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        if (position) {
            jl = new JLabel("<html>E' stato ricevuto un file!<br>Clicca questo messaggio per aprirlo</html>", SwingConstants.RIGHT);

            jl.setOpaque(true);
            jl.setBorder(new TextBubbleBorder(Color.LIGHT_GRAY, 1, 10, 7, position));

            jl.setBackground(new Color(238, 238, 238));
            jl.setAlignmentX(1);

        } else {
            jl = new JLabel("<html>E' stato inviato un file!<br>Clicca questo messaggio per aprirlo</html>");
            jl.setOpaque(true);
            jl.setBorder(new TextBubbleBorder(Color.LIGHT_GRAY, 1, 10, 7, position));

            jl.setBackground(new Color(129, 199, 132));
            jl.setAlignmentX(0);
        }
        jl.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent evt) {
                try {
                    File file = new File(mexOrPath);
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    System.err.println("Errore di I/O");
                }
            }
        });
        this.add(jl);
        this.revalidate();
        this.repaint();
    }

}

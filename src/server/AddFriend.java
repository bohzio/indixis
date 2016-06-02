package server;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

/**
 *
 * @author michele
 */
public class AddFriend extends javax.swing.JFrame {
    
    private int numFriend;
    private int numUtenti;
    /**
     * Creates new form AggiungiAmicp
     * 
     */
    public AddFriend() {
        System.out.println(listaUtenti);
        this.numFriend = (ChatGUi.ar == null ? 0: ChatGUi.ar.size());
        this.numUtenti = (listaUtenti == null ? 0: listaUtenti.size());
        if(viewResponse()){
            initComponents();
            setElementInPanels();
            setFont();
            setPosition();
        }
    }
    
    /**
     * setta i il font material di google- roboto-thin.ttf
     */
    private void setFont() {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("Roboto-thin.ttf"));
            titolo.setFont(font.deriveFont(Font.BOLD, 30f));
        } catch (FontFormatException ex) {
            Logger.getLogger(ChatGUi.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ChatGUi.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    private void setPosition(){
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);
    }
    
    private boolean viewResponse() {
        if ((numUtenti - numFriend) > 0) {
            setVisible(true);
            return true;
        } else{
            JOptionPane showMessage = new JOptionPane();
            showMessage.showMessageDialog(null, "Non ci sono utenti iscritti  a questa bellissima chat");
            setVisible(false);
        }
        return false;
    }
    
    private ArrayList getUsers(){
        int numFriend = (ChatGUi.ar == null ? 0: ChatGUi.ar.size());
        ArrayList<String> ris = null;
        
        if ((numUtenti - numFriend) > 0) {
            ris = new ArrayList();
            for (int i = 0; i < listaUtenti.size(); i++) {
                if (isMyFriend((String) listaUtenti.get(i)) && !Connection.getUsername().equals(listaUtenti.get(i))) {
                    ris.add((String) listaUtenti.get(i));
                }
            }
        }
        return ris;
    }
    
    private boolean isMyFriend(String utente) {
        for (int i = 0; i < numFriend; i++) {
            System.out.println(Connection.getUsername().equals(utente));
            if (ChatGUi.ar.get(i).equals(utente)) {
                return false;
            }
        }
        return true;
    }
    
    private void setElementInPanels() {
        sectionUsername.removeAll();sectionRequest.removeAll();
        sectionUsername.revalidate();sectionRequest.revalidate();
        sectionUsername.repaint();sectionRequest.repaint();
        ArrayList<String> variousUser = getUsers();
        for(String name: variousUser){
            JLabel username = new JLabel();
            JLabel userRequest = new JLabel();
            username.setBackground(new java.awt.Color(255, 255, 255));
            username.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
            username.setForeground(new java.awt.Color(0, 0, 153));
            username.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            username.setText(name);
            username.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            username.setMaximumSize(new java.awt.Dimension(200, 30));
            username.setMinimumSize(new java.awt.Dimension(200, 30));
            username.setOpaque(true);
            username.setPreferredSize(new java.awt.Dimension(200, 30));
            
            userRequest.setBackground(new java.awt.Color(153, 153, 255));
            userRequest.setFont(new java.awt.Font("Tahoma", 2, 12)); // NOI18N
            userRequest.setForeground(new java.awt.Color(0, 51, 102));
            userRequest.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            userRequest.setText("Aggiungi "  + name);
            userRequest.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            userRequest.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            userRequest.setMaximumSize(new java.awt.Dimension(200, 30));
            userRequest.setMinimumSize(new java.awt.Dimension(200, 30));
            userRequest.setOpaque(true);
            userRequest.setPreferredSize(new java.awt.Dimension(200, 30));
            userRequest.addMouseListener(new MouseAdapter(){
                @Override
                public void mouseReleased(MouseEvent evt) {
                    if(userRequest.getText().equals("Aggiungi " + name)){
                        Connection.sendFriendRequest(name);
                        Ricezione.listaUtenti.remove(name);
                        listaUtenti.remove(name);
                        setElementInPanels();
                        ChatGUi.setNumberOfRequest();
                    }
                }
            });
            
            sectionUsername.add(username);
            sectionRequest.add(userRequest);
        }
    }
    
    
    
    public static void setListUser(ArrayList newUsers){
        AddFriend.listaUtenti = newUsers;
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        root = new javax.swing.JPanel();
        panelTitolo = new javax.swing.JPanel();
        containerTitle = new javax.swing.JPanel();
        titolo = new javax.swing.JLabel();
        panelUser = new javax.swing.JPanel();
        sectionUsername = new javax.swing.JPanel();
        sectionRequest = new javax.swing.JPanel();

        setTitle("Aggiungi amico");
        setMinimumSize(new java.awt.Dimension(400, 300));
        setResizable(false);

        root.setBackground(new java.awt.Color(255, 255, 255));

        panelTitolo.setBackground(new java.awt.Color(255, 255, 255));
        panelTitolo.setMaximumSize(new java.awt.Dimension(400, 50));
        panelTitolo.setMinimumSize(new java.awt.Dimension(400, 50));
        panelTitolo.setPreferredSize(new java.awt.Dimension(400, 50));

        containerTitle.setBackground(new java.awt.Color(33, 150, 243));

        titolo.setBackground(new java.awt.Color(33, 150, 243));
        titolo.setFont(new java.awt.Font("Microsoft YaHei UI Light", 1, 24)); // NOI18N
        titolo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titolo.setText("Aggiungi un amico");
        titolo.setPreferredSize(new java.awt.Dimension(400, 50));

        javax.swing.GroupLayout containerTitleLayout = new javax.swing.GroupLayout(containerTitle);
        containerTitle.setLayout(containerTitleLayout);
        containerTitleLayout.setHorizontalGroup(
            containerTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(containerTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titolo, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        containerTitleLayout.setVerticalGroup(
            containerTitleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(containerTitleLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titolo, javax.swing.GroupLayout.DEFAULT_SIZE, 32, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelTitoloLayout = new javax.swing.GroupLayout(panelTitolo);
        panelTitolo.setLayout(panelTitoloLayout);
        panelTitoloLayout.setHorizontalGroup(
            panelTitoloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(containerTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelTitoloLayout.setVerticalGroup(
            panelTitoloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(containerTitle, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        JScrollPane scrollPane = new JScrollPane(panelUser);
        root.add(scrollPane);
        panelUser.setBackground(new java.awt.Color(255, 255, 255));
        panelUser.setMaximumSize(new java.awt.Dimension(400, 300));
        panelUser.setMinimumSize(new java.awt.Dimension(400, 300));
        panelUser.setPreferredSize(new java.awt.Dimension(400, 300));

        sectionUsername.setBackground(new java.awt.Color(255, 255, 255));
        sectionUsername.setMaximumSize(new java.awt.Dimension(200, 300));
        sectionUsername.setMinimumSize(new java.awt.Dimension(200, 300));
        sectionUsername.setPreferredSize(new java.awt.Dimension(200, 300));
        sectionUsername.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

        sectionRequest.setBackground(new java.awt.Color(255, 255, 255));
        sectionRequest.setMaximumSize(new java.awt.Dimension(200, 300));
        sectionRequest.setMinimumSize(new java.awt.Dimension(200, 300));
        sectionRequest.setPreferredSize(new java.awt.Dimension(200, 300));
        sectionRequest.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

        javax.swing.GroupLayout panelUserLayout = new javax.swing.GroupLayout(panelUser);
        panelUser.setLayout(panelUserLayout);
        panelUserLayout.setHorizontalGroup(
            panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelUserLayout.createSequentialGroup()
                .addComponent(sectionUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(sectionRequest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelUserLayout.setVerticalGroup(
            panelUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(sectionUsername, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(sectionRequest, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout rootLayout = new javax.swing.GroupLayout(root);
        root.setLayout(rootLayout);
        rootLayout.setHorizontalGroup(
            rootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelTitolo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        rootLayout.setVerticalGroup(
            rootLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(rootLayout.createSequentialGroup()
                .addComponent(panelTitolo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelUser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(root, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel containerTitle;
    private javax.swing.JPanel panelTitolo;
    private javax.swing.JPanel panelUser;
    private javax.swing.JPanel root;
    private javax.swing.JPanel sectionRequest;
    private javax.swing.JPanel sectionUsername;
    private javax.swing.JLabel titolo;
    // End of variables declaration//GEN-END:variables
    private static ArrayList listaUtenti;
}

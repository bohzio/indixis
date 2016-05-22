package server;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 *
 * @author michele
 */
public class AddFriend extends javax.swing.JFrame {
    
    /**
     * Creates new form AggiungiAmicp
     * @param listaUtenti
     */
    public AddFriend(ArrayList listaUtenti) {
        this.listaUtenti = listaUtenti;
        if(viewResponse()){
            initComponents();
            setElementInPanels();
        }
    }
    
    private boolean viewResponse() {
        System.out.println("Risposta: " + (listaUtenti.size() - ChatGUi.ar.size()) + ", " + listaUtenti.size() + ", " +  ChatGUi.ar.size());
        if ((listaUtenti.size() - ChatGUi.ar.size()) > 0) {
            setVisible(true);
            return true;
        } else{
            JOptionPane showMessage = new JOptionPane();
            showMessage.showMessageDialog(null, "Non ci sono utenti online che non siano tuoi amici");
            setVisible(false);
        }
        return false;
    }
    
    private ArrayList getUsers(){
        ArrayList<String> ris = null;
        if ((listaUtenti.size() - ChatGUi.ar.size()) > 0) {
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
        for (int i = 0; i < ChatGUi.ar.size(); i++) {
            System.out.println(Connection.getUsername().equals(utente));
            if (ChatGUi.ar.get(i).equals(utente)) {
                return false;
            }
        }
        return true;
    }
    
    private void setElementInPanels() {
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
                public void mousePressed(MouseEvent evt) {
                    
                }

                @Override
                public void mouseReleased(MouseEvent evt) {
                    if(userRequest.getText().equals("Aggiungi " + name)){
                        Connection.sendFriendRequest(name);
                        userRequest.setText("Richiesta inviata a " + name);
                        userRequest.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
                    }
                }
            });
            
            sectionUsername.add(username);
            sectionRequest.add(userRequest);
        }
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
        titolo = new javax.swing.JLabel();
        panelUser = new javax.swing.JPanel();
        sectionUsername = new javax.swing.JPanel();
        sectionRequest = new javax.swing.JPanel();

        setTitle("Aggiungi amico");
        setMaximumSize(new java.awt.Dimension(400, 300));
        setMinimumSize(new java.awt.Dimension(400, 300));
        setPreferredSize(new java.awt.Dimension(400, 350));
        setResizable(false);

        root.setBackground(new java.awt.Color(255, 255, 255));

        panelTitolo.setBackground(new java.awt.Color(255, 255, 255));
        panelTitolo.setMaximumSize(new java.awt.Dimension(400, 50));
        panelTitolo.setMinimumSize(new java.awt.Dimension(400, 50));
        panelTitolo.setPreferredSize(new java.awt.Dimension(400, 50));

        titolo.setFont(new java.awt.Font("Microsoft YaHei UI Light", 1, 24)); // NOI18N
        titolo.setForeground(new java.awt.Color(255, 0, 0));
        titolo.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titolo.setText("Aggiungi un amico");
        titolo.setPreferredSize(new java.awt.Dimension(400, 50));

        javax.swing.GroupLayout panelTitoloLayout = new javax.swing.GroupLayout(panelTitolo);
        panelTitolo.setLayout(panelTitoloLayout);
        panelTitoloLayout.setHorizontalGroup(
            panelTitoloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(titolo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        panelTitoloLayout.setVerticalGroup(
            panelTitoloLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(titolo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    private javax.swing.JPanel panelTitolo;
    private javax.swing.JPanel panelUser;
    private javax.swing.JPanel root;
    private javax.swing.JPanel sectionRequest;
    private javax.swing.JPanel sectionUsername;
    private javax.swing.JLabel titolo;
    // End of variables declaration//GEN-END:variables
    private ArrayList listaUtenti;
}

package server;

 

import server.Messaggio;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 
 * @author dallachiaram@gmail.com
 * @version 1.5
 */
public class UserHandler extends Thread {

    protected UserObject datiPersonali;
    protected String userName;
    protected String password;
    protected Socket socket;
    protected ObjectInputStream is;
    protected ObjectOutputStream os;
    private boolean continua = true;
    private ArrayList inputClient;
    private static Connection connection;
    private boolean login;
    
    /**
     * Costruttore UserHandler
     * @param socket
     * @throws IOException 
     */
    public UserHandler(Socket socket) throws IOException {
        this.socket = socket;
        datiPersonali = new UserObject();
        is = new ObjectInputStream(socket.getInputStream()); //is = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        os = new ObjectOutputStream(socket.getOutputStream()); // os = new PrintWriter(socket.getOutputStream(), true);

        start(); 
    }

    // ********** GESTIONE RUNNING THREAD ********** //
    @Override
    public void run() {
        try {
            ArrayList risp = new ArrayList();
            // Legge l'array username
            ArrayList tmp = (ArrayList) is.readObject();
            if (tmp.get(0).equals("NEW-REG")){
                if (registrazione(tmp)){
                    ArrayList registrazione = new ArrayList();
                    registrazione.add("REG-RIUSCITA");
                    outputSocket(registrazione);
                }
            }else{
            datiPersonali = leggiDatiPersonali(tmp); //datiPersonali è un UserObject

            // faccio il login
            if (autentica(datiPersonali.getUserName(), datiPersonali.getPassword())) {
                ArrayList richieste;
                // se si autentica lo aggiungiamo alla lista degl utenti connessi
                Server.utentiConnessi.add(datiPersonali);
                // e settiamo il nome del thread a quello dello username
                this.setName(datiPersonali.getUserName());
                // inviamo la conferma dell'autenticazione all'utente
                risp.add("AUT-R");
                outputSocket(risp);
                inviaNuovoUtenteAdAltri(datiPersonali.getUserName());
                System.out.println("Username: " + datiPersonali.getUserName());
                
                createDirFile();
                // ciclo fintanto che la connessione con il client rimane in piedi
                
                while (continua) {
                    try{
                        richieste = (ArrayList) is.readObject();
                        System.out.println((String)richieste.get(0));
                        switch((String)richieste.get(0)){
                            case "LU-REQ":
                                outputSocket(listaUtenti());
                                break;
                            case "MEX-OUT":
                                reindirizzaMessaggio(richieste);
                                break;
                            case "FILE-OUT":
                                reindirizzaFile(richieste);
                                break;
                            case "LIST-MEX":
                                outputSocket(reindirizzaListaMex());
                                break;
                            case "FRIEND-REQ":
                                sendFriendRequest((String)richieste.get(1));
                                break;
                            case "ACCEPT-FRIEND":
                                acceptFriendRequest((String)richieste.get(1));
                                break;
                            case "FRIENDS-LIST-REQ":
                                outputSocket(getFriendsList());
                                break;
                            case "REMOVE-FRIEND":
                                removeFriend((String)richieste.get(1));
                        }
                    } catch (Exception e) {
                        continua = false;
                        termina();

                        }
                    } // fine del ciclo del thread
                }
                else {
                    try {
                        termina();
                    }
                    catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            continua = false;
        }

    }
    
    /**
     * 
     * @param username
     * @param password
     * @return 
     */
    public synchronized boolean autentica(String username, String password) {
        login = false;
        try {
            String SQL = "SELECT * FROM users WHERE username='" + username + "' && password='" + password+ "';";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            // Check Username and Password
            int i = 0;
            
            while (rs.next()) {
                i++;
            }
            
            if (i == 1) login = true;
            
            stmt.close();
            
        } catch (SQLException ex) {
            System.out.println("Errore query login --> "+ ex);
            login = false;
        }
        return login;
    }
    
    /**
     * 
     * @param msg 
     */
    public synchronized void outputSocket(ArrayList msg) {
        try {
            os.writeObject(msg);
            os.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * 
     * @return 
     */
    private synchronized ArrayList listaUtenti(){
        ArrayList listaReturn = new ArrayList();
        ArrayList utenti = new ArrayList();
        
        Server.utentiConnessi.stream().forEach((user) -> {
            utenti.add(user.getUserName());
        });
        listaReturn.add("LU-REC");
        listaReturn.add(utenti);
        return listaReturn;
    }
    
    /**
     * 
     * @param mexIs
     * @return 
     */
    public synchronized boolean reindirizzaMessaggio(ArrayList mexIs){
        try{
            addMexToDb((String)mexIs.get(1),(String)mexIs.get(2));
            ArrayList mexReplicato = new ArrayList();
            for (UserHandler userH : Server.threadAperti) {
                if (userH.getName().equals(mexIs.get(1))) {
                    mexReplicato.add("MEX-IN");
                    mexReplicato.add(getName());
                    mexReplicato.add(mexIs.get(2));
                    System.out.println("Messaggio inoltrato a " + mexIs.get(1));
                    userH.outputSocket(mexReplicato);
                    addMexToDb((String)mexIs.get(1),(String)mexIs.get(2));
                    break;
                }
            }
            return true;
        }
        catch(Exception e){
            return false;
        }
    }
    
    public synchronized void reindirizzaFile(ArrayList mexIs){
        try{
            ArrayList mexReplicato = new ArrayList();
            for (UserHandler userH : Server.threadAperti) {
                if (userH.getName().equals(mexIs.get(1))) {
                    mexReplicato.add("FILE-IN");
                    mexReplicato.add(getName());
                    mexReplicato.add(mexIs.get(2));
                    mexReplicato.add(mexIs.get(3));
                    userH.outputSocket(mexReplicato);
                    addFileToDb((String)mexIs.get(1),mexIs.get(2),(String)mexIs.get(3));
                    break;
                }
            }
        }
        catch(Exception e){
        }
    }
    
    public synchronized boolean addFileToDb(String destinatario,Object file, String nomefile){
        boolean esito = true;
        String autore = datiPersonali.getUserName();
        String tipo = "file";
        System.out.println(nomefile);
        String path =  datiPersonali.getUserName()+"\\"+nomefile;
        Calendar calendar = Calendar.getInstance();
        Timestamp data = new java.sql.Timestamp(calendar.getTime().getTime());
        
         try{
            FileOutputStream filein = new FileOutputStream(System.getProperty("user.dir")+ "\\saved_file\\" + path);
            BufferedOutputStream fout = new BufferedOutputStream(filein);
            for(int line: (ArrayList<Integer>)file){
                fout.write(line);
                fout.flush();
            }
            fout.close();
            filein.close();
        } catch(java.io.IOException e){
            System.out.println(e.getMessage());
        }
         
        path = System.getProperty("user.dir")+ "\\saved_file\\" + path;
        path = path.replace("\\","\\\\");
        System.out.println(path);
                
        try {
            String SQL =  "INSERT INTO messaggi (autore,tipo,testo,path,data,destinatario) VALUES ('" + autore + "','" + tipo +  "','" + "" + "','" + path + "','"+ data +"','"+ destinatario +"');";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQL); 
            stmt.close();           
            esito = true;
            
        } catch (SQLException ex) {
            System.out.println("Errore query login --> "+ ex);
            esito = false;
        }


        return esito;
    }
    
    
    
    public synchronized boolean addMexToDb(String destinatario,String testo){
        boolean esito = true;
        String autore = datiPersonali.getUserName();
        String tipo = "text";
        String path = "";
        Calendar calendar = Calendar.getInstance();
        Timestamp data = new java.sql.Timestamp(calendar.getTime().getTime());

        try {
            String SQL =  "INSERT INTO messaggi (autore,tipo,testo,path,data,destinatario) VALUES ('" + autore + "','" + tipo +  "','" + testo + "','" + path + "','"+ data +"','"+ destinatario +"');";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQL);
  
            stmt.close();
            
            esito = true;
            
        } catch (SQLException ex) {
            System.out.println("Errore query addMexToDb --> "+ ex);
            esito = false;
        }

        return esito;
    }
    
    public synchronized void createDirFile(){

        String directory = datiPersonali.getUserName();
        

        Path newDirectoryPath = Paths.get(System.getProperty("user.dir")+"\\saved_file\\"+directory);
        if (!Files.exists(newDirectoryPath)) {
            try {
                Files.createDirectory(newDirectoryPath);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
      
    }
    
    /**
     * 
     */
    public synchronized void termina() {
        
  
         
        Server.utentiConnessi.remove(datiPersonali);  
        
        for(int i = 0; i < Server.threadAperti.size(); i++){
            UserHandler tmp = (UserHandler) Server.threadAperti.get(i);
            if( (tmp.getName()).equals(datiPersonali.getUserName())){
                Server.threadAperti.remove(i);
            }
        }
        
        try {
            socket.close();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (login){
            rimuoviUtenteAdAltri(datiPersonali.getUserName());   
            System.out.println("L'utente-->" + datiPersonali.getUserName() + " si è disconnesso");
        }
    }

    private synchronized UserObject leggiDatiPersonali(ArrayList tmp) {
        return new UserObject((String) tmp.get(0), (String) tmp.get(1));
    }

    private synchronized void inviaNuovoUtenteAdAltri(String nomeUtente) {
        ArrayList msg = new ArrayList();
        msg.add("NEW-U");
        msg.add(nomeUtente);
        for (int i = 0; i < Server.threadAperti.size() - 1; i++){
            Server.threadAperti.get(i).outputSocket(msg);
        }
    }
    
    private synchronized void rimuoviUtenteAdAltri(String nomeUtente) {
        ArrayList msg = new ArrayList();
        msg.add("REMOVE-U");
        msg.add(nomeUtente);
        for (int i = 0; i < Server.threadAperti.size() - 1; i++){
            Server.threadAperti.get(i).outputSocket(msg);
        }
    }
    
    public static boolean connettiDB(){
        boolean esito = false;
        
        String url = "jdbc:mysql://localhost:3306/chat";
        String username = "root";
        String password = ""; //Hk5eWd0G

        try{
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connessione DB riuscita");
            esito = true;
        } catch (SQLException e) {
            throw new IllegalStateException("Non riesco a connettermi al DB --> ", e);
        }
        return esito;
    }
    
    public synchronized boolean registrazione(ArrayList tmp) {
        boolean esito = true;
        String username = (String)tmp.get(1);
        String password = (String)tmp.get(2);
        
        try {
            String SQL =  "INSERT INTO users (username,password) VALUES ('" + username + "','" + password + "');";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQL);

  
            stmt.close();
            
        } catch (SQLException ex) {
            System.out.println("Errore query login --> "+ ex);
            
            ArrayList ris = new ArrayList();
            ris.add("ERRORE-REG-USERNAME-PRESENTE");
            this.outputSocket(ris);
            System.out.println("ERRORE-REG-USERNAME-PRESENTE");

            esito = false;
        }

        return esito;
    }
    
    
    public synchronized ArrayList reindirizzaListaMex(){
    ArrayList ris = new ArrayList();
    ris.add("LIST_MEX-REC");
    String userName = datiPersonali.getUserName();
    
    try{
            String SQL =  "SELECT * FROM `messaggi` WHERE autore = '" + userName + "' AND destinatario = '" + userName + "' ORDER BY data;" ;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {   
                if (rs.getString("tipo").equals("text")){
                    Messaggio messaggio = new Messaggio(rs.getString("destinatario"),rs.getString("autore"),rs.getString("data"),rs.getString("testo"));
                    ris.add(messaggio);
                }
                
            }
   
            stmt.close();
            System.out.println("Ho generato l'array di messaggi");
            
        } catch (SQLException ex) {
            System.out.println("Errore query reindirizzaListaMex --> "+ ex);

        }

    return ris;
 
    }
    
    public synchronized byte[] transformFileToByte(String percorso){
        Path path = Paths.get(percorso);
        byte[] data = null;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException ex) {
            Logger.getLogger(UserHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }
    
    public synchronized boolean sendFriendRequest(String destinatario){
        boolean esito = true;
        String autore = datiPersonali.getUserName();
        ArrayList risp = new ArrayList();
        risp.add("NEW-FRIEND-REQ");
        risp.add(destinatario);
        try {
            String SQL =  "INSERT INTO friends (user1,user2) VALUES ('" + autore + "','" + destinatario + "');";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQL);
            stmt.close();
            
             for (UserHandler userH : Server.threadAperti) {
                if (userH.getName().equals(destinatario)) {
                    userH.outputSocket(risp);   
                    break;
                }
            }
            esito = true;   
            System.out.println(autore + "  ha inviato una richiesta d'amicizia a -->" + destinatario);
        } catch (SQLException ex) {
            System.out.println("Errore query sendFriendRequest --> "+ ex);
            esito = false;
        }

        return esito;
    
    }
    
    public synchronized boolean acceptFriendRequest(String user1){
        boolean esito = true;
        String autore = datiPersonali.getUserName();
  
        try {
            String SQL =  "UPDATE friends SET stato=1 WHERE user1 = '"+user1+"' AND user2 = '" + autore + "';";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQL);
  
            stmt.close();
            
            esito = true;
            System.out.println("Amicizia tra --> "+autore+", "+user1);
            
        } catch (SQLException ex) {
            System.out.println("Errore query acceptFriendRequest--> "+ ex);
            esito = false;
        }

        return esito;
    
    }
    
    
    
    public synchronized boolean removeFriend(String user1){
        boolean esito = true;
        String autore = datiPersonali.getUserName();
        ArrayList risp = new ArrayList();
        risp.add("REMOVE-FRIEND");
        risp.add(user1);
  
        try {
            String SQL =  "DELETE from friends  WHERE user1 = '"+user1+"' AND user2 = '" + autore + "';";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQL);
  
            stmt.close();
            
            for (UserHandler userH : Server.threadAperti) {
                if (userH.getName().equals(autore)) {
                    userH.outputSocket(risp);   
                    break;
                }
            }
            
            esito = true;
            System.out.println("Rimozione d'amicizia tra --> "+autore+", "+user1);
            
        } catch (SQLException ex) {
            System.out.println("Errore query removeFriend --> "+ ex);
            esito = false;
        }

        return esito;
    
    }
    
    public synchronized ArrayList getFriendsList(){
        ArrayList ris = new ArrayList();
        ArrayList friends = new ArrayList();

        ris.add("FRIENDS-LIST-REQ");
        String userName = datiPersonali.getUserName();

        try{
                String SQL =  "SELECT * FROM `friends` WHERE user1 = '"+userName+"' OR user2 = '"+userName+"' AND stato = 1;"; 
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(SQL);

                while (rs.next()) {   
                    if (rs.getString("user1").equals(userName)){           
                        friends.add(rs.getString("user2"));
                    }
                    if (rs.getString("user2").equals(userName)){           
                        friends.add(rs.getString("user1"));
                    }
                }
                           

                ris.add(friends);
                stmt.close();
                System.out.println("Ho generato l'array di amici");

            } catch (SQLException ex) {
                System.out.println("Errore query getFriendsList --> "+ ex);

            }

        return ris;

        }
    
     public synchronized ArrayList getFriendsListWithoutAnswers(){
        ArrayList ris = new ArrayList();
        ArrayList friends = new ArrayList();

        ris.add("FRIENDS-LIST-WITHOUT-ANSWER");
        String userName = datiPersonali.getUserName();

        try{
                String SQL =  "SELECT * FROM `friends` WHERE user2 = '"+userName+"' AND stato = 0;"; 
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(SQL);

                while (rs.next()) {   
                    if (rs.getString("user1").equals(userName)){           
                        friends.add(rs.getString("user2"));
                    }
                    if (rs.getString("user2").equals(userName)){           
                        friends.add(rs.getString("user1"));
                    }

                }

                ris.add(friends);
                stmt.close();
                System.out.println("Ho generato l'array di richieste di amicizia se ci sono");

            } catch (SQLException ex) {
                System.out.println("Errore query getFriendsListWithoutAnswers --> "+ ex);

            }
        return ris;
        }
   
}

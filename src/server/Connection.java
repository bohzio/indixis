package server;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe che si connette al server e invia i dati
 *
 * @author dallachiaram@gmail.com && mattia.corradi.tr@gmail.com
 * @version 1.8
 */
public class Connection {
    private static Socket socket;
    private static ObjectOutputStream os;
    private final int portServer; // 5555
    private final String ipServer; // "127.0.0.1"
    private Ricezione ricezione = null;
    protected String username = null;
    protected String password = null;
    private final String type;
    private final ChatGUi graphics;
    private boolean esitoConnessione;
    private boolean esitoAutenticazione;
    
    public Connection(int port_server, String ip_server,String username,String password,String type,ChatGUi graphics) {
        this.portServer = port_server;
        this.ipServer = ip_server;
        this.username = username;
        this.password = password;
        this.type = type;
        this.graphics = graphics;
        if (type.equals("login")){
            controller("login");
        }if (type.equals("registrazione")){
            controller("registrazione");
        }
    }
    
    
     
     private void controller(String type){
            if (connessione(type)){
                System.out.println("Connessione riuscita");
            }else{
                System.out.println("Connessione fallita");
                termina();
                }
     }
    
    private boolean connessione(String regOrLog) {
        boolean result = false;
        
        try {

            socket = new Socket(ipServer, portServer);
            os = new ObjectOutputStream(socket.getOutputStream());
            esitoConnessione = true;
            ricezione = new Ricezione(socket,graphics);
            if (regOrLog.equals("login")){
                (this).autenticazione(username, password);
                if (ricezione.autenticazione()) {
                    System.out.println("Autenticazione");
                    esitoAutenticazione = true;
                    friendsListRequest();
                    listaUtentiRequest();
                    listaMexRequest();
                    ricezione.start();  //lancio il thread
                    result = true;
                }
            }if (regOrLog.equals("registrazione")){
                (this).registrazione(username,password);
                if (ricezione.registrazione()){
                    System.out.println("Registrazione Riuscita !!!");
                    listaUtentiRequest();
                    listaMexRequest();
                    ricezione.start();  //lancio il thread
                    result = true;
                }
            
            
            }

        } catch (Exception e) {
            result = false;
        }
        
        return result;
    }
    
    /**
     * 
     * @param username
     * @param password
     */
    private void autenticazione(String username, String password) {
        ArrayList dati = new ArrayList();
        
        dati.add(username);
        dati.add(password);

        try {
            os.writeObject(dati);
            os.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * 
     */
    public void listaUtentiRequest(){
        try{
            ArrayList listaRequest = new ArrayList();
            listaRequest.add("LU-REQ");
            os.writeObject(listaRequest);
            os.flush();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public void friendsListRequest(){
        try{
            ArrayList listaRequest = new ArrayList();
            listaRequest.add("FRIENDS-LIST-REQ");
            os.writeObject(listaRequest);
            os.flush();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    
    
    /**
     * 
     */
    public void listaMexRequest(){
        try{
            System.out.println("Ho inviato la richiesta della lista messaggi");
            ArrayList listaRequest = new ArrayList();
            listaRequest.add("LIST-MEX");
            os.writeObject(listaRequest);
            os.flush();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * 
     * @param mex
     * @param destinatario
     */
    public static void inviaMessaggio(String mex, String destinatario){
        try{
            //System.out.println("invio messaggio da Connection... ");
            ArrayList mexFormattato = new ArrayList();
            mexFormattato.add("MEX-OUT");
            mexFormattato.add(destinatario);
            mexFormattato.add(mex);
            os.writeObject(mexFormattato);
            os.flush();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    private static void invioStreamFile(ArrayList streamFile, String destinatario, String filename){
        try{
            System.out.println("Inizio invio file");
            String name = filename.split("\\\\")[filename.split("\\\\").length - 1];
            System.out.println(name);
            ArrayList mexFormattato = new ArrayList();
            mexFormattato.add("FILE-OUT");
            mexFormattato.add(destinatario);
            mexFormattato.add(streamFile);
            mexFormattato.add(name);
            os.writeObject(mexFormattato);
            os.flush();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    public static void inviaFile(String filename, String destinatario) {
        ArrayList streamFile = new ArrayList();
        try(FileInputStream file = new FileInputStream(filename); 
            BufferedInputStream fin = new BufferedInputStream(file)) {
            System.out.println("invio file da Connection...");
            System.out.println(filename);
            int line;
            while ((line = fin.read()) != -1){
                streamFile.add(line);
            }
            invioStreamFile(streamFile, destinatario, filename);
        } catch(java.io.IOException e){
            System.out.println(e.getMessage());
        }
    }
    
    public  boolean registrazione(String username, String password){
        ArrayList dati = new ArrayList();
        
        dati.add("NEW-REG");
        dati.add(username);
        dati.add(password);

        try {
            os.writeObject(dati);
            os.flush();
            System.out.println("inviato dati per registrazione");
            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    
    public static void termina(){
        
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
        
    public static void sendFriendRequest(String destinatario){
        try{
            //System.out.println("invio messaggio da Connection... ");
            ArrayList tmp = new ArrayList();
            tmp.add("FRIEND-REQ");
            tmp.add(destinatario);
            os.writeObject(tmp);
            os.flush();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    
    
    }
    
     public static void acceptFriendRequest(String destinatario){
        try{
            //System.out.println("invio messaggio da Connection... ");
            ArrayList tmp = new ArrayList();
            tmp.add("ACCEPT-FRIEND");
            tmp.add(destinatario);
            os.writeObject(tmp);
            os.flush();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
     }
        
    public static void removeFriend(String destinatario){
        try{
            //System.out.println("invio messaggio da Connection... ");
            ArrayList tmp = new ArrayList();
            tmp.add("REMOVE-FRIEND");
            tmp.add(destinatario);
            os.writeObject(tmp);
            os.flush();
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}
    
   

        
        
   
      
 
      
  
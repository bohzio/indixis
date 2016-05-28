package server;

import java.io.FileInputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
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
    private static String username = null;
    private static String password = null;
    private final String type;
    private final ChatGUi graphics;
    private boolean esitoConnessione;
    private boolean esitoAutenticazione;
    public static final String pathSendImage = System.getProperty("user.dir") + "\\src\\client\\file\\ricevuti\\";

    public Connection(int port_server, String ip_server, String username, String password, String type, ChatGUi graphics) {
        this.portServer = port_server;
        this.ipServer = ip_server;
        this.username = username;
        this.password = password;
        this.type = type;
        this.graphics = graphics;
        if (type.equals("login")) {
            controller("login");
        }
        if (type.equals("registrazione")) {
            controller("registrazione");
            System.out.println("sto registrando");
        }
    }

    private void controller(String type) {
        if (connessione(type)) {
            System.out.println("Connessione riuscita");
        } else {
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
            ricezione = new Ricezione(socket, graphics, username);
            if (regOrLog.equals("login")) {
                (this).autenticazione(username, password);
                if (ricezione.autenticazione()) {
                    System.out.println("Autenticazione");
                    esitoAutenticazione = true;
                    friendsListRequest();
                    listaUtentiRequest();
                    listaMexRequest();
                    getListFriendWithoutAnswers();
                    
                    ricezione.start();  //lancio il thread
                    result = true;
                }
            }
            else if (regOrLog.equals("registrazione")) {
                (this).registrazione(username, password);
                if (ricezione.registrazione()) {
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
    public static void listaUtentiRequest() {
        try {
            ArrayList listaRequest = new ArrayList();
            listaRequest.add("LU-REQ");
            os.writeObject(listaRequest);
            os.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void friendsListRequest() {
        try {
            ArrayList listaRequest = new ArrayList();
            listaRequest.add("FRIENDS-LIST-REQ");
            os.writeObject(listaRequest);
            os.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     */
    public void listaMexRequest() {
        try {
            System.out.println("Ho inviato la richiesta della lista messaggi");
            ArrayList listaRequest = new ArrayList();
            listaRequest.add("LIST-MEX");
            os.writeObject(listaRequest);
            os.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     *
     * @param mex
     * @param destinatario
     */
    public static void inviaMessaggio(String mex, String destinatario) {
        try {
            System.out.println("sono entrato");
            ArrayList mexFormattato = new ArrayList();
            mexFormattato.add("MEX-OUT");
            Calendar now = Calendar.getInstance();
            int day = now.get(Calendar.DAY_OF_MONTH);
            int hour = now.get(Calendar.HOUR_OF_DAY);
            Message message = new Message(username, mex, destinatario, hour, day, TypeMessage.MESSAGGIO);
            mexFormattato.add(message);
            os.writeObject(mexFormattato);
            os.flush();
            System.out.println("ho inviato il messaggio");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void invioFile(String path, String destinatario,TypeMessage type) {
        try {
            Calendar now = Calendar.getInstance();
            int day = now.get(Calendar.DAY_OF_MONTH);
            int hour = now.get(Calendar.HOUR_OF_DAY);
            System.out.println("Inizio invio file");
            String name = path.split("\\\\")[path.split("\\\\").length - 1];
            byte[] file = transformFileToByte(path);
            System.out.println(name);
            ArrayList mexFormattato = new ArrayList();
            mexFormattato.add("FILE-OUT");
            Message message = new Message(username,file,destinatario,hour,day,type,name);
            mexFormattato.add(message);
            os.writeObject(mexFormattato);
            os.flush();
            System.out.println("ho inviato il file nel metodo invioFile");
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("errore invio file");
        }
    }
    
    public void insertFile(String path, String destinatario,TypeMessage type){
        Calendar now = Calendar.getInstance();
        int day = now.get(Calendar.DAY_OF_MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        String name = path.split("\\\\")[path.split("\\\\").length - 1];
        Message message = new Message(username,transformFileToByte(path),destinatario,hour,day,type,name);
        message.setForeign(false);
        graphics.addMessage(message);
    }
    
    private static byte[] transformFileToByte(String percorso) {
        Path path = Paths.get(percorso);
        byte[] data = null;
        try {
            data = Files.readAllBytes(path);
            System.out.println("ho convertito il file");
        } catch (IOException ex) {
           ex.printStackTrace();
        }
        return data;
    }

    public boolean registrazione(String username, String password) {
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

    public static void termina() {

        try {
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void sendFriendRequest(String destinatario) {
        try {
            //System.out.println("invio messaggio da Connection... ");
            ArrayList tmp = new ArrayList();
            tmp.add("FRIEND-REQ");
            tmp.add(destinatario);
            os.writeObject(tmp);
            os.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static void acceptFriendRequest(String destinatario) {
        try {
            //System.out.println("invio messaggio da Connection... ");
            ArrayList tmp = new ArrayList();
            tmp.add("ACCEPT-FRIEND");
            tmp.add(destinatario);
            os.writeObject(tmp);
            os.flush();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void removeFriend(String destinatario) {
        try {
            //System.out.println("invio messaggio da Connection... ");
            ArrayList tmp = new ArrayList();
            tmp.add("REMOVE-FRIEND");
            tmp.add(destinatario);
            os.writeObject(tmp);
            os.flush();
            System.out.println("inviata richiesta di rimuovere");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static void getListFriendWithoutAnswers() {
        try {
            //System.out.println("invio messaggio da Connection... ");
            ArrayList tmp = new ArrayList();
            tmp.add("GET-LIST-FRIEND-WITHOUT-ANSWERS");
            os.writeObject(tmp);
            os.flush();
            System.out.println("inviata richiesta GET-LIST-FRIEND-WITHOUT-ANSWERS");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public static String getUsername() {
        return username;
    }
}

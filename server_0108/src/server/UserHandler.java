package server;

import java.io.BufferedOutputStream;
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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe che rappresenta il cuore del server. Riceve e invia i dati tramite
 * socket, scrive e legge su database
 *
 * @author Corradi Mattia, Dalla Chiara Michele
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
     *
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

    //######## GESTIONE THREAD ########//
    
    @Override
    public void run() {
        try {
            ArrayList risp = new ArrayList();
            
            ArrayList tmp = (ArrayList) is.readObject();// Legge l'array username
            if (tmp.get(0).equals("NEW-REG")) {
                if (registrazione(tmp)) {
                    ArrayList registrazione = new ArrayList();
                    registrazione.add("REG-RIUSCITA");
                    outputSocket(registrazione);
                }
            } else {
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
                    // inviaNuovoUtenteAdAltri(datiPersonali.getUserName());
                    System.out.println("Username: " + datiPersonali.getUserName());
                    //creo la directori dell'utente dove verranno salvati i suoi file
                    createDirFile();
                    // ciclo fintanto che la connessione con il client rimane in piedi

                    while (continua) {
                        try {
                            richieste = (ArrayList) is.readObject();
                            System.out.println((String) richieste.get(0));
                            switch ((String) richieste.get(0)) {
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
                                    sendFriendRequest((String) richieste.get(1));
                                    break;
                                case "ACCEPT-FRIEND":
                                    acceptFriendRequest((String) richieste.get(1));
                                    break;
                                case "FRIENDS-LIST-REQ":
                                    outputSocket(getFriendsList());
                                    break;
                                case "REMOVE-FRIEND":
                                    removeFriend((String) richieste.get(1));
                                case "GET-LIST-FRIEND-WITHOUT-ANSWERS":
                                    outputSocket(getFriendsListWithoutAnswers());
                            }
                        } catch (Exception e) {
                            continua = false;
                            termina();

                        }
                    } // fine del ciclo del thread
                } else {
                    try {
                        termina(); // richiamo termina che chiude la socket, chiude ol thread e toglie l'utente dagli array di utenti online
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            continua = false;
        }

    }

    /**
     * Metodo che verifica se l'utente esiste sul database
     * @param username
     * @param password
     * @return boolean esito autenticazione
     */
    public synchronized boolean autentica(String username, String password) {
        login = false;
        try {
            String SQL = "SELECT * FROM users WHERE username='" + username + "' && password='" + password + "';";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            int i = 0;

            while (rs.next()) {
                i++;
            }

            if (i == 1) {
                login = true;
            }

            stmt.close();

        } catch (SQLException ex) {
            System.out.println("Errore query login --> " + ex);
            login = false;
        }
        return login;
    }

    /**
     * Metodo che invia tramite socket l'array contenente i dati relativi a quello che l'applicazione deve fare
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
     * Metodo che returna l'ArrayList contentente gli iscritti alla nostra chat
     * @return listaReturn ArrayList utenti
     */
    private synchronized ArrayList listaUtenti() {
        ArrayList listaReturn = new ArrayList();
        ArrayList utenti = new ArrayList();
        listaReturn.add("LU-REC");

        try {
            String SQL = "SELECT * FROM `users` WHERE username != '" + userName + "';";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                utenti.add(rs.getString("username"));
                System.out.println(rs.getString("username"));
            }

            listaReturn.add(utenti);

            stmt.close();
            System.out.println("Ho generato l'array di utenti");

        } catch (SQLException ex) {
            System.out.println("Errore query listaUtenti --> " + ex);

        }

        return listaReturn;

    }

    /**
     * Metodo che reindirizza il messaggio al client corretto.
     * Il messaggio viene salvato su database e poi viene inoltrato se l'utente relativo è online
     * @param mexIs
     * @return boolean esito invio messaggio
     */
    public synchronized boolean reindirizzaMessaggio(ArrayList mexIs) {
        try {
            Message messaggio = (Message) mexIs.get(1);
            addMexToDb(messaggio.getDestinatario(), messaggio.getMessage());

            ArrayList mexReplicato = new ArrayList();
            for (UserHandler userH : Server.threadAperti) {
                if (userH.getName().equals(messaggio.getDestinatario())) {
                    mexReplicato.add("MEX-IN");
                    mexReplicato.add(messaggio);
                    System.out.println("Messaggio inoltrato a " + messaggio.getDestinatario());
                    userH.outputSocket(mexReplicato);
                    break;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Metodo che reindirizza il file al client corretto.
     * Il file viene salvato sul disco del server e poi viene salvato su database il path relativo.
     * Successivamente viene inoltrato se l'utente relativo è online
     * @param mexIs 
     */
    public synchronized void reindirizzaFile(ArrayList mexIs) {
        Message messaggio = (Message) mexIs.get(1);
        messaggio.setForeign(true);
        addFileToDb(messaggio.getDestinatario(), messaggio.getFile(),messaggio.getFilename(),messaggio.getType());
        try {
            ArrayList mexReplicato = new ArrayList();
            for (UserHandler userH : Server.threadAperti) {
                if (userH.getName().equals(messaggio.getDestinatario())) {
                    mexReplicato.add("FILE-IN");
                    mexReplicato.add(messaggio);
                    userH.outputSocket(mexReplicato);   
                    break;
                }
            }
        } catch (Exception e) {
        }
    }
    /**
     * Metodo che aggiunge il file al database
     * @param destinatario
     * @param file
     * @param nomefile
     * @return boolean
     */
    public synchronized boolean addFileToDb(String destinatario, byte[] file, String nomefile,TypeMessage type) {
        boolean esito = true;
        String autore = datiPersonali.getUserName();
        String tipo = type.toString();
        System.out.println(nomefile);
        String path = datiPersonali.getUserName() + "\\" + nomefile;
        Calendar calendar = Calendar.getInstance();
        Timestamp data = new java.sql.Timestamp(calendar.getTime().getTime());

        try {
            
            FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + "\\saved_file\\" + path);
            fos.write(file);
            fos.close();
            
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }

        path = System.getProperty("user.dir") + "\\saved_file\\" + path;
        path = path.replace("\\", "\\\\");
        System.out.println(path);

        try {
            String SQL = "INSERT INTO messaggi (autore,tipo,testo,path,data,destinatario) VALUES (?,?,?,?,?,?);";
            PreparedStatement preparedStatement = connection.prepareStatement(SQL);
            preparedStatement.setString(1, autore);
            preparedStatement.setString(2, tipo);
            preparedStatement.setString(3, nomefile);
            preparedStatement.setString(4, path);
            preparedStatement.setTimestamp(5, data);
            preparedStatement.setString(6, destinatario);
            preparedStatement.executeUpdate(); 
            preparedStatement.close();
            esito = true;

        } catch (SQLException ex) {
            System.out.println("Errore query insersci file database --> " + ex);
            esito = false;
        }

        return esito;
    }
    
    /**
     * Metodo per aggiungere messaggio al database
     * @param destinatario
     * @param testo
     * @return boolean
     */
    public synchronized boolean addMexToDb(String destinatario, String testo) {
        boolean esito = true;
        String autore = datiPersonali.getUserName();
        String tipo = "text";
        String path = "";
        Calendar calendar = Calendar.getInstance();
        Timestamp data = new java.sql.Timestamp(calendar.getTime().getTime());

        try {
            System.out.println("autore:" + autore + ",tipo" + tipo + ",testo" + testo + "destinataroo" + destinatario);
            String SQL = "INSERT INTO messaggi (autore,tipo,testo,path,data,destinatario) VALUES ('" + autore + "','" + tipo + "','" + testo + "','" + path + "','" + data + "','" + destinatario + "');";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQL);

            stmt.close();

            esito = true;

            System.out.println("Aggiunto il mex al db");

        } catch (SQLException ex) {
            System.out.println("Errore query addMexToDb --> " + ex);
            esito = false;
        }

        return esito;
    }

    /**
     * Metodo che create la directory dell'utente se non è già presente
     */
    public synchronized void createDirFile() {

        String directory = datiPersonali.getUserName();

        Path newDirectoryPath = Paths.get(System.getProperty("user.dir") + "\\saved_file\\" + directory);
        if (!Files.exists(newDirectoryPath)) {
            try {
                Files.createDirectory(newDirectoryPath);
            } catch (IOException e) {
                System.err.println(e);
            }
        }

    }

    /**
     *Metodo che uccide tutte le risorse relative a quell'utente
     */
    public synchronized void termina() {

        Server.utentiConnessi.remove(datiPersonali);

        for (int i = 0; i < Server.threadAperti.size(); i++) {
            UserHandler tmp = (UserHandler) Server.threadAperti.get(i);
            if ((tmp.getName()).equals(datiPersonali.getUserName())) {
                Server.threadAperti.remove(i);
            }
        }

        try {
            socket.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (login) {
            //imuoviUtenteAdAltri(datiPersonali.getUserName());   
            System.out.println("L'utente-->" + datiPersonali.getUserName() + " si è disconnesso");
        }
    }

    /**
     * Metodo che legge i dati personali di un utente
     * @param tmp
     * @return UserObject oggetto che racchiude i dati dell'utente
     */
    private synchronized UserObject leggiDatiPersonali(ArrayList tmp) {
        return new UserObject((String) tmp.get(0), (String) tmp.get(1));
    }

    /**
     * Metodo che si connette al database
     * @return boolean
     */
    public static boolean connettiDB() {
        boolean esito = false;

        String url = "jdbc:mysql://localhost:3306/chat";
        String username = "root";
        String password = ""; 

        try {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Connessione DB riuscita");
            esito = true;
        } catch (SQLException e) {
            throw new IllegalStateException("Non riesco a connettermi al DB. Controlla che sia attivo e che i collegamenti nel metodo connettiDB siano corretti--> ", e);
        }
        return esito;
    }
    
    /**
     * Metodo che registra un utente su database
     * @param tmp
     * @return boolean
     */
    public synchronized boolean registrazione(ArrayList tmp) {
        boolean esito = true;
        String username = (String) tmp.get(1);
        String password = (String) tmp.get(2);

        try {
            String SQL = "INSERT INTO users (username,password) VALUES ('" + username + "','" + password + "');";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQL);

            stmt.close();

        } catch (SQLException ex) {
            System.out.println("Errore query login --> " + ex);

            ArrayList ris = new ArrayList();
            ris.add("ERRORE-REG-USERNAME-PRESENTE");
            this.outputSocket(ris);
            System.out.println("ERRORE-REG-USERNAME-PRESENTE");

            esito = false;
        }

        return esito;
    }
    
    /**
     * Metodo che invia al client al lista di tutti i suoi relativi messaggi e file salvati su database
     * @return ArrayList<Message>
     * @throws ParseException 
     */
    public synchronized ArrayList reindirizzaListaMex() throws ParseException {
        ArrayList ris = new ArrayList();
        ris.add("LIST_MEX-REC");
        String userName = datiPersonali.getUserName();

        try {
            String SQL = "SELECT * FROM `messaggi` WHERE autore = '" + userName + "' OR destinatario = '" + userName + "' ORDER BY data;";
            try (Statement stmt = connection.createStatement()) {
                ResultSet rs = stmt.executeQuery(SQL);

                while (rs.next()) {
                    String data = rs.getString("data");
                    java.util.Date temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS").parse(data);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(temp);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    int hour = cal.get(Calendar.HOUR_OF_DAY);

                    if (rs.getString("tipo").equals("text")) {
                        boolean foregin = false;
                        if (rs.getString("destinatario").equals(userName)) {
                            foregin = true;
                        }
                        if (rs.getString("autore").equals(userName)) {
                            foregin = false;
                        }
                        Message messaggio = new Message(rs.getString("autore"), rs.getString("testo"), rs.getString("destinatario"), hour, day, TypeMessage.MESSAGGIO, foregin);
                        ris.add(messaggio);
                    }
                    if (rs.getString("tipo").equals("file")) {
                        boolean foregin = false;
                        if (rs.getString("destinatario").equals(userName)) {
                            foregin = true;
                        }
                        if (rs.getString("autore").equals(userName)) {
                            foregin = false;
                        }
                        Message messaggio = new Message(rs.getString("autore"), transformFileToByte(rs.getString("path")), rs.getString("destinatario"), hour, day, TypeMessage.FILE,foregin,rs.getString("testo"));
                        ris.add(messaggio);
                    }
                    if (rs.getString("tipo").equals("audio")) {
                         boolean foregin = false;
                        if (rs.getString("destinatario").equals(userName)) {
                            foregin = true;
                        }
                        if (rs.getString("autore").equals(userName)) {
                            foregin = false;
                        }
                        Message messaggio = new Message(rs.getString("autore"), transformFileToByte(rs.getString("path")), rs.getString("destinatario"), hour, day, TypeMessage.AUDIO,foregin,rs.getString("testo"));
                        ris.add(messaggio);
                    }
                    if (rs.getString("tipo").equals("foto")) {
                        boolean foregin = false;
                        if (rs.getString("destinatario").equals(userName)) {
                            foregin = true;
                        }
                        if (rs.getString("autore").equals(userName)) {
                            foregin = false;
                        }
                        Message messaggio = new Message(rs.getString("autore"), transformFileToByte(rs.getString("path")), rs.getString("destinatario"), hour, day, TypeMessage.FOTO,foregin,rs.getString("testo"));
                        ris.add(messaggio);
                    }

                }
            }
            System.out.println("Ho generato l'array di messaggi");

        } catch (SQLException ex) {
            System.out.println("Errore query reindirizzaListaMex --> " + ex);

        }

        return ris;

    }
    
    /**
     * Metodo che trasforma in byte un file per poterlo serializzare
     * @param percorso
     * @return 
     */
    private synchronized byte[] transformFileToByte(String percorso) {
        Path path = Paths.get(percorso);
        byte[] data = null;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException ex) {
            Logger.getLogger(UserHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return data;
    }
    
    /**
     * Metodo che permette di salvare su database e inviare una richiesta d'amicizia al relativo client
     * @param destinatario
     * @return boolean
     */
    public synchronized boolean sendFriendRequest(String destinatario) {
        boolean esito = true;
        String autore = datiPersonali.getUserName();
        ArrayList risp = new ArrayList();
        risp.add("NEW-FRIEND-REQ");
        risp.add(autore);
        try {
            String SQL = "INSERT INTO friends (user1,user2) VALUES ('" + autore + "','" + destinatario + "');";
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
            System.out.println("Errore query sendFriendRequest --> " + ex);
            esito = false;
        }

        return esito;

    }

    /**
     * Metodo che permette di accettare la richiesta di amicizia e salvare sul database 
     * @param user1
     * @return boolean
     */
    public synchronized boolean acceptFriendRequest(String user1) {
        boolean esito = true;
        String autore = datiPersonali.getUserName();
        ArrayList risp = new ArrayList<>();
        risp.add("REQUEST-ACCEPT-REAL-TIME");
        

        try {
            String SQL = "UPDATE friends SET stato=1 WHERE user1 = '" + user1 + "' AND user2 = '" + autore + "';";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQL);

            stmt.close();

            for (UserHandler userH : Server.threadAperti) {
                if (userH.getName().equals(user1)) {
                    risp.add(autore);
                    userH.outputSocket(risp);
                    break;
                }
            }
            
            risp.remove(autore);
            risp.add(user1);
            for (UserHandler userH : Server.threadAperti) {
                if (userH.getName().equals(autore)) {
                    userH.outputSocket(risp);
                    break;        
                }
            }
            esito = true;
            System.out.println("Amicizia tra --> " + autore + ", " + user1);

        } catch (SQLException ex) {
            System.out.println("Errore query acceptFriendRequest--> " + ex);
            esito = false;
        }

        return esito;

    }

    /**
     * Metodo che permette di rimuovere un amico
     * @param user1
     * @return boolean
     */
    public synchronized boolean removeFriend(String user1) {
        boolean esito = true;
        String autore = datiPersonali.getUserName();
        ArrayList risp = new ArrayList();
        risp.add("REMOVE-FRIEND-REAL-TIME");
        risp.add(user1);

        try {
            String SQL = "DELETE from friends  WHERE user1 = '" + user1 + "' AND user2 = '" + autore + "' or user1 = '" + autore + "' AND user2 = '" + user1 + "';";
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(SQL);

            stmt.close();

            for (UserHandler userH : Server.threadAperti) {
                if (userH.getName().equals(user1)) {
                    userH.outputSocket(risp);
                }
                if (userH.getName().equals(autore)) {
                    userH.outputSocket(risp);
                }
            }

            esito = true;
            System.out.println("Rimozione d'amicizia tra --> " + autore + ", " + user1);

        } catch (SQLException ex) {
            System.out.println("Errore query removeFriend --> " + ex);
            esito = false;
        }

        return esito;

    }

    /**
     * Metodo che genera la lista degli amici di un utente
     * @return ArrayList amici
     */
    public synchronized ArrayList getFriendsList() {
        ArrayList ris = new ArrayList();
        ArrayList friends = new ArrayList();

        ris.add("FRIENDS-LIST-REQ");
        String userName = datiPersonali.getUserName();

        try {
            String SQL = "SELECT * FROM `friends` WHERE user1 = '" + userName + "' OR user2 = '" + userName + "';";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                if (rs.getString("user1").equals(userName) && rs.getString("stato").equals("1")) {
                    friends.add(rs.getString("user2"));
                }
                if (rs.getString("user2").equals(userName) && rs.getString("stato").equals("1")) {
                    friends.add(rs.getString("user1"));
                }
            }

            ris.add(friends);
            stmt.close();
            System.out.println("Ho generato l'array di amici");

        } catch (SQLException ex) {
            System.out.println("Errore query getFriendsList --> " + ex);

        }

        return ris;

    }

    /**
     * Metodo che genera le richieste di amicizia senza risposta da mostrare ad ogni apertura del client
     * @return ArryList username
     */
    public synchronized ArrayList getFriendsListWithoutAnswers() {
        ArrayList ris = new ArrayList();
        ArrayList friends = new ArrayList();

        ris.add("FRIENDS-LIST-WITHOUT-ANSWER");
        String userName = datiPersonali.getUserName();

        try {
            String SQL = "SELECT * FROM `friends` WHERE user2 = '" + userName + "' AND stato = 0;";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);

            while (rs.next()) {
                if (rs.getString("user1").equals(userName)) {
                    friends.add(rs.getString("user2"));
                }
                if (rs.getString("user2").equals(userName)) {
                    friends.add(rs.getString("user1"));
                }

            }

            ris.add(friends);
            stmt.close();
            System.out.println("Ho generato l'array di richieste di amicizia se ci sono");

        } catch (SQLException ex) {
            System.out.println("Errore query getFriendsListWithoutAnswers --> " + ex);

        }
        return ris;
    }

}

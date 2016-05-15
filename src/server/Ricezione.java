package server;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Classe che riceve i dati dal server e setta la grafica.
 * @author dallachiaram@gmail.com && corradi.mattia@gmail.com
 * @version 1.8
 */

public class Ricezione extends Thread{
    private final Socket socket;
    private boolean continua = true;
    private ObjectInputStream is;
    private ArrayList listaUtenti;
    private ArrayList friendsList;
    private ArrayList friendsListWithoutAnswer;
    private final ChatGUi graphics;
    private final String username;


    /**
     * Default constructor
     * @param socket
     * @param graphics
     * @param username 
     */
    public Ricezione(Socket socket,ChatGUi graphics,String username) {
        this.socket = socket;
        this.graphics = graphics;
        this.username = username;
    }
    
   /**
    * Metodo che una riceve dal server l'esito dell'autenticazione
    * @return true se l'autenticazione va a buon fine
    */
    public boolean autenticazione() {
        boolean autenticato;
        ArrayList rispostaServer;

        try {
            is = new ObjectInputStream(socket.getInputStream());
            rispostaServer = (ArrayList) is.readObject();
            // se ci risponde con AUT-R(autenticazione riuscita), significa che ci siamo autenticati
            autenticato = ((String) rispostaServer.get(0)).equals("AUT-R");
        }
        catch (IOException | ClassNotFoundException e) {
            autenticato = false;
            System.out.println(e.getMessage());
        }
        return autenticato;
    }
    
    /**
     * Metodo che riceve l'esito della registrazione dal server
     * @return registrazione a seconda dell'esito della registrazione
     */
    public boolean registrazione() {
        boolean registrazione;
        ArrayList rispostaServer;

        try {
            is = new ObjectInputStream(socket.getInputStream());
            rispostaServer = (ArrayList) is.readObject();
            registrazione = ((String) rispostaServer.get(0)).equals("REG-RIUSCITA");
        }
        catch (IOException | ClassNotFoundException e) {
            registrazione = false;
            System.out.println(e.getMessage());
        }
        return registrazione;
    }
    
    /**
     * 
     */
    @Override
    public void run(){
        ArrayList mexInput = null;
     
        while (continua) {
            try {
                try{
                   //System.out.println(is.readObject().getClass());
                }catch(Exception e){
                    System.out.println("eccezione nuova");
                    e.printStackTrace();
                }
                mexInput = (ArrayList) is.readObject();
                System.out.println("___" + mexInput.get(0));
                switch ((String) mexInput.get(0)) {
                    case "LU-REC":
                        listaUtenti = riceviListaUtenti((ArrayList)mexInput);
                        break;
                    case "MEX-IN":
                        riceviMessaggio(mexInput);
                        break;
                    case "FILE-IN":
                        riceviStreamFile(mexInput);
                        break;
                    case "NEW-U":
                        aggiornaListaUtentiAdd(mexInput);
                        break;
                    case "REMOVE-U":
                        aggiornaListaUtentiRemove(mexInput);
                        break;
                    case "LIST_MEX-REC":
                        riceviListaMex(mexInput);
                        break;
                    case "FRIENDS-LIST-REQ":
                        friendsList = riceviListaAmici((ArrayList)mexInput);
                        break;
                    case "NEW-FRIEND-REQ":
                        updateFriendsListWithoutAnswer((ArrayList)mexInput);
                        break;
                    case "REMOVE-FRIEND":
                        removeFriends((ArrayList)mexInput);
                        break;
                    case "FRIENDS-LIST-WITHOUT-ANSWER":
                        friendsListWithoutAnswer = riceviListaAmiciSenzaRisposta((ArrayList)mexInput);
                        break;
                    case "ERRORE-REG-USERNAME-PRESENTE":
                        System.out.println("Username già presente !!");
                        Connection.termina();
                        break;
                    default:
                        break;
                }
            }
            catch (IOException e) {
                continua = false;
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                 e.printStackTrace();
            }
        }
    }
    /**
     * 
     * @param mexInput
     * @return 
     */
   private synchronized ArrayList riceviListaUtenti(ArrayList mexInput){
        System.out.println("Ho ricevuto l'arrayList con i nomi utente");
        return (ArrayList) mexInput.get(1);
    }
    
     private synchronized ArrayList riceviListaAmiciSenzaRisposta(ArrayList mexInput){
        System.out.println("Ho ricevuto l'arrayList con le richieste di amicizia in sospeso a cui io non ho risposto");
        return (ArrayList) mexInput.get(1);
    }
    
      private synchronized void updateFriendsListWithoutAnswer(ArrayList mexInput){
        friendsListWithoutAnswer.add((String)mexInput.get(1));
    }
    
    private synchronized ArrayList riceviListaAmici(ArrayList mexInput){
        ArrayList ris = (ArrayList) mexInput.get(1);
        graphics.setFriendsListArray(ris);
        System.out.println("Ho settato la lista degli amici");
        return ris;
    }
    
    private synchronized void riceviListaMex(ArrayList mexInput){
        System.out.println("lista messaggi arrivata");
        mexInput.remove(0);
        ArrayList<Message>messaggi = mexInput;
        
        graphics.setListaMessaggi(messaggi);
        for (Message message : messaggi){
            System.out.println(message.getType());
        }
    }
    

    /**
     * 
     * @param mexInput
     */
    private synchronized void riceviMessaggio(ArrayList mexInput){
        Message messaggio = (Message)mexInput.get(1);
        messaggio.setForeign(true); // truee vuol dire che è ricevuto
        
        String mex =  messaggio.getMessage();
        String user =  messaggio.getUser();
        
        System.out.println("user: "+user+" mex: "+mex);
        
        graphics.addNotify(user);
        graphics.addMessage(messaggio);
    
    }
    
    /**
     * 
     * @param mexInput 
     */
    private synchronized void riceviStreamFile(ArrayList mexInput) {
        try{
            try (FileOutputStream file = new FileOutputStream(
                    System.getProperty("user.dir")+"\\src\\client\\file\\"
                            + "ricevuti\\" + ((ArrayList) mexInput).get(3)); BufferedOutputStream fout = new BufferedOutputStream(file)) {
                for(int line: (ArrayList<Integer>)mexInput.get(2)){
                    fout.write(line);
                    fout.flush();
                }
            }
            System.out.println("File totale rivevuto");
        } catch(java.io.IOException e){
            System.out.println(e.getMessage());
        }
    }

    private synchronized void aggiornaListaUtentiAdd(ArrayList mexInput) {
        System.out.println("Connessione --> "+ mexInput);
        listaUtenti.add(mexInput.get(1));
    }
    
    private synchronized void aggiornaListaUtentiRemove(ArrayList mexInput) {
        System.out.println("Disconnessione --> "+ mexInput);
        listaUtenti.remove(mexInput.get(1));
    }
    

    private void removeFriends(ArrayList arrayList) {
        String utenteDaEliminare = (String)((ArrayList)arrayList).get(1);
        try{
            friendsList.remove(utenteDaEliminare);
            friendsListWithoutAnswer.remove(utenteDaEliminare); 
        }catch(Exception e){
        }   
    }
    
   
}

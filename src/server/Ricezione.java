package server;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 * 
 * @author dallachiaram@gmail.com && corradi.mattia
 * @version 1.5
 */
public class Ricezione extends Thread{
    private final Socket socket;
    private boolean continua = true;
    private ObjectInputStream is;
    private FileOutputStream file;
    private BufferedOutputStream fout;
    private ArrayList listaUtenti;
    private ArrayList friendsList;
    private ArrayList friendsListWithoutAnswer;
    private static boolean ricezioneListaUtenti = false;
    private boolean registrato = false;
    private final ChatGUi graphics;


    public Ricezione(Socket socket,ChatGUi graphics) {
        this.socket = socket;
        this.graphics = graphics;
    }
    
    /**
     * 
     * @return 
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
                System.out.println("ERRORE DI IO");
            }
            catch (ClassNotFoundException e) {
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
        ricezioneListaUtenti = true;
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
        System.out.println("Ho ricevuto l'arrayList con gli amici");
        ArrayList ris = (ArrayList) mexInput.get(1);
        System.out.println(ris.get(0)+"AMICOOOOOOOOOOOOOoo");
        graphics.setFriendsListArray(ris);
        System.out.println("Ho settato la lista degli amici");
        return ris;
    }
    
    private synchronized void riceviListaMex(ArrayList mexInput){
        System.out.println("lista messaggi arrivata");
        mexInput.remove(0);
        mexInput.stream().forEach((i) -> {
            System.out.println(((Messaggio)i).getMittente()); //puoi farti tutti i get per prenterdi i messaggio e aggiornare la grafica
        });  
    }
    
    
    /**
     * 
     * @param mexInput
     */
    private synchronized void riceviMessaggio(ArrayList mexInput){
        String mex =  (String)(mexInput.get(2));
        String user =  (String)(mexInput.get(1));
        System.out.println("user: "+user+" mex: "+mex);
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
        System.out.println("Nuovo utente --> "+ mexInput);
        listaUtenti.add(mexInput.get(1));
    }
    
    private synchronized void aggiornaListaUtentiRemove(ArrayList mexInput) {
        System.out.println("Disconnessione --> "+ mexInput);
        listaUtenti.remove(mexInput.get(1));
    }
    
    public static boolean getEsitoListaUtenti(){
        return ricezioneListaUtenti;
        }

    private void removeFriends(ArrayList arrayList) {
        String utenteDaEliminare = (String)((ArrayList)arrayList).get(1);
        try{
            friendsList.remove(utenteDaEliminare);
            
        }catch(Exception e){
            e.printStackTrace();
        }
        
        try{
            friendsListWithoutAnswer.remove(utenteDaEliminare);
            
        }catch(Exception e){
            e.printStackTrace();
        }    
    }
    
   
}

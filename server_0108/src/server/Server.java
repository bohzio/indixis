package server;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Classe che istanzia e gestisce il server
 * @author Dalla Chiara Michele
 */
public class Server {

    protected static ArrayList<UserObject> utentiConnessi = new ArrayList(); // Definizione vettore globale degli utenti
    protected static ArrayList<UserHandler> threadAperti = new ArrayList(); // Definizione Vettore globale dei thread

    // bisogna definire una struttura globale (lista) in cui ciascun thread andrà a scrivere i messaggi da inviare e cercare quelli relativi a sè stesso
    public Server(int port) throws IOException {
        ServerSocket server = new ServerSocket(port);
        if (UserHandler.connettiDB()) {
            System.out.println("In attesa di connessione..." + "\n");

            while (true) {
                Socket client = server.accept();
                System.out.println("Connessione accettata da " + client.getInetAddress() + ".");
                threadAperti.add(new UserHandler(client));
            }
        }
    }

    /**
     * Entry point of Server
     *
     * @param args
     * @throws IOException
     */
    public static void main(String args[]) throws IOException {
        Server server = new Server(5555);
    }
}

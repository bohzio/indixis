package server;

import java.io.Serializable;

/**
 * @author mattia.corradi.tr@gmail.com
 * @version 1.8
 * Classe che permette di istanziare un oggetto messaggio
 */
public class Messaggio implements Serializable{
    private String destinatario;
    private String mittente;
    private String data;
    private String testo;

    public Messaggio(String destinatario, String mittente, String data, String testo) {
        this.destinatario = destinatario;
        this.mittente = mittente;
        this.data = data;
        this.testo = testo;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    public String getMittente() {
        return mittente;
    }

    public void setMittente(String mittente) {
        this.mittente = mittente;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTesto() {
        return testo;
    }

    public void setTesto(String testo) {
        this.testo = testo;
    }
   
}

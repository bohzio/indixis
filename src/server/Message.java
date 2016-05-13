package server;

import java.util.Date;
import java.util.logging.Logger;

/**
 *
 * @author prova
 */
public class Message {
    private String user;
    private String message;
    private String destinatario;
    private int ora;
    private int day;
    private TypeMessage type;
    private boolean foreign; // false inviato true ricevuto

    public Message(String user, String message, String destinatario, int ora, int day, TypeMessage type) {
        this.user = user;
        this.message = message;
        this.destinatario = destinatario;
        this.ora = ora;
        this.day = day;
        this.type = type;
    }
    
    public Message(String user, String message, String destinatario, int ora, int day, TypeMessage type, boolean foreign) {
        this.user = user;
        this.message = message;
        this.destinatario = destinatario;
        this.ora = ora;
        this.day = day;
        this.type = type;
        this.foreign = foreign;
    }

    public boolean isForeign() {
        return foreign;
    }

    public void setForeign(boolean foreign) {
        this.foreign = foreign;
    }

    

    public TypeMessage getType() {
        return type;
    }

    public void setType(TypeMessage type) {
        this.type = type;
    }

    public int getOra() {
        return ora;
    }

    public void setOra(int ora) {
        this.ora = ora;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

   
    
    
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    @Override
    public String toString() {
        return "Message{" +
                "user='" + user + '\'' +
                ", message='" + message + '\'' +
                ", destinatario='" + destinatario + '\'' +
                '}';
    }
}

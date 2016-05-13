package server;

/**
 *
 * @author prova
 */
public class Message {
    private String user;
    private String message;
    private String destinatario;

    public Message(String user, String message, String destinatario) {
        this.user = user;
        this.message = message;
        this.destinatario = destinatario;
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

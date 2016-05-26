package server;

import java.io.Serializable;

/**
 *
 * @author corradi , taioli
 */
public class Message implements Serializable {

    private String user;
    private String message;
    private String destinatario;
    private int ora;
    private int day;
    private TypeMessage type;
    private boolean foreign;
    private byte[] file;
    private String filename;

    public Message(String user, String message, String destinatario, int ora, int day, TypeMessage type) {
        this.user = user;
        this.message = message;
        this.destinatario = destinatario;
        this.ora = ora;
        this.day = day;
        this.type = type;
    }

    public Message(String user, String message, String destinatario, TypeMessage type, boolean foreign) {
        this.user = user;
        this.message = message;
        this.destinatario = destinatario;
        this.type = type;
        this.foreign = foreign;
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

    public Message(String user, byte[] file, String destinatario, int ora, int day, TypeMessage type, String filename) {
        this.user = user;
        this.file = file;
        this.destinatario = destinatario;
        this.ora = ora;
        this.day = day;
        this.type = type;
        this.filename = filename;
    }

    public Message(String user, byte[] file, String destinatario, int ora, int day, TypeMessage type, boolean foreign, String filename) {
        this.user = user;
        this.file = file;
        this.destinatario = destinatario;
        this.ora = ora;
        this.day = day;
        this.type = type;
        this.foreign = foreign;
        this.filename = filename;
    }

    public Message(String user, byte[] file, String destinatario, TypeMessage type, boolean foreign, String filename) {
        this.user = user;
        this.file = file;
        this.destinatario = destinatario;
        this.type = type;
        this.foreign = foreign;
        this.filename = filename;
    }

    @Override
    public String toString() {
        return "Message{" + "user=" + getUser() + ", message=" + getMessage() + ", destinatario=" + getDestinatario() + ", type=" + getType() + ", foreign=" + isForeign() + '}';
    }

    /**
     * @return the file
     */
    public byte[] getFile() {
        return file;
    }

    /**
     * @param file the file to set
     */
    public void setFile(byte[] file) {
        this.file = file;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the destinatario
     */
    public String getDestinatario() {
        return destinatario;
    }

    /**
     * @param destinatario the destinatario to set
     */
    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }

    /**
     * @return the ora
     */
    public int getOra() {
        return ora;
    }

    /**
     * @param ora the ora to set
     */
    public void setOra(int ora) {
        this.ora = ora;
    }

    /**
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * @return the type
     */
    public TypeMessage getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(TypeMessage type) {
        this.type = type;
    }

    /**
     * @return the foreign
     */
    public boolean isForeign() {
        return foreign;
    }

    /**
     * @param foreign the foreign to set
     */
    public void setForeign(boolean foreign) {
        this.foreign = foreign;
    }

    /**
     * @return the filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * @param filename the filename to set
     */
    public void setFilename(String filename) {
        this.filename = filename;
    }

}

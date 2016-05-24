package server;

/**
 * Classe che rappresenta un utente
 * @author Dalla Chiara Michele
 */
public class UserObject {

    private String userName;
    private String password;

    /**
     * Costruttore UserObject con acquisizione variabili processanti userName,
     * password
     *
     * @param userName
     * @param password
     */
    public UserObject(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    /**
     * Costruttore UserObject senza acquisizioni
     */
    public UserObject() {
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }


    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

}

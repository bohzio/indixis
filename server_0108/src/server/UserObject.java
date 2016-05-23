
package server;

  

/**
 * 
 * @author dallachiaram@gmail.com
 * @version 1.5
 */
public class UserObject {
    private String userName;
    private String password;
    
    /**
     * Costruttore UserObject con acquisizione variabili processanti userName, password
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
    public UserObject(){
    }
    
    //getter & setter attributi
    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

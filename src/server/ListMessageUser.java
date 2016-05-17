/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.util.ArrayList;

/**
 *
 * @author michele
 */
public class ListMessageUser {
    private String user;
    private ArrayList<Message> listMessage;
    
    public ListMessageUser(String user){
        this.user = user;
        listMessage = new ArrayList();
    }

    //getter & setter
    public String getUser() {
        return user;
    }
    
    public void setUser(String user) {
        this.user = user;
    }
    
    public void addMessage(Message message){
        listMessage.add(message);
    }
    
    public ArrayList<Message> getMessage(){
        return listMessage;
    }
}

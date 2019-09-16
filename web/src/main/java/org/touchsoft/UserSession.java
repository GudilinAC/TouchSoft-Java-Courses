package org.touchsoft;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class UserSession {
    public final int Id;

    //false - agent,
    //true - client
    private Boolean isClient;
    private UserSession pair;
    private SocketChannel channel;
    private User user;
    private LinkedList<String> sendList;
    private LinkedList<String> receiveList;

    public UserSession(int id) {
        this.Id = id;
        this.user = new User();
    }

    public Boolean isClient() {
        return isClient;
    }

    public void isClient(Boolean isClient) {
        this.isClient = isClient;
    }

    public UserSession getPair() {
        return pair;
    }

    public void setPair(UserSession pair) {
        this.pair = pair;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public void setChannel(SocketChannel channel) {
        this.channel = channel;
    }

    public LinkedList<String> getSendList() {
        return sendList;
    }

    public LinkedList<String> getReceiveList() {
        return receiveList;
    }

    public void receive(String str){
        receiveList.addLast(str);
    }

    public void store(String str){
        sendList.addLast(str);
    }
}

package org.touchsoft;

import java.nio.channels.SocketChannel;
import java.util.LinkedList;

public class UserSession {
    public final int Id;

    //false - agent,
    //true - client
    private Boolean type;
    private UserSession pair;
    private SocketChannel channel;
    private User user;
    private LinkedList<String> sendList;
    private LinkedList<String> receiveList;

    public UserSession(int id) {
        this.Id = id;
        this.user = new User();
    }

    public Boolean getType() {
        return type;
    }

    public void setType(Boolean type) {
        this.type = type;
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
}

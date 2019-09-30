package org.touchsoft;

import java.nio.channels.SocketChannel;

public class UserSession {
    //false - agent,
    //true - client

    //variable type is unclear
    //you can use enum userType {AGENT, CLIENT, ...}
    //then you can add some type of users in your app
    private Boolean type;
    private UserSession pair;
    private SocketChannel channel;
    private User user;

    public UserSession(SocketChannel channel) {
        this.channel = channel;
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
}

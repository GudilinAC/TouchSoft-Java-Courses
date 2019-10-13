package org.touchsoft.data;

import org.touchsoft.model.Message;
import org.touchsoft.model.User;
import org.touchsoft.model.UserSession;

import java.util.ArrayList;
import java.util.List;

public class Data {
    public final List<User> users = new ArrayList<>();
    public final List<Message> messages = new ArrayList<>();
    public final List<UserSession> sessions = new ArrayList<>();
    public final List<UserSession> freeAgents = new ArrayList<>();
    public final List<UserSession> waitingClients = new ArrayList<>();
    public final List<UserSession> activeChats = new ArrayList<>();


    public static final Data instance = new Data();
    private Data() {}
}

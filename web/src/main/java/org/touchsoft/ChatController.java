package org.touchsoft;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.util.*;

public class ChatController {
    private final static Logger log = LogManager.getLogger(ChatController.class);
    private final static ChatController instance = new ChatController();

    private ChatController(){}

    public static ChatController getInstance() {
        return instance;
    }

    private final Map<Integer, UserSession> users = new HashMap<>();
    private final UserController userController = new UserController();

    public String getMessages(int id){
        return String.join("\n", users.get(id).getReceiveList());
    }

    public int getNewUserId(){
        UserSession session = new UserSession(IdGen.getId());
        users.put(session.Id, session);
        return session.Id;
    }

    public void processString(int id, String str){
        if (str.startsWith("/register ")) {
            if (str.indexOf("agent ") == 10)
                userController.registerUser(users.get(id), false, str.substring(16));
            else if (str.indexOf("client ") == 10)
                userController.registerUser(users.get(id), true, str.substring(17));
            else send(id, "[System] Command not recognized. Reissue the command");
        } else if (str.startsWith("/exit")) {
            removeUser(id);
        } else if (userController.ifRegistered(users.get(id))) {
            if (str.startsWith("/leave"))
                leave(id);
            else
                redirect(id, str);
        } else send(id, "[System] You should register first");
    }

    private void removeUser(int id) {
        UserSession session = users.get(id);
        UserSession pair = userController.removeUser(session);
        if (pair != null)
            send(pair.Id, "[System] user disconnected");
        users.remove(id);
    }

    private void redirect(int id, String str)  {
        UserSession session = users.get(id);
        UserSession pairSession = userController.getPair(session);
        if (pairSession != null)
            send(pairSession.Id, "[" + session.getUser().getNick() + "] " + str);
        else {
            send(id, "[System] no available agents. Please wait and try again");
            store(id, "[" + session.getUser().getNick() + "] " + str);
        }
    }

    private void leave(int id) {
        UserSession session = users.get(id);
        UserSession pair = userController.leave(session);
        if (pair != null)
            send(pair.Id, "[System] client left");
    }

    private void send(int id, String str)  {
        users.get(id).receive(str);
    }

    private void store(int id, String str){
        users.get(id).store(str);
    }
}

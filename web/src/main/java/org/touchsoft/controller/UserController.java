package org.touchsoft.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.touchsoft.data.Data;
import org.touchsoft.model.UserSession;

import java.util.ArrayDeque;
import java.util.Queue;

public class UserController {
    private final static Logger log = LogManager.getLogger(UserController.class);

    private final Data data = Data.instance;

    public boolean ifRegistered(UserSession session) {
        return session.getUser().getNick() != null;
    }

    public void registerUser(UserSession session, boolean isClient, String nick) {
        session.isClient(isClient);
        session.getUser().setNick(nick);
        if (isClient)
            log.info("Registered client " + nick);
        else {
            data.freeAgents.add(session);
            log.info("Registered agent " + nick);
        }
    }

    public UserSession getPair(UserSession session) {
        if (session.getPair() == null)
            if (data.freeAgents.isEmpty())
                return null;
            else {
                if (!session.isClient()) return null;
                session.setPair(data.freeAgents.get(0));
                data.freeAgents.remove(0);
                session.getPair().setPair(session);
                String str;
                while (null != (str = session.getSendList().pollFirst()))
                    session.getPair().getReceiveList().addLast(str);
                log.info("Client " + session.getUser().getNick() + " join chat with agent " + session.getPair().getUser().getNick());
            }
        return session.getPair();
    }

    public UserSession leave(UserSession session) {
        if (session.getPair() == null) return null;
        if (!session.isClient()) return null;
        UserSession agent = session.getPair();
        log.info("Client " + session.getUser().getNick() + " leave chat with agent " + agent.getUser().getNick());
        agent.setPair(null);
        session.setPair(null);
        return agent;
    }

    public UserSession removeUser(UserSession session) {
        if (session.isClient())
            log.info("Client " + session.getUser().getNick() + " disconnected");
        else
            log.info("Agent " + session.getUser().getNick() + " disconnected");
        UserSession pair = session.getPair();
        if (!session.isClient() && data.freeAgents.contains(session))
            data.freeAgents.remove(session);
        else if (pair != null) {
            pair.setPair(null);
            if (!pair.isClient())
                data.freeAgents.add(pair);
        }
        return pair;
    }
}

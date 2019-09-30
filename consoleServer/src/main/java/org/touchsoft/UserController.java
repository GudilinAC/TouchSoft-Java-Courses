package org.touchsoft;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Queue;

public class UserController {
    private final static Logger log = LogManager.getLogger(UserController.class);

    private final Queue<UserSession> freeAgents = new ArrayDeque<>();

    //isRegistered is cleaner
    public boolean ifRegistered(UserSession session) {
        return session.getUser().getNick() != null;
    }

    public void registerUser(UserSession session, boolean type, String nick) {
        session.setType(type);
        session.getUser().setNick(nick);
        if (type)
            log.info("Registered client " + nick);
        else {
            freeAgents.add(session);
            log.info("Registered agent " + nick);
        }
    }

    public UserSession getPair(UserSession session) {
        if (session.getPair() == null)
            if (freeAgents.isEmpty())
                return null;
            else {
                if (!session.getType()) return null;
                session.setPair(freeAgents.poll());
                session.getPair().setPair(session);
                log.info("Client " + session.getUser().getNick() + " join chat with agent " + session.getPair().getUser().getNick());
            }
        return session.getPair();
    }
    //!!check. it's unstable. for example: when you have only one client and one agent
    //client enter "/leave" and that's all, nothing work.
    public UserSession leave(UserSession session) {
        if (session.getPair() == null) return null;
        if (!session.getType()) return null;
        UserSession agent = session.getPair();
        log.info("Client " + session.getUser().getNick() + " leave chat with agent " + agent.getUser().getNick());
        agent.setPair(null);
        session.setPair(null);
        return agent;
    }

    public UserSession removeUser(UserSession session) {
        if (session.getType())
            log.info("Client " + session.getUser().getNick() + " disconnected");
        else
            log.info("Agent " + session.getUser().getNick() + " disconnected");
        UserSession pair = session.getPair();
        if (!session.getType() && freeAgents.contains(session))
            freeAgents.remove(session);
        else if (pair != null) {
            pair.setPair(null);
            if (!pair.getType())
                freeAgents.add(pair);
        }
        return pair;
    }
}

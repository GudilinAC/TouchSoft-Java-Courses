package org.touchsoft;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Queue;

public class UserController {
    private final static Logger log = LogManager.getLogger(UserController.class);

    private final Queue<UserSession> freeAgents = new ArrayDeque<>();

    public boolean ifRegistered(UserSession session) {
        return session.getUser().getNick() != null;
    }

    public void registerUser(UserSession session, boolean isClient, String nick) {
        session.isClient(isClient);
        session.getUser().setNick(nick);
        if (isClient)
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
                if (!session.isClient()) return null;
                session.setPair(freeAgents.poll());
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
        if (!session.isClient() && freeAgents.contains(session))
            freeAgents.remove(session);
        else if (pair != null) {
            pair.setPair(null);
            if (!pair.isClient())
                freeAgents.add(pair);
        }
        return pair;
    }
}

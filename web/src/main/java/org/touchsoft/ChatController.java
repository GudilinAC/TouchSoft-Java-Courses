package org.touchsoft;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ChatController {
    private final static Logger log = LogManager.getLogger(ChatController.class);

    private final Map<Integer, UserSession> users = new HashMap<>();

}

package org.touchsoft;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;

public class Server {
    private final static Logger log = LogManager.getLogger(Server.class);
    private final ChannelController channelController;

    public Server(ChannelController controller){
        this.channelController = controller;
    }

    public void start() {
        Selector selector = ServerFactory.createServer("localhost");

        try {
            if (selector != null)
                while (selector.select() > -1) {
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isValid()) {
                            if (key.isAcceptable())
                                channelController.accept(key);
                            else if (key.isReadable())
                                channelController.read(key);
                        }
                    }
                }
        } catch (IOException e) {
        } finally {
            log.warn("Server shuts down");
        }
    }


}

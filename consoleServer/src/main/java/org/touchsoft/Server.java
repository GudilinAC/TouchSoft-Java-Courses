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
    private final Selector selector;

    public Server(ChannelController controller, Selector selector){
        this.channelController = controller;
        this.selector = selector;
    }

    public void start() {

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
                            else if (key.isWritable())
                                channelController.write(key);
                        }
                    }
                }
        } catch (IOException e) {
        } finally {
            log.warn("Server shuts down");
        }
    }
}

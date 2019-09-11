package org.touchsoft;

import java.nio.channels.Selector;

public class Main {
    public static void main(String[] args) {
        Selector selector = ServerFactory.createServer("localhost");
        new Server(new ChannelController(new UserController(), selector), selector).start();
    }
}

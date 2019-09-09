package org.touchsoft;

import org.junit.Test;

import java.nio.channels.Selector;

import static org.junit.Assert.*;

public class ServerFactoryTest {

    @Test
    public void createServer() {
        Selector selector = ServerFactory.createServer("localhost");
        assertNotNull(selector);
    }
}
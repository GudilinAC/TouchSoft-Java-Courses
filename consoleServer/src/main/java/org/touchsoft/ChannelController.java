package org.touchsoft;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class ChannelController {
    private final static Logger log = LogManager.getLogger(ChannelController.class);
    private final UserController userController;

    private final Map<SocketChannel, UserSession> users = new HashMap<>();
    private final Map<SocketChannel, String> toSend = new HashMap<>();
    private final Selector selector;

    public ChannelController(UserController controller, Selector selector) {
        this.userController = controller;
        this.selector = selector;
    }

    public void accept(SelectionKey key) {
        try {
            SocketChannel newChannel = ((ServerSocketChannel) key.channel()).accept();
            newChannel.configureBlocking(false);
            newChannel.register(key.selector(), SelectionKey.OP_READ);
            users.put(newChannel, sessionInit(newChannel));
        }
        catch (IOException e){
            log.warn("Cannot accept channel", e);
        }
    }

    private UserSession sessionInit(SocketChannel newChannel) {
        return new UserSession(newChannel);
    }

    public void read(SelectionKey key) {
        SocketChannel channel = ((SocketChannel) key.channel());
        try {
            String str = readInput(channel);
            if (str == null)
                key.cancel();
            else {
                log.debug(users.get(channel).getUser().getNick()+ ": " + str);
                processString(channel, str);
            }
        }
        catch (IOException e){
            log.warn("Channel can't be read", e);
            key.cancel();
            removeUser(channel);
        }
    }

    private String readInput(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int length = -1;

        try {
            //!!! here you have to use some buffer handler for searching whole messages in buffer
            //if client send message longer then 1024 then you'll send pieces of this message
            //at first you have to read whole message and then send it to client
            //(now it works because your client read data from buffer only when he find '\n')
            length = channel.read(buffer);
        } catch (IOException e) {
        }

        if (length == -1) {
            removeUser(channel);
            return null;
        }

        byte[] data = new byte[length];
        System.arraycopy(buffer.array(), 0, data, 0, length);

        return new String(data);
    }

    private void removeUser(SocketChannel channel) {
        UserSession session = users.get(channel);
        UserSession pair = userController.removeUser(session);
            try {
                if (pair != null)
                    toSend(pair.getChannel(), "System massage: user suddenly disconnected");
            } catch (IOException e) {
                log.warn("Can't accept user pair while removing him", e);
            }
        users.remove(channel);
    }

    private void processString(SocketChannel channel, String str) throws IOException {
        //!!client can register when he already registered
        if (str.startsWith("/register ")) {
            //you can use here regEx. if str doesn't contain "agent " indexOf will reach the end of str
            //regEx can be faster
            if (str.indexOf("agent ") == 10)
                userController.registerUser(users.get(channel), false, str.substring(16));
            else if (str.indexOf("client ") == 10)
                userController.registerUser(users.get(channel), true, str.substring(17));
            else toSend(channel, "System massage: command not recognized. Reissue the command");
        } else if (userController.ifRegistered(users.get(channel))) {
            if (str.startsWith("/leave"))
                leave(channel);
            else
                redirect(channel, str);
        } else toSend(channel, "System massage: you should register first");
    }

    private void redirect(SocketChannel channel, String str) throws IOException {
        UserSession session = users.get(channel);
        UserSession pairSession = userController.getPair(session);
        if (pairSession != null)
            toSend(pairSession.getChannel(), str);
        //!!!add '\n' in the end of the sending str. your client doesn't read this.
        //!!!save messages that client sends when he hasn't a pair. when you set him pair, send saved messages
            // (check conditions of the first task)
        else toSend(channel, "System massage: no available agents. Please wait and try again");
    }

    private void leave(SocketChannel channel) throws IOException{
        UserSession session = users.get(channel);
        UserSession pair = userController.leave(session);
        if (pair != null)
            toSend(pair.getChannel(), "System massage: client left");
    }

    private void toSend(SocketChannel channel, String str) throws IOException {
        if (channel.write(ByteBuffer.wrap(str.getBytes())) == 0) {
            toSend.put(channel, str);
            channel.register(selector, SelectionKey.OP_WRITE);
        }
    }

    public void write(SelectionKey key) {
        SocketChannel channel = (SocketChannel) key.channel();
        try {

            channel.write(ByteBuffer.wrap(toSend.get(channel).getBytes()));
            toSend.remove(channel);
            channel.register(selector, SelectionKey.OP_READ);
        }
        catch (IOException e){
            log.warn("Channel can't be written", e);
            key.cancel();
            removeUser(channel);
        }
    }
}

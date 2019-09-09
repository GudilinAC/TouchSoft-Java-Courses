package org.touchsoft;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

public class ChannelController {
    private final static Logger log = LogManager.getLogger(ChannelController.class);
    private final UserController userController;

    private final Map<SocketChannel, UserSession> users = new HashMap<>();
//    private final List<Pair<SocketChannel, String>> toSend = Collections.synchronizedList(new ArrayList<>());

    public ChannelController(UserController controller) {
        this.userController = controller;

//        Thread sendingThread = new Thread(this::send);
//        sendingThread.setDaemon(true);
//        sendingThread.start();
    }

    public void accept(SelectionKey key) throws IOException {
        SocketChannel newChannel = ((ServerSocketChannel) key.channel()).accept();
        newChannel.configureBlocking(false);
        newChannel.register(key.selector(), SelectionKey.OP_READ);
        users.put(newChannel, sessionInit(newChannel));
    }

    private UserSession sessionInit(SocketChannel newChannel) {
        return new UserSession(newChannel);
    }

    public void read(SelectionKey key) throws IOException {
        SocketChannel channel = ((SocketChannel) key.channel());
        String str = readInput(channel);
        if (str == null)
            key.cancel();
        else processString(channel, str);
    }

    private String readInput(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int length = -1;

        try {
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

    private void removeUser(SocketChannel channel) throws IOException {
        UserSession session = users.get(channel);
        UserSession pair = userController.removeUser(session);
        if (pair != null)
            write(pair.getChannel(), "System massage: user suddenly disconnected");
        users.remove(channel);
        channel.close();
    }

    private void processString(SocketChannel channel, String str) throws IOException {
        if (str.startsWith("/register ")) {
            if (str.indexOf("agent ") == 10)
                userController.registerUser(users.get(channel), false, str.substring(16));
            else if (str.indexOf("client ") == 10)
                userController.registerUser(users.get(channel), true, str.substring(17));
            else write(channel, "System massage: command not recognized. Reissue the command");
        } else if (userController.ifRegistered(users.get(channel))) {
            if (str.startsWith("/leave"))
                leave(channel);
            else
                redirect(channel, str);
        } else write(channel, "System massage: you should register first");
    }

    private void redirect(SocketChannel channel, String str) throws IOException {
        UserSession session = users.get(channel);
        UserSession pairSession = userController.getPair(session);
        if (pairSession != null)
            write(pairSession.getChannel(), str);
        else write(channel, "System massage: no available agents. Please wait and try again");
    }

    private void leave(SocketChannel channel) throws IOException{
        UserSession session = users.get(channel);
        UserSession pair = userController.leave(session);
        if (pair != null)
            write(pair.getChannel(), "System massage: client left");
    }

    private void write(SocketChannel channel, String str) throws IOException {
        channel.write(ByteBuffer.wrap(str.getBytes()));
//        synchronized (toSend) {
//            toSend.add(new Pair<>(channel, str));
//        }
    }

//    private void send() {
//        try {
//            while (true) {
//                synchronized (toSend) {
//                    Iterator<Pair<SocketChannel, String>> iterator = toSend.iterator();
//                    while (iterator.hasNext())
//                        try {
//                            Pair<SocketChannel, String> pair = iterator.next();
//                            if (0 < pair.getKey().write(ByteBuffer.wrap(pair.getValue().getBytes())))
//                                iterator.remove();
//                        } catch (IOException e) {
//                        }
//                }
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                }
//            }
//        } catch (Exception e) {
//            log.error("Sending thread crushed", e);
//        }
//    }
}

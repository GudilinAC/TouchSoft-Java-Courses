import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server {
    private Map<SocketChannel, UserSession> users = new HashMap<>();
    private Queue<UserSession> freeAgents = new ArrayDeque<>();

    public void start() {
        //new Thread(() -> {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress("localhost", 9876);
            serverSocketChannel.bind(address);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, serverSocketChannel.validOps());

            while (selector.select() > -1) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isValid()) {
                        if (key.isAcceptable())
                            accept(key);
                        else if (key.isConnectable())
                            connect(key);
                        else if (key.isReadable())
                            read(key);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //}).start();
    }

    private void accept(SelectionKey key) throws IOException {
        SocketChannel newChannel = ((ServerSocketChannel) key.channel()).accept();
        newChannel.configureBlocking(false);
        newChannel.register(key.selector(), SelectionKey.OP_READ);
        users.put(newChannel, sessionInit(newChannel));
    }

    private UserSession sessionInit(SocketChannel newChannel) {
        UserSession session = new UserSession(newChannel);
        session.setUser(new User());
        return session;
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = ((SocketChannel) key.channel());
        String str = readChannel(channel);
        if (str.startsWith("/register ")) {
            if (str.indexOf("agent ") == 10)
                registerUser(channel, false, str.substring(16));
            else if (str.indexOf("client ") == 10)
                registerUser(channel, true, str.substring(17));
            else {
                //TODO ask to repeat
            }
        } else if (str.equals("/leave"))
            exit(channel, true);
        else if (str.equals("/exit"))
            exit(channel, false);
        else
            redirect(channel, str);
    }

    private void registerUser(SocketChannel channel, boolean type, String nick) {
        UserSession session = users.get(channel);
        session.setType(type);
        session.getUser().setNick(nick);
        if (!type) freeAgents.add(session);
    }

    private void redirect(SocketChannel channel, String str) throws IOException {
        UserSession session = users.get(channel);
        if (session.getPair() == null) {
            if (freeAgents.isEmpty()) {
                //TODO ask to wait
            } else {
                session.setPair(freeAgents.poll());
                session.getPair().setPair(session);
            }
        }
        session.getPair().getChannel().write(ByteBuffer.wrap(str.getBytes()));
    }

    private void exit(SocketChannel channel, boolean leave) {
        UserSession session = users.get(channel);
        if (session.getType()) {
            if (leave) return;
            else freeAgents.add(session.getPair());
        } else
            freeAgents.add(session);

        session.getPair().setPair(null);
        session.setPair(null);
    }

    private String readChannel(SocketChannel channel) throws IOException {
        ByteBuffer bb = ByteBuffer.allocate(1000);
        channel.read(bb);
        bb.flip();
        String str = new String(bb.array());
        bb.clear();
        return str;
    }

    private void connect(SelectionKey key) {

    }
}

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Server {
    private final static Logger log = LogManager.getLogger(Server.class);

    private Map<SocketChannel, UserSession> users = new HashMap<>();
    private Queue<UserSession> freeAgents = new ArrayDeque<>();

    public void start() {
        try {
            Selector selector = Selector.open();
            ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
            InetSocketAddress address = new InetSocketAddress("localhost", 9876);
            serverSocketChannel.bind(address);
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

            while (selector.select() > -1) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isValid()) {
                        if (key.isAcceptable())
                            accept(key);
                        else if (key.isReadable())
                            read(key);
                    }
                }
            }

        } catch (IOException e) {
            log.error("Сервер наебнулся", e);
        }
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
        }
        catch (IOException e){
        }

        if (length == -1){
            removeUser(channel);
            return null;
        }

        byte[] data = new byte[length];
        System.arraycopy(buffer.array(), 0, data, 0, length);

        return new String(data);
    }

    private void removeUser(SocketChannel channel) throws IOException{
        UserSession session = users.get(channel);
        if (session.getType())
            log.info("Client " + session.getUser().getNick() + " disconnected");
        else
            log.info("Agent " + session.getUser().getNick() + " disconnected");
        UserSession pair = session.getPair();
        if (pair != null) {
            pair.getChannel().write(ByteBuffer.wrap("System massage: user suddenly disconnected".getBytes()));
            pair.setPair(null);
            if (!pair.getType())
                freeAgents.add(pair);
        }
        users.remove(channel);
        channel.close();
    }

    private void processString(SocketChannel channel, String str) throws IOException{
        if (str.startsWith("/register ")) {
            if (str.indexOf("agent ") == 10)
                registerUser(channel, false, str.substring(16));
            else if (str.indexOf("client ") == 10)
                registerUser(channel, true, str.substring(17));
            else channel.write(ByteBuffer.wrap("System massage: command not recognized. Reissue the command".getBytes()));
        } else if (users.get(channel).getUser().getNick() != null) {
            if (str.startsWith("/leave"))
                leave(channel);
            else
                redirect(channel, str);
        } else channel.write(ByteBuffer.wrap("System massage: you should register first".getBytes()));
    }

    private void registerUser(SocketChannel channel, boolean type, String nick) {
        UserSession session = users.get(channel);
        session.setType(type);
        session.getUser().setNick(nick);
        if (!type) {
            freeAgents.add(session);
            log.info("Registered user " + nick);
        } else log.info("Registered agent " + nick);
    }

    private void redirect(SocketChannel channel, String str) throws IOException {
        UserSession session = users.get(channel);
        if (session.getPair() == null) {
            if (freeAgents.isEmpty()) {
                channel.write(ByteBuffer.wrap("System massage: no available agents. Please wait and try again".getBytes()));
                return;
            } else {
                session.setPair(freeAgents.poll());
                session.getPair().setPair(session);
                log.info("User " + session.getUser().getNick() + " join chat with agent " + session.getPair().getUser().getNick());
            }
        }
        session.getPair().getChannel().write(ByteBuffer.wrap(str.getBytes()));
    }

    private void leave(SocketChannel channel) {
        UserSession session = users.get(channel);
        if (session.getPair() == null) return;
        if (session.getType())
            freeAgents.add(session.getPair());
        log.info("User " + session.getUser().getNick() + " left chat with agent " + session.getPair().getUser().getNick());
        session.getPair().setPair(null);
        session.setPair(null);
    }
}
//TODO make write method and tests

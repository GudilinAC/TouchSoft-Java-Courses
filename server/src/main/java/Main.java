public class Main {
    public static void main(String[] args) {
        new Server(new ChannelController(new UserController())).start();
    }
}

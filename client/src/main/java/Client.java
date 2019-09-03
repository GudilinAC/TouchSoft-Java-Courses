import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public void start(){
        try {
            Socket socket = new Socket("localhost", 9876);
            BufferedReader sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream sockOut = new PrintStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            PrintStream out = System.out;

            new Thread(() -> {
                transfer(in, sockOut);
             }).start();

            new Thread(() -> {
                transfer(sockIn, out);
            }).start();

        } catch (IOException e) {
            e.printStackTrace();//TODO log
        }
    }

    private void transfer(BufferedReader from, PrintStream to) {
        try {
            while (true) {
                to.println(from.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();//TODO log
        }
    }

}

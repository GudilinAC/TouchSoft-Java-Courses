import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client {
    public void start(){
        try {
            Socket socket = new Socket("localhost", 9876);
            BufferedReader sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter sockOut = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(System.out));

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

    private void transfer(BufferedReader from, BufferedWriter to) {
        try {
            to.write(from.readLine());
            to.flush();
        } catch (IOException e) {
            e.printStackTrace();//TODO log
        }
    }

}

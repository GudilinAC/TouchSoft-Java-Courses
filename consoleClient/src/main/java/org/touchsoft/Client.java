package org.touchsoft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class Client {
    public void start() {
        try {
            Socket socket = new Socket("localhost", 9876);
            BufferedReader sockIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintStream sockOut = new PrintStream(socket.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            PrintStream out = System.out;

            Thread read = new Thread(() -> {
                transfer(in, sockOut);
            });
            //don't use daemon threads for input-output
            read.setDaemon(true);
            read.start();

            Thread write = new Thread(() -> {
                transfer(sockIn, out);
            });
            write.setDaemon(true);
            write.start();

            read.join();

        } catch (IOException e) {
            System.out.println("Error: unable to connect to server!");
        } catch (InterruptedException e) {
        }
    }

    private void transfer(BufferedReader from, PrintStream to) {
        try {
            while (true) {
                String str = from.readLine();
                if (str.startsWith("/exit")){
                    //send message to server that you exit. server can sending to you some data in that moment
                    //you lose that data
                    System.exit(0);
                }
                else {
                    to.println(str);
                    to.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Error: server shuts down unexpectedly!");
            System.exit(1);
        }
    }

}

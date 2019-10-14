package web;

import utils.ProgramSettings;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Date;

public class WebThread extends Thread {

    private int port;
    private boolean verbose;

    public WebThread() {
        ProgramSettings a = new ProgramSettings();
        port = a.getPort();
        verbose = a.getVerbose();
    }

    @Override
    public void run() {
        try {
            ServerSocket serverConnect = new ServerSocket(port);

            if (verbose)
                System.out.println("Server started.\nListening for connections on port : " + port + " ...\n");

            while (true) {
                WebServer myServer = new WebServer(serverConnect.accept());

                if (verbose)
                    System.out.println("Connecton opened. (" + new Date() + ")");

                // Create dedicated thread to manage the client connection
                Thread thread = new Thread(myServer);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }
}

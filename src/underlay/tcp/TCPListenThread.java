package underlay.tcp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPListenThread implements Runnable {

    private final ServerSocket serverSocket;
    private volatile boolean active;

    public TCPListenThread(ServerSocket serverSocket, TCPAdapter hostAdapter) {
        this.serverSocket = serverSocket;
        active = true;
    }

    @Override
    public void run() {
        while(active) {
            try {
                Socket incomingConnection = serverSocket.accept();

            } catch (IOException e) {
                System.err.println("[TCP Listener] Could not acquire the incoming connection.");
                e.printStackTrace();
            }
        }
    }

    public void terminate() {
        active = false;
    }
}

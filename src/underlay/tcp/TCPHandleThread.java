package underlay.tcp;

import java.net.Socket;

public class TCPHandleThread implements Runnable {

    private final Socket incomingConnection;

    public TCPHandleThread(Socket incomingConnection) {
        this.incomingConnection = incomingConnection;
    }

    @Override
    public void run() {

    }
}

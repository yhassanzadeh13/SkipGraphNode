package underlay;

import underlay.javarmi.JavaRMIUnderlay;
import underlay.packets.RequestParameters;
import underlay.packets.RequestType;
import underlay.packets.ResponseParameters;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Represents the underlay layer of the skip-graph DHT. Handles node-to-node communication.
 */
public abstract class Underlay extends RequestHandler {

    private int port;
    private String address;
    private String fullAddress;

    public int getPort() {
        return port;
    }

    public String getAddress() {
        return address;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    /**
     * Initializes the underlay.
     * @param port the port that the underlay should be bound to.
     * @return true iff the initialization was successful.
     */
    public final boolean initialize(int port) {
        this.port = port;
        try {
            address = Inet4Address.getLocalHost().getHostAddress();
        } catch(UnknownHostException e) {
            System.err.println("[Underlay] Could not acquire the local host name during initialization.");
            e.printStackTrace();
            return false;
        }
        fullAddress = address + ":" + port;
        return initUnderlay(port);
    }

    /**
     * Contains the underlay-specific initialization procedures.
     * @param port the port that the underlay should be bound to.
     * @return true iff the initialization was successful.
     */
    protected abstract boolean initUnderlay(int port);

    /**
     * Can be used to send a request to a remote server that runs the same underlay architecture.
     * @param address address of the remote server.
     * @param port port of the remote server.
     * @param t type of the request.
     * @param p parameters of the request.
     * @return response emitted by the remote server.
     */
    public abstract ResponseParameters sendMessage(String address, int port, RequestType t, RequestParameters p);


    /**
     * Terminates the underlay.
     */
    public abstract void terminate();

    /**
     * Constructs a new default underlay. Must be initialized.
     * @return a new default underlay.
     */
    public static Underlay newDefaultUnderlay() {
        return new JavaRMIUnderlay();
    }
}

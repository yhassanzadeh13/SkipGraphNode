package underlay;

import middlelayer.MiddleLayer;
import underlay.javarmi.JavaRMIUnderlay;
import underlay.packets.Request;
import underlay.packets.RequestType;
import underlay.packets.Response;
import underlay.tcp.TCPUnderlay;

import java.net.Inet4Address;
import java.net.UnknownHostException;

/**
 * Represents the underlay layer of the skip-graph DHT. Handles node-to-node communication.
 */
public abstract class Underlay {

    private MiddleLayer middleLayer;

    private int port;
    private String address;
    private String fullAddress;

    public void setMiddleLayer(MiddleLayer middleLayer) {
        this.middleLayer = middleLayer;
    }

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
     * Dispatches a request to the middle layer and returns the response.
     * @param request the request.
     * @return emitted response.
     */
    public Response dispatchRequest(Request request) {
        return middleLayer.receive(request);
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
     * @param request the request.
     * @return response emitted by the remote server.
     */
    public abstract Response sendMessage(String address, int port, Request request);


    /**
     * Terminates the underlay.
     * @return true iff the termination was successful.
     */
    public abstract boolean terminate();

    /**
     * Constructs a new default underlay. Must be initialized and connected to the middle layer.
     * @return a new default underlay.
     */
    public static Underlay newDefaultUnderlay() {
        // By default, we use Java RMI Underlay.
        return new TCPUnderlay();
    }
}

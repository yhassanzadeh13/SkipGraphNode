package underlay.javarmi;

import underlay.Underlay;
import underlay.packets.RequestParameters;
import underlay.packets.RequestType;
import underlay.packets.ResponseParameters;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

/**
 * Java RMI connection underlay implementation.
 */
public class JavaRMIUnderlay extends Underlay {

    // Java RMI instance running at the host machine.
    JavaRMIHost host;

    /**
     * Connects to the Java RMI adapter of a remote server.
     * @param fullAddress address of the server in the form of IP:PORT
     * @return a remote Java RMI adapter.
     */
    public JavaRMIService remote(String fullAddress) {
        if(host == null) {
            System.err.println("[JavaRMIUnderlay] Host does not exist.");
            return null;
        }
        JavaRMIService remote;
        try {
            remote = (JavaRMIService) Naming.lookup("//" + fullAddress + "/node");
        } catch (Exception e) {
            System.err.println("[JavaRMIUnderlay] Could not connect to the remote RMI server!");
            return null;
        }
        return remote;
    }

    /**
     * Constructs a `JavaRMIHost` instance and binds it to the given port.
     * @param port the port that the underlay should be bound to.
     * @return true iff the Java RMI initialization was successful.
     */
    @Override
    protected boolean initUnderlay(int port) {
        try {
            host = new JavaRMIHost(this);
            // Bind this RMI adapter to the given port.
            LocateRegistry.createRegistry(port).bind("node", host);
        } catch (Exception e) {
            System.err.println("[JavaRMIUnderlay] Error while initializing at port " + port);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Invokes the appropriate RMI method on the server with the given address.
     * @param address address of the remote server.
     * @param port port of the remote server.
     * @param t type of the request.
     * @param p parameters of the request.
     * @return response received from the server.
     */
    @Override
    public ResponseParameters sendMessage(String address, int port, RequestType t, RequestParameters p) {
        if(host == null) {
            System.err.println("[JavaRMIUnderlay] Host does not exist.");
            return null;
        }
        // Connect to the remote adapter.
        JavaRMIService remote = remote(address + ":" + port);
        if(remote == null) {
            System.err.println("[JavaRMIUnderlay] Could not connect to the address: " + address + ":" + port);
            return null;
        }
        // Use the remote handler to dispatch the request.
        try {
            return remote.handleRequest(t, p);
        } catch (Exception e) {
            System.err.println("[JavaRMIUnderlay] Could not send the message.");
            System.err.println();
            return null;
        }
    }

    // Java RMI termination is handled automatically.
    @Override
    public void terminate() {
    }
}

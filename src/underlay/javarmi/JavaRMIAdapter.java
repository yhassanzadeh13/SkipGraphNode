package underlay.javarmi;

import underlay.AckResponse;
import underlay.ConnectionAdapter;
import underlay.ResponseParameters;

import java.net.Inet4Address;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Java RMI connection adapter implementation.
 */
public class JavaRMIAdapter extends UnicastRemoteObject implements ConnectionAdapter {

    private String address;

    /**
     * Initializes Java RMI on this machine.
     * @return whether the construction was successful.
     */
    @Override
    public boolean construct(int port) {
        try {
            // Bind this RMI adapter to the given port.
            LocateRegistry.createRegistry(port).bind("node", this);
            // Save the address.
            address = Inet4Address.getLocalHost().getHostAddress() + ":" + port;
        } catch (Exception e) {
            System.err.println("[Java RMI] Error while initializing!");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Connects to the Java RMI adapter of a remote server.
     * @param address address of the server in the form of IP:PORT
     * @return a remote Java RMI adapter.
     */
    @Override
    public JavaRMIAdapter remote(String address) {
        JavaRMIAdapter remote;
        try {
            remote = (JavaRMIAdapter) Naming.lookup("//" + address + "/node");
        } catch (Exception e) {
            System.err.println("[Java RMI] Could not connect to the remote RMI server!");
            return null;
        }
        return remote;
    }

    @Override
    public String getAddress() {
        return address;
    }

    protected JavaRMIAdapter() throws RemoteException {
        // TODO
    }

    @Override
    public ResponseParameters searchByNameID(String targetNameID) {
        // TODO
        return new AckResponse();
    }

    @Override
    public ResponseParameters searchByNumID(String targetNumID) {
        // TODO
        return new AckResponse();
    }

    @Override
    public ResponseParameters nameIDLevelSearch(Integer level, String targetNameID) {
        // TODO
        return new AckResponse();
    }

    @Override
    public ResponseParameters updateLeftNode(Integer level, String newValue) {
        // TODO
        return new AckResponse();
    }

    @Override
    public ResponseParameters updateRightNode(Integer level, String newValue) {
        // TODO
        return new AckResponse();
    }

    @Override
    public void destruct() {
    }
}

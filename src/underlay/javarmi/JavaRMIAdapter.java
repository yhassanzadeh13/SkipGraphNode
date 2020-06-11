package underlay.javarmi;

import underlay.ConnectionAdapter;
import underlay.packets.ResponseParameters;

import java.net.Inet4Address;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

/**
 * Java RMI connection adapter implementation. This adapter has two possible states: (1) it represents the service
 * on the host machine, (2) it represents a connection to a remote interface.
 */
public class JavaRMIAdapter implements ConnectionAdapter {

    private JavaRMIService service;
    private final boolean isRemote;

    /**
     * Adapter represents the host machine.
     */
    public JavaRMIAdapter() {
        isRemote = false;
        service = null;
    }

    /**
     * Adapter is a connection to the remote service.
     * @param remote the remote service.
     */
    public JavaRMIAdapter(JavaRMIService remote) {
        isRemote = true;
        this.service = remote;
    }

    /**
     * Initializes Java RMI on this machine.
     * @param port the port to bind.
     * @return whether the construction was successful.
     */
    @Override
    public boolean initialize(int port) {
        if(isRemote) {
            System.err.println("[Java RMI] Attempted to reconstruct a remote Java RMI service.");
            return false;
        }
        try {
            String address = Inet4Address.getLocalHost().getHostAddress() + ":" + port;
            service = new JavaRMIHost(address);
            // Bind this RMI adapter to the given port.
            LocateRegistry.createRegistry(port).bind("node", service);
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
    public ConnectionAdapter remote(String address) {
        if(isRemote) {
            System.err.println("[Java RMI] Attempted to create a new connection through a remote Java RMI service.");
            return null;
        }
        JavaRMIService remote;
        try {
            remote = (JavaRMIService) Naming.lookup("//" + address + "/node");
        } catch (Exception e) {
            System.err.println("[Java RMI] Could not connect to the remote RMI server!");
            return null;
        }
        // Wrap the remote interface with the adapter.
        return new JavaRMIAdapter(remote);
    }

    /**
     * The functions below delegate the method calls to the underlying `service` interface, whether it is remote or not.
     * However, we will perform the error handling here.
     */

    @Override
    public String getAddress() {
        try {
            return service.getAddress();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseParameters searchByNameID(String targetNameID) {
        try {
            return service.searchByNameID(targetNameID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseParameters searchByNumID(int targetNumID) {
        try {
            return service.searchByNumID(targetNumID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseParameters nameIDLevelSearch(int level, String targetNameID) {
        try {
            return service.nameIDLevelSearch(level, targetNameID);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseParameters updateLeftNode(int level, String newValue) {
        try {
            return service.updateLeftNode(level, newValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResponseParameters updateRightNode(int level, String newValue) {
        try {
            return service.updateRightNode(level, newValue);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void destruct() {
    }
}

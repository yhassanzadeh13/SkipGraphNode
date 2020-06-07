package underlay.javarmi;

import underlay.ConnectionAdapter;
import underlay.RequestResponse;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Java RMI connection adapter implementation.
 */
public class JavaRMIAdapter extends UnicastRemoteObject implements ConnectionAdapter {

    /**
     * Initializes Java RMI on this machine.
     */
    @Override
    public void construct() {
        try {
            Naming.rebind("node", this);
        } catch (Exception e) {
            System.err.println("[Java RMI] Error while binding!");
            e.printStackTrace();
        }
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

    protected JavaRMIAdapter() throws RemoteException {
        // TODO
    }

    @Override
    public RequestResponse searchByNameID(String targetNameID) {
        // TODO
        return null;
    }

    @Override
    public RequestResponse searchByNumID(String targetNumID) {
        // TODO
        return null;
    }

    @Override
    public RequestResponse nameIDLevelSearch(Integer level, String targetNameID) {
        // TODO
        return null;
    }

    @Override
    public RequestResponse updateLeftNode(Integer level, String newValue) {
        // TODO
        return null;
    }

    @Override
    public RequestResponse updateRightNode(Integer level, String newValue) {
        // TODO
        return null;
    }

    @Override
    public void destruct() {
    }
}

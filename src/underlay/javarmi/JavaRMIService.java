package underlay.javarmi;

import underlay.packets.ResponseParameters;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Represents a Java RMI Service. This does not extend the `ConnectionAdapter` since the `ConnectionAdapter` interface
 * does not have errors in their signature. In other words, we need to *wrap* the implementation of this interface with
 * a class implementing `ConnectionAdapter` that performs delegation and error handling.
 */
public interface JavaRMIService extends Remote {

    /**
     * We require each RPC client to be able to handle these skip-graph primitives.
     */
    // Returns the address of the RPC client.
    String getAddress() throws RemoteException;
    // Performs a name ID search from this client.
    ResponseParameters searchByNameID(String targetNameID) throws RemoteException;
    // Performs a numerical ID search from this client.
    ResponseParameters searchByNumID(int targetNumID) throws RemoteException;
    // Performs a name ID search from this client at the given level.
    ResponseParameters nameIDLevelSearch(int level, String targetNameID) throws RemoteException;
    // Updates the left neighbor of this node at the given level with the new given value.
    ResponseParameters updateLeftNode(int level, String newValue) throws RemoteException;
    // Updates the right neighbor of this node at the given level with the new given value.
    ResponseParameters updateRightNode(int level, String newValue) throws RemoteException;
}

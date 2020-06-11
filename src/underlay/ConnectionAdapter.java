package underlay;

import underlay.packets.ResponseParameters;


/**
 * Represents an interface for an RPC adapter. RPC mechanisms can be implemented by implementing this interface and
 * setting it as the adapter in the appropriate `Underlay` object.
 */
public interface ConnectionAdapter {

    // Initializes the adapter on the host machine at the given port.
    boolean initialize(int port);
    // Terminates the adapter service on the host machine.
    void destruct();
    // Connects to the remote machine's adapter.
    ConnectionAdapter remote(String address);

    /**
     * We require each RPC client to be able to handle these skip-graph primitives.
     */
    // Returns the address of the RPC client.
    String getAddress();
    // Performs a name ID search from this client.
    ResponseParameters searchByNameID(String targetNameID);
    // Performs a numerical ID search from this client.
    ResponseParameters searchByNumID(int targetNumID);
    // Performs a name ID search from this client at the given level.
    ResponseParameters nameIDLevelSearch(int level, String targetNameID);
    // Updates the left neighbor of this node at the given level with the new given value.
    ResponseParameters updateLeftNode(int level, String newValue);
    // Updates the right neighbor of this node at the given level with the new given value.
    ResponseParameters updateRightNode(int level, String newValue);
}

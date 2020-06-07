package underlay;

/**
 * Represents an interface for an RPC adapter. RPC mechanisms can be implemented by implementing this interface and
 * setting it as the adapter in the appropriate `Underlay` object.
 */
public interface ConnectionAdapter {

    // Initializes the adapter on the host machine.
    void construct();
    // Terminates the adapter service on the host machine.
    void destruct();
    // Connects to the remote machine's adapter.
    ConnectionAdapter remote(String address);

    /**
     * We require each RPC client to be able to handle these skip-graph primitives.
     */
    // Performs a name ID search from this client.
    RequestResponse searchByNameID(String targetNameID) throws Exception;
    // Performs a numerical ID search from this client.
    RequestResponse searchByNumID(String targetNumID) throws Exception;
    // Performs a name ID search from this client at the given level.
    RequestResponse nameIDLevelSearch(Integer level, String targetNameID) throws Exception;
    // Updates the left neighbor of this node at the given level with the new given value.
    RequestResponse updateLeftNode(Integer level, String newValue) throws Exception;
    // Updates the right neighbor of this node at the given level with the new given value.
    RequestResponse updateRightNode(Integer level, String newValue) throws Exception;
}

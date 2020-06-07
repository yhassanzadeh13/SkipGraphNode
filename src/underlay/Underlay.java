package underlay;

/**
 * Represents the underlay layer of the skip-graph DHT. Handles node-to-node communication.
 */
public class Underlay {

    private ConnectionAdapter connectionAdapter;

    /**
     * Constructs the underlay.
     * @param adapter default connection adapter.
     */
    public Underlay(ConnectionAdapter adapter) {
        // Initialize & register the underlay connection adapter.
        adapter.construct();
        connectionAdapter = adapter;
    }

    /**
     * Can be used to update the connection adapter of the underlay layer.
     * @param newAdapter new connection adapter.
     */
    public void setConnectionAdapter(ConnectionAdapter newAdapter) {
        if(connectionAdapter != null) connectionAdapter.destruct();
        newAdapter.construct();
        connectionAdapter = newAdapter;
    }

    /**
     * Can be used to send a message to a remote server that runs the same underlay architecture.
     * @param address address of the remote server.
     * @param t type of the request.
     * @param p parameters of the request.
     * @return response emitted by the remote server.
     */
    public RequestResponse sendMessage(String address, RequestType t, RequestParameters p) {
        // Connect to the remote adapter.
        ConnectionAdapter remote = connectionAdapter.remote(address);
        if(remote == null) {
            System.err.println("[Underlay] Could not send the message.");
            return null;
        }
        // Transform the request to RMI invocations.
        try {
            switch (t) {
                case SearchByNameID:
                    return remote.searchByNameID((String) p.getRequestValue("targetNameID"));
                case SearchByNumID:
                    return remote.searchByNumID((String) p.getRequestValue("targetNumID"));
                case NameIDLevelSearch:
                    return remote.nameIDLevelSearch((Integer) p.getRequestValue("level"),
                            (String) p.getRequestValue("targetNameID"));
                case UpdateLeftNode:
                    return remote.updateLeftNode((Integer) p.getRequestValue("level"),
                            (String) p.getRequestValue("newValue"));
                case UpdateRightNode:
                    return remote.updateRightNode((Integer) p.getRequestValue("level"),
                            (String) p.getRequestValue("newValue"));
            }
        } catch (Exception e) {
            System.err.println();
            return null;
        }
        return null;
    }
}

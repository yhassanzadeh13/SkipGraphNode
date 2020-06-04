package underlay;

import java.rmi.Naming;

public class Underlay {

    private ConnectionAdapter connectionAdapter;

    public Underlay(ConnectionAdapter adapter) {
        // Initialize & register the underlay connection adapter.
        adapter.construct();
        connectionAdapter = adapter;
    }

    public void setConnectionAdapter(ConnectionAdapter newAdapter) {
        if(connectionAdapter != null) connectionAdapter.destruct();
        newAdapter.construct();
        connectionAdapter = newAdapter;
    }

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

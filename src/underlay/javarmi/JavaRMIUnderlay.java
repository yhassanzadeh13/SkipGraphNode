package underlay.javarmi;

import underlay.RequestParameters;
import underlay.RequestResponse;
import underlay.RequestType;
import underlay.Underlay;

import java.rmi.Naming;

public class JavaRMIUnderlay extends Underlay {

    public JavaRMIUnderlay() {
        // Construct & register the underlay connection adapter.
        try {
            connectionAdapter = new JavaRMIAdapter();
            Naming.rebind("node", (JavaRMIAdapter) connectionAdapter);
        } catch(Exception e) {
            System.err.println("Could not construct the underlay adapter.");
        }
    }

    @Override
    public RequestResponse sendMessage(String address, RequestType t, RequestParameters p) {
        // Connect to the remote Java RMI underlay adapter.
        JavaRMIService remote;
        try {
            remote = (JavaRMIService) Naming.lookup("//" + address + "/node");
        } catch (Exception e) {
            System.err.println("Could not connect to the remote RMI server.");
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

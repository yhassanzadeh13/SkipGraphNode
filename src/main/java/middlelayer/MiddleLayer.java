package middlelayer;

import overlay.Overlay;
import underlay.Underlay;
import underlay.packets.RequestParameters;
import underlay.packets.RequestType;
import underlay.packets.ResponseParameters;

/**
 * Represents a mediator between the overlay and the underlay. The requests coming from the underlay are directed
 * to the overlay and the responses emitted by the overlay are returned to the underlay. The requests coming from
 * the overlay are either directed to the underlay or to another local overlay, and the emitted response is returned
 * to the overlay.
 */
public class MiddleLayer {

    private final Underlay underlay;
    private final Overlay overlay;

    public MiddleLayer(Underlay underlay, Overlay overlay) {
        this.underlay = underlay;
        this.overlay = overlay;
    }

    /**
     * Called by the overlay to send requests to the underlay.
     * @param destinationAddress destination address.
     * @param port destination port.
     * @param t type of the request.
     * @param parameters request parameters.
     * @return the response emitted by the remote client.
     */
    public ResponseParameters send(String destinationAddress, int port, RequestType t, RequestParameters parameters) {
        // TODO: check if the dest. address is identical to the address of this node
        //  and relay the request to a different local node.
        return underlay.sendMessage(destinationAddress, port, t, parameters);
    }

    /**
     * Called by the underlay to collect the response from the overlay.
     * @return response emitted by the overlay.
     */
    public ResponseParameters receive(RequestType t, RequestParameters p) {
        return switch (t) {
            case SearchByNameID -> overlay.searchByNameID((String) p.getRequestValue("targetNameID"));
            case SearchByNumID -> overlay.searchByNumID((Integer) p.getRequestValue("targetNumID"));
            case NameIDLevelSearch -> overlay.nameIDLevelSearch((Integer) p.getRequestValue("level"),
                    (String) p.getRequestValue("targetNameID"));
            case UpdateLeftNode -> overlay.updateLeftNode((Integer) p.getRequestValue("level"),
                    (String) p.getRequestValue("newValue"));
            case UpdateRightNode -> overlay.updateRightNode((Integer) p.getRequestValue("level"),
                    (String) p.getRequestValue("newValue"));
        };
    }
}

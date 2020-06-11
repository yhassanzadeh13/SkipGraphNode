package underlay.javarmi;

import underlay.packets.AckResponse;
import underlay.packets.ResponseParameters;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Represents the Java RMI Service implementation.
 */
public class JavaRMIHost extends UnicastRemoteObject implements JavaRMIService {

    // Full address of the client that this service is constructed on.
    private final String address;

    public JavaRMIHost(String address) throws RemoteException {
        this.address = address;
    }

    @Override
    public String getAddress() {
        return address;
    }

    @Override
    public ResponseParameters searchByNameID(String targetNameID) {
        // TODO: send to overlay
        return new AckResponse();
    }

    @Override
    public ResponseParameters searchByNumID(int targetNumID) {
        // TODO: send to overlay
        return new AckResponse();
    }

    @Override
    public ResponseParameters nameIDLevelSearch(int level, String targetNameID) {
        // TODO: send to overlay
        return new AckResponse();
    }

    @Override
    public ResponseParameters updateLeftNode(int level, String newValue) {
        // TODO: send to overlay
        return new AckResponse();
    }

    @Override
    public ResponseParameters updateRightNode(int level, String newValue) {
        // TODO: send to overlay
        return new AckResponse();
    }
}

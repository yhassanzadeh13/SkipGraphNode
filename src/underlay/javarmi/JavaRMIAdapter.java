package underlay.javarmi;

import underlay.RequestResponse;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class JavaRMIAdapter extends UnicastRemoteObject implements JavaRMIAdapterInterface {

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
}

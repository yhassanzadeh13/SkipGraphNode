package underlay.javarmi;

import underlay.ConnectionAdapter;
import underlay.RequestResponse;

import java.rmi.Remote;
import java.rmi.RemoteException;

// Exposes a Java RMI service.
public interface JavaRMIService extends ConnectionAdapter, Remote {
    RequestResponse searchByNameID(String targetNameID) throws RemoteException;
    RequestResponse searchByNumID(String targetNumID) throws RemoteException;
    RequestResponse nameIDLevelSearch(Integer level, String targetNameID) throws RemoteException;
    RequestResponse updateLeftNode(Integer level, String newValue) throws RemoteException;
    RequestResponse updateRightNode(Integer level, String newValue) throws RemoteException;
}

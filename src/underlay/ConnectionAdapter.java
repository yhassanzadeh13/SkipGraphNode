package underlay;

public interface ConnectionAdapter {

    // Initializes the adapter on the host machine.
    void construct();
    // Terminates the adapter service on the host machine.
    void destruct();
    // Connects to the remote machine's adapter.
    ConnectionAdapter remote(String address);

    RequestResponse searchByNameID(String targetNameID) throws Exception;
    RequestResponse searchByNumID(String targetNumID) throws Exception;
    RequestResponse nameIDLevelSearch(Integer level, String targetNameID) throws Exception;
    RequestResponse updateLeftNode(Integer level, String newValue) throws Exception;
    RequestResponse updateRightNode(Integer level, String newValue) throws Exception;
}

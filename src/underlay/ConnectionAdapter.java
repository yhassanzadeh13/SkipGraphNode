package underlay;

public interface ConnectionAdapter {
    RequestResponse searchByNameID(String targetNameID) throws Exception;
    RequestResponse searchByNumID(String targetNumID) throws Exception;
    RequestResponse nameIDLevelSearch(Integer level, String targetNameID) throws Exception;
    RequestResponse updateLeftNode(Integer level, String newValue) throws Exception;
    RequestResponse updateRightNode(Integer level, String newValue) throws Exception;
}

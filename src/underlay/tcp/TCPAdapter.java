package underlay.tcp;

import underlay.ConnectionAdapter;
import underlay.packets.ResponseParameters;


public class TCPAdapter implements ConnectionAdapter {

    @Override
    public boolean initialize(int port) {
        return false;
    }

    @Override
    public void destruct() {

    }

    @Override
    public ConnectionAdapter remote(String address) {
        return null;
    }

    @Override
    public String getAddress() {
        return null;
    }

    @Override
    public ResponseParameters searchByNameID(String targetNameID) {
        return null;
    }

    @Override
    public ResponseParameters searchByNumID(int targetNumID) {
        return null;
    }

    @Override
    public ResponseParameters nameIDLevelSearch(int level, String targetNameID) {
        return null;
    }

    @Override
    public ResponseParameters updateLeftNode(int level, String newValue) {
        return null;
    }

    @Override
    public ResponseParameters updateRightNode(int level, String newValue) {
        return null;
    }
}

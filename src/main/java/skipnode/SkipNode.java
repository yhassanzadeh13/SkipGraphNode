package skipnode;

import lookup.LookupTable;
import middlelayer.MiddleLayer;
import underlay.Underlay;

public class SkipNode implements SkipNodeInterface {
    /**
     * Attributes
     */
    private final String address;
    private final int port;
    private final int numID;
    private final String nameID;
    private final LookupTable lookupTable;

    private MiddleLayer middleLayer;

    public SkipNode(SkipNodeIdentity snID, LookupTable lookupTable) {
        this.address = snID.getAddress();
        this.port = snID.getPort();
        this.numID = snID.getNumID();
        this.nameID = snID.getNameID();
        this.lookupTable = lookupTable;
    }

    @Override
    public void setMiddleLayer(MiddleLayer middleLayer) {
        this.middleLayer=middleLayer;
    }

    @Override
    public boolean insert(SkipNodeInterface sn, String introducerAddress) {
        // TODO Implement
        return false;
    }

    @Override
    public boolean delete() {
        // TODO Implement
        return false;
    }

    @Override
    public SkipNodeIdentity searchByNumID(int numID) {
        // TODO Implement
        return LookupTable.EMPTY_NODE;
    }

    @Override
    public SkipNodeIdentity searchByNameID(String targetNameID) {
        if(nameID.equals(targetNameID)) return new SkipNodeIdentity(nameID, numID, address, port);

        int level = SkipNodeIdentity.commonBits(nameID, targetNameID);
        SkipNodeIdentity leftLadder = lookupTable.GetLeft(level);
        SkipNodeIdentity rightLadder = lookupTable.GetRight(level);
        SkipNodeIdentity chosenLadder = LookupTable.EMPTY_NODE;

        while(!leftLadder.equals(LookupTable.EMPTY_NODE) || !rightLadder.equals(LookupTable.EMPTY_NODE)) {
            int leftLadderHeight = (leftLadder.equals(LookupTable.EMPTY_NODE)) ? -1 : SkipNodeIdentity.commonBits(leftLadder.getNameID(), targetNameID);
            if(leftLadderHeight > level) {
                chosenLadder = leftLadder;
                break;
            } else if(!leftLadder.equals(LookupTable.EMPTY_NODE)) {
                leftLadder = middleLayer.getLeftNode(leftLadder.getAddress(), leftLadder.getPort(), level);
            }
            int rightLadderHeight = (rightLadder.equals(LookupTable.EMPTY_NODE)) ? -1 : SkipNodeIdentity.commonBits(rightLadder.getNameID(), targetNameID);
            if(rightLadderHeight > level) {
                chosenLadder = rightLadder;
                break;
            } else if(!rightLadder.equals(LookupTable.EMPTY_NODE)) {
                rightLadder = middleLayer.getRightNode(rightLadder.getAddress(), rightLadder.getPort(), level);
            }
        }

        if(chosenLadder.equals(LookupTable.EMPTY_NODE)) {
            return new SkipNodeIdentity(nameID, numID, address, port);
        }
        return middleLayer.searchByNameID(chosenLadder.getAddress(), chosenLadder.getPort(), targetNameID);
    }

    @Override
    public SkipNodeIdentity nameIDLevelSearch(int level, int direction, String nameID) {
        // TODO Implement
        return LookupTable.EMPTY_NODE;
    }

    @Override
    public SkipNodeIdentity updateLeftNode(SkipNodeIdentity snId, int level) {
        return lookupTable.UpdateLeft(snId, level);
    }

    @Override
    public SkipNodeIdentity updateRightNode(SkipNodeIdentity snId, int level) {
        return lookupTable.UpdateRight(snId, level);
    }
}

package skipnode;

import lookup.LookupTable;
import underlay.Underlay;

public class SkipNode implements SkipNodeInterface{
    /**
     * Attributes
     */
    private String address;
    private int numID;
    private String nameID;
    private LookupTable lookupTable;

    //TODO: Maybe implement a middle layer to handle communications b/w overlay and underlay
    private Underlay underlay;

    public SkipNode(SkipNodeIdentity snID, LookupTable lookupTable, Underlay underlay){
        this.address = snID.getAddress();
        this.numID = snID.getNumID();
        this.nameID = snID.getNameID();
        this.lookupTable = lookupTable;
        this.underlay = underlay;
    }


    @Override
    public boolean insert(SkipNodeInterface sn, String introducerAddress) {
        //TODO Implement
        return false;
    }

    @Override
    public boolean delete() {
        //TODO Implement
        return false;
    }

    @Override
    public SkipNodeIdentity searchByNumID(int numID) {
        //TODO Implement
        return LookupTable.EMPTY_NODE;
    }

    @Override
    public SkipNodeIdentity searchByNameID(String nameID) {
        //TODO Implement
        return LookupTable.EMPTY_NODE;
    }

    @Override
    public SkipNodeIdentity UpdateLeftNode(SkipNodeIdentity snId, int level) {
        return lookupTable.UpdateLeft(snId, level);
    }

    @Override
    public SkipNodeIdentity UpdateRightNode(SkipNodeIdentity snId, int level) {
        return lookupTable.UpdateRight(snId, level);
    }
}

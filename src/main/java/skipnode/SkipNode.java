package skipnode;

import lookup.LookupTable;
import middlelayer.MiddleLayer;
import underlay.Underlay;

public class SkipNode implements SkipNodeInterface {
    /**
     * Attributes
     */
    private int port;
    private String address;
    private int numID;
    private String nameID;
    private LookupTable lookupTable;

    private MiddleLayer middleLayer;


    public SkipNode(SkipNodeIdentity snID, LookupTable lookupTable){
        this.address = snID.getAddress();
        this.numID = snID.getNumID();
        this.nameID = snID.getNameID();
        this.port = snID.getPort();
        this.lookupTable = lookupTable;
    }

    public int getNumID(){
        return numID;
    }

    public String getNameID(){
        return nameID;
    }

    public LookupTable getLookupTable(){
        return lookupTable;
    }

    public SkipNodeIdentity getIdentity(){
        return new SkipNodeIdentity(nameID, numID, address, port);
    }

    @Override
    public void setMiddleLayer(MiddleLayer middleLayer) {
        this.middleLayer=middleLayer;
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
        // If this is the node the search request is looking for, return its identity
        if (numID == this.numID) {
            return getIdentity();
        }

        // Initialize the level to begin looking at
        int level = lookupTable.getNumLevels();

        // If the target is greater than this node's numID, the search should continue to the right
        if (this.numID < numID){
            // Start from the top, while there is no right neighbor, or the right neighbor's num ID is greater than what we are searching for
            // keep going down
            while(level>=0){
                if (lookupTable.GetRight(level)==LookupTable.EMPTY_NODE ||
                        lookupTable.GetRight(level).getNumID() > numID){
                    level--;
                }
            }

            // If the level is less than zero, then this node is the closest node to the numID being searched for from the right. Return.
            if (level < 0) {
                return getIdentity();
            }

            // Else, delegate the search to that node on the right
            SkipNodeIdentity delegateNode = lookupTable.GetRight(level);
            return middleLayer.searchByNumID(delegateNode.getAddress(), delegateNode.getPort(), numID);
        }else{
            // Start from the top, while there is no right neighbor, or the right neighbor's num ID is greater than what we are searching for
            // keep going down
            while(level>=0){
                if (lookupTable.GetLeft(level)==LookupTable.EMPTY_NODE ||
                        lookupTable.GetLeft(level).getNumID() > numID){
                    level--;
                }
            }

            // If the level is less than zero, then this node is the closest node to the numID being searched for from the right. Return.
            if (level < 0) {
                return getIdentity();
            }

            // Else, delegate the search to that node on the right
            SkipNodeIdentity delegateNode = lookupTable.GetLeft(level);
            return middleLayer.searchByNumID(delegateNode.getAddress(), delegateNode.getPort(), numID);
        }
    }

    @Override
    public SkipNodeIdentity searchByNameID(String nameID) {
        //TODO Implement
        return LookupTable.EMPTY_NODE;
    }

    @Override
    public SkipNodeIdentity nameIDLevelSearch(int level, int direction, String nameID) {
        //TODO Implement
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

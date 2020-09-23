package skipnode;

import lookup.LookupTable;
import middlelayer.MiddleLayer;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

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

    private boolean inserted = false;
    private final InsertionLock insertionLock = new InsertionLock();
    private final LinkedBlockingDeque<SkipNodeIdentity> ownedLocks = new LinkedBlockingDeque<>();
    // Incremented after each lookup table update.
    private int version = 0;

    // The identity to be returned in case the node is currently unreachable (i.e., being inserted.)
    private static final SkipNodeIdentity unavailableIdentity = LookupTable.EMPTY_NODE;

    public SkipNode(SkipNodeIdentity snID, LookupTable lookupTable) {
        this.address = snID.getAddress();
        this.port = snID.getPort();
        this.numID = snID.getNumID();
        this.nameID = snID.getNameID();
        this.lookupTable = lookupTable;
    }

    public int getNumID() {
        return numID;
    }

    public String getNameID() {
        return nameID;
    }

    public LookupTable getLookupTable() {
        return lookupTable;
    }

    public SkipNodeIdentity getIdentity() {
        return new SkipNodeIdentity(nameID, numID, address, port, version);
    }

    @Override
    public void setMiddleLayer(MiddleLayer middleLayer) {
        this.middleLayer = middleLayer;
    }

    /**
     * Inserts this SkipNode to the skip graph of the introducer.
     * @param introducerAddress the address of the introducer.
     * @param introducerPort the port of the introducer.
     */
    @Override
    public void insert(String introducerAddress, int introducerPort) {
        // Do not reinsert an already inserted node.
        if(inserted) return;
        // Trivially insert the first node of the skip graph.
        if(introducerAddress == null) {
            System.out.println(getNumID() + " was inserted!");
            inserted = true;
            return;
        }
        if(!insertionLock.startInsertion()) {
            System.err.println("[SkipNode.insert] Already being inserted!");
            return;
        }
        // Try to acquire the locks from all of my neighbors.
        while(true) {
            SkipNodeIdentity left = null;
            SkipNodeIdentity right = null;
            // First, find my 0-level neighbor by making a num-id search through the introducer.
            SkipNodeIdentity searchResult = middleLayer.searchByNumID(introducerAddress, introducerPort, numID);
            // Get my 0-level left and right neighbors.
            if(getNumID() < searchResult.getNumID()) {
                right = searchResult;
                left = middleLayer.getLeftNode(right.getAddress(), right.getPort(), 0);
            } else {
                left = searchResult;
                right = middleLayer.getRightNode(left.getAddress(), left.getPort(), 0);
            }
            System.out.println(getNumID() + " found its 0-level neighbors: " + left.getNumID() + ", " + right.getNumID());
            if(acquireNeighborLocks(left, right)) break;
            // When we fail, backoff for a random interval before trying again.
            System.out.println(getNumID() + " could not acquire the locks. Backing off...");
            int sleepTime = (int)(Math.random() * 2000);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                System.err.println("[SkipNode.insert] Could not backoff.");
                e.printStackTrace();
            }
        }
        System.out.print(getNumID() + " has acquired all the locks: ");
        ownedLocks.forEach(n -> System.out.print(n.getNumID() + ", "));
        System.out.println();
        // At this point, we should have acquired all of our neighbors. Now, it is time to add them.
        for(SkipNodeIdentity n : ownedLocks) {
            // Insert the neighbor into my own table.
            insertIntoTable(n);
            // Let the neighbor insert me in its table.
            middleLayer.announceNeighbor(n.getAddress(), n.getPort(), getIdentity());
        }
        // Now, we release all of the locks.
        List<SkipNodeIdentity> toRelease = new ArrayList<>();
        ownedLocks.drainTo(toRelease);
        // Release the locks.
        toRelease.forEach(n -> {
            middleLayer.unlock(n.getAddress(), n.getPort(), getIdentity());
        });
        // Complete the insertion.
        inserted = true;
        System.out.println(getNumID() + " was inserted!");
        insertionLock.endInsertion();
    }

    /**
     * ... If not all the locks are acquired, the acquired locks are released.
     * @param left 0th level left neighbor.
     * @param right 0th level right neighbor.
     * @return true iff all the locks were acquired.
     */
    public boolean acquireNeighborLocks(SkipNodeIdentity left, SkipNodeIdentity right) {
        // Try to acquire the locks for the left and right neighbors at all the levels.
        SkipNodeIdentity leftNeighbor = left;
        SkipNodeIdentity rightNeighbor = right;
        // This flag will be set to false when we cannot acquire a lock.
        boolean allAcquired = true;
        // These flags will be used to detect when a neighbor at an upper level is the same as the lower one.
        boolean newLeftNeighbor = true;
        boolean newRightNeighbor = true;
        // Climb up the levels and acquire the left and right neighbor locks.
        for(int level = 0; level <= lookupTable.getNumLevels(); level++) {
            if(newLeftNeighbor && !leftNeighbor.equals(LookupTable.EMPTY_NODE)) {
                // Try to acquire the lock for the left neighbor.
                boolean acquired = middleLayer.tryAcquire(left.getAddress(), left.getPort(), getIdentity(), left.version);
                if(!acquired) {
                    allAcquired = false;
                    break;
                }
                // Add the new lock to our list of locks.
                ownedLocks.add(leftNeighbor);
            }
            if(newRightNeighbor && !rightNeighbor.equals(LookupTable.EMPTY_NODE)) {
                // Try to acquire the lock for the right neighbor.
                boolean acquired = middleLayer.tryAcquire(right.getAddress(), right.getPort(), getIdentity(), right.version);
                if(!acquired) {
                    allAcquired = false;
                    break;
                }
                // Add the new lock to our list of locks.
                ownedLocks.add(rightNeighbor);
            }
            // Acquire the ladders (i.e., the neighbors at the upper level) and check if they are new neighbors
            // or not. If they are not, we won't need to request a lock from them.
            SkipNodeIdentity leftLadder = (leftNeighbor.equals(LookupTable.EMPTY_NODE)) ? LookupTable.EMPTY_NODE
                    : middleLayer.findLadder(leftNeighbor.getAddress(), leftNeighbor.getPort(), level, 0, getNameID());
            newLeftNeighbor = !leftLadder.equals(leftNeighbor);
            SkipNodeIdentity rightLadder = (rightNeighbor.equals(LookupTable.EMPTY_NODE)) ? LookupTable.EMPTY_NODE
                    : middleLayer.findLadder(rightNeighbor.getAddress(), rightNeighbor.getPort(), level, 1, getNameID());
            newRightNeighbor = !rightLadder.equals(rightNeighbor);
            leftNeighbor = leftLadder;
            rightNeighbor = rightLadder;
        }
        // If we were not able to acquire all the locks, then release the locks that were acquired.
        if(!allAcquired) {
            List<SkipNodeIdentity> toRelease = new ArrayList<>();
            ownedLocks.drainTo(toRelease);
            // Release the locks.
            toRelease.forEach(n -> {
                middleLayer.unlock(n.getAddress(), n.getPort(), getIdentity());
            });
        }
        return allAcquired;
    }

    @Override
    public boolean tryAcquire(SkipNodeIdentity requester, int version) {
        // Before trying to acquire the lock, make sure that the versions match.
        if(version != this.version || !insertionLock.tryAcquire(requester)) {
            return false;
        }
        System.out.println(getNumID() + " (" + this.version + ") is being locked by " + requester.getNumID() + " with provided version " + version);
        return true;
    }

    @Override
    public boolean unlock(SkipNodeIdentity owner) {
        return insertionLock.unlockOwned(owner);
    }

    /**
     * Returns whether the node is available to be used as a router. If the node is still being inserted, or is a neighbor
     * of a node that is currently being inserted, this will return false.
     * @return whether the node is available for routing or not.
     */
    @Override
    public boolean isAvailable() {
        return inserted && !insertionLock.isLocked();
    }

    /**
     * Finds the `ladder`, i.e. the node that should be used to propagate a newly joined node to the upper layer. Only
     * used by the insertion protocol, and not by the name ID search protocol even though both of them makes use of ladders.
     * @return the `ladder` node information.
     */
    public SkipNodeIdentity findLadder(int level, int direction, String target) {
        // If the current node and the inserted node have common bits more than the current level,
        // then this node is the neighbor so we return it
        if(SkipNodeIdentity.commonBits(target, getNameID()) > level) {
            return getIdentity();
        }
        // Response from the neighbor.
        SkipNodeIdentity neighborResponse;
        // If the search is to the right...
        if(direction == 1) {
            // And if the right neighbor does not exist then at this level the right neighbor of the inserted node is null.
            if(lookupTable.getRight(level).equals(LookupTable.EMPTY_NODE)) {
                return LookupTable.EMPTY_NODE;
            }
            // Otherwise, delegate the search to right neighbor.
            SkipNodeIdentity rightNeighbor = lookupTable.getRight(level);
            neighborResponse = middleLayer.findLadder(rightNeighbor.getAddress(), rightNeighbor.getPort(), level, 1, target);
        } else {
            // If the search is to the left and if the left neighbor is null, then the left neighbor of the inserted
            // node at this level is null.
            if(lookupTable.getLeft(level).equals(LookupTable.EMPTY_NODE)) {
                return LookupTable.EMPTY_NODE;
            }
            // Otherwise, delegate the search to the left neighbor.
            SkipNodeIdentity leftNeighbor = lookupTable.getLeft(level);
            neighborResponse = middleLayer.findLadder(leftNeighbor.getAddress(), leftNeighbor.getPort(), level, 0, target);
        }
        return neighborResponse;
    }

    /**
     * Given a new neighbor, inserts it to the appropriate levels according to the name ID of the new node.
     * @param newNeighbor the identity of the new neighbor.
     */
    @Override
    public void announceNeighbor(SkipNodeIdentity newNeighbor) {
        insertIntoTable(newNeighbor);
    }

    /**
     * Puts the given node into every appropriate level & direction according to its name ID and numerical ID.
     * @param node the node to insert.
     */
    private void insertIntoTable(SkipNodeIdentity node) {
        System.out.println(getNumID() + " has updated its table.");
        version++;
        int direction = (node.getNumID() < getNumID()) ? 0 : 1;
        int maxLevel = SkipNodeIdentity.commonBits(getNameID(), node.getNameID());
        for(int i = 0; i <= maxLevel; i++) {
            if(direction == 0) updateLeftNode(node, i);
            else updateRightNode(node, i);
        }
    }

    @Override
    public boolean delete() {
        // TODO Implement
        return false;
    }

    /**
     * Search for the given numID
     * @param numID The numID to search for
     * @return The SkipNodeIdentity of the node with the given numID. If it does not exist, returns the SkipNodeIdentity of the SkipNode with NumID closest to the given
     * numID from the direction the search is initiated.
     * For example: Initiating a search for a SkipNode with NumID 50 from a SnipNode with NumID 10 will return the SkipNodeIdentity of the SnipNode with NumID 50 is it exists. If
     * no such SnipNode exists, the SkipNodeIdentity of the SnipNode whose NumID is closest to 50 among the nodes whose NumID is less than 50 is returned.
     */
    @Override
    public SkipNodeIdentity searchByNumID(int numID) {
        // If this is the node the search request is looking for, return its identity
        if (numID == this.numID) {
            return getIdentity();
        }
        // Initialize the level to begin looking at
        int level = lookupTable.getNumLevels();
        // If the target is greater than this node's numID, the search should continue to the right
        if (this.numID < numID) {
            // Start from the top, while there is no right neighbor, or the right neighbor's num ID is greater than what we are searching for
            // keep going down
            while(level>=0) {
                if (lookupTable.getRight(level)==LookupTable.EMPTY_NODE ||
                        lookupTable.getRight(level).getNumID() > numID){
                    level--;
                } else {
                    break;
                }
            }
            // If the level is less than zero, then this node is the closest node to the numID being searched for from the right. Return.
            if (level < 0) {
                return getIdentity();
            }
            // Else, delegate the search to that node on the right
            SkipNodeIdentity delegateNode = lookupTable.getRight(level);
            return middleLayer.searchByNumID(delegateNode.getAddress(), delegateNode.getPort(), numID);
        } else {
            // Start from the top, while there is no right neighbor, or the right neighbor's num ID is greater than what we are searching for
            // keep going down
            while(level>=0) {
                if (lookupTable.getLeft(level)==LookupTable.EMPTY_NODE ||
                        lookupTable.getLeft(level).getNumID() < numID){
                    level--;
                } else {
                    break;
                }
            }
            // If the level is less than zero, then this node is the closest node to the numID being searched for from the right. Return.
            if (level < 0) {
                return getIdentity();
            }
            // Else, delegate the search to that node on the right
            SkipNodeIdentity delegateNode = lookupTable.getLeft(level);
            return middleLayer.searchByNumID(delegateNode.getAddress(), delegateNode.getPort(), numID);
        }
    }

    @Override
    public boolean isLocked() {
        return insertionLock.isLocked();
    }

    @Override
    public boolean isLockedBy(String address, int port) {
        return insertionLock.isLockedBy(address, port);
    }

    /**
     * Performs a name ID lookup over the skip-graph. If the exact name ID is not found, the most similar one is
     * returned.
     * @param targetNameID the target name ID.
     * @return the node with the name ID most similar to the target name ID.
     */
    @Override
    public SearchResult searchByNameID(String targetNameID) {
        if(nameID.equals(targetNameID)) {
            return new SearchResult(getIdentity());
        }
        // If the node is not completely inserted yet, return a tentative identity.
        if(!isAvailable()) {
            return new SearchResult(unavailableIdentity);
        }
        // Find the level in which the search should be started from.
        int level = SkipNodeIdentity.commonBits(nameID, targetNameID);
        if(level < 0) {
            return new SearchResult(getIdentity());
        }
        // Initiate the search.
        return middleLayer.searchByNameIDRecursive(address, port, getIdentity(), getIdentity(), targetNameID, level);
    }

    /**
     * Implements the recursive search by name ID procedure.
     * @param potentialLeftLadder the left node that we potentially can utilize to climb up.
     * @param potentialRightLadder the right node that we potentially can utilize to climb up.
     * @param targetNameID the target name ID.
     * @param level the current level.
     * @return the SkipNodeIdentity of the closest SkipNode which has the common prefix length larger than `level`.
     */
    @Override
    public SearchResult searchByNameIDRecursive(SkipNodeIdentity potentialLeftLadder, SkipNodeIdentity potentialRightLadder,
                                                    String targetNameID, int level) {
        if(nameID.equals(targetNameID)) return new SearchResult(getIdentity());
        // Buffer contains the `most similar node` to return in case we cannot climb up anymore. At first, we try to set this to the
        // non null potential ladder.
        SkipNodeIdentity buffer = (!potentialLeftLadder.equals(LookupTable.EMPTY_NODE)) ? potentialLeftLadder : potentialRightLadder;
        // This loop will execute and we expand our search window until a ladder is found either on the right or the left.
        while(SkipNodeIdentity.commonBits(targetNameID, potentialLeftLadder.getNameID()) <= level
                && SkipNodeIdentity.commonBits(targetNameID, potentialRightLadder.getNameID()) <= level) {
            // Return the potential ladder as the result if it is the result we are looking for.
            if(potentialLeftLadder.getNameID().equals(targetNameID)) return new SearchResult(potentialLeftLadder);
            if(potentialRightLadder.getNameID().equals(targetNameID)) return new SearchResult(potentialRightLadder);
            // Expand the search window on the level.
            if(!potentialLeftLadder.equals(LookupTable.EMPTY_NODE)) {
                buffer = potentialLeftLadder;
                potentialLeftLadder = middleLayer.findLadder(potentialLeftLadder.getAddress(), potentialLeftLadder.getPort(),
                        level, 0, targetNameID);
            }
            if(!potentialRightLadder.equals(LookupTable.EMPTY_NODE)) {
                buffer = potentialRightLadder;
                potentialRightLadder = middleLayer.findLadder(potentialRightLadder.getAddress(), potentialRightLadder.getPort(),
                        level, 1, targetNameID);
            }
            // Try to climb up on the either ladder.
            if(SkipNodeIdentity.commonBits(targetNameID, potentialRightLadder.getNameID()) > level) {
                level = SkipNodeIdentity.commonBits(targetNameID, potentialRightLadder.getNameID());
                SkipNodeIdentity newLeft = middleLayer.getLeftNode(potentialRightLadder.getAddress(), potentialRightLadder.getPort(), level);
                SkipNodeIdentity newRight = middleLayer.getRightNode(potentialRightLadder.getAddress(), potentialRightLadder.getPort(), level);
                return middleLayer.searchByNameIDRecursive(potentialRightLadder.getAddress(), potentialRightLadder.getPort(),
                        newLeft, newRight, targetNameID, level);
            } else if(SkipNodeIdentity.commonBits(targetNameID, potentialLeftLadder.getNameID()) > level) {
                level = SkipNodeIdentity.commonBits(targetNameID, potentialLeftLadder.getNameID());
                SkipNodeIdentity newLeft = middleLayer.getLeftNode(potentialLeftLadder.getAddress(), potentialLeftLadder.getPort(), level);
                SkipNodeIdentity newRight = middleLayer.getRightNode(potentialLeftLadder.getAddress(), potentialLeftLadder.getPort(), level);
                return middleLayer.searchByNameIDRecursive(potentialLeftLadder.getAddress(), potentialLeftLadder.getPort(),
                        newLeft, newRight, targetNameID, level);
            }
            // If we have expanded more than the length of the level, then return the most similar node (buffer).
            if(potentialLeftLadder.equals(LookupTable.EMPTY_NODE) && potentialRightLadder.equals(LookupTable.EMPTY_NODE)) {
                return new SearchResult(buffer);
            }
        }
        return new SearchResult(buffer);
    }

    @Override
    public SkipNodeIdentity updateLeftNode(SkipNodeIdentity snId, int level) {
        return lookupTable.updateLeft(snId, level);
    }

    @Override
    public SkipNodeIdentity updateRightNode(SkipNodeIdentity snId, int level) {
        return lookupTable.updateRight(snId, level);
    }


    @Override
    public SkipNodeIdentity getRightNode(int level) {
        SkipNodeIdentity right = lookupTable.getRight(level);
        return (right.equals(LookupTable.EMPTY_NODE)) ? right
                : middleLayer.getIdentity(right.getAddress(), right.getPort());
    }

    @Override
    public SkipNodeIdentity getLeftNode(int level) {
        SkipNodeIdentity left = lookupTable.getLeft(level);
        return (left.equals(LookupTable.EMPTY_NODE)) ? left
                : middleLayer.getIdentity(left.getAddress(), left.getPort());
    }

    /*
    Test
     */
    AtomicInteger i = new AtomicInteger(0);
    @Override
    public SkipNodeIdentity increment(SkipNodeIdentity snId, int level) {
//        System.out.println(snId+" "+level+" "+i);
        if (level==0){
            return middleLayer.increment(snId.getAddress(), snId.getPort(), snId, 1);
        }else {
//            System.out.println("incrementing");
            i.addAndGet(1);//i += 1;
//            System.out.println(i);
            return new SkipNodeIdentity(""+i, i.get(), ""+i,i.get());
        }
    }

    @Override
    public boolean inject(List<SkipNodeIdentity> injections){
//        nodeStashLock.lock();
        // nodeStash.addAll(injections);
        return true;
//        for(SkipNodeIdentity injection : injections){
//
//        }
//        nodeStashLock.unlock();
    }

    private void pushOutNodes(List<SkipNodeIdentity> lst){
        for (SkipNodeIdentity nd : lst){
            middleLayer.inject(nd.getAddress(), nd.getPort(), lst);
        }
    }
}

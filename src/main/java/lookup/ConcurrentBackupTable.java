package lookup;
import skipnode.SkipNodeIdentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ConcurrentLookupTable is a backup table that supports concurrent calls
 */
public class ConcurrentBackupTable implements BackupTable{

    private final int numLevels;
    private final int maxSize;
    private ReadWriteLock lock;
    /**
     * All the neighbors are placed in an arraylist, with EMPTY_NODE for empty nodes.
     * The formula to get the index of a neighbor is 2*level for a node on the left side
     * and 2*level+1 for a node on the right side. This is reflected in the getIndex
     * method.
     */
    private ArrayList<List<SkipNodeIdentity>> nodes;
    private final List<SkipNodeIdentity> emptyLevel = new LinkedList<>();

    private enum direction{
        LEFT,
        RIGHT
    }

    public ConcurrentBackupTable(int numLevels, int maxSize){
        this.numLevels=numLevels;
        this.maxSize=maxSize;
        lock = new ReentrantReadWriteLock(true);
        nodes = new ArrayList<>(2*numLevels);
        for(int i=0;i<2*numLevels;i++){
            LinkedList<SkipNodeIdentity> ll = new LinkedList<>();
            nodes.add(i, ll);
        }
    }

    public ConcurrentBackupTable(int numLevels){
        this(numLevels, 100);
    }

    @Override
    public SkipNodeIdentity updateLeft(SkipNodeIdentity node, int level) {
        addLeftNode(node, level);
        return getLeft(level);
    }

    @Override
    public SkipNodeIdentity updateRight(SkipNodeIdentity node, int level) {
        addRightNode(node, level);
        return getRight(level);
    }

    @Override
    public SkipNodeIdentity getRight(int level) {
        lock.readLock().lock();
        int idx = getIndex(direction.RIGHT, level);
        SkipNodeIdentity node;
        if (idx < nodes.size()) {
            if (nodes.get(idx).size()==0) {
                node = LookupTable.EMPTY_NODE;
            }else{
                node = nodes.get(idx).get(0);
            }
        }else {
            node = LookupTable.EMPTY_NODE;
        }
        lock.readLock().unlock();
        return node;
    }

    @Override
    public SkipNodeIdentity getLeft(int level) {
        lock.readLock().lock();
        int idx = getIndex(direction.LEFT, level);
        SkipNodeIdentity node;
        if (idx < nodes.size()) {
            if (nodes.get(idx).size()==0) {
                node = LookupTable.EMPTY_NODE;
            }else{
                node = nodes.get(idx).get(0);
            }
        }else {
            node = LookupTable.EMPTY_NODE;
        }
        lock.readLock().unlock();
        return node;
    }

    @Override
    public List<SkipNodeIdentity> getRightNeighbors(int level) {
        int idx = getIndex(direction.RIGHT, level);
        if(idx >= nodes.size()){
            return emptyLevel;
        }
        return nodes.get(idx);
    }

    @Override
    public List<SkipNodeIdentity> getLeftNeighbors(int level) {
        int idx = getIndex(direction.LEFT, level);
        if(idx >= nodes.size()){
            return emptyLevel;
        }
        return nodes.get(idx);
    }

    @Override
    public List<SkipNodeIdentity> addRightNode(SkipNodeIdentity node, int level) {
        lock.writeLock().lock();
        int idx = getIndex(direction.RIGHT, level);
        List<SkipNodeIdentity> entry = nodes.get(idx);
        entry.add(node);
        Collections.sort(entry);
        if(entry.size()>this.maxSize){
            entry.remove(entry.size()-1);
        }
        lock.writeLock().unlock();
        return entry;
    }

    public List<SkipNodeIdentity> addLeftNode(SkipNodeIdentity node, int level) {
        lock.writeLock().lock();
        int idx = getIndex(direction.LEFT, level);
        List<SkipNodeIdentity> entry = nodes.get(idx);
        entry.add(node);
        Collections.sort(entry);
        Collections.reverse(entry);
        if(entry.size()>this.maxSize){
            entry.remove(entry.size()-1);
        }
        lock.writeLock().unlock();
        return entry;
    }

    @Override
    public List<SkipNodeIdentity> removeLeft(SkipNodeIdentity sn, int level) {
        lock.writeLock().lock();
        List<SkipNodeIdentity> leftNodes = getLeftNeighbors(level);
        leftNodes.removeIf(nd -> nd.equals(sn));
        lock.writeLock().unlock();
        return leftNodes;
    }

    @Override
    public SkipNodeIdentity removeLeft(int level) {
        SkipNodeIdentity lft = getLeft(level);
        removeLeft(lft, level);
        return getLeft(level);
    }

    @Override
    public List<SkipNodeIdentity> removeRight(SkipNodeIdentity sn, int level) {
        lock.writeLock().lock();
        List<SkipNodeIdentity> rightNodes = getRightNeighbors(level);
        rightNodes.removeIf(nd -> nd.equals(sn));
        lock.writeLock().unlock();
        return rightNodes;
    }

    @Override
    public SkipNodeIdentity removeRight(int level) {
        SkipNodeIdentity right = getRight(level);
        removeRight(right, level);
        return getRight(level);
    }

    @Override
    public int getNumLevels() {
        return this.numLevels;
    }

    private int getIndex(direction dir, int level){
        if(level<0) return Integer.MAX_VALUE;
        if(dir==direction.LEFT){
            return level*2;
        }else{
            return level*2+1;
        }
    }
}


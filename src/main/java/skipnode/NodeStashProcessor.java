package skipnode;

import lookup.ConcurrentBackupTable;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class NodeStashProcessor implements Runnable {

    private final List<SkipNodeIdentity> nodeStashRef;
    private final ReentrantLock nodeStashLockRef;
    private final ConcurrentBackupTable backupTableRef;
    private final SkipNodeIdentity ownIdentity;

    public boolean running = true;

    public NodeStashProcessor(List<SkipNodeIdentity> nodeStash, ReentrantLock nodeStashLockRef,
                              ConcurrentBackupTable backupTableRef, SkipNodeIdentity ownIdentity) {
        this.nodeStashRef = nodeStash;
        this.nodeStashLockRef = nodeStashLockRef;
        this.backupTableRef = backupTableRef;
        this.ownIdentity = ownIdentity;
    }

    @Override
    public void run() {
        while(running) {
            nodeStashLockRef.lock();
            // Go through every node in the stash and put them in their rightful spot.
            for (SkipNodeIdentity n : nodeStashRef) {
                int level = SkipNodeIdentity.commonBits(n.getNameID(), ownIdentity.getNameID());
                if (n.getNumID() < ownIdentity.getNumID() && !backupTableRef.getLefts(level).contains(n)) {
                    for (int j = level; j >= 0; j--) {
                        backupTableRef.addLeftNode(n, j);
                    }
                } else if (n.getNumID() >= ownIdentity.getNumID() && !backupTableRef.getRights(level).contains(n)) {
                    for (int j = level; j >= 0; j--) {
                        backupTableRef.addRightNode(n, j);
                    }
                }
            }
            // Clear the stash.
            nodeStashRef.clear();
            nodeStashLockRef.unlock();
            // Sleep for a while before checking the stash again.
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.err.println("NodeStashProcessor could not wait!");
                e.printStackTrace();
            }
        }
    }
}

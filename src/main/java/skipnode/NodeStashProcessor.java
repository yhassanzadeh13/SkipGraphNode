package skipnode;

import lookup.ConcurrentBackupTable;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 */
public class NodeStashProcessor implements Runnable {

    private final LinkedBlockingDeque<SkipNodeIdentity> nodeStashRef;
    private final ConcurrentBackupTable backupTableRef;
    private final SkipNodeIdentity ownIdentity;

    public boolean running = true;

    public NodeStashProcessor(LinkedBlockingDeque<SkipNodeIdentity> nodeStash, ConcurrentBackupTable backupTableRef,
                              SkipNodeIdentity ownIdentity) {
        this.nodeStashRef = nodeStash;
        this.backupTableRef = backupTableRef;
        this.ownIdentity = ownIdentity;
    }

    @Override
    public void run() {
        while(running) {
            List<SkipNodeIdentity> stash = new LinkedList<>();
            nodeStashRef.drainTo(stash);
            // Go through every node in the stash and put them in their rightful spot.
            for (SkipNodeIdentity n : stash) {
                int level = SkipNodeIdentity.commonBits(n.getNameID(), ownIdentity.getNameID());
                if (n.getNumID() < ownIdentity.getNumID() && !backupTableRef.getLefts(level).contains(n)) {
                    System.out.println("FILLING OUT");
                    for (int j = level; j >= 0; j--) {
                        backupTableRef.addLeftNode(n, j);
                    }
                } else if (n.getNumID() >= ownIdentity.getNumID() && !backupTableRef.getRights(level).contains(n)) {
                    System.out.println("FILLING OUT");
                    for (int j = level; j >= 0; j--) {
                        backupTableRef.addRightNode(n, j);
                    }
                }
            }
        }
    }
}

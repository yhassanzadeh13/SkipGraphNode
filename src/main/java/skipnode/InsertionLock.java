package skipnode;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class InsertionLock {

    private final Semaphore locked = new Semaphore(1, true);
    private SkipNodeIdentity owner = null;

    public boolean startInsertion() {
        boolean acquired = locked.tryAcquire();
        if(acquired) owner = null;
        return acquired;
    }

    public void endInsertion() {
        if(owner == null) locked.release();
    }

    public boolean tryAcquire(SkipNodeIdentity receiver) {
        boolean acquired = locked.tryAcquire();
        if(acquired) owner = receiver;
        return acquired;
    }

    public boolean isLocked() {
        return locked.availablePermits() == 0;
    }

    public boolean isLockedBy(String address, int port) {
        return isLocked() && owner != null && owner.getAddress().equals(address) && owner.getPort() == port;
    }

    public boolean unlockOwned(SkipNodeIdentity owner) {
        if(!this.owner.equals(owner)) return false;
        locked.release();
        return true;
    }
}

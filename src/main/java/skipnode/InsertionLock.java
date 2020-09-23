package skipnode;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class InsertionLock {

    private final int TIMEOUT_SECONDS = 5;

    private final Semaphore locked = new Semaphore(1, true);
    private SkipNodeIdentity owner = null;

    public boolean startInsertion() {
        boolean acquired = locked.tryAcquire();
        if(acquired) owner = null;
        return acquired;
    }

    public boolean endInsertion() {
        if(owner == null) locked.release();
        return owner == null;
    }

    public boolean timerLocked(SkipNodeIdentity receiver) {
        try {
            boolean acquired = locked.tryAcquire(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            if(acquired) owner = receiver;
            return acquired;
        } catch (InterruptedException e) {
            System.err.println("[InsertionLock.timerLocked] Interrupted.");
            e.printStackTrace();
        }
        return false;
    }

    public boolean isLocked() {
        return locked.availablePermits() == 0;
    }

    public boolean isLockedBy(String address, int port) {
        return isLocked() && owner.getAddress().equals(address) && owner.getPort() == port;
    }

    public boolean unlockOwned(SkipNodeIdentity owner) {
        if(this.owner != owner) return false;
        locked.release();
        return true;
    }
}

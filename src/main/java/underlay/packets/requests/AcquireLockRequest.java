package underlay.packets.requests;

import skipnode.SkipNodeIdentity;
import underlay.packets.Request;
import underlay.packets.RequestType;

public class AcquireLockRequest extends Request {

    public final SkipNodeIdentity requester;

    public AcquireLockRequest(SkipNodeIdentity requester) {
        super(RequestType.AcquireLock);
        this.requester = requester;
    }
}

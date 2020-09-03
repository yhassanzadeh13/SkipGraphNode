package underlay.packets.requests;

import skipnode.SkipNodeIdentity;
import underlay.packets.Request;
import underlay.packets.RequestType;

import java.util.List;

public class SearchByNameIDRecursiveRequest extends Request {

    public final SkipNodeIdentity left;
    public final SkipNodeIdentity right;
    public final String target;
    public final int level;
    public final List<SkipNodeIdentity> path;

    public SearchByNameIDRecursiveRequest(SkipNodeIdentity left, SkipNodeIdentity right, String target, int level,
                                          List<SkipNodeIdentity> path) {
        super(RequestType.SearchByNameIDRecursive);
        this.left = left;
        this.right = right;
        this.target = target;
        this.level = level;
        this.path = path;
    }
}

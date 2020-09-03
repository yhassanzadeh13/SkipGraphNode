package skipnode;

import java.io.Serializable;
import java.util.List;

/**
 * Represents the result of a search containing the piggybacked information.
 */
public class SearchResult implements Serializable {
    public final SkipNodeIdentity result;
    public final List<SkipNodeIdentity> path;

    public SearchResult(SkipNodeIdentity result, List<SkipNodeIdentity> path) {
        this.result = result;
        this.path = path;
    }
}

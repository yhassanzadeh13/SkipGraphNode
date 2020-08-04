package lookup;

import skipnode.SkipNodeIdentity;

import java.util.List;

public interface BackupTable extends LookupTable{
    List<SkipNodeIdentity> getRightNeighbors(int level);

    List<SkipNodeIdentity> getLeftNeighbors(int level);

    List<SkipNodeIdentity> addRightNode(SkipNodeIdentity node, int level);

    List<SkipNodeIdentity> addLeftNode(SkipNodeIdentity node, int level);

    List<SkipNodeIdentity> removeLeft(SkipNodeIdentity sn, int level);

    List<SkipNodeIdentity> removeRight(SkipNodeIdentity sn, int level);
}

package lookup;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import skipnode.SkipNodeIdentity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ConcurrentBackupTableTest {

    protected static ConcurrentBackupTable backupTable;
    protected static List<SkipNodeIdentity> nodesToInsert;

    // Initializes the backup table.
    @BeforeEach
    static void setUp(){
        backupTable = new ConcurrentBackupTable(30);
        nodesToInsert = new ArrayList<>();

        for (int i = 1; i < 10; i++){
            SkipNodeIdentity sn = new SkipNodeIdentity("0000", i, "None", -1);
            nodesToInsert.add(sn);
        }
    }

    @Test
    static void addRightNeighborsInReverseOrderTest(){
        for(int i=nodesToInsert.size()-1;i>=0;i--){
            backupTable.addRightNode(nodesToInsert.get(i), 0);
        }
        Assertions.assertIterableEquals(nodesToInsert, backupTable.getRightNeighbors(0));
        Assertions.assertEquals(nodesToInsert.size(), nodesToInsert.size());
    }

    @Test
    static void addRightNeighborsInOrderTest(){
        for(int i=0 ; i<nodesToInsert.size();i++){
            backupTable.addRightNode(nodesToInsert.get(i), 0);
        }
        Assertions.assertIterableEquals(nodesToInsert, backupTable.getRightNeighbors(0));
    }


    @Test
    static void addLeftNeighborsInReverseOrderTest(){
        Collections.reverse(nodesToInsert);
        for(int i=0 ; i<nodesToInsert.size();i++){
            backupTable.addLeftNode(nodesToInsert.get(i), 0);
        }
        Assertions.assertIterableEquals(nodesToInsert, backupTable.getLeftNeighbors(0));
    }

    @Test
    static void addLeftNeighborsInOrderTest(){
        for(int i=0 ; i<nodesToInsert.size();i++){
            backupTable.addLeftNode(nodesToInsert.get(i), 0);
        }
        Collections.reverse(nodesToInsert);
        Assertions.assertIterableEquals(nodesToInsert, backupTable.getLeftNeighbors(0));
    }

}

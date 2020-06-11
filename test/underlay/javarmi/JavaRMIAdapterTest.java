package underlay.javarmi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import underlay.ConnectionAdapter;

import java.net.Inet4Address;

/**
 * This test creates two RMI clients on the machine at different ports and checks the
 * connectivity between them.
 */
class JavaRMIAdapterTest {

    private static final int LOCAL_PORT = 9090;
    private static final int REMOTE_PORT = 9091;

    private static JavaRMIAdapter localAdapter;
    private static JavaRMIAdapter remoteAdapter;

    private static String localIP;

    // Create the adapters & construct them.
    @BeforeAll
    static void setUp() {
        try {
            localAdapter = new JavaRMIAdapter();
            remoteAdapter = new JavaRMIAdapter();
            localIP = Inet4Address.getLocalHost().getHostAddress();
            // Make sure that the construction is successful.
            Assertions.assertTrue(localAdapter.initialize(LOCAL_PORT));
            Assertions.assertTrue(remoteAdapter.initialize(REMOTE_PORT));
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    // Check if connecting to a remote adapter works.
    @Test
    void remote() {
        ConnectionAdapter remote = localAdapter.remote(localIP + ":" + REMOTE_PORT);
        Assertions.assertNotNull(remote);
        Assertions.assertEquals(remoteAdapter.getAddress(), remote.getAddress());
    }

    // Checks if the addresses are correctly reported.
    @Test
    void getAddress() {
        // Assert that the method return the correct value on a local context.
        Assertions.assertEquals(localIP + ":" + LOCAL_PORT, localAdapter.getAddress());
        Assertions.assertEquals(localIP + ":" + REMOTE_PORT, remoteAdapter.getAddress());
        // Assert that the method returns the correct value when requested from a remote server.
        ConnectionAdapter remote = localAdapter.remote(localIP + ":" + REMOTE_PORT);
        Assertions.assertNotNull(remote);
        Assertions.assertEquals(localIP + ":" + REMOTE_PORT, remote.getAddress());
    }

    // Check if search by name ID returns a response.
    @Test
    void searchByNameID() {
        ConnectionAdapter remote = localAdapter.remote(localIP + ":" + REMOTE_PORT);
        Assertions.assertNotNull(remote);
        Assertions.assertNotNull(remote.searchByNameID(""));
    }

    // Check if search by numerical ID returns a response.
    @Test
    void searchByNumID() {
        ConnectionAdapter remote = localAdapter.remote(localIP + ":" + REMOTE_PORT);
        Assertions.assertNotNull(remote);
        Assertions.assertNotNull(remote.searchByNumID(0));
    }

    // Check if search by name ID (level-based) returns a response.
    @Test
    void nameIDLevelSearch() {
        ConnectionAdapter remote = localAdapter.remote(localIP + ":" + REMOTE_PORT);
        Assertions.assertNotNull(remote);
        Assertions.assertNotNull(remote.nameIDLevelSearch(0, ""));
    }

    // Check if requesting to update a lookup table value returns a response.
    @Test
    void updateLeftNode() {
        ConnectionAdapter remote = localAdapter.remote(localIP + ":" + REMOTE_PORT);
        Assertions.assertNotNull(remote);
        Assertions.assertNotNull(remote.updateLeftNode(0, ""));
    }

    // Check if requesting to update a lookup table value returns a response.
    @Test
    void updateRightNode() {
        ConnectionAdapter remote = localAdapter.remote(localIP + ":" + REMOTE_PORT);
        Assertions.assertNotNull(remote);
        Assertions.assertNotNull(remote.updateRightNode(0, ""));
    }
}
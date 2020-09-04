package skipnode;

import lookup.LookupTable;
import middlelayer.MiddleLayer;
import misc.LocalSkipGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import underlay.Underlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains the skip-node tests.
 */
class SkipNodeTest {

    static int STARTING_PORT = 8080;
    static int NODES = 8;

    static int SEARCH_THRESHOLD = 5000;
    static int SEARCH_THREADS = 500;

    @Test
    void concurrentInsertionsAndSearches() {
        // First, construct the underlays.
        List<Underlay> underlays = new ArrayList<>(NODES);
        for(int i = 0; i < NODES; i++) {
            Underlay underlay = Underlay.newDefaultUnderlay();
            underlay.initialize(STARTING_PORT + i);
            underlays.add(underlay);
        }
        // Then, construct the local skip graph without manually constructing the lookup tables.
        LocalSkipGraph g = new LocalSkipGraph(NODES, underlays.get(0).getAddress(), STARTING_PORT, false);
        // Create the middle layers.
        for(int i = 0; i < NODES; i++) {
            MiddleLayer middleLayer = new MiddleLayer(underlays.get(i), g.getNodes().get(i));
            // Assign the middle layer to the underlay & overlay.
            underlays.get(i).setMiddleLayer(middleLayer);
            g.getNodes().get(i).setMiddleLayer(middleLayer);
        }
        // Insert the first node.
        g.getNodes().get(0).insert(null, -1);
        // Construct the threads.
        Thread[] insertionThreads = new Thread[NODES-1];
        for(int i = 1; i <= insertionThreads.length; i++) {
            final SkipNode introducer = g.getNodes().get((int) (Math.random() * (i-1)));
            final SkipNode node = g.getNodes().get(i);
            insertionThreads[i-1] = new Thread(() -> {
                node.insert(introducer.getIdentity().getAddress(), introducer.getIdentity().getPort());
            });
        }
        // Start the insertion threads.
        for(Thread t : insertionThreads) t.start();
        // Wait for them to complete.
        for(Thread t : insertionThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // We expect the lookup tables to converge to a correct state after SEARCH_THRESHOLD many searches.
        for(int k = 0; k < SEARCH_THRESHOLD; k++) {
            final SkipNode initiator = g.getNodes().get((int)(Math.random() * NODES));
            final SkipNode target = g.getNodes().get((int)(Math.random() * NODES));
            initiator.searchByNameID(target.getNameID());
        }
        // Construct the search threads.
        Thread[] searchThreads = new Thread[SEARCH_THREADS];
        for(int i = 0; i < searchThreads.length; i++) {
            // Choose two random nodes.
            final SkipNode initiator = g.getNodes().get((int)(Math.random() * NODES));
            final SkipNode target = g.getNodes().get((int)(Math.random() * NODES));
            searchThreads[i] = new Thread(() -> {
                SearchResult res = initiator.searchByNameID(target.getNameID());
                Assertions.assertEquals(target.getNameID(), res.result.getNameID());
            });
        }
        // Start the search threads.
        for(Thread t : searchThreads) t.start();
        // Complete the threads.
        try {
            for(Thread t : searchThreads) t.join();
        } catch(InterruptedException e) {
            System.err.println("Could not join the thread.");
            e.printStackTrace();
        }
        // Perform searches and check their correctness.
        for(int i = 0; i < searchThreads.length; i++) {
            // Choose two random nodes.
            final SkipNode initiator = g.getNodes().get((int)(Math.random() * NODES));
            final SkipNode target = g.getNodes().get((int)(Math.random() * NODES));
            SearchResult res = initiator.searchByNameID(target.getNameID());
            Assertions.assertEquals(target.getNameID(), res.result.getNameID());
        }
        // Create a map of num ids to their corresponding lookup tables.
        Map<Integer, LookupTable> tableMap = g.getNodes().stream()
                .collect(Collectors.toMap(SkipNode::getNumID, SkipNode::getLookupTable));
        // Check the correctness & consistency of the tables.
        for(SkipNode n : g.getNodes()) {
            tableCorrectnessCheck(n.getNumID(), n.getNameID(), n.getLookupTable());
            tableConsistencyCheck(tableMap, n);
        }
    }

    @Test
    void concurrentInsertions() {
        // First, construct the underlays.
        List<Underlay> underlays = new ArrayList<>(NODES);
        for(int i = 0; i < NODES; i++) {
            Underlay underlay = Underlay.newDefaultUnderlay();
            underlay.initialize(STARTING_PORT + i);
            underlays.add(underlay);
        }
        // Then, construct the local skip graph without manually constructing the lookup tables.
        LocalSkipGraph g = new LocalSkipGraph(NODES, underlays.get(0).getAddress(), STARTING_PORT, false);
        // Create the middle layers.
        for(int i = 0; i < NODES; i++) {
            MiddleLayer middleLayer = new MiddleLayer(underlays.get(i), g.getNodes().get(i));
            // Assign the middle layer to the underlay & overlay.
            underlays.get(i).setMiddleLayer(middleLayer);
            g.getNodes().get(i).setMiddleLayer(middleLayer);
        }
        // Insert the first node.
        g.getNodes().get(0).insert(null, -1);
        Thread[] threads = new Thread[NODES-1];
        // Construct the threads.
        for(int i = 1; i <= threads.length; i++) {
            final SkipNode introducer = g.getNodes().get(i-1);
            final SkipNode node = g.getNodes().get(i);
            threads[i-1] = new Thread(() -> {
                node.insert(introducer.getIdentity().getAddress(), introducer.getIdentity().getPort());
            });
        }
        // Initiate the insertions.
        for(Thread t : threads) t.start();
        // Wait for the insertions to complete.
        for(Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.err.println("Could not join the thread.");
                e.printStackTrace();
            }
        }
        // Create a map of num ids to their corresponding lookup tables.
        Map<Integer, LookupTable> tableMap = g.getNodes().stream()
                .collect(Collectors.toMap(SkipNode::getNumID, SkipNode::getLookupTable));
        // Check the correctness & consistency of the tables.
        for(SkipNode n : g.getNodes()) {
            tableCorrectnessCheck(n.getNumID(), n.getNameID(), n.getLookupTable());
            tableConsistencyCheck(tableMap, n);
        }
    }

    @Test
    void insert() {
        // First, construct the underlays.
        List<Underlay> underlays = new ArrayList<>(NODES);
        for(int i = 0; i < NODES; i++) {
            Underlay underlay = Underlay.newDefaultUnderlay();
            underlay.initialize(STARTING_PORT + i);
            underlays.add(underlay);
        }
        // Then, construct the local skip graph without manually constructing the lookup tables.
        LocalSkipGraph g = new LocalSkipGraph(NODES, underlays.get(0).getAddress(), STARTING_PORT, false);
        // Create the middle layers.
        for(int i = 0; i < NODES; i++) {
            MiddleLayer middleLayer = new MiddleLayer(underlays.get(i), g.getNodes().get(i));
            // Assign the middle layer to the underlay & overlay.
            underlays.get(i).setMiddleLayer(middleLayer);
            g.getNodes().get(i).setMiddleLayer(middleLayer);
        }
        // Now, insert every node in a randomized order.
        g.insertAllRandomized();
        // Create a map of num ids to their corresponding lookup tables.
        Map<Integer, LookupTable> tableMap = g.getNodes().stream()
                .collect(Collectors.toMap(SkipNode::getNumID, SkipNode::getLookupTable));
        // Check the correctness of the tables.
        for(SkipNode n : g.getNodes()) {
            tableCorrectnessCheck(n.getNumID(), n.getNameID(), n.getLookupTable());
            tableConsistencyCheck(tableMap, n);
        }
        underlays.forEach(Underlay::terminate);
    }

    @Test
    void searchByNameID() {
        // First, construct the underlays.
        List<Underlay> underlays = new ArrayList<>(NODES);
        for(int i = 0; i < NODES; i++) {
            Underlay underlay = Underlay.newDefaultUnderlay();
            underlay.initialize(STARTING_PORT + i);
            underlays.add(underlay);
        }
        // Then, construct the local skip graph.
        LocalSkipGraph g = new LocalSkipGraph(NODES, underlays.get(0).getAddress(), STARTING_PORT, false);
        // Create the middle layers.
        for(int i = 0; i < NODES; i++) {
            MiddleLayer middleLayer = new MiddleLayer(underlays.get(i), g.getNodes().get(i));
            // Assign the middle layer to the underlay & overlay.
            underlays.get(i).setMiddleLayer(middleLayer);
            g.getNodes().get(i).setMiddleLayer(middleLayer);
        }
        // Insert all the nodes in a randomized fashion.
        g.insertAllRandomized();
        // We will now perform name ID searches for every node from each node in the skip graph.
        for(int i = 0; i < NODES; i++) {
            SkipNode initiator = g.getNodes().get(i);
            for(int j = 0; j < NODES; j++) {
                SkipNode target = g.getNodes().get(j);
                SearchResult result = initiator.searchByNameID(target.getNameID());
                Assertions.assertEquals(target.getIdentity(), result.result);
            }
        }
        underlays.forEach(Underlay::terminate);
    }

    @Test
    void searchByNumID() {
        // First, construct the underlays.
        List<Underlay> underlays = new ArrayList<>(NODES);
        for(int i = 0; i < NODES; i++) {
            Underlay underlay = Underlay.newDefaultUnderlay();
            underlay.initialize(STARTING_PORT - NODES + i);
            underlays.add(underlay);
        }
        // Then, construct the local skip graph.
        LocalSkipGraph g = new LocalSkipGraph(NODES, underlays.get(0).getAddress(), STARTING_PORT- NODES, true);
        // Create the middle layers.
        for(int i = 0; i < NODES; i++) {
            MiddleLayer middleLayer = new MiddleLayer(underlays.get(i), g.getNodes().get(i));
            // Assign the middle layer to the underlay & overlay.
            underlays.get(i).setMiddleLayer(middleLayer);
            g.getNodes().get(i).setMiddleLayer(middleLayer);
        }

        // We will now perform name ID searches for every node from each node in the skip graph.
        for(int i = 0; i < NODES; i++) {
            SkipNode initiator = g.getNodes().get(i);
            for(int j = 0; j < NODES; j++) {
                SkipNode target = g.getNodes().get(j);
                SkipNodeIdentity result = initiator.searchByNumID(target.getNumID());
                Assertions.assertEquals(target.getIdentity(), result);
            }
        }
        underlays.forEach(Underlay::terminate);
    }

    // Checks the correctness of a lookup table owned by the node with the given identity parameters.
    static void tableCorrectnessCheck(int numID, String nameID, LookupTable table) {
        for(int i = 0; i < table.getNumLevels(); i++) {
            List<SkipNodeIdentity> lefts = table.getLefts(i);
            List<SkipNodeIdentity> rights = table.getRights(i);
            for(SkipNodeIdentity l : lefts) {
                Assertions.assertTrue(l.getNumID() < numID);
                Assertions.assertTrue(SkipNodeIdentity.commonBits(l.getNameID(), nameID) >= i);
            }
            for(SkipNodeIdentity r : rights) {
                Assertions.assertTrue(r.getNumID() > numID);
                Assertions.assertTrue(SkipNodeIdentity.commonBits(r.getNameID(), nameID) >= i);
            }
        }
    }

    // Checks the consistency of a lookup table. In other words, we assert that if x is a neighbor of y at level l,
    // then y is a neighbor of x at level l (in opposite directions).
    static void tableConsistencyCheck(Map<Integer, LookupTable> tableMap, SkipNode node) {
        LookupTable table = node.getLookupTable();
        for(int i = 0; i < table.getNumLevels(); i++) {
            List<SkipNodeIdentity> lefts = table.getLefts(i);
            List<SkipNodeIdentity> rights = table.getRights(i);
            // Check whether the neighbors agree on the neighborship relationships.
            for(SkipNodeIdentity l : lefts) {
                LookupTable neighborMap = tableMap.get(l.getNumID());
                Assertions.assertTrue(neighborMap.isRightNeighbor(node.getIdentity(), i));
            }
            for(SkipNodeIdentity r : rights) {
                LookupTable neighborMap = tableMap.get(r.getNumID());
                Assertions.assertTrue(neighborMap.isLeftNeighbor(node.getIdentity(), i));
            }
        }
    }
}
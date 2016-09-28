/*
 * File:                PersonalizedPageRankTest.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright 2016, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.graph.community;

import gov.sandia.cognition.graph.DenseMemoryGraph;
import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
import gov.sandia.cognition.graph.WeightedDenseMemoryGraph;
import gov.sandia.cognition.util.DoubleVector;
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the PersonalizedPageRank class
 *
 * @author jdwendt
 */
public class PersonalizedPageRankTest
{

    @Test
    public void basicTest()
    {
        DirectedNodeEdgeGraph<Integer> graph = new DenseMemoryGraph<>(8, 15);
        for (int i = 0; i < 8; ++i)
        {
            graph.addNode(i);
        }
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(0, 3);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(2, 3);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(4, 6);
        graph.addEdge(4, 7);
        graph.addEdge(5, 6);
        graph.addEdge(5, 7);
        graph.addEdge(6, 7);

        PersonalizedPageRank<Integer> ppr = new PersonalizedPageRank<>(graph);
        DoubleVector rank = ppr.getScoresForAllNodesById(0);
        // These scores pulled by running the above test against the original Python code
        assertEquals(rank.get(0), 0.12537838024216372, 1e-10);
        assertEquals(rank.get(1), 0.11052762994515698, 1e-10);
        assertEquals(rank.get(2), 0.11037813886936204, 1e-10);
        assertEquals(rank.get(3), 0.1379002079958543, 1e-10);
        assertEquals(rank.get(4), 0.10425699547298657, 1e-10);
        assertEquals(rank.get(5), 0.07236686571571394, 1e-10);
        assertEquals(rank.get(6), 0.0725170996662634, 1e-10);
        assertEquals(rank.get(7), 0.07259186730843395, 1e-10);

        // This tests that the randomness does change the answer.  While it is
        // possible that as this unit test is run, some run sets up the random
        // orders to all be the specified order.  However, it seems that it
        // runs dozens of passes of that randomized code, so the odds of none
        // of those being out of order is very slight.
        rank = ppr.getScoresForAllNodesById(0, true);
        assertNotEquals(rank.get(0), 0.12537838024216372, 1e-10);
        assertNotEquals(rank.get(1), 0.11052762994515698, 1e-10);
        assertNotEquals(rank.get(2), 0.11037813886936204, 1e-10);
        assertNotEquals(rank.get(3), 0.1379002079958543, 1e-10);
        assertNotEquals(rank.get(4), 0.10425699547298657, 1e-10);
        assertNotEquals(rank.get(5), 0.07236686571571394, 1e-10);
        assertNotEquals(rank.get(6), 0.0725170996662634, 1e-10);
        assertNotEquals(rank.get(7), 0.07259186730843395, 1e-10);
        DoubleVector last = rank;
        rank = ppr.getScoresForAllNodesById(0, true);
        assertNotEquals(rank.get(0), 0.12537838024216372, 1e-10);
        assertNotEquals(rank.get(1), 0.11052762994515698, 1e-10);
        assertNotEquals(rank.get(2), 0.11037813886936204, 1e-10);
        assertNotEquals(rank.get(3), 0.1379002079958543, 1e-10);
        assertNotEquals(rank.get(4), 0.10425699547298657, 1e-10);
        assertNotEquals(rank.get(5), 0.07236686571571394, 1e-10);
        assertNotEquals(rank.get(6), 0.0725170996662634, 1e-10);
        assertNotEquals(rank.get(7), 0.07259186730843395, 1e-10);
        assertNotEquals(rank.get(0), last.get(0), 1e-10);
        assertNotEquals(rank.get(1), last.get(1), 1e-10);
        assertNotEquals(rank.get(2), last.get(2), 1e-10);
        assertNotEquals(rank.get(3), last.get(3), 1e-10);
        assertNotEquals(rank.get(4), last.get(4), 1e-10);
        assertNotEquals(rank.get(5), last.get(5), 1e-10);
        assertNotEquals(rank.get(6), last.get(6), 1e-10);
        assertNotEquals(rank.get(7), last.get(7), 1e-10);

        Set<Integer> comm
            = ppr.getCommunityForNodeById(graph.getNodeId(2), 1, 1);
        assertEquals(4, comm.size());
        assertTrue(comm.contains(0));
        assertTrue(comm.contains(1));
        assertTrue(comm.contains(2));
        assertTrue(comm.contains(3));
        assertEquals(1.0 / 13.0, CommunityMetrics.computeConductance(graph, comm), 1e-10);
        rank = ppr.getScoresForAllNodesById(5);
        assertEquals(rank.get(0), 0.0724287985384937, 1e-10);
        assertEquals(rank.get(1), 0.0722783732977096, 1e-10);
        assertEquals(rank.get(2), 0.07235370956183046, 1e-10);
        assertEquals(rank.get(3), 0.10433419584976945, 1e-10);
        assertEquals(rank.get(4), 0.13795865003125696, 1e-10);
        assertEquals(rank.get(5), 0.12549330004021364, 1e-10);
        assertEquals(rank.get(6), 0.1104182666358747, 1e-10);
        assertEquals(rank.get(7), 0.11049322095390347, 1e-10);
        comm = ppr.getCommunityForNodeById(graph.getNodeId(4), 1, 1);
        assertEquals(4, comm.size());
        assertTrue(comm.contains(4));
        assertTrue(comm.contains(5));
        assertTrue(comm.contains(6));
        assertTrue(comm.contains(7));

        // Add some self loops and make sure none of the results have changed
        graph.addEdge(0, 0);
        graph.addEdge(4, 4);
        graph.addEdge(6, 6);
        ppr = new PersonalizedPageRank<>(graph);
        rank = ppr.getScoresForAllNodesById(0);
        // These scores pulled by running the above test against the original Python code
        assertEquals(rank.get(0), 0.12537838024216372, 1e-10);
        assertEquals(rank.get(1), 0.11052762994515698, 1e-10);
        assertEquals(rank.get(2), 0.11037813886936204, 1e-10);
        assertEquals(rank.get(3), 0.1379002079958543, 1e-10);
        assertEquals(rank.get(4), 0.10425699547298657, 1e-10);
        assertEquals(rank.get(5), 0.07236686571571394, 1e-10);
        assertEquals(rank.get(6), 0.0725170996662634, 1e-10);
        assertEquals(rank.get(7), 0.07259186730843395, 1e-10);
        ppr.setTolerance(0.00001);
        comm = ppr.getCommunityForNodeById(graph.getNodeId(2), 1, 1);
        assertEquals(4, comm.size());
        assertTrue(comm.contains(0));
        assertTrue(comm.contains(1));
        assertTrue(comm.contains(2));
        assertTrue(comm.contains(3));
        ppr.setTolerance(0.01);
        rank = ppr.getScoresForAllNodesById(5);
        assertEquals(rank.get(0), 0.0724287985384937, 1e-10);
        assertEquals(rank.get(1), 0.0722783732977096, 1e-10);
        assertEquals(rank.get(2), 0.07235370956183046, 1e-10);
        assertEquals(rank.get(3), 0.10433419584976945, 1e-10);
        assertEquals(rank.get(4), 0.13795865003125696, 1e-10);
        assertEquals(rank.get(5), 0.12549330004021364, 1e-10);
        assertEquals(rank.get(6), 0.1104182666358747, 1e-10);
        assertEquals(rank.get(7), 0.11049322095390347, 1e-10);
        comm = ppr.getCommunityForNodeById(graph.getNodeId(4), 1, 1);
        assertEquals(4, comm.size());
        assertTrue(comm.contains(4));
        assertTrue(comm.contains(5));
        assertTrue(comm.contains(6));
        assertTrue(comm.contains(7));
    }

    @Test
    public void weightedTest()
    {
        WeightedDenseMemoryGraph<Integer> graph
            = new WeightedDenseMemoryGraph<>(8, 15);
        for (int i = 0; i < 8; ++i)
        {
            graph.addNode(i);
        }
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 3);
        graph.addEdge(0, 3, .1);
        graph.addEdge(1, 2, 3);
        graph.addEdge(1, 3, .1);
        graph.addEdge(2, 3, .1);
        graph.addEdge(4, 3, 14);
        graph.addEdge(7, 6, 3);
        graph.addEdge(7, 5, 3);
        graph.addEdge(7, 4, .1);
        graph.addEdge(6, 5, 3);
        graph.addEdge(6, 4, .1);
        graph.addEdge(5, 4, .1);

        PersonalizedPageRank<Integer> ppr = new PersonalizedPageRank<>(graph);
        DoubleVector from3 = ppr.getScoresForAllNodesByIdMultirun(
            graph.getNodeId(3), 20000);
        DoubleVector from4 = ppr.getScoresForAllNodesByIdMultirun(
            graph.getNodeId(4), 20000);
        assertEquals(from3.get(graph.getNodeId(0)),
            from4.get(graph.getNodeId(7)), 1e-3);
        assertEquals(from3.get(graph.getNodeId(1)),
            from4.get(graph.getNodeId(6)), 1e-3);
        assertEquals(from3.get(graph.getNodeId(2)),
            from4.get(graph.getNodeId(5)), 1e-3);
        assertEquals(from3.get(graph.getNodeId(3)),
            from4.get(graph.getNodeId(4)), 1e-3);
        assertEquals(from3.get(graph.getNodeId(4)),
            from4.get(graph.getNodeId(3)), 1e-3);
        assertEquals(from3.get(graph.getNodeId(5)),
            from4.get(graph.getNodeId(2)), 1e-3);
        assertEquals(from3.get(graph.getNodeId(6)),
            from4.get(graph.getNodeId(1)), 1e-3);
        assertEquals(from3.get(graph.getNodeId(7)),
            from4.get(graph.getNodeId(0)), 1e-3);
        ppr.setTolerance(0.0001);
        Set<Integer> comm = ppr.getCommunityForNodeById(graph.getNodeId(0),
            2000, 20);
        assertEquals(5, comm.size());
        assertTrue(comm.contains(0));
        assertTrue(comm.contains(1));
        assertTrue(comm.contains(2));
        assertTrue(comm.contains(3));
        assertTrue(comm.contains(4));
        assertEquals(0.3 / 18.3, CommunityMetrics.computeConductance(graph, comm), 1e-6);
        ppr.setTolerance(0.01);
        comm = ppr.getCommunityForNodeById(graph.getNodeId(1), 2000, 20);
        assertEquals(5, comm.size());
        assertTrue(comm.contains(0));
        assertTrue(comm.contains(1));
        assertTrue(comm.contains(2));
        assertTrue(comm.contains(3));
        assertTrue(comm.contains(4));
        comm = ppr.getCommunityForNodeById(graph.getNodeId(2), 2000, 20);
        assertEquals(5, comm.size());
        assertTrue(comm.contains(0));
        assertTrue(comm.contains(1));
        assertTrue(comm.contains(2));
        assertTrue(comm.contains(3));
        assertTrue(comm.contains(4));
        comm = ppr.getCommunityForNodeById(graph.getNodeId(5), 2000, 20);
        assertEquals(5, comm.size());
        assertTrue(comm.contains(3));
        assertTrue(comm.contains(4));
        assertTrue(comm.contains(5));
        assertTrue(comm.contains(6));
        assertTrue(comm.contains(7));
        comm = ppr.getCommunityForNodeById(graph.getNodeId(6), 2000, 20);
        assertEquals(5, comm.size());
        assertTrue(comm.contains(3));
        assertTrue(comm.contains(4));
        assertTrue(comm.contains(5));
        assertTrue(comm.contains(6));
        assertTrue(comm.contains(7));
        comm = ppr.getCommunityForNodeById(graph.getNodeId(7), 2000, 20);
        assertEquals(5, comm.size());
        assertTrue(comm.contains(3));
        assertTrue(comm.contains(4));
        assertTrue(comm.contains(5));
        assertTrue(comm.contains(6));
        assertTrue(comm.contains(7));
        comm = ppr.getCommunityForNodeById(graph.getNodeId(3), 2000, 20);
        assertEquals(5, comm.size());
        assertTrue(comm.contains(0));
        assertTrue(comm.contains(1));
        assertTrue(comm.contains(2));
        assertTrue(comm.contains(3));
        assertTrue(comm.contains(4));
        comm = ppr.getCommunityForNodeById(graph.getNodeId(4), 2000, 20);
        assertEquals(5, comm.size());
        assertTrue(comm.contains(3));
        assertTrue(comm.contains(4));
        assertTrue(comm.contains(5));
        assertTrue(comm.contains(6));
        assertTrue(comm.contains(7));
    }

    @Test
    public void matchesOriginalImplementation()
    {
        DirectedNodeEdgeGraph<Integer> graph = new DenseMemoryGraph<>(27, 51);
        for (int i = 1; i < 28; ++i)
        {
            graph.addNode(i);
        }
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(1, 5);
        graph.addEdge(1, 6);
        graph.addEdge(2, 4);
        graph.addEdge(3, 6);
        graph.addEdge(3, 7);
        graph.addEdge(4, 5);
        graph.addEdge(4, 7);
        graph.addEdge(4, 8);
        graph.addEdge(5, 6);
        graph.addEdge(5, 8);
        graph.addEdge(5, 9);
        graph.addEdge(5, 10);
        graph.addEdge(6, 7);
        graph.addEdge(7, 9);
        graph.addEdge(8, 9);
        graph.addEdge(9, 20);
        graph.addEdge(10, 11);
        graph.addEdge(10, 12);
        graph.addEdge(10, 14);
        graph.addEdge(10, 15);
        graph.addEdge(11, 12);
        graph.addEdge(11, 13);
        graph.addEdge(11, 14);
        graph.addEdge(12, 13);
        graph.addEdge(12, 14);
        graph.addEdge(12, 15);
        graph.addEdge(13, 15);
        graph.addEdge(14, 25);
        graph.addEdge(16, 17);
        graph.addEdge(16, 19);
        graph.addEdge(16, 20);
        graph.addEdge(16, 21);
        graph.addEdge(16, 22);
        graph.addEdge(17, 18);
        graph.addEdge(17, 19);
        graph.addEdge(17, 20);
        graph.addEdge(18, 20);
        graph.addEdge(18, 21);
        graph.addEdge(18, 22);
        graph.addEdge(22, 23);
        graph.addEdge(23, 24);
        graph.addEdge(23, 25);
        graph.addEdge(23, 26);
        graph.addEdge(23, 27);
        graph.addEdge(24, 25);
        graph.addEdge(24, 26);
        graph.addEdge(24, 27);
        graph.addEdge(25, 26);
        graph.addEdge(25, 27);

        PersonalizedPageRank<Integer> ppr = new PersonalizedPageRank<>(graph);
        DoubleVector scores = ppr.getScoresForAllNodes(1);
        assertEquals(scores.get(0), 0.05443035867625151, 1e-10);
        assertEquals(scores.get(1), 0.02019683469901085, 1e-10);
        assertEquals(scores.get(2), 0.02908997726706587, 1e-10);
        assertEquals(scores.get(3), 0.029305090683170748, 1e-10);
        assertEquals(scores.get(4), 0.041121042067147784, 1e-10);
        assertEquals(scores.get(5), 0.03640290217735352, 1e-10);
        assertEquals(scores.get(6), 0.030010729753008787, 1e-10);
        assertEquals(scores.get(7), 0.018358330911577337, 1e-10);
        assertEquals(scores.get(8), 0.02051104457347991, 1e-10);
        assertEquals(scores.get(9), 0.009331981483102011, 1e-10);
        assertEquals(scores.get(10), 0.003526785400876157, 1e-10);
        assertEquals(scores.get(11), 0.004461657254560571, 1e-10);
        assertEquals(scores.get(12), 0.0023523597787462693, 1e-10);
        assertEquals(scores.get(13), 0.002806647249785621, 1e-10);
        assertEquals(scores.get(14), 0.003160399771070798, 1e-10);
        assertEquals(scores.get(15), 0.0005028295714806916, 1e-10);
        assertEquals(scores.get(16), 0.0008904385467607474, 1e-10);
        assertEquals(scores.get(17), 0.0009160345550701841, 1e-10);
        assertEquals(scores.get(18), 0, 1e-10);
        assertEquals(scores.get(19), 0.004930866568458458, 1e-10);
        assertEquals(scores.get(20), 0, 1e-10);
        assertEquals(scores.get(21), 0, 1e-10);
        assertEquals(scores.get(22), 0, 1e-10);
        assertEquals(scores.get(23), 0, 1e-10);
        assertEquals(scores.get(24), 0, 1e-10);
        assertEquals(scores.get(25), 0, 1e-10);
        assertEquals(scores.get(26), 0, 1e-10);
    }

}

/*
 * File:                CommunityMetricsTest.java
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
import gov.sandia.cognition.graph.GraphMetrics;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Basic tests for the CommunityMetrics class
 *
 * @author jdwendt
 */
public class CommunityMetricsTest
{

    @Test
    public void testModularity()
    {
        DirectedNodeEdgeGraph<String> twoComms = new DenseMemoryGraph<>();
        twoComms.addEdge("a", "b");
        twoComms.addEdge("a", "c");
        twoComms.addEdge("a", "d");
        twoComms.addEdge("b", "c");
        twoComms.addEdge("b", "d");
        twoComms.addEdge("c", "d");
        twoComms.addEdge("d", "e");
        twoComms.addEdge("e", "f");
        twoComms.addEdge("e", "g");
        twoComms.addEdge("e", "h");
        twoComms.addEdge("f", "g");
        twoComms.addEdge("f", "h");
        twoComms.addEdge("g", "h");

        Louvain<String> l = new Louvain<>(twoComms);
        NodePartitioning<String> np = l.solveCommunities();

        double modularity = np.getModularity();
        Set<Set<String>> communities = new HashSet<>(np.numPartitions());
        for (int i = 0; i < np.numPartitions(); ++i)
        {
            communities.add(np.getPartitionMembers(i));
        }
        assertEquals(modularity,
            CommunityMetrics.computeModularity(twoComms, np), 1e-8);
        assertEquals(modularity, CommunityMetrics.computeModularity(twoComms,
            communities), 1e-8);
    }

    @Test
    public void testPermanence()
    {
        DirectedNodeEdgeGraph<Integer> graph = new DenseMemoryGraph<>();
        graph.addEdge(0, 1);
        graph.addEdge(0, 3);
        graph.addEdge(0, 4);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(1, 4);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(3, 4);
        graph.addEdge(4, 5);
        graph.addEdge(4, 9);
        graph.addEdge(4, 13);
        graph.addEdge(5, 6);
        graph.addEdge(5, 7);
        graph.addEdge(6, 7);
        graph.addEdge(6, 8);
        graph.addEdge(7, 8);
        graph.addEdge(9, 10);
        graph.addEdge(9, 11);
        graph.addEdge(9, 12);
        graph.addEdge(10, 12);
        graph.addEdge(11, 12);
        graph.addEdge(11, 13);
        graph.addEdge(12, 13);

        NodePartitioning<Integer> part = new NodePartitioning<Integer>()
        {
            @Override
            public int numPartitions()
            {
                return 3;
            }

            @Override
            public Set<Integer> getPartitionMembers(int i)
            {
                HashSet<Integer> members = new HashSet<>();
                switch (i)
                {
                    case 0:
                        members.add(0);
                        members.add(1);
                        members.add(2);
                        members.add(3);
                        members.add(4);
                        break;
                    case 1:
                        members.add(5);
                        members.add(6);
                        members.add(7);
                        members.add(8);
                        break;
                    case 2:
                        members.add(9);
                        members.add(10);
                        members.add(11);
                        members.add(12);
                        members.add(13);
                        break;
                    default:
                        throw new IllegalArgumentException(
                            "Unknown partition id: " + i);
                }
                return members;
            }

            @Override
            public Set<Integer> getAllMembers()
            {
                HashSet<Integer> allMembers = new HashSet<>();
                for (int i = 0; i < 14; ++i)
                {
                    allMembers.add(i);
                }

                return allMembers;
            }

            @Override
            public int getPartition(Integer node)
            {
                if (node >= 0 && node <= 4)
                {
                    return 0;
                }
                else if (node >= 5 && node <= 8)
                {
                    return 1;
                }
                else if (node >= 9 && node <= 13)
                {
                    return 2;
                }
                else
                {
                    throw new IllegalArgumentException("Unknown node: " + node);
                }
            }

            @Override
            public Double getModularity()
            {
                return null;
            }

            @Override
            public int getPartitionById(int nodeId)
            {
                // The name and id are the same here
                return getPartition(graph.getNode(nodeId));
            }

        };
        GraphMetrics<Integer> metrics = new GraphMetrics<>(graph);
        assertEquals(4.0 / 2.0 * (1.0 / 7.0) - (1 - 5.0 / 6.0),
            CommunityMetrics.computeOneNodePermanence(metrics, part, 4, graph), 1e-6);
        assertEquals(2.0 / 1.0 * (1.0 / 3.0) - (1 - 1.0 / 1.0),
            CommunityMetrics.computeOneNodePermanence(metrics, part, 5, graph), 1e-6);
        assertEquals(3.0 / 1.0 * (1.0 / 4.0) - (1 - 2.0 / 3.0),
            CommunityMetrics.computeOneNodePermanence(metrics, part, 9, graph), 1e-6);
        assertEquals(4.0 / 1.0 * (1.0 / 4.0) - (1 - 5.0 / 6.0),
            CommunityMetrics.computeOneNodePermanence(metrics, part, 3, graph), 1e-6);
    }

}

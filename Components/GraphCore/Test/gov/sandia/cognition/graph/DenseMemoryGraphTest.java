/*
 * File:                DenseMemoryGraphTest.java
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

package gov.sandia.cognition.graph;

import gov.sandia.cognition.util.Pair;
import java.util.Collection;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author jdwendt
 */
public class DenseMemoryGraphTest
{

    private static void testContains(Collection<Integer> coll,
        int[] vals)
    {
        assertEquals(vals.length, coll.size());
        for (int val : vals)
        {
            assertTrue(coll.contains(val));
        }
    }

    private static void testIn(int i,
        int[] vals)
    {
        boolean found = false;
        for (int val : vals)
        {
            found |= (i == val);
        }
        assertTrue(found);
    }

    @Test
    public void basicTest()
    {
        DenseMemoryGraph<Integer> graph = new DenseMemoryGraph<>(6, 8);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2);
        graph.addEdge(1, 0);
        graph.addEdge(1, 2);
        graph.addEdge(1, 3);
        graph.addEdge(1, 4);
        graph.addEdge(3, 4);
        graph.addEdge(4, 0);
        graph.addNode(5);

        assertEquals(6, graph.numNodes());
        assertEquals(8, graph.numEdges());
        assertTrue(graph.containsNode(0));
        assertTrue(graph.containsNode(1));
        assertTrue(graph.containsNode(2));
        assertTrue(graph.containsNode(3));
        assertTrue(graph.containsNode(4));
        assertTrue(graph.containsNode(5));
        assertFalse(graph.containsNode(6));

        Collection<Integer> nodes = graph.getNodes();
        assertEquals(nodes.size(), graph.numNodes());
        for (Integer node : nodes)
        {
            assertTrue(graph.containsNode(node));
        }
        for (int i = 0; i < graph.numNodes(); ++i)
        {
            assertEquals(i, graph.getNodeId(graph.getNode(i)));
        }
        try
        {
            nodes.add(-1);
            assertTrue(false);
        }
        catch (UnsupportedOperationException uoe)
        {
            // The correct path
        }

        for (int i = 0; i < graph.numEdges(); ++i)
        {
            Pair<Integer, Integer> edge = graph.getEdgeEndpointIds(i);
            switch (edge.getFirst())
            {
                case 0:
                    testIn(edge.getSecond(), new int[]
                    {
                        1, 2
                    });
                    break;
                case 1:
                    testIn(edge.getSecond(), new int[]
                    {
                        0, 2, 3, 4
                    });
                    break;
                case 3:
                    testIn(edge.getSecond(), new int[]
                    {
                        4
                    });
                    break;
                case 4:
                    testIn(edge.getSecond(), new int[]
                    {
                        0
                    });
                    break;
                default:
                    assertTrue(false);
            }
        }

        testContains(graph.getSuccessors(0), new int[]
        {
            1, 2
        });
        testContains(graph.getSuccessors(1), new int[]
        {
            0, 2, 3, 4
        });
        testContains(graph.getSuccessors(2), new int[]
        {
        });
        testContains(graph.getSuccessors(3), new int[]
        {
            4
        });
        testContains(graph.getSuccessors(4), new int[]
        {
            0
        });
        testContains(graph.getSuccessors(5), new int[]
        {
        });

        graph.clear();
        assertEquals(0, graph.numNodes());
        assertEquals(0, graph.numEdges());
    }

}

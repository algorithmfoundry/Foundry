/*
 * File:                WeightedDenseMemoryGraphTest.java
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

import gov.sandia.cognition.util.DefaultKeyValuePair;
import gov.sandia.cognition.util.Pair;
import java.util.Collection;
import java.util.Iterator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 *
 * @author jdwendt
 */
public class WeightedDenseMemoryGraphTest
{

    private static void testContains(Collection<Pair<Integer, Double>> coll,
        int[] vals,
        double[] weights)
    {
        assertEquals(vals.length, coll.size());
        for (int i = 0; i < vals.length; ++i)
        {
            boolean found = false;
            for (Pair<Integer, Double> pair : coll)
            {
                found |= (pair.getFirst() == vals[i]) && (pair.getSecond()
                    == weights[i]);
            }
            assertTrue(found);
        }
    }

    @Test
    public void basicTest()
    {
        WeightedDenseMemoryGraph<Integer> graph
            = new WeightedDenseMemoryGraph<>(6, 8);
        graph.addEdge(0, 1);
        graph.addEdge(0, 2, 0.5);
        graph.addEdge(1, 0, 2.0);
        graph.addEdge(1, 2, 3.0);
        graph.addEdge(1, 3, 1.0);
        graph.addEdge(1, 4);
        graph.addEdge(3, 4, 0.0);
        graph.addEdge(4, 0, 0.2);
        graph.addNode(5);

        assertEquals(6, graph.getNumNodes());
        assertEquals(8, graph.getNumEdges());
        assertTrue(graph.containsNode(0));
        assertTrue(graph.containsNode(1));
        assertTrue(graph.containsNode(2));
        assertTrue(graph.containsNode(3));
        assertTrue(graph.containsNode(4));
        assertTrue(graph.containsNode(5));
        assertFalse(graph.containsNode(6));
        testContains(graph.getSuccessorsWithWeights(0), new int[]
        {
            1, 2
        }, new double[]
        {
            1, 0.5
        });
        testContains(graph.getSuccessorsWithWeights(1), new int[]
        {
            0, 2, 3, 4
        }, new double[]
        {
            2,
            3, 1, 1
        });
        testContains(graph.getSuccessorsWithWeights(2), new int[]
        {
        }, new double[]
        {
        });
        testContains(graph.getSuccessorsWithWeights(3), new int[]
        {
            4
        }, new double[]
        {
            0
        });
        testContains(graph.getSuccessorsWithWeights(4), new int[]
        {
            0
        }, new double[]
        {
            0.2
        });
        testContains(graph.getSuccessorsWithWeights(5), new int[]
        {
        }, new double[]
        {
        });

        Iterator<Integer> nodes = graph.getNodes().iterator();
        for (int i = 0; i < graph.getNumNodes(); ++i)
        {
            for (Pair<Integer, Double> successor
                : graph.getSuccessorsWithWeights(i))
            {
                assertTrue(graph.getSuccessors(i).contains(successor.getFirst()));
            }
            assertEquals(graph.getNode(i).intValue(), graph.getNodeId(
                graph.getNode(i)));
            assertEquals(graph.getNode(i), nodes.next());
        }
        assertFalse(nodes.hasNext());

        int lastStartNodeId = -1;
        for (int i = 0; i < graph.getNumEdges(); ++i)
        {
            Pair<Integer, Integer> edge = graph.getEdgeEndpointIds(i);
            if (edge.getFirst() != lastStartNodeId)
            {
                assertEquals(graph.getFirstEdgeFrom(edge.getFirst()), i);
                lastStartNodeId = edge.getFirst();
            }
            Pair<Integer, Double> successor = new DefaultKeyValuePair<>(
                edge.getSecond(), graph.getEdgeWeight(i));
            assertTrue(graph.getSuccessorsWithWeights(edge.getFirst()).contains(
                successor));
        }

        graph.clear();
        assertEquals(0, graph.getNumNodes());
        assertEquals(0, graph.getNumEdges());
    }

}

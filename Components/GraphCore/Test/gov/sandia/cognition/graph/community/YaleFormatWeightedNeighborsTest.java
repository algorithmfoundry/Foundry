/*
 * File:                YaleFormatWeightedNeighborsTest.java
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
import gov.sandia.cognition.util.Pair;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests YaleFormatWeightedNeighbors
 *
 * @author jdwendt
 */
public class YaleFormatWeightedNeighborsTest
{

    @Test
    public void test()
    {
        DirectedNodeEdgeGraph<Integer> g = new DenseMemoryGraph<>(8, 20);
        g.addEdge(0, 1);
        g.addEdge(0, 2);
        g.addEdge(0, 3);
        g.addEdge(1, 2);
        g.addEdge(1, 3);
        g.addEdge(2, 3);
        g.addEdge(3, 4);
        g.addEdge(4, 5);
        g.addEdge(4, 6);
        g.addEdge(4, 7);
        g.addEdge(5, 6);
        g.addEdge(5, 7);
        g.addEdge(6, 7);
        YaleFormatWeightedNeighbors<Integer> neigh
            = new YaleFormatWeightedNeighbors<>(g, false);
        assertEqual(g, neigh, 0, true);

        g.addEdge(2, 2);
        neigh = new YaleFormatWeightedNeighbors<>(g, false);
        assertEqual(g, neigh, 1, true);
        neigh = new YaleFormatWeightedNeighbors<>(g, true);
        assertEqual(g, neigh, 1, false);

        WeightedDenseMemoryGraph<Integer> wg = new WeightedDenseMemoryGraph<>(8,
            20);
        wg.addEdge(0, 1, .1);
        wg.addEdge(0, 2, .2);
        wg.addEdge(0, 3, .3);
        wg.addEdge(1, 2, .4);
        wg.addEdge(1, 3, .5);
        wg.addEdge(2, 3, .6);
        wg.addEdge(3, 4, .7);
        wg.addEdge(4, 5, .8);
        wg.addEdge(4, 6, .9);
        wg.addEdge(4, 7, 1);
        wg.addEdge(5, 6, 2);
        wg.addEdge(5, 7, 3);
        wg.addEdge(6, 7, 4);
        neigh = new YaleFormatWeightedNeighbors<>(wg, false);
        assertEqual(wg, neigh, 0, true);

        wg.addEdge(0, 0, 2);
        wg.addEdge(7, 7);
        neigh = new YaleFormatWeightedNeighbors<>(wg, false);
        assertEqual(wg, neigh, 2, true);
        neigh = new YaleFormatWeightedNeighbors<>(wg, true);
        assertEqual(wg, neigh, 2, false);
    }

    /**
     * Helper that returns the index into the neighbor object's arrays for the
     * input source node having a neighbor at destination.
     *
     * @param src The index of the source node
     * @param dst The index of the destination node
     * @param neigh The neighbor structure
     * @return The index of in neigh for src's link to dst
     */
    private static int getNeighborIndex(int src,
        int dst,
        YaleFormatWeightedNeighbors<Integer> neigh)
    {
        for (int i = neigh.getNeighborsFirstIndex().get(src); i
            < neigh.getNeighborsFirstIndex().get(src + 1); ++i)
        {
            if (neigh.getNeighbors().get(i) == dst)
            {
                return i;
            }
        }

        throw new RuntimeException("Unable to find neighbor " + dst
            + " in node " + src + "'s neighbors");
    }

    /**
     * Tests that (a) src contains a neighbor called dst, (b) with weight w
     *
     * @param src The source node's index
     * @param dst The destination node's index
     * @param w The weight for the edge between them
     * @param neigh The neighbor struct
     */
    private static void testNeighborAndWeightForEdge(int src,
        int dst,
        double w,
        YaleFormatWeightedNeighbors<Integer> neigh)
    {
        int idx = getNeighborIndex(src, dst, neigh);
        assertEquals(w, neigh.getNeighborsWeights().get(idx), 1e-15);
    }

    /**
     * Helper that ensures that neigh contains a proper representation of g
     *
     * @param g The original graph
     * @param neigh The neighbor object to check
     * @param numSelfLinks The number of self-links in the graph
     * @param selfLinksAllowed If true, neigh should contain self links
     */
    private static void assertEqual(DirectedNodeEdgeGraph<Integer> g,
        YaleFormatWeightedNeighbors<Integer> neigh,
        int numSelfLinks,
        boolean selfLinksAllowed)
    {
        assertEquals(g.getNumNodes() + 1, neigh.getNeighborsFirstIndex().size());
        if (selfLinksAllowed)
        {
            assertEquals(g.getNumEdges() * 2 - numSelfLinks,
                neigh.getNeighbors().size());
            assertEquals(g.getNumEdges() * 2 - numSelfLinks,
                neigh.getNeighborsWeights().size());
        }
        else
        {
            assertEquals((g.getNumEdges() - numSelfLinks) * 2,
                neigh.getNeighbors().size());
            assertEquals((g.getNumEdges() - numSelfLinks) * 2,
                neigh.getNeighborsWeights().size());
        }
        for (int i = 0; i < g.getNumEdges(); ++i)
        {
            Pair<Integer, Integer> e = g.getEdgeEndpointIds(i);
            double w = 1.0;
            if (g instanceof WeightedDenseMemoryGraph)
            {
                w = ((WeightedDenseMemoryGraph) g).getEdgeWeight(i);
            }
            if (e.getFirst().equals(e.getSecond()) && !selfLinksAllowed)
            {
                continue;
            }
            else if (e.getFirst().equals(e.getSecond()))
            {
                // If both endpoints point to one node, that node gets double weight
                w *= 2;
            }

            testNeighborAndWeightForEdge(e.getFirst(), e.getSecond(), w,
                neigh);
        }
    }

}

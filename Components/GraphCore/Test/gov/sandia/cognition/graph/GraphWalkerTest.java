/*
 * File:                GraphWalkerTest.java
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

import java.util.Map;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the GraphWalker class
 *
 * @author jdwendt
 */
public class GraphWalkerTest
{

    @Test
    public void basicTest()
    {
        DirectedNodeEdgeGraph<String> g = new DenseMemoryGraph<>();
        g.addEdge("a", "b");

        GraphWalker.RandomWalker<String> undirected
            = new GraphWalker.RandomWalker<>(false);
        GraphWalker<String> walker = new GraphWalker<>(g, undirected);
        assertEquals("b", walker.getEndNode("a", 1));
        assertEquals("b", walker.getEndNode("a", 3));
        assertEquals("a", walker.getEndNode("a", 2));
        assertEquals("b", walker.getEndNode("b", 2));
        assertEquals("a", walker.getEndNode("b", 3));
        GraphWalker.RandomWalker<String> directed
            = new GraphWalker.RandomWalker<>(true);
        walker = new GraphWalker<>(g, directed);
        assertEquals("b", walker.getEndNode("a", 1));
        assertEquals("b", walker.getEndNode("a", 3));
        assertEquals("b", walker.getEndNode("a", 2));
        assertEquals("b", walker.getEndNode("b", 2));
        assertEquals("b", walker.getEndNode("b", 3));

        g.addEdge("a", "c");
        g.addEdge("b", "d");
        walker = new GraphWalker<>(g, directed);
        Map<String, Integer> endNodes = walker.getEndNodes("a", 3, 1000);
        assertEquals(2, endNodes.size());
        assertTrue(endNodes.containsKey("c"));
        assertTrue(endNodes.containsKey("d"));
        // They should split 50/50 over time, but there will be some mess in the randomness
        assertTrue(endNodes.get("c") > 475);
        assertTrue(endNodes.get("d") > 475);

        // This one is more complicated, but you can work out the probabilities
        walker = new GraphWalker<>(g, undirected);
        endNodes = walker.getEndNodes("a", 2, 1000);
        assertEquals(2, endNodes.size());
        assertTrue(endNodes.containsKey("a"));
        assertTrue(endNodes.containsKey("d"));
        assertTrue(endNodes.get("a") > 720);
        assertTrue(endNodes.get("d") > 220);
    }

}

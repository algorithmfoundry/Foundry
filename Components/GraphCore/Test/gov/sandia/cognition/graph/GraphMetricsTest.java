/*
 * File:                GraphMetricsTest.java
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
import java.util.Set;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for the GraphMetrics class
 *
 * @author jdwendt
 */
public class GraphMetricsTest
{

    /**
     * Test basic functionality for all metrics
     */
    @Test
    public void basicTest()
    {
        DirectedNodeEdgeGraph<String> g = new DenseMemoryGraph<>(7, 10);
        g.addEdge("a", "b");
        g.addEdge("a", "c");
        g.addEdge("a", "d");
        g.addEdge("b", "c");
        g.addEdge("b", "d");
        g.addEdge("c", "d");
        g.addEdge("d", "e");
        g.addEdge("e", "f");
        g.addEdge("e", "g");
        g.addEdge("f", "g");
        g.addEdge("g", "g");

        GraphMetrics<String> metrics = new GraphMetrics<>(g);
        assertEquals(7, metrics.numNodes());
        assertEquals(11, metrics.numEdges());
        assertEquals(3, metrics.degree("a"));
        assertEquals(3, metrics.degree("b"));
        assertEquals(3, metrics.degree("c"));
        assertEquals(4, metrics.degree("d"));
        assertEquals(3, metrics.degree("e"));
        assertEquals(2, metrics.degree("f"));
        assertEquals(4, metrics.degree("g"));

        assertEquals(3, metrics.numSuccessors("a"));
        assertEquals(2, metrics.numSuccessors("b"));
        assertEquals(1, metrics.numSuccessors("c"));
        assertEquals(1, metrics.numSuccessors("d"));
        assertEquals(2, metrics.numSuccessors("e"));
        assertEquals(1, metrics.numSuccessors("f"));
        assertEquals(1, metrics.numSuccessors("g"));

        assertFalse(metrics.successors("a").contains("a"));
        assertTrue(metrics.successors("a").contains("b"));
        assertTrue(metrics.successors("a").contains("c"));
        assertTrue(metrics.successors("a").contains("d"));
        assertFalse(metrics.successors("a").contains("e"));
        assertFalse(metrics.successors("a").contains("f"));
        assertFalse(metrics.successors("a").contains("g"));
        assertFalse(metrics.successors("b").contains("a"));
        assertFalse(metrics.successors("b").contains("b"));
        assertTrue(metrics.successors("b").contains("c"));
        assertTrue(metrics.successors("b").contains("d"));
        assertFalse(metrics.successors("b").contains("e"));
        assertFalse(metrics.successors("b").contains("f"));
        assertFalse(metrics.successors("b").contains("g"));
        assertFalse(metrics.successors("c").contains("a"));
        assertFalse(metrics.successors("c").contains("b"));
        assertFalse(metrics.successors("c").contains("c"));
        assertTrue(metrics.successors("c").contains("d"));
        assertFalse(metrics.successors("c").contains("e"));
        assertFalse(metrics.successors("c").contains("f"));
        assertFalse(metrics.successors("c").contains("g"));
        assertFalse(metrics.successors("d").contains("a"));
        assertFalse(metrics.successors("d").contains("b"));
        assertFalse(metrics.successors("d").contains("c"));
        assertFalse(metrics.successors("d").contains("d"));
        assertTrue(metrics.successors("d").contains("e"));
        assertFalse(metrics.successors("d").contains("f"));
        assertFalse(metrics.successors("d").contains("g"));
        assertFalse(metrics.successors("e").contains("a"));
        assertFalse(metrics.successors("e").contains("b"));
        assertFalse(metrics.successors("e").contains("c"));
        assertFalse(metrics.successors("e").contains("d"));
        assertFalse(metrics.successors("e").contains("e"));
        assertTrue(metrics.successors("e").contains("f"));
        assertTrue(metrics.successors("e").contains("g"));
        assertFalse(metrics.successors("f").contains("a"));
        assertFalse(metrics.successors("f").contains("b"));
        assertFalse(metrics.successors("f").contains("c"));
        assertFalse(metrics.successors("f").contains("d"));
        assertFalse(metrics.successors("f").contains("e"));
        assertFalse(metrics.successors("f").contains("f"));
        assertTrue(metrics.successors("f").contains("g"));
        assertFalse(metrics.successors("g").contains("a"));
        assertFalse(metrics.successors("g").contains("b"));
        assertFalse(metrics.successors("g").contains("c"));
        assertFalse(metrics.successors("g").contains("d"));
        assertFalse(metrics.successors("g").contains("e"));
        assertFalse(metrics.successors("g").contains("f"));
        assertTrue(metrics.successors("g").contains("g"));

        assertEquals(3, metrics.numNeighbors("a"));
        assertEquals(3, metrics.numNeighbors("b"));
        assertEquals(3, metrics.numNeighbors("c"));
        assertEquals(4, metrics.numNeighbors("d"));
        assertEquals(3, metrics.numNeighbors("e"));
        assertEquals(2, metrics.numNeighbors("f"));
        // NOTE: One of its degree count is due to a self loop (where both ends are the same node
        assertEquals(3, metrics.numNeighbors("g"));

        assertFalse(metrics.neighbors("a").contains("a"));
        assertTrue(metrics.neighbors("a").contains("b"));
        assertTrue(metrics.neighbors("a").contains("c"));
        assertTrue(metrics.neighbors("a").contains("d"));
        assertFalse(metrics.neighbors("a").contains("e"));
        assertFalse(metrics.neighbors("a").contains("f"));
        assertFalse(metrics.neighbors("a").contains("g"));
        assertTrue(metrics.neighbors("b").contains("a"));
        assertFalse(metrics.neighbors("b").contains("b"));
        assertTrue(metrics.neighbors("b").contains("c"));
        assertTrue(metrics.neighbors("b").contains("d"));
        assertFalse(metrics.neighbors("b").contains("e"));
        assertFalse(metrics.neighbors("b").contains("f"));
        assertFalse(metrics.neighbors("b").contains("g"));
        assertTrue(metrics.neighbors("c").contains("a"));
        assertTrue(metrics.neighbors("c").contains("b"));
        assertFalse(metrics.neighbors("c").contains("c"));
        assertTrue(metrics.neighbors("c").contains("d"));
        assertFalse(metrics.neighbors("c").contains("e"));
        assertFalse(metrics.neighbors("c").contains("f"));
        assertFalse(metrics.neighbors("c").contains("g"));
        assertTrue(metrics.neighbors("d").contains("a"));
        assertTrue(metrics.neighbors("d").contains("b"));
        assertTrue(metrics.neighbors("d").contains("c"));
        assertFalse(metrics.neighbors("d").contains("d"));
        assertTrue(metrics.neighbors("d").contains("e"));
        assertFalse(metrics.neighbors("d").contains("f"));
        assertFalse(metrics.neighbors("d").contains("g"));
        assertFalse(metrics.neighbors("e").contains("a"));
        assertFalse(metrics.neighbors("e").contains("b"));
        assertFalse(metrics.neighbors("e").contains("c"));
        assertTrue(metrics.neighbors("e").contains("d"));
        assertFalse(metrics.neighbors("e").contains("e"));
        assertTrue(metrics.neighbors("e").contains("f"));
        assertTrue(metrics.neighbors("e").contains("g"));
        assertFalse(metrics.neighbors("f").contains("a"));
        assertFalse(metrics.neighbors("f").contains("b"));
        assertFalse(metrics.neighbors("f").contains("c"));
        assertFalse(metrics.neighbors("f").contains("d"));
        assertTrue(metrics.neighbors("f").contains("e"));
        assertFalse(metrics.neighbors("f").contains("f"));
        assertTrue(metrics.neighbors("f").contains("g"));
        assertFalse(metrics.neighbors("g").contains("a"));
        assertFalse(metrics.neighbors("g").contains("b"));
        assertFalse(metrics.neighbors("g").contains("c"));
        assertFalse(metrics.neighbors("g").contains("d"));
        assertTrue(metrics.neighbors("g").contains("e"));
        assertTrue(metrics.neighbors("g").contains("f"));
        // It's a neighbor to itself due to its self-loop
        assertTrue(metrics.neighbors("g").contains("g"));

        assertEquals(3, metrics.numNodeTriangles("a"));
        assertEquals(3, metrics.numNodeTriangles("b"));
        assertEquals(3, metrics.numNodeTriangles("c"));
        assertEquals(3, metrics.numNodeTriangles("d"));
        assertEquals(1, metrics.numNodeTriangles("e"));
        assertEquals(1, metrics.numNodeTriangles("f"));
        assertEquals(1, metrics.numNodeTriangles("g"));

        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("a"), "b", "c"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("a"), "b", "d"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("a"), "d", "c"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("b"), "a", "c"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("b"), "a", "d"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("b"), "d", "c"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("c"), "a", "d"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("c"), "a", "b"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("c"), "b", "d"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("d"), "a", "c"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("d"), "a", "b"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("d"), "b", "c"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("e"), "f", "g"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("f"), "e", "g"));
        assertTrue(
            triangleEndpoints(metrics.getNodeTriangleEndpoints("g"), "f", "e"));

        // a->b
        assertEquals(2.0 / 4.0, metrics.getEdgeJaccardSimilarity(0), 1e-6);
        assertEquals(1.0, metrics.getPerEdgeTriangleDensity(0), 1e-6);
        triangleEdgeEndpoints(metrics.getEdgeTriangleOtherEndpointNames(0),
            new String[]
            {
                "c", "d"
            });
        // a->c
        assertEquals(2.0 / 4.0, metrics.getEdgeJaccardSimilarity(1), 1e-6);
        assertEquals(1.0, metrics.getPerEdgeTriangleDensity(1), 1e-6);
        triangleEdgeEndpoints(metrics.getEdgeTriangleOtherEndpointNames(1),
            new String[]
            {
                "b", "d"
            });
        // a->d
        assertEquals(2.0 / 5.0, metrics.getEdgeJaccardSimilarity(2), 1e-6);
        assertEquals(4.0 / 5.0, metrics.getPerEdgeTriangleDensity(2), 1e-6);
        triangleEdgeEndpoints(metrics.getEdgeTriangleOtherEndpointNames(2),
            new String[]
            {
                "b", "c"
            });
        // b->c
        assertEquals(2.0 / 4.0, metrics.getEdgeJaccardSimilarity(3), 1e-6);
        assertEquals(1.0, metrics.getPerEdgeTriangleDensity(3), 1e-6);
        triangleEdgeEndpoints(metrics.getEdgeTriangleOtherEndpointNames(3),
            new String[]
            {
                "a", "d"
            });
        // b->d
        assertEquals(2.0 / 5.0, metrics.getEdgeJaccardSimilarity(4), 1e-6);
        assertEquals(4.0 / 5.0, metrics.getPerEdgeTriangleDensity(4), 1e-6);
        triangleEdgeEndpoints(metrics.getEdgeTriangleOtherEndpointNames(4),
            new String[]
            {
                "a", "c"
            });
        // c->d
        assertEquals(2.0 / 5.0, metrics.getEdgeJaccardSimilarity(5), 1e-6);
        assertEquals(4.0 / 5.0, metrics.getPerEdgeTriangleDensity(5), 1e-6);
        triangleEdgeEndpoints(metrics.getEdgeTriangleOtherEndpointNames(5),
            new String[]
            {
                "a", "b"
            });
        // d->e
        assertEquals(0.0 / 7.0, metrics.getEdgeJaccardSimilarity(6), 1e-6);
        assertEquals(0.0, metrics.getPerEdgeTriangleDensity(6), 1e-6);
        triangleEdgeEndpoints(metrics.getEdgeTriangleOtherEndpointNames(6),
            new String[]
            {
            });
        // e->f
        assertEquals(1.0 / 4.0, metrics.getEdgeJaccardSimilarity(7), 1e-6);
        assertEquals(2.0 / 3.0, metrics.getPerEdgeTriangleDensity(7), 1e-6);
        triangleEdgeEndpoints(metrics.getEdgeTriangleOtherEndpointNames(7),
            new String[]
            {
                "g"
            });
        // e->g
        assertEquals(2.0 / 4.0, metrics.getEdgeJaccardSimilarity(8), 1e-6);
        assertEquals(2.0 / 5.0, metrics.getPerEdgeTriangleDensity(8), 1e-6);
        triangleEdgeEndpoints(metrics.getEdgeTriangleOtherEndpointNames(8),
            new String[]
            {
                "f"
            });
        // f->g
        assertEquals(2.0 / 3.0, metrics.getEdgeJaccardSimilarity(9), 1e-6);
        assertEquals(1.0 / 2.0, metrics.getPerEdgeTriangleDensity(9), 1e-6);
        triangleEdgeEndpoints(metrics.getEdgeTriangleOtherEndpointNames(9),
            new String[]
            {
                "e"
            });
        // g->g
        assertEquals(3.0 / 3.0, metrics.getEdgeJaccardSimilarity(10), 1e-6);
        assertEquals(0.0, metrics.getPerEdgeTriangleDensity(10), 1e-6);
        triangleEdgeEndpoints(metrics.getEdgeTriangleOtherEndpointNames(10),
            new String[]
            {
            });

        assertEquals(3, metrics.getPerNodeEccentricity("a"));
        assertEquals(3, metrics.getPerNodeEccentricity("b"));
        assertEquals(3, metrics.getPerNodeEccentricity("c"));
        assertEquals(2, metrics.getPerNodeEccentricity("d"));
        assertEquals(2, metrics.getPerNodeEccentricity("e"));
        assertEquals(3, metrics.getPerNodeEccentricity("f"));
        assertEquals(3, metrics.getPerNodeEccentricity("g"));
        assertEquals(2, metrics.getRadius());
        assertEquals(3, metrics.getDiameter());
        assertTrue(metrics.isWcc());
    }

    private static void triangleEdgeEndpoints(Set<String> otherEndpoints,
        String[] correct)
    {
        assertEquals(correct.length, otherEndpoints.size());
        for (String s : correct)
        {
            assertTrue(otherEndpoints.contains(s));
        }
    }

    /**
     * Private helper that tests the triangle endpoints contain the input
     * vertices (order independently)
     *
     * @param triangles The set of triangle endpoints found
     * @param v1 One of the two endpoints to needed
     * @param v2 The other of the two endpoints needed
     * @return True if one of the two orderings of v1, v2 are in the input set
     */
    private static boolean triangleEndpoints(Set<Pair<String, String>> triangles,
        String v1,
        String v2)
    {
        return triangles.contains(new DefaultKeyValuePair<>(v1, v2))
            || triangles.contains(new DefaultKeyValuePair<>(v2, v1));
    }

    /**
     * Some specific tests for degree assortativity
     */
    @Test
    public void testDegreeAssortativity()
    {
        DirectedNodeEdgeGraph<String> g = new DenseMemoryGraph<>();
        for (int i = 0; i < 100; ++i)
        {
            for (int j = i + 1; j < 10; ++j)
            {
                g.addEdge("a" + i, "a" + j);
            }
        }
        GraphMetrics<String> metrics = new GraphMetrics<>(g);
        assertEquals(1.0, metrics.degreeAssortativity(), 1e-6);
        g.addEdge("a0", "a0");
        metrics = new GraphMetrics<>(g);
        assertTrue(metrics.degreeAssortativity() > 0);

        g = new DenseMemoryGraph<>();
        for (int i = 0; i < 100; ++i)
        {
            g.addEdge("a", "b" + i);
        }
        metrics = new GraphMetrics<>(g);
        assertEquals(-1.0, metrics.degreeAssortativity(), 1e-6);
        g.addEdge("b0", "b1");
        metrics = new GraphMetrics<>(g);
        assertEquals(-0.98, metrics.degreeAssortativity(), 1e-2);

        // This example from the "Scope" section of https://reference.wolfram.com/language/ref/GraphAssortativity.html
        g = new DenseMemoryGraph<>();
        g.addEdge("2", "1");
        g.addEdge("3", "1");
        g.addEdge("6", "1");
        g.addEdge("5", "1");
        g.addEdge("3", "6");
        g.addEdge("4", "6");
        g.addEdge("4", "5");
        metrics = new GraphMetrics<>(g);
        assertEquals(-5.0 / 9.0, metrics.degreeAssortativity(), 1e-6);
    }

    /**
     * Some corner-cases for triangle tests (no pun intended)
     */
    @Test
    public void testTriangleCornerCases()
    {
        // First test -- even though there are three different edges, they only
        // pass through two nodes.  By definition, a triangle requires three
        // nodes.
        DirectedNodeEdgeGraph<String> g = new DenseMemoryGraph<>();
        g.addEdge("a", "b");
        g.addEdge("b", "b");
        g.addEdge("b", "a");
        GraphMetrics<String> m = new GraphMetrics<>(g);
        assertEquals(0, m.numNodeTriangles("a"));
        assertEquals(0, m.numNodeTriangles("b"));
        assertEquals(0, m.numEdgeTriangles(0));
        assertEquals(0, m.numEdgeTriangles(1));
        assertEquals(0, m.numEdgeTriangles(2));

        // Second test -- even though there are many distinct edge-sets that
        // allow paths through these three nodes, we consider the triangle
        // the same here.
        g = new DenseMemoryGraph<>();
        g.addEdge("a", "b");
        g.addEdge("a", "b");
        g.addEdge("b", "c");
        g.addEdge("a", "c");
        g.addEdge("c", "a");
        g.addEdge("b", "a");
        m = new GraphMetrics<>(g);
        assertEquals(1, m.numNodeTriangles("a"));
        assertEquals(1, m.numNodeTriangles("b"));
        assertEquals(1, m.numNodeTriangles("c"));
        assertEquals(1, m.numEdgeTriangles(0));
        assertEquals(1, m.numEdgeTriangles(1));
        assertEquals(1, m.numEdgeTriangles(2));
        assertEquals(1, m.numEdgeTriangles(3));
        assertEquals(1, m.numEdgeTriangles(4));
        assertEquals(1, m.numEdgeTriangles(5));
    }

    @Test
    public void testEccentricityFurther()
    {
        // Test for a disconnected graph
        DirectedNodeEdgeGraph<String> g = new DenseMemoryGraph<>();
        g.addEdge("a", "b");
        g.addEdge("b", "c");
        g.addEdge("c", "d");
        g.addEdge("d", "e");
        g.addEdge("e", "f");
        g.addEdge("f", "g");
        g.addEdge("h", "i");
        g.addNode("j");
        g.addEdge("k", "l");
        g.addEdge("k", "m");
        g.addEdge("n", "o");
        g.addEdge("n", "p");
        g.addEdge("o", "p");
        GraphMetrics<String> m = new GraphMetrics<>(g);
        assertEquals(6, m.getPerNodeEccentricity("a"));
        assertEquals(5, m.getPerNodeEccentricity("b"));
        assertEquals(4, m.getPerNodeEccentricity("c"));
        assertEquals(3, m.getPerNodeEccentricity("d"));
        assertEquals(4, m.getPerNodeEccentricity("e"));
        assertEquals(5, m.getPerNodeEccentricity("f"));
        assertEquals(6, m.getPerNodeEccentricity("g"));
        assertEquals(1, m.getPerNodeEccentricity("h"));
        assertEquals(1, m.getPerNodeEccentricity("i"));
        assertEquals(0, m.getPerNodeEccentricity("j"));
        assertEquals(0, m.getPerNodeEccentricity("j"));
        assertEquals(1, m.getPerNodeEccentricity("k"));
        assertEquals(2, m.getPerNodeEccentricity("l"));
        assertEquals(2, m.getPerNodeEccentricity("m"));
        assertEquals(1, m.getPerNodeEccentricity("n"));
        assertEquals(1, m.getPerNodeEccentricity("o"));
        assertEquals(1, m.getPerNodeEccentricity("p"));
        assertEquals(Integer.MAX_VALUE, m.getRadius());
        assertEquals(Integer.MAX_VALUE, m.getDiameter());
        assertFalse(m.isWcc());
    }

}

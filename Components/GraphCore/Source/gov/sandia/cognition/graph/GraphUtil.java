/*
 * File:                GraphUtil.java
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

/**
 * Stores a variety of static utility methods for the graph package.
 *
 * @author jdwendt
 */
public class GraphUtil
{

    /**
     * Makes a deep copy of the input graph, matching implementation of the
     * interface.
     *
     * @param <NodeType> The type for nodes
     * @param src The graph to make a deep copy of
     * @return A deep copy of the input graph
     */
    public static <NodeType> DirectedNodeEdgeGraph<NodeType> deepCopy(
        DirectedNodeEdgeGraph<NodeType> src)
    {
        if (src == null)
        {
            return null;
        }
        if (src instanceof DirectedWeightedNodeEdgeGraph)
        {
            return deepCopy((DirectedWeightedNodeEdgeGraph<NodeType>) src);
        }
        DirectedNodeEdgeGraph<NodeType> dst = new DenseMemoryGraph<>(
            src.getNumNodes(), src.getNumEdges());

        for (NodeType node : src.getNodes())
        {
            dst.addNode(node);
        }
        for (int i = 0; i < src.getNumEdges(); ++i)
        {
            Pair<Integer, Integer> edge = src.getEdgeEndpointIds(i);
            dst.addEdge(src.getNode(edge.getFirst()), src.getNode(
                edge.getSecond()));
        }

        return dst;
    }

    /**
     * Helper that makes a deep copy of the input DirectedWeightedNodeEdgeGraph
     *
     * @param <NodeType> The type used to name nodes
     * @param src The graph to copy from
     * @return A deep copy of the input graph, replicating all nodes, edges, and
     * weights
     */
    private static <NodeType> DirectedWeightedNodeEdgeGraph<NodeType> deepCopy(
        DirectedWeightedNodeEdgeGraph<NodeType> src)
    {
        DirectedWeightedNodeEdgeGraph<NodeType> dst
            = new WeightedDenseMemoryGraph<>(src.getNumNodes(), src.getNumEdges());

        for (NodeType node : src.getNodes())
        {
            dst.addNode(node);
        }
        for (int i = 0; i < src.getNumEdges(); ++i)
        {
            Pair<Integer, Integer> edge = src.getEdgeEndpointIds(i);
            dst.addEdge(src.getNode(
                edge.getFirst()), src.getNode(edge.getSecond()),
                src.getEdgeWeight(i));
        }

        return dst;
    }

}

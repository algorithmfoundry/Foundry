/*
 * File:                YaleFormatWeightedNeighbors.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
import gov.sandia.cognition.graph.DirectedWeightedNodeEdgeGraph;
import gov.sandia.cognition.collection.DoubleArrayList;
import gov.sandia.cognition.collection.IntArrayList;
import gov.sandia.cognition.util.Pair;
import java.util.HashMap;
import java.util.Map;

/**
 * This class initializes the neighbors set of a graph as a Yale-format-like
 * graph. As this was needed by at least two algorithms already, it was
 * extracted to its own class. However, as it's fairly hard to interface with
 * directly if you don't know what you're doing (and those two classes do and
 * want into the guts) this is package private.
 *
 * @author jdwendt
 */
@PublicationReference(type = PublicationType.WebPage, author = "Wikipedia",
    title = "Sparse matrix - Compressed sparse row (CSR, CRS, or Yale format)",
    year = 2016, url
    = "https://en.wikipedia.org/wiki/Sparse_matrix#Compressed_sparse_row_.28CSR.2C_CRS_or_Yale_format.29")
class YaleFormatWeightedNeighbors<NodeNameType>
{

    /**
     * Yale-format-like representation of the neighbors of each node (see
     * https://en.wikipedia.org/wiki/Sparse_matrix#Compressed_sparse_row_.28CSR.2C_CRS_or_Yale_format.29
     * ). This specifies the index of the first neighbor in the neighbors list.
     */
    private final IntArrayList neighborsFirstIdx;

    /**
     * Yale-format-like representation of the neighbors of each node (see
     * https://en.wikipedia.org/wiki/Sparse_matrix#Compressed_sparse_row_.28CSR.2C_CRS_or_Yale_format.29
     * ). This contains the ids of all neighbors of all nodes in node-order. To
     * figure out a specific node's neighbors, look from indices
     * neighborsFirstIdx.get(i) to neighborsFirstIdx.get(i+1).
     */
    private final IntArrayList neighbors;

    /**
     * Yale-format-like representation of the neighbors of each node (see
     * https://en.wikipedia.org/wiki/Sparse_matrix#Compressed_sparse_row_.28CSR.2C_CRS_or_Yale_format.29
     * ). This contains the weights of all neighbors of all nodes in node-order.
     * Follows the same order as IntVector neighbors.
     */
    private final DoubleArrayList wNeighbors;

    /**
     * Initializes the three parts of the weighted Yale format for the neighbors
     * of all nodes in the input graph.
     *
     * @param graph The graph whose parameters should be initialized.
     * @param removeSelfLoops If true, self loops won't be in the computed
     * neighbors
     */
    public YaleFormatWeightedNeighbors(DirectedNodeEdgeGraph<NodeNameType> graph,
        boolean removeSelfLoops)
    {
        int numNodes = graph.getNumNodes();
        this.neighborsFirstIdx = new IntArrayList(numNodes + 1);

        // Initialize the per-node values
        int neighborsSoFar = 0;
        Map<Integer, HashMap<Integer, Double>> edges = new HashMap<>(
            graph.getNumNodes());
        for (int i = 0; i < graph.getNumNodes(); ++i)
        {
            edges.put(i, new HashMap<>());
        }
        for (int i = 0; i < graph.getNumEdges(); ++i)
        {
            Pair<Integer, Integer> edge = graph.getEdgeEndpointIds(i);
            int l = edge.getFirst();
            int r = edge.getSecond();
            if (removeSelfLoops && (l == r))
            {
                continue;
            }
            double w = 1.0;
            if (graph instanceof DirectedWeightedNodeEdgeGraph)
            {
                w = ((DirectedWeightedNodeEdgeGraph) graph).getEdgeWeight(i);
            }
            if (!edges.get(l).containsKey(r))
            {
                edges.get(l).put(r, 0.0);
            }
            edges.get(l).put(r, w + edges.get(l).get(r));
            if (!edges.get(r).containsKey(l))
            {
                edges.get(r).put(l, 0.0);
            }
            edges.get(r).put(l, w + edges.get(r).get(l));
        }
        for (int i = 0; i < graph.getNumNodes(); ++i)
        {
            // This is to optimize the nieghbors list
            this.neighborsFirstIdx.add(neighborsSoFar);
            neighborsSoFar += edges.get(i).size();
        }
        this.neighborsFirstIdx.add(neighborsSoFar);
        // Initialize neighbors to null values (for filling on next loop)
        this.neighbors = new IntArrayList(neighborsSoFar);
        this.wNeighbors = new DoubleArrayList(neighborsSoFar);
        for (int i = 0; i < neighborsSoFar; ++i)
        {
            this.neighbors.add(-1);
            this.wNeighbors.add(0);
        }
        // Initialize the per-edge values
        for (Map.Entry<Integer, HashMap<Integer, Double>> edgeMap
            : edges.entrySet())
        {
            int l = edgeMap.getKey();
            for (Map.Entry<Integer, Double> edge : edgeMap.getValue().entrySet())
            {
                int r = edge.getKey();
                if (removeSelfLoops && (l == r))
                {
                    continue;
                }
                double w = edge.getValue();
                int idx = findNextEmptyNeighbor(l);
                this.neighbors.set(idx, r);
                this.wNeighbors.set(idx, w);
            }
        }
    }

    /**
     * Private helper for adding neighbors to the internal neighbors list (which
     * is initialized to all -1s).
     *
     * @param nodeNum The node whose first empty neighbor spot is sought after
     * @return The index in neighbors where there's an opening for a neighbor
     * for nodeNum
     * @throws IllegalArgumentException if nodeNum has no more openings for
     * neighbors. This point should never be reached in this code.
     */
    private int findNextEmptyNeighbor(int nodeNum)
    {
        for (int i = neighborsFirstIdx.get(nodeNum); i < neighborsFirstIdx.get(
            nodeNum + 1); ++i)
        {
            if (neighbors.get(i) == -1)
            {
                return i;
            }
        }

        throw new IllegalArgumentException(
            "This node is full, but such shouldn't be possible");
    }

    /**
     * Returns the int vector containing the neighbors for all nodes stored in
     * Yale format. Alter this at your own risk!
     *
     * @return the int vector containing the neighbors for all nodes stored in
     * Yale format
     */
    public IntArrayList getNeighbors()
    {
        return neighbors;
    }

    /**
     * Returns the int vector containing the first index into the neighbors
     * vector for all nodes stored in Yale format. Alter this at your own risk!
     *
     * @return the int vector containing the first index into the neighbors
     * vector for all nodes stored in Yale format
     */
    public IntArrayList getNeighborsFirstIndex()
    {
        return neighborsFirstIdx;
    }

    /**
     * Returns the double vector containing the weights for all neighbors for
     * all nodes stored in Yale format. Alter this at your own risk!
     *
     * @return the double vector containing the weights for all neighbors for
     * all nodes stored in Yale format
     */
    public DoubleArrayList getNeighborsWeights()
    {
        return wNeighbors;
    }

}

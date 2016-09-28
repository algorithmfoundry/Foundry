/*
 * File:                WeightedDenseMemoryGraph.java
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
import gov.sandia.cognition.util.DoubleVector;
import gov.sandia.cognition.util.Pair;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation for a weighted graph type
 *
 * @author jdwendt
 *
 * @param <NodeNameType>
 */
public class WeightedDenseMemoryGraph<NodeNameType>
    extends DenseMemoryGraph<NodeNameType>
    implements DirectedWeightedNodeEdgeGraph<NodeNameType>
{

    private static final long serialVersionUID = 9009145019650654200L;

    /**
     * Storage for the weights -- This stays in-line with the edges when the
     * edges are added and sorted
     */
    private DoubleVector weights;

    /**
     * Default constructor creates an empty graph
     *
     * Execution: O(1)
     */
    public WeightedDenseMemoryGraph()
    {
        this(5, 10);
    }

    /**
     * Initialize an empty graph with a default size (for speed-ups later)
     *
     * Execution: O(m + n) for reserving necessary space
     *
     * @param expectedNumNodes
     * @param expectedNumEdges
     */
    public WeightedDenseMemoryGraph(int expectedNumNodes,
        int expectedNumEdges)
    {
        super(expectedNumNodes, expectedNumEdges);
        weights = new DoubleVector(expectedNumEdges);
    }

    /**
     * @see IDirectedWeightedGraph#addEdge(Object, Object)
     *
     * Execution: O(1)
     */
    @Override
    public void addEdge(NodeNameType left,
        NodeNameType right)
    {
        addEdge(left, right, 1.0);
    }

    /**
     * @see IDirectedWeightedGraph#addEdge(Object, Object, double)
     *
     * Execution: O(1)
     */
    @Override
    public void addEdge(NodeNameType left,
        NodeNameType right,
        double weight)
    {
        super.addEdge(left, right);
        weights.add(weight);
    }

    /**
     * Overrides the parent's version to ensure the weights swap at the same
     * time the edges do
     */
    @Override
    protected void swap(int i,
        int j)
    {
        super.swap(i, j);
        weights.swap(i, j);
    }

    /**
     * Execution: O(log m + d) where d is the degree of the specified node
     * (which can be O(m) but is usually O(1)). NOTE: This has a one-time O(m
     * log m) cost after adding new edges.
     *
     * @param node The node whose successors are wanted
     */
    @Override
    public Collection<Pair<NodeNameType, Double>> getSuccessorsWithWeights(
        NodeNameType node)
    {
        Set<Pair<NodeNameType, Double>> ret = new HashSet<>();
        // first check that the given node is in the graph
        int nodeId = getNodeId(node);

        int m = numEdges();

        // find matching node pair, and iterate through all that match
        int idx0 = getFirstEdgeFrom(nodeId);
        if (idx0 >= 0 && idx0 <= m)
        {
            for (int i = idx0; i < m; ++i)
            {
                Pair<Integer, Integer> endPts = getEdgeEndpointIds(i);
                if (endPts.getFirst() == nodeId)
                {
                    ret.add(new DefaultKeyValuePair<>(
                        getNode(endPts.getSecond()), weights.get(i)));
                }
                else if (endPts.getFirst() > nodeId)
                {
                    break;
                }
            }
        }

        // return the accumulated set of successors
        return ret;
    }

    /**
     * @see IDirectedWeightedGraph#getEdgeWeight(int)
     *
     * Execution: O(1) with a one-time O(m log m) cost after calling addEdge.
     */
    @Override
    public double getEdgeWeight(int id)
    {
        optimizeEdges();
        return weights.get(id);
    }

    /**
     * @see gov.sandia.graph.BasicDenseMemoryGraph#clear()
     */
    @Override
    public void clear()
    {
        super.clear();
        weights.clear();
    }

    /**
     * Helper method for serializing the input to file.
     *
     * @param fileName The filename (with optional path) to write this out to
     * @param graph The graph to write
     */
    public static void serialize(String fileName,
        WeightedDenseMemoryGraph<?> graph)
    {
        try (ObjectOutputStream out
            = new ObjectOutputStream(new FileOutputStream(fileName)))
        {
            out.writeObject(graph);
        }
        catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Helper method for deserializing from a file
     *
     * @param fileName The file (with path if necessary) to read a graph from
     * @return The file object read from the input filename
     */
    public static WeightedDenseMemoryGraph<?> deserialize(String fileName)
    {
        WeightedDenseMemoryGraph<?> graph = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(
            fileName)))
        {
            graph = (WeightedDenseMemoryGraph<?>) in.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            throw new RuntimeException(e);
        }
        return graph;
    }

}

/*
 * File:                DenseMemoryGraph.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright 2006, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.graph;

import gov.sandia.cognition.util.DefaultKeyValuePair;
import gov.sandia.cognition.util.IntVector;
import gov.sandia.cognition.util.Pair;
import gov.sandia.cognition.util.TwoWayIndex;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Most basic-est of graph types. This is a directed graph that stores node
 * names and edges between them
 *
 * @author jdwendt
 *
 * @param <NodeNameType> The type for the node names
 */
public class DenseMemoryGraph<NodeNameType>
    implements DirectedNodeEdgeGraph<NodeNameType>, java.io.Serializable
{

    /**
     *
     */
    private static final long serialVersionUID = 52275907258580715L;

    /**
     * For sorting the edges after they've been entered (Quicksort is slower
     * than Insertion Sort after the list gets small enough. This value sets the
     * switching point between Quicksort and Insertion Sort)
     */
    private static final int MIN_QSORT_SIZE = 5;

    /**
     * Node objects are stored herein. Their "id" is their position in the
     * array. This allows O(1) lookup of the node given the id. Storage: O(n).
     */
    private TwoWayIndex<NodeNameType> nodes;

    /**
     * This links source node and destination node for all edges. Each pair of
     * node id values are in order. The edge is identified by the half the index
     * into this array (because this stores them two-in-a-row). Storage: O(m).
     */
    private IntVector edges;

    /**
     * Stores whether the edge list is sorted. Set to false after each addEdge.
     * Set to true after optimizeEdges is called. Storage: O(1).
     */
    private boolean isOptimized;

    /**
     * Initializes an empty graph.
     *
     * Execution: O(1).
     */
    public DenseMemoryGraph()
    {
        this(5, 10);
    }

    /**
     * Initializes an empty graph, but sets aside O(m + n) memory. This speeds
     * up later filling of the graph by removing the need to re-allocate more
     * memory for the nodes and edges as the graph grows (until the input values
     * are exceeded at least).
     *
     * Execution: O(1) + time to allocate O(m+n) memory.
     *
     * @param expectedNumNodes The expected number of nodes for the graph
     * @param expectedNumEdges The expected number of edges for the graph
     */
    public DenseMemoryGraph(int expectedNumNodes,
        int expectedNumEdges)
    {
        nodes = new TwoWayIndex<>(expectedNumNodes);
        edges = new IntVector(expectedNumEdges * 2);
        isOptimized = true;
    }

    /**
     * @see IDirectedNodeEdgeGraph#getNodes()
     *
     * Execution: O(1) time
     */
    @Override
    public Collection<NodeNameType> getNodes()
    {
        return nodes.getValues();
    }

    /**
     * @see IDirectedNodeEdgeGraph#numNodes()
     *
     * Execution: O(1) time
     */
    @Override
    public int numNodes()
    {
        return nodes.size();
    }

    /**
     * @see IDirectedNodeEdgeGraph#numEdges()
     *
     * Execution: O(1) time
     */
    @Override
    public int numEdges()
    {
        return edges.size() / 2;
    }

    /**
     * @see IDirectedNodeEdgeGraph#addEdge(Object, Object)
     *
     * Execution: O(1) time
     */
    @Override
    public void addEdge(NodeNameType left,
        NodeNameType right)
    {
        newEdge(left, right);
    }

    /**
     * @see IDirectedNodeEdgeGraph#addNode(Object)
     *
     * Execution: O(1) time
     */
    @Override
    public void addNode(NodeNameType node)
    {
        newNode(node);
    }

    /**
     * @see IDirectedNodeEdgeGraph#containsNode(Object)
     *
     * Execution: O(1) time
     */
    @Override
    public boolean containsNode(NodeNameType node)
    {
        return nodes.contains(node);
    }

    /**
     * @see IDirectedNodeEdgeGraph#getSuccessors(Object)
     *
     * Execution: O(log m + d) where d is the degree of the input node (d can be
     * O(m) but usually is closer to O(1)) (With a O(m log m) one-time cost
     * after edges are newly added)
     */
    @Override
    public Collection<NodeNameType> getSuccessors(NodeNameType node)
    {
        Set<NodeNameType> ret = new HashSet<>();
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
                    ret.add(getNode(endPts.getSecond()));
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
     * Helper that returns the internal ID for the input node. Throws an
     * exception if the input node isn't found.
     *
     * Execution: O(1)
     *
     * @param node The node to search for
     * @return the internal ID for that node
     */
    @Override
    public int getNodeId(NodeNameType node)
    {
        return nodes.getIndex(node);
    }

    /**
     * Helper that returns the node for the internal ID. Throws an exception if
     * the input ID is invalid.
     *
     * Execution: O(1)
     *
     * @param nodeId The id for the sought-after node
     * @return The node for the input id
     */
    @Override
    public NodeNameType getNode(int nodeId)
    {
        return nodes.getValue(nodeId);
    }

    /**
     * Helper that returns the endpoint values for the input edge id.
     *
     * Execution: O(1) (one time O(m log m) cost after addEdges is called to get
     * the edges in the right order)
     *
     * @param i The edge id to search for
     * @return the endpoint values for the input edge id
     */
    @Override
    public Pair<Integer, Integer> getEdgeEndpointIds(int i)
    {
        optimizeEdges();
        return new DefaultKeyValuePair<>(edges.get(2 * i + 0),
            edges.get(2 * i + 1));
    }

    /**
     * Helper that returns the first edge that node nodeVal (id) participates
     * in.
     *
     * Execution: O(log m + d) where d is the degree of node nodeVal (With a O(m
     * log m) one-time cost after edges are newly added)
     *
     * @param nodeVal The id of the node whose first edge is sought
     * @return The integer id of the first edge nodeVal participates in
     */
    protected int getFirstEdgeFrom(int nodeVal)
    {
        optimizeEdges();
        int left = 0;
        int right = numEdges() - 1;

        // loop invariant: [0..left-1] <= target and [right+1..last] >= nodeVal
        while (left <= right)
        {
            int mid = left + ((right - left) / 2);
            int midRep = edges.get(2 * mid + 0);
            if (midRep < nodeVal)
            {
                left = mid + 1;
            }
            else if (midRep > nodeVal)
            {
                right = mid - 1;
            }
            else
            {
                right = mid;
                left = right + 1;
            }
        }

        // loop invariant: [right+1..last] >= nodeVal
        while (right >= 0)
        {
            int vRep = edges.get(2 * right + 0);
            if (vRep < nodeVal)
            {
                break;
            }
            right--;
        }

        // now right+1 is the first element not less than target
        return right + 1;
    }

    /**
     * Helper that adds the new node to the graph (if not already there).
     * Returns the internal id of the newly added node (or the id of the already
     * added node, if node already there).
     *
     * Execution: O(1) with very infrequent penalty for when the internal memory
     * usage needs to double in size
     *
     * @param node The node to add
     * @return The id of the newly added node (or the id of the already added
     * node, if node already there)
     */
    private int newNode(NodeNameType node)
    {
        if (nodes.contains(node))
        {
            return nodes.getIndex(node);
        }
        else
        {
            int n = nodes.putValue(node);
            return n;
        }
    }

    /**
     * Helper that adds a new edge from first to second (adding the nodes as
     * necessary)
     *
     * Execution: O(1) with very infrequent penalty for when the internal memory
     * usage needs to double in size
     *
     * @param first The first node to add
     * @param second The second node to add
     * @return The edge index of the new edge
     */
    synchronized private int newEdge(NodeNameType first,
        NodeNameType second)
    {
        isOptimized = false;
        int firstIdx = newNode(first);
        int secondIdx = newNode(second);

        // Divide by 2 because I'm storing them two-in-a-row
        int n = (edges.add(firstIdx) / 2);
        edges.add(secondIdx);

        return n;
    }

    /**
     * This private helper sorts all edges for quicker edge-lookup times.
     *
     * Execution: If !isOptimized, O(m log m), else O(1).
     */
    protected void optimizeEdges()
    {
        if (!isOptimized)
        {
            runOptimization();
        }
    }

    /**
     * The only reason for this second method is that there's no reason to
     * synchronize unless isOptimized is false. That is checked in the
     * non-synchronized code that this calls. It's checked a second time as it's
     * likely that a second thread could reach this method while the sorting is
     * still happening.
     */
    synchronized private void runOptimization()
    {
        if (!isOptimized)
        {
            quicksortEdges(0, numEdges() - 1);
            isOptimized = true;
        }
    }

    /**
     * This quicksort implementation sorts the edge ids array-of-pairs and
     * maintains their pairing. This code is from pseudocode in Intro to
     * Algorithms, 2nd ed. (Cormen, Leiserson, Rivest, and Stein), p. 154 and
     * 146.
     *
     * Execution: O(m log m).
     *
     * @param firstIdx The first index to include in the sort (inclusive)
     * @param lastIdx The last index to include in the sort (inclusive)
     */
    private void quicksortEdges(int firstIdx,
        int lastIdx)
    {
        Stack<Pair<Integer, Integer>> stack = new Stack<>();
        stack.push(new DefaultKeyValuePair<>(firstIdx, lastIdx));
        while (!stack.isEmpty())
        {
            // Pop the stack
            Pair<Integer, Integer> cur = stack.pop();
            int left = cur.getFirst();
            int right = cur.getSecond();

            // If this one is small enough, run insertion sort
            if (right - left < MIN_QSORT_SIZE)
            {
                insertion_sort_edges(left, right);
                continue;
            }

            // Choose a random pivot point (so that nearly-sorted lists are
            // still O(m log m)
            int randomIdx = ((int) (Math.random() * (right - left)) + left);
            swap(randomIdx, right);

            // Split so that the two sides are less than or greater than pivot
            int pivotSrc = edges.get(2 * right + 0);
            int pivotDst = edges.get(2 * right + 1);
            int i = left - 1;
            for (int j = left; j < right; ++j)
            {
                int edge_jSrc = edges.get(2 * j + 0);
                int edge_jDst = edges.get(2 * j + 1);
                long c = compare(pivotSrc, pivotDst, edge_jSrc, edge_jDst);
                if (c >= 0)
                {
                    ++i;
                    if (i != j)
                    {
                        swap(i, j);
                    }
                }
            }
            ++i;
            if (i != right)
            {
                swap(i, right);
            }

            // Push on the stack for the next iteration
            stack.push(new DefaultKeyValuePair<>(left, i - 1));
            stack.push(new DefaultKeyValuePair<>(i + 1, right));
        }
    }

    /**
     * Simple helper compares the indices for edge 1 (src1, dst1) and edge 2
     * (src2, dst2) and returns the Integer.compare for the srcs first (if not
     * equal) then the dsts second.
     *
     * @param src1 The source of edge 1
     * @param dst1 The destination of edge 1
     * @param src2 The source of edge 2
     * @param dst2 The destination of edge 2
     * @return The Integer.compare for the sources if they are non-equal, else
     * the Integer.compare for the destintations
     */
    private int compare(int src1,
        int dst1,
        int src2,
        int dst2)
    {
        if (src1 != src2)
        {
            return Integer.compare(src1, src2);
        }
        else
        {
            return Integer.compare(dst1, dst2);
        }
    }

    /**
     * Helper that computes the insertion sort for the edges between first and
     * last (inclusive).
     *
     * Execution: O(n log n) (but only ever called with very small n)
     *
     * @param first The index of the first edge to consider
     * @param last The index of the last edge to consider (inclusive)
     */
    private void insertion_sort_edges(int first,
        int last)
    {
        int pick; // which index to insert next
        int valSrc, valDst, edgeJSrc, edgeJDst;
        // considered

        for (int i = first; i < last; i++)
        {
            pick = i;
            valSrc = edges.get(pick * 2 + 0);
            valDst = edges.get(pick * 2 + 1);
            for (int j = i + 1; j <= last; j++)
            {
                edgeJSrc = edges.get(j * 2 + 0);
                edgeJDst = edges.get(j * 2 + 1);
                if (compare(valSrc, valDst, edgeJSrc, edgeJDst) > 0)
                {
                    pick = j;
                    valSrc = edgeJSrc;
                    valDst = edgeJDst;
                }
            }
            if (pick != i)
            {
                swap(i, pick);
            }
        }
    }

    /**
     * Helper that swaps the values at i and j in the edge list (handles the
     * fact that the edge list is 2 values in a row for each edge).
     *
     * Execution: O(1)
     *
     * @param i The edge index to swap with j
     * @param j The edge index to swap with i
     */
    protected void swap(int i,
        int j)
    {
        edges.swap(2 * i, 2 * j);
        edges.swap(2 * i + 1, 2 * j + 1);
    }

    /**
     * @see gov.sandia.graph.IDirectedNodeEdgeGraph#clear()
     */
    @Override
    public void clear()
    {
        nodes.clear();
        edges.clear();
        isOptimized = true;
    }

    /**
     * Helper method for serializing the input to file.
     *
     * @param fileName The filename (with optional path) to write this out to
     * @param graph The graph to write
     */
    public static void serialize(String fileName,
        DenseMemoryGraph<?> graph)
    {
        try (ObjectOutputStream out = new ObjectOutputStream(
            new FileOutputStream(fileName)))
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
    public static DenseMemoryGraph<?> deserialize(String fileName)
    {
        DenseMemoryGraph<?> graph = null;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(
            fileName)))
        {
            graph = (DenseMemoryGraph<?>) in.readObject();
        }
        catch (IOException | ClassNotFoundException ioe)
        {
            throw new RuntimeException(ioe);
        }
        return graph;
    }

}

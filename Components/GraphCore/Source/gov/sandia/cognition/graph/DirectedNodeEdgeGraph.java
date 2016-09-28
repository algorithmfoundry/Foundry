/*
 * File:                DirectedNodeEdgeGraph.java
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

/**
 * This interface defines the minimal set of methods necessary to create and
 * walk a directed graph.
 *
 * @author jdwendt
 *
 * @param <NodeNameType>
 */
public interface DirectedNodeEdgeGraph<NodeNameType>
{

    /**
     * Returns all of the nodes contained in the graph.
     *
     * @return all of the nodes in the graph
     */
    public Collection<NodeNameType> getNodes();

    /**
     * Return the number of nodes
     *
     * @return the number of nodes
     */
    public int numNodes();

    /**
     * Return the number of edges.
     *
     * @return the number of edges
     */
    public int numEdges();

    /**
     * Adds a directed edge from left to right. If left and right are not
     * already in the graph, they are added before the edge is added.
     *
     * @param left The source of the edge
     * @param right The destination of the edge
     */
    void addEdge(NodeNameType left,
        NodeNameType right);

    /**
     * Adds the input node to the graph. Calling this method is unnecessary if
     * the node participates in any edges. If you call
     * {@link #addEdge(Object, Object)}, it handles adding new nodes.
     *
     * @param node The node to add to the graph
     */
    void addNode(NodeNameType node);

    /**
     * Returns whether or not the graph contains the specified vertex.
     *
     * @param node The node to check for
     * @return true if this contains node
     */
    public boolean containsNode(NodeNameType node);

    /**
     * Returns all the nodes this node connects to (outgoing edges only).
     *
     * @param node The node whose successors are requested
     * @return all the nodes this node connects to.
     */
    public Collection<NodeNameType> getSuccessors(NodeNameType node);

    /**
     * Return the edge endpoints (by node id) for edge id (where id [0 ...
     * numEdges()).
     *
     * @param id The edge id
     * @return The node id pair for the source and destination of an edge (use
     * {@link #getNode(int)} to determine which nodes these are (if you care)).
     */
    public Pair<Integer, Integer> getEdgeEndpointIds(int id);

    /**
     * Returns the node label for the input id. To be used in conjunction with
     * {@link #getEdgeEndpointIds(int)}. If you pass in a bad id, an exception
     * will be thrown.
     *
     * @param id The node id
     * @return The node label for the input id
     */
    public NodeNameType getNode(int id);

    /**
     * Returns the integer ID of the input node. To be used in conjunction with
     * {@link #getEdgeEndpointIds(int)}.
     *
     * @param node The node value
     * @return The index for the node as stored internally
     */
    public int getNodeId(NodeNameType node);

    /**
     * Clears the graph back to the original, empty state
     */
    public void clear();

}

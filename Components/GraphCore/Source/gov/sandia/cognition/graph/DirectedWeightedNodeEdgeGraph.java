/*
 * File:                DirectedWeightedNodeEdgeGraph.java
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
 * Adds the necessary methods for a graph with weighted edges.
 *
 * @author jdwendt
 *
 * @param <NodeNameType> The type used to specify nodes
 */
public interface DirectedWeightedNodeEdgeGraph<NodeNameType>
    extends
    DirectedNodeEdgeGraph<NodeNameType>
{

    /**
     * Adds a directed edge from left to right. If left and right are not
     * already in the graph, they are added before the edge is added. NOTE:
     * Calling this method results in an edge with 1.0 weight.
     *
     * @param left The source of the edge
     * @param right The destination of the edge
     */
    @Override
    void addEdge(NodeNameType left,
        NodeNameType right);

    /**
     * Adds a directed edge from left to right with the input weight. If left
     * and right are not already in the graph, they are added before the edge is
     * added.
     *
     * @param left The source of the edge
     * @param right The destination of the edge
     * @param weight The weight to assign this edge
     */
    void addEdge(NodeNameType left,
        NodeNameType right,
        double weight);

    /**
     * Returns all the nodes this node connects to with edge weights (outgoing
     * edges only).
     *
     * @param node The node whose successors and weights are wanted
     * @return all the nodes this node connects to.
     */
    public Collection<Pair<NodeNameType, Double>> getSuccessorsWithWeights(
        NodeNameType node);

    /**
     * This returns the weight of edge id (where id is [0 ... numEdges())). Note
     * that the ids for edges are not necessarily fixed across calls to addEdge
     * (as edge ordering can change due to insertion of new edges).
     *
     * @param id The id of the edge whose weight is desired
     * @return The weight for edge id
     */
    public double getEdgeWeight(int id);

}

/*
 * File:                GraphWrappingEnergyFunction.java
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

package gov.sandia.cognition.graph.inference;

import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a simple wrapper for any input graph becoming a BP-capable
 * graph. It does require the caller prepare a class that will provide the
 * pairwise and unary potentials for edges and nodes and the possible labels for
 * nodes. This class is intended to be the top layer in layered energy function
 * implementations.
 *
 * @author jdwendt
 * @param <LabelType> The type for labels assigned to nodes
 * @param <NodeNameType> The type for names of nodes
 */
public class GraphWrappingEnergyFunction<LabelType, NodeNameType>
    implements NodeNameAwareEnergyFunction<LabelType, NodeNameType>
{

    /**
     * The graph itself
     */
    private final DirectedNodeEdgeGraph<NodeNameType> graph;

    /**
     * The handler for providing problem-specific values
     */
    private final PotentialHandler<LabelType, NodeNameType> handler;

    /**
     * Contains the nodes which have been assigned a BP label.
     */
    private final Map<Integer, LabelType> labeledNodes;

    /**
     * This speeds up requests for getting edges by caching them. As the edges
     * are iterated over likely many times, it's worth the memory cost.
     */
    private final List<Pair<Integer, Integer>> edges;

    /**
     * This interface defines the problem-specific methods this class requires
     * as input
     *
     * @param <LabelType> The type for labels assigned to nodes
     * @param <NodeNameType> The type for names of nodes
     */
    public interface PotentialHandler<LabelType, NodeNameType>
    {

        /**
         * Provide the pairwise potential for the specified edge
         *
         * @param graph The graph the edge is from
         * @param edgeId The id of the edge whose potential is wanted
         * @param ilabel The label being considered for the first node on the
         * edge
         * @param jlabel The label being considered for the second node on the
         * edge
         * @return the pairwise potential
         */
        public double getPairwisePotential(
            DirectedNodeEdgeGraph<NodeNameType> graph,
            int edgeId,
            LabelType ilabel,
            LabelType jlabel);

        /**
         * Provide the unary potential for the specified node
         *
         * @param graph The graph the node is from
         * @param i The id of the node whose potential is wanted
         * @param label The label being considered for the node
         * @param assignedLabel the label assigned to the node (null if none
         * assigned via setLabel)
         * @return the unary potential
         */
        public double getUnaryPotential(
            DirectedNodeEdgeGraph<NodeNameType> graph,
            int i,
            LabelType label,
            LabelType assignedLabel);

        /**
         * Get the possible labels for the specified node
         *
         * @param graph The graph the node is from
         * @param nodeId The id of the node in the graph
         * @return the possible labels for that node
         */
        public List<LabelType> getPossibleLabels(
            DirectedNodeEdgeGraph<NodeNameType> graph,
            int nodeId);

    }

    /**
     * Creates a new instance of this class containing the input graph (shallow
     * copy -- don't change it after this!) and the handler for problem-specific
     * details (also only a shallow copy).
     *
     * @param graph The graph this contains
     * @param handler The handler for problem-specific methods
     */
    public GraphWrappingEnergyFunction(
        DirectedNodeEdgeGraph<NodeNameType> graph,
        PotentialHandler<LabelType, NodeNameType> handler)
    {
        this.labeledNodes = new HashMap<>();
        this.graph = graph;
        this.handler = handler;
        this.edges = new ArrayList<>(graph.numEdges());
        for (int i = 0; i < graph.numEdges(); ++i)
        {
            edges.add(graph.getEdgeEndpointIds(i));
        }
    }

    /**
     * @see EnergyFunction#getPossibleLabels(int)
     */
    @Override
    public Collection<LabelType> getPossibleLabels(int nodeId)
    {
        return handler.getPossibleLabels(graph, nodeId);
    }

    /**
     * @see EnergyFunction#getEdge(int)
     */
    @Override
    public Pair<Integer, Integer> getEdge(int i)
    {
        return edges.get(i);
    }

    /**
     * @see EnergyFunction#getUnaryPotential(int, java.lang.Object)
     */
    @Override
    public double getUnaryPotential(int i,
        LabelType label)
    {
        return handler.getUnaryPotential(graph, i, label, labeledNodes.get(i));
    }

    /**
     * @see EnergyFunction#getUnaryCost(int, java.lang.Object)
     */
    @Override
    public double getUnaryCost(int i,
        LabelType label)
    {
        double p = getUnaryPotential(i, label);
        // This is actually faster than evaluating -Math.log(0)
        if (p == 0)
        {
            return Double.MAX_VALUE;
        }
        return -Math.log(p);
    }

    /**
     * @see EnergyFunction#getPairwisePotential(int, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public double getPairwisePotential(int edgeId,
        LabelType ilabel,
        LabelType jlabel)
    {
        return handler.getPairwisePotential(graph, edgeId, ilabel, jlabel);
    }

    /**
     * @see EnergyFunction#getPairwiseCost(int, java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public double getPairwiseCost(int edgeId,
        LabelType ilabel,
        LabelType jlabel)
    {
        double p = getPairwisePotential(edgeId, ilabel, jlabel);
        // This is actually faster than evaluating -Math.log(0)
        if (p == 0)
        {
            return Double.MAX_VALUE;
        }
        return -Math.log(p);
    }

    /**
     * @see EnergyFunction#numEdges()
     */
    @Override
    public int numEdges()
    {
        return graph.numEdges();
    }

    /**
     * @see EnergyFunction#numNodes()
     */
    @Override
    public int numNodes()
    {
        return graph.numNodes();
    }

    /**
     * @see NodeTypeAwareEnergyFunction#setLabel(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void setLabel(NodeNameType node,
        LabelType label)
    {
        int nodeId = graph.getNodeId(node);
        if (!handler.getPossibleLabels(graph, nodeId).contains(label))
        {
            throw new IllegalArgumentException("Input label (" + label
                + ") can't be assigned to node " + node);
        }
        labeledNodes.put(nodeId, label);
    }

    /**
     * Clears labels previously set. Critical for if you want to re-use the same
     * energy function on multiple different runs.
     */
    public void clearLabels()
    {
        labeledNodes.clear();
    }

    /**
     * @see NodeTypeAwareEnergyFunction#getBeliefs(java.lang.Object,
     * gov.sandia.cognition.graph.inference.BeliefPropagation)
     */
    @Override
    public Map<LabelType, Double> getBeliefs(NodeNameType node,
        EnergyFunctionSolver<LabelType> bp)
    {
        Map<LabelType, Double> ret = new HashMap<>();
        int nodeId = graph.getNodeId(node);
        List<LabelType> labels = handler.getPossibleLabels(graph, nodeId);
        for (int i = 0; i < labels.size(); ++i)
        {
            ret.put(labels.get(i), bp.getBelief(nodeId, i));
        }

        return ret;
    }

}

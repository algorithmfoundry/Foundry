/*
 * File:                CostSpeedupEnergyFunction.java
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

import gov.sandia.cognition.util.Pair;
import java.util.Collection;
import java.util.Map;

/**
 * This class trades memory usage (to store all of the costs) for compute time.
 * We've found that sometimes one of the slowest parts of belief propagation is
 * the multiple log operations, and this class eliminates a significant number
 * of those. It appears that it depends on the size of the graph if the memory
 * overhead outweighs the benefits of the less computations.
 *
 * @author jdwendt
 * @param <LabelType> The labels that can be assigned to nodes
 * @param <NodeNameType> The type used to name nodes
 */
public class CostSpeedupEnergyFunction<LabelType, NodeNameType>
    implements NodeNameAwareEnergyFunction<LabelType, NodeNameType>
{

    /**
     * The energy function being wrapped by this
     */
    private final NodeNameAwareEnergyFunction<LabelType, NodeNameType> wrapped;

    /**
     * The local cache of pairwise costs
     */
    private final double[][] pairwiseCosts;

    /**
     * The local cache of unary costs
     */
    private final double[][] unaryCosts;

    /**
     * Initializes this with the wrapped function and empty values for the
     * pairwise costs (which are only computed and stored as needed).
     *
     * @param wrapme The function to wrap with this one
     */
    public CostSpeedupEnergyFunction(
        NodeNameAwareEnergyFunction<LabelType, NodeNameType> wrapme)
    {
        this.wrapped = wrapme;
        int m = wrapped.numEdges();
        this.pairwiseCosts = new double[m][];
        for (int i = 0; i < m; ++i)
        {
            Pair<Integer, Integer> edge = wrapme.getEdge(i);
            int srcLabelsCnt = wrapme.getPossibleLabels(edge.getFirst()).size();
            int dstLabelsCnt = wrapme.getPossibleLabels(edge.getSecond()).size();
            int size = srcLabelsCnt * dstLabelsCnt;
            this.pairwiseCosts[i] = new double[size];
            for (int j = 0; j < size; ++j)
            {
                this.pairwiseCosts[i][j] = Double.MAX_VALUE;
            }
        }
        int n = wrapped.numNodes();
        this.unaryCosts = new double[n][];
        for (int i = 0; i < n; ++i)
        {
            int size = wrapme.getPossibleLabels(i).size();
            this.unaryCosts[i] = new double[size];
            for (int j = 0; j < size; ++j)
            {
                this.unaryCosts[i][j] = Double.MAX_VALUE;
            }
        }
    }

    /**
     * Clears the pre-computed costs that were stored to keep from calling log
     * and potential each time. Necessary for if we want to re-use the function
     * without re-initializing memory.
     */
    public void clearStoredCosts()
    {
        int m = wrapped.numEdges();
        for (int i = 0; i < m; ++i)
        {
            for (int j = 0; j < this.pairwiseCosts[i].length; ++j)
            {
                this.pairwiseCosts[i][j] = Double.MAX_VALUE;
            }
        }
        int n = wrapped.numNodes();
        for (int i = 0; i < n; ++i)
        {
            for (int j = 0; j < this.unaryCosts[i].length; ++j)
            {
                this.unaryCosts[i][j] = Double.MAX_VALUE;
            }
        }
    }

    /**
     * @see NodeNameAwareEnergyFunction#setLabel(java.lang.Object,
     * java.lang.Object)
     */
    @Override
    public void setLabel(NodeNameType node,
        LabelType label)
    {
        wrapped.setLabel(node, label);
    }

    /**
     * @see NodeNameAwareEnergyFunction#getBeliefs(java.lang.Object,
     * gov.sandia.cognition.graph.inference.BeliefPropagation)
     */
    @Override
    public Map<LabelType, Double> getBeliefs(NodeNameType node,
        EnergyFunctionSolver<LabelType> bp)
    {
        return wrapped.getBeliefs(node, bp);
    }

    /**
     * @see EnergyFunction#getPossibleLabels(int)
     */
    @Override
    public Collection<LabelType> getPossibleLabels(int nodeId)
    {
        return wrapped.getPossibleLabels(nodeId);
    }

    /**
     * @see EnergyFunction#numEdges()
     */
    @Override
    public int numEdges()
    {
        return wrapped.numEdges();
    }

    /**
     * @see EnergyFunction#numNodes()
     */
    @Override
    public int numNodes()
    {
        return wrapped.numNodes();
    }

    /**
     * @see EnergyFunction#getEdge(int)
     */
    @Override
    public Pair<Integer, Integer> getEdge(int i)
    {
        return wrapped.getEdge(i);
    }

    /**
     * @see EnergyFunction#getUnaryPotential(int, java.lang.Object)
     */
    @Override
    public double getUnaryPotential(int i,
        LabelType label)
    {
        return wrapped.getUnaryPotential(i, label);
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
        return wrapped.getPairwisePotential(edgeId, ilabel, jlabel);
    }

    /**
     * Helper that takes a collection and a value which should be from that
     * collection and returns the index of that value from that collection.
     *
     * @param <LabelType> The type for the values in the collection
     * @param label The value whose index is needed
     * @param labels The collection
     * @return The index of the value in the collection
     */
    private static <LabelType> int indexOf(LabelType label,
        Collection<LabelType> labels)
    {
        int idx = 0;
        for (LabelType l : labels)
        {
            if (l.equals(label))
            {
                return idx;
            }
            ++idx;
        }
        throw new RuntimeException("Unable to find input label (" + label
            + ") in input");
    }

    /**
     * @see EnergyFunction#getUnaryCost(int, java.lang.Object)
     */
    @Override
    public double getUnaryCost(int i,
        LabelType label)
    {
        Collection<LabelType> labels = wrapped.getPossibleLabels(i);
        int idx = indexOf(label, labels);
        if (unaryCosts[i][idx] == Double.MAX_VALUE)
        {
            unaryCosts[i][idx] = wrapped.getUnaryCost(i, label);
        }
        return unaryCosts[i][idx];
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
        Pair<Integer, Integer> endpoints = wrapped.getEdge(edgeId);
        Collection<LabelType> ilabels = wrapped.getPossibleLabels(
            endpoints.getFirst());
        Collection<LabelType> jlabels = wrapped.getPossibleLabels(
            endpoints.getSecond());
        int idx = indexOf(ilabel, ilabels) * jlabels.size() + indexOf(jlabel,
            jlabels);
        if (pairwiseCosts[edgeId][idx] == Double.MAX_VALUE)
        {
            pairwiseCosts[edgeId][idx] = wrapped.getPairwiseCost(edgeId, ilabel,
                jlabel);
        }
        return pairwiseCosts[edgeId][idx];
    }

}

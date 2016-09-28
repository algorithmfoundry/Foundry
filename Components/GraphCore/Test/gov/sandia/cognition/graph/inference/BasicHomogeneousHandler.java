/*
 * File:                BasicHomogeneousHandler.java
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simple class created for multiple tests that tests homogenous systems.
 *
 * @author jdwendt
 * @param <NodeLabelType> The type for the nodes
 */
public class BasicHomogeneousHandler<NodeLabelType>
    implements
    GraphWrappingEnergyFunction.PotentialHandler<Integer, NodeLabelType>
{

    private final Map<Integer, Double> equalsProb;

    public BasicHomogeneousHandler()
    {
        equalsProb = new HashMap<>();
    }

    public void setSpecialUnaryPotential(NodeLabelType node,
        DirectedNodeEdgeGraph<NodeLabelType> graph,
        double prob)
    {
        equalsProb.put(graph.getNodeId(node), prob);
    }

    @Override
    public double getPairwisePotential(
        DirectedNodeEdgeGraph<NodeLabelType> graph,
        int edgeId,
        Integer ilabel,
        Integer jlabel)
    {
        if (ilabel.equals(jlabel))
        {
            return 0.99;
        }
        else
        {
            return 0.01;
        }
    }

    @Override
    public double getUnaryPotential(
        DirectedNodeEdgeGraph<NodeLabelType> graph,
        int i,
        Integer label,
        Integer assignedLabel)
    {
        if (assignedLabel != null)
        {
            if (assignedLabel.equals(label))
            {
                if (equalsProb.containsKey(i))
                {
                    return equalsProb.get(i);
                }
                return 0.9999999;
            }
            else
            {
                if (equalsProb.containsKey(i))
                {
                    return 1 - equalsProb.get(i);
                }
                return 0.0000001;
            }
        }
        else
        {
            return 0.5;
        }
    }

    @Override
    public List<Integer> getPossibleLabels(
        DirectedNodeEdgeGraph<NodeLabelType> graph,
        int nodeId)
    {
        List<Integer> ret = new ArrayList<>(2);
        ret.add(0);
        ret.add(1);

        return ret;
    }

}

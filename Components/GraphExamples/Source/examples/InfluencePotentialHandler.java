/*
 * File:                InfluencePotentialHandler.java
 * Authors:             Tu-Thach Quach and Jeremy D. Wendt
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
package examples;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
import gov.sandia.cognition.graph.DirectedWeightedNodeEdgeGraph;
import gov.sandia.cognition.graph.inference.GraphWrappingEnergyFunction.PotentialHandler;
import gov.sandia.cognition.util.DoubleVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Potential handler for influence propagation. This class stores/returns what
 * each node's unary potential is and the per-edge pairwise potentials.
 *
 * @author jdwendt and tong
 *
 */
@PublicationReference(author
    = "Tu-Thach Quach and Jeremy D. Wendt",
    title
    = "A diffusion model for maximizing influence spread in large networks",
    type = PublicationType.Conference,
    publication
    = "Proceedings of the International Conference on Social Informatics", year
    = 2016)
public class InfluencePotentialHandler
    implements PotentialHandler<Integer, String>
{

    public static double MINIMUM_EDGE_POTENTIAL = 0.001;

    private final List<Integer> labels;

    private final DoubleVector unaryPotentials;

    /**
     * Creates a potential handler.
     *
     * @param graph The graph being wrapped
     * @param unaryPotentials Map of node (user) to unary potential of being in
     * state 1 (activated).
     */
    public InfluencePotentialHandler(DirectedNodeEdgeGraph<String> graph,
        Map<String, Double> unaryPotentials)
    {
        labels = new ArrayList<>();
        labels.add(0);
        labels.add(1);

        int n = graph.numNodes();
        this.unaryPotentials = new DoubleVector(n);
        for (int i = 0; i < n; ++i)
        {
            this.unaryPotentials.add(-Double.MAX_VALUE);
        }
        for (Map.Entry<String, Double> e : unaryPotentials.entrySet())
        {
            this.unaryPotentials.set(graph.getNodeId(e.getKey()), e.getValue());
        }
        // Now make sure all were set
        for (int i = 0; i < n; ++i)
        {
            if (this.unaryPotentials.get(i) == -Double.MAX_VALUE)
            {
                throw new RuntimeException(
                    "No unary potential passed in for node " + graph.getNode(i));
            }
        }
    }

    /**
     * @see
     * PotentialHandler#getPossibleLabels(gov.sandia.cognition.graph.DirectedNodeEdgeGraph,
     * int)
     */
    @Override
    public List<Integer> getPossibleLabels(DirectedNodeEdgeGraph<String> graph,
        int node)
    {
        return labels;
    }

    /**
     * @see
     * PotentialHandler#getPairwisePotential(gov.sandia.cognition.graph.DirectedNodeEdgeGraph,
     * int, java.lang.Object, java.lang.Object)
     */
    @Override
    public double getPairwisePotential(DirectedNodeEdgeGraph<String> graph,
        int edgeId,
        Integer ilabel,
        Integer jlabel)
    {
        // 0.5 0.5
        // 1-p p
        if (ilabel == 0)
        {
            return 0.5; // No influence.
        }
        else
        {
            double p = Math.max(MINIMUM_EDGE_POTENTIAL,
                ((DirectedWeightedNodeEdgeGraph<String>) graph).getEdgeWeight(
                    edgeId));
            if (jlabel == 0)
            {
                return 1 - p;
            }
            else
            {
                return p;
            }
        }
    }

    /**
     * @see
     * PotentialHandler#getUnaryPotential(gov.sandia.cognition.graph.DirectedNodeEdgeGraph,
     * int, java.lang.Object, java.lang.Object)
     */
    @Override
    public double getUnaryPotential(DirectedNodeEdgeGraph<String> graph,
        int i,
        Integer label,
        Integer assignedLabel)
    {
        // A seed node will have an assigned label.
        if (assignedLabel != null)
        {
            if (label.equals(assignedLabel))
            {
                return 1;
            }
            else
            {
                return 0; // Seed node cannot take on label 0.
            }
        }
        else
        {
            double p = unaryPotentials.get(i);
            if (label == 0)
            {
                return 1 - p;
            }
            else
            {
                return p;
            }
        }
    }

}

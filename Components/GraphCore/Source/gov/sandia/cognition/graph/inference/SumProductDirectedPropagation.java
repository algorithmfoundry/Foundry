/*
 * File:                SumProductPairwiseBayesNet.java
 * Authors:             Jeremy Wendt
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.util.Pair;
import java.util.Collection;

/**
 * This class implements a Bayes Net -- but only allowing for pairwise influence
 * along edges. That is p(x | a, b, c) is approximated by p(x | a) * p(x | b) *
 * p(x | c). We realize that's a considerable approximation, but on real graphs
 * with very high degree nodes, it quickly becomes infeasible to compute p(x |
 * a, b, ...) when there are hundreds of incoming edges and some approximation
 * must be chosen.
 *
 * @author jdwendt
 * @param <LabelType>
 */
@PublicationReference(author
    = "Tu-Thach Quach and Jeremy D. Wendt",
    title
    = "A diffusion model for maximizing influence spread in large networks",
    type = PublicationType.Conference,
    publication
    = "Proceedings of the International Conference on Social Informatics", year
    = 2016)
public class SumProductDirectedPropagation<LabelType>
    extends SumProductInferencingAlgorithm<LabelType>
{

    /**
     * Creates a new pairwise Bayes Net solver with the specified settings.
     *
     * @param maxNumIterations The maximum number of iterations that will be run
     * @param eps The stopping epsilon that will be used
     * @param numThreads The number of threads that will be used
     */
    public SumProductDirectedPropagation(int maxNumIterations,
        double eps,
        int numThreads)
    {
        super(maxNumIterations, eps, numThreads);
    }

    /**
     * Creates a new pairwise Bayes Net solver with the default settings
     * excepting max number of iterations.
     *
     * @param maxNumIterations The maximum number of iterations that will be run
     */
    public SumProductDirectedPropagation(int maxNumIterations)
    {
        this(maxNumIterations, DEFAULT_EPS, DEFAULT_NUM_THREADS);
    }

    /**
     * Creates a new pairwise Bayes Net solver with the default settings.
     */
    public SumProductDirectedPropagation()
    {
        this(DEFAULT_MAX_ITERATIONS, DEFAULT_EPS, DEFAULT_NUM_THREADS);
    }

    @Override
    void initMessages(Pair<Integer, Integer> edgePair)
    {
        Node<LabelType> node = nodes.get(edgePair.getSecond());
        node.link(edgePair.getFirst(), false);
    }

    /**
     * Private helper that computes the temporary message for the specified edge
     * for the current iteration going in the specified direction
     *
     * @param edge The edge to compute the temporary message for (you can't
     * replace the current message yet as the current message may be needed for
     * other edges).
     */
    @Override
    protected void computeTemporaryMessage(int edge)
    {
        Pair<Integer, Integer> edgePair = fn.getEdge(edge);
        Node<LabelType> sourceNode = nodes.get(edgePair.getFirst());
        Node<LabelType> targetNode = nodes.get(edgePair.getSecond());

        int sourceNodeId = sourceNode.getId();
        int targetNodeId = targetNode.getId();

        // Compute m_{ij}(x_j).
        Collection<LabelType> sourceLabels = fn.getPossibleLabels(sourceNodeId);
        Collection<LabelType> targetLabels = fn.getPossibleLabels(targetNodeId);
        int size = sourceLabels.size() * targetLabels.size();
        double[] values = new double[size];
        double max = -Double.MAX_VALUE;
        int ij = 0;
        for (LabelType targetLabel : targetLabels)
        {
            int sourceLabelIdx = 0;
            for (LabelType sourceLabel : sourceLabels)
            {
                values[ij] = -fn.getUnaryCost(sourceNodeId, sourceLabel);
                values[ij] += -fn.getPairwiseCost(edge, sourceLabel,
                    targetLabel);
                values[ij] += sourceNode.getLogMessageSum(sourceLabelIdx,
                    targetNodeId);
                max = Math.max(values[ij], max);
                ++sourceLabelIdx;
                ++ij;
            }
        }
        // Now correct so that all log values are less than or equal to 0
        ij = 0;
        Message targetMessage = targetNode.getMessageFromSource(sourceNodeId);
        for (int i = 0; i < targetLabels.size(); ++i)
        {
            double value = 0;
            for (int j = 0; j < sourceLabels.size(); ++j)
            {
                value += Math.exp(values[ij] - max);
                ++ij;
            }
            targetMessage.setTempValue(i, value);
        }
    }

}

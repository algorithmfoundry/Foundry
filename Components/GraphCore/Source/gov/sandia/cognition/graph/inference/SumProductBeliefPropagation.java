/*
 * File:                SumProductBeliefPropagation.java
 * Authors:             Tu-Thach Quach, Jeremy D. Wendt
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
 * This class implements the sum-product belief propagation algorithm for
 * arbitrary energy functions. It has been tested against graph-based energy
 * functions with widely varying node degrees and overall scales.
 *
 * It runs the algorithm in parallel on as many cores as the caller specifies.
 *
 * @author tong, jdwendt
 * @param <LabelType> The type that the nodes' labels can take on. Note that
 * these values will be considered enumerations internally.
 */
@PublicationReference(author
    = "Jonahtan S. Yedidia, William T. Freeman, and Yair Weiss",
    title = "Understanding Belief Propagation and its Generalizations",
    type = PublicationType.TechnicalReport,
    year = 2001, notes =
    {
        "Institution: Mitsubishi Electric Research Laboratories"
    })
public class SumProductBeliefPropagation<LabelType>
    extends SumProductInferencingAlgorithm<LabelType>
{

    /**
     * Creates a new BP solver with the specified settings.
     *
     * @param maxNumIterations The maximum number of iterations that will be run
     * @param eps The stopping epsilon that will be used
     * @param numThreads The number of threads that will be used
     */
    public SumProductBeliefPropagation(int maxNumIterations,
        double eps,
        int numThreads)
    {
        super(maxNumIterations, eps, numThreads);
    }

    /**
     * Creates a new BP solver with the default settings excepting max number of
     * iterations.
     *
     * @param maxNumIterations The maximum number of iterations that will be run
     */
    public SumProductBeliefPropagation(int maxNumIterations)
    {
        this(maxNumIterations, DEFAULT_EPS, DEFAULT_NUM_THREADS);
    }

    /**
     * Creates a new BP solver with the default settings.
     */
    public SumProductBeliefPropagation()
    {
        this(DEFAULT_MAX_ITERATIONS, DEFAULT_EPS, DEFAULT_NUM_THREADS);
    }

    /**
     * Private helper that computes the temporary message for the specified edge
     * for the current iteration going in the specified direction (a different
     * message for each direction)
     *
     * @param edge The edge to compute the temporary message for (you can't
     * replace the current message yet as the current message may be needed for
     * other edges).
     * @param reverse Specifies whether this should compute the message from i
     * to j or j to i
     */
    private void computeTemporaryMessage(int edge,
        boolean reverse)
    {
        Node<LabelType> sourceNode, targetNode;

        Pair<Integer, Integer> edgePair = fn.getEdge(edge);
        if (reverse)
        {
            sourceNode = nodes.get(edgePair.getSecond());
            targetNode = nodes.get(edgePair.getFirst());
        }
        else
        {
            sourceNode = nodes.get(edgePair.getFirst());
            targetNode = nodes.get(edgePair.getSecond());
        }

        int sourceNodeId = sourceNode.getId();
        int targetNodeId = targetNode.getId();
        Message targetMessage = targetNode.getMessageFromSource(sourceNodeId);

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
                if (!reverse)
                {
                    values[ij] += -fn.getPairwiseCost(edge, sourceLabel,
                        targetLabel);
                }
                else
                {
                    values[ij] += -fn.getPairwiseCost(edge, targetLabel,
                        sourceLabel);
                }
                values[ij] += sourceNode.getLogMessageSum(sourceLabelIdx,
                    targetNodeId);
                max = Math.max(values[ij], max);
                ++sourceLabelIdx;
                ++ij;
            }
        }
        // Now correct so that all log values are less than or equal to 0
        ij = 0;
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

    @Override
    protected void computeTemporaryMessage(int edge)
    {
        computeTemporaryMessage(edge, true);
        computeTemporaryMessage(edge, false);
    }

    @Override
    void initMessages(Pair<Integer, Integer> edgePair)
    {
        Node<LabelType> node = nodes.get(edgePair.getFirst());
        node.link(edgePair.getSecond(), true);

        node = nodes.get(edgePair.getSecond());
        node.link(edgePair.getFirst(), true);
    }

}

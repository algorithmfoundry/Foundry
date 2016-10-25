/*
 * File:                InferenceHelper.java
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

import gov.sandia.cognition.graph.DenseMemoryGraph;
import java.util.Collection;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Helper that tests results in a standardized manner used by various tests in
 * this package.
 *
 * @author jdwendt
 */
class InferenceHelper
{

    public static <NodeLabelType, LabelType> void testExactResults(
        DenseMemoryGraph<NodeLabelType> graph,
        NodeNameAwareEnergyFunction<LabelType, NodeLabelType> fn,
        EnergyFunctionSolver<LabelType> solver,
        double[] expectedResultsInOrder,
        boolean printResults)
    {
        int idx = 0;
        for (int i = 0; i < graph.getNumNodes(); ++i)
        {
            NodeLabelType node = graph.getNode(i);
            Map<LabelType, Double> b = fn.getBeliefs(node, solver);
            Collection<LabelType> lbls = fn.getPossibleLabels(i);
            if (printResults)
            {
                System.out.print("Node " + node + ": ");
                String sep = ": ";
                for (LabelType lbl : lbls)
                {
                    System.out.print(sep + b.get(lbl));
                    sep = ", ";
                }
                System.out.println();
            }
            for (LabelType lbl : lbls)
            {
                assertEquals(expectedResultsInOrder[idx], b.get(lbl), 1e-4);
                ++idx;
            }
        }
    }

    public static <NodeLabelType, LabelType> void testApproximateResults(
        DenseMemoryGraph<NodeLabelType> graph,
        NodeNameAwareEnergyFunction<LabelType, NodeLabelType> fn,
        EnergyFunctionSolver<LabelType> solver,
        double[] expectedResultsInOrder,
        double acceptableError,
        boolean printResults)
    {
        int idx = 0;
        for (int i = 0; i < graph.getNumNodes(); ++i)
        {
            NodeLabelType node = graph.getNode(i);
            Map<LabelType, Double> b = fn.getBeliefs(node, solver);
            Collection<LabelType> lbls = fn.getPossibleLabels(i);
            if (printResults)
            {
                System.out.print("Node " + node + ": ");
                String sep = ": ";
                for (LabelType lbl : lbls)
                {
                    System.out.print(sep + b.get(lbl));
                    sep = ", ";
                }
                System.out.println();
            }
            for (LabelType lbl : lbls)
            {
                assertTrue(expectedResultsInOrder[idx] + " - " + b.get(lbl),
                    Math.abs(expectedResultsInOrder[idx] - b.get(lbl))
                    < acceptableError);
                ++idx;
            }
        }
    }

}

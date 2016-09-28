/*
 * File:                GraphWrappingEnergyFunctionTest.java
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
import gov.sandia.cognition.graph.DirectedNodeEdgeGraph;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author jdwendt
 */
public class GraphWrappingEnergyFunctionTest
{

    @Test
    public void basicTest()
    {
        DenseMemoryGraph<Integer> graph = new DenseMemoryGraph<>(3, 2);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        GraphWrappingEnergyFunction.PotentialHandler<Integer, Integer> handler
            = new GraphWrappingEnergyFunction.PotentialHandler<Integer, Integer>()
        {

            private final double[][] pairwisePotentials0 =
            {
                {
                    1, 2
                },
                {
                    4, 1
                }
            };

            private final double[][] pairwisePotentials1 =
            {
                {
                    1, 3
                },
                {
                    3, 1
                }
            };

            @Override
            public double getPairwisePotential(
                DirectedNodeEdgeGraph<Integer> graph,
                int edgeId,
                Integer ilabel,
                Integer jlabel)
            {
                if (edgeId == 0)
                {
                    return pairwisePotentials0[ilabel][jlabel];
                }
                else if (edgeId == 1)
                {
                    return pairwisePotentials1[ilabel][jlabel];
                }
                throw new RuntimeException("Input edge id: " + edgeId
                    + " does not exist in the graph");
            }

            @Override
            public double getUnaryPotential(
                DirectedNodeEdgeGraph<Integer> graph,
                int i,
                Integer label,
                Integer assignedLabel)
            {
                return 1.0;
            }

            @Override
            public List<Integer> getPossibleLabels(
                DirectedNodeEdgeGraph<Integer> graph,
                int nodeId)
            {
                List<Integer> ret = new ArrayList<>();
                ret.add(0);
                ret.add(1);
                return ret;
            }

        };
        GraphWrappingEnergyFunction<Integer, Integer> fn
            = new GraphWrappingEnergyFunction<>(graph, handler);
        EnergyFunctionSolver<Integer> bp = new SumProductBeliefPropagation<>();
        bp.init(fn);
        assertTrue(bp.solve());
        InferenceHelper.testExactResults(graph, fn, bp, new double[]
        {
            0.375, 0.625, 0.625, 0.375, 0.4375, 0.5625
        },
            false);
    }

}

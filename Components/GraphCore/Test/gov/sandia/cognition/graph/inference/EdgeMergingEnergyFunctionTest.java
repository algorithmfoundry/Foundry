/*
 * File:                EdgeMergingEnergyFunctionTest.java
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
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Tests the edge merging energy function -- the energy function that makes it
 * so that BP sees at most one edge between any pair of nodes and handles
 * merging the potentials properly.
 *
 * @author jdwendt
 */
public class EdgeMergingEnergyFunctionTest
{

    private static class FlippedEdgePotentialHandler
        implements
        GraphWrappingEnergyFunction.PotentialHandler<Integer, Integer>
    {

        private boolean isFlipped0_0;

        private boolean isFlipped0_1;

        private final double[][] pairwisePotentials0_0 =
        {
            {
                1, 1
            },
            {
                4, 1
            }
        };

        private final double[][] pairwisePotentials0_1 =
        {
            {
                1, 2
            },
            {
                1, 1
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
            // NOTE: By looking only at the edge ID, this leads to complications
            // in the test below.  However, I can think of no other easy way of
            // handling the fact that the first two edges might be flipped 
            // between 0->1 or 1->0.
            if (edgeId == 0)
            {
                if (isFlipped0_0)
                {
                    return pairwisePotentials0_0[jlabel][ilabel];
                }
                else
                {
                    return pairwisePotentials0_0[ilabel][jlabel];
                }
            }
            else if (edgeId == 1)
            {
                if (isFlipped0_1)
                {
                    return pairwisePotentials0_1[jlabel][ilabel];
                }
                else
                {
                    return pairwisePotentials0_1[ilabel][jlabel];
                }
            }
            else if (edgeId == 2)
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

    @Test
    public void basicTest()
    {
        double[] correct = new double[]
        {
            0.375, 0.625, 0.625, 0.375, 0.4375, 0.5625
        };
        DenseMemoryGraph<Integer> graph = new DenseMemoryGraph<>(3, 3);
        // Adding nodes is necessary for this test so that the edges always show up in the correct order
        graph.addNode(0);
        graph.addNode(1);
        graph.addNode(2);
        graph.addEdge(0, 1);
        graph.addEdge(1, 0);
        graph.addEdge(1, 2);
        FlippedEdgePotentialHandler handler = new FlippedEdgePotentialHandler();
        handler.isFlipped0_0 = false;
        handler.isFlipped0_1 = true;
        GraphWrappingEnergyFunction<Integer, Integer> fn
            = new GraphWrappingEnergyFunction<>(graph, handler);
        EnergyFunctionSolver<Integer> bp = new SumProductBeliefPropagation<>();
        bp.init(new EdgeMergingEnergyFunction<>(fn));
        assertTrue(bp.solve());
        InferenceHelper.testExactResults(graph, fn, bp, correct, false);

        graph = new DenseMemoryGraph<>(3, 3);
        // Adding nodes is necessary for this test so that the edges always show up in the correct order
        graph.addNode(0);
        graph.addNode(1);
        graph.addNode(2);
        graph.addEdge(1, 0);
        graph.addEdge(1, 0);
        graph.addEdge(1, 2);
        handler.isFlipped0_0 = true;
        handler.isFlipped0_1 = true;
        fn = new GraphWrappingEnergyFunction<>(graph, handler);
        bp = new SumProductBeliefPropagation<>();
        bp.init(new EdgeMergingEnergyFunction<>(fn));
        assertTrue(bp.solve());
        InferenceHelper.testExactResults(graph, fn, bp, correct, false);

        graph = new DenseMemoryGraph<>(3, 3);
        // Adding nodes is necessary for this test so that the edges always show up in the correct order
        graph.addNode(0);
        graph.addNode(1);
        graph.addNode(2);
        graph.addEdge(1, 0);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        // NOTTE: This seems out of order after seeing the order the edges are added above.  The problem
        // is that the edges are sorted internal to the object, so the edge 0->1 becomes the first edge,
        // and 1->0 becomes the second.
        handler.isFlipped0_0 = false;
        handler.isFlipped0_1 = true;
        fn = new GraphWrappingEnergyFunction<>(graph, handler);
        bp = new SumProductBeliefPropagation<>();
        bp.init(new EdgeMergingEnergyFunction<>(fn));
        assertTrue(bp.solve());
        InferenceHelper.testExactResults(graph, fn, bp, correct, false);

        graph = new DenseMemoryGraph<>(3, 3);
        // Adding nodes is necessary for this test so that the edges always show up in the correct order
        graph.addNode(0);
        graph.addNode(1);
        graph.addNode(2);
        graph.addEdge(0, 1);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        handler.isFlipped0_0 = false;
        handler.isFlipped0_1 = false;
        fn = new GraphWrappingEnergyFunction<>(graph, handler);
        bp = new SumProductBeliefPropagation<>();
        bp.init(new EdgeMergingEnergyFunction<>(fn));
        assertTrue(bp.solve());
        InferenceHelper.testExactResults(graph, fn, bp, correct, false);

    }

}

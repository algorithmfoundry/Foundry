/*
 * File:                SumProductBeliefPropagationTest.java
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
import gov.sandia.cognition.util.DefaultKeyValuePair;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test case for sum-product belief propagation.
 *
 * @author tong
 *
 */
public class SumProductBeliefPropagationTest
{

    @Test
    public void testSolve()
    {
        // We'll use our simple example consisting of 3 nodes.
        EnergyFunctionSolver<Integer> bp = new SumProductBeliefPropagation<>();
        bp.init(new ThreeNodeEnergyFunction());
        assertTrue(bp.solve());
//        for (int node = 0; node < 3; node++)
//        {
//            System.out.print(bp.getBelief(node, 0));
//            System.out.print('\t');
//            System.out.print(bp.getBelief(node, 1));
//            System.out.println();
//        }
        double delta = 0.0001;
        assertEquals(0.375, bp.getBelief(0, 0), delta);
        assertEquals(0.625, bp.getBelief(0, 1), delta);
        assertEquals(0.625, bp.getBelief(1, 0), delta);
        assertEquals(0.375, bp.getBelief(1, 1), delta);
        assertEquals(0.4375, bp.getBelief(2, 0), delta);
        assertEquals(0.5625, bp.getBelief(2, 1), delta);
    }

    @Test
    public void testSolveDiffEdgeOrder()
    {
        // We'll use our simple example consisting of 3 nodes.
        EnergyFunctionSolver<Integer> bp
            = new SumProductBeliefPropagation<>();
        ThreeNodeEnergyFunction fn = new ThreeNodeEnergyFunction();
        fn.flipOrder = true;
        bp.init(fn);
        assertTrue(bp.solve());
//        for (int node = 0; node < 3; node++)
//        {
//            System.out.print(bp.getBelief(node, 0));
//            System.out.print('\t');
//            System.out.print(bp.getBelief(node, 1));
//            System.out.println();
//        }
        double delta = 0.0001;
        assertEquals(0.375, bp.getBelief(0, 0), delta);
        assertEquals(0.625, bp.getBelief(0, 1), delta);
        assertEquals(0.625, bp.getBelief(1, 0), delta);
        assertEquals(0.375, bp.getBelief(1, 1), delta);
        assertEquals(0.4375, bp.getBelief(2, 0), delta);
        assertEquals(0.5625, bp.getBelief(2, 1), delta);
    }

    /**
     * Three nodes, 2 edges. 0-1-2.
     *
     * @author tong
     *
     */
    class ThreeNodeEnergyFunction
        implements EnergyFunction<Integer>
    {

        private double[][] pairwisePotentials0 =
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

        private boolean flipOrder = false;

        @Override
        public int numNodes()
        {
            return 3;
        }

        @Override
        public int numEdges()
        {
            return 2;
        }

        @Override
        public Pair<Integer, Integer> getEdge(int edge)
        {
            if (edge == 0)
            {
                if (flipOrder)
                {
                    return new DefaultKeyValuePair<>(1, 0);
                }
                else
                {
                    return new DefaultKeyValuePair<>(0, 1);
                }
            }
            else if (edge == 1)
            {
                return new DefaultKeyValuePair<>(1, 2);
            }
            else
            {
                throw new IllegalArgumentException("Edge " + edge
                    + " is not in the graph.");
            }
        }

        @Override
        public List<Integer> getPossibleLabels(int node)
        {
            ArrayList<Integer> labels = new ArrayList<>();
            labels.add(0);
            labels.add(1);
            return labels;
        }

        @Override
        public double getUnaryPotential(int node,
            Integer label)
        {
            return 1;
        }

        @Override
        public double getPairwisePotential(int edge,
            Integer ilabel,
            Integer jlabel)
        {
            if (flipOrder)
            {
                pairwisePotentials0 = new double[][]
                {
                    {
                        1, 4
                    },
                    {
                        2, 1
                    }
                };
            }
            if (edge == 0)
            {
                return pairwisePotentials0[ilabel][jlabel];
            }
            else if (edge == 1)
            {
                return pairwisePotentials1[ilabel][jlabel];
            }
            else
            {
                throw new IllegalArgumentException("Edge " + edge
                    + " is not in the graph.");
            }
        }

        @Override
        public double getUnaryCost(int i,
            Integer label)
        {
            return -Math.log(getUnaryPotential(i, label));
        }

        @Override
        public double getPairwiseCost(int edgeId,
            Integer ilabel,
            Integer jlabel)
        {
            return -Math.log(getPairwisePotential(edgeId, ilabel, jlabel));
        }

    }

    @Test
    public void exactTest()
    {
        DenseMemoryGraph<Integer> graph = new DenseMemoryGraph<>(3, 2);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        BasicHomogeneousHandler<Integer> handler = new BasicHomogeneousHandler<>();
        NodeNameAwareEnergyFunction<Integer, Integer> fn
            = new GraphWrappingEnergyFunction<>(graph, handler);
        fn.setLabel(1, 0);
        EnergyFunctionSolver<Integer> bp = new SumProductBeliefPropagation<>(
            100, 1e-6, 1);
        bp.init(fn);

        assertTrue(bp.solve());
        InferenceHelper.testExactResults(graph, fn, bp, new double[]
        {
            1.0, 0.0, 0.99, 0.01, 0.9802, 0.0198
        }, false);

        handler.setSpecialUnaryPotential(3, graph, 0.8);
        fn.setLabel(3, 1);

        bp.init(fn);

        assertTrue(bp.solve());
        InferenceHelper.testExactResults(graph, fn, bp, new double[]
        {
            1.0, 0.0, 0.9625, 0.0375, 0.9252, 0.0748
        }, false);
    }

}

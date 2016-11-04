/*
 * File:                SumProductPairwiseBayesNetTest.java
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
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the SumProductPairwiseBayesNet class.
 *
 * @author jdwendt
 */
public class SumProductDirectedPropagationTest
{

    @Test
    public void exactTest()
    {
        DenseMemoryGraph<Integer> graph = new DenseMemoryGraph<>(3, 2);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        BasicHomogeneousHandler<Integer> handler
            = new BasicHomogeneousHandler<>();
        NodeNameAwareEnergyFunction<Integer, Integer> fn
            = new GraphWrappingEnergyFunction<>(graph, handler);
        fn.setLabel(1, 0);
        EnergyFunctionSolver<Integer> bayes = new SumProductDirectedPropagation<>(
            100, 1e-6, 1);
        bayes.init(fn);

        assertTrue(bayes.solve());
        InferenceHelper.testExactResults(graph, fn, bayes, new double[]
        {
            1.0, 0.0, 0.99, 0.01, 0.9802, 0.0198
        }, false);

        handler.setSpecialUnaryPotential(3, graph, 0.8);
        fn.setLabel(3, 1);

        bayes.init(fn);

        assertTrue(bayes.solve());
        InferenceHelper.testExactResults(graph, fn, bayes, new double[]
        {
            1.0, 0.0, 0.99, 0.01, 0.9252, 0.0748
        }, false);
    }

    @Test
    public void basicTest()
    {
        DenseMemoryGraph<Integer> graph = new DenseMemoryGraph<>(6, 5);
        graph.addEdge(0, 1);
        graph.addEdge(1, 2);
        graph.addEdge(2, 3);
        graph.addEdge(2, 4);
        graph.addEdge(5, 4);
        NodeNameAwareEnergyFunction<Integer, Integer> fn
            = new GraphWrappingEnergyFunction<>(graph,
                new BasicHomogeneousHandler<Integer>());

        fn.setLabel(1, 0);
        EnergyFunctionSolver<Integer> bayes = new SumProductDirectedPropagation<>(
            100, 1e-6, 1);

        bayes.init(fn);

        assertTrue(bayes.solve());
        InferenceHelper.testApproximateResults(graph, fn, bayes,
            new double[]
            {
                0.5, 0.5, 0.99, 0.01, 0.98, 0.02, 0.98, 0.02, 0.98, 0.02, 0.5,
                0.5
            }, 0.01, false);
        Map<Integer, Double> beliefs1 = fn.getBeliefs(1, bayes);
        Map<Integer, Double> beliefs2 = fn.getBeliefs(2, bayes);
        // 2's belief should be less than 1's because it's learning from 1
        assertTrue(beliefs2.get(0) < beliefs1.get(0));
        Map<Integer, Double> beliefs3 = fn.getBeliefs(3, bayes);
        // 3's belief should be less than 2's because it's learning from 2
        assertTrue(beliefs3.get(0) < beliefs2.get(0));
    }

}

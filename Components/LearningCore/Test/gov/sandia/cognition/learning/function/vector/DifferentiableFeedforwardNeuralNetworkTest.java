/*
 * File:                DifferentiableFeedforwardNeuralNetworkTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 1, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendableTestHarness;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * Unit tests for DifferentiableFeedforwardNeuralNetworkTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class DifferentiableFeedforwardNeuralNetworkTest
    extends FeedforwardNeuralNetworkTest
{

    /**
     * Test
     * @param testName test
     */
    public DifferentiableFeedforwardNeuralNetworkTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of computeParameterGradient method, of class gov.sandia.isrc.learning.util.function.DifferentiableFeedforwardNeuralNetwork.
     */
    public void testComputeParameterGradient()
    {
        System.out.println("computeParameterGradient");
        double A = 0.1;

        DifferentiableFeedforwardNeuralNetwork ann1 =
            new DifferentiableFeedforwardNeuralNetwork(1, 2, 3, new LinearVectorFunction(random.nextDouble()), random);
        int N1 = 1;
        Vector p = ann1.convertToVector();
        Vector p2 = VectorFactory.getDefault().createUniformRandom(p.getDimensionality(), -A, A, random);
        ann1.convertFromVector(p2);
        Vector x1 = VectorFactory.getDefault().createUniformRandom(N1, -A, A, random);
        GradientDescendableTestHarness.testGradient(ann1, x1);

        for (int i = 0; i < 10; i++)
        {
            DifferentiableFeedforwardNeuralNetwork instance = this.createRandom();
            int N = instance.getLayers().get(0).getMatrixMultiply().getInternalMatrix().getNumColumns();
            Vector input = VectorFactory.getDefault().createUniformRandom(N, -A, A, random);
            GradientDescendableTestHarness.testGradient(instance, input);
        }

    }

    /**
     * Test of getLayers method, of class gov.sandia.isrc.learning.util.function.DifferentiableFeedforwardNeuralNetwork.
     */
    @Override
    public void testGetLayers()
    {
        System.out.println("getLayers");

        DifferentiableFeedforwardNeuralNetwork instance = this.createRandom();
        assertNotNull(instance.getLayers());
        for (int i = 0; i < instance.getLayers().size(); i++)
        {
            DifferentiableSquashedMatrixMultiplyVectorFunction layer =
                instance.getLayers().get(i);
        }
    }
    

}

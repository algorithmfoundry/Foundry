/*
 * File:                RemoveOldestKernelPerceptronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 28, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class RemoveOldestKernelPerceptron.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class RemoveOldestKernelPerceptronTest
    extends OnlineKernelBinaryLearnerTestHarness<DefaultKernelBinaryCategorizer<Vector>>
{
    /**
     * Creates a new test.
     */
    public RemoveOldestKernelPerceptronTest()
    {
    }

    @Override
    protected RemoveOldestKernelPerceptron<Vector> createInstance(
        final Kernel<? super Vector> kernel)
    {
        return new RemoveOldestKernelPerceptron<Vector>(
            kernel, 100);
    }

    /**
     * Test of constructors of class RemoveOldestKernelPerceptron.
     */
    @Test
    public void testConstructors()
    {
        Kernel<? super Vector> kernel = null;
        int budget = OnlineKernelRandomizedBudgetPerceptron.DEFAULT_BUDGET;
        RemoveOldestKernelPerceptron<Vector> instance =
            new RemoveOldestKernelPerceptron<Vector>();
        assertSame(kernel, instance.getKernel());
        assertEquals(budget, instance.getBudget());

        kernel = new PolynomialKernel(1 + random.nextInt(10));
        budget = 1 + random.nextInt(10000);
        instance = new RemoveOldestKernelPerceptron<Vector>(
            kernel, budget);
        assertSame(kernel, instance.getKernel());
        assertEquals(budget, instance.getBudget());
    }

    /**
     * Test of update method, of class RemoveOldestKernelPerceptron.
     */
    @Test
    public void testUpdate()
    {
        RemoveOldestKernelPerceptron<Vector> instance = new RemoveOldestKernelPerceptron<Vector>(
            new LinearKernel(), 2);

        DefaultKernelBinaryCategorizer<Vector> result = instance.createInitialLearnedObject();
        assertEquals(0, result.getExamples().size());
        assertEquals(0.0, result.getBias(), 0.0);

        instance.update(result, DefaultInputOutputPair.create(new Vector2(2.0, 3.0), true));
        assertEquals(1, result.getExamples().size());
        assertEquals(new Vector2(2.0, 3.0), toWeights(result));
        assertEquals(1.0, result.getBias(), 0.0);

        instance.update(result, DefaultInputOutputPair.create(new Vector2(4.0, 4.0), true));
        assertEquals(1, result.getExamples().size());
        assertEquals(new Vector2(2.0, 3.0), toWeights(result));
        assertEquals(1.0, result.getBias(), 0.0);

        instance.update(result, DefaultInputOutputPair.create(new Vector2(1.0, 1.0), false));
        assertEquals(2, result.getExamples().size());
        assertEquals(new Vector2(1.0, 2.0), toWeights(result));
        assertEquals(0.0, result.getBias(), 0.0);

        instance.update(result, DefaultInputOutputPair.create(new Vector2(1.0, 1.0), false));
        assertEquals(2, result.getExamples().size());
        assertEquals(new Vector2(-2.0, -2.0), toWeights(result));
        assertEquals(-2.0, result.getBias(), 0.0);

        instance.update(result, DefaultInputOutputPair.create(new Vector2(2.0, 3.0), true));
        assertEquals(2, result.getExamples().size());
        assertEquals(new Vector2(1.0, 2.0), toWeights(result));
        assertEquals(0.0, result.getBias(), 0.0);
    }
    
}
/*
 * File:                AbstractOnlineBudgetedKernelBinaryCategorizerLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright May 04, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class AbstractOnlineBudgetedKernelBinaryCategorizerLearner.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class AbstractOnlineBudgetedKernelBinaryCategorizerLearnerTest
{

    /** Random number generator. */
    protected Random random = new Random(211);

    /**
     * Creates a new test.
     */
    public AbstractOnlineBudgetedKernelBinaryCategorizerLearnerTest()
    {
    }

    /**
     * Test of constructors of class AbstractOnlineBudgetedKernelBinaryCategorizerLearner.
     */
    @Test
    public void testConstructors()
    {
        int budget = AbstractOnlineBudgetedKernelBinaryCategorizerLearner.DEFAULT_BUDGET;
        Kernel<? super Vector> kernel = null;
        AbstractOnlineBudgetedKernelBinaryCategorizerLearner<Vector> instance =
            new DummyOnlineBudgetedKernelBinaryCategorizerLearner<Vector>();
        assertEquals(budget, instance.getBudget());
        assertEquals(kernel, instance.getKernel());

        budget += 2;
        kernel = new PolynomialKernel(7, 8);
        instance = new DummyOnlineBudgetedKernelBinaryCategorizerLearner<Vector>(
            kernel, budget);
        assertEquals(budget, instance.getBudget());
        assertEquals(kernel, instance.getKernel());
    }

    /**
     * Test of getBudget method, of class AbstractOnlineBudgetedKernelBinaryCategorizerLearner.
     */
    @Test
    public void testGetBudget()
    {
        this.testSetBudget();
    }

    /**
     * Test of setBudget method, of class AbstractOnlineBudgetedKernelBinaryCategorizerLearner.
     */
    @Test
    public void testSetBudget()
    {
        int budget = RemoveOldestKernelPerceptron.DEFAULT_BUDGET;
        AbstractOnlineBudgetedKernelBinaryCategorizerLearner<Vector> instance =
            new DummyOnlineBudgetedKernelBinaryCategorizerLearner<Vector>();
        assertEquals(budget, instance.getBudget());

        int[] goodValues = {1, 2, 3, 7, 10, 20, 30, 1 + random.nextInt(1000) };
        for (int goodValue : goodValues)
        {
            budget = goodValue;
            instance.setBudget(budget);
            assertEquals(budget, instance.getBudget());
        }

        int[] badValues = {0, -1, -2, -10, -random.nextInt(1000) };
        for (int badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setBudget(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(budget, instance.getBudget());
        }
    }

    public class DummyOnlineBudgetedKernelBinaryCategorizerLearner<InputType>
        extends AbstractOnlineBudgetedKernelBinaryCategorizerLearner<InputType>
    {

        public DummyOnlineBudgetedKernelBinaryCategorizerLearner()
        {
            super();
        }

        public DummyOnlineBudgetedKernelBinaryCategorizerLearner(
            final Kernel<? super InputType> kernel,
            final int budget)
        {
            super(kernel, budget);
        }

        @Override
        public void update(
            final DefaultKernelBinaryCategorizer<InputType> target,
            final InputType input,
            final boolean output)
        {
            throw new UnsupportedOperationException("Dummy class.");
        }
    }

}
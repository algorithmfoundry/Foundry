/*
 * File:                OnlineKernelRandomizedBudgetPerceptronTest.java
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
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class OnlineKernelRandomizedBudgetPerceptron.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class OnlineKernelRandomizedBudgetPerceptronTest
    extends OnlineKernelBinaryLearnerTestHarness<DefaultKernelBinaryCategorizer<Vector>>
{
    
    /** Random number generator. */
    protected Random random = new Random(211);

    /**
     * Creates a new test.
     */
    public OnlineKernelRandomizedBudgetPerceptronTest()
    {
    }

    @Override
    protected OnlineKernelRandomizedBudgetPerceptron<Vector> createInstance(
        final Kernel<? super Vector> kernel)
    {
        return new OnlineKernelRandomizedBudgetPerceptron<Vector>(
            kernel, 100, random);
    }

    /**
     * Test of constructors of class OnlineKernelRandomizedBudgetPerceptron.
     */
    @Test
    public void testConstructors()
    {
        Kernel<? super Vector> kernel = null;
        int budget = OnlineKernelRandomizedBudgetPerceptron.DEFAULT_BUDGET;
        OnlineKernelRandomizedBudgetPerceptron<Vector> instance =
            new OnlineKernelRandomizedBudgetPerceptron<Vector>();
        assertSame(kernel, instance.getKernel());
        assertEquals(budget, instance.getBudget());
        assertNotNull(instance.getRandom());

        kernel = new PolynomialKernel(1 + random.nextInt(10));
        budget = 1 + random.nextInt(10000);
        Random random = new Random();
        instance = new OnlineKernelRandomizedBudgetPerceptron<Vector>(
            kernel, budget, random);
        assertSame(kernel, instance.getKernel());
        assertEquals(budget, instance.getBudget());
        assertSame(random, instance.getRandom());
    }

    /**
     * Test of update method, of class OnlineKernelRandomizedBudgetPerceptron.
     */
    @Test
    public void testUpdate()
    {
        OnlineKernelRandomizedBudgetPerceptron<Vector> instance =
            new OnlineKernelRandomizedBudgetPerceptron<Vector>(
                new LinearKernel(), 3, random);
        DefaultKernelBinaryCategorizer<Vector> learned = instance.createInitialLearnedObject();;

        Vector input = new Vector2(2.0, 3.0);
        Boolean output = true;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(1, learned.getExamples().size());

        input = new Vector2(4.0, 4.0);
        output = true;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(1, learned.getExamples().size());

        input = new Vector2(1.0, 1.0);
        output = false;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(2, learned.getExamples().size());

        input = new Vector2(1.0, 1.0);
        output = false;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(3, learned.getExamples().size());

        input = new Vector2(2.0, 3.0);
        output = true;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(3, learned.getExamples().size());

        input = new Vector2(2.0, 3.0);
        output = false;
        instance.update(learned, DefaultInputOutputPair.create(input, output));
        assertEquals(3, learned.getExamples().size());
    }

    /**
     * Test of getRandom method, of class OnlineKernelRandomizedBudgetPerceptron.
     */
    @Test
    public void testGetRandom()
    {
        this.testSetRandom();
    }

    /**
     * Test of setRandom method, of class OnlineKernelRandomizedBudgetPerceptron.
     */
    @Test
    public void testSetRandom()
    {
        Random random = null;
        OnlineKernelRandomizedBudgetPerceptron<Vector> instance =
            new OnlineKernelRandomizedBudgetPerceptron<Vector>();
        assertNotNull(instance.getRandom());

        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = null;
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());
    }

}
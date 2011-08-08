/*
 * File:                ForgetronGreedyTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright May 11, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.math.matrix.Vector;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class Forgetron.Greedy.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class ForgetronGreedyTest
    extends ForgetronTest
{
    /**
     * Creates a new test.
     */
    public ForgetronGreedyTest()
    {
    }

    @Override
    protected Forgetron.Greedy<Vector> createInstance(
        final Kernel<? super Vector> kernel)
    {
        return new Forgetron.Greedy<Vector>(kernel, 100);
    }

    /**
     * Test of constructors of class Forgetron.Greedy.
     */
    @Test
    public void testConstructors()
    {
        Kernel<? super Vector> kernel = null;
        int budget = Forgetron.DEFAULT_BUDGET;
        Forgetron.Greedy<Vector> instance = new Forgetron.Greedy<Vector>();
        assertSame(kernel, instance.getKernel());
        assertEquals(budget, instance.getBudget());

        kernel = new PolynomialKernel(
            1 + this.random.nextInt(10), this.random.nextDouble());
        budget = 1 + random.nextInt(100);
        instance = new Forgetron.Greedy<Vector>(kernel, budget);
        assertSame(kernel, instance.getKernel());
        assertEquals(budget, instance.getBudget());
    }

}
/*
 * File:                ForgetronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright May 10, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron.kernel;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.learning.function.categorization.DefaultKernelBinaryCategorizer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class Forgetron.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class ForgetronTest
    extends OnlineKernelBinaryLearnerTestHarness<DefaultKernelBinaryCategorizer<Vector>>
{
    /**
     * Creates a new test.
     */
    public ForgetronTest()
    {
    }

    @Override
    protected Forgetron<Vector> createInstance(
        final Kernel<? super Vector> kernel)
    {
        return new Forgetron<Vector>(kernel, 100);
    }

    /**
     * Test of constructors of class Forgetron.
     */
    @Test
    public void testConstructors()
    {
        Kernel<? super Vector> kernel = null;
        int budget = Forgetron.DEFAULT_BUDGET;
        Forgetron<Vector> instance = new Forgetron<Vector>();
        assertSame(kernel, instance.getKernel());
        assertEquals(budget, instance.getBudget());

        kernel = new PolynomialKernel(
            1 + this.random.nextInt(10), this.random.nextDouble());
        budget = 1 + random.nextInt(100);
        instance = new Forgetron<Vector>(kernel, budget);
        assertSame(kernel, instance.getKernel());
        assertEquals(budget, instance.getBudget());
    }
    
    /**
     * Test of update method, of class Forgetron.
     */
    @Test
    public void testUpdate()
    {
        Forgetron<Vector> instance = this.createInstance(new LinearKernel());
        instance.setBudget(3);
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

}
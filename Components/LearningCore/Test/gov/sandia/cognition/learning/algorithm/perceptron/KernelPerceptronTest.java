/*
 * File:                KernelPerceptronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 13, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.kernel.LinearKernel;
import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     KernelPerceptron
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class KernelPerceptronTest
    extends TestCase
{

    public KernelPerceptronTest(
        String testName)
    {
        super(testName);
    }

    public void testConstants()
    {
        assertEquals(Perceptron.DEFAULT_MAX_ITERATIONS, KernelPerceptron.DEFAULT_MAX_ITERATIONS);
        assertEquals(Perceptron.DEFAULT_MARGIN_POSITIVE, KernelPerceptron.DEFAULT_MARGIN_POSITIVE);
        assertEquals(Perceptron.DEFAULT_MARGIN_NEGATIVE, KernelPerceptron.DEFAULT_MARGIN_NEGATIVE);
    }

    public void testConstructors()
    {
        KernelPerceptron<Vector> instance = new KernelPerceptron<Vector>();
        assertNull(instance.getKernel());
        assertEquals(Perceptron.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations());
        assertEquals(Perceptron.DEFAULT_MARGIN_POSITIVE, instance.getMarginPositive());
        assertEquals(Perceptron.DEFAULT_MARGIN_NEGATIVE, instance.getMarginNegative());

        PolynomialKernel kernel = new PolynomialKernel(4, 7.0);
        instance = new KernelPerceptron<Vector>(kernel);
        assertSame(kernel, instance.getKernel());
        assertEquals(Perceptron.DEFAULT_MAX_ITERATIONS, instance.getMaxIterations());
        assertEquals(Perceptron.DEFAULT_MARGIN_POSITIVE, instance.getMarginPositive());
        assertEquals(Perceptron.DEFAULT_MARGIN_NEGATIVE, instance.getMarginNegative());

        int maxIterations = Perceptron.DEFAULT_MAX_ITERATIONS + 10;
        instance = new KernelPerceptron<Vector>(kernel, maxIterations);
        assertSame(kernel, instance.getKernel());
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(Perceptron.DEFAULT_MARGIN_POSITIVE, instance.getMarginPositive());
        assertEquals(Perceptron.DEFAULT_MARGIN_NEGATIVE, instance.getMarginNegative());

        double marginPositive = Math.random();
        double marginNegative = Math.random();
        instance = new KernelPerceptron<Vector>(kernel, maxIterations, marginPositive, marginNegative);
        assertSame(kernel, instance.getKernel());
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(marginPositive, instance.getMarginPositive());
        assertEquals(marginNegative, instance.getMarginNegative());
    }

    public void testLearn()
    {
        KernelPerceptron<Vector> instance = new KernelPerceptron<Vector>(
            LinearKernel.getInstance());

        Vector2[] positives = new Vector2[] {
            new Vector2(1.00, 1.00),
            new Vector2(1.00, 3.00),
            new Vector2(0.25, 4.00),
            new Vector2(2.00, 1.00),
            new Vector2(5.00, -3.00)
        };

        Vector2[] negatives = new Vector2[] {
            new Vector2(2.00, 3.00),
            new Vector2(2.00, 4.00),
            new Vector2(3.00, 2.00),
            new Vector2(4.25, 3.75),
            new Vector2(4.00, 7.00),
            new Vector2(7.00, 4.00)
        };

        ArrayList<InputOutputPair<Vector2, Boolean>> examples =
            new ArrayList<InputOutputPair<Vector2, Boolean>>();
        for (Vector2 example : positives)
        {
            examples.add(new DefaultInputOutputPair<Vector2, Boolean>(example, true));
        }

        for (Vector2 example : negatives)
        {
            examples.add(new DefaultInputOutputPair<Vector2, Boolean>(example, false));
        }

        Evaluator<? super Vector,Boolean> result = instance.learn(examples);
        assertEquals(0, instance.getErrorCount());
        assertEquals(result, instance.getResult());
        
        for (Vector2 example : positives)
        {
            assertTrue(result.evaluate(example));
        }

        for (Vector2 example : negatives)
        {
            assertFalse(result.evaluate(example));
        }

        instance.setMargin(10.0);
        instance.setMaxIterations(1000);
        result = instance.learn(examples);
        assertEquals(0, instance.getErrorCount());
        assertEquals(result, instance.getResult());


        for (Vector2 example : positives)
        {
            assertTrue(result.evaluate(example));
        }

        for (Vector2 example : negatives)
        {
            assertFalse(result.evaluate(example));
        }

        instance.setMaxIterations(instance.getIteration() / 2);
        result = instance.learn(examples);
        assertTrue(instance.getErrorCount() > 0);

        examples = new ArrayList<InputOutputPair<Vector2, Boolean>>();
        result = instance.learn(examples);
        assertNull(result);

        result = instance.learn(null);
        assertNull(result);
    }

    /**
     * Test of getKernel method, of class gov.sandia.cognition.learning.perceptron.KernelPerceptron.
     */
    public void testGetKernel()
    {
        this.testSetKernel();
    }

    /**
     * Test of setKernel method, of class gov.sandia.cognition.learning.perceptron.KernelPerceptron.
     */
    public void testSetKernel()
    {
        KernelPerceptron<Vector> instance = new KernelPerceptron<Vector>();
        assertNull(instance.getKernel());

        Kernel<? super Vector> kernel = LinearKernel.getInstance();
        instance.setKernel(kernel);
        assertSame(kernel, instance.getKernel());

        kernel = new PolynomialKernel(4, 7.0);
        instance.setKernel(kernel);
        assertSame(kernel, instance.getKernel());

        instance.setKernel(null);
        assertNull(instance.getKernel());
    }

    /**
     * Test of setMargin method, of class gov.sandia.cognition.learning.perceptron.KernelPerceptron.
     */
    public void testSetMargin()
    {
        KernelPerceptron<Vector> instance = new KernelPerceptron<Vector>();
        double margin = Math.random();
        instance.setMargin(margin);
        assertEquals(margin, instance.getMarginPositive());
        assertEquals(margin, instance.getMarginNegative());
    }

    /**
     * Test of getMarginPositive method, of class gov.sandia.cognition.learning.perceptron.KernelPerceptron.
     */
    public void testGetMarginPositive()
    {
        this.testSetMarginPositive();
    }

    /**
     * Test of setMarginPositive method, of class gov.sandia.cognition.learning.perceptron.KernelPerceptron.
     */
    public void testSetMarginPositive()
    {
        KernelPerceptron<Vector> instance = new KernelPerceptron<Vector>();
        assertEquals(KernelPerceptron.DEFAULT_MARGIN_POSITIVE, instance.getMarginPositive());

        double margin = Math.random();
        instance.setMarginPositive(margin);
        assertEquals(margin, instance.getMarginPositive());

        margin = 0.0;
        instance.setMarginPositive(margin);
        assertEquals(margin, instance.getMarginPositive());

        margin = -1.0;
        instance.setMarginPositive(margin);
        assertEquals(margin, instance.getMarginPositive());
    }

    /**
     * Test of getMarginNegative method, of class gov.sandia.cognition.learning.perceptron.KernelPerceptron.
     */
    public void testGetMarginNegative()
    {
        this.testSetMarginNegative();
    }

    /**
     * Test of setMarginNegative method, of class gov.sandia.cognition.learning.perceptron.KernelPerceptron.
     */
    public void testSetMarginNegative()
    {
        KernelPerceptron<Vector> instance = new KernelPerceptron<Vector>();
        assertEquals(KernelPerceptron.DEFAULT_MARGIN_NEGATIVE, instance.getMarginNegative());

        double margin = Math.random();
        instance.setMarginNegative(margin);
        assertEquals(margin, instance.getMarginNegative());

        margin = 0.0;
        instance.setMarginNegative(margin);
        assertEquals(margin, instance.getMarginNegative());

        margin = -1.0;
        instance.setMarginNegative(margin);
        assertEquals(margin, instance.getMarginNegative());
    }

    /**
     * Test of getResult method, of class gov.sandia.cognition.learning.perceptron.KernelPerceptron.
     */
    public void testGetResult()
    {
    // Tested by learn.
    }

    /**
     * Test of getErrorCount method, of class gov.sandia.cognition.learning.perceptron.KernelPerceptron.
     */
    public void testGetErrorCount()
    {
    // Tested by learn.
    }

}

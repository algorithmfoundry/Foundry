/*
 * File:                ScalarFunctionKernelTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.kernel;

import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import java.util.Random;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 * ScalarFunctionKernel
 * @author Justin Basilico
 * @since  2.0
 */
public class ScalarFunctionKernelTest
    extends TestCase
{

    public final Random RANDOM = new Random(1);

    /**
     * 
     * @param testName
     */
    public ScalarFunctionKernelTest(
        String testName)
    {
        super(testName);
    }

    /**
     * 
     */
    public void testConstructors()
    {
        ScalarFunctionKernel<Double> instance =
            new ScalarFunctionKernel<Double>();
        assertNull(instance.getFunction());

        PolynomialFunction function = new PolynomialFunction(RANDOM.nextDouble());
        instance = new ScalarFunctionKernel<Double>(function);
        assertSame(function, instance.getFunction());
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.learning.kernel.ScalarFunctionKernel.
     */
    public void testClone()
    {
        PolynomialFunction function = new PolynomialFunction(RANDOM.nextDouble());
        ScalarFunctionKernel<Double> instance =
            new ScalarFunctionKernel<Double>(function);
        assertSame(function, instance.getFunction());

        ScalarFunctionKernel<Double> clone = instance.clone();
        assertNotNull( clone.getFunction() );
        assertNotSame(function, clone.getFunction());
    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.kernel.ScalarFunctionKernel.
     */
    public void testEvaluate()
    {
        PolynomialFunction function = new PolynomialFunction(RANDOM.nextDouble());
        ScalarFunctionKernel<Double> instance =
            new ScalarFunctionKernel<Double>(function);

        int count = 10;
        for (int i = 0; i < count; i++)
        {
            double x = RANDOM.nextDouble();
            double y = RANDOM.nextDouble();

            double expected = function.evaluate(x) * function.evaluate(y);
            assertEquals(expected, instance.evaluate(x, y));
            assertEquals(expected, instance.evaluate(y, x));
        }
    }

    /**
     * Test of getFunction method, of class gov.sandia.cognition.learning.kernel.ScalarFunctionKernel.
     */
    public void testGetFunction()
    {
        this.testSetFunction();
    }

    /**
     * Test of setFunction method, of class gov.sandia.cognition.learning.kernel.ScalarFunctionKernel.
     */
    public void testSetFunction()
    {
        ScalarFunctionKernel<Double> instance =
            new ScalarFunctionKernel<Double>();
        assertNull(instance.getFunction());

        PolynomialFunction function = new PolynomialFunction(RANDOM.nextDouble());
        instance.setFunction(function);
        assertSame(function, instance.getFunction());

        instance.setFunction(null);
        assertNull(instance.getFunction());
    }

}

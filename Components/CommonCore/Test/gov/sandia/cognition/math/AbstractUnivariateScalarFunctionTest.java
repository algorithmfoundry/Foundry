/*
 * File:                AbstractUnivariateScalarFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.math;

import java.util.Random;
import junit.framework.TestCase;

/**
 * JUnit tests for class AbstractUnivariateScalarFunctionTest
 * @author Kevin R. Dixon
 */
public class AbstractUnivariateScalarFunctionTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random random = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double EPS = 1e-5;

    /**
     * 
     */
    public class ScaleFunction
        extends AbstractDifferentiableUnivariateScalarFunction
    {

        /**
         * 
         */
        public double scale;

        /**
         * 
         */
        public ScaleFunction()
        {
            this.scale = random.nextGaussian();
        }

        @Override
        public ScaleFunction clone()
        {
            ScaleFunction clone = new ScaleFunction();
            clone.scale = this.scale;
            return clone;
        }

        public double evaluate(
            double input)
        {
            return this.scale * input;
        }

        public double differentiate(
            double input)
        {
            return this.scale;
        }

    }

    /**
     * 
     * @return
     */
    public ScaleFunction createInstance()
    {
        return new ScaleFunction();
    }

    /**
     * Entry point for JUnit tests for class AbstractUnivariateScalarFunctionTest
     * @param testName name of this test
     */
    public AbstractUnivariateScalarFunctionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class AbstractUnivariateScalarFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        ScaleFunction instance = this.createInstance();
        ScaleFunction clone = instance.clone();
        assertNotSame(instance, clone);
        assertEquals(instance.scale, clone.scale);

    }

    /**
     * Test of evaluate method, of class AbstractUnivariateScalarFunction.
     */
    public void testEvaluate_double()
    {
        System.out.println("evaluate.double");
        ScaleFunction instance = this.createInstance();
        double x = Math.random();
        double y = instance.scale * x;
        double yhat = instance.evaluate(x);
        assertEquals(y, yhat);
    }

    /**
     * Test of evaluate method, of class AbstractUnivariateScalarFunction.
     */
    public void testEvaluate_Double()
    {
        System.out.println("evaluate.Double");
        ScaleFunction instance = this.createInstance();
        Double x = Math.random();
        Double y = instance.scale * x;
        Double yhat = instance.evaluate(x);
        assertEquals(y, yhat);
    }

}

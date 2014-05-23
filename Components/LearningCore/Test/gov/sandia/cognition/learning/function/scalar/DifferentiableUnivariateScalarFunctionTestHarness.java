/*
 * File:                DifferentiableUnivariateScalarFunctionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 6, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.NumericalDifferentiator;
import gov.sandia.cognition.util.ObjectUtil;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for DifferentiableUnivariateScalarFunctionTestHarness.
 *
 * @author krdixon
 */
public abstract class DifferentiableUnivariateScalarFunctionTestHarness
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double TOLERANCE = 1e-5;

    /**
     * Number of samples, {@value}.
     */
    public final static int NUM_SAMPLES = 100;

    /**
     * Tests for class VectorFunctionToScalarFunctionTest.
     * @param testName Name of the test.
     */
    public DifferentiableUnivariateScalarFunctionTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates a new function
     * @return
     * New function
     */
    public abstract DifferentiableUnivariateScalarFunction createInstance();

    /**
     * Tests constructors
     */
    public abstract void testConstructors();

    /**
     * Clone
     */
    public void testMasterClone()
    {
        System.out.println( "Clone" );

        DifferentiableUnivariateScalarFunction f = this.createInstance();
        DifferentiableUnivariateScalarFunction clone = ObjectUtil.cloneSmart(f);
        assertNotNull( clone );
        assertNotSame( f, clone );

        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            double input = RANDOM.nextGaussian();
            assertEquals( f.evaluate(input), clone.evaluate(input), TOLERANCE );
            assertEquals( f.differentiate(input), clone.differentiate(input), TOLERANCE );
        }

    }

    public void testEvaluateDouble()
    {
        System.out.println( "Evaluate(Double)" );

        DifferentiableUnivariateScalarFunction f = this.createInstance();

        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            Double input = RANDOM.nextGaussian();
            Double result = f.evaluate(input);
            assertNotNull( result );
            assertEquals( result, f.evaluate(input.doubleValue()) );
        }
        
    }

    public void testApproximateDerivative()
    {
        System.out.println( "Approximate derivative" );

        DifferentiableUnivariateScalarFunction f = this.createInstance();
        NumericalDifferentiator.DoubleJacobian dfhat =
            new NumericalDifferentiator.DoubleJacobian( f );
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            Double input = RANDOM.nextGaussian();
            double result = f.differentiate(input);
            double estimate = dfhat.differentiate(input);
            assertEquals( estimate, result, TOLERANCE );
        }

    }

}

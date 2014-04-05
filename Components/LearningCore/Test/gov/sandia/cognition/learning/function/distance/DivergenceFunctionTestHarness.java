/*
 * File:                DivergenceFunctionTestHarness.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 26, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.distance;

import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Tests that a class obeys the properties of a divergence function.
 * @param <FirstType> First type to compute the divergence of
 * @param <SecondType> Second type to compute the divergence of
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class DivergenceFunctionTestHarness<FirstType,SecondType>
    extends TestCase
{

    /**
     * Random number generator
     */
    public static final Random RANDOM = new Random(1);

    /**
     * Tolerance of the tests
     */
    public static double TOLERANCE = 1.0e-5;
    
    /**
     * Number of samples to test in regression
     */
    public int NUM_SAMPLES = 10000;

    /**
     * Constructor
     * @param testName name
     */
    public DivergenceFunctionTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates a random FirstType
     * @return FirstType
     */
    public abstract FirstType generateRandomFirstType();

    /**
     * Creates a random SecondType
     * @return
     */
    public abstract SecondType generateRandomSecondType();

    /**
     * Creates a divergence function
     * @return
     * Divergence function
     */
    public abstract DivergenceFunction<FirstType,SecondType> createInstance();


    /**
     * Tests divergence function against known values
     */
    public abstract void testKnownValues();


    /**
     * Tests the clone method
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        DivergenceFunction<FirstType,SecondType> f = this.createInstance();
        @SuppressWarnings("unchecked")
        DivergenceFunction<FirstType,SecondType> clone = ObjectUtil.cloneSmart(f);
        assertNotNull( clone );
        assertNotSame( f, clone );

        FirstType first = this.generateRandomFirstType();
        SecondType second = this.generateRandomSecondType();
        double fout = f.evaluate(first, second);
        double cloneout = clone.evaluate(first, second);
        assertEquals( fout, cloneout, TOLERANCE );

    }

    /**
     * Makes sure that the divergence is nonnegative
     */
    public void testNonNegative()
    {
        System.out.println( "Nonnegative" );

        DivergenceFunction<FirstType,SecondType> f = this.createInstance();

        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            double output = f.evaluate(
                this.generateRandomFirstType(), this.generateRandomSecondType());
            assertTrue( output >= 0.0 );
        }

    }

}

/*
 * File:                WeightedNumberAveragerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 4, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math;

import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.LinkedList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for WeightedNumberAveragerTest.
 *
 * @author krdixon
 */
public class WeightedNumberAveragerTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random random = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public static final double TOLERANCE = 1e-5;

    /**
     * Tests for class WeightedNumberAveragerTest.
     * @param testName Name of the test.
     */
    public WeightedNumberAveragerTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class WeightedNumberAveragerTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        WeightedNumberAverager instance = new WeightedNumberAverager();
        assertNotNull( instance );
    }

    /**
     * Test of clone method, of class WeightedNumberAverager
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        WeightedNumberAverager instance = new WeightedNumberAverager();
        WeightedNumberAverager clone = (WeightedNumberAverager) instance.clone();
        assertNotSame( instance, clone );
        assertNotNull( clone );
    }

    /**
     * Test of summarize method, of class WeightedNumberAverager.
     */
    public void testSummarize()
    {
        System.out.println("summarize");
        WeightedNumberAverager instance = WeightedNumberAverager.INSTANCE;
        final int NUM_SAMPLES = 100;
        ArrayList<DefaultWeightedValue<Double>> data =
            new ArrayList<DefaultWeightedValue<Double>>( NUM_SAMPLES );
        double weightedSum = 0.0;
        double weightTotal = 0.0;
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            double v = random.nextGaussian();
            double w = random.nextDouble();
            weightedSum += w*v;
            weightTotal += w;
            data.add( new DefaultWeightedValue<Double>( v, w ) );
        }

        double expected = weightedSum / weightTotal;
        double result = instance.summarize(data);
        assertEquals( expected, result, TOLERANCE );

        assertEquals(0.0, instance.summarize(new LinkedList<WeightedValue<Double>>()));
    }

}

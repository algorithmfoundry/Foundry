/*
 * File:                WeightedRingAveragerTest.java
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

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.DefaultWeightedValue;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for WeightedRingAveragerTest.
 *
 * @author krdixon
 */
public class WeightedRingAveragerTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class WeightedRingAveragerTest.
     * @param testName Name of the test.
     */
    public WeightedRingAveragerTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class WeightedRingAveragerTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        WeightedRingAverager<?> averager = new WeightedRingAverager<Vector>();
        assertNotNull( averager );
    }

    /**
     * Clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );

        WeightedRingAverager<?> instance = new WeightedRingAverager<Vector>();
        WeightedRingAverager<?> clone = (WeightedRingAverager<?>) instance.clone();
        assertNotSame( instance, clone );
        assertNotNull( clone );
    }

    /**
     * Test of summarize method, of class WeightedRingAverager.
     */
    public void testSummarize()
    {
        System.out.println("summarize");
        final int NUM_SAMPLES = 100;
        final int DIM = 2;
        ArrayList<DefaultWeightedValue<Vector>> data =
            new ArrayList<DefaultWeightedValue<Vector>>( NUM_SAMPLES );
        RingAccumulator<Vector> average =
            new RingAccumulator<Vector>();
        double weightSum = 0.0;
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            Vector v = VectorFactory.getDefault().createUniformRandom(
                DIM, -1.0, 1.0, RANDOM );
            double w = RANDOM.nextDouble();
            weightSum += w;
            data.add( new DefaultWeightedValue<Vector>( v, w ) );
            average.accumulate( v.scale(w) );
        }

        WeightedRingAverager<Vector> instance = new WeightedRingAverager<Vector>();
        Vector result = instance.summarize(data);
        Vector expected = average.getSum().scale( 1.0/weightSum );
        if( !expected.equals( result, TOLERANCE ) )
        {
            assertEquals( expected, result );
        }

    }

}

/*
 * File:                MultivariateDistributionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 14, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.math.RingAccumulator;
import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for MultivariateDistributionTestHarness.
 *
 * @param <RingType> Type of Ring to test.
 * @author krdixon
 */
public abstract class MultivariateDistributionTestHarness<RingType extends Ring<RingType>>
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double TOLERANCE = 1e-5;

    /**
     * Default number of samples, {@value}.
     */
    public int NUM_SAMPLES = 1000;

    /**
     * Confidence of tests.
     */
    public double CONFIDENCE = 0.95;

    /**
     * Tests for class MultivariateDistributionTestHarness.
     * @param testName Name of the test.
     */
    public MultivariateDistributionTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance
     * @return
     * Instance.
     */
    abstract public Distribution<RingType> createInstance();

    /**
     * Tests the constructors of class MultivariateDistributionTestHarness.
     */
    abstract public void testConstructors();

    /**
     * Tests against known values.
     */
    abstract public void testKnownValues();

    /**
     * clone
     */
    public void testClone()
    {
        System.out.println( "clone" );

        Distribution<RingType> instance = this.createInstance();

        @SuppressWarnings("unchecked")
        Distribution<RingType> clone = (Distribution<RingType>) instance.clone();

        assertNotNull( clone );
        assertNotSame( instance, clone );

        final int rs = RANDOM.nextInt();
        final int ns = RANDOM.nextInt(100);
        Random r1 = new Random( rs );
        Random r2 = new Random( rs );

        ArrayList<? extends RingType> x1 = instance.sample(r1,ns);
        ArrayList<? extends RingType> x2 = clone.sample(r2,ns);

        for( int n = 0; n < ns; n++ )
        {
            RingType y1 = x1.get(n);
            RingType y2 = x2.get(n);
            assertNotNull( y1 );
            assertNotNull( y2 );
            assertNotSame( y1, y2 );
            assertTrue( y1.equals(y2, TOLERANCE ) );
        }

    }

    /**
     * Test of getMean method, of class Distribution.
     */
    public void testGetMean()
    {
        System.out.println("getMean");
        Distribution<RingType> instance = this.createInstance();
        RingAccumulator<RingType> f =
            new RingAccumulator<RingType>( instance.sample(RANDOM, NUM_SAMPLES) );
        System.out.println( "Instance: " + instance );
        RingType sampleMean = f.getMean();
        RingType statedMean = instance.getMean();
        System.out.println( "Sample Mean: " + sampleMean );
        System.out.println( "Stated Mean: " + statedMean );
        if( !sampleMean.equals( statedMean, TOLERANCE ) )
        {
            assertEquals( sampleMean, statedMean );
        }
    }

    /**
     * Test of sample method, of class Distribution.
     */
    public void testSample_Random()
    {
        System.out.println("sample");
        final int rs = RANDOM.nextInt();
        Random r1 = new Random( rs );
        Random r2 = new Random( rs );

        Distribution<RingType> d = this.createInstance();

        RingType x1 = d.sample(r1);
        RingType x2 = d.sample(r2);

        assertNotNull( x1 );
        assertNotNull( x2 );
        assertNotSame( x1, x2 );
        assertTrue( x1.equals( x2, TOLERANCE ) );

    }

    /**
     * Test of sample method, of class Distribution.
     */
    public void testSample_Random_int()
    {
        System.out.println("sample");
        final int rs = RANDOM.nextInt();
        Random r1 = new Random( rs );
        Random r2 = new Random( rs );

        Distribution<RingType> d = this.createInstance();

        ArrayList<? extends RingType> x1 = d.sample(r1,NUM_SAMPLES);
        ArrayList<? extends RingType> x2 = d.sample(r2,NUM_SAMPLES);

        assertEquals( NUM_SAMPLES, x1.size() );
        assertEquals( NUM_SAMPLES, x2.size() );
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            RingType y1 = x1.get(n);
            RingType y2 = x2.get(n);
            assertNotNull( y1 );
            assertNotNull( y2 );
            assertNotSame( y1, y2 );
            assertTrue( y1.equals(y2, TOLERANCE ) );
        }

    }

    /**
     * toString
     */
    public void testToString()
    {
        System.out.println( "toString" );
        
        Distribution<RingType> d = this.createInstance();
        String s = d.toString();
        System.out.println( "Distribution: " + d );
        assertTrue( s.length() > 0 );
    }

    /**
     * Tests getEstimator
     */
    @SuppressWarnings("unchecked")
    public void testEstimableDistributionGetEstimator()
    {
        System.out.println( "EstimableDistribution.getEstimator" );

        Distribution<RingType> instance = this.createInstance();
        if( instance instanceof EstimableDistribution )
        {
            @SuppressWarnings("unchecked")
            EstimableDistribution<RingType,? extends EstimableDistribution<RingType,? extends EstimableDistribution<RingType,?>>> estimable =
                (EstimableDistribution<RingType,? extends EstimableDistribution<RingType,? extends EstimableDistribution<RingType,?>>>) instance;
            DistributionEstimator<RingType, ? extends EstimableDistribution<RingType,? extends EstimableDistribution<RingType,?>>> estimator =
                estimable.getEstimator();

            ArrayList<? extends RingType> samples =
                instance.sample(RANDOM,NUM_SAMPLES);
            EstimableDistribution<RingType, ? extends EstimableDistribution<RingType, ?>> result =
                estimator.learn(samples);
            assertNotNull( result );
            assertNotSame( instance, result );
        }

    }

}

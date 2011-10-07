/*
 * File:                ScalarDataDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 27, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.statistics.UnivariateDistributionTestHarness;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * JUnit tests for class ScalarDataDistributionTest
 * @author Kevin R. Dixon
 */
public class ScalarDataDistributionTest
    extends UnivariateDistributionTestHarness<Double>
{

    /**
     * Entry point for JUnit tests for class ScalarDataDistributionTest
     * @param testName name of this test
     */
    public ScalarDataDistributionTest(
        String testName)
    {
        super(testName);
    }
    
    @Override
    public ScalarDataDistribution createInstance()
    {
        return new ScalarDataDistribution( Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0, 3.0 ) );
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );
        
        ScalarDataDistribution d =
            new ScalarDataDistribution( new ArrayList<Double>( 1 ) );
        System.out.println( d );
        assertEquals( 0.0, d.getTotal() );

        d = new ScalarDataDistribution(
            Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );
        assertEquals( 5.0, d.getTotal() );
        assertEquals( 3, d.getDomain().size() );
        assertEquals( 1.0, d.get(0.0) );
        assertEquals( 2.0, d.get(1.0) );
        assertEquals( 2.0, d.get(2.0) );
        assertEquals( 0.0, d.get(-1.0) );
        assertEquals( 0.0, d.get(3.0) );
        
        System.out.println( d );
        
        ScalarDataDistribution d2 = new ScalarDataDistribution( d );
        assertEquals( d.getTotal(), d2.getTotal() );
        assertEquals( d.getDomain().size(), d2.getDomain().size() );
        assertEquals( 3, d2.getDomain().size() );
        for( Double value : d.getDomain() )
        {
            assertEquals( d.get(value), d2.get(value) );
        }

        d2.increment( -1.0, 1 );
        assertEquals( 6.0, d2.getTotal() );
        assertEquals( 5.0, d.getTotal() );
        assertEquals( 1.0, d2.get(-1.0) );
        assertEquals( 0.0, d.get(-1.0) );
        
    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );

        ScalarDataDistribution.CDF d =
            new ScalarDataDistribution.CDF( new ArrayList<Double>( 1 ) );
        System.out.println( d );
        assertEquals( 0.0, d.getTotal() );

        d = new ScalarDataDistribution.CDF( Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );
        assertEquals( 5.0, d.getTotal() );
        assertEquals( 3, d.getDomain().size() );
        assertEquals( 1.0, d.get(0.0) );
        assertEquals( 2.0, d.get(1.0) );
        assertEquals( 2.0, d.get(2.0) );
        assertEquals( 0.0, d.get(-1.0) );
        assertEquals( 0.0, d.get(3.0) );

        System.out.println( d );

        ScalarDataDistribution.CDF d2 = new ScalarDataDistribution.CDF( d );
        assertEquals( d.getTotal(), d2.getTotal() );
        assertEquals( d.getDomain().size(), d2.getDomain().size() );
        assertEquals( 3, d2.getDomain().size() );
        for( Double value : d.getDomain() )
        {
            assertEquals( d.get(value), d2.get(value) );
        }

        d2.increment( -1.0, 1 );
        assertEquals( 6.0, d2.getTotal() );
        assertEquals( 5.0, d.getTotal() );
        assertEquals( 1.0, d2.get(-1.0) );
        assertEquals( 0.0, d.get(-1.0) );
    }



    /**
     * PMF constructors
     */
    public void testPMFConstructors()
    {
        System.out.println( "PMF Constructors" );

        ScalarDataDistribution.PMF d =
            new ScalarDataDistribution.PMF( new ArrayList<Double>( 1 ) );
        System.out.println( d );
        assertEquals( 0.0, d.getTotal() );

        d = new ScalarDataDistribution.PMF( Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );
        assertEquals( 5.0, d.getTotal() );
        assertEquals( 3, d.getDomain().size() );
        assertEquals( 1.0, d.get(0.0) );
        assertEquals( 2.0, d.get(1.0) );
        assertEquals( 2.0, d.get(2.0) );
        assertEquals( 0.0, d.get(-1.0) );
        assertEquals( 0.0, d.get(3.0) );

        System.out.println( d );

        ScalarDataDistribution.PMF d2 = new ScalarDataDistribution.PMF( d );
        assertEquals( d.getTotal(), d2.getTotal() );
        assertEquals( d.getDomain().size(), d2.getDomain().size() );
        assertEquals( 3, d2.getDomain().size() );
        for( Double value : d.getDomain() )
        {
            assertEquals( d.get(value), d2.get(value) );
        }

        d2.increment( -1.0, 1 );
        assertEquals( 6.0, d2.getTotal() );
        assertEquals( 5.0, d.getTotal() );
        assertEquals( 1.0, d2.get(-1.0) );
        assertEquals( 0.0, d.get(-1.0) );
    }

    /**
     * Test of getEntropy method, of class ScalarDataDistribution.
     */
    public void testGetEntropy()
    {
        System.out.println( "getEntropy" );
        ScalarDataDistribution.PMF instance = new ScalarDataDistribution.PMF(
            Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );
        
        assertEquals( 1.521928, instance.getEntropy(), TOLERANCE );
        
    }

    /**
     * Test of getDomain method, of class ScalarDataDistribution.
     */
    public void testKnownGetDomain()
    {
        System.out.println( "getDomain" );
        List<Double> values = Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 );
        ScalarDataDistribution instance =
            new ScalarDataDistribution( values );
        
        assertNotNull( instance.getDomain() );
        assertNotSame( values, instance.getDomain() );

        assertEquals( 2.0, CollectionUtil.getElement( instance.getDomain(), 0 ) );
        assertEquals( 1.0, CollectionUtil.getElement( instance.getDomain(), 1 ) );
        assertEquals( 0.0, CollectionUtil.getElement( instance.getDomain(), 2 ) );
        
    }

    /**
     * Test of getMean method, of class ScalarDataDistribution.
     */
    public void testGetMean()
    {
        System.out.println( "getMean" );
        ScalarDataDistribution instance = new ScalarDataDistribution(
            Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );
        
        assertEquals( 1.2, instance.getMean() );

        instance = new ScalarDataDistribution( new LinkedList<Double>() );
        assertEquals( 0.0, instance.getMean() );
        
    }

    /**
     * getVariance
     */
    public void testGetVariance()
    {
        System.out.println( "getVariance" );
        ScalarDataDistribution instance = new ScalarDataDistribution(
            Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );

        assertEquals( 0.56, instance.getVariance(), TOLERANCE );
        
        instance = new ScalarDataDistribution();
        assertEquals( 0.0, instance.getVariance() );
    }

    /**
     * Test of sample method, of class ScalarDataDistribution.
     */
    public void testSample()
    {
        System.out.println( "sample" );
        ScalarDataDistribution instance = new ScalarDataDistribution(
            Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );
        
        double standardError = Math.sqrt( instance.getVariance() / NUM_SAMPLES );
        
        ArrayList<Double> result = instance.sample( RANDOM, NUM_SAMPLES );
        
        double sampleMean = UnivariateStatisticsUtil.computeMean( result );
        
        assertEquals( instance.getMean(), sampleMean, standardError*2.0 );
        
    }

    /**
     * evaluate
     */
    public void testPMFKnownValues()
    {
        System.out.println( "PMF.evaluate()" );
        
        ScalarDataDistribution.PMF instance = new ScalarDataDistribution.PMF(
            new LinkedList<Double>() );
        assertEquals( 0.0, instance.evaluate( 2.0 ) );
        
        instance = new ScalarDataDistribution.PMF(
            Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );
        assertEquals( 0.2, instance.evaluate( 0.0 ) );
        assertEquals( 0.4, instance.evaluate( 1.0 ) );
        assertEquals( 0.4, instance.evaluate( 2.0 ) );
        assertEquals( 0.0, instance.evaluate( 1.5 ) );
        
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF.evaluate()" );
        
        ScalarDataDistribution.CDF instance = new ScalarDataDistribution.CDF();
        assertEquals( 0.0, instance.evaluate( 2.0 ) );
        
        instance = new ScalarDataDistribution.CDF(
            Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );
        assertEquals( 0.2, instance.evaluate( 0.0 ) );
        assertEquals( 0.6, instance.evaluate( 1.0 ) );
        assertEquals( 1.0, instance.evaluate( 2.0 ) );
        
        assertEquals( 0.0, instance.evaluate( -1.5 ) );        
        assertEquals( 0.6, instance.evaluate( 1.5 ) );
        assertEquals( 1.0, instance.evaluate( 10.0 ) );
    }
    

    /**
     * add
     */
    public void testCDFAdd()
    {
        System.out.println( "CDF.increment()" );
        
        ScalarDataDistribution.CDF instance = new ScalarDataDistribution.CDF(
            Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );
        assertEquals( 0.2, instance.evaluate( 0.0 ) );
        assertEquals( 0.6, instance.evaluate( 1.0 ) );
        assertEquals( 1.0, instance.evaluate( 2.0 ) );
        
        instance.increment( 1.0 );
        assertEquals( 1.0/6.0, instance.evaluate( 0.0 ) );
        assertEquals( 4.0/6.0, instance.evaluate( 1.0 ) );
        assertEquals( 1.0, instance.evaluate( 2.0 ) );
        
    }

    /**
     * remove
     */
    public void testCDFDecrement()
    {
        System.out.println( "CDF.decrement()" );
        
        ScalarDataDistribution.CDF instance = new ScalarDataDistribution.CDF(
            Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );
        assertEquals( 0.2, instance.evaluate( 0.0 ) );
        assertEquals( 0.6, instance.evaluate( 1.0 ) );
        assertEquals( 1.0, instance.evaluate( 2.0 ) );
        
        instance.decrement( 1.0 );
        assertEquals( 1.0/4.0, instance.evaluate( 0.0 ) );
        assertEquals( 2.0/4.0, instance.evaluate( 1.0 ) );
        assertEquals( 1.0, instance.evaluate( 2.0 ) );
        
        instance.decrement( 2.0, 2 );
        assertEquals( 0.5, instance.evaluate( 0.0 ) );
        assertEquals( 1.0, instance.evaluate( 1.0 ) );
        assertEquals( 1.0, instance.evaluate( 2.0 ) );
        
    }

    /**
     * logEvaluate
     */
    public void testLogEvaluate()
    {
        System.out.println( "logEvaluate" );
        ScalarDataDistribution.PMF instance = new ScalarDataDistribution.PMF(
            Arrays.asList( 2.0, 1.0, 2.0, 1.0, 0.0 ) );

        for( Double value : instance.getDomain() )
        {
            double p = instance.evaluate(value);
            double plog = instance.logEvaluate(value);
            double phat = Math.exp( plog );
            assertEquals( p, phat, TOLERANCE );
        }

    }

    @Override
    public void testCDFSample_Random_int()
    {
//        super.testCDFSample_Random_int();
    }

    @Override
    public void testDistributionGetCDF()
    {
//        super.testDistributionGetCDF();
    }

}

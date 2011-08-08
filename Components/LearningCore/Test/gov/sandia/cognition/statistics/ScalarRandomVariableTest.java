/*
 * File:                ScalarRandomVariableTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 4, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.statistics;

import gov.sandia.cognition.statistics.distribution.UnivariateGaussian;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.GaussianConfidence;
import java.util.ArrayList;
import java.util.Random;

/**
 * JUnit tests for class ScalarRandomVariableTest
 * @author Kevin R. Dixon
 */
public class ScalarRandomVariableTest
    extends RingTestHarness<RandomVariable<Number>>
{

    public Random random = new Random( 1 );
    
    public double TOLERANCE = 1e-5;
    
    public double CONFIDENCE = 0.95;
    
    /**
     * Entry point for JUnit tests for class ScalarRandomVariableTest
     * @param testName name of this test
     */
    public ScalarRandomVariableTest(
        String testName)
    {
        super(testName);
    }

    @Override
    protected ScalarRandomVariable createRandom()
    {
        double mean = random.nextGaussian();
        double variance = random.nextDouble();
        return new ScalarRandomVariable( new UnivariateGaussian(mean,variance), RANDOM );
    }

    @Override
    public void testScaleEquals()
    {
        System.out.println( "scaleEquals" );
        ScalarRandomVariable r = this.createRandom();
        double mean = r.getMean().doubleValue();
        double variance = r.getVariance();
        
        double scale = random.nextGaussian();
        r.scaleEquals( scale );
        
        ConfidenceInterval interval = r.getSamplingError( CONFIDENCE );        
        assertTrue( interval.withinInterval( mean*scale ) );
        
        double EPS = interval.getUpperBound() - interval.getCentralValue();
        assertEquals( variance*scale*scale, r.getVariance(), EPS );
    }

    @Override
    public void testPlusEquals()
    {
        System.out.println( "plusEquals" );
        ScalarRandomVariable r1 = this.createRandom();
        ScalarRandomVariable r2 = this.createRandom();
        
        double mean = r1.getMean().doubleValue() + r2.getMean().doubleValue();
        double variance = r1.getVariance() + r2.getVariance();
        r1.plusEquals( r2 );
        ConfidenceInterval interval = r1.getSamplingError( CONFIDENCE );
        assertTrue( interval.withinInterval( mean ) );
        
        double EPS = interval.getUpperBound() - interval.getCentralValue();
        assertEquals( variance, r1.getVariance(), EPS );
        
    }

    @Override
    public void testDotTimesEquals()
    {
        System.out.println( "dotTimesEquals" );
        ScalarRandomVariable r1 = this.createRandom();
        ScalarRandomVariable r2 = this.createRandom();
        
        double mean = r1.getMean().doubleValue() * r2.getMean().doubleValue();
        r1.dotTimesEquals( r2 );
        ConfidenceInterval interval = r1.getSamplingError( CONFIDENCE );
        assertTrue( interval.withinInterval( mean ) );        
        
    }    
    
    /**
     * Test of equals method, of class ScalarRandomVariable.
     */
    public void testEquals_RandomVariable_double()
    {
        System.out.println( "equals" );
        RandomVariable<Number> other = this.createRandom();
        double effectiveZero = random.nextDouble();
        ScalarRandomVariable instance = this.createRandom();
        
        assertEquals( instance, instance );
        assertEquals( other, other );
        assertFalse( instance.equals( other, effectiveZero ) );
    }

    /**
     * Test of sample method, of class ScalarRandomVariable.
     */
    public void testSample()
    {
        System.out.println( "sample" );
        int numSamples = 10000;
        ScalarRandomVariable instance = this.createRandom();
        
        Random random1a = new Random( 1 );
        ArrayList<Number> r1a = instance.sample( random1a, numSamples );
        
        Random random1b = new Random( 1 );
        ArrayList<Number> r1b = instance.sample( random1b, numSamples );
        for( int i = 0; i < numSamples; i++ )
        {
            assertEquals( r1a.get(i), r1b.get(i) );
        }

        ArrayList<Double> d1a = new ArrayList<Double>( r1a.size() );
        for( Number n1 : r1a )
        {
            d1a.add( n1.doubleValue() );
        }
        
        GaussianConfidence.Statistic stat = GaussianConfidence.evaluateNullHypothesis(
            d1a, instance.getMean().doubleValue() );
        assertEquals( 1.0, stat.getNullHypothesisProbability(), 0.95 );
        
    }

    /**
     * Test of getNumSamples method, of class ScalarRandomVariable.
     */
    public void testGetNumSamples()
    {
        System.out.println( "getNumSamples" );
        ScalarRandomVariable instance = this.createRandom();
        assertTrue( instance.getNumSamples() >= 1 );
    }

    /**
     * Test of setNumSamples method, of class ScalarRandomVariable.
     */
    public void testSetNumSamples()
    {
        System.out.println( "setNumSamples" );
        ScalarRandomVariable instance = this.createRandom();
        int n = instance.getNumSamples();
        assertTrue( n >= 1 );
        int n2 = n + random.nextInt() + 1;
        instance.setNumSamples( n2 );
        assertEquals( n2, instance.getNumSamples() );
        
        try
        {
            instance.setNumSamples( 0 );
            fail( "Num samples must be >= 1" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    /**
     * Test of getRandom method, of class ScalarRandomVariable.
     */
    public void testGetRandom()
    {
        System.out.println( "getRandom" );
        ScalarRandomVariable instance = this.createRandom();
        assertNotNull( instance.getRandom() );
    }

    /**
     * Test of setRandom method, of class ScalarRandomVariable.
     */
    public void testSetRandom()
    {
        System.out.println( "setRandom" );
        ScalarRandomVariable instance = this.createRandom();
        Random r = instance.getRandom();
        assertNotNull( r );
        instance.setRandom( null );
        assertNull( instance.getRandom() );
        instance.setRandom( r );
        assertSame( r, instance.getRandom() );
    }

    /**
     * Test of getDistribution method, of class ScalarRandomVariable.
     */
    public void testGetDistribution()
    {
        System.out.println( "getDistribution" );
        ScalarRandomVariable instance = this.createRandom();
        assertNotNull( instance.getDistribution() );
    }

    /**
     * Test of setDistribution method, of class ScalarRandomVariable.
     */
    public void testSetDistribution()
    {
        System.out.println( "setDistribution" );
        ScalarRandomVariable instance = this.createRandom();
        ScalarDistribution<? extends Number> dist = instance.getDistribution();
        assertNotNull( dist );
        instance.setDistribution( null );
        assertNull( instance.getDistribution() );
        instance.setDistribution( dist );
        assertSame( dist, instance.getDistribution() );
    }

    /**
     * Test of getMean method, of class ScalarRandomVariable.
     */
    public void testGetMean()
    {
        System.out.println( "getMean" );
        ScalarRandomVariable instance = this.createRandom();
        assertEquals( instance.getDistribution().getMean(), instance.getMean() );
    }

    /**
     * Test of getVariance method, of class ScalarRandomVariable.
     */
    public void testGetVariance()
    {
        System.out.println( "getVariance" );
        ScalarRandomVariable instance = this.createRandom();
        assertEquals( instance.getDistribution().getVariance(), instance.getVariance() );
    }

}

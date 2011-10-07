/*
 * File:                BernoulliDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 5, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.collection.IntegerSpan;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.ClosedFormDiscreteUnivariateDistributionTestHarness;

/**
 * JUnit tests for class BernoulliDistributionTest
 * @author Kevin R. Dixon
 */
public class BernoulliDistributionTest
    extends ClosedFormDiscreteUnivariateDistributionTestHarness<Number>
{

    /**
     * Entry point for JUnit tests for class BernoulliDistributionTest
     * @param testName name of this test
     */
    public BernoulliDistributionTest(
        String testName)
    {
        super(testName);
        NUM_SAMPLES = 10*NUM_SAMPLES;
    }

    /**
     * Test of getVariance method, of class BernoulliDistribution.
     */
    public void testGetVariance()
    {
        System.out.println( "getVariance" );
        BernoulliDistribution instance = this.createInstance();
        double variance = instance.getP() * (1.0-instance.getP());
        double result = instance.getVariance();
        assertEquals( variance, result );
    }
    
    /**
     * Test getMean
     */
    public void testGetMean()
    {
        System.out.println( "getMean" );
        BernoulliDistribution instance = this.createInstance();
        double mean = instance.getP();
        double result = instance.getMean();
        assertEquals( mean, result );        
    }

    /**
     * Test of getDomain method, of class BernoulliDistribution.
     */
    @Override
    public void testKnownGetDomain()
    {
        System.out.println( "getDomain" );
        BernoulliDistribution instance = this.createInstance();
        IntegerSpan result = instance.getDomain();
        assertEquals( 2, result.size() );
        assertEquals( 0, CollectionUtil.getElement(result,0).intValue() );
        assertEquals( 1, CollectionUtil.getElement(result,1).intValue() );
    }

    /**
     * Test of getP method, of class BernoulliDistribution.
     */
    public void testGetP()
    {
        System.out.println( "getP" );
        double p = RANDOM.nextDouble();
        BernoulliDistribution instance = new BernoulliDistribution.PMF( p );
        assertEquals( p, instance.getP() );
        
        instance = new BernoulliDistribution.PMF();
        assertEquals( BernoulliDistribution.DEFAULT_P, instance.getP() );
    }

    /**
     * Test of setP method, of class BernoulliDistribution.
     */
    public void testSetP()
    {
        System.out.println( "setP" );
        double p = RANDOM.nextDouble();
        BernoulliDistribution instance = new BernoulliDistribution.PMF( p );
        assertEquals( p, instance.getP() );
        double p2 = p * RANDOM.nextDouble();
        instance.setP( p2 );
        assertEquals( p2, instance.getP() );
        
        double p3 = 0.0;
        instance.setP( p3 );
        assertEquals( p3, instance.getP() );
        
        double p4 = 1.0;
        instance.setP( p4 );
        assertEquals( p4, instance.getP() );
        
        try
        {
            double p5 = -1.0;
            instance.setP( p5 );
            fail( "p must be >= 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        try
        {
            double p6 = 2.0;
            instance.setP( p6 );
            fail( "p must be <= 1.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    @Override
    public void testPMFKnownValues()
    {
        System.out.println( "PMF.knownValues" );
        BernoulliDistribution.PMF pmf =
            this.createInstance().getProbabilityFunction();
        assertEquals( 0.0, pmf.evaluate( -1 ) );
        assertEquals( 1.0-pmf.getP(), pmf.evaluate( 0 ) );
        assertEquals( pmf.getP(), pmf.evaluate( 1 ) );
        assertEquals( 0.0, pmf.evaluate( 2 ) );
    }

    @Override
    public BernoulliDistribution createInstance()
    {
        return new BernoulliDistribution( RANDOM.nextDouble() );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "CDF.knownConvertToVector" );
        BernoulliDistribution cdf = this.createInstance();
        Vector x = cdf.convertToVector();
        assertEquals( 1, x.getDimensionality() );
        assertEquals( cdf.getP(), x.getElement( 0 ) );
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF.knownValues" );
        BernoulliDistribution.CDF cdf = this.createInstance().getCDF();
        assertEquals( 0.0, cdf.evaluate( -1 ) );
        assertEquals( 1.0-cdf.getP(), cdf.evaluate( 0 ) );
        assertEquals( 1.0, cdf.evaluate( 1 ) );
        assertEquals( 1.0, cdf.evaluate( 2 ) );        
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );
        BernoulliDistribution instance = new BernoulliDistribution();
        assertEquals( BernoulliDistribution.DEFAULT_P, instance.getP() );

        double p = RANDOM.nextDouble();
        instance = new BernoulliDistribution( p );
        assertEquals( p, instance.getP() );

        BernoulliDistribution d2 = new BernoulliDistribution( instance );
        assertNotSame( instance, d2 );
        assertEquals( instance.getP(), d2.getP() );

    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF.Constructors" );
        BernoulliDistribution.CDF instance = new BernoulliDistribution.CDF();
        assertEquals( BernoulliDistribution.DEFAULT_P, instance.getP() );

        double p = RANDOM.nextDouble();
        instance = new BernoulliDistribution.CDF( p );
        assertEquals( p, instance.getP() );

        BernoulliDistribution.CDF d2 = new BernoulliDistribution.CDF( instance );
        assertNotSame( instance, d2 );
        assertEquals( instance.getP(), d2.getP() );

        String b = instance.toString();
        System.out.println( "Bernoulli: " + b );
        assertTrue( b.length() > 0 );
    }

    @Override
    public void testPMFConstructors()
    {
        System.out.println( "PMF.Constructors" );
        BernoulliDistribution.PMF instance = new BernoulliDistribution.PMF();
        assertEquals( BernoulliDistribution.DEFAULT_P, instance.getP() );

        double p = RANDOM.nextDouble();
        instance = new BernoulliDistribution.PMF( p );
        assertEquals( p, instance.getP() );

        BernoulliDistribution.PMF d2 = new BernoulliDistribution.PMF( instance );
        assertNotSame( instance, d2 );
        assertEquals( instance.getP(), d2.getP() );
    }

}

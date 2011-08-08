/*
 * File:                DeterministicDistributionTest.java
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
package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.ClosedFormDiscreteUnivariateDistributionTestHarness;
import java.util.ArrayList;
import java.util.Collection;

/**
 * JUnit tests for class DeterministicDistributionTest
 * @author Kevin R. Dixon
 */
public class DeterministicDistributionTest
    extends ClosedFormDiscreteUnivariateDistributionTestHarness<Double>
{

    /**
     * Entry point for JUnit tests for class DeterministicDistributionTest
     * @param testName name of this test
     */
    public DeterministicDistributionTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public void testDistributionGetVariance()
    {
        System.out.println( "getVariance" );

        DeterministicDistribution instance = this.createInstance();

        ArrayList<? extends Number> s1 = instance.sample(RANDOM, NUM_SAMPLES);
        double sampleVariance = UnivariateStatisticsUtil.computeVariance(s1);
        assertEquals( 0.0, sampleVariance, TOLERANCE );
    }

    /**
     * Test of getPoint method, of class DeterministicDistribution.
     */
    public void testGetPoint()
    {
        System.out.println( "getPoint" );
        
        double point = RANDOM.nextGaussian();
        DeterministicDistribution instance = new DeterministicDistribution( point );
        assertEquals( point, instance.getPoint() );
    }

    /**
     * Test of setPoint method, of class DeterministicDistribution.
     */
    public void testSetPoint()
    {
        System.out.println( "setPoint" );
        double point = RANDOM.nextGaussian();
        DeterministicDistribution instance = new DeterministicDistribution( point );
        assertEquals( point, instance.getPoint() );
        
        double p2 = point + RANDOM.nextDouble();
        instance.setPoint( p2 );
        assertEquals( p2, instance.getPoint() );
    }

    @Override
    public DeterministicDistribution createInstance()
    {
        double point = RANDOM.nextGaussian();
        return new DeterministicDistribution( point );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "CDF.knownConvertToVector" );
        DeterministicDistribution f = this.createInstance();
        Vector x = f.convertToVector();
        assertEquals( 1, x.getDimensionality() );
        assertEquals( f.getPoint(), x.getElement( 0 ) );
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF.knownValues" );
        DeterministicDistribution.CDF f = this.createInstance().getCDF();
        assertEquals( 1.0, f.evaluate( f.getPoint() ) );
        for( int i = 0; i < 1000; i++ )
        {
            double x = f.getPoint() + RANDOM.nextGaussian();
            double p = (x < f.getPoint()) ? 0.0 : 1.0;
            assertEquals( p, f.evaluate( x ) );
        }
        
    }

    @Override
    public void testCDFGetVariance()
    {
        // There is zero variance here... overriding because it's
        // too hard to check for something without variance in the general case.
        DeterministicDistribution.CDF f = this.createInstance().getCDF();
        Collection<Double> samples = f.sample(RANDOM, NUM_SAMPLES);
        assertEquals( 0.0, UnivariateStatisticsUtil.computeVariance(samples), TOLERANCE );
    }

    @Override
    public void testDistributionConstructors()
    {
        DeterministicDistribution d = new DeterministicDistribution();
        assertEquals( DeterministicDistribution.DEFAULT_POINT, d.getPoint() );

        double p = RANDOM.nextGaussian();
        d = new DeterministicDistribution( p );
        assertEquals( p, d.getPoint() );

        DeterministicDistribution d2 = new DeterministicDistribution( d );
        assertEquals( d.getPoint(), d2.getPoint() );
    }

    @Override
    public void testCDFConstructors()
    {
        DeterministicDistribution.CDF d = new DeterministicDistribution.CDF();
        assertEquals( DeterministicDistribution.DEFAULT_POINT, d.getPoint() );

        double p = RANDOM.nextGaussian();
        d = new DeterministicDistribution.CDF( p );
        assertEquals( p, d.getPoint() );

        DeterministicDistribution.CDF d2 = new DeterministicDistribution.CDF( d );
        assertEquals( d.getPoint(), d2.getPoint() );
    }


    @Override
    public void testPMFConstructors()
    {
        DeterministicDistribution.PMF d = new DeterministicDistribution.PMF();
        assertEquals( DeterministicDistribution.DEFAULT_POINT, d.getPoint() );

        double p = RANDOM.nextGaussian();
        d = new DeterministicDistribution.PMF( p );
        assertEquals( p, d.getPoint() );

        DeterministicDistribution.PMF d2 = new DeterministicDistribution.PMF( d );
        assertEquals( d.getPoint(), d2.getPoint() );
    }


    @Override
    public void testKnownGetDomain()
    {
        System.out.println( "Known Domain" );

        DeterministicDistribution instance = this.createInstance();
        Collection<Double> domain = instance.getDomain();
        assertEquals( 1, domain.size() );
        assertEquals( instance.getPoint(), CollectionUtil.getFirst(domain).doubleValue() );
    }

    @Override
    public void testPMFKnownValues()
    {
        System.out.println( "PMF Known Values" );
        DeterministicDistribution.PMF pmf = this.createInstance().getProbabilityFunction();
        assertEquals( 1.0, pmf.evaluate(pmf.getPoint()) );
        assertEquals( 0.0, pmf.logEvaluate(pmf.getPoint()) );

        assertEquals( 0.0, pmf.evaluate(pmf.getPoint()+RANDOM.nextDouble()) );
    }

    @Override
    public void testPMFSample()
    {
        // Need to nerf this because the Chi-Square test requires at least
        // two domain values...
    }

}

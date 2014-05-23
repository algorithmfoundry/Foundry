/*
 * File:                NegativeBinomialDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 23, 2010, Sandia Corporation.
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
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ClosedFormIntegerDistributionTestHarness;

/**
 * Unit tests for NegativeBinomialDistributionTest.
 *
 * @author krdixon
 */
public class NegativeBinomialDistributionTest
    extends ClosedFormIntegerDistributionTestHarness
{

    /**
     * Tests for class NegativeBinomialDistributionTest.
     * @param testName Name of the test.
     */
    public NegativeBinomialDistributionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of getP method, of class NegativeBinomialDistribution.
     */
    public void testGetP()
    {
        System.out.println("getP");
        NegativeBinomialDistribution instance = this.createInstance();
        assertTrue( 0.0 <= instance.getP() );
        assertTrue( instance.getP() <= 1.0 );
    }

    /**
     * Test of setP method, of class NegativeBinomialDistribution.
     */
    public void testSetP()
    {
        System.out.println("setP");
        NegativeBinomialDistribution instance = this.createInstance();
        double p = RANDOM.nextDouble();
        instance.setP(p);
        assertEquals( p, instance.getP() );

        p = 1.0;
        instance.setP(p);
        assertEquals( p, instance.getP() );
        p = 0.0;
        instance.setP(p);
        assertEquals( p, instance.getP() );

        try
        {
            instance.setP(-1.0);
            fail( "P must be [0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setP(2.0);
            fail( "P must be [0,1]" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getR method, of class NegativeBinomialDistribution.
     */
    public void testGetR()
    {
        System.out.println("getR");
        NegativeBinomialDistribution instance = this.createInstance();
        assertTrue( instance.getR() > 0 );
    }

    /**
     * Test of setR method, of class NegativeBinomialDistribution.
     */
    public void testSetR()
    {
        System.out.println("setR");
        NegativeBinomialDistribution instance = this.createInstance();
        double r = RANDOM.nextDouble() * 10.0 + 10.0;
        instance.setR(r);
        assertEquals( r, instance.getR() );
        try
        {
            instance.setR(0);
            fail( "R must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public NegativeBinomialDistribution createInstance()
    {
        double p = RANDOM.nextDouble();
        double r = RANDOM.nextDouble() * 10.0 + 5.0;
        return new NegativeBinomialDistribution( r, p );
    }

    @Override
    public void testPMFConstructors()
    {
        System.out.println( "PMF Constructors" );

        NegativeBinomialDistribution.PMF instance =
            new NegativeBinomialDistribution.PMF();
        assertEquals( NegativeBinomialDistribution.DEFAULT_R, instance.getR() );
        assertEquals( NegativeBinomialDistribution.DEFAULT_P, instance.getP() );

        double p = RANDOM.nextDouble();
        double r = RANDOM.nextDouble()*10.0 + 1;
        instance = new NegativeBinomialDistribution.PMF( r, p );
        assertEquals( r, instance.getR() );
        assertEquals( p, instance.getP() );

        NegativeBinomialDistribution.PMF i2 =
            new NegativeBinomialDistribution.PMF( instance );
        assertEquals( instance.getR(), i2.getR() );
        assertEquals( instance.getP(), i2.getP() );
    }

    @Override
    public void testKnownGetDomain()
    {
        System.out.println( "Known getDomain" );

        NegativeBinomialDistribution.CDF cdf = this.createInstance().getCDF();
        IntegerSpan domain = cdf.getDomain();
        assertEquals( 0, CollectionUtil.getFirst(domain).intValue() );
        assertEquals( 1.0, cdf.evaluate( CollectionUtil.getElement(domain, domain.size()-1) ), TOLERANCE );
    }

    @Override
    public void testPMFKnownValues()
    {
        System.out.println( "PMF Known Values" );
        
        NegativeBinomialDistribution.PMF pmf =
            new NegativeBinomialDistribution.PMF( 10, 0.4 );

        assertEquals( 0.12395856317, pmf.evaluate(6), TOLERANCE );
        assertEquals( 0.11067728855, pmf.evaluate(4), TOLERANCE );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );
        NegativeBinomialDistribution instance = this.createInstance();
        Vector parameters = instance.convertToVector();
        assertEquals( 2, parameters.getDimensionality() );
        assertEquals( instance.getR(), parameters.getElement(0) );
        assertEquals( instance.getP(), parameters.getElement(1) );
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Distribution Constructors" );

        NegativeBinomialDistribution instance = new NegativeBinomialDistribution();
        assertEquals( NegativeBinomialDistribution.DEFAULT_R, instance.getR() );
        assertEquals( NegativeBinomialDistribution.DEFAULT_P, instance.getP() );

        double p = RANDOM.nextDouble();
        double r = RANDOM.nextDouble()*10.0 + 1;
        instance = new NegativeBinomialDistribution( r, p );
        assertEquals( r, instance.getR() );
        assertEquals( p, instance.getP() );

        NegativeBinomialDistribution i2 = new NegativeBinomialDistribution( instance );
        assertEquals( instance.getR(), i2.getR() );
        assertEquals( instance.getP(), i2.getP() );

    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );

        NegativeBinomialDistribution.CDF instance =
            new NegativeBinomialDistribution.CDF();
        assertEquals( NegativeBinomialDistribution.DEFAULT_R, instance.getR() );
        assertEquals( NegativeBinomialDistribution.DEFAULT_P, instance.getP() );

        double p = RANDOM.nextDouble();
        double r = RANDOM.nextDouble()*10.0 + 1;
        instance = new NegativeBinomialDistribution.CDF( r, p );
        assertEquals( r, instance.getR() );
        assertEquals( p, instance.getP() );

        NegativeBinomialDistribution.CDF i2 =
            new NegativeBinomialDistribution.CDF( instance );
        assertEquals( instance.getR(), i2.getR() );
        assertEquals( instance.getP(), i2.getP() );
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF Known Values" );

        NegativeBinomialDistribution.CDF cdf =
            new NegativeBinomialDistribution.CDF( 10, 0.4 );

        assertEquals( 0.527174272325658, cdf.evaluate(6), TOLERANCE );
        assertEquals( 0.27925698723913767, cdf.evaluate(4), TOLERANCE );

    }

    @Override
    public void testCDFConvertFromVector()
    {
        System.out.println( "CDF.convertToVector" );

        NegativeBinomialDistribution instance = this.createInstance();
        int r = RANDOM.nextInt(10) + 1;
        double p = RANDOM.nextDouble();
        instance.setR(r);
        instance.setP(p);
        Vector parameters = instance.convertToVector();

        parameters.setElement(0, r);
        parameters.setElement(1, p);
        instance.convertFromVector(parameters);
        Vector p2 = instance.convertToVector();
        assertEquals( parameters, p2 );

        try
        {
            instance.convertFromVector(null);
            System.out.println( "Cannot have null parameters" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            Vector p3 = VectorFactory.getDefault().copyValues(1.0, 0.5, 1.0 );
            instance.convertFromVector(p3);
            fail( "Wrong dimensions!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }
    
    /**
     * Learner
     */
    public void testLearner()
    {
        System.out.println( "Learner" );
        
        this.distributionEstimatorTest( new NegativeBinomialDistribution.MaximumLikelihoodEstimator() );
    }

}

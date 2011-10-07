/*
 * File:                GeometricDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 30, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.collection.IntegerSpan;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.ClosedFormDiscreteUnivariateDistributionTestHarness;
import java.util.Random;

/**
 * Unit tests for GeometricDistributionTest.
 *
 * @author krdixon
 */
public class GeometricDistributionTest
    extends ClosedFormDiscreteUnivariateDistributionTestHarness<Number>
{

    /**
     * Tests for class GeometricDistributionTest.
     * @param testName Name of the test.
     */
    public GeometricDistributionTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public GeometricDistribution createInstance()
    {
        double p = RANDOM.nextDouble();
        return new GeometricDistribution( p );
    }


    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        GeometricDistribution instance = new GeometricDistribution();
        assertEquals( GeometricDistribution.DEFAULT_P, instance.getP() );

        double p = RANDOM.nextDouble();
        instance = new GeometricDistribution( p );
        assertEquals( p, instance.getP() );

        GeometricDistribution i2 = new GeometricDistribution(instance);
        assertEquals( instance.getP(), i2.getP() );
    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );

        GeometricDistribution.CDF instance = new GeometricDistribution.CDF();
        assertEquals( GeometricDistribution.DEFAULT_P, instance.getP() );

        double p = RANDOM.nextDouble();
        instance = new GeometricDistribution.CDF( p );
        assertEquals( p, instance.getP() );

        GeometricDistribution.CDF i2 = new GeometricDistribution.CDF(instance);
        assertEquals( instance.getP(), i2.getP() );
    }

    @Override
    public void testPMFConstructors()
    {
        System.out.println( "PMF Constructors" );

        GeometricDistribution.PMF instance = new GeometricDistribution.PMF();
        assertEquals( GeometricDistribution.DEFAULT_P, instance.getP() );

        double p = RANDOM.nextDouble();
        instance = new GeometricDistribution.PMF( p );
        assertEquals( p, instance.getP() );

        GeometricDistribution.PMF i2 = new GeometricDistribution.PMF(instance);
        assertEquals( instance.getP(), i2.getP() );
    }

    /**
     * Test of getP method, of class GeometricDistribution.
     */
    public void testGetP()
    {
        System.out.println("getP");
        GeometricDistribution instance = this.createInstance();
        assertTrue( instance.getP() >= 0.0 );
        assertTrue( instance.getP() <= 1.0 );
    }

    /**
     * Test of setP method, of class GeometricDistribution.
     */
    public void testSetP()
    {
        System.out.println("setP");
        double p = RANDOM.nextDouble();
        GeometricDistribution instance = this.createInstance();
        instance.setP(p);
        assertEquals( p, instance.getP() );

        instance.setP(0.0);
        instance.setP(1.0);

        try
        {
            instance.setP(-1.0);
            fail( "p must be [0,1] " );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        try
        {
            instance.setP(2.0);
            fail( "p must be [0,1] " );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public void testKnownGetDomain()
    {
        System.out.println( "Known getDomain" );

        GeometricDistribution.CDF instance = this.createInstance().getCDF();
        IntegerSpan domain = instance.getDomain();
        assertEquals( 0, domain.getMinValue() );
        assertEquals( 1.0, instance.evaluate( domain.getMaxValue() ), TOLERANCE );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        GeometricDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( 1, p.getDimensionality() );
        assertEquals( instance.getP(), p.getElement(0) );
    }

    @Override
    public void testPMFKnownValues()
    {
        System.out.println( "PMF Known Values" );

        GeometricDistribution.PMF pmf = new GeometricDistribution.PMF();
        pmf.setP(0.03);
        assertEquals( 0.0140092412, pmf.evaluate(25), TOLERANCE );
        assertEquals( 0.0257620208, pmf.evaluate(5), TOLERANCE );

        pmf.setP(0.01);
        assertEquals( 0.0090438208, pmf.evaluate(10), TOLERANCE );
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF Known Values" );

        GeometricDistribution.CDF cdf = new GeometricDistribution.CDF();
        cdf.setP(0.03);
        assertEquals( 0.5470345359, cdf.evaluate(25), TOLERANCE );
        assertEquals( 0.1670279951, cdf.evaluate(5), TOLERANCE );

        cdf.setP(0.01);
        assertEquals( 0.1046617457, cdf.evaluate(10), TOLERANCE );
    }

    @Override
    public void testDistributionGetVariance()
    {
        RANDOM = new Random(2);
        super.testDistributionGetVariance();
    }




}

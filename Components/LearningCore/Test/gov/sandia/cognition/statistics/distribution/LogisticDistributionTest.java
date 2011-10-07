/*
 * File:                LogisticDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 30, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothUnivariateDistributionTestHarness;

/**
 * Tests for class LogisticDistributionTest.
 * @author krdixon
 */
public class LogisticDistributionTest
    extends SmoothUnivariateDistributionTestHarness
{


    /**
     * Default Constructor
     * @param testName
     * test name
     */
    public LogisticDistributionTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public LogisticDistribution createInstance()
    {
        return new LogisticDistribution( RANDOM.nextGaussian(), RANDOM.nextDouble()*10.0 );
    }


    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Distribuition Constructors" );

        LogisticDistribution instance = new LogisticDistribution();
        assertEquals( LogisticDistribution.DEFAULT_MEAN, instance.getMean() );
        assertEquals( LogisticDistribution.DEFAULT_SCALE, instance.getScale() );

        double mean = RANDOM.nextGaussian();
        double scale = RANDOM.nextDouble();
        instance = new LogisticDistribution( mean, scale );
        assertEquals( mean, instance.getMean() );
        assertEquals( scale, instance.getScale() );

        LogisticDistribution i2 = new LogisticDistribution( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.convertToVector(), i2.convertToVector() );
    }

    @Override
    public void testPDFConstructors()
    {
        System.out.println( "CDF constructors" );

        LogisticDistribution.CDF instance = new LogisticDistribution.CDF();
        assertEquals( LogisticDistribution.DEFAULT_MEAN, instance.getMean() );
        assertEquals( LogisticDistribution.DEFAULT_SCALE, instance.getScale() );

        double mean = RANDOM.nextGaussian();
        double scale = RANDOM.nextDouble();
        instance = new LogisticDistribution.CDF( mean, scale );
        assertEquals( mean, instance.getMean() );
        assertEquals( scale, instance.getScale() );

        LogisticDistribution.CDF i2 = new LogisticDistribution.CDF( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.convertToVector(), i2.convertToVector() );

    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "PDF constructors" );

        LogisticDistribution.PDF instance = new LogisticDistribution.PDF();
        assertEquals( LogisticDistribution.DEFAULT_MEAN, instance.getMean() );
        assertEquals( LogisticDistribution.DEFAULT_SCALE, instance.getScale() );

        double mean = RANDOM.nextGaussian();
        double scale = RANDOM.nextDouble();
        instance = new LogisticDistribution.PDF( mean, scale );
        assertEquals( mean, instance.getMean() );
        assertEquals( scale, instance.getScale() );

        LogisticDistribution.PDF i2 = new LogisticDistribution.PDF( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.convertToVector(), i2.convertToVector() );
    }


    /**
     * Test of getScale method, of class LogisticDistribution.
     */
    public void testGetScale()
    {
        System.out.println("getScale");
        LogisticDistribution instance = this.createInstance();
        double scale = RANDOM.nextDouble();
        instance.setScale(scale);
        assertEquals( scale, instance.getScale() );

    }

    /**
     * Test of setScale method, of class LogisticDistribution.
     */
    public void testSetScale()
    {
        System.out.println("setScale");
        LogisticDistribution instance = this.createInstance();
        double scale = RANDOM.nextDouble();
        instance.setScale(scale);
        assertEquals( scale, instance.getScale() );

        try
        {
            instance.setScale(0.0);
            fail( "Scale must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }


    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF Known Values" );

        LogisticDistribution.PDF pdf = new LogisticDistribution.PDF( 1.0, 2.0 );

        assertEquals( 0.0745732260, pdf.evaluate(-2.0), TOLERANCE );
        assertEquals( 0.0983059666, pdf.evaluate(-1.0), TOLERANCE );
        assertEquals( 0.1175018561, pdf.evaluate( 2.0), TOLERANCE );
        assertEquals( 0.0745732260, pdf.evaluate( 4.0), TOLERANCE );

    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF Known Values" );
        LogisticDistribution.CDF cdf = new LogisticDistribution.CDF( 1.0, 2.0 );
        assertEquals( 0.5, cdf.evaluate(cdf.getMean()), TOLERANCE );

        assertEquals( 0.1824255238, cdf.evaluate(-2.0), TOLERANCE );
        assertEquals( 0.2689414214, cdf.evaluate(-1.0), TOLERANCE );
        assertEquals( 0.6224593312, cdf.evaluate( 2.0), TOLERANCE );
        assertEquals( 0.8175744762, cdf.evaluate( 4.0), TOLERANCE );

    }


    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        LogisticDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( 2, p.getDimensionality() );
        assertEquals( instance.getMean(), p.getElement(0) );
        assertEquals( instance.getScale(), p.getElement(1) );
    }



}

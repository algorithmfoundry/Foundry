/*
 * File:                CauchyDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 25, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothScalarDistributionTestHarness;

/**
 * Unit tests for CauchyDistributionTest.
 *
 * @author krdixon
 */
public class CauchyDistributionTest
    extends SmoothScalarDistributionTestHarness
{

    /**
     * Tests for class CauchyDistributionTest.
     * @param testName Name of the test.
     */
    public CauchyDistributionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Test of getLocation method, of class CauchyDistribution.
     */
    public void testGetLocation()
    {
        System.out.println("getLocation");
        double location = RANDOM.nextGaussian();
        CauchyDistribution instance = this.createInstance();
        instance.setLocation(location);
        assertEquals( location, instance.getLocation() );
    }

    /**
     * Test of setLocation method, of class CauchyDistribution.
     */
    public void testSetLocation()
    {
        System.out.println("setLocation");
        double location = RANDOM.nextGaussian();
        CauchyDistribution instance = this.createInstance();
        instance.setLocation(location);
        assertEquals( location, instance.getLocation() );
    }

    /**
     * Test of getScale method, of class CauchyDistribution.
     */
    public void testGetScale()
    {
        System.out.println("getScale");
        CauchyDistribution instance = this.createInstance();
        assertTrue( instance.getScale() > 0.0 );
    }

    /**
     * Test of setScale method, of class CauchyDistribution.
     */
    public void testSetScale()
    {
        System.out.println("setScale");
        double scale = RANDOM.nextDouble() * 5.0;
        CauchyDistribution instance = this.createInstance();
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
    public CauchyDistribution createInstance()
    {
        double location = RANDOM.nextGaussian();
        double scale = RANDOM.nextDouble() * 5.0;
        return new CauchyDistribution( location, scale );
    }

    @Override
    public void testPDFConstructors()
    {
        System.out.println( "PDF Constructors" );
        CauchyDistribution.PDF instance = new CauchyDistribution.PDF();
        assertEquals( CauchyDistribution.DEFAULT_LOCATION, instance.getLocation() );
        assertEquals( CauchyDistribution.DEFAULT_SCALE, instance.getScale() );

        double location = RANDOM.nextGaussian();
        double scale = RANDOM.nextDouble();
        instance = new CauchyDistribution.PDF( location, scale );
        assertEquals( location, instance.getLocation() );
        assertEquals( scale, instance.getScale() );

        CauchyDistribution.PDF i2 = new CauchyDistribution.PDF( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getLocation(), i2.getLocation() );
        assertEquals( instance.getScale(), i2.getScale() );
    }

    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF Known Values" );

        CauchyDistribution.PDF pdf = new CauchyDistribution.PDF( 1.0, 2.0 );
        assertEquals( 0.159154943091895, pdf.evaluate(1.0), TOLERANCE );
        assertEquals( 0.127323954473516, pdf.evaluate(2.0), TOLERANCE );
        assertEquals( 0.048970751720583, pdf.evaluate(-2.0), TOLERANCE );

    }

    @Override
    public void testKnownConvertToVector()
    {
        CauchyDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( 2, p.getDimensionality() );
        assertEquals( instance.getLocation(), p.getElement(0), TOLERANCE );
        assertEquals( instance.getScale(), p.getElement(1), TOLERANCE );
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        CauchyDistribution instance = new CauchyDistribution();
        assertEquals( CauchyDistribution.DEFAULT_LOCATION, instance.getLocation() );
        assertEquals( CauchyDistribution.DEFAULT_SCALE, instance.getScale() );

        double location = RANDOM.nextGaussian();
        double scale = RANDOM.nextDouble();
        instance = new CauchyDistribution( location, scale );
        assertEquals( location, instance.getLocation() );
        assertEquals( scale, instance.getScale() );

        CauchyDistribution i2 = new CauchyDistribution( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getLocation(), i2.getLocation() );
        assertEquals( instance.getScale(), i2.getScale() );
    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );
        CauchyDistribution.CDF instance = new CauchyDistribution.CDF();
        assertEquals( CauchyDistribution.DEFAULT_LOCATION, instance.getLocation() );
        assertEquals( CauchyDistribution.DEFAULT_SCALE, instance.getScale() );

        double location = RANDOM.nextGaussian();
        double scale = RANDOM.nextDouble();
        instance = new CauchyDistribution.CDF( location, scale );
        assertEquals( location, instance.getLocation() );
        assertEquals( scale, instance.getScale() );

        CauchyDistribution.CDF i2 = new CauchyDistribution.CDF( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getLocation(), i2.getLocation() );
        assertEquals( instance.getScale(), i2.getScale() );
    }

    @Override
    public void testCDFKnownValues()
    {
        CauchyDistribution.CDF cdf = new CauchyDistribution.CDF( 1.0, 2.0 );
        assertEquals( 0.5, cdf.evaluate(1.0), TOLERANCE );
        assertEquals( 0.647583617650433, cdf.evaluate(2.0), TOLERANCE );
        assertEquals( 0.187167041810999, cdf.evaluate(-2.0), TOLERANCE );
    }

    @Override
    public void testPDFMonteCarlo()
    {
        // We're not going to call PDF Monte Carlo because we're already happy
        // that we're working ;)
        // And because the mean of the Cauchy is so jacked up.
    }

}

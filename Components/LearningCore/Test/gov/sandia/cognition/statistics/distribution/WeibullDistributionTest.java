/*
 * File:                WeibullDistributionTest.java
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

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothUnivariateDistributionTestHarness;

/**
 * Unit tests for WeibullDistributionTest.
 *
 * @author krdixon
 */
public class WeibullDistributionTest
    extends SmoothUnivariateDistributionTestHarness
{

    /**
     * Tests for class WeibullDistributionTest.
     * @param testName Name of the test.
     */
    public WeibullDistributionTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public WeibullDistribution createInstance()
    {
        double shape = RANDOM.nextDouble() * 2.0 + 1.0;
        double scale = RANDOM.nextDouble() * 2.0;
        return new WeibullDistribution(shape, scale);
    }


    /**
     * Tests the constructors of class WeibullDistributionTest.
     */
    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        WeibullDistribution instance = new WeibullDistribution();
        assertEquals( WeibullDistribution.DEFAULT_SHAPE, instance.getShape() );
        assertEquals( WeibullDistribution.DEFAULT_SCALE, instance.getScale() );

        double shape = RANDOM.nextDouble();
        double scale = RANDOM.nextDouble();
        instance = new WeibullDistribution(shape, scale);
        assertEquals( shape, instance.getShape() );
        assertEquals( scale, instance.getScale() );

        WeibullDistribution i2 = new WeibullDistribution(instance);
        assertNotSame( instance, i2 );
        assertEquals( instance.getShape(), i2.getShape() );
        assertEquals( instance.getScale(), i2.getScale() );
    }


    @Override
    public void testPDFConstructors()
    {
        System.out.println( "PDF.Constructors" );

        WeibullDistribution.PDF instance = new WeibullDistribution.PDF();
        assertEquals( WeibullDistribution.DEFAULT_SHAPE, instance.getShape() );
        assertEquals( WeibullDistribution.DEFAULT_SCALE, instance.getScale() );

        double shape = RANDOM.nextDouble();
        double scale = RANDOM.nextDouble();
        instance = new WeibullDistribution.PDF(shape, scale);
        assertEquals( shape, instance.getShape() );
        assertEquals( scale, instance.getScale() );

        WeibullDistribution.PDF i2 = new WeibullDistribution.PDF(instance);
        assertNotSame( instance, i2 );
        assertEquals( instance.getShape(), i2.getShape() );
        assertEquals( instance.getScale(), i2.getScale() );
    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF.Constructors" );

        WeibullDistribution.CDF instance = new WeibullDistribution.CDF();
        assertEquals( WeibullDistribution.DEFAULT_SHAPE, instance.getShape() );
        assertEquals( WeibullDistribution.DEFAULT_SCALE, instance.getScale() );

        double shape = RANDOM.nextDouble();
        double scale = RANDOM.nextDouble();
        instance = new WeibullDistribution.CDF(shape, scale);
        assertEquals( shape, instance.getShape() );
        assertEquals( scale, instance.getScale() );

        WeibullDistribution.CDF i2 = new WeibullDistribution.CDF(instance);
        assertNotSame( instance, i2 );
        assertEquals( instance.getShape(), i2.getShape() );
        assertEquals( instance.getScale(), i2.getScale() );
    }

    /**
     * Test of getShape method, of class WeibullDistribution.
     */
    public void testGetShape()
    {
        System.out.println("getShape");
        WeibullDistribution instance = this.createInstance();
        assertTrue( instance.getShape() > 0.0 );
    }

    /**
     * Test of setShape method, of class WeibullDistribution.
     */
    public void testSetShape()
    {
        System.out.println("setShape");
        double shape = RANDOM.nextDouble();
        WeibullDistribution instance = this.createInstance();
        instance.setShape(shape);
        assertEquals( shape, instance.getShape() );

        try
        {
            instance.setShape(0.0);
            fail( "Shape must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getScale method, of class WeibullDistribution.
     */
    public void testGetScale()
    {
        System.out.println("getScale");
        WeibullDistribution instance = this.createInstance();
        assertTrue( instance.getScale() > 0.0 );
    }

    /**
     * Test of setScale method, of class WeibullDistribution.
     */
    public void testSetScale()
    {
        System.out.println("setScale");
        double scale = RANDOM.nextDouble();
        WeibullDistribution instance = this.createInstance();
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
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        WeibullDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( 2, p.getDimensionality() );
        assertEquals( instance.getShape(), p.getElement(0) );
        assertEquals( instance.getScale(), p.getElement(1) );
    }

    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF Known Values" );

        WeibullDistribution.PDF instance = new WeibullDistribution.PDF( 1.0, 2.0 );
        assertEquals( 0.1115650801, instance.evaluate(3.0), TOLERANCE );

        instance.setShape(5.0);
        instance.setScale(3.0);
        assertEquals( 0.020491603, instance.evaluate(1.0), TOLERANCE );

        assertEquals( 0.0, instance.evaluate(-1.0) );
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF Known Values" );

        WeibullDistribution.CDF cdf = new WeibullDistribution.CDF();

        cdf.setShape(1.0);
        cdf.setScale(2.0);
        assertEquals( 0.7768698399, cdf.evaluate(3.0), TOLERANCE );

        cdf.setShape(5.0);
        cdf.setScale(3.0);
        assertEquals( 0.0041067704, cdf.evaluate(1.0), TOLERANCE );

        assertEquals( 0.0, cdf.evaluate(-1.0) );   
    }

}

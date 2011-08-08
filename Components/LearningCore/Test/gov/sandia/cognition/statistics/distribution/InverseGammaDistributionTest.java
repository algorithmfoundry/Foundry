/*
 * File:                InverseGammaDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 10, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothScalarDistributionTestHarness;
import java.util.ArrayList;

/**
 * Unit tests for InverseGammaDistributionTest.
 *
 * @author krdixon
 */
public class InverseGammaDistributionTest
    extends SmoothScalarDistributionTestHarness
{

    /**
     * Tests for class InverseGammaDistributionTest.
     * @param testName Name of the test.
     */
    public InverseGammaDistributionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class InverseGammaDistributionTest.
     */
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        InverseGammaDistribution instance = new InverseGammaDistribution();
        assertEquals( InverseGammaDistribution.DEFAULT_SHAPE, instance.getShape() );
        assertEquals( InverseGammaDistribution.DEFAULT_SCALE, instance.getScale() );

        double shape = RANDOM.nextDouble() + 1.0;
        double scale = RANDOM.nextDouble() + 1.0;
        instance = new InverseGammaDistribution(shape,scale);
        assertEquals( shape, instance.getShape() );
        assertEquals( scale, instance.getScale() );

        InverseGammaDistribution i2 = new InverseGammaDistribution( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getShape(), i2.getShape() );
        assertEquals( instance.getScale(), i2.getScale() );
    }

    /**
     * Test of getShape method, of class InverseGammaDistribution.
     */
    public void testGetShape()
    {
        System.out.println("getShape");
        double shape = RANDOM.nextDouble() + 1.0;
        InverseGammaDistribution instance = this.createInstance();
        instance.setShape(shape);
        assertEquals( shape, instance.getShape() );
    }

    /**
     * Test of setShape method, of class InverseGammaDistribution.
     */
    public void testSetShape()
    {
        System.out.println("setShape");
        double shape = RANDOM.nextDouble() + 1.0;
        InverseGammaDistribution instance = this.createInstance();
        instance.setShape(shape);
        assertEquals( shape, instance.getShape() );

        try
        {
            instance.setShape(0.0);
            fail( "Shape must be >0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getScale method, of class InverseGammaDistribution.
     */
    public void testGetScale()
    {
        System.out.println("getScale");
        double scale = RANDOM.nextDouble() + 1.0;
        InverseGammaDistribution instance = this.createInstance();
        instance.setScale(scale);
        assertEquals( scale, instance.getScale() );

    }

    /**
     * Test of setScale method, of class InverseGammaDistribution.
     */
    public void testSetScale()
    {
        System.out.println("setScale");
        double scale = RANDOM.nextDouble() + 1.0;
        InverseGammaDistribution instance = this.createInstance();
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
    public InverseGammaDistribution createInstance()
    {
        double shape = RANDOM.nextDouble()*3.0 + 2.0;
        double scale = RANDOM.nextDouble()*3.0;
        return new InverseGammaDistribution( shape, scale );
    }

    @Override
    public void testPDFConstructors()
    {
        System.out.println( "PDF.Constructors" );
        InverseGammaDistribution.PDF instance = new InverseGammaDistribution.PDF();
        assertEquals( InverseGammaDistribution.DEFAULT_SHAPE, instance.getShape() );
        assertEquals( InverseGammaDistribution.DEFAULT_SCALE, instance.getScale() );

        double shape = RANDOM.nextDouble() + 1.0;
        double scale = RANDOM.nextDouble() + 1.0;
        instance = new InverseGammaDistribution.PDF(shape,scale);
        assertEquals( shape, instance.getShape() );
        assertEquals( scale, instance.getScale() );

        InverseGammaDistribution.PDF i2 =
            new InverseGammaDistribution.PDF( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getShape(), i2.getShape() );
        assertEquals( instance.getScale(), i2.getScale() );
    }

    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF Known Values" );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        InverseGammaDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( 2, p.getDimensionality() );
        assertEquals( instance.getShape(), p.getElement(0) );
        assertEquals( instance.getScale(), p.getElement(1) );
    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "PDF.Constructors" );
        InverseGammaDistribution.CDF instance = new InverseGammaDistribution.CDF();
        assertEquals( InverseGammaDistribution.DEFAULT_SHAPE, instance.getShape() );
        assertEquals( InverseGammaDistribution.DEFAULT_SCALE, instance.getScale() );

        double shape = RANDOM.nextDouble() + 1.0;
        double scale = RANDOM.nextDouble() + 1.0;
        instance = new InverseGammaDistribution.CDF(shape,scale);
        assertEquals( shape, instance.getShape() );
        assertEquals( scale, instance.getScale() );

        InverseGammaDistribution.CDF i2 =
            new InverseGammaDistribution.CDF( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getShape(), i2.getShape() );
        assertEquals( instance.getScale(), i2.getScale() );
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF Known values" );

        InverseGammaDistribution.CDF instance = new InverseGammaDistribution.CDF();
        GammaDistribution.CDF gamma = new GammaDistribution.CDF(
            instance.getShape(), 1.0/instance.getScale() );
        ArrayList<? extends Double> samples = instance.sample(RANDOM,NUM_SAMPLES);
        for( Double sample : samples )
        {
            assertEquals( 1.0-gamma.evaluate(1.0/sample), instance.evaluate(sample), TOLERANCE );
        }

    }

}

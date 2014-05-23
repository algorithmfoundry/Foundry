/*
 * File:                YuleSimonDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 31, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.collection.IntegerSpan;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.ClosedFormIntegerDistributionTestHarness;

/**
 * Unit tests for YuleSimonDistributionTest.
 *
 * @author krdixon
 */
public class YuleSimonDistributionTest
    extends ClosedFormIntegerDistributionTestHarness
{

    /**
     * Tests for class YuleSimonDistributionTest.
     * @param testName Name of the test.
     */
    public YuleSimonDistributionTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public YuleSimonDistribution createInstance()
    {
        return new YuleSimonDistribution( RANDOM.nextDouble() + 3.0 );
    }


    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        YuleSimonDistribution instance = new YuleSimonDistribution();
        assertEquals( YuleSimonDistribution.DEFAULT_SHAPE, instance.getShape() );

        double shape = RANDOM.nextDouble();
        instance = new YuleSimonDistribution( shape );
        assertEquals( shape, instance.getShape() );

        YuleSimonDistribution i2 = new YuleSimonDistribution( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getShape(), i2.getShape() );
    }

    @Override
    public void testPMFConstructors()
    {
        System.out.println( "PMF Constructors" );

        YuleSimonDistribution.PMF instance = new YuleSimonDistribution.PMF();
        assertEquals( YuleSimonDistribution.DEFAULT_SHAPE, instance.getShape() );

        double shape = RANDOM.nextDouble();
        instance = new YuleSimonDistribution.PMF( shape );
        assertEquals( shape, instance.getShape() );

        YuleSimonDistribution.PMF i2 = new YuleSimonDistribution.PMF( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getShape(), i2.getShape() );
    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );

        YuleSimonDistribution.CDF instance = new YuleSimonDistribution.CDF();
        assertEquals( YuleSimonDistribution.DEFAULT_SHAPE, instance.getShape() );

        double shape = RANDOM.nextDouble();
        instance = new YuleSimonDistribution.CDF( shape );
        assertEquals( shape, instance.getShape() );

        YuleSimonDistribution.CDF i2 = new YuleSimonDistribution.CDF( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getShape(), i2.getShape() );
    }

    /**
     * Test of getShape method, of class YuleSimonDistribution.
     */
    public void testGetShape()
    {
        System.out.println("getShape");
        YuleSimonDistribution instance = this.createInstance();
        assertTrue( instance.getShape() > 0.0 );
    }

    /**
     * Test of setShape method, of class YuleSimonDistribution.
     */
    public void testSetShape()
    {
        System.out.println("setShape");
        YuleSimonDistribution instance = this.createInstance();
        double shape = RANDOM.nextDouble();
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

    @Override
    public void testKnownGetDomain()
    {
        System.out.println( "Known getDomain" );

        YuleSimonDistribution.CDF instance = this.createInstance().getCDF();
        IntegerSpan domain = instance.getDomain();
        assertEquals( 1, domain.getMinValue() );
        assertEquals( 1.0, instance.evaluate(domain.getMaxValue()), TOLERANCE );
    }

    @Override
    public void testPMFKnownValues()
    {
        System.out.println( "PMF Known Values" );

        YuleSimonDistribution.PMF instance = new YuleSimonDistribution.PMF( 0.25 );
        assertEquals( 0.0888888888, instance.evaluate(2), TOLERANCE );
        assertEquals( 0.012550, instance.evaluate(10), TOLERANCE );

        instance.setShape(4.0);
        assertEquals( 0.1333333333, instance.evaluate(2), TOLERANCE );
        assertEquals( 3.996003995e-4, instance.evaluate(10), TOLERANCE );
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF Known Values" );
        YuleSimonDistribution.CDF instance = new YuleSimonDistribution.CDF( 0.25 );
        assertEquals( 0.2888888888888, instance.evaluate(2), TOLERANCE );
        assertEquals( 0.4980025, instance.evaluate(10), TOLERANCE );

        instance.setShape( 4.0 );
        assertEquals( 0.9333333333333, instance.evaluate(2), TOLERANCE );
        assertEquals( 0.999000999, instance.evaluate(10), TOLERANCE );

    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        YuleSimonDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( 1, p.getDimensionality() );
        assertEquals( instance.getShape(), p.getElement(0) );
    }

    @Override
    public void testDistributionGetVariance()
    {
        int temp = NUM_SAMPLES;
        NUM_SAMPLES = 10000;
        super.testDistributionGetVariance();
        NUM_SAMPLES = temp;
    }

}

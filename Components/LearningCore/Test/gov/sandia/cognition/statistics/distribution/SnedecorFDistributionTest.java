/*
 * File:                SnedecorFDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 16, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.ClosedFormScalarDistributionTestHarness;

/**
 *
 * @author Kevin R. Dixon
 */
public class SnedecorFDistributionTest
    extends ClosedFormScalarDistributionTestHarness<Double>
{

    /**
     * Constructor
     * @param testName name
     */
    public SnedecorFDistributionTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public SnedecorFDistribution createInstance()
    {
        double v1 = RANDOM.nextDouble() * 10.0 + 4;
        double v2 = RANDOM.nextDouble() * 10.0 + 4;
        return new SnedecorFDistribution( v1, v2 );
    }
    
    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF.evaluate" );

        SnedecorFDistribution.CDF cdf = new SnedecorFDistribution.CDF( 2, 3 );

        // I got these values from octave's f_cdf() function
        assertEquals( 0.092270, cdf.evaluate( 0.1 ), TOLERANCE );
        assertEquals( 0.350481, cdf.evaluate( 0.5 ), TOLERANCE );
        assertEquals( 0.535242, cdf.evaluate( 1.0 ), TOLERANCE );
        assertEquals( 0.719434, cdf.evaluate( 2.0 ), TOLERANCE );

        cdf = new SnedecorFDistribution.CDF( 4, 2 );
        assertEquals( 0.027778, cdf.evaluate( 0.1 ), TOLERANCE );
        assertEquals( 0.250000, cdf.evaluate( 0.5 ), TOLERANCE );
        assertEquals( 0.444444, cdf.evaluate( 1.0 ), TOLERANCE );
        assertEquals( 0.640000, cdf.evaluate( 2.0 ), TOLERANCE );
    }

    /**
     * Test of getV1 method, of class gov.sandia.cognition.learning.util.statistics.SnedecorFDistribution.CumulativeDistribution.
     */
    public void testGetV1()
    {
        System.out.println( "getV1" );

        double v1 = RANDOM.nextDouble() * 10.0;
        double v2 = RANDOM.nextDouble() * 10.0;
        SnedecorFDistribution.CDF instance = new SnedecorFDistribution.CDF( v1, v2 );
        assertEquals( v1, instance.getV1() );

    }

    /**
     * Test of setV1 method, of class gov.sandia.cognition.learning.util.statistics.SnedecorFDistribution.CumulativeDistribution.
     */
    public void testSetV1()
    {
        System.out.println( "setV1" );

        double v1 = RANDOM.nextDouble() * 10.0;
        double v2 = RANDOM.nextDouble() * 10.0;
        SnedecorFDistribution.CDF instance = new SnedecorFDistribution.CDF( v1, v2 );
        assertEquals( v1, instance.getV1() );
        
        v1 += RANDOM.nextDouble();
        instance.setV1( v1 );
        assertEquals( v1, instance.getV1() );
        
        try
        {
            instance.setV1( 0.0 );
            fail( "v1 must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getV2 method, of class gov.sandia.cognition.learning.util.statistics.SnedecorFDistribution.CumulativeDistribution.
     */
    public void testGetV2()
    {
        System.out.println( "getV2" );

        double v1 = RANDOM.nextDouble() * 10.0;
        double v2 = RANDOM.nextDouble() * 10.0;
        SnedecorFDistribution.CDF instance = new SnedecorFDistribution.CDF( v1, v2 );
        assertEquals( v2, instance.getV2() );
    }

    /**
     * Test of setV2 method, of class gov.sandia.cognition.learning.util.statistics.SnedecorFDistribution.CumulativeDistribution.
     */
    public void testSetV2()
    {
        System.out.println( "setV2" );

        double v1 = RANDOM.nextDouble() * 10.0;
        double v2 = RANDOM.nextDouble() * 10.0;
        SnedecorFDistribution.CDF instance = new SnedecorFDistribution.CDF( v1, v2 );
        assertEquals( v2, instance.getV2() );
        
        v2 += RANDOM.nextDouble();
        instance.setV2( v2 );
        assertEquals( v2, instance.getV2() );
        
        try
        {
            instance.setV2( 0.0 );
            fail( "v2 must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "CDF.convertToVector" );
        SnedecorFDistribution f = this.createInstance();
        Vector x = f.convertToVector();
        assertEquals( 2, x.getDimensionality() );
        assertEquals( f.getV1(), x.getElement( 0 ) );
        assertEquals( f.getV2(), x.getElement( 1 ) );
    }
    
    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructor" );
        SnedecorFDistribution f = new SnedecorFDistribution();
        assertEquals( SnedecorFDistribution.DEFAULT_V1, f.getV1() );
        assertEquals( SnedecorFDistribution.DEFAULT_V2, f.getV2() );

        double v1 = Math.abs( RANDOM.nextGaussian() );
        double v2 = Math.abs( RANDOM.nextGaussian() );
        f = new SnedecorFDistribution( v1, v2 );
        assertEquals( v1, f.getV1() );
        assertEquals( v2, f.getV2() );

        SnedecorFDistribution f2 = new SnedecorFDistribution( f );
        assertEquals( f.getV1(), f2.getV1() );
        assertEquals( f.getV2(), f2.getV2() );

    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructor" );
        SnedecorFDistribution.CDF f = new SnedecorFDistribution.CDF();
        assertEquals( SnedecorFDistribution.DEFAULT_V1, f.getV1() );
        assertEquals( SnedecorFDistribution.DEFAULT_V2, f.getV2() );

        double v1 = Math.abs( RANDOM.nextGaussian() );
        double v2 = Math.abs( RANDOM.nextGaussian() );
        f = new SnedecorFDistribution.CDF( v1, v2 );
        assertEquals( v1, f.getV1() );
        assertEquals( v2, f.getV2() );

        SnedecorFDistribution.CDF f2 = new SnedecorFDistribution.CDF( f );
        assertEquals( f.getV1(), f2.getV1() );
        assertEquals( f.getV2(), f2.getV2() );
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

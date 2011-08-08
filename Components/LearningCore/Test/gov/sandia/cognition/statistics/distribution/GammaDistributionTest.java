/*
 * File:                GammaDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 2, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothScalarDistributionTestHarness;

/**
 * 
 * @author Kevin R. Dixon
 */
public class GammaDistributionTest
    extends SmoothScalarDistributionTestHarness
{

    /**
     * Constructor
     * @param testName name
     */
    public GammaDistributionTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public GammaDistribution createInstance()
    {
        double shape = RANDOM.nextDouble() * 5 + 2;
        double scale = RANDOM.nextDouble() * 5 + 1;
        return new GammaDistribution( shape, scale );
    }
    
    /**
     * Default constructor
     */
    public void testDefaultConstructor()
    {
        System.out.println( "Default constructor" );

        GammaDistribution g = new GammaDistribution.CDF();
        assertEquals( GammaDistribution.DEFAULT_SHAPE, g.getShape() );
        assertEquals( GammaDistribution.DEFAULT_SCALE, g.getScale() );

        g = new GammaDistribution.PDF();
        assertEquals( GammaDistribution.DEFAULT_SHAPE, g.getShape() );
        assertEquals( GammaDistribution.DEFAULT_SCALE, g.getScale() );
        
    }

    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF.evaluate" );

        // I got these values from octave's gamma_pdf() function
        // However, octave's second parameter is INVERTED from my expecatation
        // Thus, GammaDistribution.evaluate( x, dof, theta ) == gamma_pdf(x, dof, 1/theta)
        assertEquals( 0.3678794412, GammaDistribution.PDF.evaluate( 1.0, 1.0, 1.0 ), TOLERANCE );
        assertEquals( 0.0497870684, GammaDistribution.PDF.evaluate( 3.0, 1.0, 1.0 ), TOLERANCE );
        assertEquals( 0.1493612051, GammaDistribution.PDF.evaluate( 3.0, 2.0, 1.0 ), TOLERANCE );
        assertEquals( 0.1673476201, GammaDistribution.PDF.evaluate( 3.0, 2.0, 2.0 ), TOLERANCE );
        assertEquals( 0.0396463705, GammaDistribution.PDF.evaluate( 3.0, 4.5, 2.0 ), TOLERANCE );
        assertEquals( 0.0871725152, GammaDistribution.PDF.evaluate( 5.0, 4.5, 2.0 ), TOLERANCE );

    }

    /**
     * Test of getShape method, of class gov.sandia.cognition.learning.util.statistics.distribution.GammaDistribution.
     */
    public void testGetShape()
    {
        System.out.println( "getShape" );

        double shape = RANDOM.nextDouble() * 5.0;
        double scale = RANDOM.nextDouble() * 5.0;
        GammaDistribution.PDF instance = new GammaDistribution.PDF( shape, scale );
        assertEquals( shape, instance.getShape() );
    }

    /**
     * Test of setShape method, of class gov.sandia.cognition.learning.util.statistics.distribution.GammaDistribution.
     */
    public void testSetShape()
    {
        System.out.println( "setShape" );

        GammaDistribution.PDF instance = this.createInstance().getProbabilityFunction();
        assertTrue( instance.getShape() > 0.0 );
        double s2 = instance.getShape() + 1.0;
        instance.setShape( s2 );
        assertEquals( s2, instance.getShape() );

        try
        {
            instance.setShape( 0.0 );
            fail( "Shape must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getScale method, of class gov.sandia.cognition.learning.util.statistics.distribution.GammaDistribution.
     */
    public void testGetScale()
    {
        System.out.println( "getScale" );

        double shape = RANDOM.nextDouble() * 5.0;
        double scale = RANDOM.nextDouble() * 5.0;
        GammaDistribution.PDF instance = new GammaDistribution.PDF( shape, scale );
        assertEquals( scale, instance.getScale() );
    }

    /**
     * Test of setScale method, of class gov.sandia.cognition.learning.util.statistics.distribution.GammaDistribution.
     */
    public void testSetScale()
    {
        System.out.println( "setScale" );

        GammaDistribution instance = this.createInstance();
        assertTrue( instance.getScale() > 0.0 );
        double s2 = instance.getScale() + 1.0;
        instance.setScale( s2 );
        assertEquals( s2, instance.getScale() );

        try
        {
            instance.setScale( 0.0 );
            fail( "Scale must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF.evaluate" );

        // I got these values from octave's gamma_cdf() function
        // However, octave's second parameter is INVERTED from my expecatation
        // Thus, GammaDistribution.CDF.evaluate( x, dof, theta ) == gamma_cdf(x, dof, 1/theta)
        assertEquals( 0.6321205588, GammaDistribution.CDF.evaluate( 1.0, 1.0, 1.0 ), TOLERANCE );
        assertEquals( 0.8008517625, GammaDistribution.CDF.evaluate( 3.0, 2.0, 1.0 ), TOLERANCE );
        assertEquals( 0.0549772417, GammaDistribution.CDF.evaluate( 1.5, 2.0, 4.0 ), TOLERANCE );
        assertEquals( 0.0231152878, GammaDistribution.CDF.evaluate( 1.5, 3.0, 2.5 ), TOLERANCE );

    }

    @Override
    public void testKnownConvertToVector()
    {
        GammaDistribution cdf = this.createInstance();
        Vector x = cdf.convertToVector();
        assertEquals( 2, x.getDimensionality() );
        assertEquals( cdf.getShape(), x.getElement( 0 ) );
        assertEquals( cdf.getScale(), x.getElement( 1 ) );
    }

    @Override
    public void testPDFConstructors()
    {
        System.out.println( "PDF Constructors" );

        GammaDistribution.PDF f = new GammaDistribution.PDF();
        assertEquals( GammaDistribution.DEFAULT_SCALE, f.getScale() );
        assertEquals( GammaDistribution.DEFAULT_SHAPE, f.getShape() );

        double shape = Math.abs( RANDOM.nextDouble() );
        double scale = Math.abs( RANDOM.nextDouble() );
        f = new GammaDistribution.PDF( shape, scale );
        assertEquals( shape, f.getShape() );
        assertEquals( scale, f.getScale() );

        GammaDistribution.PDF f2 = new GammaDistribution.PDF( f );
        assertEquals( f.getShape(), f2.getShape() );
        assertEquals( f.getScale(), f2.getScale() );
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        GammaDistribution f = new GammaDistribution();
        assertEquals( GammaDistribution.DEFAULT_SCALE, f.getScale() );
        assertEquals( GammaDistribution.DEFAULT_SHAPE, f.getShape() );

        double shape = Math.abs( RANDOM.nextDouble() );
        double scale = Math.abs( RANDOM.nextDouble() );
        f = new GammaDistribution( shape, scale );
        assertEquals( shape, f.getShape() );
        assertEquals( scale, f.getScale() );

        GammaDistribution f2 = new GammaDistribution( f );
        assertEquals( f.getShape(), f2.getShape() );
        assertEquals( f.getScale(), f2.getScale() );
        
    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );

        GammaDistribution.CDF f = new GammaDistribution.CDF();
        assertEquals( GammaDistribution.DEFAULT_SCALE, f.getScale() );
        assertEquals( GammaDistribution.DEFAULT_SHAPE, f.getShape() );

        double shape = Math.abs( RANDOM.nextDouble() );
        double scale = Math.abs( RANDOM.nextDouble() );
        f = new GammaDistribution.CDF( shape, scale );
        assertEquals( shape, f.getShape() );
        assertEquals( scale, f.getScale() );

        GammaDistribution.CDF f2 = new GammaDistribution.CDF( f );
        assertEquals( f.getShape(), f2.getShape() );
        assertEquals( f.getScale(), f2.getScale() );
    }
    
    /**
     * toString
     */
    public void testToString()
    {
        System.out.println( "toString" );
        GammaDistribution gamma = this.createInstance();
        System.out.println( "Gamma: " + gamma.toString() );
        assertNotNull( gamma.toString() );
    }

    /**
     * Learner
     */
    public void testLearner()
    {
        System.out.println( "Learner" );

        GammaDistribution.MomentMatchingEstimator learner =
            new GammaDistribution.MomentMatchingEstimator();
        this.distributionEstimatorTest(learner);
    }

    /**
     * Weighted learner
     */
    public void testWeightedLearner()
    {
        System.out.println( "Weighted Learner" );

        GammaDistribution.WeightedMomentMatchingEstimator learner =
            new GammaDistribution.WeightedMomentMatchingEstimator();
        this.weightedDistributionEstimatorTest(learner);
    }

}

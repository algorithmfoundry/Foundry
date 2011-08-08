/*
 * File:                ChiSquareDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 7, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.SmoothUnivariateDistributionTestHarness;
import java.util.Random;

/**
 * Tests ChiSquareDistribution
 * @author Kevin R. Dixon
 */
public class ChiSquareDistributionTest
    extends SmoothUnivariateDistributionTestHarness
{

    /**
     * Constructor
     * @param testName name
     */
    public ChiSquareDistributionTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public ChiSquareDistribution createInstance()
    {
        double dof = RANDOM.nextDouble() * 5 + 1;
        return new ChiSquareDistribution( dof );
    }
    
    @Override
    public void testPDFKnownValues()
    {
        System.out.println( "PDF.evaluate" );

        // I got these values from the chisquare_pdf() function in octave
        assertEquals( 0.2419707245, ChiSquareDistribution.PDF.evaluate( 1.0, 1.0 ), TOLERANCE );
        assertEquals( 0.3032653299, ChiSquareDistribution.PDF.evaluate( 1.0, 2.0 ), TOLERANCE );
        assertEquals( 0.4756147123, ChiSquareDistribution.PDF.evaluate( 0.1, 2.0 ), TOLERANCE );
        assertEquals( 0.0040001298, ChiSquareDistribution.PDF.evaluate( 0.1, 5.0 ), TOLERANCE );
        assertEquals( 0.0668009429, ChiSquareDistribution.PDF.evaluate( 5.0, 10.0 ), TOLERANCE );

        try
        {
            ChiSquareDistribution.PDF.evaluate( RANDOM.nextDouble(), 0.0 );
            fail( "DOF must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of getDegreesOfFreedom method, of class gov.sandia.cognition.learning.util.statistics.distribution.ChiSquareDistribution.
     */
    public void testGetDegreesOfFreedom()
    {
        System.out.println( "getDegreesOfFreedom" );

        double dof = RANDOM.nextDouble() * 10.0;
        ChiSquareDistribution.PDF instance = new ChiSquareDistribution.PDF( dof );
        assertEquals( dof, instance.getDegreesOfFreedom() );
    }

    /**
     * Test of setDegreesOfFreedom method, of class gov.sandia.cognition.learning.util.statistics.distribution.ChiSquareDistribution.
     */
    public void testSetDegreesOfFreedom()
    {
        System.out.println( "setDegreesOfFreedom" );

        double dof = RANDOM.nextDouble() * 10.0;
        ChiSquareDistribution.PDF instance = new ChiSquareDistribution.PDF( dof );
        assertEquals( dof, instance.getDegreesOfFreedom() );

        dof += RANDOM.nextDouble();
        instance.setDegreesOfFreedom( dof );
        assertEquals( dof, instance.getDegreesOfFreedom() );

        try
        {
            instance.setDegreesOfFreedom( 0.0 );
            fail( "DOF must be > 0.0" );
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

        // Checked using the chisquare_cdf() function in octave
        assertEquals( 0.6826894921, ChiSquareDistribution.CDF.evaluate( 1.0, 1.0 ), TOLERANCE );
        assertEquals( 0.8427007929, ChiSquareDistribution.CDF.evaluate( 2.0, 1.0 ), TOLERANCE );
        assertEquals( 0.9746526813, ChiSquareDistribution.CDF.evaluate( 5.0, 1.0 ), TOLERANCE );

        assertEquals( 0.3934693403, ChiSquareDistribution.CDF.evaluate( 1.0, 2.0 ), TOLERANCE );
        assertEquals( 0.6321205588, ChiSquareDistribution.CDF.evaluate( 2.0, 2.0 ), TOLERANCE );
        assertEquals( 0.9179150014, ChiSquareDistribution.CDF.evaluate( 5.0, 2.0 ), TOLERANCE );

        assertEquals( 0.0374342268, ChiSquareDistribution.CDF.evaluate( 1.0, 5.0 ), TOLERANCE );
        assertEquals( 0.1508549639, ChiSquareDistribution.CDF.evaluate( 2.0, 5.0 ), TOLERANCE );
        assertEquals( 0.5841198130, ChiSquareDistribution.CDF.evaluate( 5.0, 5.0 ), TOLERANCE );

    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "CDF.convertToVector" );
        
        ChiSquareDistribution instance = this.createInstance();
        Vector x = instance.convertToVector();
        assertEquals( 1, x.getDimensionality() );
        assertEquals( instance.getDegreesOfFreedom(), x.getElement( 0 ) );
    }

    @Override
    public void testPDFConstructors()
    {
        System.out.println( "CDF Constructor" );
        ChiSquareDistribution.PDF instance = new ChiSquareDistribution.PDF();
        assertEquals( ChiSquareDistribution.DEFAULT_DEGREES_OF_FREEDOM, instance.getDegreesOfFreedom() );

        double dof = Math.abs(RANDOM.nextGaussian());
        instance = new ChiSquareDistribution.PDF( dof );
        assertEquals( dof, instance.getDegreesOfFreedom() );

        ChiSquareDistribution.PDF d2 = new ChiSquareDistribution.PDF( instance );
        assertEquals( instance.getDegreesOfFreedom(), d2.getDegreesOfFreedom() );
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructor" );
        ChiSquareDistribution instance = new ChiSquareDistribution();
        assertEquals( ChiSquareDistribution.DEFAULT_DEGREES_OF_FREEDOM, instance.getDegreesOfFreedom() );

        double dof = Math.abs(RANDOM.nextGaussian());
        instance = new ChiSquareDistribution( dof );
        assertEquals( dof, instance.getDegreesOfFreedom() );

        ChiSquareDistribution d2 = new ChiSquareDistribution( instance );
        assertEquals( instance.getDegreesOfFreedom(), d2.getDegreesOfFreedom() );
    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructor" );
        ChiSquareDistribution.CDF instance = new ChiSquareDistribution.CDF();
        assertEquals( ChiSquareDistribution.DEFAULT_DEGREES_OF_FREEDOM, instance.getDegreesOfFreedom() );

        double dof = Math.abs(RANDOM.nextGaussian());
        instance = new ChiSquareDistribution.CDF( dof );
        assertEquals( dof, instance.getDegreesOfFreedom() );

        ChiSquareDistribution.CDF d2 = new ChiSquareDistribution.CDF( instance );
        assertEquals( instance.getDegreesOfFreedom(), d2.getDegreesOfFreedom() );
    }

    @Override
    public void testDistributionGetVariance()
    {
        RANDOM = new Random(2);
        super.testDistributionGetVariance();
    }

}

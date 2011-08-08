/*
 * File:                KolmogorovDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 14, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.statistics.UnivariateDistributionTestHarness;

/**
 *
 * @author Kevin R. Dixon
 */
public class KolmogorovDistributionTest
    extends UnivariateDistributionTestHarness<Double>
{

    /**
     * Constructor
     * @param testName name
     */
    public KolmogorovDistributionTest(
        String testName )
    {
        super( testName );
    }

    public KolmogorovDistribution createInstance()
    {
        return new KolmogorovDistribution();
    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF.evaluate" );

        assertEquals( 0.0, KolmogorovDistribution.CDF.evaluate( 0.0 ) );
        assertEquals( 0.0, KolmogorovDistribution.CDF.evaluate( -RANDOM.nextDouble() ) );
        assertEquals( 1.0, KolmogorovDistribution.CDF.evaluate( Double.POSITIVE_INFINITY ) );

        // These values are from the kolmogorov_smirnov_cdf() function in octave
        assertEquals( 0.036055, KolmogorovDistribution.CDF.evaluate( 0.5 ), TOLERANCE );
        assertEquals( 0.730000, KolmogorovDistribution.CDF.evaluate( 1.0 ), TOLERANCE );
        assertEquals( 0.977782, KolmogorovDistribution.CDF.evaluate( 1.5 ), TOLERANCE );
    }

    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        KolmogorovDistribution f = new KolmogorovDistribution();
        assertNotNull( f );
    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );

        KolmogorovDistribution.CDF f = new KolmogorovDistribution.CDF();
        assertNotNull( f );
    }

}

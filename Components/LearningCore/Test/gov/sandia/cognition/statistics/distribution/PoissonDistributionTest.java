/*
 * File:                PoissonDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 14, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.collection.IntegerSpan;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.ClosedFormDiscreteUnivariateDistributionTestHarness;
import java.util.Collection;

/**
 * Unit tests for PoissonDistributionTest.
 *
 * @author krdixon
 */
public class PoissonDistributionTest
    extends ClosedFormDiscreteUnivariateDistributionTestHarness<Number>
{

    /**
     * Tests for class PoissonDistributionTest.
     * @param testName Name of the test.
     */
    public PoissonDistributionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test constructors
     */
    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );

        PoissonDistribution f = new PoissonDistribution();
        assertEquals( PoissonDistribution.DEFAULT_RATE, f.getRate() );

        double v = RANDOM.nextDouble() * 10.0;
        f = new PoissonDistribution( v );
        assertEquals( v, f.getRate() );

        PoissonDistribution g = new PoissonDistribution( f );
        assertNotSame( f, g );
        assertEquals( f.getRate(), g.getRate() );
    }

    /**
     * Test PMF constructors
     */
    public void testPMFConstructors()
    {
        System.out.println( "PMF.Constructors" );

        PoissonDistribution.PMF f = new PoissonDistribution.PMF();
        assertEquals( PoissonDistribution.DEFAULT_RATE, f.getRate() );

        double v = RANDOM.nextDouble() * 10.0;
        f = new PoissonDistribution.PMF( v );
        assertEquals( v, f.getRate() );

        PoissonDistribution.PMF g = new PoissonDistribution.PMF( f );
        assertNotSame( f, g );
        assertEquals( f.getRate(), g.getRate() );
    }

    /**
     * Test CDF constructors
     */
    public void testCDFConstructors()
    {
        System.out.println( "CDF.Constructors" );

        PoissonDistribution.CDF f = new PoissonDistribution.CDF();
        assertEquals( PoissonDistribution.DEFAULT_RATE, f.getRate() );

        double v = RANDOM.nextDouble() * 10.0;
        f = new PoissonDistribution.CDF( v );
        assertEquals( v, f.getRate() );

        PoissonDistribution.CDF g = new PoissonDistribution.CDF( f );
        assertNotSame( f, g );
        assertEquals( f.getRate(), g.getRate() );
    }



    /**
     * Test of getDomain method, of class PoissonDistribution.
     */
//    public void testGetDomain()
//    {
//        System.out.println("getDomain");
//
//        for( int n = 0; n < NUM_SAMPLES; n++ )
//        {
//            double v = RANDOM.nextDouble() * 10.0;
//            PoissonDistribution.PMF instance = new PoissonDistribution.PMF( v );
//            Collection<? extends Number> support = instance.getDomain();
//            double sum = 0.0;
//            for( Number x : support )
//            {
//                sum += instance.evaluate(x);
//            }
//            assertEquals( "Failed when rate = " + v, 1.0, sum, TOLERANCE );
//        }
//
//
//
//    }

    /**
     * Test of getRate method, of class PoissonDistribution.
     */
    public void testGetRate()
    {
        System.out.println("getRate");
        PoissonDistribution instance = this.createInstance();
        assertTrue( instance.getRate() > 0.0 );
    }

    /**
     * Test of setRate method, of class PoissonDistribution.
     */
    public void testSetRate()
    {
        System.out.println("setRate");
        PoissonDistribution instance = this.createInstance();
        assertTrue( instance.getRate() > 0.0 );

        double v = RANDOM.nextDouble() * 10.0;
        instance.setRate(v);
        assertEquals( v, instance.getRate() );

        try
        {
            instance.setRate(0.0);
            fail( "Rate must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    @Override
    public PoissonDistribution createInstance()
    {
        return new PoissonDistribution( RANDOM.nextDouble() * 10.0 );
    }

    @Override
    public void testPMFKnownValues()
    {
        System.out.println( "PMF.knownValues" );

        // http://stattrek.com/Tables/Poisson.aspx
        PoissonDistribution.PMF f = new PoissonDistribution.PMF( 2.0 );
        assertEquals( 0.1804470443, f.evaluate(3.0), TOLERANCE );
        assertEquals( 0.135335283236613, f.evaluate(0.0), TOLERANCE );
        assertEquals( 0.0, f.evaluate(-1.0) );

        f.setRate(10.0);
        assertEquals( 0.0189166374010354, f.evaluate(4.0), TOLERANCE );

    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "CDF.convertToVector" );

        double v = RANDOM.nextDouble()*10.0;
        PoissonDistribution.CDF f = new PoissonDistribution.CDF( v );
        Vector p = f.convertToVector();
        assertEquals( 1, p.getDimensionality() );
        assertEquals( v, p.getElement(0) );

    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF.knownValues" );

        // http://stattrek.com/Tables/Poisson.aspx
        PoissonDistribution.CDF f = new PoissonDistribution.CDF( 2.0 );
        assertEquals( 0.857123460498547, f.evaluate(3.0), TOLERANCE );
        assertEquals( 0.135335283236613, f.evaluate(0.0), TOLERANCE );
        assertEquals( 0.0, f.evaluate(-1.0) );

        f.setRate(10.0);
        assertEquals( 0.0292526880769611, f.evaluate(4.0), TOLERANCE );
    }

    @Override
    public void testKnownGetDomain()
    {
        System.out.println( "Known Domain" );

        double rate = Math.abs( RANDOM.nextGaussian() ) + 10;
        PoissonDistribution d = new PoissonDistribution( rate );
        IntegerSpan domain = d.getDomain();
        assertTrue( domain.size() > rate*10 );
        assertEquals( 0, CollectionUtil.getFirst(domain).intValue() );
    }

    @Override
    public void testCDFSample_Random_int()
    {
//       RANDOM = new Random(2);
//        super.testCDFSample_Random_int();
    }

}

/*
 * File:                BinomialDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.ClosedFormDiscreteScalarDistributionTestHarness;

/**
 *
 * @author Kevin R. Dixon
 */
public class BinomialDistributionTest
    extends ClosedFormDiscreteScalarDistributionTestHarness<Number>
{

    /**
     * Constructor
     * @param testName Tests
     */
    public BinomialDistributionTest(
        String testName )
    {
        super( testName );
    }

    @Override
    public BinomialDistribution createInstance()
    {
        double p = RANDOM.nextDouble();
        int N = RANDOM.nextInt( 10 ) + 5;
        return new BinomialDistribution( N, p );
    }

    /**
     * Default constructor
     */
    public void testDefaultConstructor()
    {
        System.out.println( "Default constructor" );
        BinomialDistribution bi = new BinomialDistribution.PMF();
        assertEquals( BinomialDistribution.DEFAULT_P, bi.getP() );
        assertEquals( BinomialDistribution.DEFAULT_N, bi.getN() );

        bi = new BinomialDistribution.CDF();
        assertEquals( BinomialDistribution.DEFAULT_P, bi.getP() );
        assertEquals( BinomialDistribution.DEFAULT_N, bi.getN() );

    }

    @Override
    public void testPMFKnownValues()
    {
        System.out.println( "PMF.evaluate" );

        // I got these values from octave's binomial_pdf() function
        assertEquals( 0.4782969000, BinomialDistribution.PMF.evaluate( 7, 0, 0.1 ), TOLERANCE );
        assertEquals( 0.0025515000, BinomialDistribution.PMF.evaluate( 7, 4, 0.1 ), TOLERANCE );
        assertEquals( 0.1384150213, BinomialDistribution.PMF.evaluate( 25, 4, 0.1 ), TOLERANCE );
        assertEquals( 0.0572314020, BinomialDistribution.PMF.evaluate( 25, 4, 0.3 ), TOLERANCE );

    }

    @Override
    public void testCDFKnownValues()
    {
        System.out.println( "CDF.evaluate" );

        BinomialDistribution.CDF cdf = new BinomialDistribution.CDF( 10, 0.25 );

        // I got these numbers from octave's function binomial_cdf(x,N,p)
        assertEquals( 0.244025, cdf.evaluate( 1.0 ), TOLERANCE  );
        assertEquals( 0.244025, cdf.evaluate( 1.5 ), TOLERANCE  );

        assertEquals( 0.980272, cdf.evaluate( 5.0 ), TOLERANCE  );
        assertEquals( 0.980272, cdf.evaluate( 5.9 ), TOLERANCE  );

        assertEquals( 0.999970, cdf.evaluate( 8.0 ), TOLERANCE  );

        assertEquals( 1.0, cdf.evaluate( 10.0 ), TOLERANCE  );
    }

    /**
     * Test of getN method, of class gov.sandia.cognition.learning.util.statistics.BinomialDistribution.CumulativeDistribution.
     */
    public void testGetN()
    {
        System.out.println( "getN" );

        int N = 10;
        double p = RANDOM.nextDouble();
        BinomialDistribution.PMF instance = new BinomialDistribution.PMF( N, p );

        assertEquals( N, instance.getN() );

    }

    /**
     * Test of setN method, of class gov.sandia.cognition.learning.util.statistics.BinomialDistribution.CumulativeDistribution.
     */
    public void testSetN()
    {
        System.out.println( "setN" );

        int N = 10;
        double p = RANDOM.nextDouble();
        BinomialDistribution.PMF instance = new BinomialDistribution.PMF( N, p );

        assertEquals( N, instance.getN() );

        int N2 = N + 1;
        instance.setN( N2 );
        assertEquals( N2, instance.getN() );

        instance.setN( 1 );

        try
        {
            instance.setN( 0 );
            fail( "N must be > 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getP method, of class gov.sandia.cognition.learning.util.statistics.BinomialDistribution.CumulativeDistribution.
     */
    public void testGetP()
    {
        System.out.println( "getP" );

        int N = 10;
        double p = RANDOM.nextDouble();
        BinomialDistribution.CDF instance = new BinomialDistribution.CDF( N, p );

        assertEquals( p, instance.getP() );
    }

    /**
     * Test of setP method, of class gov.sandia.cognition.learning.util.statistics.BinomialDistribution.CumulativeDistribution.
     */
    public void testSetP()
    {
        System.out.println( "setP" );

        int N = 10;
        double p = RANDOM.nextDouble();
        BinomialDistribution.CDF instance = new BinomialDistribution.CDF( N, p );

        assertEquals( p, instance.getP() );

        double p2 = p / 2.0;
        instance.setP( p2 );
        assertEquals( p2, instance.getP() );


        instance.setP( 1.0 );
        instance.setP( 0.0 );

        try
        {
            instance.setP( 1.1 );
            fail( "P must be <= 1.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setP( -1.1 );
            fail( "P must be >= 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getDomain method, of class BinomialDistribution.
     */
    @Override
    public void testKnownGetDomain()
    {
        System.out.println( "getDomain" );

        int N = 10;
        BinomialDistribution.PMF b = new BinomialDistribution.PMF( N, RANDOM.nextDouble() );
        int index = 0;
        for (Number v : b.getDomain())
        {
            assertEquals( index, v.intValue() );
            index++;
        }

        assertEquals( N + 1, index );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "CDF.convertToVector" );
        
        BinomialDistribution cdf = this.createInstance();
        Vector x = cdf.convertToVector();
        assertEquals( 2, x.getDimensionality() );
        assertEquals( (double) cdf.getN(), x.getElement( 0 ) );
        assertEquals( cdf.getP(), x.getElement( 1 ) );
    }

    @Override
    public void testCDFConvertFromVector()
    {
        BinomialDistribution cdf = this.createInstance();
        
        Vector x1 = cdf.convertToVector();
        assertNotNull( x1 );
        int N = x1.getDimensionality();
        
        // Create a new parameterization of the CDF
        // (try using positive parameters as some distributions need
        // positive parameters)
        final double r = 2.0;
        Vector y1 = VectorFactory.getDefault().copyValues( RANDOM.nextInt( 10 ) + 1, RANDOM.nextDouble() );
        cdf.convertFromVector( y1 );
        Vector y2 = cdf.convertToVector();
        assertNotNull( y2 );
        assertNotSame( y1, y2 );
        assertEquals( y1, y2 );
        
        // Convert back to the original parameterization
        cdf.convertFromVector( x1 );
        Vector x2 = cdf.convertToVector();
        assertNotNull( x2 );
        assertNotSame( x1, x2 );
        assertEquals( x1, x2 );
        
        try
        {
            cdf.convertFromVector( null );
            fail( "Cannot convert from null Vector" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        try
        {
            Vector z1 = VectorFactory.getDefault().createUniformRandom( N-1, r, r, RANDOM );
            cdf.convertFromVector( z1 );
            fail( "Cannot convert from a N-1 dimension Vector!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        try
        {
            Vector z1 = VectorFactory.getDefault().createUniformRandom( N+1, r, r, RANDOM );
            cdf.convertFromVector( z1 );
            fail( "Cannot convert from a N+1 dimension Vector!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    @Override
    public void testPMFConstructors()
    {
        System.out.println( "PMF Constructors" );
        BinomialDistribution.PMF instance = new BinomialDistribution.PMF();
        assertEquals( BinomialDistribution.DEFAULT_P, instance.getP() );
        assertEquals( BinomialDistribution.DEFAULT_N, instance.getN() );

        double p = RANDOM.nextDouble();
        int N = RANDOM.nextInt( 10 ) + 1;
        instance = new BinomialDistribution.PMF( N, p );
        assertEquals( N, instance.getN() );
        assertEquals( p, instance.getP() );

        BinomialDistribution.PMF d2 = new BinomialDistribution.PMF( instance );
        assertEquals( instance.getN(), d2.getN() );
        assertEquals( instance.getP(), d2.getP() );
    }
    
    @Override
    public void testDistributionConstructors()
    {
        System.out.println( "Constructors" );
        BinomialDistribution instance = new BinomialDistribution();
        assertEquals( BinomialDistribution.DEFAULT_P, instance.getP() );
        assertEquals( BinomialDistribution.DEFAULT_N, instance.getN() );

        double p = RANDOM.nextDouble();
        int N = RANDOM.nextInt( 10 ) + 1;
        instance = new BinomialDistribution( N, p );
        assertEquals( N, instance.getN() );
        assertEquals( p, instance.getP() );

        BinomialDistribution d2 = new BinomialDistribution( instance );
        assertEquals( instance.getN(), d2.getN() );
        assertEquals( instance.getP(), d2.getP() );

    }

    @Override
    public void testCDFConstructors()
    {
        System.out.println( "CDF Constructors" );
        BinomialDistribution.CDF instance = new BinomialDistribution.CDF();
        assertEquals( BinomialDistribution.DEFAULT_P, instance.getP() );
        assertEquals( BinomialDistribution.DEFAULT_N, instance.getN() );

        double p = RANDOM.nextDouble();
        int N = RANDOM.nextInt( 10 ) + 1;
        instance = new BinomialDistribution.CDF( N, p );
        assertEquals( N, instance.getN() );
        assertEquals( p, instance.getP() );

        BinomialDistribution.CDF d2 = new BinomialDistribution.CDF( instance );
        assertEquals( instance.getN(), d2.getN() );
        assertEquals( instance.getP(), d2.getP() );
    }
    
}

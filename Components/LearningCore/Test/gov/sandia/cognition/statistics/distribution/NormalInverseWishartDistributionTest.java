/*
 * File:                NormalInverseWishartDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 25, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.MultivariateClosedFormComputableDistributionTestHarness;

/**
 * Unit tests for NormalInverseWishartDistributionTest.
 *
 * @author krdixon
 */
public class NormalInverseWishartDistributionTest
    extends MultivariateClosedFormComputableDistributionTestHarness<Matrix>
{

    /**
     * Tests for class NormalInverseWishartDistributionTest.
     * @param testName Name of the test.
     */
    public NormalInverseWishartDistributionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class NormalInverseWishartDistributionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        NormalInverseWishartDistribution instance =
            new NormalInverseWishartDistribution();
        assertEquals( NormalInverseWishartDistribution.DEFAULT_DIMENSIONALITY, instance.getInputDimensionality() );
        assertEquals( NormalInverseWishartDistribution.DEFAULT_COVARIANCE_DIVISOR, instance.getCovarianceDivisor() );

        int dim = RANDOM.nextInt(10) + 2;
        double divisor = RANDOM.nextDouble();
        instance = new NormalInverseWishartDistribution( dim, divisor );
        assertEquals( dim, instance.getInputDimensionality() );
        assertEquals( divisor, instance.getCovarianceDivisor() );

        MultivariateGaussian g = instance.getGaussian();
        InverseWishartDistribution iw = instance.getInverseWishart();
        divisor = instance.getCovarianceDivisor();
        instance = new NormalInverseWishartDistribution( g, iw, divisor );
        assertSame( g, instance.getGaussian() );
        assertSame( iw, instance.getInverseWishart() );
        assertEquals( divisor, instance.getCovarianceDivisor() );

        NormalInverseWishartDistribution i2 =
            new NormalInverseWishartDistribution( instance );
        assertEquals( instance.convertToVector(), i2.convertToVector() );

    }
    
    @Override
    public void testProbabilityFunctionConstructors()
    {
        System.out.println( "PDF Constructor" );
        
        NormalInverseWishartDistribution.PDF instance =
            new NormalInverseWishartDistribution.PDF();
        assertEquals( NormalInverseWishartDistribution.DEFAULT_DIMENSIONALITY, instance.getInputDimensionality() );
        assertEquals( NormalInverseWishartDistribution.DEFAULT_COVARIANCE_DIVISOR, instance.getCovarianceDivisor() );

        int dim = RANDOM.nextInt(10) + 2;
        double divisor = RANDOM.nextDouble();
        instance = new NormalInverseWishartDistribution.PDF( dim, divisor );
        assertEquals( dim, instance.getInputDimensionality() );
        assertEquals( divisor, instance.getCovarianceDivisor() );

        MultivariateGaussian g = instance.getGaussian();
        InverseWishartDistribution iw = instance.getInverseWishart();
        divisor = instance.getCovarianceDivisor();
        instance = new NormalInverseWishartDistribution.PDF( g, iw, divisor );
        assertSame( g, instance.getGaussian() );
        assertSame( iw, instance.getInverseWishart() );
        assertEquals( divisor, instance.getCovarianceDivisor() );

        NormalInverseWishartDistribution.PDF i2 =
            new NormalInverseWishartDistribution.PDF( instance );
        assertEquals( instance.convertToVector(), i2.convertToVector() );
        
    }


    /**
     * Test of getGaussian method, of class NormalInverseWishartDistribution.
     */
    public void testGetGaussian()
    {
        System.out.println("getGaussian");
        NormalInverseWishartDistribution instance = this.createInstance();
        MultivariateGaussian g = instance.getGaussian();
        assertNotNull( g );
    }

    /**
     * Test of setGaussian method, of class NormalInverseWishartDistribution.
     */
    public void testSetGaussian()
    {
        System.out.println("setGaussian");
        NormalInverseWishartDistribution instance = this.createInstance();
        MultivariateGaussian g = instance.getGaussian();
        assertNotNull( g );
        instance.setGaussian(null);
        assertNull( instance.getGaussian() );
        instance.setGaussian(g);
        assertSame( g, instance.getGaussian() );
    }

    /**
     * Test of getInverseWishart method, of class NormalInverseWishartDistribution.
     */
    public void testGetInverseWishart()
    {
        System.out.println("getInverseWishart");
        NormalInverseWishartDistribution instance = this.createInstance();
        InverseWishartDistribution iw = instance.getInverseWishart();
        assertNotNull( iw );
    }

    /**
     * Test of setInverseWishart method, of class NormalInverseWishartDistribution.
     */
    public void testSetInverseWishart()
    {
        System.out.println("setInverseWishart");
        NormalInverseWishartDistribution instance = this.createInstance();
        InverseWishartDistribution iw = instance.getInverseWishart();
        assertNotNull( iw );
        instance.setInverseWishart(null);
        assertNull( instance.getInverseWishart() );
        instance.setInverseWishart(iw);
        assertSame( iw, instance.getInverseWishart() );
    }

    /**
     * Test of getCovarianceDivisor method, of class NormalInverseWishartDistribution.
     */
    public void testGetCovarianceDivisor()
    {
        System.out.println("getCovarianceDivisor");
        NormalInverseWishartDistribution instance = this.createInstance();
        assertTrue( instance.getCovarianceDivisor() > 0.0 );
    }

    /**
     * Test of setCovarianceDivisor method, of class NormalInverseWishartDistribution.
     */
    public void testSetCovarianceDivisor()
    {
        System.out.println("setCovarianceDivisor");
        double covarianceDivisor = RANDOM.nextDouble();
        NormalInverseWishartDistribution instance = this.createInstance();
        instance.setCovarianceDivisor(covarianceDivisor);
        assertEquals( covarianceDivisor, instance.getCovarianceDivisor() );

        try
        {
            instance.setCovarianceDivisor(0.0);
            fail( "covarianceDivisor must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    /**
     * Test of getInputDimensionality method, of class NormalInverseWishartDistribution.
     */
    public void testGetInputDimensionality()
    {
        System.out.println("getInputDimensionality");
        NormalInverseWishartDistribution instance = this.createInstance();
        assertEquals( instance.getGaussian().getInputDimensionality(), instance.getInputDimensionality() );
    }

    @Override
    public NormalInverseWishartDistribution createInstance()
    {
        return new NormalInverseWishartDistribution();
    }

    @Override
    public void testProbabilityFunctionKnownValues()
    {
        System.out.println( "PDF Known Values" );

        NormalInverseWishartDistribution.PDF pdf = this.createInstance().getProbabilityFunction();
        Matrix xmean = pdf.getMean();
        double pmean = pdf.evaluate(xmean);
        assertEquals( 0.00206457531653, pmean, TOLERANCE );
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        NormalInverseWishartDistribution instance = this.createInstance();
        final int d = instance.getInputDimensionality();
        Vector p = instance.convertToVector();
        assertEquals( 1+d + 1+d*d, p.getDimensionality() );
        assertEquals( instance.getCovarianceDivisor(), p.getElement(0) );
        assertEquals( instance.getGaussian().getMean(), p.subVector(1,d) );
        assertEquals( instance.getInverseWishart().getDegreesOfFreedom(), (int) p.getElement(d+1) );
    }

    @Override
    public void testKnownValues()
    {
    }

    @Override
    public void testGetMean()
    {
        double temp = TOLERANCE;
        TOLERANCE = 1.0;
        super.testGetMean();
        TOLERANCE = temp;
    }

}

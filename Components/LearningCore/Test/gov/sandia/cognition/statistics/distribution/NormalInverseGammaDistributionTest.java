/*
 * File:                NormalInverseGammaDistributionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 16, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.math.MultivariateStatisticsUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.MultivariateClosedFormComputableDistributionTestHarness;
import gov.sandia.cognition.util.Pair;
import java.util.ArrayList;

/**
 * Unit tests for NormalInverseGammaDistributionTest.
 *
 * @author krdixon
 */
public class NormalInverseGammaDistributionTest
    extends MultivariateClosedFormComputableDistributionTestHarness<Vector>
{

    /**
     * Tests for class NormalInverseGammaDistributionTest.
     * @param testName Name of the test.
     */
    public NormalInverseGammaDistributionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class NormalInverseGammaDistributionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        NormalInverseGammaDistribution instance =
            new NormalInverseGammaDistribution();
        assertEquals( NormalInverseGammaDistribution.DEFAULT_LOCATION, instance.getLocation() );
        assertEquals( NormalInverseGammaDistribution.DEFAULT_PRECISION, instance.getPrecision() );
        assertEquals( NormalInverseGammaDistribution.DEFAULT_SHAPE, instance.getShape() );
        assertEquals( NormalInverseGammaDistribution.DEFAULT_SCALE, instance.getScale() );

        double location = RANDOM.nextGaussian();
        double precision = RANDOM.nextDouble();
        double shape = RANDOM.nextDouble();
        double scale = RANDOM.nextDouble();

        instance = new NormalInverseGammaDistribution(
            location, precision,shape, scale);
        assertEquals( location, instance.getLocation() );
        assertEquals( precision, instance.getPrecision() );
        assertEquals( shape, instance.getShape() );
        assertEquals( scale, instance.getScale() );

        NormalInverseGammaDistribution i2 =
            new NormalInverseGammaDistribution( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getLocation(), i2.getLocation() );
        assertEquals( instance.getPrecision(), i2.getPrecision() );
        assertEquals( instance.getShape(), i2.getShape() );
        assertEquals( instance.getScale(), i2.getScale() );

    }


    @Override
    public void testProbabilityFunctionConstructors()
    {
        System.out.println( "PDF.Constructors" );

        NormalInverseGammaDistribution.PDF instance =
            new NormalInverseGammaDistribution.PDF();
        assertEquals( NormalInverseGammaDistribution.DEFAULT_LOCATION, instance.getLocation() );
        assertEquals( NormalInverseGammaDistribution.DEFAULT_PRECISION, instance.getPrecision() );
        assertEquals( NormalInverseGammaDistribution.DEFAULT_SHAPE, instance.getShape() );
        assertEquals( NormalInverseGammaDistribution.DEFAULT_SCALE, instance.getScale() );

        double location = RANDOM.nextGaussian();
        double precision = RANDOM.nextDouble();
        double shape = RANDOM.nextDouble();
        double scale = RANDOM.nextDouble();

        instance = new NormalInverseGammaDistribution.PDF(
            location, precision,shape, scale);
        assertEquals( location, instance.getLocation() );
        assertEquals( precision, instance.getPrecision() );
        assertEquals( shape, instance.getShape() );
        assertEquals( scale, instance.getScale() );

        NormalInverseGammaDistribution.PDF i2 =
            new NormalInverseGammaDistribution.PDF( instance );
        assertNotSame( instance, i2 );
        assertEquals( instance.getLocation(), i2.getLocation() );
        assertEquals( instance.getPrecision(), i2.getPrecision() );
        assertEquals( instance.getShape(), i2.getShape() );
        assertEquals( instance.getScale(), i2.getScale() );
    }

    /**
     * Test of getLocation method, of class NormalInverseGammaDistribution.
     */
    public void testGetLocation()
    {
        System.out.println("getLocation");
        double location = RANDOM.nextGaussian();
        NormalInverseGammaDistribution instance = this.createInstance();
        instance.setLocation(location);
        assertEquals( location, instance.getLocation() );
    }

    /**
     * Test of setLocation method, of class NormalInverseGammaDistribution.
     */
    public void testSetLocation()
    {
        System.out.println("setLocation");
        double location = RANDOM.nextGaussian();
        NormalInverseGammaDistribution instance = this.createInstance();
        instance.setLocation(location);
        assertEquals( location, instance.getLocation() );
    }

    /**
     * Test of getPrecision method, of class NormalInverseGammaDistribution.
     */
    public void testGetPrecision()
    {
        System.out.println("getPrecision");
        double precision = RANDOM.nextDouble() * 10.0;
        NormalInverseGammaDistribution instance = this.createInstance();
        instance.setPrecision(precision);
        assertEquals( precision, instance.getPrecision() );
    }

    /**
     * Test of setPrecision method, of class NormalInverseGammaDistribution.
     */
    public void testSetPrecision()
    {
        System.out.println("setPrecision");
        double precision = RANDOM.nextDouble() * 10.0;
        NormalInverseGammaDistribution instance = this.createInstance();
        instance.setPrecision(precision);
        assertEquals( precision, instance.getPrecision() );
        try
        {
            instance.setPrecision(0.0);
            fail( "Precision must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    /**
     * Test of getShape method, of class NormalInverseGammaDistribution.
     */
    public void testGetShape()
    {
        System.out.println("getShape");
        double shape = RANDOM.nextDouble()*10.0;
        NormalInverseGammaDistribution instance = this.createInstance();
        instance.setShape(shape);
        assertEquals( shape, instance.getShape() );
    }

    /**
     * Test of setShape method, of class NormalInverseGammaDistribution.
     */
    public void testSetShape()
    {
        System.out.println("setShape");
        double shape = RANDOM.nextDouble()*10.0;
        NormalInverseGammaDistribution instance = this.createInstance();
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
     * Test of getScale method, of class NormalInverseGammaDistribution.
     */
    public void testGetScale()
    {
        System.out.println("getScale");
        double scale = RANDOM.nextDouble() * 10.0;
        NormalInverseGammaDistribution instance = this.createInstance();
        instance.setScale(scale);
        assertEquals( scale, instance.getScale() );
    }

    /**
     * Test of setScale method, of class NormalInverseGammaDistribution.
     */
    public void testSetScale()
    {
        System.out.println("setScale");
        double scale = RANDOM.nextDouble() * 10.0;
        NormalInverseGammaDistribution instance = this.createInstance();
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
    public void testProbabilityFunctionKnownValues()
    {
        System.out.println( "PDF Known Values" );

        NormalInverseGammaDistribution instance = this.createInstance();
        NormalInverseGammaDistribution.PDF pdf = instance.getProbabilityFunction();
        ArrayList<Vector> samples = instance.sample(RANDOM, NUM_SAMPLES);
        for( Vector sample : samples )
        {
            double result = pdf.evaluate(sample);
            UnivariateGaussian.PDF g = new UnivariateGaussian.PDF(
                instance.getLocation(), sample.getElement(1)/instance.getPrecision() );
            InverseGammaDistribution.PDF ig = new InverseGammaDistribution.PDF(
                instance.getShape(), instance.getScale() );
            double expected = g.evaluate( sample.getElement(0) ) * ig.evaluate( sample.getElement(1) );
            assertEquals( expected, result, TOLERANCE );
        }

        Vector bad = VectorFactory.getDefault().createVector(3);
        try
        {
            pdf.evaluate(bad);
            fail( "Wrong dimensionality!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );
    }

    @Override
    public NormalInverseGammaDistribution createInstance()
    {
        double location = RANDOM.nextGaussian();
        double precision = RANDOM.nextDouble() * 10.0;
        double shape = RANDOM.nextDouble() * 3.0 + 2.0;
        double scale = RANDOM.nextDouble() * 3.0 + 2.0;
        return new NormalInverseGammaDistribution(
            location, precision,shape, scale);
    }

    @Override
    public void testGetMean()
    {
        double temp = TOLERANCE;
        TOLERANCE = 1e-1;
        super.testGetMean();
        TOLERANCE = temp;
    }

    /**
     * getCovariance
     */
    public void testGetCovariance()
    {
        System.out.println( "getCovariance" );

        NormalInverseGammaDistribution instance = this.createInstance();
        ArrayList<Vector> samples = instance.sample(RANDOM, NUM_SAMPLES);
        Pair<Vector,Matrix> pair =
            MultivariateStatisticsUtil.computeMeanAndCovariance(samples);
        System.out.println( "Instance: " + instance );
        System.out.println( "Mean: " + pair.getFirst() );
        System.out.println( "Covariance:\n" + pair.getSecond() );

    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        NormalInverseGammaDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        assertEquals( 4, p.getDimensionality() );
        assertEquals( instance.getLocation(), p.getElement(0) );
        assertEquals( instance.getPrecision(), p.getElement(1) );
        assertEquals( instance.getShape(), p.getElement(2) ) ;
        assertEquals( instance.getScale(), p.getElement(3) );
    }

}

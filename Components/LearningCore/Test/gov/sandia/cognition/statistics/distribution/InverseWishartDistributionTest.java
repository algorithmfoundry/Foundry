/*
 * File:                InverseWishartDistributionTest.java
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
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.MultivariateClosedFormComputableDistributionTestHarness;

/**
 * Unit tests for InverseWishartDistributionTest.
 *
 * @author krdixon
 */
public class InverseWishartDistributionTest
    extends MultivariateClosedFormComputableDistributionTestHarness<Matrix>
{

    /**
     * Tests for class InverseWishartDistributionTest.
     * @param testName Name of the test.
     */
    public InverseWishartDistributionTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Tests the constructors of class InverseWishartDistributionTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        InverseWishartDistribution instance = new InverseWishartDistribution();
        assertEquals( InverseWishartDistribution.DEFAULT_DIMENSIONALITY, instance.getInputDimensionality() );
        assertEquals( InverseWishartDistribution.DEFAULT_DIMENSIONALITY+2, instance.getDegreesOfFreedom() );

        Matrix sample = instance.sample(RANDOM);
        int dof = RANDOM.nextInt(10) + sample.getNumRows() + 2;
        instance = new InverseWishartDistribution( sample, dof );
        assertSame( sample, instance.getInverseScale() );
        assertEquals( dof, instance.getDegreesOfFreedom() );

        InverseWishartDistribution i2 = new InverseWishartDistribution( instance );
        assertEquals( instance.getDegreesOfFreedom(), i2.getDegreesOfFreedom() );
        assertEquals( instance.getInverseScale(), i2.getInverseScale() );
        assertNotSame( instance.getInverseScale(), i2.getInverseScale() );

    }


    @Override
    public void testProbabilityFunctionConstructors()
    {
        System.out.println( "PDF Constructors" );

        InverseWishartDistribution.PDF instance = new InverseWishartDistribution.PDF();
        assertEquals( InverseWishartDistribution.DEFAULT_DIMENSIONALITY, instance.getInputDimensionality() );
        assertEquals( InverseWishartDistribution.DEFAULT_DIMENSIONALITY+2, instance.getDegreesOfFreedom() );

        Matrix sample = instance.sample(RANDOM);
        int dof = RANDOM.nextInt(10) + sample.getNumRows() + 2;
        instance = new InverseWishartDistribution.PDF( sample, dof );
        assertSame( sample, instance.getInverseScale() );
        assertEquals( dof, instance.getDegreesOfFreedom() );

        InverseWishartDistribution.PDF i2 = new InverseWishartDistribution.PDF( instance );
        assertEquals( instance.getDegreesOfFreedom(), i2.getDegreesOfFreedom() );
        assertEquals( instance.getInverseScale(), i2.getInverseScale() );
        assertNotSame( instance.getInverseScale(), i2.getInverseScale() );
    }

    /**
     * Test of getInputDimensionality method, of class InverseWishartDistribution.
     */
    public void testGetInputDimensionality()
    {
        System.out.println("getInputDimensionality");
        InverseWishartDistribution instance = this.createInstance();
        assertTrue( instance.getInputDimensionality() > 0 );
        assertEquals( instance.getInverseScale().getNumRows(), instance.getInputDimensionality() );
    }

    /**
     * Test of getInverseScale method, of class InverseWishartDistribution.
     */
    public void testGetInverseScale()
    {
        System.out.println("getInverseScale");
        InverseWishartDistribution instance = this.createInstance();
        assertNotNull( instance.getInverseScale() );
    }

    /**
     * Test of setInverseScale method, of class InverseWishartDistribution.
     */
    public void testSetInverseScale()
    {
        System.out.println("setInverseScale");
        InverseWishartDistribution instance = this.createInstance();
        Matrix sample = instance.sample(RANDOM);
        instance.setInverseScale(sample);
        assertSame( sample, instance.getInverseScale() );
    }

    /**
     * Test of getDegreesOfFreedom method, of class InverseWishartDistribution.
     */
    public void testGetDegreesOfFreedom()
    {
        System.out.println("getDegreesOfFreedom");
        InverseWishartDistribution instance = this.createInstance();
        assertTrue( instance.getDegreesOfFreedom() > instance.getInputDimensionality() - 1 );
    }

    /**
     * Test of setDegreesOfFreedom method, of class InverseWishartDistribution.
     */
    public void testSetDegreesOfFreedom()
    {
        System.out.println("setDegreesOfFreedom");
        InverseWishartDistribution instance = this.createInstance();
        int dof = RANDOM.nextInt(10) + instance.getInputDimensionality() + 2;
        instance.setDegreesOfFreedom(dof);
        assertEquals( dof, instance.getDegreesOfFreedom() );

        try
        {
            instance.setDegreesOfFreedom(instance.getInputDimensionality()-1);
            fail( "DOF must be > dim-1" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public InverseWishartDistribution createInstance()
    {
        return new InverseWishartDistribution();
    }

    @Override
    public void testProbabilityFunctionKnownValues()
    {
        System.out.println( "PDF Known Values" );

        InverseWishartDistribution.PDF pdf =
            this.createInstance().getProbabilityFunction();
        Matrix x = pdf.getInverseScale();
        assertEquals( 0.012972109294, pdf.evaluate(x), TOLERANCE );
    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known Values" );
    }

    @Override
    public void testConvertFromVector()
    {

        InverseWishartDistribution f = this.createInstance();

        Vector x1 = f.convertToVector();
        assertNotNull( x1 );
        int N = x1.getDimensionality();

        Vector x2 = x1.scale(RANDOM.nextDouble());
        f.convertFromVector(x2);
        Vector x3 = f.convertToVector();
        assertNotNull( x3 );
        assertNotSame( x2, x3 );

        // Convert back to the original parameterization
        f.convertFromVector( x1 );
        Vector x4 = f.convertToVector();
        assertNotNull( x4 );
        assertNotSame( x1, x4 );
        assertEquals( x1, x4 );

        try
        {
            f.convertFromVector( null );
            fail( "Cannot convert from null Vector" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        final double r = 1.0;
        try
        {
            Vector z1 = VectorFactory.getDefault().createUniformRandom( N-1, r, r, RANDOM );
            f.convertFromVector( z1 );
            fail( "Cannot convert from a N-1 dimension Vector!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            Vector z1 = VectorFactory.getDefault().createUniformRandom( N+1, r, r, RANDOM );
            f.convertFromVector( z1 );
            fail( "Cannot convert from a N+1 dimension Vector!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        InverseWishartDistribution instance = this.createInstance();
        Vector p = instance.convertToVector();
        int d = instance.getInputDimensionality();
        assertEquals( 1+d*d, p.getDimensionality() );
        assertEquals( instance.getDegreesOfFreedom(), (int) p.getElement(0) );
        Vector ip = p.subVector(1, p.getDimensionality()-1);
        Matrix IP = MatrixFactory.getDefault().createMatrix(d, d);
        IP.convertFromVector(ip);
        assertEquals( instance.getInverseScale(), IP );
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

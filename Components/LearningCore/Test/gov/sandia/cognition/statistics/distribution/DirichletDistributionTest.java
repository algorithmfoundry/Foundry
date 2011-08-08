/*
 * File:                DirichletDistributionTest.java
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

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.MultivariateClosedFormComputableDistributionTestHarness;

/**
 * Unit tests for DirichletDistributionTest.
 *
 * @author krdixon
 */
public class DirichletDistributionTest
    extends MultivariateClosedFormComputableDistributionTestHarness<Vector>
{

    /**
     * Tests for class DirichletDistributionTest.
     * @param testName Name of the test.
     */
    public DirichletDistributionTest(
        String testName)
    {
        super(testName);
    }


    @Override
    public DirichletDistribution createInstance()
    {
        final double r = 10.0;

        Vector a = VectorFactory.getDefault().copyValues( r*RANDOM.nextDouble(), r*RANDOM.nextDouble(), r*RANDOM.nextDouble() );
        return new DirichletDistribution( a );
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        DirichletDistribution f = new DirichletDistribution();
        assertEquals( 2, f.getParameters().getDimensionality() );
        assertEquals( 1.0, f.getParameters().getElement(0) );
        assertEquals( 1.0, f.getParameters().getElement(1) );

        Vector a1 = VectorFactory.getDefault().copyValues(RANDOM.nextDouble(), RANDOM.nextDouble());
        assertNotSame( f.getParameters(), a1 );
        f = new DirichletDistribution( a1 );
        assertSame( a1, f.getParameters() );

        DirichletDistribution f2 = new DirichletDistribution( f );
        assertNotSame( f.getParameters(), f2.getParameters() );
        assertEquals( f.getParameters(), f2.getParameters() );

    }

    @Override
    public void testKnownValues()
    {
        System.out.println( "Known values" );

        DirichletDistribution.PDF f = new DirichletDistribution.PDF(
            VectorFactory.getDefault().copyValues(1.0,1.0) );

        Vector half = VectorFactory.getDefault().copyValues(0.5,0.5);
        assertEquals( half, f.getMean() );

        f.setParameters( VectorFactory.getDefault().copyValues(2.0,2.0) );
        assertEquals( half, f.getMean() );

        f.setParameters( VectorFactory.getDefault().copyValues(3.0,1.0) );
        Vector m31 = VectorFactory.getDefault().copyValues(0.75,0.25);
        assertEquals( m31, f.getMean() );

    }

    @Override
    public void testGetMean()
    {
        double temp = this.TOLERANCE;
        this.TOLERANCE = 1e-2;
        super.testGetMean();
        this.TOLERANCE = temp;
    }

    /**
     * Test of getParameters method, of class DirichletDistribution.
     */
    public void testGetParameters()
    {
        System.out.println("getParameters");

        Vector a1 = VectorFactory.getDefault().copyValues( RANDOM.nextDouble(), RANDOM.nextDouble() );
        DirichletDistribution instance = new DirichletDistribution(a1);
        assertSame( a1, instance.getParameters() );
    }

    /**
     * Test of setParameters method, of class DirichletDistribution.
     */
    public void testSetParameters()
    {
        System.out.println("setParameters");
        Vector a1 = VectorFactory.getDefault().copyValues( RANDOM.nextDouble(), RANDOM.nextDouble() );
        DirichletDistribution instance = new DirichletDistribution(a1);
        assertSame( a1, instance.getParameters() );

        // Different dimensionality
        Vector a2 = VectorFactory.getDefault().copyValues( RANDOM.nextDouble(), RANDOM.nextDouble(), RANDOM.nextDouble() );
        instance.setParameters(a2);
        assertSame( a2, instance.getParameters() );

        Vector a3 = VectorFactory.getDefault().copyValues( RANDOM.nextDouble(), RANDOM.nextDouble(), 0.0 );
        try
        {
            instance.setParameters(a3);
            fail( "Parameters must be >0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setParameters(null);
            fail( "Cannot have null parameters" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            instance.setParameters(VectorFactory.getDefault().copyValues(1.0));
            fail( "Must have dimensionality >= 2" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    @Override
    public void testProbabilityFunctionConstructors()
    {
        System.out.println( "Constructors" );
        DirichletDistribution.PDF f = new DirichletDistribution.PDF();
        assertEquals( 2, f.getParameters().getDimensionality() );
        assertEquals( 1.0, f.getParameters().getElement(0) );
        assertEquals( 1.0, f.getParameters().getElement(1) );

        Vector a1 = VectorFactory.getDefault().copyValues(RANDOM.nextDouble(), RANDOM.nextDouble());
        assertNotSame( f.getParameters(), a1 );
        f = new DirichletDistribution.PDF( a1 );
        assertSame( a1, f.getParameters() );

        DirichletDistribution.PDF f2 = new DirichletDistribution.PDF( f );
        assertNotSame( f.getParameters(), f2.getParameters() );
        assertEquals( f.getParameters(), f2.getParameters() );

    }

    @Override
    public void testProbabilityFunctionKnownValues()
    {

        // Uniform distribution on 2-dimensions
        DirichletDistribution.PDF f = new DirichletDistribution.PDF(
            VectorFactory.getDefault().copyValues(1.0,1.0) );
        for( int i = 0; i < 10; i++ )
        {
            assertEquals( 1.0, f.evaluate(
                VectorFactory.getDefault().copyValues(RANDOM.nextDouble(), RANDOM.nextDouble()) ),
                TOLERANCE );
        }

        f.setParameters( VectorFactory.getDefault().copyValues(2.0,2.0) );
        assertEquals( 1.5, f.evaluate(VectorFactory.getDefault().copyValues(0.5,0.5) ), TOLERANCE );

    }

    @Override
    public void testKnownConvertToVector()
    {
        System.out.println( "Known convertToVector" );

        DirichletDistribution.PDF f = new DirichletDistribution.PDF(
            VectorFactory.getDefault().copyValues(1.0,2.0) );

        Vector p = f.convertToVector();
        assertEquals( 2, p.getDimensionality() );
        assertEquals( f.getParameters(), p );
        assertNotSame( f.getParameters(), p );
    }

}

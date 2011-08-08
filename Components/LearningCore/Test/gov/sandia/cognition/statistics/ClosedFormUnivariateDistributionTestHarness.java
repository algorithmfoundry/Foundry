/*
 * File:                ClosedFormUnivariateDistributionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 2, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * Unit tests for ClosedFormUnivariateDistribution.
 *
 * @param <NumberType> Number type
 * @author krdixon
 */
public abstract class ClosedFormUnivariateDistributionTestHarness<NumberType extends Number>
    extends UnivariateDistributionTestHarness<NumberType>
{

    /**
     * Tests for class ClosedFormScalarDistributionTestHarness2.
     * @param testName Name of the test.
     */
    public ClosedFormUnivariateDistributionTestHarness(
        String testName)
    {
        super(testName);
    }

    public abstract ClosedFormUnivariateDistribution<NumberType> createInstance();

    /**
     * Known convertToVector
     */
    public abstract void testKnownConvertToVector();

    /**
     * convertToVector
     */
    public void testDistributionConvertToVector()
    {
        System.out.println( "Distribution.convertToVector" );
        ClosedFormUnivariateDistribution<NumberType> instance = this.createInstance();

        // Should have at least one parameter
        Vector x1 = instance.convertToVector();
        assertNotNull( x1 );
        assertTrue( x1.getDimensionality() > 0 );

        // Should return the equal parameterization
        Vector x2 = instance.convertToVector();
        assertNotNull( x2 );
        assertNotSame( x1, x2 );
        assertEquals( x1.getDimensionality(), x2.getDimensionality() );
        assertEquals( x1, x2 );

        // Parameterization shouldn't be effected by changed returned parameters
        x2.setElement( 0, x2.getElement( 0 ) + RANDOM.nextDouble() );
        x2.scaleEquals( RANDOM.nextDouble() );
        Vector x3 = instance.convertToVector();
        assertNotNull( x3 );
        assertFalse( x2.equals( x3 ) );
        assertEquals( x1, x3 );
    }

    /**
     * Tests convertToVector
     */
    public void testCDFConvertToVector()
    {

        System.out.println( "CDF.convertToVector" );

        ClosedFormUnivariateDistribution<NumberType> instance = this.createInstance();
        ClosedFormCumulativeDistributionFunction<NumberType> cdf = instance.getCDF();

        assertEquals( instance.convertToVector(), cdf.convertToVector() );

        // Should have at least one parameter
        Vector x1 = cdf.convertToVector();
        assertNotNull( x1 );
        assertTrue( x1.getDimensionality() > 0 );

        // Should return the equal parameterization
        Vector x2 = cdf.convertToVector();
        assertNotNull( x2 );
        assertNotSame( x1, x2 );
        assertEquals( x1.getDimensionality(), x2.getDimensionality() );
        assertEquals( x1, x2 );

        // Parameterization shouldn't be effected by changed returned parameters
        x2.setElement( 0, x2.getElement( 0 ) + RANDOM.nextDouble() );
        x2.scaleEquals( RANDOM.nextDouble() );
        Vector x3 = cdf.convertToVector();
        assertNotNull( x3 );
        assertFalse( x2.equals( x3 ) );
        assertEquals( x1, x3 );

    }

    /**
     * Tests CDF.convertFromVector
     */
    public void testDistributionConvertFromVector()
    {
        System.out.println( "Distribution.convertFromVector" );

        ClosedFormUnivariateDistribution<NumberType> instance = this.createInstance();
        Vector x1 = instance.convertToVector();
        assertNotNull( x1 );
        int N = x1.getDimensionality();

        // Create a new parameterization of the CDF
        // (try using positive parameters as some distributions need
        // positive parameters)
        final double r = 2.0;
        Vector y1 = x1.scale( RANDOM.nextDouble() );
//        Vector y1 = VectorFactory.getDefault().createUniformRandom( N, 0.0, r, RANDOM );
        instance.convertFromVector( y1 );
        Vector y2 = instance.convertToVector();
        assertNotNull( y2 );
        assertNotSame( y1, y2 );
//        assertEquals( y1, y2 );

        // Convert back to the original parameterization
        instance.convertFromVector( x1 );
        Vector x2 = instance.convertToVector();
        assertNotNull( x2 );
        assertNotSame( x1, x2 );
        assertEquals( x1, x2 );

        try
        {
            instance.convertFromVector( null );
            fail( "Cannot convert from null Vector" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            Vector z1 = VectorFactory.getDefault().createUniformRandom( N-1, r, r, RANDOM );
            instance.convertFromVector( z1 );
            fail( "Cannot convert from a N-1 dimension Vector!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            Vector z1 = VectorFactory.getDefault().createUniformRandom( N+1, r, r, RANDOM );
            instance.convertFromVector( z1 );
            fail( "Cannot convert from a N+1 dimension Vector!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Tests CDF.convertFromVector
     */
    public void testCDFConvertFromVector()
    {
        System.out.println( "CDF.convertFromVector" );

        ClosedFormUnivariateDistribution<NumberType> instance = this.createInstance();
        ClosedFormCumulativeDistributionFunction<NumberType> cdf = instance.getCDF();

        Vector x1 = cdf.convertToVector();
        assertNotNull( x1 );
        int N = x1.getDimensionality();

        // Create a new parameterization of the CDF
        // (try using positive parameters as some distributions need
        // positive parameters)
        final double r = 1.0;
        Vector y1 = VectorFactory.getDefault().createUniformRandom( N, 0.0, r, RANDOM );
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

}

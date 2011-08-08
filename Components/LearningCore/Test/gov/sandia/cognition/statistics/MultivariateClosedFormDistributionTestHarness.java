/*
 * File:                MultivariateClosedFormDistributionTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 17, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics;

import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * Tests MultivariateClosedFormDistribution
 * @param <RingType> RingType
 * @author Kevin R. Dixon
 * @since 3.0
 */
public abstract class MultivariateClosedFormDistributionTestHarness<RingType extends Ring<RingType>>
    extends MultivariateDistributionTestHarness<RingType>
{

    /**
     * Tests for class MultivariateDistributionTestHarness.
     * @param testName Name of the test.
     */
    public MultivariateClosedFormDistributionTestHarness(
        String testName)
    {
        super(testName);
    }

    @Override
    public abstract ClosedFormDistribution<RingType> createInstance();

    /**
     * Tests known convertToVector values
     */
    public abstract void testKnownConvertToVector();

    /**
     * Convert to vector
     */
    public void testConvertToVector()
    {
        System.out.println( "convertToVector" );

        ClosedFormDistribution<RingType> f = this.createInstance();

        // Should have at least one parameter
        Vector x1 = f.convertToVector();
        assertNotNull( x1 );
        assertTrue( x1.getDimensionality() > 0 );

        // Should return the equal parameterization
        Vector x2 = f.convertToVector();
        assertNotNull( x2 );
        assertNotSame( x1, x2 );
        assertEquals( x1.getDimensionality(), x2.getDimensionality() );
        assertEquals( x1, x2 );

        // Parameterization shouldn't be effected by changed returned parameters
        x2.setElement( 0, x2.getElement( 0 ) + RANDOM.nextDouble() );
        x2.scaleEquals( RANDOM.nextDouble() );
        Vector x3 = f.convertToVector();
        assertNotNull( x3 );
        assertFalse( x2.equals( x3 ) );
        assertEquals( x1, x3 );

    }


    /**
     * convertFromVector
     */
    public void testConvertFromVector()
    {
        System.out.println( "convertFromVector" );

        ClosedFormDistribution<RingType> f = this.createInstance();

        Vector x1 = f.convertToVector();
        assertNotNull( x1 );
        int N = x1.getDimensionality();

        Vector x2 = x1.scale(RANDOM.nextDouble());
        f.convertFromVector(x2);
        Vector x3 = f.convertToVector();
        assertNotNull( x3 );
        assertNotSame( x2, x3 );
//        assertEquals( x2, x3 );

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

    
}

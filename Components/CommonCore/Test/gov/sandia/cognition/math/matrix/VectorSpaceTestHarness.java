
/*
 * File:                AbstractVectorSpace.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Jun 23, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.RingTestHarness;

/**
 * Tests for class VectorSpaceTestHarness.
 * @param <VectorType>
 * Type of Vector
 * @author krdixon
 */
public abstract class VectorSpaceTestHarness<VectorType extends VectorSpace<VectorType,?>>
    extends RingTestHarness<VectorType>
{

    /**
     * Num samples.
     */
    public static int NUM_SAMPLES = 10;

    /**
     * Creates a new instance of VectorTestHarness
     * @param testName Name of the test
     */
    public VectorSpaceTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates a vector with the given dimension
     * @param numDim
     * Number of dimensions
     * @return
     * Vector with the specified dimensions
     */
    abstract protected VectorType createVector(
        int numDim);

    /**
     * Copies the Vector to the local class
     * @param vector
     * Vector to copy
     * @return
     * Copy of the Vector
     */
    abstract protected VectorType createCopy(
        Vector vector);

    /**
     * Creates a new RANDOM Vector with a given dimension and range
     * @param numDim dimension of the vector
     * @param minRange minimum value for the entries
     * @param maxRange maximum value for the entries
     * @return Vector of dimension "numDim" and uniformly distributed elements
     */
    protected VectorType createRandom(
        int numDim,
        double minRange,
        double maxRange)
    {
        return this.createCopy(VectorFactory.getDefault().createUniformRandom(
            numDim, minRange, maxRange, RANDOM));
    }

    /**
     * Max dimensions
     */
    protected static int DEFAULT_MAX_DIMENSION = 10;

    @Override
    protected VectorType createRandom()
    {
        int M = RANDOM.nextInt( DEFAULT_MAX_DIMENSION ) + 1;
        return this.createRandom(M, -RANGE, RANGE);
    }

    /**
     * Tests the constructors of class VectorSpaceTestHarness.
     */
    public void testConstructors()
    {
    }

    /**
     * Test of sum method, of class VectorSpace.
     */
    public void testSum()
    {
        System.out.println("sum");
        VectorType instance = this.createRandom();
        double result = instance.sum();
        double sum = 0.0;
        for( VectorSpace.Entry entry : instance )
        {
            sum += entry.getValue();
        }
        assertEquals( sum, result, TOLERANCE );
    }

    /**
     * Test of norm1 method, of class VectorSpace.
     */
    public void testNorm1()
    {
        System.out.println("norm1");
        VectorType instance = this.createRandom();
        assertEquals( instance.norm(1.0), instance.norm1(), TOLERANCE );
    }

    /**
     * Test of norm2 method, of class VectorSpace.
     */
    public void testNorm2()
    {
        System.out.println("norm2");
        VectorType instance = this.createRandom();

        assertEquals( instance.norm(2.0), instance.norm2(), TOLERANCE);
    }

    /**
     * Test of norm2Squared method, of class VectorSpace.
     */
    public void testNorm2Squared()
    {
        System.out.println("norm2Squared");
        VectorType instance = this.createRandom();
        assertEquals( instance.norm2()*instance.norm2(), instance.norm2Squared(), TOLERANCE );
    }

    /**
     * Test of normInfinity method, of class VectorSpace.
     */
    public void testNormInfinity()
    {
        System.out.println("normInfinity");
        VectorType instance = this.createRandom();

        double max = 0.0;
        for( VectorSpace.Entry entry : instance )
        {
            final double value = Math.abs(entry.getValue());
            if( max < value )
            {
                max = value;
            }
        }
        assertEquals( max, instance.normInfinity() );

        // Infinity norm shouldn't be effected by scaling by -1.0
        instance.scaleEquals(-1.0);
        assertEquals( max, instance.normInfinity() );

    }

    /**
     * Test of norm method, of class VectorSpace.
     */
    public void testNorm()
    {
        System.out.println("norm");

        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            double power = RANDOM.nextDouble() * 100.0;
            VectorType instance = this.createRandom();
            double sump = 0.0;
            for( VectorSpace.Entry entry : instance )
            {
                final double value = Math.abs(entry.getValue());
                sump += Math.pow( value, power );
            }
            final double normp = Math.pow( sump, 1.0/power );
            assertEquals( normp, instance.norm(power), TOLERANCE );

            assertEquals( instance.normInfinity(), instance.norm(Double.POSITIVE_INFINITY), TOLERANCE );
        }

        try
        {
            VectorType instance = this.createRandom();
            instance.norm(0.0);
            fail( "power must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            VectorType instance = this.createRandom();
            instance.norm(-1.0);
            fail( "power must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            VectorType instance = this.createRandom();
            instance.norm(Double.NEGATIVE_INFINITY);
            fail( "power must be > 0.0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        try
        {
            VectorType instance = this.createRandom();
            instance.norm(Double.NaN);
            fail( "power can't be NaN" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

        VectorType instance = this.createRandom();
        instance.zero();
        assertEquals( 0.0, instance.norm(RANDOM.nextDouble()*100.0), TOLERANCE );

        instance = this.createVector(0);
        assertEquals( 0.0, instance.norm( 1.0 ), TOLERANCE );

    }

    /**
     * Test of dot method, of class VectorSpace.
     */
    public void testDot()
    {
        System.out.println("dot");

        VectorType a = this.createCopy(VectorFactory.getDefault().copyValues(1.0, 2.0, 3.0));
        VectorType b = this.createCopy(VectorFactory.getDefault().copyValues(2.0, 1.0, 3.0));
        assertEquals(13.0, a.dot(b), TOLERANCE);
        assertEquals(a.dot(b), b.dot(a), TOLERANCE);
        assertEquals(a.dotProduct(b), a.dot(b), TOLERANCE);
    }
    
    /**
     * Test of dotProduct method, of class VectorSpace.
     */
    public void testDotProduct()
    {
        System.out.println("dotProduct");

        VectorType a = this.createCopy(VectorFactory.getDefault().copyValues(1.0, 2.0, 3.0));
        VectorType b = this.createCopy(VectorFactory.getDefault().copyValues(2.0, 1.0, 3.0));
        assertEquals( 13.0, a.dotProduct(b), TOLERANCE );
        assertEquals( a.dotProduct(b), b.dotProduct(a), TOLERANCE );
    }

    /**
     * Test of angle method, of class VectorSpace.
     */
    public void testAngle()
    {
        System.out.println("angle");
        VectorType a = this.createCopy(VectorFactory.getDefault().copyValues(1.0, 2.0, 3.0));
        VectorType b = this.createCopy(VectorFactory.getDefault().copyValues(2.0, 1.0, 3.0));
        assertEquals( 0.380251206692933, a.angle(b), TOLERANCE );
        assertEquals( a.angle(b), b.angle(a), TOLERANCE );
    }

    /**
     * Test of cosine method, of class VectorSpace.
     */
    public void testCosine()
    {
        System.out.println("cosine");
        
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            VectorType a = this.createRandom( 10, -RANGE, RANGE );
            VectorType b = this.createRandom( 10, -RANGE, RANGE );
            double dot = a.dotProduct(b);
            double na = a.norm2Squared();
            double nb = b.norm2Squared();
            double cosine = dot/Math.sqrt(na*nb);
            assertEquals( cosine, a.cosine(b) );
            assertEquals( cosine, b.cosine(a) );
        }

        VectorType a = this.createRandom();
        VectorType b = a.clone();
        assertEquals( 1.0, a.cosine(b), TOLERANCE );
        assertEquals( 1.0, b.cosine(a), TOLERANCE );
        b.zero();
        assertEquals( 0.0, a.cosine(b), TOLERANCE );
        assertEquals( 0.0, b.cosine(a), TOLERANCE );

    }

    /**
     * Test of euclideanDistance method, of class VectorSpace.
     */
    public void testEuclideanDistance()
    {
        System.out.println("euclideanDistance");
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            VectorType a = this.createRandom( 10, -RANGE, RANGE );
            VectorType b = this.createRandom( 10, -RANGE, RANGE );
            assertEquals( a.minus(b).norm2(), a.euclideanDistance(b) );
            assertEquals( a.minus(b).norm2(), b.euclideanDistance(a) );
            assertEquals( 0.0, a.euclideanDistance(a) );
            assertEquals( 0.0, b.euclideanDistance(b) );
        }
    }

    /**
     * Test of euclideanDistanceSquared method, of class VectorSpace.
     */
    public void testEuclideanDistanceSquared()
    {
        System.out.println("euclideanDistanceSquared");
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            VectorType a = this.createRandom( 10, -RANGE, RANGE );
            VectorType b = this.createRandom( 10, -RANGE, RANGE );
            assertEquals( a.minus(b).norm2Squared(), a.euclideanDistanceSquared(b), TOLERANCE );
            assertEquals( a.minus(b).norm2Squared(), b.euclideanDistanceSquared(a), TOLERANCE );
            assertEquals( 0.0, a.euclideanDistanceSquared(a) );
            assertEquals( 0.0, b.euclideanDistanceSquared(b) );
        }
    }

    /**
     * Test of unitVector method, of class VectorSpace.
     */
    public void testUnitVector()
    {
        System.out.println("unitVector");
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            VectorType instance = this.createRandom();
            VectorType unit = instance.unitVector();
            assertTrue( instance.scale( 1.0 / instance.norm2() ).equals( unit, TOLERANCE ) );
        }
    }

    /**
     * Test of unitVectorEquals method, of class VectorSpace.
     */
    public void testUnitVectorEquals()
    {
        System.out.println("unitVectorEquals");
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            VectorType instance = this.createRandom();
            VectorType clone = instance.clone();
            assertEquals( instance, clone );
            instance.unitVectorEquals();
            assertFalse( instance.equals(clone) );
            VectorType unit = clone.unitVector();
            assertEquals( unit, instance );
            assertEquals( 1.0, instance.norm2(), TOLERANCE );
        }

        VectorType instance = this.createRandom();
        instance.zero();
        instance.unitVectorEquals();
        assertEquals( 0.0, instance.norm2() );
    }

    /**
     * Test of isUnitVector method, of class VectorSpace.
     */
    public void testIsUnitVector()
    {
        System.out.println("isUnitVector");
        for( int n = 0; n < NUM_SAMPLES; n++ )
        {
            VectorType instance = this.createRandom();
            assertFalse( instance.isUnitVector() );
            instance.unitVectorEquals();
            assertTrue( instance.isUnitVector(TOLERANCE) );
            instance.zero();
            assertFalse( instance.isUnitVector() );
        }
    }

}
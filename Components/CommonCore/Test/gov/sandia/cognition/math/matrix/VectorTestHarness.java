/*
 * File:                VectorTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 9, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;

/**
 * Test suite for implementations of Vector 
 *
 * @author krdixon
 * @since  1.0
 *
 */
abstract public class VectorTestHarness
    extends VectorSpaceTestHarness<Vector>
{

    /**
     * Creates a vector with the given dimension
     * @param numDim
     * Number of dimensions
     * @return
     * Vector with the specified dimensions
     */
    abstract protected Vector createVector(
        int numDim);

    /**
     * Copies the Vector to the local class
     * @param vector
     * Vector to copy
     * @return
     * Copy of the Vector
     */
    abstract protected Vector createCopy(
        Vector vector);

    /**
     * Creates a new RANDOM Vector with a given dimension and range
     * @param numDim dimension of the vector
     * @param minRange minimum value for the entries
     * @param maxRange maximum value for the entries
     * @return Vector of dimension "numDim" and uniformly distributed elements
     */
    protected Vector createRandom(
        int numDim,
        double minRange,
        double maxRange)
    {
        return this.createCopy(VectorFactory.getDefault().createUniformRandom(numDim, minRange, maxRange, RANDOM));
    }

    protected Vector createRandom()
    {
        int M = RANDOM.nextInt( DEFAULT_MAX_DIMENSION ) + 1;
        return this.createRandom(M, -RANGE, RANGE);
    }

    /** Creates a new instance of VectorTestHarness
     * @param testName Name of the test
     */
    public VectorTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of createVector
     */
    public void testCreateVector()
    {
        System.out.println("createVector");

        int M1 = 100;
        Vector v1 = this.createVector(M1);
        assertEquals(v1.getDimensionality(), M1);

        this.createVector(0);

        try
        {
            Vector v2 = this.createVector(-1);
            fail("Should have thrown exception: negative dimension: " + v2.getClass());
        }
        catch (Exception e)
        {
        }
    }

    /**
     * Test of createVector
     */
    public void testCreateRandomVector()
    {
        System.out.println("createRandomVector");

        int M1 = 100;
        double min = (RANDOM.nextDouble() * 100.0) + 1.0;
        double max = (RANDOM.nextDouble() * 1000.0) + min;

        Vector v1 = this.createRandom(M1, min, max);
        assertEquals(v1.getDimensionality(), M1);

        for (int i = 0; i < M1; i++)
        {
            assertTrue(v1.getElement(i) >= min);
            assertTrue(v1.getElement(i) <= max);
        }

        this.createRandom(0, -RANGE, RANGE);

        try
        {
            Vector v2 = this.createRandom(-1, -RANGE, RANGE);
            fail("Should have thrown exception: negative dimension: " + v2.getClass());
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of getDimensionality method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testGetDimensionality()
    {
        System.out.println("getDimensionality");

        int M = RANDOM.nextInt( DEFAULT_MAX_DIMENSION ) + 1;
        Vector v1 = this.createRandom(M, -RANGE, RANGE);

        assertEquals(M, v1.getDimensionality());
    }

    /**
     * Test of getElement method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testGetElement()
    {
        System.out.println("getElement");

        double e0 = RANDOM.nextDouble();
        double e1 = RANDOM.nextDouble();
        double e2 = RANDOM.nextDouble();
        Vector v1 = this.createCopy(new Vector3(e0, e1, e2));

        int M = v1.getDimensionality();
        assertEquals(e0, v1.getElement(0));
        assertEquals(e1, v1.getElement(1));
        assertEquals(e2, v1.getElement(2));
        v1.getElement(M - 1);

        try
        {
            v1.getElement(-1);
            fail("Should have thrown exception: " + v1.getClass());
        }
        catch (Exception e)
        {
        }

        try
        {
            v1.getElement(M);
            fail("Should have thrown exception: " + v1.getClass());
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of setElement method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testSetElement()
    {
        System.out.println("setElement");

        double value = RANDOM.nextDouble() - 10.0 * RANGE;

        Vector v1 = this.createRandom();

        int M = v1.getDimensionality();
        int index = RANDOM.nextInt( M );
        assertFalse(v1.getElement(index) == value);

        v1.setElement(index, value);
        assertEquals(v1.getElement(index), value);

        v1.setElement(0, RANDOM.nextDouble());

        try
        {
            v1.setElement(-1, RANDOM.nextDouble());
            fail("Should have thrown exception: " + v1.getClass());
        }
        catch (Exception e)
        {
        }

        try
        {
            v1.setElement(M, RANDOM.nextDouble());
            fail("Should have thrown exception: " + v1.getClass());
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of scaleEquals method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testScaleEquals()
    {
        System.out.println("scaleEquals");

        Vector v1 = this.createRandom();
        int M = v1.getDimensionality();

        Vector v2 = v1.clone();
        double scaleFactor = RANDOM.nextDouble();
        v2.scaleEquals(scaleFactor);
        for (int i = 0; i < M; i++)
        {
            assertEquals(v1.getElement(i) * scaleFactor, v2.getElement(i));
        }

    }

    /**
     * Test of outerProduct method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testOuterProduct()
    {
        System.out.println("outerProduct");

        Vector v1 = this.createRandom();
        int M1 = v1.getDimensionality();

        int M2 = RANDOM.nextInt( DEFAULT_MAX_DIMENSION ) + M1;
        Vector v2 = this.createRandom(M2, -RANGE, RANGE);

        Matrix m1 = v1.outerProduct(v2);
        assertEquals(M1, m1.getNumRows());
        assertEquals(M2, m1.getNumColumns());

        for (int i = 0; i < M1; i++)
        {
            for (int j = 0; j < M2; j++)
            {
                assertEquals(v1.getElement(i) * v2.getElement(j), m1.getElement(i, j), TOLERANCE );
            }
        }

        Matrix m2 = v2.outerProduct(v1);
        assertEquals(M2, m2.getNumRows());
        assertEquals(M1, m2.getNumColumns());

    }

    /**
     * Test of plusEquals method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testPlusEquals()
    {
        System.out.println("plusEquals");

        Vector v1 = this.createRandom();
        int M = v1.getDimensionality();

        Vector v2 = this.createRandom(M, -RANGE, RANGE);

        Vector v1clone = v1.clone();
        v1.plusEquals(v2);
        for (int i = 0; i < M; i++)
        {
            assertEquals(v1clone.getElement(i) + v2.getElement(i), v1.getElement(i));
        }

        Vector v3 = this.createRandom(M + 1, -RANGE, RANGE);

        try
        {
            v3.plusEquals(v1);
            fail("Should have thrown exception: " + v3.getClass());
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of dotTimesEquals method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testDotTimesEquals()
    {
        System.out.println("dotTimesEquals");

        Vector v1 = this.createRandom();
        int M = v1.getDimensionality();
        Vector v2 = this.createRandom(M, -RANGE, RANGE);

        System.out.println("v1: " + v1);
        System.out.println("v2: " + v2);
        Vector v3 = v1.clone();
        v3.dotTimesEquals(v2);

        System.out.println("v1: " + v1);
        System.out.println("v2: " + v2);
        System.out.println("v3: " + v3);

        for (int i = 0; i < M; i++)
        {
            System.out.println("i: " + i + "v1: " + v1.getElement(i) + " v2: " + v2.getElement(i) + " v3: " + v3.getElement(i));
            assertEquals(v3.getElement(i), v1.getElement(i) * v2.getElement(i), TOLERANCE);
        }

        Vector v4 = this.createRandom(M + 1, -RANGE, RANGE);

        try
        {
            v4.dotTimesEquals(v2);
            fail("Should have thrown exception: " + v4.getClass());
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of equals method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testEquals()
    {
        System.out.println("equals");

        Vector v1 = this.createRandom();
        int M = v1.getDimensionality();
        assertEquals(v1, v1);

        int index = RANDOM.nextInt( M );
        double delta = RANDOM.nextDouble();

        Vector v2 = v1.clone();
        v1.setElement(index, v1.getElement(index) + delta);
        assertFalse(v1.equals(v2));
        assertTrue(v1.equals(v2, delta + delta));

        Vector v3 = this.createRandom(M + 1, -RANGE, RANGE);
        assertFalse( v1.equals( v3 ) );

    }
    
    /**
     * Test of hashCode method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testHashCode()
    {
        System.out.println("hashCode");

        Vector v1 = this.createRandom();
        int M = v1.getDimensionality();
        assertEquals(v1.hashCode(), v1.hashCode());
        assertEquals(v1.hashCode(), v1.clone().hashCode());

        int index = RANDOM.nextInt( M );
        double delta = RANDOM.nextDouble();

        Vector v2 = v1.clone();
        assertEquals( v1.hashCode(), v2.hashCode() );
        
        v1.setElement(index, v1.getElement(index) + delta);
        assertFalse(v1.hashCode() == v2.hashCode());

        v1.setElement(index, 1.0);
        assertFalse(v1.hashCode() == v2.hashCode());

        Vector v3 = this.createRandom(M + 1, -RANGE, RANGE);
        assertFalse(v1.hashCode() == v3.hashCode());

    }

    /**
     * Test of times
     */
    public void testTimes()
    {
        System.out.println( "Times" );
        
        Vector x = this.createRandom();
        
        final double EPS = 1e-5;
        
        int M = x.getDimensionality();
        int N = M+1;
        
        final double r = 1.0;
        Matrix A1 = MatrixFactory.getDefault().createUniformRandom( M, N, -r, r, RANDOM  );
        
        Vector y1 = x.times( A1 );
        assertTrue( y1.equals( A1.transpose().times( x ), EPS ) );
        
        Matrix A2 = MatrixFactory.getDefault().createUniformRandom( M, N+1, -r, r, RANDOM  );
        Vector y2 = x.times( A2 );
        assertTrue( y2.equals( A2.transpose().times( x ), EPS ) );
        
        Matrix A3 = MatrixFactory.getDefault().createUniformRandom( M, M-1, -r, r, RANDOM  );
        Vector y3 = x.times( A3 );
        assertTrue( y3.equals( A3.transpose().times( x ), EPS ) );
        
        try
        {
            Matrix A4 = MatrixFactory.getDefault().createUniformRandom( M+1, N, -r, r, RANDOM  );
            Vector y4 = x.times( A4 );
            fail( "Should have thrown dimension exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        try
        {
            Matrix A4 = MatrixFactory.getDefault().createUniformRandom( M-1, N, -r, r, RANDOM  );
            Vector y4 = x.times( A4 );
            fail( "Should have thrown dimension exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        
    }

    /**
     * Test of checkSameDimensionality method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testCheckSameDimensionality()
    {
        System.out.println("checkSameDimensionality");

        Vector v1 = this.createRandom();
        int M1 = v1.getDimensionality();

        assertTrue(v1.checkSameDimensionality(v1));

        Vector v2 = this.createRandom(M1 + 1, -RANGE, RANGE);
        assertFalse(v1.checkSameDimensionality(v2));

        Vector v3 = this.createRandom(M1, -RANGE, RANGE);
        assertTrue(v1.checkSameDimensionality(v3));

    }

    /**
     * Test of stack method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testStack()
    {
        System.out.println("stack");

        Vector v1 = this.createRandom();
        int M1 = v1.getDimensionality();

        Vector v2 = this.createRandom();
        int M2 = v2.getDimensionality();

        Vector v1clone = v1.clone();
        Vector v2clone = v2.clone();

        Vector v3 = v1.stack(v2);
        assertEquals(v3.getDimensionality(), M1 + M2);

        assertEquals(v1, v1clone);
        assertEquals(v2, v2clone);

        for (int i = 0; i < M1; i++)
        {
            assertEquals(v1.getElement(i), v3.getElement(i));
        }

        for (int i = 0; i < M2; i++)
        {
            assertEquals(v2.getElement(i), v3.getElement(i + M1));
        }


    }

    /**
     * Test of convertFromVector method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testConvertFromVector()
    {
        System.out.println("convertFromVector");

        Vector v1 = this.createRandom();
        int M = v1.getDimensionality();

        Vector v1clone = v1.clone();

        assertEquals(v1, v1clone);

        Vector v2 = this.createRandom(M, -RANGE, RANGE);

        assertFalse(v1.equals(v2));

        v1.convertFromVector(v2);

        assertFalse(v1.equals(v1clone));
        assertEquals(v1, v2);

        Vector v3 = this.createRandom(M + 1, -RANGE, RANGE);

        try
        {
            v1.convertFromVector(v3);
            fail("Should have thrown exception: " + v1.getClass());
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of convertToVector method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testConvertToVector()
    {
        System.out.println("convertToVector");

        Vector v1 = this.createRandom();
        int M = v1.getDimensionality();

        Vector v1clone = v1.clone();

        assertEquals(v1, v1clone);

        Vector v2 = v1.convertToVector();

        assertEquals(v2.getDimensionality(), M);

        assertEquals(v1, v2);

    }

    /**
     * Test of iterator method, of class gov.sandia.isrc.math.matrix.Vector.
     */
    public void testIterator()
    {
        System.out.println("iterator");

        Vector v1 = this.createRandom();
        int M = v1.getDimensionality();

        Iterator<VectorEntry> iterator = v1.iterator();

        assertNotNull(iterator);

        assertTrue(iterator.hasNext());

        assertNotNull(iterator.next());

    }

    /**
     * Test of subVector
     */
    public void testSubVector()
    {
        System.out.println("subVector");

        Vector v1 = this.createRandom();
        int M = v1.getDimensionality();

        int i1 = RANDOM.nextInt( M );
        int i2 = RANDOM.nextInt( M );

        int min = Math.min(i1, i2);
        int max = Math.max(i1, i2);

        Vector v2 = v1.subVector(min, max);

        assertEquals(v2.getDimensionality(), max - min + 1);

        for (int i = 0; i < v2.getDimensionality(); i++)
        {
            assertEquals(v2.getElement(i), v1.getElement(i + min));
        }

        v1.subVector(min, min);
        try
        {
            v1.subVector(max, min - 2);
            fail("Should have thrown exception");
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of toString
     */
    public void testToString()
    {
        System.out.println("toString()");

        Vector v1 = this.createRandom();
        String s = v1.toString();
        System.out.println("String:\n" + s);
        assertNotNull(s);
        assertTrue( s.length() > 0 );
    }

    public void testToStringNumberFormat()
    {
        System.out.println( "toString(NumberFormat)" );


        Vector v1 = this.createRandom();
        NumberFormat format = new DecimalFormat();
        String s = v1.toString(format);
        System.out.println( "String:\n" + s );
        assertNotNull( s );
        assertTrue( s.length() > 0 );
    }

}

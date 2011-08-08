/*
 * File:                RingAccumulatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 19, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */
package gov.sandia.cognition.math;

import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import junit.framework.*;

/**
 * Unit tests for RingAccumulatorTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class RingAccumulatorTest extends TestCase
{
    private Random random = new Random();
    public RingAccumulatorTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(RingAccumulatorTest.class);
        
        return suite;
    }

    /**
     * Test of clear method, of class gov.sandia.isrc.math.RingAccumulator.
     */
    public void testClear()
    {
        System.out.println("clear");
        
        RingAccumulator<Matrix> instance = new RingAccumulator<Matrix>();
        instance.accumulate( MatrixFactory.getDefault().createUniformRandom( 10, 5, -10, 10, random ) );
        assertEquals( instance.getCount(), 1 );
        
        instance.clear();
        
        assertEquals( instance.getCount(), 0 );
        
    }

    /**
     * Test of accumulate method, of class gov.sandia.isrc.math.RingAccumulator.
     */
    public void testAccumulate()
    {
        System.out.println("accumulate");
        
        RingAccumulator<Vector> instance = new RingAccumulator<Vector>();
        Vector vector = VectorFactory.getDefault().createUniformRandom( 10, -10, 10, random );
        instance.accumulate( vector );
        assertEquals( instance.getCount(), 1 );
        assertEquals( instance.getSum(), vector );
        assertEquals( instance.getMean(), vector );

        Vector vector2 = VectorFactory.getDefault().createUniformRandom( vector.getDimensionality(), -1, 1, random );
        instance.accumulate( vector2 );
        Vector sum = instance.getSum();
        
        Collection<Vector> c = Arrays.asList( vector, vector2 );
        RingAccumulator<Vector> i2 = new RingAccumulator<Vector>( c );
        assertEquals( sum, i2.getSum() );
        
        
        
    }

    /**
     * Test of scaleSum method, of class gov.sandia.isrc.math.RingAccumulator.
     */
    public void testScaleSum()
    {
        System.out.println("scaleSum");
        
        RingAccumulator<Vector> instance = new RingAccumulator<Vector>();        
        assertNull( instance.scaleSum( Math.random() ) );
        
        double scaleFactor = Math.random();
        Vector vector = VectorFactory.getDefault().createUniformRandom( 10, -10, 10, random );
        instance.accumulate( vector );
        assertEquals( instance.getSum(), vector );
        assertEquals( instance.scaleSum( scaleFactor ), vector.scale( scaleFactor ) );
    }

    /**
     * Test of getMean method, of class gov.sandia.isrc.math.RingAccumulator.
     */
    public void testGetMean()
    {
        System.out.println("getMean");
                
        RingAccumulator<Vector> instance = new RingAccumulator<Vector>();
        assertNull( instance.getMean() );
        
        Vector vector1 = VectorFactory.getDefault().createUniformRandom( 10, -10, 10, random );
        Vector vector2 = VectorFactory.getDefault().createUniformRandom( 10, -10, 10, random );
        instance.accumulate( vector1 );
        assertEquals( instance.getMean(), vector1 );
        instance.accumulate( vector2 );
        assertEquals( instance.getMean(), vector1.plus( vector2 ).scale( 0.5 ) );
    }

    /**
     * Test of getSum method, of class gov.sandia.isrc.math.RingAccumulator.
     */
    public void testGetSum()
    {
        System.out.println("getSum");
        
        RingAccumulator<Vector> instance = new RingAccumulator<Vector>();
        assertNull( instance.getSum() );
        
        Vector vector1 = VectorFactory.getDefault().createUniformRandom( 10, -10, 10, random );
        Vector vector2 = VectorFactory.getDefault().createUniformRandom( 10, -10, 10, random );
        instance.accumulate( vector1 );
        assertEquals( instance.getSum(), vector1 );
        instance.accumulate( vector2 );
        assertEquals( instance.getSum(), vector1.plus( vector2 ) );
    }

    /**
     * Test of getCount method, of class gov.sandia.isrc.math.RingAccumulator.
     */
    public void testGetCount()
    {
        System.out.println("getCount");
        
        RingAccumulator<Vector> instance = new RingAccumulator<Vector>();
        Vector vector1 = VectorFactory.getDefault().createUniformRandom( 10, -10, 10, random );
        int N = (int) (Math.random() * 1000);
        for( int i = 0; i < N; i++ )
        {
            assertEquals( instance.getCount(), i );
            instance.accumulate( vector1 );
        }

    }
    
}

/*
 * File:                VectorFactoryTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import java.util.Random;
import junit.framework.TestCase;
import static junit.framework.TestCase.assertEquals;


/**
 * Unit tests for VectorFactoryTestHarness
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public abstract class VectorFactoryTestHarness
    extends TestCase
{
    
    /**
     * Constructor
     * @param testName name
     */
    public VectorFactoryTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates an instance of the VectorFactory
     * @return
     * VectorFactory
     */
    public abstract VectorFactory<?> createFactory();

    /**
     * Random-number range, {@value}.
     */
    protected double RANGE = 10.0;

    /**
     * Random-number generator.
     */
    protected Random RANDOM = new Random(1);

    /**
     * Constructor test
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );
        VectorFactory<?> f = this.createFactory();
        assertNotNull( f );
    }

    /**
     * Test of copyVector method, of class gov.sandia.isrc.math.Vector.VectorFactory.
     */
    public void testCopyVector()
    {
        System.out.println("copyVector");
        
        int M = RANDOM.nextInt(10) + 1;      
        Vector m = VectorFactory.getDefault().createUniformRandom( M, -RANGE, RANGE, RANDOM );
        Vector mclone = m.clone();
        
        VectorFactory<?> instance = this.createFactory();
        
        assertEquals( m, mclone );
        Vector r = instance.copyVector( m );
        assertEquals( m, r );

        assertEquals( m, mclone );
        r.setElement( 0, r.getElement(0)+1.0 );
        assertFalse( m.equals( r ) );
        assertEquals( m, mclone );

        try
        {
            instance.copyVector(null);
            fail( "Can't accept null Vector" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }


    }

    /**
     * Test of copyArray method, of class gov.sandia.isrc.math.Vector.VectorFactory.
     */
    public void testCopyArray()
    {
        System.out.println("copyArray");
        
        int M = RANDOM.nextInt(10) + 1;
        double [] values = new double[ M ];
        for( int i = 0; i < M; i++ )
        {
            if( i != 0 )
            {
                values[i] = RANDOM.nextGaussian();
            }
            else
            {
                values[i] = 0.0;
            }
        }
        double [] valuesClone = values.clone();
        
        VectorFactory<?> f = this.createFactory();
        Vector m = f.copyArray( values );
        
        assertEquals( m.getDimensionality(), M );
        for( int i = 0; i < M; i++ )
        {
            assertEquals( values[i], valuesClone[i] );
            assertEquals( values[i], m.getElement(i) );
            m.setElement(i, m.getElement(i) + RANDOM.nextDouble() );
        }

        m.scaleEquals( RANDOM.nextGaussian() );
        for( int i = 0; i < M; i++ )
        {
            assertEquals( values[i], valuesClone[i] );
            assertFalse( values[i] == m.getElement(i) );
        }

        Vector v0 = f.copyArray( new double[0] );
        assertEquals( 0, v0.getDimensionality() );

        try
        {
            f.copyArray(null);
            fail( "Can't accept null array" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }


    }

    /**
     * Test of copyValues method, of class gov.sandia.isrc.math.Vector.VectorFactory.
     */
    public void testCopyValues()
    {
        System.out.println("copyValues");
        
        
        VectorFactory<?> f = this.createFactory();
        Vector m0 = f.copyValues();
        assertEquals( 0, m0.getDimensionality() );
        
        double v0 = RANDOM.nextGaussian();
        Vector m = f.copyValues( v0 );
        assertEquals( 1, m.getDimensionality() );
        assertEquals( v0, m.getElement(0) );
        
        double v1 = RANDOM.nextGaussian();
        m = f.copyValues( v0, v1 );
        assertEquals( 2, m.getDimensionality() );
        assertEquals( v0, m.getElement(0) );
        assertEquals( v1, m.getElement(1) );
     
        m = f.copyValues( v0, v1, v0, v1 );
        assertEquals( 4, m.getDimensionality() );
        assertEquals( v0, m.getElement(0) );
        assertEquals( v1, m.getElement(1) );
        assertEquals( v0, m.getElement(2) );
        assertEquals( v1, m.getElement(3) );
        
    }
    
    
    /**
     * Test of createVector method, of class gov.sandia.isrc.math.Vector.VectorFactory.
     */
    public void testCreateVector()
    {
        System.out.println("createVector");
        
        int M = RANDOM.nextInt(10) + 1;
        VectorFactory<?> f = this.createFactory();
        Vector m = f.createVector( M );
        assertEquals( M, m.getDimensionality() );
        for( int i = 0; i < M; i++ )
        {
            assertEquals( 0.0, m.getElement(i) );
        }

        Vector v0 = f.createVector(0);
        assertEquals( 0, v0.getDimensionality() );
        try
        {
            f.createVector(-1);
            fail( "Dimension must be >= 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Tests createVector(dim,value)
     */
    public void testCreateVectorValue()
    {
        System.out.println( "createVector(value)" );

        int M = RANDOM.nextInt(10) + 1;
        double value = RANDOM.nextGaussian();
        VectorFactory<?> f = this.createFactory();
        Vector v1 = f.createVector(M,0.0);
        Vector m = f.createVector(M, value);
        assertEquals( M, m.getDimensionality() );
        for( int i = 0; i < M; i++ )
        {
            assertEquals( value, m.getElement(i) );
            assertEquals( 0.0, v1.getElement(i) );
        }

        Vector v0 = f.createVector(0, RANDOM.nextGaussian() );
        assertEquals( 0, v0.getDimensionality() );
        try
        {
            f.createVector(-1, RANDOM.nextGaussian() );
            fail( "Dimension must be >= 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    /**
     * Test of createUniformRandom method, of class gov.sandia.isrc.math.Vector.VectorFactory.
     */
    public void testCreateUniformRandom()
    {
        System.out.println("createUniformRandom");
        
        int M = RANDOM.nextInt(10) + 1;
        
        VectorFactory<?> f = this.createFactory();
        
        Vector m = f.createUniformRandom(M, RANDOM);
        assertEquals(M, m.getDimensionality());
        m.forEachElement((int i, double x) -> assertTrue(x >= 0.0 && x <= 1.0));
        assertTrue(m.sum() > 0.0);
        assertFalse(m.equals(f.createUniformRandom(M, RANDOM)));
        
        m = f.createUniformRandom( M, RANGE, 2*RANGE, RANDOM );
        assertEquals( M, m.getDimensionality() );
        for( int i = 0; i < M; i++ )
        {
            double value = m.getElement(i);
            assertTrue( value >= RANGE );
            assertTrue( value <= 2*RANGE );
        }
       
    }
    
    /**
     * Test of createGaussianRandom method, of class VectorFactory.
     */
    public void testCreateGaussianRandom()
    {
        int d = RANDOM.nextInt(10) + 1;
        
        VectorFactory<?> f = this.createFactory();
        
        Vector m = f.createGaussianRandom(d, RANDOM);
        assertEquals(d, m.getDimensionality());
        assertTrue(m.norm2() > 0.0);
        assertFalse(m.equals(f.createGaussianRandom(d, RANDOM)));
        
        d = 2000;
        m = f.createGaussianRandom(d, RANDOM);
        assertEquals(d, m.getDimensionality());
        double mean = UnivariateStatisticsUtil.computeMean(m.valuesAsList());
        double variance = UnivariateStatisticsUtil.computeVariance(m.valuesAsList());
        assertEquals(0.0, mean, 1e-2);
        assertEquals(1.0, variance, 1e-2);
    }

    /**
     * Test of copyArray method, of class SparseVectorFactory.
     */
    public void testCopyArray_3args()
    {
        System.out.println("copyArray");
        VectorFactory<?> instance = this.createFactory();

        int dimensionality = 10;
        int[] indices = { 1, 2, 5, 4, 0 };
        double[] values = new double[ indices.length ];
        values[0] = 0.0;
        for( int i = 1; i < values.length; i++ )
        {
            values[i] = RANDOM.nextGaussian();
        }

        
        Vector v = instance.copyArray(dimensionality, indices, values);
        assertNotNull( v );
        assertEquals( dimensionality, v.getDimensionality() );
        System.out.println( "V: " + v );
        for( int i = 0; i < indices.length; i++ )
        {
            assertEquals( values[i], v.getElement(indices[i]) );
        }

        try
        {
            instance.copyArray(dimensionality, indices, new double[ indices.length+1 ] );
            fail( "Length of values and indices must be equal!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }

    }

    public void testCreateVector1D()
    {
        VectorFactory<?> f = this.createFactory();
        Vector1D v = f.createVector1D();
        assertNotNull(v);
        assertEquals(1, v.getDimensionality());
        assertEquals(0.0, v.getX(), 0.0);
        assertNotSame(v, f.createVector1D());
        assertEquals(v, f.createVector1D());

        double x = this.RANDOM.nextDouble();
        v = f.createVector1D(x);
        assertNotNull(v);
        assertEquals(1, v.getDimensionality());
        assertEquals(x, v.getX(), 0.0);
        assertNotSame(v, f.createVector1D(x));
        assertEquals(v, f.createVector1D(x));

        x = 1.0;
        v = f.createVector1D(x);
        assertNotNull(v);
        assertEquals(1, v.getDimensionality());
        assertEquals(x, v.getX(), 0.0);
        assertNotSame(v, f.createVector1D(x));
        assertEquals(v, f.createVector1D(x));

        x = -2.3;
        v = f.createVector1D(x);
        assertNotNull(v);
        assertEquals(1, v.getDimensionality());
        assertEquals(x, v.getX(), 0.0);
        assertNotSame(v, f.createVector1D(x));
        assertEquals(v, f.createVector1D(x));
    }

    public void testCreateVector2D()
    {
        VectorFactory<?> f = this.createFactory();
        Vector2D v = f.createVector2D();
        assertNotNull(v);
        assertEquals(2, v.getDimensionality());
        assertEquals(0.0, v.getX(), 0.0);
        assertEquals(0.0, v.getY(), 0.0);
        assertNotSame(v, f.createVector2D());
        assertEquals(v, f.createVector2D());

        double x = this.RANDOM.nextDouble();
        double y = this.RANDOM.nextDouble();
        v = f.createVector2D(x, y);
        assertNotNull(v);
        assertEquals(2, v.getDimensionality());
        assertEquals(x, v.getX(), 0.0);
        assertEquals(y, v.getY(), 0.0);
        assertNotSame(v, f.createVector2D(x, y));
        assertEquals(v, f.createVector2D(x, y));

        x = 1.0;
        y = -2.3;
        v = f.createVector2D(x, y);
        assertNotNull(v);
        assertEquals(2, v.getDimensionality());
        assertEquals(x, v.getX(), 0.0);
        assertEquals(y, v.getY(), 0.0);
        assertNotSame(v, f.createVector2D(x, y));
        assertEquals(v, f.createVector2D(x, y));

        x = -3.1;
        y = 101.0;
        v = f.createVector2D(x, y);
        assertNotNull(v);
        assertEquals(2, v.getDimensionality());
        assertEquals(x, v.getX(), 0.0);
        assertEquals(y, v.getY(), 0.0);
        assertNotSame(v, f.createVector2D(x, y));
        assertEquals(v, f.createVector2D(x, y));
    }

    public void testCreateVector3D()
    {
        VectorFactory<?> f = this.createFactory();
        Vector3D v = f.createVector3D();
        assertNotNull(v);
        assertEquals(3, v.getDimensionality());
        assertEquals(0.0, v.getX(), 0.0);
        assertEquals(0.0, v.getY(), 0.0);
        assertEquals(0.0, v.getZ(), 0.0);
        assertNotSame(v, f.createVector3D());
        assertEquals(v, f.createVector3D());

        double x = this.RANDOM.nextDouble();
        double y = this.RANDOM.nextDouble();
        double z = this.RANDOM.nextDouble();
        v = f.createVector3D(x, y, z);
        assertNotNull(v);
        assertEquals(3, v.getDimensionality());
        assertEquals(x, v.getX(), 0.0);
        assertEquals(y, v.getY(), 0.0);
        assertEquals(z, v.getZ(), 0.0);
        assertNotSame(v, f.createVector3D(x, y, z));
        assertEquals(v, f.createVector3D(x, y, z));

        x = 1.0;
        y = -2.3;
        z = 1234.5;
        v = f.createVector3D(x, y, z);
        assertNotNull(v);
        assertEquals(3, v.getDimensionality());
        assertEquals(x, v.getX(), 0.0);
        assertEquals(y, v.getY(), 0.0);
        assertEquals(z, v.getZ(), 0.0);
        assertNotSame(v, f.createVector3D(x, y, z));
        assertEquals(v, f.createVector3D(x, y, z));

        x = -3.1;
        y = 101.0;
        z = 0.1;
        v = f.createVector3D(x, y, z);
        assertNotNull(v);
        assertEquals(3, v.getDimensionality());
        assertEquals(x, v.getX(), 0.0);
        assertEquals(y, v.getY(), 0.0);
        assertEquals(z, v.getZ(), 0.0);
        assertNotSame(v, f.createVector3D(x, y, z));
        assertEquals(v, f.createVector3D(x, y, z));
    }

}

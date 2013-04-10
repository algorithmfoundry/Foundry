/*
 * File:                MatrixFactoryTestHarness.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 25, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import java.util.ArrayList;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for MatrixFactoryTestHarness.
 *
 * @author krdixon
 */
public abstract class MatrixFactoryTestHarness
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random random = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public double EPS = 1e-5;

    /**
     * Range to create random matrices, {@value}.
     */
    protected static double RANGE = 10.0;

    /**
     * Tests for class MatrixFactoryTestHarness.
     * @param testName Name of the test.
     */
    public MatrixFactoryTestHarness(
        String testName)
    {
        super(testName);
    }

    /**
     * Creates a new MatrixFactory
     * @return
     * MatrixFactory to test
     */
    public abstract MatrixFactory<?> createFactory();

    /**
     * Creates a random matrix from the factory
     * @return
     * random matrix
     */
    public abstract Matrix createRandomMatrix();


    /**
     * Tests the constructors of class MatrixFactoryTestHarness.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        MatrixFactory<?> factory = this.createFactory();
        assertNotNull( factory );
        
    }

    /**
     * Test of getDefault method, of class MatrixFactory.
     */
    public void testGetDefault()
    {
        System.out.println("getDefault");
        MatrixFactory<? extends Matrix> result = MatrixFactory.getDefault();
        assertSame( MatrixFactory.DEFAULT_DENSE_INSTANCE, result );
    }

    /**
     * Test of getDenseDefault method, of class MatrixFactory.
     */
    public void testGetDenseDefault()
    {
        System.out.println("getDenseDefault");
        MatrixFactory<? extends Matrix> result = MatrixFactory.getDenseDefault();
        assertSame( MatrixFactory.DEFAULT_DENSE_INSTANCE, result );
    }

    /**
     * Test of getSparseDefault method, of class MatrixFactory.
     */
    public void testGetSparseDefault()
    {
        System.out.println("getDefault");
        MatrixFactory<? extends Matrix> result = MatrixFactory.getSparseDefault();
        assertSame( MatrixFactory.DEFAULT_SPARSE_INSTANCE, result );
    }

    /**
     * Test of getDiagonalDefault method, of class MatrixFactory.
     */
    public void testGetDiagonalDefault()
    {
        System.out.println("getDiagonalDefault");
        MatrixFactory<? extends DiagonalMatrix> result =
            MatrixFactory.getDiagonalDefault();
        assertSame( MatrixFactory.DEFAULT_DIAGONAL_INSTANCE, result );
    }

    /**
     * Test of copyMatrix method, of class MatrixFactory.
     */
    public void testCopyMatrix()
    {
        System.out.println("copyMatrix");
        MatrixFactory<?> instance = this.createFactory();

        Matrix matrix = this.createRandomMatrix();
        assertNotNull( matrix );
        Matrix copy = instance.copyMatrix(matrix);
        assertNotNull( copy );
        assertNotSame( matrix, copy );
        assertEquals( matrix, copy );

        matrix.scaleEquals(random.nextDouble());
        assertFalse( matrix.equals(copy) );
    }

    /**
     * Test of copyArray method, of class MatrixFactory.
     */
    public void testCopyArray()
    {
        System.out.println("copyArray");

        MatrixFactory<?> instance = this.createFactory();
        Matrix matrix = this.createRandomMatrix();
        int M = matrix.getNumRows();
        int N = matrix.getNumColumns();
        double[][] values = new double[ M ][ N ];
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < N; j++ )
            {
                values[i][j] = matrix.getElement(i, j);
            }
        }

        Matrix copy = instance.copyArray(values);
        assertNotNull( copy );
        assertNotSame( matrix, copy );
        assertEquals( matrix, copy );

        matrix.scaleEquals(random.nextDouble());
        assertFalse( matrix.equals( copy ) );

        Matrix m00 = instance.copyArray( new double[0][0] );
        assertEquals( 0, m00.getNumRows() );
        assertEquals( 0, m00.getNumColumns() );

    }

    /**
     * Test of createMatrix method, of class MatrixFactory.
     */
    public void testCreateMatrix()
    {
        System.out.println("createMatrix");

        Matrix matrix = this.createRandomMatrix();
        int M = matrix.getNumRows();
        int N = matrix.getNumColumns();

        MatrixFactory<?> factory = this.createFactory();
        Matrix m2 = factory.createMatrix(M, N);
        assertNotNull( m2 );
        assertNotSame( matrix, m2 );

        assertEquals( M, m2.getNumRows() );
        assertEquals( N, m2.getNumColumns() );
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < N; j++ )
            {
                assertEquals( 0.0, m2.getElement(i, j) );
            }
        }

    }

    public void testCreateMatrixWithInitialValue()
    {
        Matrix matrix = this.createRandomMatrix();
        int M = matrix.getNumRows();
        int N = matrix.getNumColumns();

        MatrixFactory<?> factory = this.createFactory();
        double initialValue = this.random.nextGaussian();
        Matrix m3 = factory.createMatrix(M, N, initialValue);
        assertNotNull(m3);
        assertEquals(M, m3.getNumRows());
        assertEquals(N, m3.getNumColumns());

        boolean hasNonZero = false;
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                double value = m3.getElement(i, j);

                if (value != 0.0)
                {
                    hasNonZero = true;
                    assertEquals(initialValue, value, 0.0);
                }
            }
        }

        assertTrue(hasNonZero);
    }

    /**
     * Test of createIdentity method, of class MatrixFactory.
     */
    public void testCreateIdentity()
    {
        System.out.println("createIdentity");
        Matrix matrix = this.createRandomMatrix();
        int M = matrix.getNumRows();
        int N = matrix.getNumColumns();
        MatrixFactory<?> instance = this.createFactory();
        Matrix ident = instance.createIdentity(M, N);
        assertNotNull( ident );
        assertNotSame( matrix, ident );
        assertEquals( M, ident.getNumRows() );
        assertEquals( N, ident.getNumColumns() );
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < N; j++ )
            {
                if( i == j )
                {
                    assertEquals( 1.0, ident.getElement(i, j) );
                }
                else
                {
                    assertEquals( 0.0, ident.getElement(i, j) );
                }
            }
        }

    }

    /**
     * Test of createUniformRandom method, of class MatrixFactory.
     */
    public void testCreateUniformRandom()
    {
        System.out.println("createUniformRandom");

        Matrix m = this.createRandomMatrix();
        MatrixFactory<?> factory = this.createFactory();

        int M = m.getNumRows();
        int N = m.getNumColumns();
        Matrix mr = factory.createUniformRandom(M, N, -RANGE, RANGE, random);
        assertNotNull( mr );
        assertNotSame( m, mr );
        assertEquals( M, mr.getNumRows() );
        assertEquals( N, mr.getNumColumns() );

        boolean nonzero = false;
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < N; j++ )
            {
                double value = mr.getElement(i, j);
                if( value != 0.0 )
                {
                    nonzero = true;
                    if( (value < -RANGE) || (value > RANGE) )
                    {
                        fail( "Nonzero value outside the given range!" );
                    }
                }
            }
        }

        if( !nonzero )
        {
            fail( "I didn't find any nonzero values in your random matrix!!" );
        }

    }

    /**
     * Test of copyRowVectors method, of class MatrixFactory.
     */
    public void testCopyRowVectors_Collection()
    {
        System.out.println("copyRowVectors");

        Matrix m = this.createRandomMatrix();
        int M = m.getNumRows();
        int N = m.getNumColumns();

        ArrayList<Vector> rows = new ArrayList<Vector>( M );
        for( int i = 0; i < M; i++ )
        {
            rows.add( m.getRow(i) );
        }

        MatrixFactory<?> factory = this.createFactory();
        @SuppressWarnings("unchecked")
        Matrix mr = factory.copyRowVectors(rows);
        assertNotNull( mr );
        assertNotSame( m, mr );
        assertEquals( m, mr );
        for( int i = 0; i < M; i++ )
        {
            assertEquals( m.getRow(i), mr.getRow(i) );
        }

    }

    /**
     * Test of copyRowVectors method, of class MatrixFactory.
     */
    public void testCopyRowVectors_VectorizableArr()
    {
        System.out.println("copyRowVectors");
        Matrix m = this.createRandomMatrix();
        int M = m.getNumRows();
        int N = m.getNumColumns();

        Vector[] rows = new Vector[ M ];
        for( int i = 0; i < M; i++ )
        {
            rows[i] = m.getRow(i);
        }

        MatrixFactory<?> factory = this.createFactory();
        @SuppressWarnings("unchecked")
        Matrix mr = factory.copyRowVectors(rows);
        assertNotNull( mr );
        assertNotSame( m, mr );
        assertEquals( m, mr );
        for( int i = 0; i < M; i++ )
        {
            assertEquals( m.getRow(i), mr.getRow(i) );
        }
    }

    /**
     * Test of copyColumnVectors method, of class MatrixFactory.
     */
    public void testCopyColumnVectors_Collection()
    {
        System.out.println("copyColumnVectors");
        Matrix m = this.createRandomMatrix();
        int M = m.getNumRows();
        int N = m.getNumColumns();

        ArrayList<Vector> cols = new ArrayList<Vector>( M );
        for( int j = 0; j < N; j++ )
        {
            cols.add( m.getColumn(j) );
        }

        MatrixFactory<?> factory = this.createFactory();
        @SuppressWarnings("unchecked")
        Matrix mr = factory.copyColumnVectors(cols);
        assertNotNull( mr );
        assertNotSame( m, mr );
        assertEquals( m, mr );
        for( int j = 0; j < N; j++ )
        {
            assertEquals( m.getColumn(j), mr.getColumn(j) );
        }
    }

    /**
     * Test of copyColumnVectors method, of class MatrixFactory.
     */
    public void testCopyColumnVectors_VectorizableArr()
    {
        System.out.println("copyColumnVectors");
        Matrix m = this.createRandomMatrix();
        int M = m.getNumRows();
        int N = m.getNumColumns();

        Vector[] cols = new Vector[ N ];
        for( int j = 0; j < N; j++ )
        {
            cols[j] = m.getColumn(j);
        }

        MatrixFactory<?> factory = this.createFactory();
        @SuppressWarnings("unchecked")
        Matrix mr = factory.copyColumnVectors(cols);
        assertNotNull( mr );
        assertNotSame( m, mr );
        assertEquals( m, mr );
        for( int j = 0; j < N; j++ )
        {
            assertEquals( m.getColumn(j), mr.getColumn(j) );
        }
    }

    /**
     * Test of createDiagonal method, of class MatrixFactory.
     */
    public void testCreateDiagonal()
    {
        System.out.println("createDiagonal");
        Vector diagonal = VectorFactory.getDefault().copyValues(
            random.nextGaussian(), random.nextGaussian(), random.nextGaussian() );
        int M = diagonal.getDimensionality();
        MatrixFactory<?> instance = this.createFactory();
        Matrix diag = instance.createDiagonal(diagonal);
        assertNotNull( diag );
        assertEquals( M, diag.getNumRows() );
        assertEquals( M, diag.getNumColumns() );
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < M; j++ )
            {
                if( i == j )
                {
                    assertEquals( diagonal.getElement(i), diag.getElement(i, j) );
                }
                else
                {
                    assertEquals( 0.0, diag.getElement(i, j) );
                }
            }

        }

    }

}

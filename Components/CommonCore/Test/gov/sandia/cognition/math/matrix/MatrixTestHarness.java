/*
 * File:                MatrixTestHarness.java
 * Authors:             Kevin Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 31, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.math.RingTestHarness;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import java.text.DecimalFormat;
import java.util.Iterator;

/**
 * Test suite for all implementations of Matrix
 * @author krdixon
 * @since 1.0
 */
abstract public class MatrixTestHarness
    extends RingTestHarness<Matrix>
{

    /**
     * Creates a matrix with the specified dimensions
     * @param numRows
     * Number of rows
     * @param numColumns
     * Number of columns
     * @return
     * Matrix with the specified dimensions
     */
    abstract protected Matrix createMatrix( int numRows, int numColumns );

    /**
     * Copies the given matrix into the local class's representation
     * @param matrix
     * Matrix to copy
     * @return
     * Copies matrix
     */
    abstract protected Matrix createCopy( Matrix matrix );

    /**
     * Creates a random matrix of the given dimensions with values within the
     * given range
     * @param numRows
     * Number of rows
     * @param numColumns
     * Number of columns
     * @param minRange
     * min value of elements
     * @param maxRange
     * max value of elements
     * @return
     * Random matrix
     */
    protected Matrix createRandom( 
        int numRows, int numColumns, double minRange, double maxRange )
    {
        return this.createCopy( MatrixFactory.getDefault().createUniformRandom( numRows, numColumns, minRange, maxRange, RANDOM  ) );
    }

    /**
     * Default dimension of the matrices
     */
    protected int DEFAULT_DIM = RANDOM.nextInt( 10 ) + 10;

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    protected Matrix createRandom()
    {
        int M = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        int N = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        return this.createRandom( M, N, -RANGE, RANGE );
    }

    /**
     * Constructor
     * @param testName
     * Name of the test
     */
    protected MatrixTestHarness( String testName )
    {
        super( testName );
    }

    /**
     * Test of clone method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testConstructorIJ()
    {
        System.out.println( "constructorIJ" );

        this.createMatrix( 0, 0 );
        this.createMatrix( 10, 10 );
        this.createMatrix( 10, 0 );
        this.createMatrix( 0, 10 );
        this.createMatrix( 10, 1 );
        this.createMatrix( 1, 10 );

        try
        {
            Matrix m1 = this.createMatrix( -1, 10 );
            fail( "Should have thrown exception " + m1.getClass() );
        }
        catch (Exception e)
        {
        }

        try
        {
            Matrix m1 = this.createMatrix( 10, -1 );
            fail( "Should have thrown exception " + m1.getClass() );
        }
        catch (Exception e)
        {
        }

        try
        {
            Matrix m1 = this.createMatrix( -1, -1 );
            fail( "Should have thrown exception " + m1.getClass() );
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of getNumRows method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testGetNumRows()
    {
        System.out.println( "getNumRows" );

        int M = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        int N = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        double range = 10;
        assertEquals( M, this.createMatrix( M, N ).getNumRows() );
        assertEquals( M, this.createRandom( M, N, -range, range ).getNumRows() );

    }

    /**
     * Test of getNumColumns method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testGetNumColumns()
    {
        System.out.println( "getNumColumns" );

        int M = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        int N = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        double range = 10;
        assertEquals( N, this.createMatrix( M, N ).getNumColumns() );
        assertEquals( N, this.createRandom( M, N, -range, range ).getNumColumns() );
    }

    /**
     * Test of getElement method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testGetElement()
    {
        System.out.println( "getElement" );

        Matrix instance = this.createRandom();
        int M = instance.getNumRows();
        int N = instance.getNumColumns();

        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                double element = RANDOM.nextDouble();
                instance.setElement( i, j, element );
                assertEquals( "getElement(" + i + "," + j + ") failed.",
                    instance.getElement( i, j ), element );
            }
        }

        int badRows[] = {-10 * M, -1, M, 10 * M};
        for (int i = 0; i < badRows.length; i++)
        {
            for (int j = 0; j < N; j++)
            {
                try
                {
                    instance.getElement( badRows[i], j );
                    fail( "Should have thrown out of bounds exception for row = " + badRows[i] );
                }
                catch (Exception e)
                {
                }
            }
        }


        int badColumns[] = {-10 * N, -1, N, 10 * N};
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < badColumns.length; j++)
            {
                try
                {
                    instance.getElement( i, badColumns[j] );
                    fail( "Should have thrown out of bounds exception for column = " + badColumns[j] );
                }
                catch (Exception e)
                {
                }
            }
        }

        for (int i = 0; i < badRows.length; i++)
        {
            for (int j = 0; j < badColumns.length; j++)
            {
                try
                {
                    instance.getElement( badRows[i], badColumns[j] );
                    fail( "Should have thrown out of bounds exception for row = " + badRows[i] + ", column = " + badColumns[j] );
                }
                catch (Exception e)
                {
                }

            }
        }


    }

    /**
     * Test of setElement method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testSetElement()
    {
        System.out.println( "setElement" );

        Matrix instance = this.createRandom();
        int M = instance.getNumRows();
        int N = instance.getNumColumns();

        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                double element = RANDOM.nextDouble() + RANGE;
                assertTrue( instance.getElement( i, j ) != element );

                instance.setElement( i, j, element );
                assertEquals( "setElement(" + i + "," + j + "," + element + ") failed.",
                    instance.getElement( i, j ), element );
            }
        }

        int badRows[] = {-10 * M, -1, M, 10 * M};
        for (int i = 0; i < badRows.length; i++)
        {
            for (int j = 0; j < N; j++)
            {
                try
                {
                    double element = RANDOM.nextDouble() + RANGE;
                    instance.setElement( badRows[i], j, element );
                    fail( "Should have thrown out of bounds exception for row = " + badRows[i] );
                }
                catch (Exception e)
                {
                }
            }
        }


        int badColumns[] = {-10 * N, -1, N, 10 * N};
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < badColumns.length; j++)
            {
                try
                {
                    double element = RANDOM.nextDouble() + RANGE;
                    instance.setElement( i, badColumns[j], element );
                    fail( "Should have thrown out of bounds exception for column = " + badColumns[j] );
                }
                catch (Exception e)
                {
                }
            }
        }

        for (int i = 0; i < badRows.length; i++)
        {
            for (int j = 0; j < badColumns.length; j++)
            {
                try
                {
                    double element = RANDOM.nextDouble() + RANGE;
                    instance.setElement( badRows[i], badColumns[j], element );
                    fail( "Should have thrown out of bounds exception for row = " + badRows[i] + ", column = " + badColumns[j] );
                }
                catch (Exception e)
                {
                }

            }
        }
    }

    /**
     * Test of getSubMatrix method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testGetSubMatrix()
    {
        System.out.println( "getSubMatrix" );

        int M = RANDOM.nextInt( DEFAULT_DIM ) + 10;
        int N = RANDOM.nextInt( DEFAULT_DIM ) + 10;

        double range = 10.0;
        Matrix instance = this.createRandom( M, N, -range, range );

        int minM = RANDOM.nextInt( M - 1 );
        int minN = RANDOM.nextInt( N - 1 );

        int dM = M - minM;
        int dN = N - minN;

        int maxM = RANDOM.nextInt(dM) + minM;
        int maxN = RANDOM.nextInt(dN) + minN;

        System.out.printf( "getSubMatrix(%d,%d,%d,%d) from (%dx%d) matrix\n", minM, maxM, minN, maxN, M, N );
        Matrix subMatrix = instance.getSubMatrix( minM, maxM, minN, maxN );
        assertEquals( "Num rows for submatrix is wrong.",
            subMatrix.getNumRows(), maxM - minM + 1 );
        assertEquals( "Num columns for submatrix is wrong.",
            subMatrix.getNumColumns(), maxN - minN + 1 );

        for (int i = 0; i < subMatrix.getNumRows(); i++)
        {
            for (int j = 0; j < subMatrix.getNumColumns(); j++)
            {
                assertEquals( subMatrix.getElement( i, j ),
                    instance.getElement( i + minM, j + minN ) );
            }
        }

        subMatrix = instance.getSubMatrix( minM, minM, minN, minN );
        assertEquals( 1, subMatrix.getNumRows() );
        assertEquals( 1, subMatrix.getNumColumns() );
        assertEquals( subMatrix.getElement( 0, 0 ), instance.getElement( minM, minN ) );

        int badMinRows[] = {-10 * M, -1, M, 10 * M, maxM + 1, maxM + 100};
        for (int i = 0; i < badMinRows.length; i++)
        {
            try
            {
                subMatrix = instance.getSubMatrix( badMinRows[i], maxM, minN, maxN );
                fail( "Should have thrown exception with minRow = " + badMinRows[i] + ", maxRow = " + maxM + ", type = " + subMatrix.getClass() );
            }
            catch (Exception e)
            {
            }
        }

        int badMinCols[] = {-10 * N, -1, N, 10 * N, maxN + 1, maxN + 100};
        for (int j = 0; j < badMinCols.length; j++)
        {
            try
            {
                subMatrix = instance.getSubMatrix( minM, maxM, badMinCols[j], maxN );
                fail( "Should have thrown exception with minRow = " + badMinCols[j] + ", maxCol = " + maxN );
            }
            catch (Exception e)
            {
            }
        }

        for (int i = 0; i < badMinRows.length; i++)
        {
            for (int j = 0; j < badMinCols.length; j++)
            {
                try
                {
                    subMatrix = instance.getSubMatrix( badMinRows[i], maxM, badMinCols[j], maxN );
                    fail( "Should have thrown exception" );
                }
                catch (Exception e)
                {
                }
            }
        }

    }

    /**
     * Test of setSubMatrix method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testSetSubMatrix()
    {
        System.out.println( "setSubMatrix" );

        Matrix submatrix = this.createRandom();
        int M = submatrix.getNumRows();
        int N = submatrix.getNumColumns();
        Matrix instance = this.createRandom( M * 2, N * 2, RANGE * 2.0, RANGE * 10.0 );
        int iM = instance.getNumRows();
        int iN = instance.getNumColumns();

        int dM = (iM - M) + 1;
        int dN = (iN - N) + 1;

        for (int startRow = 0; startRow < dM; startRow++)
        {
            for (int startCol = 0; startCol < dN; startCol++)
            {
                instance.setSubMatrix( startRow, startCol, submatrix );
                assertEquals( submatrix, instance.getSubMatrix( startRow, startRow + M - 1, startCol, startCol + N - 1 ) );
            }
        }

        int badStartRows[] = {-10 * iM, -1, dM, iM};
        for (int i = 0; i < badStartRows.length; i++)
        {
            for (int startCol = 0; startCol < dN; startCol++)
            {
                try
                {
                    instance.setSubMatrix( badStartRows[i], startCol, submatrix );
                    fail( "Should have thrown exception on start row = " + badStartRows[i] );
                }
                catch (Exception e)
                {
                }
            }
        }

        int badStartCols[] = {-10 * iN, -1, dN, iN};
        for (int startRow = 0; startRow < dM; startRow++)
        {
            for (int j = 0; j < badStartCols.length; j++)
            {
                try
                {
                    instance.setSubMatrix( startRow, badStartCols[j], submatrix );
                    fail( "Should have thrown exception on start col = " + badStartCols[j] );
                }
                catch (Exception e)
                {
                }
            }
        }

        for (int i = 0; i < badStartRows.length; i++)
        {
            for (int j = 0; j < badStartCols.length; j++)
            {
                try
                {
                    instance.setSubMatrix( badStartRows[i], badStartCols[j], submatrix );
                    fail( "Should have thrown exception." );
                }
                catch (Exception e)
                {

                }

            }
        }

    }

    /**
     * Test of equals method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testEquals()
    {
        System.out.println( "equals" );

        Matrix instance = this.createRandom();
        int M = instance.getNumRows();
        int N = instance.getNumColumns();
        instance.setElement( 0, 0, 1.0 );

        Matrix clone = instance.clone();

        assertTrue( instance.equals( instance ) );
        assertTrue( instance.equals( clone ) );
        assertTrue( instance.equals( (Object) clone ) );
        assertTrue( instance.equals( clone, 0.0 ) );
        assertTrue( instance.equals( clone, RANDOM.nextDouble() ) );
        
        assertFalse(this.createMatrix(N, M).equals(this.createMatrix(M + 1, N + 1)));

        Matrix pusher = instance.clone();

        double tolerance = 2.0;
        pusher.setElement( 0, 0, instance.getElement( 0, 0 ) + tolerance );
        assertFalse(instance.equals(pusher));
        assertTrue( instance.equals( pusher, tolerance ) );
        pusher.setElement( 0, 0, instance.getElement( 0, 0 ) - tolerance );
        assertFalse(instance.equals(pusher));
        assertTrue( instance.equals( pusher, tolerance ) );

        Matrix skewer = this.createRandom( M, N, -tolerance, tolerance );
        assertTrue( instance.equals( clone.plus( skewer ), tolerance ) );

        int badRows[] = {M - 1, M + 1, 10 * M};
        for (int i = 0; i < badRows.length; i++)
        {
            Matrix barfer = this.createRandom(
                badRows[i], M, RANGE * 2, RANGE * 10 );
            assertFalse( instance.equals(barfer) );
        }

        int badCols[] = {N - 1, N + 1, 10 * N};
        for (int j = 0; j < badCols.length; j++)
        {
            Matrix barfer = this.createRandom(
                M, badCols[j], RANGE * 2, RANGE * 10 );
            assertFalse( instance.equals( barfer ) );
        }

        for (int i = 0; i < badRows.length; i++)
        {
            for (int j = 0; j < badCols.length; j++)
            {
                Matrix barfer = this.createRandom(
                    badRows[i], badCols[j], RANGE * 2, RANGE * 10 );
                assertFalse( instance.equals( barfer ) );
            }
        }

    }
    
    /**
     * Test of hashCode method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testHashCode()
    {
        System.out.println( "hashCode" );

        Matrix instance = this.createRandom();
        int M = instance.getNumRows();
        int N = instance.getNumColumns();
        instance.setElement( 0, 0, 1.0 );

        Matrix clone = instance.clone();

        assertEquals(instance.hashCode(), instance.hashCode());
        assertEquals(instance.hashCode(), clone.hashCode());

        Matrix changed = instance.clone();
        int i = RANDOM.nextInt(M);
        int j = RANDOM.nextInt(N);
        double delta = RANDOM.nextDouble();
        changed.setElement(i, j, delta);
        
        assertFalse(instance.hashCode() == changed.hashCode());
        
        Matrix otherSize = this.createMatrix(M + 1, N);
        otherSize.setSubMatrix(0, 0, instance);
        assertFalse(instance.hashCode() == otherSize.hashCode());
    }

    /**
     * Test of isSymmetric method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testIsSymmetric()
    {
        System.out.println( "isSymmetric" );

        Matrix sqrt = this.createRandom();
        Matrix instance = sqrt.times( sqrt.transpose() );
        int M = instance.getNumRows();
        int N = instance.getNumColumns();

        assertTrue( instance.isSymmetric() );
        assertTrue( this.createMatrix( M, N ).isSymmetric() );

        try
        {
            this.createMatrix( M + 1, N ).isSymmetric();
            fail( "Matrices of unequal dimension should throw exception" );
        }
        catch (Exception e)
        {
        }

        try
        {
            this.createMatrix( M - 1, N ).isSymmetric();
            fail( "Matrices of unequal dimension should throw exception" );
        }
        catch (Exception e)
        {
        }

        try
        {
            this.createMatrix( M, N - 1 ).isSymmetric();
            fail( "Matrices of unequal dimension should throw exception" );
        }
        catch (Exception e)
        {
        }

        try
        {
            this.createMatrix( M, N + 1 ).isSymmetric();
            fail( "Matrices of unequal dimension should throw exception" );
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of checkSameDimensions method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testCheckSameDimensions()
    {
        System.out.println( "checkSameDimensions" );

        int M1 = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        int M2 = RANDOM.nextInt( DEFAULT_DIM ) + 1 + M1;

        int N1 = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        int N2 = RANDOM.nextInt( DEFAULT_DIM ) + 1 + N1;

        Matrix m11 = this.createMatrix( M1, N1 );
        Matrix m12 = this.createMatrix( M1, N2 );
        Matrix m21 = this.createMatrix( M2, N1 );

        assertTrue( m11.checkSameDimensions( m11 ) );
        assertFalse( m11.checkSameDimensions( m12 ) );
        assertFalse( m12.checkSameDimensions( m11 ) );
        assertTrue( m12.checkSameDimensions( m12 ) );
        assertTrue( m21.checkSameDimensions( m21 ) );
        assertFalse( m21.checkSameDimensions( m11 ) );
        assertFalse( m12.checkSameDimensions( m21 ) );

        try
        {
            m11.checkSameDimensions( null );
            fail( "Should have thrown null-pointer exception" );
        }
        catch (Exception e)
        {
        }


    }

    /**
     * Test of assertSameDimensions method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testAssertSameDimensions()
    {
        System.out.println( "assertSameDimensions" );

        int M1 = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        int M2 = RANDOM.nextInt( DEFAULT_DIM ) + 1 + M1;

        int N1 = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        int N2 = RANDOM.nextInt( DEFAULT_DIM ) + 1 + N1;

        Matrix m11 = this.createMatrix( M1, N1 );
        Matrix m12 = this.createMatrix( M1, N2 );

        // This is much simpler than the test for
        // checkSameDimensions because if that method is
        // fine, then this method is as well.

        try
        {
            m11.assertSameDimensions( m11 );
        }
        catch (Exception e)
        {
            fail( "Inproper thrown assertion" );
        }

        try
        {
            m11.assertSameDimensions( m12 );
            fail( "Should have thrown assertion" );
        }
        catch (Exception e)
        {
        }

        try
        {
            m11.assertSameDimensions( null );
            fail( "Should have thrown null-pointer exception" );
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of checkMultiplicationDimensions method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testCheckMultiplicationDimensions()
    {
        System.out.println( "checkMultiplicationDimensions" );

        int D1 = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        int D2 = RANDOM.nextInt( DEFAULT_DIM ) + 1 + D1;
        int D3 = RANDOM.nextInt( DEFAULT_DIM ) + 1 + D2;

        Matrix m12 = this.createMatrix( D1, D2 );
        Matrix m21 = this.createMatrix( D2, D1 );
        Matrix m23 = this.createMatrix( D2, D3 );

        assertTrue( m12.checkMultiplicationDimensions( m21 ) );
        assertTrue( m21.checkMultiplicationDimensions( m12 ) );
        assertTrue( m12.checkMultiplicationDimensions( m23 ) );

        assertFalse( m23.checkMultiplicationDimensions( m12 ) );
        assertFalse( m23.checkMultiplicationDimensions( m21 ) );

    }

    /**
     * Test of plusEquals method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testPlusEquals()
    {
        System.out.println( "plusEquals" );

        Matrix mr1 = this.createRandom();

        int M = mr1.getNumRows();
        int N = mr1.getNumColumns();

        Matrix mr2 = this.createRandom( M, N, -RANGE, RANGE );

        Matrix r1 = mr1.clone();
        r1.plusEquals( mr2 );
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                assertEquals( r1.getElement( i, j ), mr1.getElement( i, j ) + mr2.getElement( i, j ) );
            }
        }

        Vector3 column1 = new Vector3( 1.0, 2.0, 3.0 );
        Vector3 column2 = new Vector3( 4.0, 5.0, 6.0 );
        Vector3 column3 = new Vector3( 7.0, -8.0, 9.0 );

        Matrix m1 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column2, column3 ) );

        column1 = new Vector3( 7.0, -8.0, 9.0 );
        column2 = new Vector3( 1.0, 2.0, 3.0 );
        column3 = new Vector3( 4.0, 5.0, 6.0 );

        Matrix m2 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column2, column3 ) );

        Vector3 rc1 = new Vector3( 8, -6, 12 );
        Vector3 rc2 = new Vector3( 5, 7, 9 );
        Vector3 rc3 = new Vector3( 11, -3, 15 );

        Matrix e1 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( rc1, rc2, rc3 ) );
        assertFalse( e1.equals( m1 ) );

        m1.plusEquals( m2 );
        assertEquals( e1, m1 );

        N = RANDOM.nextInt( DEFAULT_DIM ) + 1 + m1.getNumColumns();
        Matrix m3 = this.createMatrix( m1.getNumRows(), N );
        try
        {
            m3.plusEquals( m1 );
            fail( "Did not throw exception" );
        }
        catch (Exception e)
        {
        }

        M = RANDOM.nextInt( DEFAULT_DIM ) + 1 + m1.getNumRows();
        Matrix m4 = MatrixFactory.getDefault().createMatrix( M, m1.getNumColumns() );
        try
        {
            m4.plusEquals( m1 );
            fail( "Did not throw exception" );
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of dotTimesEquals method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testDotTimesEquals()
    {
        System.out.println( "dotTimesEquals" );

        // This test assumes that plusEquals works and is tested
        Matrix m1 = this.createRandom();
        int M = m1.getNumRows();
        int N = m1.getNumColumns();
        Matrix m2 = this.createRandom( M, N, -RANGE, RANGE );

        Matrix r1 = m1.clone();
        r1.dotTimesEquals( m2 );

        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                assertEquals( r1.getElement( i, j ), m1.getElement( i, j ) * m2.getElement( i, j ) );
            }
        }

        Matrix r2 = m2.clone();
        r2.dotTimesEquals( m1 );
        assertEquals( r1, r2 );

        int M2 = M + 1;
        int N2 = N - 1;

        Matrix m3 = this.createRandom( M2, N, -RANGE, RANGE );
        try
        {
            r1.dotTimesEquals( m3 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

        Matrix m4 = this.createRandom( M, N2, -RANGE, RANGE );
        try
        {
            r1.dotTimesEquals( m4 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

        Matrix m5 = this.createRandom( M2, N2, -RANGE, RANGE );
        try
        {
            r1.dotTimesEquals( m5 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of transpose method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testTranspose()
    {
        System.out.println( "transpose" );

        Matrix m1 = this.createRandom();
        Matrix r1 = m1.transpose();

        assertEquals( m1.getNumRows(), r1.getNumColumns() );
        assertEquals( m1.getNumColumns(), r1.getNumRows() );

        int M = m1.getNumRows();
        int N = m1.getNumColumns();
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                assertEquals( m1.getElement( i, j ), r1.getElement( j, i ) );
            }
        }

    }

    /**
     * Test of times method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testTimesMatrix()
    {
        System.out.println( "times(Matrix)" );

        Vector3 column1 = new Vector3( 1.0, 2.0, 3.0 );
        Vector3 column2 = new Vector3( 4.0, 5.0, 6.0 );
        Vector3 column3 = new Vector3( 7.0, -8.0, 9.0 );
        Matrix m1 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column2, column3 ) );

        Matrix m2 = this.createCopy( MatrixFactory.getDefault().createMatrix( 3, 4 ) );
        m2.setElement( 0, 0, 1.0 );
        m2.setElement( 0, 1, 2.0 );
        m2.setElement( 0, 2, 3.0 );
        m2.setElement( 0, 3, 4.0 );

        m2.setElement( 1, 0, 5.0 );
        m2.setElement( 1, 1, 6.0 );
        m2.setElement( 1, 2, -1.0 );
        m2.setElement( 1, 3, -2.0 );

        m2.setElement( 2, 0, -3.0 );
        m2.setElement( 2, 1, -4.0 );
        m2.setElement( 2, 2, -5.0 );
        m2.setElement( 2, 3, -6.0 );

        Matrix r1 = m1.times( m2 );

        Matrix m3 = this.createCopy( MatrixFactory.getDefault().createMatrix( 3, 4 ) );
        m3.setElement( 0, 0, 0 );
        m3.setElement( 0, 1, -2 );
        m3.setElement( 0, 2, -36 );
        m3.setElement( 0, 3, -46 );

        m3.setElement( 1, 0, 51 );
        m3.setElement( 1, 1, 66 );
        m3.setElement( 1, 2, 41 );
        m3.setElement( 1, 3, 46 );

        m3.setElement( 2, 0, 6 );
        m3.setElement( 2, 1, 6 );
        m3.setElement( 2, 2, -42 );
        m3.setElement( 2, 3, -54 );

        assertEquals( m3, r1 );

        try
        {
            m2.times( m1 );
            fail( "Did not throw exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Threw exception: " + e );
        }

        Matrix m4 = this.createMatrix( 10, 10 );
        try
        {
            m4.times( m1 );
            fail( "Did not throw exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Threw exception: " + e );
        }

    }

    /**
     * Test of times(Vector) method,
     * of class gov.sandia.isrc.math.matrix.mtj.AbstractMTJMatrix.
     */
    public void testTimesVector()
    {
        System.out.println( "times(Vector)" );

        Vector3 column1 = new Vector3( 1.0, 2.0, 3.0 );
        Vector3 column2 = new Vector3( 4.0, 5.0, 6.0 );
        Vector3 column3 = new Vector3( 7.0, -8.0, 9.0 );

        Matrix m1 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column2, column3 ) );
        Vector v1 = new Vector3( -3.14, 0.0, 2.71 );

        Vector e1 = new Vector3( 15.830, -27.960, 14.970 );

        Vector r1 = m1.times( v1 );

        assertTrue( e1.equals( r1, 0.001 ) );

        Vector v2 = VectorFactory.getDefault().createVector( 10 );
        try
        {
            m1.times( v2 );
            fail( "Did not throw exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Threw exception: " + e );
        }
    }

    /**
     * Test of scaleEquals method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testScaleEquals()
    {
        System.out.println( "scaleEquals" );

        double scaleFactor = (RANDOM.nextDouble() * 2.0 * RANGE) - RANGE;
        Matrix instance = this.createRandom();

        Matrix clone = instance.clone();

        instance.scaleEquals( scaleFactor );

        int M = instance.getNumRows();
        int N = instance.getNumColumns();
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                assertEquals( instance.getElement( i, j ), clone.getElement( i, j ) * scaleFactor );
            }
        }

    }

    /**
     * Test of inverse method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testInverse()
    {
        System.out.println( "inverse" );

        Vector3 column1 = new Vector3( 1.0, 2.0, 3.0 );
        Vector3 column2 = new Vector3( 4.0, 5.0, 6.0 );
        Vector3 column3 = new Vector3( 7.0, -8.0, 9.0 );
        Matrix m1 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column2, column3 ) );
        Matrix r1 = m1.inverse();

        Vector3 rc1 = new Vector3( -0.968750, 0.437500, 0.031250 );
        Vector3 rc2 = new Vector3( -0.062500, 0.125000, -0.062500 );
        Vector3 rc3 = new Vector3( 0.697917, -0.229167, 0.031250 );
        Matrix e1 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( rc1, rc2, rc3 ) );

        assertTrue( e1.equals( r1, 0.00001 ) );

        Matrix m2 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column1, column3 ) );
        Matrix test;
        try
        {
            test = m2.inverse();
            System.out.println( "Uh-oh... Singular matrix inverse:\n" + test );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Threw exception: " + e );
        }

        Matrix m3 = this.createMatrix( 10, 6 );
        try
        {
            test = m3.inverse();
            System.out.println( "Uh-oh... Singular matrix inverse:\n" + test );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Threw exception: " + e );
        }

        Matrix m4 = this.createMatrix( 4, 4 );
        try
        {
            test = m4.inverse();
            System.out.println( "Uh-oh... Singular matrix inverse:\n" + test );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Threw exception: " + e );
        }

    }

    /**
     * Test of pseudoInverse method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testPseudoInverse()
    {
        System.out.println( "pseudoInverse" );

        Matrix m2 = this.createMatrix( 3, 4 );
        m2.setElement( 0, 0, 1.0 );
        m2.setElement( 0, 1, 2.0 );
        m2.setElement( 0, 2, 3.0 );
        m2.setElement( 0, 3, 4.0 );

        m2.setElement( 1, 0, 5.0 );
        m2.setElement( 1, 1, 6.0 );
        m2.setElement( 1, 2, -1.0 );
        m2.setElement( 1, 3, -2.0 );

        m2.setElement( 2, 0, -3.0 );
        m2.setElement( 2, 1, -4.0 );
        m2.setElement( 2, 2, -5.0 );
        m2.setElement( 2, 3, -6.0 );

        Matrix r1 = m2.pseudoInverse();


        Matrix e1 = this.createMatrix( 4, 3 );
        e1.setElement( 0, 0, -1.611111 );
        e1.setElement( 0, 1, -0.111111 );
        e1.setElement( 0, 2, -1.000000 );

        e1.setElement( 1, 0, 1.407407 );
        e1.setElement( 1, 1, 0.240741 );
        e1.setElement( 1, 2, 0.833333 );

        e1.setElement( 2, 0, -0.981481 );
        e1.setElement( 2, 1, -0.148148 );
        e1.setElement( 2, 2, -0.666667 );

        e1.setElement( 3, 0, 0.685185 );
        e1.setElement( 3, 1, 0.018519 );
        e1.setElement( 3, 2, 0.333333 );

        assertTrue( e1.equals( r1, 0.00001 ) );

    }

    /**
     * Test of logDeterminant method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testLogDeterminant()
    {
        System.out.println( "logDeterminant" );

        Vector3 column1 = new Vector3( 1.0, 2.0, 3.0 );
        Vector3 column2 = new Vector3( 4.0, 5.0, 6.0 );
        Vector3 column3 = new Vector3( 7.0, -8.0, 9.0 );

        Matrix m1 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column2, column3 ) );
        ComplexNumber e1 = new ComplexNumber( 4.5643, 3.1416 );
        ComplexNumber r1 = m1.logDeterminant();
        assertTrue( e1.equals( r1, 0.0001 ) );

        Matrix m2 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column2, column2 ) );
        ComplexNumber r2 = m2.logDeterminant().computeExponent();
        ComplexNumber e2 = new ComplexNumber( 0.0, 0.0 );
        assertTrue( e2.equals( r2, 0.0001 ) );

        Matrix m3 = this.createMatrix( 5, 5 );
        ComplexNumber r3 = m3.logDeterminant().computeExponent();
        ComplexNumber e3 = new ComplexNumber( 0.0, 0.0 );
        assertTrue( e3.equals( r3, 0.0001 ) );

        Matrix m4 = this.createMatrix( 4, 3 );
        try
        {
            m4.logDeterminant();
            fail( "Did not throw exception" );
        }
        catch (Exception e)
        {
            System.out.println( "Good! Threw exception: " + e );
        }
    }

    /**
     * Test of trace method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testTrace()
    {
        System.out.println( "trace" );

        int M = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        Matrix m1 = this.createMatrix( M, M );
        assertEquals( 0.0, m1.trace() );

        double range = 10.0;
        int N = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        Matrix m2 = this.createRandom( N, N, -range, range );

        double sum = 0.0;
        for (int i = 0; i < N; i++)
        {
            sum += m2.getElement( i, i );
        }

        assertEquals( sum, m2.trace() );

        Matrix m3 = this.createMatrix( M, M + 1 );
        try
        {
            m3.trace();
            fail( "Can only take the trace of square matrices" );
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of rank method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testRank()
    {
        System.out.println( "rank" );

        Vector3 column1 = new Vector3( 1.0, 2.0, 3.0 );
        Vector3 column2 = new Vector3( 4.0, 5.0, 6.0 );
        Vector3 column3 = new Vector3( 7.0, -8.0, 9.0 );
        Matrix m1 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column2, column3 ) );

        assertEquals( 3, m1.rank() );

        Matrix m2 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column2, column2 ) );
        assertEquals( 2, m2.rank() );

        Matrix m3 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column1, column1 ) );
        assertEquals( 1, m3.rank(TOLERANCE) );

        Matrix m4 = this.createMatrix( 3, 3 );
        assertEquals( 0, m4.rank() );

    }

    /**
     * Test of normFrobenius method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testNormFrobenius()
    {
        System.out.println( "normFrobenius" );

        Matrix m1 = this.createRandom();

        double sumSquared = 0.0;
        int M = m1.getNumRows();
        int N = m1.getNumColumns();
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                double v = m1.getElement( i, j );
                sumSquared += v * v;
            }
        }

        assertEquals( Math.sqrt( sumSquared ), m1.normFrobenius(), 1e-5 );

    }

    /**
     * Test of isSquare method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testIsSquare()
    {
        System.out.println( "isSquare" );

        int d1 = RANDOM.nextInt( DEFAULT_DIM ) + 1;
        Matrix m1 = this.createMatrix( d1, d1 );
        assertTrue( m1.isSquare() );

        Matrix m2 = this.createMatrix( d1 + 1, d1 );
        assertFalse( m2.isSquare() );

        Matrix m3 = this.createMatrix( d1, d1 + 1 );
        assertFalse( m3.isSquare() );

    }

    /**
     * Test of solve method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testSolveMatrix()
    {
        System.out.println( "solveMatrix" );

        Vector3 column1 = new Vector3( 1.0, 2.0, 3.0 );
        Vector3 column2 = new Vector3( 4.0, 5.0, 6.0 );
        Vector3 column3 = new Vector3( 7.0, -8.0, 9.0 );
        Matrix m1 = this.createCopy( MatrixFactory.getDefault().copyColumnVectors( column1, column2, column3 ) );

        Matrix m2 = this.createMatrix( 3, 4 );
        m2.setElement( 0, 0, 1.0 );
        m2.setElement( 0, 1, 2.0 );
        m2.setElement( 0, 2, 3.0 );
        m2.setElement( 0, 3, 4.0 );

        m2.setElement( 1, 0, 5.0 );
        m2.setElement( 1, 1, 6.0 );
        m2.setElement( 1, 2, -1.0 );
        m2.setElement( 1, 3, -2.0 );

        m2.setElement( 2, 0, -3.0 );
        m2.setElement( 2, 1, -4.0 );
        m2.setElement( 2, 2, -5.0 );
        m2.setElement( 2, 3, -6.0 );

        Matrix r1 = m1.solve( m2 );

        Matrix e1 = this.createMatrix( 3, 4 );
        e1.setElement( 0, 0, -3.3750 );
        e1.setElement( 0, 1, -5.1042 );
        e1.setElement( 0, 2, -6.3333 );
        e1.setElement( 0, 3, -7.9375 );

        e1.setElement( 1, 0, 1.7500 );
        e1.setElement( 1, 1, 2.5417 );
        e1.setElement( 1, 2, 2.3333 );
        e1.setElement( 1, 3, 2.8750 );

        e1.setElement( 2, 0, -0.37500 );
        e1.setElement( 2, 1, -0.43750 );
        e1.setElement( 2, 2, 0.0 );
        e1.setElement( 2, 3, 0.06250 );

        assertTrue( e1.equals( r1, 1e-3 ) );

        for (int i = 0; i < 100; i++)
        {
            Matrix A = this.createRandom();
            int M = A.getNumColumns();
            int N = RANDOM.nextInt( DEFAULT_DIM ) + 1;
            Matrix X = this.createRandom( M, N, -10.0, 10.0 );
            Matrix B = A.times( X );
            Matrix Xhat = A.solve( B );
            Matrix Bhat = A.times( Xhat );
            if (!B.equals( Bhat, 1e-3 ))
            {
                System.out.println( "Failed on Matrix " + i );
                System.out.println( "Expecting:\n" + X );
                System.out.println( "Estimated:\n" + Xhat );
                System.out.println( "Direct Error Norm: " + X.minus( Xhat ).normFrobenius() );
                System.out.println( "Implied Error Norm: " + B.minus( Bhat ).normFrobenius() );
                fail( "Matrix solver error failed!" );
            }
        }


    }

    /**
     * Test of solve method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testSolveVector()
    {
        System.out.println( "solveVector" );

        Matrix m2 = this.createMatrix( 3, 4 );
        m2.setElement( 0, 0, 1.0 );
        m2.setElement( 0, 1, 2.0 );
        m2.setElement( 0, 2, 3.0 );
        m2.setElement( 0, 3, 4.0 );

        m2.setElement( 1, 0, 5.0 );
        m2.setElement( 1, 1, 6.0 );
        m2.setElement( 1, 2, -1.0 );
        m2.setElement( 1, 3, -2.0 );

        m2.setElement( 2, 0, -3.0 );
        m2.setElement( 2, 1, -4.0 );
        m2.setElement( 2, 2, -5.0 );
        m2.setElement( 2, 3, -6.0 );

        Vector v1 = new Vector3( 1.0, 2.0, -1.0 );
        Vector r2 = m2.solve( v1 );

        Vector e2 = VectorFactory.getDefault().createVector( 4 );
        e2.setElement( 0, -0.83333 );
        e2.setElement( 1, 1.05556 );
        e2.setElement( 2, -0.61111 );
        e2.setElement( 3, 0.38889 );

        assertTrue( m2.times( r2 ).equals( v1, 1e-2 ) );

        for (int i = 0; i < 100; i++)
        {
            Matrix A = this.createRandom();
            int M = A.getNumColumns();
            Vector x = VectorFactory.getDefault().createUniformRandom( M, -1.0, 1.0, RANDOM  );
            Vector b = A.times( x );
            Vector xhat = A.solve( b );
            Vector bhat = A.times( xhat );
            if (!b.equals( bhat, 1e-2 ))
            {
                System.out.println( "Failed on Matrix " + i );
                System.out.println( "Expecting:\n" + x );
                System.out.println( "Estimated:\n" + xhat );
                System.out.println( "Direct Error Norm: " + x.minus( xhat ).norm2() );
                System.out.println( "Implied Error Norm: " + b.minus( bhat ).norm2() );
                fail( "Matrix solver error failed!" );
            }

        }


    }

    /**
     * Test of zero method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    @Override
    public void testZero()
    {
        System.out.println( "zero" );

        super.testZero();
        
        Matrix m1 = this.createRandom( 10, 20, 1.0, 100.0 );
        int M = m1.getNumRows();
        int N = m1.getNumColumns();
        m1.zero();
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                assertEquals( m1.getElement( i, j ), 0.0 );
            }
        }

    }

    /**
     * Test of identity method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testIdentity()
    {
        System.out.println( "identity" );
        Matrix m1 = this.createRandom();
        int M = m1.getNumRows();
        int N = m1.getNumColumns();
        m1.identity();
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                assertEquals( m1.getElement( i, j ), (i == j) ? 1.0 : 0.0 );
            }
        }

    }

    /**
     * Test of getColumn method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testGetColumn()
    {
        System.out.println( "getColumn" );

        Matrix m1 = this.createRandom();
        int M = m1.getNumRows();
        int N = m1.getNumColumns();

        int c = RANDOM.nextInt( N );
        Vector column = m1.getColumn( c );

        assertEquals( column.getDimensionality(), M );

        for (int i = 0; i < M; i++)
        {
            assertEquals( column.getElement( i ), m1.getElement( i, c ) );
        }

    }

    /**
     * Test of getRow method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testGetRow()
    {
        System.out.println( "getRow" );

        Matrix m1 = this.createRandom();
        int M = m1.getNumRows();
        int N = m1.getNumColumns();

        int i = RANDOM.nextInt( M );
        Vector row = m1.getRow( i );

        assertEquals( row.getDimensionality(), N );

        for (int j = 0; j < N; j++)
        {
            assertEquals( row.getElement( j ), m1.getElement( i, j ) );
        }

        try
        {
            m1.getColumn( N );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

        try
        {
            m1.getColumn( -1 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of setColumn method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testSetColumn()
    {
        System.out.println( "setColumn" );

        Matrix m1 = this.createRandom();
        int M = m1.getNumRows();
        int N = m1.getNumColumns();

        int j1 = RANDOM.nextInt( N );
        Vector c1 = m1.getColumn( j1 );

        Matrix m2 = this.createRandom( M, N, -RANGE, RANGE );
        int c2 = RANDOM.nextInt( N );
        Matrix clone2 = m2.clone();
        assertEquals( m2, clone2 );
        m2.setColumn( j1, c1 );

        assertFalse( m2.equals( clone2 ) );

        assertEquals( m2.getColumn( j1 ), c1 );

        Matrix m3 = this.createMatrix( M + 1, N );
        try
        {
            m3.setColumn( j1, c1 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

        Matrix m4 = this.createMatrix( M - 1, N );
        try
        {
            m3.setColumn( j1, c1 );
        }
        catch (Exception e)
        {
        }

        try
        {
            m1.setColumn( -1, c1 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

        try
        {
            m1.setColumn( N, c1 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

        Matrix m5 = this.createMatrix( M, N + 1 );
        m5.setColumn( j1, c1 );

    }

    /**
     * Test of setRow method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testSetRow()
    {
        System.out.println( "setRow" );

        Matrix m1 = this.createRandom();
        int M = m1.getNumRows();
        int N = m1.getNumColumns();

        int i1 = RANDOM.nextInt( M );
        Vector r1 = m1.getRow( i1 );

        Matrix m2 = this.createRandom( M, N, -RANGE, RANGE );
        int i2 = RANDOM.nextInt( M );
        Matrix clone2 = m2.clone();
        assertEquals( m2, clone2 );
        m2.setRow( i1, r1 );

        assertFalse( m2.equals( clone2 ) );

        assertEquals( m2.getRow( i1 ), r1 );

        Matrix m3 = this.createMatrix( M, N + 1 );
        try
        {
            m3.setRow( i1, r1 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

        Matrix m4 = this.createMatrix( M, N - 1 );
        try
        {
            m3.setRow( i1, r1 );
        }
        catch (Exception e)
        {
        }

        try
        {
            m1.setRow( -1, r1 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

        try
        {
            m1.setRow( M, r1 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

        Matrix m5 = this.createMatrix( M + 1, N );
        m5.setRow( i1, r1 );

    }

    /**
     * Test of sumOfRows method, of class Matrix.
     */
    public void testSumOfRows()
    {
        Matrix m1 = this.createRandom();

        int M = m1.getNumRows();
        int N = m1.getNumColumns();
        final Vector expected = VectorFactory.getDefault().createVector(N);
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                expected.setElement(j, m1.getElement(i, j)
                    + expected.getElement(j));
            }
        }

        assertEquals(expected, m1.sumOfRows());
    }

    /**
     * Test of sumOfColumns method, of class Matrix.
     */
    public void testSumOfColumns()
    {
        Matrix m1 = this.createRandom();

        int M = m1.getNumRows();
        int N = m1.getNumColumns();
        final Vector expected = VectorFactory.getDefault().createVector(M);
        for (int i = 0; i < M; i++)
        {
            for (int j = 0; j < N; j++)
            {
                expected.setElement(i, m1.getElement(i, j)
                    + expected.getElement(i));
            }
        }

        assertEquals(expected, m1.sumOfColumns());

    }

    /**
     * Test of convertFromVector method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testConvertFromVector()
    {
        System.out.println( "convertFromVector" );

        Matrix m1 = this.createRandom();
        int M = m1.getNumRows();
        int N = m1.getNumColumns();

        Vector v1 = m1.convertToVector();

        Matrix m2 = this.createRandom( M, N, -RANGE, RANGE );
        assertFalse( m1.equals( m2 ) );

        m2.convertFromVector( v1 );
        assertEquals( m1, m2 );

        Matrix m3 = this.createRandom( M + 1, N, -RANGE, RANGE );
        try
        {
            m3.convertFromVector( v1 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

        Matrix m4 = this.createRandom( M, N + 1, -RANGE, RANGE );
        try
        {
            m4.convertFromVector( v1 );
            fail( "Should have thrown exception" );
        }
        catch (Exception e)
        {
        }

    }

    /**
     * Test of convertToVector method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testConvertToVector()
    {
        System.out.println( "convertToVector" );

        Matrix m1 = this.createRandom();
        int M = m1.getNumRows();
        int N = m1.getNumColumns();

        Vector v1 = m1.convertToVector();

        assertEquals( v1.getDimensionality(), M * N );

        Matrix m2 = this.createRandom( M, N, -RANGE, RANGE );
        assertFalse( m1.equals( m2 ) );

        m2.convertFromVector( v1 );
        Vector v2 = m2.convertToVector();
        assertEquals( m1, m2 );
        assertEquals( v1, v2 );

    }

    /**
     * Test of convertToVector method, of class gov.sandia.isrc.math.matrix.Matrix.
     */
    public void testIterator()
    {
        System.out.println( "convertToVector" );
        int M = 10;
        int N = 100;
        Matrix m1 = this.createRandom( M, N, 10.0, 100.0 );
        Iterator<MatrixEntry> iterator = m1.iterator();

        assertNotNull( iterator );
        assertTrue( iterator.hasNext() );

        assertNotNull( iterator.next() );

    }

    /**
     * Test of the toArray method.
     */
    public void testToArray()
    {
        Matrix m1 = this.createRandom();
        int M = m1.getNumRows();
        int N = m1.getNumColumns();

        double[][] result = m1.toArray();
        assertNotSame(result, m1.toArray());
        
        assertEquals(M, result.length);
        for (int i = 0; i < M; i++)
        {
            assertEquals(N, result[i].length);

            for (int j = 0; j < N; j++)
            {
                assertEquals(m1.getElement(i, j), result[i][j]);
            }
        }

        int i = this.RANDOM.nextInt(M);
        int j = this.RANDOM.nextInt(N);
        result[i][j] += 1;
        assertFalse(m1.getElement(i, j) == result[i][j]);

        i = this.RANDOM.nextInt(M);
        j = this.RANDOM.nextInt(N);
        m1.setElement(i, j, m1.getElement(i, j) * 2);
        assertFalse(m1.getElement(i, j) == result[i][j]);
    }

    /**
     * Test of toString() method
     */
    public void testToString()
    {
        System.out.println( "toString()" );

        Matrix m1 = this.createRandom();
        String s = m1.toString();
        System.out.println( "String:\n" + s );
        assertNotNull( s );
        assertTrue( s.length() > 0 );
    }

    /**
     * toString(NumberFormat)
     */
    public void testToStringFormat()
    {
        System.out.println( "toString(NumberFormat)" );
        Matrix m1 = this.createRandom();
        String s = m1.toString( new DecimalFormat() );
        System.out.println( "String:\n" + s );
        assertNotNull( s );
        assertTrue( s.length() > 0 );
    }

}

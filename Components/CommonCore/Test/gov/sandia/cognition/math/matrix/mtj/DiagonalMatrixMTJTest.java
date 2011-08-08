/*
 * File:                DiagonalMatrixMTJTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Sep 19, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */
package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.RingTestHarness;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit tests for class DiagonalMatrixMTJTest
 * @author Kevin R. Dixon
 */
public class DiagonalMatrixMTJTest
    extends RingTestHarness
{

    /**
     * Entry point for JUnit tests for class DiagonalMatrixMTJTest
     * @param testName name of this test
     */
    public DiagonalMatrixMTJTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Test
     * @return Test
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite(DiagonalMatrixMTJTest.class);
        
        return suite;
    }

    @Override
    protected DiagonalMatrixMTJ createRandom()
    {   
        int dim = RANDOM.nextInt( 10 ) + 2;
        DiagonalMatrixMTJ matrix = 
            DiagonalMatrixFactoryMTJ.INSTANCE.createMatrix( dim );
        double[] diagonal = matrix.getDiagonal();
        for( int i = 0; i < dim; i++ )
        {
            diagonal[i] = RANDOM.nextDouble() * 2.0 * RANGE - RANGE;
        }
        
        return matrix;
        
    }

    @Override
    public void testScaleEquals()
    {
        System.out.println( "scaleEquals" );
        
        DiagonalMatrixMTJ matrix = this.createRandom();
        DiagonalMatrixMTJ original = (DiagonalMatrixMTJ) matrix.clone();
        assertNotSame( matrix, original );
        assertEquals( matrix.getDimensionality(), original.getDimensionality() );
        assertEquals( matrix, original );
        
        double scale = RANDOM.nextDouble() * 2.0 * RANGE - RANGE;
        matrix.scaleEquals( scale );
        
        assertNotSame( matrix, original );
        assertFalse( matrix.equals( original ) );
        for( int i = 0; i < matrix.getDimensionality(); i++ )
        {
            assertEquals( matrix.getElement(i), original.getElement(i)*scale );
        }
        
    }

    @Override
    public void testPlusEquals()
    {
        System.out.println( "plusEquals" );
        
        DiagonalMatrixMTJ original = this.createRandom();
        DiagonalMatrixMTJ m1 = (DiagonalMatrixMTJ) original.clone();
        int M = m1.getDimensionality();
        DiagonalMatrixMTJ m2 = new DiagonalMatrixMTJ( M );
        for( int i = 0; i < M; i++ )
        {
            m2.setElement( i, RANDOM.nextDouble() );
        }
        
        assertEquals( original, m1 );
        
        DiagonalMatrixMTJ m2clone = (DiagonalMatrixMTJ) m2.clone();
        assertNotSame( m2clone, m2 );
        assertEquals( m2clone, m2 );
        m1.plusEquals( m2 );
        assertEquals( m2clone, m2 );
        assertFalse( original.equals( m1 ) );
        
        for( int i = 0; i < m1.getDimensionality(); i++ )
        {
            assertEquals( original.getElement(i) + m2clone.getElement(i), m1.getElement(i) );
        }
        
        DenseMatrix mbad = new DenseMatrix( M, M );
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < M; j++ )
            {
                mbad.setElement( i, j, RANDOM.nextDouble() * 2.0 * RANGE - RANGE );
            }
        }
        
        try
        {
            m1.plusEquals( mbad );
            fail( "Can't add off-diagonal elements to a diagonal matrix" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        // This should be fine
        mbad.plusEquals( m1 );
        
    }

    @Override
    public void testDotTimesEquals()
    {
        DiagonalMatrixMTJ original = this.createRandom();
        DiagonalMatrixMTJ m1 = (DiagonalMatrixMTJ) original.clone();
        int M = m1.getDimensionality();
        DiagonalMatrixMTJ m2 = new DiagonalMatrixMTJ( M );
        for( int i = 0; i < M; i++ )
        {
            m2.setElement( i, RANDOM.nextDouble() );
        }
        
        assertEquals( original, m1 );
        
        DiagonalMatrixMTJ m2clone = (DiagonalMatrixMTJ) m2.clone();
        assertNotSame( m2clone, m2 );
        assertEquals( m2clone, m2 );
        m1.dotTimesEquals( m2 );
        assertEquals( m2clone, m2 );
        assertFalse( original.equals( m1 ) );
        
        for( int i = 0; i < m1.getDimensionality(); i++ )
        {
            assertEquals( original.getElement(i) * m2clone.getElement(i), m1.getElement(i) );
        }
        
        DenseMatrix mbad = new DenseMatrix( M, M );
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < M; j++ )
            {
                mbad.setElement( i, j, RANDOM.nextDouble() * 2.0 * RANGE - RANGE );
            }
        }
        
        // This should be fine.
        m1.dotTimesEquals( mbad );        
        
    }

    /**
     * Test of getDimensionality method, of class DiagonalMatrixMTJ.
     */
    public void testGetDimensionality()
    {
        System.out.println( "getDimensionality" );
        DiagonalMatrixMTJ instance = this.createRandom();
        assertEquals( instance.getDimensionality(), instance.getDiagonal().length );
        
        int M = 10;
        instance = new DiagonalMatrixMTJ( M );
        assertEquals( M, instance.getDimensionality() );
        assertEquals( M, instance.getDiagonal().length );
        
        assertEquals( instance.getDimensionality(), instance.getNumRows() );
        assertEquals( instance.getDimensionality(), instance.getNumColumns() );
    }

    /**
     * Test of getInternalMatrix method, of class DiagonalMatrixMTJ.
     */
    public void testGetInternalMatrix()
    {
        System.out.println( "getInternalMatrix" );
        DiagonalMatrixMTJ instance = this.createRandom();
        assertNotNull( instance.getInternalMatrix() );
    }

    /**
     * Test of setInternalMatrix method, of class DiagonalMatrixMTJ.
     */
    public void testSetInternalMatrix()
    {
        System.out.println( "setInternalMatrix" );
        DiagonalMatrixMTJ instance = this.createRandom();
        instance.getInternalMatrix();
        
        try
        {
            instance.setInternalMatrix( null );
            fail( "Cannot set null internal matrix" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        DiagonalMatrixMTJ m2 = this.createRandom();
        assertNotSame( m2.getInternalMatrix(), instance.getInternalMatrix() );
        instance.setInternalMatrix( m2.getInternalMatrix() );
        assertSame( instance.getInternalMatrix(), m2.getInternalMatrix() );
        
    }

    /**
     * Test of times method, of class DiagonalMatrixMTJ.
     */
    public void testTimes_AbstractMTJMatrix()
    {
        System.out.println( "times Matrix" );
        
        double[] diag = { 2.0, -2.0, 3.0 };
        DiagonalMatrixMTJ m = new DiagonalMatrixMTJ( diag );
        Vector3 c0 = new Vector3( 1.0, 2.0, 3.0 );
        Vector3 c1 = new Vector3( 4.0, 5.0, 6.0 );
        Vector3 c2 = new Vector3( -7.0, -8.0, -9.0 );
        
        Matrix m1 = MatrixFactory.getDefault().copyColumnVectors( c0, c1, c2 );
        
        Vector3 ec0 = new Vector3( 2.0, -4.0, 9.0 ); 
        Vector3 ec1 = new Vector3( 8.0, -10.0, 18.0 ); 
        Vector3 ec2 = new Vector3( -14.0, 16.0, -27.0 ); 
        Matrix e1 = MatrixFactory.getDefault().copyColumnVectors( ec0, ec1, ec2 );
        Matrix r1 = m.times( m1 );
        System.out.println( "E1:\n" + e1 );
        System.out.println( "Result:\n" + r1 );
        assertEquals( e1, r1 );
        assertTrue( e1.equals( r1, TOLERANCE )  );
        assertEquals( m1.transpose().times( m ).transpose(), r1 );
        
        double[][] v2 = {{1.0, 2.0},{3.0, 4.0},{5.0, 6.0}};
        Matrix m2 = MatrixFactory.getDefault().copyArray( v2 );
        System.out.println( "M2:\n" + m2 );
        
        double[][] ev2 = {{2.0,4.0},{-6.0,-8.0},{15.0,18.0}};
        Matrix e2 = MatrixFactory.getDefault().copyArray( ev2 );
        Matrix r2 = m.times( m2 );
        assertEquals( e2, r2 );
        assertEquals( m2.transpose().times( m ).transpose(), r2 );

        try
        {
            m.times( m2.transpose() );
            fail( "Dimensions do not match!" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    /**
     * Test of times method, of class DiagonalMatrixMTJ.
     */
    public void testTimes_AbstractMTJVector()
    {
        System.out.println( "times Vector" );
        double[] diag = { 2.0, -2.0, 3.0 };
        DiagonalMatrixMTJ m = new DiagonalMatrixMTJ( diag );
        
        Vector v1 = VectorFactory.getDefault().copyArray(
            new double[]{1.0,2.0,-3.0} );          
        Vector e1 = VectorFactory.getDefault().copyArray(
            new double[]{2.0,-4.0,-9.0} );
        Vector r1 = m.times( v1 );
        assertEquals( e1, r1 );
        assertEquals( r1, v1.times( m ) );
        
        Vector v2 = VectorFactory.getDefault().copyArray(
            new double[]{-3.0,-2.0,1.0} );          
        Vector e2 = VectorFactory.getDefault().copyArray(
            new double[]{-6.0,4.0,3.0} );
        Vector r2 = m.times( v2 );
        assertEquals( e2, r2 );
        
        assertEquals( r2, v2.times( m ) );
        
    }

    /**
     * Test of isSquare method, of class DiagonalMatrixMTJ.
     */
    public void testIsSquare()
    {
        System.out.println( "isSquare" );
        DiagonalMatrixMTJ instance = this.createRandom();
        assertTrue( instance.isSquare() );
    }

    /**
     * Test of isSymmetric method, of class DiagonalMatrixMTJ.
     */
    public void testIsSymmetric1()
    {
        System.out.println( "isSymmetric" );
        DiagonalMatrixMTJ instance = this.createRandom();
        assertTrue( instance.isSymmetric() );
    }

    /**
     * Test of isSymmetric method, of class DiagonalMatrixMTJ.
     */
    public void testIsSymmetric_double()
    {
        System.out.println( "isSymmetric(double)" );
        double effectiveZero = RANDOM.nextDouble();
        DiagonalMatrixMTJ instance = this.createRandom();
        assertTrue( instance.isSymmetric( effectiveZero ) );
    }

    /**
     * Test of logDeterminant method, of class DiagonalMatrixMTJ.
     */
    public void testLogDeterminant()
    {
        System.out.println( "logDeterminant" );
        
        double[] diagonal = { 1.0, 2.0, 3.0 };
        DiagonalMatrixMTJ instance = new DiagonalMatrixMTJ( diagonal );
        ComplexNumber logDet = instance.logDeterminant();
        assertEquals( 1.791759, logDet.getRealPart(), TOLERANCE );
        assertEquals( 0.0, logDet.getImaginaryPart() );
        
        instance.scaleEquals( -2.0 );
        logDet = instance.logDeterminant();
        assertEquals( 3.871201, logDet.getRealPart(), TOLERANCE );
        assertEquals( Math.PI, logDet.getImaginaryPart(), TOLERANCE );        
    }

    /**
     * Test of normFrobenius method, of class DiagonalMatrixMTJ.
     */
    public void testNormFrobenius()
    {
        System.out.println( "normFrobenius" );
        double[] diagonal = { 2.0, -3.0, 4.0 };
        DiagonalMatrixMTJ instance = new DiagonalMatrixMTJ( diagonal );
        assertEquals( 5.385165, instance.normFrobenius(), TOLERANCE );
    }

    /**
     * Test of rank method, of class DiagonalMatrixMTJ.
     */
    public void testRank()
    {
        System.out.println( "rank" );
        double[] diagonal = { 0.0, -1.0, 2.0 };
        DiagonalMatrixMTJ m = new DiagonalMatrixMTJ( diagonal );
        assertEquals( 2, m.rank(0.0) );
        assertEquals( 2, m.rank() );
        
        assertEquals( 3, m.rank(diagonal[1]) );
        assertEquals( 1, m.rank(Math.abs(diagonal[1]) ) );
        assertEquals( 0, m.rank(Math.abs(diagonal[2]) ) );
        
    }

    /**
     * Test of solve method, of class DiagonalMatrixMTJ.
     */
    public void testSolve_AbstractMTJVector()
    {
        System.out.println( "solve" );
        
        
        DiagonalMatrixMTJ A = this.createRandom();
        int M = A.getDimensionality();
        final double r = 10.0;
        AbstractMTJVector x = (AbstractMTJVector) DenseVectorFactoryMTJ.INSTANCE.createUniformRandom( M, -r, r, RANDOM );
        Vector b = A.times( x );
        
        Vector xhat = A.solve( b );
        if( !xhat.equals( x, TOLERANCE ) )
        {
            assertEquals( xhat, x );
        }
    }

    /**
     * Test of solve method, of class DiagonalMatrixMTJ.
     */
    public void testSolve_gscmmMatrix()
    {
        System.out.println( "solve" );
        DiagonalMatrixMTJ A = this.createRandom();
        int M = A.getDimensionality();
        int N = RANDOM.nextInt( M ) + 2;
        final double r = 10.0;
        Matrix X = MatrixFactory.getDefault().createUniformRandom( M, N, r, r, RANDOM);
        Matrix B = A.times( X );
        
        Matrix Xhat = A.solve( B );
        if( !X.equals( Xhat, TOLERANCE ) )
        {
            assertEquals( X, Xhat );
        }
        
    }

    /**
     * Test of solve method, of class DiagonalMatrixMTJ.
     */
    public void testSolve_Vector()
    {
        System.out.println( "solve" );
        DiagonalMatrixMTJ A = this.createRandom();
        int M = A.getDimensionality();
        final double r = 10.0;
        Vector x = VectorFactory.getDefault().createUniformRandom( M, -r, r, RANDOM );
        Vector b = A.times( x );
        
        Vector xhat = A.solve( b );
        if( !xhat.equals( x, TOLERANCE ) )
        {
            assertEquals( xhat, x );
        }
        
    }

    /**
     * Test of getSubMatrix method, of class DiagonalMatrixMTJ.
     */
    public void testGetSubMatrix()
    {
        System.out.println( "getSubMatrix" );
        DiagonalMatrixMTJ m = this.createRandom();
        
        int minRow = 1;
        int maxRow = 2;
        int minColumn = 0;
        int maxColumn = m.getDimensionality()-1;
        SparseMatrix result = m.getSubMatrix( minRow, maxRow, minColumn, maxColumn );
        
        for( int i = minRow; i <= maxRow; i++ )
        {
            for( int j = minColumn; j <= maxColumn; j++ )
            {
                assertEquals( m.getElement( i, j ), result.getElement( i-minRow, j-minColumn ) );
            }
        }
        
    }

    /**
     * Test of transpose method, of class DiagonalMatrixMTJ.
     */
    public void testTranspose()
    {
        System.out.println( "transpose" );
        DiagonalMatrixMTJ m = this.createRandom();
        DiagonalMatrixMTJ t = m.transpose();
        assertSame( m, t );
        assertEquals( m, t );
    }

    /**
     * Test of getDiagonal method, of class DiagonalMatrixMTJ.
     */
    public void testGetDiagonal()
    {
        System.out.println( "getDiagonal" );
        double[] diagonal = { 1.0, -2.0, 3.0 };
        DiagonalMatrixMTJ instance = new DiagonalMatrixMTJ( diagonal );
        double[] result = instance.getDiagonal();
        assertEquals( diagonal.length, instance.getDimensionality() );
        assertEquals( diagonal.length, instance.getNumRows() );
        assertEquals( diagonal.length, instance.getNumColumns() );
        assertEquals( diagonal.length, result.length );
        for( int i = 0; i < diagonal.length; i++ )
        {
            assertEquals( diagonal[i], result[i] );
        }
    }

    /**
     * Test of pseudoInverse method, of class DiagonalMatrixMTJ.
     */
    public void testPseudoInverse()
    {
        System.out.println( "pseudoInverse" );
        double effectiveZero = 0.0;
        double[] diagonal = { 1.0, -2.0, 0.0, 3.0 };
        DiagonalMatrixMTJ m1 = new DiagonalMatrixMTJ( diagonal );
        
        double[] ed1 = { 1.0, -0.5, 0.0, 1.0/3.0 };
        DiagonalMatrixMTJ e1 = new DiagonalMatrixMTJ( ed1 );
        assertEquals( e1, m1.pseudoInverse( effectiveZero ) );
        
        double[] ed2 = { 0.0, -0.5, 0.0, 1.0/3.0 };
        DiagonalMatrixMTJ e2 = new DiagonalMatrixMTJ( ed2 );
        assertEquals( e2, m1.pseudoInverse( 1.0 ) );
    }

    /**
     * Test of getColumn method, of class DiagonalMatrixMTJ.
     */
    public void testGetColumn()
    {
        System.out.println( "getColumn" );
        DiagonalMatrixMTJ m = this.createRandom();
        
        for( int i = 0; i < m.getDimensionality(); i++ )
        {
            Vector vi = m.getColumn( i );
            for( int j = 0; j < vi.getDimensionality(); j++ )
            {
                if( i == j )
                {
                    assertEquals( m.getElement( i ), vi.getElement( j ) );
                }
                else
                {
                    assertEquals( 0.0, vi.getElement( j ) );
                }
            }
        }
        
    }

    /**
     * Test of getRow method, of class DiagonalMatrixMTJ.
     */
    public void testGetRow()
    {
        System.out.println( "getRow" );
        DiagonalMatrixMTJ m = this.createRandom();
        
        for( int i = 0; i < m.getDimensionality(); i++ )
        {
            Vector vi = m.getRow( i );
            for( int j = 0; j < vi.getDimensionality(); j++ )
            {
                if( i == j )
                {
                    assertEquals( m.getElement( i ), vi.getElement( j ) );
                }
                else
                {
                    assertEquals( 0.0, vi.getElement( j ) );
                }
            }
        }
    }

    /**
     * Test of getElement method, of class DiagonalMatrixMTJ.
     */
    public void testGetElement_int_int()
    {
        System.out.println( "getElement" );
        double[] diagonal = { 1.0, -2.0, 3.0 };
        DiagonalMatrixMTJ m = new DiagonalMatrixMTJ( diagonal );
        for( int i = 0; i < m.getNumRows(); i++ )
        {
            for( int j = 0; j < m.getNumColumns(); j++ )
            {
                if( i != j )
                {
                    assertEquals( 0.0, m.getElement( i, j ) );
                }
                else
                {
                    assertEquals( diagonal[i], m.getElement( i, j ) );
                }
            }
        }
        
    }

    /**
     * Test of setElement method, of class DiagonalMatrixMTJ.
     */
    public void testSetElement_3args()
    {
        System.out.println( "setElement" );
        DiagonalMatrixMTJ m = this.createRandom();
        for( int i = 0; i < m.getNumRows(); i++ )
        {
            for( int j = 0; j < m.getNumColumns(); j++ )
            {
                if( i != j )
                {
                    try
                    {
                        m.setElement( i, j, RANDOM.nextDouble() );
                        fail( "Cannot set off-diagonal elements" );
                    }
                    catch (Exception e)
                    {
                        System.out.println( "Good: " + e );
                    }
                }
                else
                {
                    double v = RANDOM.nextDouble();
                    m.setElement( i, j, v );
                    assertEquals( v, m.getElement( i, j ) );
                }
            }
        }
        
    }

    /**
     * Test of getElement method, of class DiagonalMatrixMTJ.
     */
    public void testGetElement_int()
    {
        System.out.println( "getElement" );
        double[] diagonal = { 1.0, 2.0, 3.0, 4.0 };
        DiagonalMatrixMTJ instance = new DiagonalMatrixMTJ( diagonal );
        for( int i = 0; i < instance.getDimensionality(); i++ )
        {
            assertEquals( diagonal[i], instance.getElement( i ) );
            assertEquals( instance.getElement( i, i ), instance.getElement( i ) );
        }
        
        try
        {
            instance.getElement( -1 );
            fail( "Cannot index < 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        try
        {
            instance.getElement( instance.getDimensionality() );
            fail( "Cannot index >= dimensionality" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    /**
     * Test of setElement method, of class DiagonalMatrixMTJ.
     */
    public void testSetElement_int_double()
    {
        System.out.println( "setElement" );
        
        DiagonalMatrixMTJ m = this.createRandom();
        for( int i = 0; i < m.getDimensionality(); i++ )
        {
            double v = RANDOM.nextDouble();
            m.setElement( i, v );
            assertEquals( m.getElement( i ), v );
        }
        
        try
        {
            m.setElement( -1, 0.0 );
            fail( "Cannot set < 0" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
        try
        {
            m.setElement( m.getDimensionality(), 0.0 );
            fail( "Cannot set >= dimensionality" );
        }
        catch (Exception e)
        {
            System.out.println( "Good: " + e );
        }
        
    }

    /**
     * Test of convertToVector method, of class DiagonalMatrixMTJ.
     */
    public void testConvertToVector()
    {
        System.out.println( "convertToVector" );
        
        double[] diagonal = { 1.0, 2.0, 3.0 };
        DiagonalMatrixMTJ m = new DiagonalMatrixMTJ( diagonal );
        
        Vector e = VectorFactory.getDefault().copyArray( diagonal );
        Vector r = m.convertToVector();
        assertEquals( e.getDimensionality(), r.getDimensionality() );
        assertEquals( e, r );
        
    }

    /**
     * Test of convertFromVector method, of class DiagonalMatrixMTJ.
     */
    public void testConvertFromVector()
    {
        System.out.println( "convertFromVector" );
        
        double[] v = { 1.0, 2.0, 3.0 };
        Vector parameters = VectorFactory.getDefault().copyArray( v );
        
        DiagonalMatrixMTJ instance = new DiagonalMatrixMTJ( v.length );
        
        assertFalse( parameters.equals( instance.convertToVector() ) );
        
        instance.convertFromVector( parameters );
        Vector r = instance.convertToVector();
        assertNotSame( parameters, r );
        assertEquals( parameters, r );
        
        for( int i = 0; i < v.length; i++ )
        {
            assertEquals( v[i], r.getElement( i ) );
        }
        
    }

    /**
     * Test of toString method, of class DiagonalMatrixMTJ.
     */
    public void testToString()
    {
        System.out.println( "toString" );
        DiagonalMatrixMTJ instance = this.createRandom();
        String r = instance.toString();
        assertNotNull( r );
        System.out.println( "String:\n" + r );
    }   

}

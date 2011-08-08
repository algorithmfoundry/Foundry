/*
 * File:                AbstractSparseMatrixTest.java
 * Authors:             Kevin Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright August 8, 2006, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */


package gov.sandia.cognition.math.matrix.mtj;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixTestHarness;
import gov.sandia.cognition.math.matrix.Vector;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * JUnit test for class AbstractSparseMatrix
 * @author krdixon
 */
public class AbstractSparseMatrixTest extends MatrixTestHarness
{
    
    public AbstractSparseMatrixTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(AbstractSparseMatrixTest.class);
        
        return suite;
    }

    protected AbstractSparseMatrix createCopy(Matrix matrix)
    {
        if( RANDOM.nextBoolean() )
        {
            return new SparseRowMatrix( matrix );
        }
        else
        {
            return new SparseColumnMatrix( matrix );
        }
    }

    protected AbstractSparseMatrix createMatrix(int numRows, int numColumns)
    {
        if( RANDOM.nextBoolean() )
        {
            return new SparseRowMatrix( numRows, numColumns );
        }
        else
        {
            return new SparseColumnMatrix( numRows, numColumns );
        }
    }    

    
    /**
     * Test of compact method, of class gov.sandia.isrc.math.matrix.mtj.AbstractSparseMatrix.
     */
    public void testCompact()
    {
        System.out.println("compact");
        

        AbstractSparseMatrix m1 = (AbstractSparseMatrix) this.createRandom();
        m1.setElement(0,0, 0.0);
        Matrix m2 = m1.clone();
        assertEquals( m1, m2 );
        
        m1.compact();
        
        assertEquals( m1, m2 );
        
    }


    /**
     * Test of sparseSolve
     */
    public void testSolveSparseVector()
    {
        System.out.println( "solveSparseVector" );
        
        
        double prob = 0.2;
        
        int M = 100;
        int N = 100;
        
        AbstractSparseMatrix A = this.createMatrix( M, N );
        SparseVector x = new SparseVector( N );
        int nA = 0;
        int nx = 0;
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < N; j++ )
            {
                if( Math.random() < prob )
                {
                    nA++;
                    A.setElement( i, j, Math.random() );
                }
                
                if( i == 0 )
                {
                    if( Math.random() < prob )
                    {
                        nx++;
                        x.setElement( j, Math.random() );
                    }
                }
            }            
            
        }
                
        SparseVector b = A.times( x );
        System.out.printf( "Solving (%d,%d -> %d) = (%d -> %d)\n", M, N, nA, N, nx );
        System.out.flush();
        
        DenseMatrix Ad = new DenseMatrix( A );
        Vector xdhat = Ad.solve( b );
        
        SparseVector xhat = A.solve( b );
        Vector bhat = A.times( xdhat );
        

        
        assertTrue( b.equals( bhat, 1e-5 ) );
        
        
    }
    
    
}

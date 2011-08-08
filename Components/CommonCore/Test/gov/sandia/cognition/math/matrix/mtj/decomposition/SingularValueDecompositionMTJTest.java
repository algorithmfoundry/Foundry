/*
 * File:                SingularValueDecompositionMTJTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 29, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix.mtj.decomposition;

import gov.sandia.cognition.math.matrix.MatrixUnionIterator;
import gov.sandia.cognition.math.matrix.TwoMatrixEntry;
import gov.sandia.cognition.math.matrix.decomposition.AbstractSingularValueDecomposition;
import gov.sandia.cognition.math.matrix.mtj.AbstractMTJMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.MatrixUnionIteratorMTJ;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Kevin R. Dixon
 */
public class SingularValueDecompositionMTJTest extends TestCase
{

    /**
     * 
     * @param testName
     */
    public SingularValueDecompositionMTJTest(
        String testName )
    {
        super( testName );
    }

    /**
     * 
     * @return
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite( SingularValueDecompositionMTJTest.class );

        return suite;
    }

    /**
     * 
     */
    public static void testSVD()
    {
        DenseMatrixFactoryMTJ matrixFactory = DenseMatrixFactoryMTJ.INSTANCE;

        DenseMatrix m2 = matrixFactory.createMatrix( 3, 4 );
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

        AbstractSingularValueDecomposition r1 =
            SingularValueDecompositionMTJ.create( m2 );

        DenseMatrix U = matrixFactory.createMatrix( 3, 3 );
        U.setElement( 0, 0, -0.457200 );
        U.setElement( 0, 1, 0.263852 );
        U.setElement( 0, 2, 0.849323 );

        U.setElement( 1, 0, -0.357743 );
        U.setElement( 1, 1, -0.928874 );
        U.setElement( 1, 2, 0.095988 );

        U.setElement( 2, 0, 0.814241 );
        U.setElement( 2, 1, -0.259954 );
        U.setElement( 2, 2, 0.519072 );

        System.out.println( "U: " + U );
        System.out.println( "SVD U: " + r1.getU() );

        MatrixUnionIterator iter =
            new MatrixUnionIteratorMTJ( U, (AbstractMTJMatrix) r1.getU() );

        // Eigenvectors are unique to a sign, so just see if their magnitudes
        // are equal... note that the proper way is to compare columns of
        // U, not individual elements... this will catch all but the most
        // pathalogical cases.
        while (iter.hasNext())
        {
            TwoMatrixEntry e = iter.next();

            assertEquals( Math.abs( e.getFirstValue() ),
                Math.abs( e.getSecondValue() ), 0.00001 );
        }

        DenseMatrix S = matrixFactory.createMatrix( m2.getNumRows(), m2.getNumColumns() );
        S.setElement( 0, 0, 11.12390 );
        S.setElement( 1, 1, 7.62488 );
        S.setElement( 2, 2, 0.34655 );

        assertTrue( S.equals( r1.getS(), 0.0001 ) );

        DenseMatrix Vtranspose = matrixFactory.createMatrix( 4, 4 );
        Vtranspose.setElement( 0, 0, -0.42149 );
        Vtranspose.setElement( 0, 1, -0.56795 );
        Vtranspose.setElement( 0, 2, -0.45713 );
        Vtranspose.setElement( 0, 3, -0.53927 );

        Vtranspose.setElement( 1, 0, -0.47222 );
        Vtranspose.setElement( 1, 1, -0.52535 );
        Vtranspose.setElement( 1, 2, 0.39610 );
        Vtranspose.setElement( 1, 3, 0.58662 );

        Vtranspose.setElement( 2, 0, -0.65779 );
        Vtranspose.setElement( 2, 1, 0.57216 );
        Vtranspose.setElement( 2, 2, -0.41373 );
        Vtranspose.setElement( 2, 3, 0.26225 );

        Vtranspose.setElement( 3, 0, 0.40825 );
        Vtranspose.setElement( 3, 1, -0.27217 );
        Vtranspose.setElement( 3, 2, -0.68041 );
        Vtranspose.setElement( 3, 3, 0.54433 );

        iter = new MatrixUnionIteratorMTJ(
            Vtranspose, (AbstractMTJMatrix) r1.getVtranspose() );
        while (iter.hasNext())
        {
            TwoMatrixEntry e = iter.next();

            assertEquals( Math.abs( e.getFirstValue() ),
                Math.abs( e.getSecondValue() ), 0.0001 );
        }
    }

}

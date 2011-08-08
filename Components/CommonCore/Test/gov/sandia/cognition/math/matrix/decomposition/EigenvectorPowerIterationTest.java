/*
 * File:                EigenvectorPowerIterationTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.math.matrix.decomposition;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.decomposition.EigenDecompositionRightMTJ;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 *
 * @author Kevin R. Dixon
 */
public class EigenvectorPowerIterationTest extends TestCase
{

    /**
     * Random number generator
     */
    protected Random random = new Random( 1 );

    /**
     * 
     * @param testName
     */
    public EigenvectorPowerIterationTest(
        String testName )
    {
        super( testName );
    }

    /**
     * Test of estimateEigenvectors method, of class gov.sandia.cognition.math.matrix.EigenvectorPowerIteration.
     */
    public void testEstimateEigenvectors()
    {
        System.out.println( "estimateEigenVectors" );


        int M = 3;
        double r = 1;
        Matrix C = MatrixFactory.getDefault().createUniformRandom( M, M, -r, r, random );
        Matrix A = C.times( C.transpose() );
        double stoppingThreshold = 1e-5;
        int maxIterations = 100;

        int numComponents = M;
        ArrayList<Vector> eigenvectors =
            EigenvectorPowerIteration.estimateEigenvectors(
            A.clone(), numComponents, stoppingThreshold, maxIterations );

        EigenDecomposition evd = EigenDecompositionRightMTJ.create(
            DenseMatrixFactoryMTJ.INSTANCE.copyMatrix( A ) );
        System.out.println( "Actual:\n" + evd.getEigenVectorsRealPart() );

        System.out.println( "Eigenvectors:\n" );
        for (int i = 0; i < eigenvectors.size(); i++)
        {
            Vector actual = evd.getEigenVectorsRealPart().getColumn( i );
            Vector estimate = eigenvectors.get( i );

            double error = Math.min( actual.minus( estimate ).norm2(),
                actual.plus( estimate ).norm2() );


            System.out.println( "Actual: " + actual );
            System.out.println( "Estimate: " + estimate );
            System.out.println( "Error: " + error );
            assertEquals( 0.0, error, 1e-2 );
        }

    }

    /**
     * Test of estimateEigenVector method, of class gov.sandia.cognition.math.matrix.EigenvectorPowerIteration.
     */
    public void testEstimateEigenvector()
    {
        System.out.println( "estimateEigenVector" );

        int M = 3;
        double r = 1;
        Matrix C = MatrixFactory.getDefault().createUniformRandom( M, M, -r, r, random );
        Matrix A = C.times( C.transpose() );
        Vector u = VectorFactory.getDefault().copyValues( 1.0, 0.0, 0.0 );
        double stoppingThreshold = 1e-5;
        int maxIterations = 100;

        Vector result = EigenvectorPowerIteration.estimateEigenvector( u, A, stoppingThreshold, maxIterations );
        System.out.println( "EigenVector: " + result );
    }

    /**
     * Test of estimateEigenValue method, of class gov.sandia.cognition.math.matrix.EigenvectorPowerIteration.
     */
    public void testEstimateEigenvalue()
    {
        System.out.println( "estimateEigenvalue" );

        int M = 3;
        double r = 1;
        Matrix C = MatrixFactory.getDefault().createUniformRandom( M, M, -r, r, random );
        Matrix A = C.times( C.transpose() );
        Vector v = EigenvectorPowerIteration.estimateEigenvectors( A, 1, 1e-5, 100 ).get( 0 );

        double result = EigenvectorPowerIteration.estimateEigenvalue( A, v );

        EigenDecomposition evd = EigenDecompositionRightMTJ.create(
            DenseMatrixFactoryMTJ.INSTANCE.copyMatrix( A ) );
        double lambda = evd.getEigenValue( 0 ).getMagnitude();
        System.out.println( "Actual: " + lambda + " Estimate: " + result );

        final double EPS = 1e-5;
        assertEquals( lambda, result, EPS );
    }

    /**
     * Degenerate case testing
     */
    public void testDegenerateCases()
    {
        System.out.println( "DegenerateCases" );
        int M = 3;

        // Some of the degenerate cases for eigendecompositions are
        // Nonpositive definite matrices (negative and zero eigenvalues)

        Matrix B = MatrixFactory.getDefault().createMatrix( M, M );
        B.setElement( 0, 0, -1.0 );
        B.setElement( 1, 1, -2.0 );
        EigenDecomposition evd = EigenDecompositionRightMTJ.create(
            DenseMatrixFactoryMTJ.INSTANCE.copyMatrix( B ) );
        Matrix Bc = B.clone();
        ArrayList<Vector> vs = EigenvectorPowerIteration.estimateEigenvectors( Bc, 2, 1e-5, 100 );

        final double EPS = 1e-5;
        for (int i = 0; i < vs.size(); i++)
        {
            double lambdahat = EigenvectorPowerIteration.estimateEigenvalue( B, vs.get( i ) );
            double lambda = evd.getEigenValue( i ).getRealPart();
            System.out.println( i + ": Lambda: " + lambda + " Estimate: " + lambdahat );
            assertEquals( lambda, lambdahat, EPS );
        }

        // Also, repeated eigenvalues can be a problem for some formulations.
        Matrix B2 = MatrixFactory.getDefault().createMatrix( M, M );
        B2.setElement( 0, 0, 3.0 );
        B2.setElement( 1, 1, 3.0 );
        EigenDecomposition evd2 = EigenDecompositionRightMTJ.create(
            DenseMatrixFactoryMTJ.INSTANCE.copyMatrix( B2 ) );
        Matrix Bc2 = B2.clone();
        ArrayList<Vector> vs2 = EigenvectorPowerIteration.estimateEigenvectors( Bc2, 2, 1e-5, 100 );

        for (int i = 0; i < vs2.size(); i++)
        {
            double lambdahat = EigenvectorPowerIteration.estimateEigenvalue( B2, vs2.get( i ) );
            double lambda = evd2.getEigenValue( i ).getRealPart();
            System.out.println( i + ": Lambda: " + lambda + " Estimate: " + lambdahat );
            assertEquals( lambda, lambdahat, EPS );
        }


    }

}

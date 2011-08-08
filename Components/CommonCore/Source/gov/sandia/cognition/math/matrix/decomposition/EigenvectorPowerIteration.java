/*
 * File:                EigenvectorPowerIteration.java
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
 */

package gov.sandia.cognition.math.matrix.decomposition;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;

/**
 * Implementation of the Eigenvector Power Iteration algorithm.  The core 
 * algorithm finds the eigenvector corresponding to the largest-magnitude
 * eigenvalue by repeated iteration from an initial guess: (v=A*v).  The
 * rate of convergence of this algorithm is determined by the ratio of 
 * successive eigenvalues. In practice, I usually see convergence to extremely 
 * accurate estimates in less than ten iterations.  Because this method only
 * involves a simple matrix-vector multiplication, it can be readily used with
 * very large, sparse matrices (which is what Google does).  The algorithm 
 * requires that the input matrix ("A") be symmetric, but it will converge even
 * when there are negative eigenvalues, repeated eigenvalues, and (in my 
 * experience) poorly conditioned matrices.  The algorithm is known to have
 * convergence problems in some cases, but I have not seen them occur.
 * <BR><BR>
 * We have also provided another method that will estimate the eigenvectors 
 * corresponding to the eigenvalues with the top "numEigenvectors" magnitudes.
 * This algorithm works by finding eigenvectors in sequence with the Power
 * Iteration algorithm and then subtracting the space spanned by the just-found
 * eigenvector: (A=A-v*v').  Rinse, lather, repeat.  Because of the subtraction,
 * this is not appropriate for large sparse matrices, as the result will almost
 * certainly be nonsparse.  In practice, I have found this approach to be
 * MUCH more computationally efficient than using LAPACK to compute a full EVD
 * of a Matrix.  However, we require that the matrix be symmetric (but can have
 * negative or repeated eigenvalues), whereas LAPACK can compute the EVD of a
 * general asymmetric real matrix.
 * <BR><BR>
 * Finally, we also provide a method for estimating the eigenvalue for a matrix
 * and eigenvector.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@PublicationReference(
    author="Wikipedia",
    title="Power iteration",
    type=PublicationType.WebPage,
    year=2009,
    url="http://en.wikipedia.org/wiki/Power_iteration"
)
public class EigenvectorPowerIteration
    extends AbstractCloneableSerializable
{

    /**
     * Default stopping threshold for power iteration, {@value}.
     */
    public final static double DEFAULT_STOPPING_THRESHOLD = 1e-5;

    /**
     * Default maximum iterations for power iteration, {@value}.
     */
    public final static int DEFAULT_MAXIMUM_ITERATIONS = 100;

    /** 
     * Creates a new instance of EigenvectorPowerIteration.
     */
    public EigenvectorPowerIteration()
    {
    }

    /**
     * Estimates the top eigenvectors of the given matrix using the power 
     * iteration algorithm.  This is a very efficient algorithm (used by
     * Google and many others) to estimate the largest "numEigenvectors"
     * eigenvectors of the symmetric matrix "A".  By largest eigenvector, 
     * we mean the eigenvector corresponding to the largest eigenvalue 
     * (and so on). The matrix "A" is permitted to have negative eigenvalues.
     * Power iteration will typically converge in less than ten iterations for
     * for each eigenvector.  However, the convergence rate is related to the
     * ratio of sequential eigenvalues, so if a matrix has similar eigenvalues
     * then convergence will be slow.
     * <BR><BR>
     * If you want a full eigendecomposition, you probably should not be using
     * this method, but the EigenDecomposition interface.
     * Please note that all eigenvectors are unique to
     * a direction.  That is, sometimes eigenvectors may be a scale factor 
     * of -1.0 to eigenvectors found by other approaches or initial conditions.
     * <BR><BR>
     * This approach is appropriate for sparse matrices for finding the top
     * SINGLE eigenvector.  That is, if multiple eigenvectors must be computed
     * from a very large-dimension and very sparse matrix, then you should use
     * another algorithm.  This is because the first eigenvector is found
     * by repeated multiplication (v=A*v until convergence).  However, after
     * the first eigenvector is found, and we are supposed to find more 
     * eigenvectors, then we subtract the space spanned by the
     * first eigenvector from the matrix: (A=A-v*v').  This will almost 
     * certainly destroy any sparseness in the original matrix and result in
     * a very unpleasant surprise of memory usage.
     * <BR><BR>
     * Note: The Matrix provided is modified for the estimation of the 
     * eigenvectors.
     * 
     * 
     * @param A The matrix to estimate the eigenvectors for. It must be symmetric.
     *      It will be modified by the algorithm.
     * @param numEigenvectors The number of eigenvectors to compute.
     * @return 
     * Collection of eigenvectors (of size "numEigenvectors") where the ith
     * entry corresponds to the eigenvector with the ith largest-magnitude
     * eigenvector.
     */
    public static ArrayList<Vector> estimateEigenvectors(
        final Matrix A,
        final int numEigenvectors )
    {
        return estimateEigenvectors( A, numEigenvectors,
            DEFAULT_STOPPING_THRESHOLD, DEFAULT_MAXIMUM_ITERATIONS );
    }

    /**
     * Estimates the top eigenvectors of the given matrix using the power
     * iteration algorithm.  This is a very efficient algorithm (used by
     * Google and many others) to estimate the largest "numEigenvectors"
     * eigenvectors of the symmetric matrix "A".  By largest eigenvector,
     * we mean the eigenvector corresponding to the largest eigenvalue
     * (and so on). The matrix "A" is permitted to have negative eigenvalues.
     * Power iteration will typically converge in less than ten iterations for
     * for each eigenvector.  However, the convergence rate is related to the
     * ratio of sequential eigenvalues, so if a matrix has similar eigenvalues
     * then convergence will be slow.
     * <BR><BR>
     * If you want a full eigendecomposition, you probably should not be using
     * this method, but the EigenDecomposition interface.
     * Please note that all eigenvectors are unique to
     * a direction.  That is, sometimes eigenvectors may be a scale factor
     * of -1.0 to eigenvectors found by other approaches or initial conditions.
     * <BR><BR>
     * This approach is appropriate for sparse matrices for finding the top
     * SINGLE eigenvector.  That is, if multiple eigenvectors must be computed
     * from a very large-dimension and very sparse matrix, then you should use
     * another algorithm.  This is because the first eigenvector is found
     * by repeated multiplication (v=A*v until convergence).  However, after
     * the first eigenvector is found, and we are supposed to find more
     * eigenvectors, then we subtract the space spanned by the
     * first eigenvector from the matrix: (A=A-v*v').  This will almost
     * certainly destroy any sparseness in the original matrix and result in
     * a very unpleasant surprise of memory usage.
     * <BR><BR>
     * Note: The Matrix provided is modified for the estimation of the
     * eigenvectors.
     * 
     * @param A The matrix to estimate the eigenvectors for. It must be symmetric.
     *      It will be modified by the algorithm.
     * @param numEigenvectors The number of eigenvectors to compute.
     * @param stoppingThreshold The stopping threshold for the power iteration algorithm. The 
     *      algorithm will stop its computation of an eigenvector when the
     * @param maxIterations The maximum number of iterations for the power iteration algorithm.
     * @return 
     * Collection of eigenvectors (of size "numEigenvectors") where the ith
     * entry corresponds to the eigenvector with the ith largest-magnitude
     * eigenvector.
     */
    public static ArrayList<Vector> estimateEigenvectors(
        final Matrix A,
        final int numEigenvectors,
        final double stoppingThreshold,
        final int maxIterations )
    {
        if (!A.isSymmetric( stoppingThreshold ))
        {
            throw new IllegalArgumentException(
                "Matrix must be symmetric to compute eigenvectors." );
        }

        // Get the number of rows (and columns).
        final int M = A.getNumRows();

        if (numEigenvectors < 0 || numEigenvectors > M)
        {
            throw new IllegalArgumentException(
                "The number of eigenvectors must be between 1 " + "and the size of the matrix." );
        }

        // Create a list where the resulting eigenvectors will be stored.
        final ArrayList<Vector> eigenvectors =
            new ArrayList<Vector>( numEigenvectors );
        for (int i = 0; i < numEigenvectors; i++)
        {
            // Create a uniform vector for the initial eigenvectors
            Vector ui = VectorFactory.getDefault().createVector( M, 1.0 );

            // Estimate the next eigenvector.
            Vector ei = estimateEigenvector(
                ui, A, stoppingThreshold, maxIterations );
            eigenvectors.add( ei );

            // no need to do this after the final eigenvector has been found
            if (i < (numEigenvectors - 1))
            {
                double eigenvalue =
                    EigenvectorPowerIteration.estimateEigenvalue( A, ei );

                // Subtract the eigenvector from the range space and keep
                // on trucking with the remaining space for the remaining
                // eigenvectors
                Matrix B = ei.scale( eigenvalue ).outerProduct( ei );
                A.minusEquals( B );
            }
        }

        return eigenvectors;
    }

    /**
     * Estimates the eigenvector corresponding to the largest magnitude 
     * eigenvalue.  The eigenvector will be of unit length, unless the
     * input Matrix is all zeros, in which case the method will return
     * an all-zero Vector.  Please note that all eigenvectors are unique to
     * a direction.  That is, sometimes eigenvectors may be a scale factor 
     * of -1.0 to eigenvectors found by other approaches or initial conditions.
     * <BR><BR>
     * This method is appropriate for sparse matrix problems.
     * 
     * 
     * @return Eigenvector corresponding to the largest magnitude eigenvalue.
     * @param initial 
     * Initial estimate of the eigenvector. This is generally a uniform 
     * (constant nonzero) Vector.
     * @param A The matrix to estimate the eigenvectors for. It must be symmetric.
     *      It will be modified by the algorithm.
     * @param stoppingThreshold The stopping threshold for the power iteration algorithm. The 
     *      algorithm will stop its computation of an eigenvector when the
     * @param maxIterations The maximum number of iterations for the power iteration algorithm.
     */
// TODO: Implement the AnytimeAlgorithm interface here.  --krdixon, 2009-07-02
    public static Vector estimateEigenvector(
        final Vector initial,
        final Matrix A,
        final double stoppingThreshold,
        final int maxIterations )
    {
        // This is the brain-dead algorithm called "Power Iteration"
        Vector v = initial.unitVector();

        double normChange;
        int iteration = 0;
        boolean keepGoing = true;
        while (keepGoing)
        {
            final Vector vPrevious = v;
            v = A.times( v );
            v.unitVectorEquals();
            normChange = v.minus( vPrevious ).norm2();
            iteration++;

            if ((normChange <= stoppingThreshold) || (iteration >= maxIterations))
            {
                keepGoing = false;
            }
        }

        return v;
    }

    /**
     * Finds the eigenvalue associated with the given Matrix and eigenvector.
     * This is found by noting that the definition of an eigensystem is:
     * lamba*v=A*v.  Therefore, the absolute value of the eigenvalue will be 
     * norm2(A*v), but determining the sign of the eigenvalue takes some minor
     * computation (which we do, so this method works with negative
     * eigenvalues).
     *
     * @param A 
     * Matrix to estimate the eigenvalue of.  May have negative, repeated, 
     * positive, or zero eigenvalues
     * @param v 
     * Eigenvector associated with the unknown eigenvalue
     * @return 
     * Eigenvalue associated with the eigenvector and Matrix
     */
    public static double estimateEigenvalue(
        final Matrix A,
        final Vector v )
    {
        // Definition of eigenvalue/eigenvector: lamba*ei = A*ei
        Vector vunit = v.unitVector();
        Vector vlambda = A.times( vunit );
        double lambda = vlambda.norm2();

        if (lambda != 0.0)
        {
            Vector vunithat = vlambda.scale( 1.0 / lambda );
            double dp = vunithat.minus( vunit ).norm2();
            double dn = vunithat.plus( vunit ).norm2();
            if (dn < dp)
            {
                lambda *= -1.0;
            }
        }
        return lambda;
    }

}

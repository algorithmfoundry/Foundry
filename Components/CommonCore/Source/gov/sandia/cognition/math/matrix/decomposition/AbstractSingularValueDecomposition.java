/*
 * File:                AbstractSingularValueDecomposition.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.decomposition;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.Matrix;

/**
 * Abstract container class that stores the matrices for a Singular Value
 * Decomposition (SVD) and related operations but does not actually perform
 * a singular value decomposition
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-16",
    changesNeeded=false,
    comments={
        "Made very minor changes.",
        "Otherwise, looks fine."
    }
)
public abstract class AbstractSingularValueDecomposition
    extends java.lang.Object
    implements SingularValueDecomposition
{

    /** Matrix that has the left singular vectors */
    private Matrix U;

    /** Diagonal matrix of singular values */
    private Matrix S;

    /** Transpose of the matrix containing the right singular vectors */
    private Matrix Vtranspose;

    /**
     * Default constructor that nulls out all matrices
     */
    public AbstractSingularValueDecomposition()
    {
        this( null, null, null );
    }

    /**
     * Creates a new instance of AbstractSingularValueDecomposition where
     * U*S*Vtranspose = original_matrix
     * 
     * @param U
     *      orthonormal matrix of left singular vectors
     * @param S
     *      PSD diagonal matrix of singular values, sorted in descending order
     * @param Vtranspose
     *      transpose of the orthonormal matrix of the right singular vectors
     */
    public AbstractSingularValueDecomposition(
        Matrix U,
        Matrix S,
        Matrix Vtranspose )
    {
        this.setU( U );
        this.setS( S );
        this.setVtranspose( Vtranspose );
    }

    public Matrix getU()
    {
        return this.U;
    }

    /**
     * setter for left singular vectors
     * @param U left singular vectors
     */
    protected void setU( Matrix U )
    {
        this.U = U;
    }

    public Matrix getS()
    {
        return this.S;
    }

    /**
     * setter for the singular values matrix
     * @param S singular values matrix
     */
    protected void setS(
        Matrix S )
    {
        this.S = S;
    }

    public Matrix getVtranspose()
    {
        return this.Vtranspose;
    }

    /**
     * sets the transpose of the right singular vectors matrix
     * @param Vtranspose transpose of the right singular vectors matrix
     */
    protected void setVtranspose(
        Matrix Vtranspose )
    {
        this.Vtranspose = Vtranspose;
    }

    public double norm2()
    {
        double maxSV = this.getS().getElement( 0, 0 );
        return maxSV;
    }

    public double conditionNumber()
    {
        int N = this.getS().getNumRows();

        double maxSV = this.getS().getElement( 0, 0 );
        double minSV = this.getS().getElement( N - 1, N - 1 );

        double conditionNumber;

        if (minSV > 0.0)
        {
            conditionNumber = maxSV / minSV;
        }
        else
        {
            conditionNumber = Double.POSITIVE_INFINITY;
        }

        return conditionNumber;
    }

    public int rank()
    {
        return this.effectiveRank( 0.0 );
    }

    public int effectiveRank(
        double effectiveZero )
    {
        int n, N = Math.min(this.getS().getNumRows(), this.getS().getNumColumns());
        for (n = 0; n < N; n++)
        {
            if (this.getS().getElement( n, n ) <= effectiveZero)
            {
                break;
            }
        }

        return n;
    }

    public Matrix pseudoInverse()
    {
        return this.pseudoInverse( 0.0 );
    }

    public Matrix pseudoInverse(
        double effectiveZero )
    {

        Matrix V = this.getVtranspose().transpose();
        Matrix Utranspose = this.getU().transpose();
        Matrix Spinv = this.getS().transpose();

        int N = Math.min( Spinv.getNumRows(), Spinv.getNumColumns() );
        for (int i = 0; i < N; i++)
        {
            double singularValue = Spinv.getElement( i, i );
            double svinv;
            if (singularValue <= effectiveZero)
            {
                svinv = 0.0;
            }
            else
            {
                svinv = 1.0 / singularValue;
            }
            Spinv.setElement( i, i, svinv );
        }

        return V.times( Spinv ).times( Utranspose );
    }

}

/*
 * File:                CholeskyDecompositionMTJ.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 2, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix.mtj.decomposition;

import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import no.uib.cipr.matrix.DenseCholesky;

/**
 * Computes the Cholesky decomposition of the symmetric positive definite
 * matrix.  This is sometimes known as a "square-root" decomposition.  The
 * Cholesky decoposition is extremely efficient for computing the eigenvalues
 * and inverses of symmetric PD matrices.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class CholeskyDecompositionMTJ 
    extends AbstractCloneableSerializable
{

    /**
     * Cholesky factor, such that R.transpose().times( R ) equals the original
     * matrix
     */
    private DenseMatrix R;
    
    /**
     * Creates a Cholesky decomposition of the symmetric positive definite
     * matrix A.  The result is a Cholesky factor R, such that
     * R.transpose().times( R ) equals the original symmetirc PD matrix A.
     * 
     * @param A
     * Symmetric positive definite matrix A to decompose
     * @return
     * Cholesky decomposition of the given matrix A.
     */
    public static CholeskyDecompositionMTJ create(
        final DenseMatrix A )
    {
        
        DenseCholesky cholesky = DenseCholesky.factorize( A.getInternalMatrix() );
        
        if( !cholesky.isSPD() )
        {
            throw new IllegalArgumentException(
                "Matrix must be symmetric and positive definite!" );
        }
        
        DenseMatrix R = DenseMatrixFactoryMTJ.INSTANCE.createWrapper( 
            new no.uib.cipr.matrix.DenseMatrix( cholesky.getU() ) );
        return new CholeskyDecompositionMTJ( R );
    }
    
    /** 
     * Creates a new instance of CholeskyDecompositionMTJ 
     * @param R 
     * Cholesky factor, such that R.transpose().times( R ) equals the original
     * matrix
     */
    private CholeskyDecompositionMTJ(
        DenseMatrix R )
    {
        this.setR( R );
    }

    /**
     * Getter for R
     * @return
     * Cholesky factor, such that R.transpose().times( R ) equals the original
     * matrix
     */
    public DenseMatrix getR()
    {
        return this.R;
    }

    /**
     * Setter for R
     * @param R
     * Cholesky factor, such that R.transpose().times( R ) equals the original
     * matrix
     */
    public void setR(
        DenseMatrix R )
    {
        this.R = R;
    }
    
}

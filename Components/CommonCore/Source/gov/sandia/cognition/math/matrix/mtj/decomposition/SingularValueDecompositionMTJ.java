/*
 * File:                SingularValueDecompositionMTJ.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 7, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj.decomposition;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.math.matrix.decomposition.AbstractSingularValueDecomposition;
import gov.sandia.cognition.math.OperationNotConvergedException;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;

/**
 * Full singular-value decomposition, based on MTJ's SVD.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-07-27",
    changesNeeded=true,
    comments="The constructor should be changed to a static method because it involves significant computation.",
    response=@CodeReviewResponse(
        respondent="Kevin R. Dixon",
        date="2007-11-25",
        moreChangesNeeded=false,
        comments="Added static create() method, made constructor private"
    )
)
public class SingularValueDecompositionMTJ
    extends AbstractSingularValueDecomposition
{

    /**
     * Creates a new instance of SingularValueDecompositionMTJ
     * 
     * @param matrix DenseMatrix to be decomposed. Not modified.
     * @return New SingularValueDecompositionMTJ for the given matrix
     * @throws OperationNotConvergedException If the SVD does not converge.
     */
    public static SingularValueDecompositionMTJ create(
        final Matrix matrix )
        throws OperationNotConvergedException
    {
        DenseMatrix denseMatrix =
            DenseMatrixFactoryMTJ.INSTANCE.copyMatrix( matrix);

        return new SingularValueDecompositionMTJ( denseMatrix );
    }

    /**
     * Creates a new instance of SingularValueDecompositionMTJ
     * 
     * @param matrix DenseMatrix to be decomposed. Not modified.
     * @throws OperationNotConvergedException If the SVD does not converge.
     */
    private SingularValueDecompositionMTJ(
        final DenseMatrix matrix )
        throws OperationNotConvergedException
    {
        int numRows = matrix.getNumRows();
        int numColumns = matrix.getNumColumns();

        no.uib.cipr.matrix.SVD internalSVD =
            new no.uib.cipr.matrix.SVD( numRows, numColumns );

        try
        {
            internalSVD.factor( new no.uib.cipr.matrix.DenseMatrix(
                matrix.getInternalMatrix() ) );
        }
        catch (no.uib.cipr.matrix.NotConvergedException e)
        {
            throw new OperationNotConvergedException( e.getMessage() );
        }


        DenseMatrixFactoryMTJ matrixFactory = DenseMatrixFactoryMTJ.INSTANCE;
        DenseMatrix U = matrixFactory.createWrapper( internalSVD.getU() );
        double[] singularValues = internalSVD.getS();
        int numSingular = singularValues.length;
        DenseMatrix S = matrixFactory.createMatrix( numRows, numColumns );
        for (int i = 0; i < numSingular; i++)
        {
            S.setElement( i, i, singularValues[i] );
        }

        DenseMatrix Vtranspose = matrixFactory.createWrapper(
            internalSVD.getVt() );

        this.setU( U );
        this.setS( S );
        this.setVtranspose( Vtranspose );
    }

}

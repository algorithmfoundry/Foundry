/*
 * File:                EigenDecompositionRightMTJ.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 14, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix.mtj.decomposition;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.decomposition.AbstractEigenDecomposition;
import gov.sandia.cognition.math.OperationNotConvergedException;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrix;
import gov.sandia.cognition.math.matrix.mtj.DenseMatrixFactoryMTJ;
import no.uib.cipr.matrix.EVD;

/**
 * Computes the right (standard) eigendecomposition of the given matrix.
 * Eigenvalues/vectors will be sorted in descending order.  Does not provide
 * complex eigenvectors; simply the magnitude of the eigenvector elements.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-12-02",
            changesNeeded=false,
            comments={
                "Moved previous code review to annotation.",
                "Otherwise, class looks fine."
            }
        )
        ,
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
    }
)
public class EigenDecompositionRightMTJ
    extends AbstractEigenDecomposition
{

    /**
     * Creates a new instance of EigenDecompositionRightMTJ.
     *
     * @param matrix DenseMatrix to compute the right EVD of
     * @throws OperationNotConvergedException If the operation does not 
     *         converge.
     */
    private EigenDecompositionRightMTJ(
        final DenseMatrix matrix )
        throws OperationNotConvergedException
    {
        super( null, null, null );

        boolean leftEVD = false;
        boolean rightEVD = true;
        EVD mtjEVD = new EVD( matrix.getNumRows(), leftEVD, rightEVD );
        try
        {
            mtjEVD.factor( new no.uib.cipr.matrix.DenseMatrix(
                matrix.getInternalMatrix() ) );
        }
        catch (no.uib.cipr.matrix.NotConvergedException e)
        {
            throw new OperationNotConvergedException( e.getMessage() );
        }

        int numEigenValues = matrix.getNumRows();
        ComplexNumber[] unsortedEigenValues = new ComplexNumber[numEigenValues];

        DenseMatrixFactoryMTJ matrixFactory = DenseMatrixFactoryMTJ.INSTANCE;
        DenseMatrix lapackEigenVectors =
            matrixFactory.createWrapper( mtjEVD.getRightEigenvectors() );

        DenseMatrix eigenVectorsRealPart =
            matrixFactory.createMatrix( numEigenValues, numEigenValues );

        DenseMatrix eigenVectorsImaginaryPart =
            matrixFactory.createMatrix( numEigenValues, numEigenValues );

        double[] realEigenValues = mtjEVD.getRealEigenvalues();
        double[] imaginaryEigenValues = mtjEVD.getImaginaryEigenvalues();

        boolean firstComplexConjugateDone = false;

        ComplexNumber[] complexEigenVector = new ComplexNumber[numEigenValues];
        for (int j = 0; j < numEigenValues; j++)
        {
            unsortedEigenValues[j] = new ComplexNumber(
                realEigenValues[j], imaginaryEigenValues[j] );

            // If an eigenvalue is complex, then its corresponding eigenvector
            // is complex as well.  Since we only use real matrices, complex
            // eigenvalues and eigenvectors appear as complex-conjugate pairs.
            // 
            // LAPACK represents complex eigenvectors in a bizarre manner:
            // The location of the first complex eigenvector contains the 
            // real part of the eigenvector, while its complex-conjugate pair
            // (second) contains the imaginary part of the eigenvector...
            //
            // If your eyes are fluttering right now, you're not alone.
            if (unsortedEigenValues[j].getImaginaryPart() != 0.0)
            {
                // If we haven't set the first complex conjugate pair, then
                // firstComplexConjugateDone == false
                if (!firstComplexConjugateDone)
                {
                    // The first complex conjugate vector is the positive one.
                    firstComplexConjugateDone = true;
                    for (int i = 0; i < numEigenValues; i++)
                    {
                        complexEigenVector[i] = new ComplexNumber(
                            lapackEigenVectors.getElement( i, j ),
                            lapackEigenVectors.getElement( i, j + 1 ) );
                    }
                }
                else
                {
                    // The second complex vector is the conjugate of the
                    // previous one, so 
                    firstComplexConjugateDone = false;
                    for (int i = 0; i < numEigenValues; i++)
                    {
                        complexEigenVector[i] = new ComplexNumber(
                            lapackEigenVectors.getElement( i, j - 1 ),
                            -lapackEigenVectors.getElement( i, j ) );
                    }
                }
            }
            else
            {
                // This is for pure real eigenvalues and eigenvectors
                firstComplexConjugateDone = false;
                for (int i = 0; i < numEigenValues; i++)
                {
                    complexEigenVector[i] = new ComplexNumber(
                        lapackEigenVectors.getElement( i, j ),
                        0.0 );
                }
            }

            // Copy real andimaginary parts.
            for (int i = 0; i < numEigenValues; i++)
            {
                eigenVectorsRealPart.setElement( i, j,
                    complexEigenVector[i].getRealPart() );

                eigenVectorsImaginaryPart.setElement( i, j,
                    complexEigenVector[i].getImaginaryPart() );
            }
        }

        this.setEigenDecomposition( unsortedEigenValues,
            eigenVectorsRealPart, eigenVectorsImaginaryPart, true );
    }
    
    /**
     * Creates a new instance of EigenDecompositionRightMTJ.
     *
     * @param matrix DenseMatrix to compute the right EVD of
     * @return new eigendecomposition that describes the given matrix
     * @throws OperationNotConvergedException If the operation does not
     *         converge.
     */
    public static EigenDecompositionRightMTJ create(
        final DenseMatrix matrix )
        throws OperationNotConvergedException
    {
        return new EigenDecompositionRightMTJ( matrix );
    }    

}

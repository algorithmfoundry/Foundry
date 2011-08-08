/*
 * File:                AbstractEigenDecomposition.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 13, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 *
 */

package gov.sandia.cognition.math.matrix.decomposition;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.util.ArrayIndexSorter;

/**
 * Abstract partial implementation of the EigenDecomposition interface
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-16",
    changesNeeded=true,
    comments={
        "Comments indicated by triple slashes",
        "Some refactoring needed here.",
        "Another review needed after refactoring."
    },
    response={
        @CodeReviewResponse(
            respondent="Kevin R. Dixon",
            date="2006-05-17",
            moreChangesNeeded=true,
            comments="Fixes from J.T.'s code review"
        ),
        @CodeReviewResponse(
            respondent="Jonathan McClain",
            date="2006-05-17",
            moreChangesNeeded=false,
            comments="Rechecking after changes were made. Looks good now."
        )
    }
)
public abstract class AbstractEigenDecomposition
    implements EigenDecomposition
{

    /** array of complex-valued eigenvalues */
    private ComplexNumber[] eigenValues;

    /** matrix containing the real parts of the eigenvectors */
    private Matrix eigenVectorsRealPart;

    /** matrix containing the imaginary parts of the eigenvectors */
    private Matrix eigenVectorsImaginaryPart;

    /**
     * Stores the given eigenvalues and eigenvectors internally, the eigenvalues
     * and eigenvectors will not be sorted. 
     * 
     * 
     * @param eigenValues
     *          array of complex-valued eigenvalue
     * @param eigenVectorsRealPart
     *          matrix of the real parts of the eigenvectors
     * @param eigenVectorsImaginaryPart 
     *          matrix of the imaginary parts of the eigenvectors
     */
    protected AbstractEigenDecomposition(
        ComplexNumber[] eigenValues,
        Matrix eigenVectorsRealPart,
        Matrix eigenVectorsImaginaryPart )
    {
        this.setEigenValues( eigenValues );
        this.setEigenVectorsRealPart( eigenVectorsRealPart );
        this.setEigenVectorsImaginaryPart( eigenVectorsImaginaryPart );
    }

    /**
     * Creates a new eigendecomposition using the given eigenvalues and
     * eigenvectors... this does not specify if the eigenvectors are the
     * right or left eigenvectors.  That should be done in a subclass
     * implementation. 
     * 
     * 
     * @param eigenValues array of complex-valued eigenvalues to store
     * @param eigenVectorsRealPart matrix where the ith column of the matrix
     *          contains the real part of the ith eigenvector of the
     *          underlying matrix
     * @param eigenVectorsImaginaryPart matrix where the ith column of the
     *          matrix contains the real part of the ith eigenvector of the
     *          underlying matrix
     * @param sort if true, then the constructor will sort the eigenvectors and
     *          eigenvectors by descending magnitude of the eigenvalue.  In
     *          this case, eigenValues[0] will be the largest magnitude and
     *          eigenVectors(:,0) is its corresponding eigenvector.
     */
    protected AbstractEigenDecomposition(
        ComplexNumber[] eigenValues,
        Matrix eigenVectorsRealPart,
        Matrix eigenVectorsImaginaryPart,
        boolean sort )
    {
        super();

        this.setEigenDecomposition( eigenValues,
            eigenVectorsRealPart, eigenVectorsImaginaryPart, sort );
    }

    /**
     * Sets the eigen decomposition for this
     * @param eigenValues array of eigenvalues for the underlying matrix
     * @param eigenVectorsRealPart real part of the eigenvectors for the underlying matrix
     * @param eigenVectorsImaginaryPart imaginary part of the eigenvalues for the underlying matrix
     * @param sort true to sort eigen values/vectors by descending order of magnitude of the
     * eigenvalues
     */
    protected void setEigenDecomposition(
        ComplexNumber[] eigenValues,
        Matrix eigenVectorsRealPart,
        Matrix eigenVectorsImaginaryPart,
        boolean sort )
    {
        if (sort == true)
        {
            this.sortAndSetEigenDecomposition(
                eigenValues, eigenVectorsRealPart, eigenVectorsImaginaryPart );
        }
        else
        {
            this.setUnsortedEigenDecomposition(
                eigenValues, eigenVectorsRealPart, eigenVectorsImaginaryPart );
        }
    }

    /**
     * Sorts the eigendecomposition in descending order of the value of the
     * magnitudes of the eigenvalues
     * @param eigenValues array of eigenvalues for the underlying matrix
     * @param eigenVectorsRealPart real part of the eigenvectors for the underlying matrix
     * @param eigenVectorsImaginaryPart imaginary part of the eigenvalues for the underlying matrix
     */
    protected void sortAndSetEigenDecomposition(
        ComplexNumber[] eigenValues,
        Matrix eigenVectorsRealPart,
        Matrix eigenVectorsImaginaryPart )
    {

        int M = eigenValues.length;
        double[] magnitudes = new double[M];
        for (int i = 0; i < M; i++)
        {
            magnitudes[i] = eigenValues[i].getMagnitude();
        }

        int[] indices = ArrayIndexSorter.sortArrayDescending( magnitudes );

        ComplexNumber[] sortedEigenValues = new ComplexNumber[M];
        Matrix sortedEigenVectorsRealPart = eigenVectorsRealPart.clone();
        Matrix sortedEigenVectorsImaginaryPart =
            eigenVectorsImaginaryPart.clone();
        for (int j = 0; j < M; j++)
        {
            sortedEigenValues[j] = eigenValues[indices[j]];
            for (int i = 0; i < M; i++)
            {
                sortedEigenVectorsRealPart.setElement( i, j,
                    eigenVectorsRealPart.getElement( i, indices[j] ) );

                sortedEigenVectorsImaginaryPart.setElement( i, j,
                    eigenVectorsImaginaryPart.getElement( i, indices[j] ) );
            }
        }

        this.setUnsortedEigenDecomposition(
            sortedEigenValues,
            sortedEigenVectorsRealPart,
            sortedEigenVectorsImaginaryPart );

    }

    /**
     * Creates a new eigendecomposition using the given eigenvalues and
     * eigenvectors... this does not specify if the eigenvectors are the
     * right or left eigenvectors.  That should be done in a subclass
     * implementation. 
     *
     * @param eigenValues array of eigenvalues for the underlying matrix
     * @param eigenVectorsRealPart real part of the eigenvectors for the underlying matrix
     * @param eigenVectorsImaginaryPart imaginary part of the eigenvalues for the underlying matrix
     */
    protected void setUnsortedEigenDecomposition(
        ComplexNumber[] eigenValues,
        Matrix eigenVectorsRealPart,
        Matrix eigenVectorsImaginaryPart )
    {
        this.setEigenValues( eigenValues );
        this.setEigenVectorsRealPart( eigenVectorsRealPart );
        this.setEigenVectorsImaginaryPart( eigenVectorsImaginaryPart );
    }

    /**
     * Getter for eigenValues
     * @return eigenValues
     */
    public ComplexNumber[] getEigenValues()
    {
        return this.eigenValues;
    }

    /**
     * setter for eigenValues
     * @param eigenValues eigenvalues to set
     */
    protected void setEigenValues(
        ComplexNumber[] eigenValues )
    {
        this.eigenValues = eigenValues;
    }

    /**
     * gets the indexed eigenvalue
     * @param index zero-based index into the eigenvalue array
     * @return index eigenvalue
     */
    public ComplexNumber getEigenValue(
        int index )
    {
        return this.getEigenValues()[index];
    }

    /**
     * getter for eigenvectorsrealPart
     * @return real part of the eienvector, where the ith eigenvector is the ith column
     */
    public Matrix getEigenVectorsRealPart()
    {
        return this.eigenVectorsRealPart;
    }

    /**
     * setter for eigenVectorsRealPart, where the ith eigenvector is the ith column
     * @param eigenVectorsRealPart real part of the eienvector, where the ith eigenvector is the ith column
     */
    protected void setEigenVectorsRealPart(
        Matrix eigenVectorsRealPart )
    {
        this.eigenVectorsRealPart = eigenVectorsRealPart;
    }

    /**
     * gets the imaginary part of the eienvector, where the ith eigenvector is the
     * ith column
     * @return imaginary part of the eienvector, where the ith eigenvector is the ith column
     */
    public Matrix getEigenVectorsImaginaryPart()
    {
        return this.eigenVectorsImaginaryPart;
    }

    /**
     * setter for the imaginary part of the eienvector, where the ith eigenvector is
     * the ith column
     * @param eigenVectorsImaginaryPart imaginary part of the eienvector, where the ith eigenvector is the ith column
     */
    public void setEigenVectorsImaginaryPart(
        Matrix eigenVectorsImaginaryPart )
    {
        this.eigenVectorsImaginaryPart = eigenVectorsImaginaryPart;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    public ComplexNumber getLogDeterminant()
    {

        ComplexNumber logsum = new ComplexNumber( 0.0, 0.0 );

        ComplexNumber eigenvalues[] = this.getEigenValues();
        for (ComplexNumber c : eigenvalues)
        {
            logsum.plusEquals( c.computeNaturalLogarithm() );
        }

        return logsum;

    }

}

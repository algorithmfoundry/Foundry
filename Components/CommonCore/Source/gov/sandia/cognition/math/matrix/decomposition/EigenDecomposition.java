/*
 * File:                EigenDecomposition.java
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
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.ComplexNumber;
import gov.sandia.cognition.math.matrix.Matrix;

/**
 * Performs a right eigendecomposition for symmetric or asymmetric matrices
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-16",
    changesNeeded=false,
    comments="Interface looks fine."
)
@PublicationReference(
    author="Wikipedia",
    title="Eigendecomposition of a matrix",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Eigendecomposition_of_a_matrix"
)
public interface EigenDecomposition
{

    /**
     * Gets the complex-valued (potentially real and/or imaginary)
     * eigenvalue specified by the index "index"
     *
     * @param index
     *          zero-based eigenvalue index to return
     * @return ComplexNumber that specifies the eigenvalue
     */
    public ComplexNumber getEigenValue(
        int index );

    /**
     * Returns the array of complex-valued eigenvalues, the eigenvalues may be
     * sorted in descending order of the magnitude of the eigenvalue, or they
     * may be unsorted, depending on the specific implementation used
     *
     * @return array of complex-valued eigenvalues
     */
    public ComplexNumber[] getEigenValues();

    /**
     * Returns a matrix with the real parts of the right eigenvalues of the
     * underlying matrix 
     *
     * @return matrix containing the real parts of the right eivenvalues
     */
    public Matrix getEigenVectorsRealPart();

    /**
     * Returns a matrix with the imaginary parts of the right eigenvalues of
     * the underlying matrix 
     *
     * @return matrix containing the imaginary parts of the right eivenvalues
     */
    public Matrix getEigenVectorsImaginaryPart();

    /**
     * Computes the natural logarithm determinant from the collection of
     * eigenvalues
     *
     * @return natural logarithm of the determinant
     */
    public ComplexNumber getLogDeterminant();

}

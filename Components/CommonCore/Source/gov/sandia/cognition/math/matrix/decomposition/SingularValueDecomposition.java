/*
 * File:                SingularValueDecomposition.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 8, 2006, Sandia Corporation.  Under the terms of Contract
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
import gov.sandia.cognition.math.matrix.Matrix;

/**
 * Interface that describes the operations of all SingularValueDecompositions
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Jonathan McClain",
    date="2006-05-17",
    changesNeeded=false,
    comments="Interface looks fine."
)
@PublicationReference(
    author="Wikipedia",
    title="Singular Value Decomposition",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Singular_value_decomposition"
)
public interface SingularValueDecomposition
{

    /**
     * Gets the orthonormal matrix containing the left singular vectors of the
     * underlying matrix
     *
     * @return U
     */
    public Matrix getU();

    /**
     * Gets the PSD diagonal matrix containing the sorted singular values 
     *
     * @return S
     */
    public Matrix getS();

    /**
     * Gets the transpose of the orthonormal matrix containing the right
     * singular vectors of the underlying matrix
     *
     * @return Vtranspose
     */
    public Matrix getVtranspose();

    /**
     * Returns the associated 2-norm (spectral norm) of the underlying matrix,
     * which is simply the largest singular value
     *
     * @return Largest singular value on the interval [0.0, inf)
     */
    public double norm2();

    /**
     * Returns the condition number of the underlying matrix, which is simply
     * the ratio of the largest to smallest singular value 
     *
     * @return Condition number on the interval [1.0, inf]
     */
    public double conditionNumber();

    /**
     * Returns the rank of the underlying matrix by calling this.effectiveRank
     * with an effectiveZero = 0.0 
     *
     * @return rank of the underlying matrix
     */
    public int rank();

    /**
     * Returns the effective rank of the underlying matrix by counting the
     * number of singular values whose values are larger than effectiveZero 
     *
     * @param effectiveZero
     *      threshold for considering a singular value to be zero
     *
     * @return effective rank of the underlying matrix
     */
    public int effectiveRank(
        double effectiveZero );

    /**
     * Computes the Least Squares pseudoinverse of the underlying matrix
     *
     * @return pseudoinverse of the underlying matrix: V * pinv(S) * U'
     */
    public Matrix pseudoInverse();

    /**
     * Computes the Least Squares pseudoinverse of the underlying matrix,
     * while clipping the singular values at effectiveZero
     * 
     * @param effectiveZero
     *          value below which to consider the singular values zero
     * @return pseudoinverse of the underlying matrix:
     *      V * pinv(S,effectiveZero) * U'
     */
    public Matrix pseudoInverse(
        double effectiveZero );

}

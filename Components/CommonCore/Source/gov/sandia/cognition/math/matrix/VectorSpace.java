/*
 * File:                VectorSpace.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 23, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.math.Ring;

/**
 * In the Foundry, a VectorSpace is a type of Ring that we can perform
 * Vector-like operations on: norm, distances between Vectors, etc.
 * @param <VectorType>
 * Type of VectorSpace
 * @param <EntryType>
 * Type of entry for the iteration
 * @author Kevin R. Dixon
 * @since 3.3.1
 */
@PublicationReference(
    author="Wikipedia",
    title="Vector space",
    type=PublicationType.WebPage,
    year=2011,
    url="http://en.wikipedia.org/wiki/Vector_space"
)
public interface VectorSpace<VectorType extends VectorSpace<VectorType,?>,EntryType extends VectorSpace.Entry>
    extends Ring<VectorType>,
    Iterable<EntryType>
{

    /**
     * Computes the sum of the elements in the vector.
     *
     * @return  The sum of the elements in the vector.
     * @since   3.0
     */
    public double sum();

    /**
     * 1-norm of the vector (sum of absolute values in the vector)
     * @return 1-norm of the vector, [0,\infty)
     */
    public double norm1();

    /**
     * 2-norm of the vector (aka Euclidean distance of the vector)
     *
     * @return 2-norm of the vector, [0,\infty)
     */
    public double norm2();

    /**
     * Squared 2-norm of the vector (aka squared Euclidean distance of the
     * vector)
     *
     * @return Squared 2-norm of the vector, [0,\infty)
     */
    public double norm2Squared();

    /**
     * Returns the infinity norm of the Vector, which is the maximum
     * absolute value of an element in the Vector.
     * @return
     * Maximum absolute value of any element in the Vector.
     */
    public double normInfinity();

    /**
     * Returns the p-norm of the Vector with the given power.
     * @param power
     * Power to exponentiate each entry, must be greater than 0.0,
     * Double.POSITIVE_INFINITY
     * @return
     * p-norm with the given power.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Vector norm, p-norm",
        type=PublicationType.WebPage,
        year=2011,
        url="http://en.wikipedia.org/wiki/Vector_norm#p-norm"
    )
    public double norm(
        final double power );

    /**
     * Inner Vector product between two Vectors
     *
     * @param other
     *          the Vector with which to compute the dot product with this,
     *          must be the same dimension as this
     * @return dot product, (0,\infty)
     */
    public double dotProduct(
        final VectorType other );

    /**
     * Computes the angle between two Vectors.
     *
     * @param   other
     *      Another vector with which to compute the angle. Must be the same
     *      dimensionality.
     * @return
     *      The angle between the two vectors in [0, PI].
     */
    public double angle(
        final VectorType other);

    /**
     * Computes the cosine between two Vectors
     *
     * @param other
     *          another vector with which to compute the cosine, must be the
     *          same dimension as this
     * @return cosine between the vectors, [0,1]
     */
    public double cosine(
        final VectorType other );

    /**
     * Euclidean distance between <code>this</code> and <code>other</code>,
     * which is the 2-norm between the difference of the Vectors
     *
     * @param other
     *          Vector to which to compute the distance, must be the same
     *          dimension as this
     * @return this.minus( other ).norm2(), which is [0,\infty)
     */
    public double euclideanDistance(
        final VectorType other );

    /**
     * Squared Euclidean distance between <code>this</code> and
     * <code>other</code>, which is the 2-norm between the difference of the
     * Vectors
     *
     * @param other
     *          Vector to which to compute the squared distance, must be the
     *          same dimension as this
     * @return this.minus( other ).norm2Squared(), which is [0,\infty)
     */
    public double euclideanDistanceSquared(
        final VectorType other );

    /**
     * Returns the unit vector of this vector. That is, a vector in the same
     * "direction" where the length (norm2) is 1.0. This is computed by
     * dividing each element buy the length (norm2). If this vector is all
     * zeros, then the vector returned will be all zeros.
     *
     * @return The unit vector of this vector.
     */
    public VectorType unitVector();

    /**
     * Modifies this vector to be a the unit vector. That is, a vector in the
     * same "direction" where the length (norm2) is 1.0. This is computed by
     * dividing each element buy the length (norm2). If this vector is all
     * zeros, then this vector will all zeros.
     */
    public void unitVectorEquals();

    /**
     * Determines if this vector is a unit vector (norm2 = 1.0).
     *
     * @return
     *      True if this vector is a unit vector; otherwise, false.
     */
    public boolean isUnitVector();

    /**
     * Determines if this vector is a unit vector within some tolerance for the
     * 2-norm.
     *
     * @param   tolerance
     *      The tolerance around 1.0 to allow the length.
     * @return
     *      True if this is a unit vector within the given tolerance; otherwise,
     *      false.
     */
    public boolean isUnitVector(
        final double tolerance);

    /**
     * Entry into the VectorSpace
     */
    public static interface Entry
    {

        /**
         * Gets the value to which this entry points
         *
         * @return value of the Vector entry
         */
        public double getValue();

        /**
         * Sets the value to which this entry points
         *
         * @param value
         *          new value for the Vector at the current index
         */
        public void setValue(
            final double value );

    }
    
}

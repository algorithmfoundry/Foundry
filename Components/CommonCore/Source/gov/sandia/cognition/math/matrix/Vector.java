/*
 * File:                Vector.java
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

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.math.Ring;
import java.text.NumberFormat;

/**
 * The <code>Vector</code> interface defines the operations that are expected
 * on a mathematical vector. The <code>Vector</code> can be thought of as a
 * collection of doubles of fixed size (the dimensionality) that supports
 * array-like indexing into the <code>Vector</code>. The <code>Vector</code>
 * is defined as an interface because there is more than one way to implement
 * a Vector, in particular if you want to make use of sparseness, which occurs
 * when most of the elements of a <code>Vector</code> are zero so that they are
 * not represented.
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-26",
            changesNeeded=false,
            comments={
                "Minor changes to formatting.",
                "Otherwise, looks good."
            }
        ),    
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-17",
            changesNeeded=false,
            comments="Looks fine."
        )
    }
)
public interface Vector
    extends Ring<Vector>, Iterable<VectorEntry>, Vectorizable
{
    
    public Vector clone();

    /**
     * Returns the number of elements in the Vector
     *
     * @return number of elements in the Vector
     */
    public int getDimensionality();

    /**
     * Gets the zero-based indexed element from the Vector 
     *
     * @param index
     *          zero-based index
     * @return zero-based indexed element in the Vector
     */
    public double getElement(
        int index );

    /**
     * Sets the zero-based indexed element in the Vector from the specified value 
     *
     * @param index
     *          zero-based index
     * @param value
     *          value to set the element in the Vector
     */
    public void setElement(
        int index,
        double value );

    /**
     * Inner Vector product between two Vectors 
     *
     * @param other
     *          the Vector with which to compute the dot product with this,
     *          must be the same dimension as this
     * @return dot product, (0,\infty)
     */
    public double dotProduct(
        final Vector other );

    /**
     * Computes the outer matrix product between the two vectors 
     *
     * @param other
     *          post-multiplied Vector with which to compute the outer product
     * @return Outer matrix product, which will have dimensions
     *         (this.getDimensionality x other.getDimensionality) and WILL BE 
     *         singular
     */
    public Matrix outerProduct(
        final Vector other );

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
        final Vector other);
    
    /**
     * Computes the cosine between two Vectors 
     *
     * @param other
     *          another vector with which to compute the cosine, must be the
     *          same dimension as this
     * @return cosine between the vectors, [0,1]
     */
    public double cosine(
        final Vector other );

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
     * Euclidean distance between <code>this</code> and <code>other</code>,
     * which is the 2-norm between the difference of the Vectors
     *
     * @param other
     *          Vector to which to compute the distance, must be the same
     *          dimension as this
     * @return this.minus( other ).norm2(), which is [0,\infty)
     */
    public double euclideanDistance(
        final Vector other );

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
        final Vector other );
    
    /**
     * Premultiplies the matrix by the vector "this"
     * @param matrix
     * Matrix to premultiply by "this", must have the same number of rows as
     * the dimensionality of "this"
     * @return
     * Vector of dimension equal to the number of columns of "matrix"
     */
    public Vector times(
        Matrix matrix );
    /**
     * Returns the unit vector of this vector. That is, a vector in the same 
     * "direction" where the length (norm2) is 1.0. This is computed by 
     * dividing each element buy the length (norm2). If this vector is all 
     * zeros, then the vector returned will be all zeros.
     *
     * @return The unit vector of this vector.
     */
    Vector unitVector();

    /**
     * Modifies this vector to be a the unit vector. That is, a vector in the 
     * same "direction" where the length (norm2) is 1.0. This is computed by 
     * dividing each element buy the length (norm2). If this vector is all 
     * zeros, then this vector will all zeros.
     */
    void unitVectorEquals();

    /**
     * Determines if <code>this</code> and <code>other</code> have the same
     * number of dimensions (size)
     *
     * @param other
     *          vector to compare to
     * @return true iff this.getDimensionality() == other.getDimensionality(),
     *          false otherwise
     */
    boolean checkSameDimensionality(
        Vector other );
    
    /**
     * Asserts that this vector has the same dimensionality as the given 
     * vector. If this assertion fails, a
     * {@code DimensionalityMismatchException} is thrown.
     * 
     * @param   other The other vector to compare to.
     */
    void assertSameDimensionality(
        final Vector other);
    
    /**
     * Asserts that the dimensionality of this vector equals the given 
     * dimensionality. If this assertion fails, a
     * {@code DimensionalityMismatchException} is thrown.
     * 
     * @param   otherDimensionality The dimensionality of the.
     */
    void assertDimensionalityEquals(
        final int otherDimensionality);

    /**
     * Stacks "other" below "this" and returns the stacked Vector 
     *
     * @param other
     *          Vector to stack below "this"
     * @return stacked Vector
     */
    Vector stack(
        Vector other );

    /**
     * Gets a subvector of "this", specified by the inclusive indices
     * @param minIndex minimum index to get (inclusive)
     * @param maxIndex maximum index to get (inclusive)
     * @return vector of dimension (maxIndex-minIndex+1)
     */
    public Vector subVector(
        int minIndex,
        int maxIndex );    
    
    public String toString();
    
    /**
     * Converts the vector to a {@code String}, using the given formatter.
     * 
     * @param   format The number format to use.
     * @return  The string representation of the vector.
     */
    public String toString(
        final NumberFormat format);
    
    /**
     * Converts the vector to a {@code String}, using the given formatter and
     * delimiter.
     * 
     * @param   format The number format to use.
     * @param   delimiter The delimiter to use.
     * @return  The string representation of the vector.
     */
    public String toString(
        final NumberFormat format,
        final String delimiter);

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

}

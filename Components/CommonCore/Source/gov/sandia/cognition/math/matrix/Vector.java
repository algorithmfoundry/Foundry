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
import gov.sandia.cognition.math.UnivariateScalarFunction;
import java.text.NumberFormat;
import java.util.List;

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
 * @author Justin Basilico
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
    extends VectorSpace<Vector,VectorEntry>,
    Vectorizable
{
    
    @Override
    public Vector clone();

    /**
     * Returns the number of elements in the Vector
     *
     * @return number of elements in the Vector
     */
    public int getDimensionality();
    
    /**
     * Gets the value of element of the vector at the zero-based index. 
     * It throws an ArrayIndexOutOfBoundsException if the index is out of range.
     * It is a convenience method for {@code getElement}.
     * 
     * @param   index
     *      The zero-based index. Must be between 0 (inclusive) and the 
     *      dimensionality of the vector (exclusive).
     * @return 
     *      The value at the index in the vector.
     * @since   3.4.0
     */
    public double get(
        final int index);
    
    /**
     * Sets the value for an element at the zero-based index from the vector.
     * It throws an ArrayIndexOutOfBoundsException if the index is out of range.
     * It is a convenience method for {@code setElement}.
     * 
     * @param   index
     *      The zero-based index. Must be between 0 (inclusive) and the 
     *      dimensionality of the vector (exclusive).
     * @param   value
     *      The value at the index in the vector.
     * @since   3.4.0
     */
    public void set(
        final int index,
        final double value);

    /**
     * Gets the zero-based indexed element from the Vector 
     *
     * @param index
     *          zero-based index
     * @return zero-based indexed element in the Vector
     */
    public double getElement(
        final int index );

    /**
     * Sets the zero-based indexed element in the Vector from the specified value 
     *
     * @param index
     *          zero-based index
     * @param value
     *          value to set the element in the Vector
     */
    public void setElement(
        final int index,
        final double value );

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
     * Premultiplies the matrix by the vector "this"
     * @param matrix
     * Matrix to premultiply by "this", must have the same number of rows as
     * the dimensionality of "this"
     * @return
     * Vector of dimension equal to the number of columns of "matrix"
     */
    public Vector times(
        final Matrix matrix );

    /**
     * Element-wise division of {@code this} by {@code other}. Note that if
     * {@code other} has zero elements the result will contain {@code NaN}
     * values.
     *
     * @param   other
     *      The other ring whose elements will divide into this one.
     * @return
     *      A new ring of equal size whose elements are equal to the
     *      corresponding element in {@code this} divided by the element in
     *      {@code other}.
     */
    public Vector dotDivide(
        final Vector other);

    /**
     * Inline element-wise division of {@code this} by {@code other}. Note
     * that if {@code other} has zero elements this will contain {@code NaN}
     * values.
     *
     * @param   other
     *      The other vector whose elements will divide into this one.
     */
    public void dotDivideEquals(
        final Vector other);

    /**
     * Determines if <code>this</code> and <code>other</code> have the same
     * number of dimensions (size)
     *
     * @param other
     *          vector to compare to
     * @return true iff this.getDimensionality() == other.getDimensionality(),
     *          false otherwise
     */
    public boolean checkSameDimensionality(
        final Vector other );
    
    /**
     * Asserts that this vector has the same dimensionality as the given 
     * vector. If this assertion fails, a
     * {@code DimensionalityMismatchException} is thrown.
     * 
     * @param   other The other vector to compare to.
     */
    public void assertSameDimensionality(
        final Vector other);
    
    /**
     * Asserts that the dimensionality of this vector equals the given 
     * dimensionality. If this assertion fails, a
     * {@code DimensionalityMismatchException} is thrown.
     * 
     * @param   otherDimensionality The dimensionality of the.
     */
    public void assertDimensionalityEquals(
        final int otherDimensionality);

    /**
     * Stacks "other" below "this" and returns the stacked Vector 
     *
     * @param other
     *          Vector to stack below "this"
     * @return stacked Vector
     */
    public Vector stack(
        Vector other );

    /**
     * Gets a subvector of "this", specified by the inclusive indices
     * @param minIndex minimum index to get (inclusive)
     * @param maxIndex maximum index to get (inclusive)
     * @return vector of dimension (maxIndex-minIndex+1)
     */
    public Vector subVector(
        final int minIndex,
        final int maxIndex );

    /**
     * Applies the given function to each of the elements of this vector and
     * returns a new vector with the result. Note that this is a dense operation
     * where zero elements will be passed to the function.
     *
     * @param   function
     *      The function from double to double to apply.
     * @return
     *      A new vector whose elements represent the result of applying the
     *      function to corresponding element in this vector.
     */
    public Vector transform(
        final UnivariateScalarFunction function);

    /**
     * Applies the given function to each of the elements of this vector and
     * sets them to the result.
     *
     * @param   function
     *      The function from double to double to apply.
     */
    public void transformEquals(
        final UnivariateScalarFunction function);

    /**
     * Applies the given function to each of the non-zero elements of this
     * vector and returns a new vector with the result.
     *
     * @param   function
     *      The function from double to double to apply to the non-zero
     *      elements.
     * @return
     *      A new vector whose elements represent the result of applying the
     *      function to the corresponding element in this vector, except for
     *      zeros, which are exactly the same as in this vector.
     */
    public Vector transformNonZeros(
        final UnivariateScalarFunction function);

    /**
     * Applies the given function to each of the non-zero elements of this
     * vector and sets them to the result.
     *
     * @param   function
     *      The function from double to double to apply to the non-zero
     *      elements.
     */
    public void transformNonZerosEquals(
        final UnivariateScalarFunction function);

    /**
     * Applies the given function to each element in this vector. This is a
     * dense operation that includes all values, even on a sparse vector.
     * 
     * @param   consumer 
     *      The consumer for the entries. It is called for each element in
     *      the vector, in order by increasing index.
     */
    public void forEachElement(
        final IndexValueConsumer consumer);
    
    /**
     * Applies the given function to each active entry in this vector. This can
     * be faster than looping over the entries using an iterator.
     * 
     * @param   consumer 
     *      The consumer for the entries. It is called for each active entry in
     *      the vector, in order by increasing index.
     */
    public void forEachEntry(
        final IndexValueConsumer consumer);
    
    /**
     * Applies the given function to each non-zero entry in this vector. This 
     * can be faster than looping over the entries using an iterator.
     * 
     * @param   consumer 
     *      The consumer for the non-zero entries. It is called for each  
     *      non-zero entry in the vector, in order by increasing index.
     */
    public void forEachNonZero(
        final IndexValueConsumer consumer);
    
    /**
     * Increments the value of the given index by 1.
     * 
     * @param   index 
     *      The index of the dimension to increment.
     */
    public void increment(
        final int index);
    
    /**
     * Increments the value of the given index by the given value.
     * 
     * @param   index 
     *      The index of the dimension to increment.
     * @param   value
     *      The value to add.
     */
    public void increment(
        final int index,
        final double value);
    
    /**
     * Decrements the value of the given index by 1.
     * 
     * @param   index 
     *      The index of the dimension to decrement.
     */
    public void decrement(
        final int index);
    
    /**
     * Decrements the value of the given index by the given value.
     * 
     * @param   index 
     *      The index of the dimension to decrement.
     * @param   value
     *      The value to subtract.
     */
    public void decrement(
        final int index,
        final double value);

    /**
     * Returns true if this vector has a potentially sparse underlying
     * structure. This can indicate that it is faster to only process the
     * non-zero elements rather than to do dense operations on it. Of course,
     * even with a sparse structure, there may be no zero elements or
     * conversely even with a non-sparse (dense) structure there may be many
     * zero elements.
     *
     * @return
     *      True if the vector has a potentially sparse structure. Otherwise,
     *      false.
     */
    public boolean isSparse();
    
    /**
     * Gets the number of active entries in the vector. Must be between zero 
     * and the dimensionality.
     * 
     * @return 
     *      The number of active entries in the vector.
     */
    public int getEntryCount();

    /**
     * Converts this vector to a new array of doubles, in the same order as they
     * are in the vector. The returned will be safe in that no references are
     * maintained by this vector.
     *
     * @return
     *      The vector as a dense array of doubles. Its length will be equal to
     *      the dimensionality of the vector.
     */
    public double[] toArray();
    

    /**
     * Gets a view of this vector as a list. The list is the same size as
     * this array.
     * 
     * @return 
     *      A list representing this array.
     */
    public List<Double> valuesAsList();
    
    @Override
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
     * Defines the functionality for a consumer of vector entries, which are an
     * index and a value. Typically this interface is used in conjunction with
     * the Vector forEachEntry method.
     *
     * @since   3.4.2
     */
    public interface IndexValueConsumer
    {

        /**
         * Consumes one entry in the vector.
         * 
         * @param   index
         *      The vector index. Cannot be negative.
         * @param   value
         *      The value in the vector for that index.
         */
        public void consume(
            final int index,
            final double value);

    }
}

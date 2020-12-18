/*
 * File:                AbstractVector.java
 * Authors:             Kevin R. Dixon and Justin Basilico
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
import gov.sandia.cognition.math.MathUtil;
import gov.sandia.cognition.math.UnivariateScalarFunction;
import java.text.NumberFormat;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

/**
 * Abstract implementation of some of the Vector interface, in a storage-free
 * manner
 *
 * @author Kevin R. Dixon
 * @author Justin Basilico
 * @since  1.0
 *
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-27",
            changesNeeded=false,
            comments={
                "Minor changes to formatting and documentation for equals().",
                "Otherwise, looks good."
            }
        ),        
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-16",
            changesNeeded=false,
            comments="Added some documentation and other minor changes. Looks ok."
        )
    }
)
public abstract class AbstractVector
    extends AbstractVectorSpace<Vector,VectorEntry>
    implements Vector
{
    /** The default delimiter for a vector. */
    public static final String DEFAULT_DELIMITER = " ";

    @Override
    public boolean equals(
        final Object other )
    {
        if (other == null)
        {
            return false;
        }
        else if (this == other)
        {
            return true;
        }
        else if (other instanceof Vector)
        {
            return this.equals( (Vector) other, 0.0 );
        }
        else
        {
            return false;
        }
    }

    @Override
    public boolean equals(
        final Vector other,
        final double effectiveZero)
    {
        if( !this.checkSameDimensionality(other) )
        {
            return false;
        }
        
        // Loop through all the defined entries of the two vectors.
        final Iterator<TwoVectorEntry> iterator = new VectorUnionIterator(
            this, other);
        while (iterator.hasNext())
        {
            final TwoVectorEntry entry = iterator.next();
            if (!MathUtil.equals(entry.getFirstValue(), entry.getSecondValue(), effectiveZero))
            {
                return false;
            }
        }
        
        return true;
    }
 
    @Override
    public int hashCode()
    {
        final int dimensionality = this.getDimensionality();
        int result = 7 + dimensionality;
        
        for (int i = 0; i < dimensionality; i++)
        {
            final double value = this.getElement(i);
            
            // We add a check for zero so that sparse vectors can create the
            // same hash-code without needing to handle the zeros.
            if (value != 0.0)
            {
                final long bits = Double.doubleToLongBits(value);
                
                // This is based on how NetBeans auto-generates hash codes
                // plus the documentation for Map.Entry.
                result = 47 * result + i ^ (int) (bits ^ (bits >>> 32));
            }
        }
        
        return result;
    }
    
    @Override
    public boolean checkSameDimensionality(
        final Vector other )
    {
        return (this.getDimensionality() == other.getDimensionality());
    }

    @Override
    public void assertSameDimensionality(
        final Vector other)
    {
        assertEqualDimensionality(this, other);
    }
    
    @Override
    public void assertDimensionalityEquals(
        final int otherDimensionality)
    {
        if (this.getDimensionality() != otherDimensionality)
        {
            throw new DimensionalityMismatchException(
                this.getDimensionality(), otherDimensionality);
        }
    }

    /**
     * Throws a DimensionalityMismatchException if
     * first.getDimensionality() != second.getDimensionality(),
     * otherwise this function has no effect
     *
     * @param first 
     *          first vector to consider
     * @param second 
     *          second vector to consider
     */
    public static void assertEqualDimensionality(
        final Vector first,
        final Vector second )
    {
        first.assertDimensionalityEquals( second.getDimensionality() );
    }
    
    @Override
    public void plusEquals(
        final Vector other)
    {
        // This is a generic implementation to support interoperability. 
        // Sub-classes should make custom ones for performance.
        this.assertSameDimensionality(other);
        for (final VectorEntry entry : other)
        {
            this.increment(entry.getIndex(), entry.getValue());
        }
    }

    @Override
    public void minusEquals(
        final Vector other)
    {
        // This is a generic implementation to support interoperability. 
        // Sub-classes should make custom ones for performance.
        this.assertSameDimensionality(other);
        for (final VectorEntry entry : other)
        {
            this.decrement(entry.getIndex(), entry.getValue());
        }
    }

    @Override
    public void dotTimesEquals(
        final Vector other)
    {
        // This is a generic implementation to support interoperability. 
        // Sub-classes should make custom ones for performance.
        this.assertSameDimensionality(other);
        for (final VectorEntry entry : this)
        {
            entry.setValue(entry.getValue() * other.get(entry.getIndex()));
        }
    }

    @Override
    public void scaledPlusEquals(
        final double scaleFactor,
        final Vector other)
    {
        // This is a generic implementation to support interoperability. 
        // Sub-classes should make custom ones for performance.
        this.assertSameDimensionality(other);
        for (final VectorEntry entry : other)
        {
            this.increment(entry.getIndex(), scaleFactor * entry.getValue());
        }
    }

    @Override
    public double dotProduct(
        final Vector other)
    {
        // This is a generic implementation to support interoperability. 
        // Sub-classes should make custom ones for performance.
        this.assertSameDimensionality(other);
        double result = 0.0;
        for (final VectorEntry entry : this)
        {
            result += entry.getValue() * other.get(entry.getIndex());
        }
        return result;
    }

    @Override
    public double euclideanDistanceSquared(
        final Vector other)
    {
        // This is a generic implementation to support interoperability. 
        // Sub-classes should make custom ones for performance.
        this.assertSameDimensionality(other);
        
        // Go through all the defined entries in both vectors.
        double result = 0.0;
        final Iterator<TwoVectorEntry> iterator = new VectorUnionIterator(
            this, other);
        while (iterator.hasNext())
        {
            final TwoVectorEntry entry = iterator.next();
            final double difference = 
                entry.getFirstValue() - entry.getSecondValue();
            result += difference * difference;
        }
        return result;
    }
    
    @Override
    public Vector times(
        final Matrix matrix)
    {
        // This is a generic implementation to support interoperability. 
        // Sub-classes should make custom ones for performance.
        this.assertDimensionalityEquals(matrix.getNumRows());
        final int n = matrix.getNumColumns();
        final Vector result = this.getVectorFactory().createVector(n);
        for (final MatrixEntry entry : matrix)
        {
            result.increment(entry.getColumnIndex(), 
                this.get(entry.getRowIndex()) * entry.getValue());
        }
        return result;
    }

    @Override
    public Matrix outerProduct(
        final Vector other)
    {
        final int rowCount = this.getDimensionality();
        final int columnCount = other.getDimensionality();
        final Matrix result;
        if (!other.isSparse())
        {
            result = this.getVectorFactory().getAssociatedMatrixFactory()
                .createMatrix(rowCount, columnCount);
        }
        else
        {
            result = other.getVectorFactory().getAssociatedMatrixFactory()
                .createMatrix(rowCount, columnCount);
        }
        
        for (VectorEntry thisEntry : this)
        {
            final int i = thisEntry.getIndex();
            final double thisI = thisEntry.getValue();
            for (VectorEntry otherEntry : other)
            {
                final int j = otherEntry.getIndex();
                final double otherJ = otherEntry.getValue();
                result.set(i, j, thisI * otherJ);
            }
        }
        
        return result;
    }
    
    @Override
    public Vector dotDivide(
        final Vector other)
    {
        final Vector result = this.clone();
        result.dotDivideEquals(other);
        return result;
    }

    @Override
    public void dotDivideEquals(
        final Vector other)
    {
        this.assertSameDimensionality(other);

        // This is a dense loop since there is no sparsity in division.
        final int dimensionality = this.getDimensionality();
        for (int i = 0; i < dimensionality; i++)
        {
            this.setElement(i, this.getElement(i) / other.getElement(i));
        }
    }

    @Override
    public Vector transform(
        final UnivariateScalarFunction function)
    {
        final Vector result = this.clone();
        result.transformEquals(function);
        return result;
    }

    @Override
    public Vector transform(
        final IndexValueTransform function)
    {
        final Vector result = this.clone();
        result.transformEquals(function);
        return result;
    }

    @Override
    public void transformEquals(
        final UnivariateScalarFunction function)
    {
        final int dimensionality = this.getDimensionality();
        for (int i = 0; i < dimensionality; i++)
        {
            final double value = function.evaluate(this.get(i));
            this.set(i, value);
        }
    }

    @Override
    public void transformEquals(
        final IndexValueTransform function)
    {
        final int dimensionality = this.getDimensionality();
        for (int i = 0; i < dimensionality; i++)
        {
            final double value = function.transform(i, this.get(i));
            this.set(i, value);
        }
    }
    
    @Override
    public Vector transformNonZeros(
        final UnivariateScalarFunction function)
    {
        final Vector result = this.clone();
        result.transformNonZerosEquals(function);
        return result;
    }
    
    @Override
    public Vector transformNonZeros(
        final IndexValueTransform function)
    {
        final Vector result = this.clone();
        result.transformNonZerosEquals(function);
        return result;
    }

    @Override
    public void transformNonZerosEquals(
        final UnivariateScalarFunction function)
    {
        // Default implementation uses an iterator. However, specialized 
        // sub-classes can add optimizations.
        for (final VectorEntry entry : this)
        {
            final double value = entry.getValue();
            if (value != 0.0)
            {
                entry.setValue(function.evaluate(value));
            }
        }
    }

    @Override
    public void transformNonZerosEquals(
        final IndexValueTransform function)
    {
        // Default implementation uses an iterator. However, specialized 
        // sub-classes can add optimizations.
        for (final VectorEntry entry : this)
        {
            final double value = entry.getValue();
            if (value != 0.0)
            {
                entry.setValue(function.transform(entry.getIndex(), value));
            }
        }
    }

    @Override
    public void forEachElement(
        final IndexValueConsumer consumer)
    {
        // This is by definition a dense iteration.
        final int dimensionality = this.getDimensionality();
        for (int i = 0; i < dimensionality; i++)
        {
            consumer.consume(i, this.get(i));
        }
    }
    
    @Override
    public void forEachEntry(
        final IndexValueConsumer consumer)
    {
        // Default implementation uses an iterator. However, specialized 
        // sub-classes can add optimizations.
        for (final VectorEntry entry : this)
        {
            consumer.consume(entry.getIndex(), entry.getValue());
        }
    }

    @Override
    public void forEachNonZero(
        final IndexValueConsumer consumer)
    {
        // Default implementation uses an iterator. However, specialized 
        // sub-classes can add optimizations.
        for (final VectorEntry entry : this)
        {
            final double value = entry.getValue();
            if (value != 0.0)
            {
                consumer.consume(entry.getIndex(), value);
            }
        }
    }

    @Override
    public void increment(
        final int index)
    {
        this.increment(index, 1.0);
    }

    @Override
    public void increment(
        final int index,
        final double value)
    {
        this.setElement(index, this.getElement(index) + value);
    }

    @Override
    public void decrement(
        final int index)
    {
        this.decrement(index, 1.0);
    }

    @Override
    public void decrement(
        final int index,
        final double value)
    {
        this.increment(index, -value);
    }
    
    @Override
    public Vector stack(
        final Vector other)
    {
        final int d1 = this.getDimensionality();
        final int d2 = other.getDimensionality();
        
        final Vector stacked = this.getVectorFactory().createVector(d1 + d2);
        for (final VectorEntry e : this)
        {
            stacked.setElement(e.getIndex(), e.getValue());
        }
        for (final VectorEntry e : other)
        {
            stacked.setElement(d1 + e.getIndex(), e.getValue());
        }
        
        return stacked;
    }
    
    @Override
    public double[] toArray()
    {
        final int dimensionality = this.getDimensionality();
        final double[] result = new double[dimensionality];
        for (int i = 0; i < dimensionality; i++)
        {
            result[i] = this.getElement(i);
        }
        return result;
    }

    @Override
    public List<Double> valuesAsList()
    {
        return new ValuesListView();
    }
    
    /**
     * Converts a vector to a string that consists of each value in the vector 
     *
     * @return String containing the elements of this
     */
    @Override
    public String toString()
    {
        final int dimensionality = this.getDimensionality();
        final StringBuilder builder = new StringBuilder(20 * dimensionality);
        for (int i = 0; i < dimensionality; i++)
        {
            if (i > 0)
            {
                builder.append(DEFAULT_DELIMITER);
            }

            builder.append( this.getElement(i) );
        }

        return builder.toString();
    }
    
    @Override
    public String toString(
        final NumberFormat format)
    {
        return this.toString(format, DEFAULT_DELIMITER);
    }
    
    @Override
    public String toString(
        final NumberFormat format,
        final String delimiter)
    {
        final int dimensionality = this.getDimensionality();
        final StringBuilder builder = new StringBuilder(20 * dimensionality);
        for (int i = 0; i < dimensionality; i++)
        {
            if (i > 0)
            {
                builder.append(delimiter);
            }
            
            builder.append( format.format(this.getElement(i)) );
        }

        return builder.toString();
    }

    /**
     * Returns this.
     *
     * @return This.
     */
    @Override
    public Vector convertToVector()
    {
        return this;
    }

    /**
     * Assigns the values in the provided vector into this.
     *
     * @param parameters The vector to convert.
     */
    @Override
    public void convertFromVector(
        final Vector parameters )
    {
        this.assertSameDimensionality( parameters );

        final int dimensionality = this.getDimensionality();
        for (int i = 0; i < dimensionality; i++)
        {
            this.setElement( i, parameters.getElement( i ) );
        }
    }
    
    /**
     * Implements a view of this vector as a {@link List}.
     */
    class ValuesListView
        extends AbstractList<Double>
    {
        /**
         * Creates a new {@link ValuesListView} for this vector.
         */
        public ValuesListView()
        {
            super();
        }

        @Override
        public Double get(
            final int i)
        {
            return getElement(i);
        }

        @Override
        public Double set(
            final int i,
            final Double value)
        {
            final double previous = getElement(i);
            setElement(i, value);
            return previous;
        }

        @Override
        public int size()
        {
            return getDimensionality();
        }

    }

}

/*
 * File:                SparseVector.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2015, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.math.matrix.custom;

import gov.sandia.cognition.math.MutableDouble;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Our sparse vector implementation. Rather straightforward: stores all non-zero
 * data in a map from index to value.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
public class SparseVector
    extends BaseVector
{

    /**
     * Sparse matrices and vectors appear to be less effective after passing
     * this threshold
     */
    static final double SPARSE_TO_DENSE_THRESHOLD = 0.25;

    /**
     * The vector length
     */
    final private int dimensionality;

    /**
     * The index-to-value map
     */
    private TreeMap<Integer, MutableDouble> elements;

    /**
     * Compressed version of the data: The values. Similar to the Yale format
     * for sparse matrices.
     */
    private double[] values;

    /**
     * Compressed version of the data: The locations. Similar to the Yale format
     * for sparse matrices.
     */
    private int[] indices;

    /**
     * Create a new sparse vector. All values begin empty (which is interpreted
     * to 0).
     *
     * @param n The vector length
     */
    public SparseVector(
        final int n)
    {
        ArgumentChecker.assertIsNonNegative("dimensionality", n);
        this.dimensionality = n;
        elements = new TreeMap<>();
        values = null;
        indices = null;
    }

    /**
     * Copy constructor -- creates a deep copy of the input sparse vector.
     *
     * @param v The sparse vector to copy
     */
    public SparseVector(
        final SparseVector v)
    {
        this.dimensionality = v.dimensionality;
        if (!v.isCompressed())
        {
            elements = new TreeMap<>(v.elements);
            // Need to copy over all the values.
            for (Map.Entry<Integer, MutableDouble> entry : this.elements.entrySet())
            {
                entry.setValue(new MutableDouble(entry.getValue()));
            }
            values = null;
            indices = null;
        }
        else
        {
            elements = new TreeMap<>();
            values = Arrays.copyOf(v.values, v.values.length);
            indices = Arrays.copyOf(v.indices, v.indices.length);
        }
    }

    /**
     * Copy constructor -- creates a deep copy of the input sparse vector.
     *
     * @param v The sparse vector to copy
     */
    public SparseVector(
        final DenseVector v)
    {
        this.dimensionality = v.values.length;
        int nnz = v.countNonZeros();
        values = new double[nnz];
        indices = new int[nnz];
        elements = new TreeMap<>();
        int idx = 0;
        for (int i = 0; i < dimensionality; ++i)
        {
            double val = v.values[i];
            if (val != 0)
            {
                values[idx] = val;
                indices[idx] = i;
                ++idx;
            }
        }
    }

    /**
     * This should never be called by anything or anyone other than Java's
     * serialization code.
     */
    protected SparseVector()
    {
        // NOTE: This initializes to bad values or nothing
        dimensionality = 0;
    }

    /**
     * The compressed representation should allow for quicker mathematical
     * operations, but does not permit editing the values in the vector. This
     * returns true if the vector is currently compressed, false if not
     * compressed.
     *
     * @return true if the vector is currently compressed, false if not
     * compressed.
     */
    final public boolean isCompressed()
    {
        return (values != null) && (indices != null);
    }

    /**
     * The compressed representation should allow for quicker mathematical
     * operations, but does not permit editing the values in the vector. This
     * transitions from the uncompressed to the compressed form. If already
     * compressed, this does nothing.
     */
    final public void compress()
    {
        if (isCompressed())
        {
            return;
        }

        int nnz = elements.size();
        values = new double[nnz];
        indices = new int[nnz];
        int idx = 0;
        for (Map.Entry<Integer, MutableDouble> e : elements.entrySet())
        {
            indices[idx] = e.getKey();
            values[idx] = e.getValue().value;
            ++idx;
        }
        elements.clear();
    }

    /**
     * The compressed representation should allow for quicker mathematical
     * operations, but does not permit editing the values in the vector. This
     * transitions from the compressed to the uncompressed form. If already
     * uncompressed, this does nothing.
     */
    final public void decompress()
    {
        if (!isCompressed())
        {
            return;
        }

        elements.clear();
        for (int i = 0; i < values.length; ++i)
        {
            final double value = values[i];
            if (value != 0.0)
            {
                elements.put(indices[i], new MutableDouble(value));
            }
        }
        indices = null;
        values = null;
    }

    @Override
    public void zero()
    {
        this.elements.clear();           
        this.indices = null;
        this.values = null;
    }
    
    @Override
    final public Vector clone()
    {
// TODO: Fix this clone.
        return new SparseVector(this);
    }

    @Override
    final public Vector plus(
        final Vector v)
    {
        // I need to flip this so that if it the input is a dense vector, I
        // return a dense vector.  If it's a sparse vector, then a sparse vector
        // is still returned.
        Vector result = v.clone();
        result.plusEquals(this);
        return result;
    }

    @Override
    final public Vector minus(
        final Vector v)
    {
        // I need to flip this so that if it the input is a dense vector, I
        // return a dense vector.  If it's a sparse vector, then a sparse vector
        // is still returned.
        Vector result = v.clone();
        result.negativeEquals();
        result.plusEquals(this);
        return result;
    }

    /**
     * Counts the number of non-zero operations after a sparse-on-spares
     * operation.
     *
     * @param other The other vector
     * @param op The operation to perform (addition/subtraction = OR, dot
     * product = AND)
     * @return The number of non-zero entries after the operation -- ignoring
     * zeroes generated by the actual operation (2 - 2 = 0) -- just counting
     * locations where operations will occur.
     */
    private int numNonZeroAfterOp(
        final SparseVector other,
        final SparseMatrix.Combiner op)
    {
        compress();
        other.compress();
        int myidx = 0;
        int otheridx = 0;
        int nnz = 0;
        while ((myidx < indices.length) && (otheridx < other.indices.length))
        {
            if (indices[myidx] == other.indices[otheridx])
            {
                ++nnz;
                ++myidx;
                ++otheridx;
            }
            else if (indices[myidx] < other.indices[otheridx])
            {
                if (op == SparseMatrix.Combiner.OR)
                {
                    ++nnz;
                }
                ++myidx;
            }
            else if (other.indices[otheridx] < indices[myidx])
            {
                if (op == SparseMatrix.Combiner.OR)
                {
                    ++nnz;
                }
                ++otheridx;
            }
        }
        if (op == SparseMatrix.Combiner.OR)
        {
            nnz += indices.length - myidx;
            nnz += other.indices.length - otheridx;
        }

        return nnz;
    }
    /**
     * {@inheritDoc}
     *
     * NOTE: This operation is not recommended as it is most likely to create a
     * very dense vector being stored in a sparse-vector format. This will be
     * memory inefficient.
     */
    @Override
    public void scaledPlusEquals(
        final DenseVector other,
        final double scaleFactor)
    {
        this.assertSameDimensionality(other);
        compress();

        // Just assume that this is going to be a new dense vector.
        // Use these as the "output" and local vals and locs as current vals
        double[] valsAfter = new double[dimensionality];
        int[] locsAfter = new int[dimensionality];

        int idx = 0;
        for (int i = 0; i < dimensionality; ++i)
        {
            if ((idx < indices.length) && indices[idx] == i)
            {
                valsAfter[i] = values[idx] + other.values[i] * scaleFactor;
                ++idx;
            }
            else
            {
                valsAfter[i] = other.values[i] * scaleFactor;
            }
            locsAfter[i] = i;
        }
        values = valsAfter;
        indices = locsAfter;
    }

    @Override
    public void scaledPlusEquals(
        final SparseVector other,
        final double scaleFactor)
    {
        this.assertSameDimensionality(other);
        compress();

        int nnz = numNonZeroAfterOp(other, SparseMatrix.Combiner.OR);
        double[] valsAfter = new double[nnz];
        int[] locsAfter = new int[nnz];

        int myidx = 0;
        int otheridx = 0;
        int outidx = 0;
        while ((myidx < values.length) && (otheridx < other.values.length))
        {
            if (indices[myidx] == other.indices[otheridx])
            {
                valsAfter[outidx] = values[myidx] + other.values[otheridx] * scaleFactor;
                locsAfter[outidx] = indices[myidx];
                ++myidx;
                ++otheridx;
            }
            else if (indices[myidx] < other.indices[otheridx])
            {
                valsAfter[outidx] = values[myidx];
                locsAfter[outidx] = indices[myidx];
                ++myidx;
            }
            else // if (other.locs[otheridx] < locs[myidx])
            {
                valsAfter[outidx] = other.values[otheridx] * scaleFactor;
                locsAfter[outidx] = other.indices[otheridx];
                ++otheridx;
            }
            ++outidx;
        }
        while (myidx < values.length)
        {
            valsAfter[outidx] = values[myidx];
            locsAfter[outidx] = indices[myidx];
            ++myidx;
            ++outidx;
        }
        while (otheridx < other.values.length)
        {
            valsAfter[outidx] = other.values[otheridx] * scaleFactor;
            locsAfter[outidx] = other.indices[otheridx];
            ++otheridx;
            ++outidx;
        }
        values = valsAfter;
        indices = locsAfter;
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: This operation is not recommended as it is most likely to create a
     * very dense vector being stored in a sparse-vector format. This will be
     * memory inefficient.
     */
    @Override
    public final void plusEquals(
        final DenseVector other)
    {
        this.scaledPlusEquals(other, 1.0);
    }

    @Override
    public final void plusEquals(
        final SparseVector other)
    {
        this.scaledPlusEquals(other, 1.0);
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: This operation is not recommended as it is most likely to create a
     * very dense vector being stored in a sparse-vector format. This will be
     * memory inefficient.
     */
    @Override
    public final void minusEquals(
        final DenseVector other)
    {
        this.scaledPlusEquals(other, -1.0);
    }

    @Override
    public final void minusEquals(
        final SparseVector other)
    {
        this.scaledPlusEquals(other, -1.0);
    }

    @Override
    public final void dotTimesEquals(
        final DenseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        for (int i = 0; i < values.length; ++i)
        {
            values[i] *= other.values[indices[i]];
        }
    }

    @Override
    public final void dotTimesEquals(
        final SparseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        other.compress();
        int nnz = numNonZeroAfterOp(other, SparseMatrix.Combiner.AND);
        double[] valsAfter = new double[nnz];
        int[] locsAfter = new int[nnz];

        int outidx = 0;
        int otheridx = 0;
        for (int i = 0; i < values.length; ++i)
        {
            while (other.indices[otheridx] < indices[i])
            {
                ++otheridx;
            }
            if (other.indices[otheridx] == indices[i])
            {
                valsAfter[outidx] = values[i] * other.values[otheridx];
                locsAfter[outidx] = indices[i];
                ++outidx;
                ++otheridx;
            }
        }
        values = valsAfter;
        indices = locsAfter;
    }

    @Override
    public final double euclideanDistanceSquared(
        final DenseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        double dist = 0;
        int idx = 0;
        for (int i = 0; i < dimensionality; ++i)
        {
            double tmp = other.values[i];
            if ((idx < indices.length) && (indices[idx] == i))
            {
                tmp -= values[idx];
                ++idx;
            }
            dist += tmp * tmp;
        }

        return dist;
    }

    @Override
    public final double euclideanDistanceSquared(
        final SparseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        other.compress();
        int myidx = 0;
        int otheridx = 0;
        double dist = 0;
        while ((myidx < values.length) && (otheridx < other.values.length))
        {
            double tmp;
            if (indices[myidx] == other.indices[otheridx])
            {
                tmp = values[myidx] - other.values[otheridx];
                ++myidx;
                ++otheridx;
            }
            else if (indices[myidx] < other.indices[otheridx])
            {
                tmp = values[myidx];
                ++myidx;
            }
            else // if (other.locs[otheridx] < locs[myidx])
            {
                tmp = other.values[otheridx];
                ++otheridx;
            }
            dist += tmp * tmp;
        }
        // Only one of the following while loops (if either) should ever occur -- not both.
        while (myidx < values.length)
        {
            dist += values[myidx] * values[myidx];
            ++myidx;
        }
        while (otheridx < other.values.length)
        {
            dist += other.values[otheridx] * other.values[otheridx];
            ++otheridx;
        }

        return dist;
    }

    @Override
    public final Matrix outerProduct(
        final DenseVector other)
    {
        compress();
        int numRows = getDimensionality();
        int numCols = other.getDimensionality();
        // This is debatable.  The issue is that each row is likely to be dense,
        // but many rows are likely to be completely empty.  My current thinking
        // is that storing the empty rows as dense vectors is a complete waste,
        // and the additional overehead of storing the dense rows as sparse
        // vectors is decreased when the sparse matrix is optimized.
        SparseMatrix result = new SparseMatrix(numRows, numCols, true);

        int idx = 0;
        for (int i = 0; i < numRows; ++i)
        {
            SparseVector row = new SparseVector(numCols);
            if ((idx < indices.length) && (indices[idx] == i))
            {
                for (int j = 0; j < numCols; ++j)
                {
                    row.elements.put(j, new MutableDouble(values[idx] * other.values[j]));
                }
                ++idx;
            }
            result.setRowInternal(i, row);
        }

        return result;
    }

    @Override
    public final Matrix outerProduct(
        final SparseVector other)
    {
        compress();
        other.compress();
        int numRows = getDimensionality();
        int numCols = other.getDimensionality();

        SparseMatrix result = new SparseMatrix(numRows, numCols, true);
        int idx = 0;
        for (int i = 0; i < numRows; ++i)
        {
            SparseVector row = new SparseVector(numCols);
            if ((idx < indices.length) && (indices[idx] == i))
            {
                for (int j = 0; j < other.indices.length; ++j)
                {
                    row.elements.put(other.indices[j], new MutableDouble(values[idx] * other.values[j]));
                }
                ++idx;
            }
            result.setRowInternal(i, row);
        }

        return result;
    }

    @Override
    public final Vector stack(
        final DenseVector other)
    {
        compress();
        Vector result;
        int len = dimensionality + other.values.length;
        int nnz = getNonZeroCount() + other.countNonZeros();
        if (nnz > SPARSE_TO_DENSE_THRESHOLD * len)
        {
            result = new DenseVector(len);
        }
        else
        {
            result = new SparseVector(len);
        }
        // NOTE: The below could be faster (and I could get rid of all of the
        // "setElement"s if I wanted to write two versions of this method.  As
        // it's likely to be infrequently called, I don't want to increase code
        // complexity for a minimal gain.
        int idx = 0;
        for (int i = 0; i < dimensionality; ++i)
        {
            if ((idx < indices.length) && (indices[idx] == i))
            {
                result.setElement(i, values[idx]);
                ++idx;
            }
            else
            {
                result.setElement(i, 0);
            }
        }
        for (int i = 0; i < other.values.length; ++i)
        {
            result.setElement(dimensionality + i, other.values[i]);
        }

        return result;
    }

    @Override
    public final Vector stack(
        final SparseVector other)
    {
        compress();
        other.compress();
        int len = dimensionality + other.dimensionality;
        int nnz = getNonZeroCount() + other.getNonZeroCount();
        SparseVector result = new SparseVector(len);
        result.values = new double[nnz];
        result.indices = new int[nnz];
        int idx = 0;
        for (int i = 0; i < indices.length; ++i)
        {
            result.values[idx] = values[i];
            result.indices[idx] = indices[i];
            ++idx;
        }
        for (int i = 0; i < other.indices.length; ++i)
        {
            result.values[idx] = other.values[i];
            result.indices[idx] = other.indices[i] + dimensionality;
            ++idx;
        }

        return result;
    }

    @Override
    public final double dotProduct(
        final SparseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        other.compress();
        double result = 0;
        int otheridx = 0;
        final int thisLength = this.indices.length;
        final int otherLength = other.indices.length;
        for (int i = 0; i < thisLength && otheridx < otherLength; ++i)
        {
            while (other.indices[otheridx] < indices[i])
            {
                ++otheridx;
                if (otheridx >= otherLength)
                {
                    return result;
                }
            }
            if (other.indices[otheridx] == indices[i])
            {
                result += values[i] * other.values[otheridx];
                ++otheridx;
            }
        }

        return result;
    }

    @Override
    public final double dotProduct(
        final DenseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        double result = 0;
        for (int i = 0; i < indices.length; ++i)
        {
            result += values[i] * other.values[indices[i]];
        }

        return result;
    }

    @Override
    final public Iterator<VectorEntry> iterator()
    {
        this.compress();
        return new EntryIterator();
    }

    @Override
    final public int getDimensionality()
    {
        return dimensionality;
    }

    /**
     * Helper that checks that i is within the bounds of this array. Throws an
     * ArrayIndexOutOfBoundsException if not in bounds
     *
     * @param i The index to check
     * @throws ArrayIndexOutOfBoundsException if i not in bounds
     */
    private void checkBounds(
        final int i)
    {
        if ((i < 0) || (i >= dimensionality))
        {
            throw new ArrayIndexOutOfBoundsException("Input index " + i
                + " is out of bounds for vectors of length " + dimensionality);
        }
    }

    @Override
    public double get(
        final int index)
    {
        return getElement(index);
    }

    @Override
    final public double getElement(
        final int index)
    {
        checkBounds(index);
        if (isCompressed())
        {
            int low = 0;
            int high = indices.length - 1;
            while (low <= high)
            {
                int mid = (int) Math.round((low + high) * .5);
                if (indices[mid] == index)
                {
                    return values[mid];
                }
                else if (indices[mid] < index)
                {
                    low = mid + 1;
                }
                else // if (locs[mid] > index)
                {
                    high = mid - 1;
                }
            }
            // It's not found cuz it's not in there!
            return 0;
        }
        else
        {
            MutableDouble v = elements.get(index);
            return (v == null) ? 0 : v.value;
        }
    }

    @Override
    public void set(
        final int index,
        final double value)
    {
        setElement(index, value);
    }

    @Override
    final public void setElement(
        final int index,
        final double value)
    {
        if (this.isCompressed())
        {
            // If we're in compressed mode and this matches an existing index,
            // then we can just update the array value.
            final int i = Arrays.binarySearch(this.indices, index);
            if (i >= 0)
            {
                // Found the index in the array, so update the value.
                this.values[i] = value;
                return;
            }
            // else - No entry found. Go through to normal modification mode.
        }
        
        decompress();
        checkBounds(index);
        if (value == 0.0)
        {
            // Remove zeros. If it is not there, then this will be a no-op.
            this.elements.remove(index);
        }
        else
        {
            // See if there is already an entry for this value.
            MutableDouble entry = this.elements.get(index);
            if (entry != null)
            {
                // Update the value.
                entry.value = value;
            }
            else
            {
                // Make a new entry for the value.
                this.elements.put(index, new MutableDouble(value));
            }
        }
    }

    @Override
    final public Vector subVector(
        final int minIndex,
        final int maxIndex)
    {
        if (minIndex > maxIndex)
        {
            throw new NegativeArraySizeException("Input bounds [" + minIndex
                + ", " + maxIndex + "] goes backwards!");
        }
        if (minIndex < 0 || minIndex > maxIndex || maxIndex > dimensionality)
        {
            throw new ArrayIndexOutOfBoundsException("Input bounds for sub-"
                + "vector [" + minIndex + ", " + maxIndex
                + "] is not within supported bounds [0, " + dimensionality + ")");
        }
        compress();
        SparseVector result = new SparseVector(maxIndex - minIndex + 1);
        for (int i = 0; i < indices.length; ++i)
        {
            if ((indices[i] >= minIndex) && (indices[i] <= maxIndex))
            {
                result.elements.put(indices[i] - minIndex, new MutableDouble(values[i]));
            }
        }

        return result;
    }

    /**
     * Package-private helper that returns the compressed values. NOTE: If this
     * isn't compressed before this method is called, this method returns null
     * (it does not ensure this is compressed for optimization).
     *
     * @return the compressed values
     */
    final double[] getValues()
    {
        return values;
    }

    /**
     * Package-private helper that returns the compressed locations. NOTE: If
     * this isn't compressed before this method is called, this method returns
     * null (it does not ensure this is compressed for optimization).
     *
     * @return the compressed locations
     */
    final int[] getIndices()
    {
        return indices;
    }

    /**
     * Package-private helper that tells how many non-zero entries are in this
     * sparse vector
     *
     * @return the number of non-zero entries in this sparse vector
     */
    final int getNonZeroCount()
    {
        if (isCompressed())
        {
            int nnz = 0;
            for (double v : values)
            {
                if (v != 0)
                {
                    ++nnz;
                }
            }
            return nnz;
        }
        else
        {
            return elements.size();
        }
    }

    @Override
    final public Vector scale(
        final double d)
    {
        compress();
        SparseVector result = new SparseVector(this);
        for (int i = 0; i < result.values.length; ++i)
        {
            result.values[i] *= d;
        }

        return result;
    }

    /**
     * Package-private helper that clears the contents of this vector
     */
    final void clear()
    {
        if (isCompressed())
        {
            values = null;
            indices = null;
        }
        elements.clear();
    }

    @Override
    public boolean isSparse()
    {
        return true;
    }

    @Override
    public VectorFactory<?> getVectorFactory()
    {
        return CustomSparseVectorFactory.INSTANCE;
    }

    @Override
    public double sum()
    {
        this.compress();
        double result = 0.0;
        for (final double value : this.values)
        {
            result += value;
        }
        return result;
    }
    
    @Override
    public double getMinValue()
    {
        this.compress();
        double min = this.getEntryCount() < this.getDimensionality() ? 0.0 :
            Double.POSITIVE_INFINITY;
        for (final double value : this.values)
        {
            if (value < min) 
            {
                min = value;
            }
        }
        return min;
    }
    
    @Override
    public double getMaxValue()
    {
        this.compress();
        double max = this.getEntryCount() < this.getDimensionality() ? 0.0 :
            Double.NEGATIVE_INFINITY;
        for (final double value : this.values)
        {
            if (value > max) 
            {
                max = value;
            }
        }
        return max;
    }
    
    @Override
    public int getEntryCount()
    {
        this.compress();
        return this.values.length;
    }

    /**
     * Implements an iterator over sparse entries in this vector.
     */
    private class EntryIterator
        extends Object
        implements Iterator<VectorEntry>
    {
        
        /** Index of current element in iterator. */
        private int offset;
        
        /**
         * Creates a new {@link EntryIterator}.
         */
        public EntryIterator()
        {
            super();
            
            this.offset = 0;
        }

        @Override
        public boolean hasNext()
        {
            this.assertNoModification();
            return indices != null && this.offset < indices.length;
        }

        @Override
        public VectorEntry next()
        {
            this.assertNoModification();
            final VectorEntry result = new Entry(this.offset);
            this.offset++;
            return result;
        }
        
        /**
         * Asserts that no (bad) modifications have been made since the
         * iterator started.
         */
        private void assertNoModification()
        {
            if (!elements.isEmpty())
            {
                throw new ConcurrentModificationException();
            }
        }
        
    }
    
    /**
     * Represents an entry in the sparse vector. Used by the 
     * {@link EntryIterator}.
     */
    class Entry
        extends Object
        implements VectorEntry
    {
        /** The 0-based offset in the compressed representation. */
        private int offset;
        
        /**
         * Creates a new {@link Entry}/
         * 
         * @param   offset The 0-based offset in the compressed representation.
         */
        public Entry(
            final int offset)
        {
            super();
            
            this.offset = offset;
        }

        @Override
        public int getIndex()
        {
            return indices[offset];
        }

        @Override
        public void setIndex(
            final int index)
        {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public double getValue()
        {
            return values[offset];
        }

        @Override
        public void setValue(
            final double value)
        {
            values[offset] = value;
        }
        
    }
}

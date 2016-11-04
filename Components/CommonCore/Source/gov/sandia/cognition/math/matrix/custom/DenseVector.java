/*
 * File:                DenseVector.java
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

import gov.sandia.cognition.collection.ArrayUtil;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Our dense vector implementation. Rather straightforward: stores all of the
 * data in an in-order array.
 *
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
public class DenseVector
    extends BaseVector
{

    /**
     * The data is stored in this vector.
     */
    double[] values;

    /**
     * This should never be called by anything or anyone other than Java's
     * serialization code.
     */
    protected DenseVector()
    {
        super();
        // NOTE: This doesn't initialize anything
    }

    /**
     * Creates a dense vector of length n. Initializes the vector to all zeroes.
     *
     * @param n The length the vector should be
     */
    public DenseVector(
        final int n)
    {
        super();
        
        this.values = new double[n];
    }

    /**
     * Initializes the vector to length n with all values initialized to
     * defaultVal.
     *
     * @param n The length the vector should be
     * @param defaultVal The initial value for all elements of the vector
     */
    public DenseVector(
        final int n,
        final double defaultVal)
    {
        super();
        
        this.values = new double[n];
        Arrays.fill(this.values, defaultVal);
    }

    /**
     * Copy constructor copies the input dense vector into this
     *
     * @param v The vector to copy
     */
    public DenseVector(
        final DenseVector v)
    {
        this(v.values);
    }

    /**
     * Helper constructor that copies the data from the array into this
     *
     * @param arr An array of doubles to put in this dense vector
     */
    public DenseVector(
        final double[] arr)
    {
        super();
        
        this.values = Arrays.copyOf(arr, arr.length);
    }

    /**
     * Helper constructor that copies the data from the list into this
     *
     * @param arr A list of doubles to put into this dense vector
     */
    public DenseVector(
        final List<Double> arr)
    {
        super();
        
        final int d = arr.size();
        values = new double[d];
        for (int i = 0; i < d; ++i)
        {
            values[i] = arr.get(i);
        }
    }

    @Override
    final public DenseVector clone()
    {
        final DenseVector result = (DenseVector) super.clone();
        result.values = ArrayUtil.copy(this.values);
        return result;
    }

    @Override
    public void scaledPlusEquals(
        final DenseVector other,
        final double scaleFactor)
    {
        this.assertSameDimensionality(other);
        for (int i = 0; i < values.length; ++i)
        {
            values[i] += other.values[i] * scaleFactor;
        }
    }

    @Override
    public void scaledPlusEquals(
        final SparseVector other,
        final double scaleFactor)
    {
        this.assertSameDimensionality(other);
        other.compress();
        int[] locs = other.getIndices();
        double[] vals = other.getValues();
        for (int i = 0; i < locs.length; ++i)
        {
            values[locs[i]] += vals[i] * scaleFactor;
        }
    }

    @Override
    public final void plusEquals(
        final DenseVector other)
    {
        this.assertSameDimensionality(other);
        for (int i = 0; i < values.length; ++i)
        {
            values[i] += other.values[i];
        }
    }

    @Override
    public final void plusEquals(
        final SparseVector other)
    {
        this.assertSameDimensionality(other);
        other.compress();
        int[] locs = other.getIndices();
        double[] vals = other.getValues();
        for (int i = 0; i < locs.length; ++i)
        {
            values[locs[i]] += vals[i];
        }
    }

    @Override
    public final void minusEquals(
        final DenseVector other)
    {
        this.assertSameDimensionality(other);
        for (int i = 0; i < values.length; ++i)
        {
            values[i] -= other.values[i];
        }
    }

    @Override
    public final void minusEquals(
        final SparseVector other)
    {
        this.assertSameDimensionality(other);
        other.compress();
        int[] locs = other.getIndices();
        double[] vals = other.getValues();
        for (int i = 0; i < locs.length; ++i)
        {
            values[locs[i]] -= vals[i];
        }
    }

    @Override
    public final void dotTimesEquals(
        final DenseVector other)
    {
        this.assertSameDimensionality(other);
        for (int i = 0; i < values.length; ++i)
        {
            values[i] *= other.values[i];
        }
    }

    @Override
    public final void dotTimesEquals(
        final SparseVector other)
    {
        this.assertSameDimensionality(other);
        other.compress();
        int[] locs = other.getIndices();
        double[] vals = other.getValues();
        int idx = 0;
        for (int i = 0; i < values.length; ++i)
        {
            if ((idx < locs.length) && (locs[idx] == i))
            {
                values[i] *= vals[idx];
                ++idx;
            }
            else
            {
                values[i] = 0;
            }
        }
    }

    @Override
    public final double euclideanDistanceSquared(
        final DenseVector other)
    {
        this.assertSameDimensionality(other);
        double dist = 0.0;
        double tmp;
        for (int i = 0; i < values.length; ++i)
        {
            tmp = (values[i] - other.values[i]);
            dist += tmp * tmp;
        }

        return dist;
    }

    @Override
    public final double euclideanDistanceSquared(
        final SparseVector other)
    {
        return other.euclideanDistanceSquared(this);
    }

    @Override
    public final Matrix outerProduct(
        final DenseVector other)
    {
        int numRows = getDimensionality();
        int numCols = other.getDimensionality();
        final DenseVector[] rows = new DenseVector[numRows];
        for (int i = 0; i < numRows; ++i)
        {
            DenseVector row = new DenseVector(numCols);
            for (int j = 0; j < numCols; ++j)
            {
                row.values[j] = values[i] * other.values[j];
            }
            rows[i] = row;
        }

        return new DenseMatrix(rows);
    }

    @Override
    public final Matrix outerProduct(
        final SparseVector other)
    {
        int numRows = getDimensionality();
        int numCols = other.getDimensionality();
        SparseMatrix result = new SparseMatrix(numRows, numCols, true);
        other.compress();
        int[] locs = other.getIndices();
        double[] vals = other.getValues();
        for (int i = 0; i < numRows; ++i)
        {
            SparseVector row = new SparseVector(numCols);
            for (int j = 0; j < locs.length; ++j)
            {
                row.setElement(locs[j], values[i] * vals[j]);
            }
            result.setRowInternal(i, row);
        }

        return result;
    }

    @Override
    public final Vector stack(
        final DenseVector other)
    {
        DenseVector result = new DenseVector(values.length + other.values.length);
        for (int i = 0; i < values.length; ++i)
        {
            result.values[i] = values[i];
        }
        for (int i = 0; i < other.values.length; ++i)
        {
            result.values[values.length + i] = other.values[i];
        }

        return result;
    }

    @Override
    public final Vector stack(
        final SparseVector other)
    {
        Vector result;
        int len = values.length + other.getDimensionality();
        int nnz = countNonZeros() + other.countNonZeros();
        if (nnz > SparseVector.SPARSE_TO_DENSE_THRESHOLD * len)
        {
            result = new DenseVector(len);
        }
        else
        {
            result = new SparseVector(len);
        }
        for (int i = 0; i < values.length; ++i)
        {
            result.setElement(i, values[i]);
        }
        // NOTE: The below could be faster (and I could get rid of all of the
        // "setElement"s if I wanted to write two versions of this method.  As
        // it's likely to be infrequently called, I don't want to increase code
        // complexity for a minimal gain.
        // 
        // The way to do this would be to move the below into the above if/elses.
        // Then, you would only need to set the 0s in the dense vector class
        // (as 0s are implicitly stored in sparse vectors).  Moreover, you
        // could call the package-private methods to get direct access to the
        // entries of the sparse or dense vector instead of calling setElement.
        other.compress();
        int[] locs = other.getIndices();
        double[] vals = other.getValues();
        int idx = 0;
        final int otherDimensionality = other.getDimensionality();
        for (int i = 0; i < otherDimensionality; ++i)
        {
            if ((idx < locs.length) && (locs[idx] == i))
            {
                result.setElement(values.length + i, vals[idx]);
                ++idx;
            }
            else
            {
                result.setElement(values.length + i, 0);
            }
        }

        return result;
    }

    @Override
    public final double dotProduct(
        final DenseVector other)
    {
        this.assertSameDimensionality(other);
        double result = 0;
        for (int i = 0; i < values.length; ++i)
        {
            result += values[i] * other.values[i];
        }

        return result;
    }

    @Override
    public final double dotProduct(
        final SparseVector other)
    {
        return other.dotProduct(this);
    }

    @Override
    final public Iterator<VectorEntry> iterator()
    {
        return new VectorIterator(this);
    }

    @Override
    final public int getDimensionality()
    {
        return values.length;
    }

    @Override
    public double get(
        final int index)
    {
        return values[index];
    }

    @Override
    final public double getElement(
        final int index)
    {
        return values[index];
    }

    @Override
    public void set(
        final int index,
        final double value)
    {
        values[index] = value;
    }

    @Override
    final public void setElement(
        final int index,
        final double value)
    {
        values[index] = value;
    }
    
    /**
     * Package-private method that allows peers direct access to the elements
     * contained herein.
     *
     * @return the array of elements stored herein.
     */
    final double[] getValues()
    {
        return this.values;
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
        if ((minIndex < 0) || (maxIndex >= values.length))
        {
            throw new ArrayIndexOutOfBoundsException("Input subvector from "
                + minIndex + " to " + maxIndex + " (inclusive) exceeds the "
                + "bounds of this vector [0, " + values.length + ").");
        }
        int len = maxIndex - minIndex + 1;
        DenseVector result = new DenseVector(len);
        for (int i = minIndex; i <= maxIndex; ++i)
        {
            result.values[i - minIndex] = values[i];
        }

        return result;
    }

    @Override
    final public Vector scale(
        final double d)
    {
        DenseVector result = new DenseVector(values.length);
        for (int i = 0; i < values.length; ++i)
        {
            result.values[i] = values[i] * d;
        }

        return result;
    }

    @Override
    final public Vector dotTimes(
        final Vector v)
    {
        // By switch from this.dotTimes(v) to v.dotTimes(this), we get sparse
        // vectors dotted with dense still being sparse and dense w/ dense is
        // still dense.  The way this was originally implemented in the Foundry
        // (this.clone().dotTimesEquals(v)), if v is sparse, it returns a
        // dense vector type storing sparse data.
        Vector result = v.clone();
        result.dotTimesEquals(this);
        return result;
    }

    /**
     * Computes the number of non-zero entries in this
     *
     * @return The number of non-zero entries in this
     */
    final public int countNonZeros()
    {
        int nnz = 0;
        for (final double d : values)
        {
            nnz += (d == 0) ? 0 : 1;
        }

        return nnz;
    }

    @Override
    public boolean isSparse()
    {
        return false;
    }

    @Override
    public VectorFactory<?> getVectorFactory()
    {
        return CustomDenseVectorFactory.INSTANCE;
    }

    @Override
    public double sum()
    {
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
        double min = Double.POSITIVE_INFINITY;
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
        double max = Double.NEGATIVE_INFINITY;
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
        return this.values.length;
    }

    @Override
    public void zero()
    {
        Arrays.fill(this.values, 0.0);
    }
    
}

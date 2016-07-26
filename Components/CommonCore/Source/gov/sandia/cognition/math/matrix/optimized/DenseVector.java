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

package gov.sandia.cognition.math.matrix.optimized;

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
     * The data is stored in this vector
     */
    private double[] vec;

    /**
     * This should never be called by anything or anyone other than Java's
     * serialization code.
     */
    protected DenseVector()
    {
        // NOTE: This doesn't initialize anything
    }

    /**
     * Creates a dense vector of length n. Initializes the vector to all zeroes.
     *
     * @param n The length the vector should be
     */
    public DenseVector(int n)
    {
        vec = new double[n];
        Arrays.fill(vec, 0);
    }

    /**
     * Initializes the vector to length n with all values initialized to
     * defaultVal.
     *
     * @param n The length the vector should be
     * @param defaultVal The initial value for all elements of the vector
     */
    public DenseVector(int n,
        double defaultVal)
    {
        vec = new double[n];
        Arrays.fill(vec, defaultVal);
    }

    /**
     * Copy constructor copies the input dense vector into this
     *
     * @param v The vector to copy
     */
    public DenseVector(final DenseVector v)
    {
        vec = Arrays.copyOf(v.vec, v.vec.length);
    }

    /**
     * Helper constructor that copies the data from the array into this
     *
     * @param arr An array of doubles to put in this dense vector
     */
    public DenseVector(double[] arr)
    {
        vec = Arrays.copyOf(arr, arr.length);
    }

    /**
     * Helper constructor that copies the data from the list into this
     *
     * @param arr A list of doubles to put into this dense vector
     */
    public DenseVector(List<Double> arr)
    {
        vec = new double[arr.size()];
        for (int i = 0; i < arr.size(); ++i)
        {
            vec[i] = arr.get(i);
        }
    }

    /**
     * Helper, package-private constructor that initializes the vector to length
     * n, but does not set the value for any of the elements herein. NOTE: This
     * should only be called if the values are immediately to be set.
     *
     * @param n The length of the vector to create
     * @param unused An unused parameter that just differentiates this
     * constructor from the previous that initializes all values to 0.
     */
    DenseVector(int n,
        boolean unused)
    {
        vec = new double[n];
        // Don't initialize values
    }

    @Override
    final public DenseVector clone()
    {
        final DenseVector result = (DenseVector) super.clone();
        result.vec = ArrayUtil.copy(this.vec);
        return new DenseVector(this);
    }

    @Override
    public void scaledPlusEquals(DenseVector other,
        double scaleFactor)
    {
        this.assertSameDimensionality(other);
        for (int i = 0; i < vec.length; ++i)
        {
            vec[i] += other.vec[i] * scaleFactor;
        }
    }

    @Override
    public void scaledPlusEquals(SparseVector other,
        double scaleFactor)
    {
        this.assertSameDimensionality(other);
        other.compress();
        int[] locs = other.getLocs();
        double[] vals = other.getVals();
        for (int i = 0; i < locs.length; ++i)
        {
            vec[locs[i]] += vals[i] * scaleFactor;
        }
    }

    @Override
    public final void plusEquals(DenseVector other)
    {
        this.assertSameDimensionality(other);
        for (int i = 0; i < vec.length; ++i)
        {
            vec[i] += other.vec[i];
        }
    }

    @Override
    public final void plusEquals(SparseVector other)
    {
        this.assertSameDimensionality(other);
        other.compress();
        int[] locs = other.getLocs();
        double[] vals = other.getVals();
        for (int i = 0; i < locs.length; ++i)
        {
            vec[locs[i]] += vals[i];
        }
    }

    @Override
    public final void minusEquals(DenseVector other)
    {
        this.assertSameDimensionality(other);
        for (int i = 0; i < vec.length; ++i)
        {
            vec[i] -= other.vec[i];
        }
    }

    @Override
    public final void minusEquals(SparseVector other)
    {
        this.assertSameDimensionality(other);
        other.compress();
        int[] locs = other.getLocs();
        double[] vals = other.getVals();
        for (int i = 0; i < locs.length; ++i)
        {
            vec[locs[i]] -= vals[i];
        }
    }

    @Override
    public final void dotTimesEquals(DenseVector other)
    {
        this.assertSameDimensionality(other);
        for (int i = 0; i < vec.length; ++i)
        {
            vec[i] *= other.vec[i];
        }
    }

    @Override
    public final void dotTimesEquals(SparseVector other)
    {
        this.assertSameDimensionality(other);
        other.compress();
        int[] locs = other.getLocs();
        double[] vals = other.getVals();
        int idx = 0;
        for (int i = 0; i < vec.length; ++i)
        {
            if ((idx < locs.length) && (locs[idx] == i))
            {
                vec[i] *= vals[idx];
                ++idx;
            }
            else
            {
                vec[i] = 0;
            }
        }
    }

    @Override
    public final double euclideanDistanceSquared(DenseVector other)
    {
        this.assertSameDimensionality(other);
        double dist = 0.0;
        double tmp;
        for (int i = 0; i < vec.length; ++i)
        {
            tmp = (vec[i] - other.vec[i]);
            dist += tmp * tmp;
        }

        return dist;
    }

    @Override
    public final double euclideanDistanceSquared(SparseVector other)
    {
        return other.euclideanDistanceSquared(this);
    }

    @Override
    public final Matrix outerProduct(DenseVector other)
    {
        int numRows = getDimensionality();
        int numCols = other.getDimensionality();
        DenseMatrix ret = new DenseMatrix(numRows, numCols, true);
        for (int i = 0; i < numRows; ++i)
        {
            DenseVector row = new DenseVector(numCols, true);
            for (int j = 0; j < numCols; ++j)
            {
                row.vec[j] = vec[i] * other.vec[j];
            }
            ret.setRow(i, row);
        }

        return ret;
    }

    @Override
    public final Matrix outerProduct(SparseVector other)
    {
        int numRows = getDimensionality();
        int numCols = other.getDimensionality();
        SparseMatrix ret = new SparseMatrix(numRows, numCols, true);
        other.compress();
        int[] locs = other.getLocs();
        double[] vals = other.getVals();
        for (int i = 0; i < numRows; ++i)
        {
            SparseVector row = new SparseVector(numCols);
            for (int j = 0; j < locs.length; ++j)
            {
                row.setElement(locs[j], vec[i] * vals[j]);
            }
            ret.setRowInternal(i, row);
        }

        return ret;
    }

    @Override
    public final Vector stack(DenseVector other)
    {
        DenseVector ret = new DenseVector(vec.length + other.vec.length);
        for (int i = 0; i < vec.length; ++i)
        {
            ret.vec[i] = vec[i];
        }
        for (int i = 0; i < other.vec.length; ++i)
        {
            ret.vec[vec.length + i] = other.vec[i];
        }

        return ret;
    }

    @Override
    public final Vector stack(SparseVector other)
    {
        Vector ret;
        int len = vec.length + other.getDimensionality();
        int nnz = numNonZero() + other.numNonZero();
        if (nnz > SparseVector.SPARSE_TO_DENSE_THRESHOLD * len)
        {
            ret = new DenseVector(len, true);
        }
        else
        {
            ret = new SparseVector(len);
        }
        for (int i = 0; i < vec.length; ++i)
        {
            ret.setElement(i, vec[i]);
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
        int[] locs = other.getLocs();
        double[] vals = other.getVals();
        int idx = 0;
        for (int i = 0; i < other.getDimensionality(); ++i)
        {
            if ((idx < locs.length) && (locs[idx] == i))
            {
                ret.setElement(vec.length + i, vals[idx]);
                ++idx;
            }
            else
            {
                ret.setElement(vec.length + i, 0);
            }
        }

        return ret;
    }

    @Override
    public final double dotProduct(DenseVector other)
    {
        this.assertSameDimensionality(other);
        double ret = 0;
        for (int i = 0; i < vec.length; ++i)
        {
            ret += vec[i] * other.vec[i];
        }

        return ret;
    }

    @Override
    public final double dotProduct(SparseVector other)
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
        return vec.length;
    }

    @Override
    public double get(int index)
    {
        return vec[index];
    }

    @Override
    final public double getElement(int index)
    {
        return vec[index];
    }

    @Override
    public void set(int index,
        double value)
    {
        vec[index] = value;
    }

    @Override
    final public void setElement(int index,
        double value)
    {
        vec[index] = value;
    }

    /**
     * Package-private method that allows peers direct access to the elements
     * contained herein.
     *
     * @return the array of elements stored herein.
     */
// TODO: Rename this.
    final double[] elements()
    {
        return vec;
    }
    
    @Override
    final public Vector subVector(int minIndex,
        int maxIndex)
    {
        if (minIndex > maxIndex)
        {
            throw new NegativeArraySizeException("Input bounds [" + minIndex
                + ", " + maxIndex + "] goes backwards!");
        }
        if ((minIndex < 0) || (maxIndex >= vec.length))
        {
            throw new ArrayIndexOutOfBoundsException("Input subvector from "
                + minIndex + " to " + maxIndex + " (inclusive) exceeds the "
                + "bounds of this vector [0, " + vec.length + ").");
        }
        int len = maxIndex - minIndex + 1;
        DenseVector ret = new DenseVector(len);
        for (int i = minIndex; i <= maxIndex; ++i)
        {
            ret.vec[i - minIndex] = vec[i];
        }

        return ret;
    }

    @Override
    final public Vector scale(double d)
    {
        DenseVector ret = new DenseVector(vec.length, true);
        for (int i = 0; i < vec.length; ++i)
        {
            ret.vec[i] = vec[i] * d;
        }

        return ret;
    }

    @Override
    final public Vector dotTimes(Vector v)
    {
        // By switch from this.dotTimes(v) to v.dotTimes(this), we get sparse
        // vectors dotted with dense still being sparse and dense w/ dense is
        // still dense.  The way this was originally implemented in the Foundry
        // (this.clone().dotTimesEquals(v)), if v is sparse, it returns a
        // dense vector type storing sparse data.
        Vector ret = v.clone();
        ret.dotTimesEquals(this);
        return ret;
    }

    /**
     * Computes the number of non-zero entries in this
     *
     * @return The number of non-zero entries in this
     */
// TODO: Rename this.
    final public int numNonZero()
    {
        int nnz = 0;
        for (double d : vec)
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
        return DenseVectorFactoryOptimized.INSTANCE;
    }

    @Override
    public double sum()
    {
        double result = 0.0;
        for (final double value : this.vec)
        {
            result += value;
        }
        return result;
    }
    
    @Override
    public double getMinValue()
    {
        double min = Double.POSITIVE_INFINITY;
        for (final double value : this.vec)
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
        for (final double value : this.vec)
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
        return this.vec.length;
    }

    @Override
    public void zero()
    {
        Arrays.fill(this.vec, 0.0);
    }
    
}

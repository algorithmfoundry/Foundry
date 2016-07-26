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

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.Arrays;
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
    final private int n;

    /**
     * The index-to-value map
     */
    private Map<Integer, Double> elements;

    /**
     * Compressed version of the data: The values. Similar to the Yale format
     * for sparse matrices.
     */
    private double[] vals;

    /**
     * Compressed version of the data: The locations. Similar to the Yale format
     * for sparse matrices.
     */
    private int[] locs;

    /**
     * Create a new sparse vector. All values begin empty (which is interpreted
     * to 0).
     *
     * @param n The vector length
     */
    public SparseVector(int n)
    {
        ArgumentChecker.assertIsNonNegative("dimensionality", n);
        this.n = n;
        elements = new TreeMap<Integer, Double>();
        vals = null;
        locs = null;
    }

    /**
     * Copy constructor -- creates a deep copy of the input sparse vector.
     *
     * @param v The sparse vector to copy
     */
    public SparseVector(SparseVector v)
    {
        this.n = v.n;
        if (!v.isCompressed())
        {
            elements = new TreeMap<Integer, Double>(v.elements);
            vals = null;
            locs = null;
        }
        else
        {
            elements = new TreeMap<Integer, Double>();
            vals = Arrays.copyOf(v.vals, v.vals.length);
            locs = Arrays.copyOf(v.locs, v.locs.length);
        }
    }

    /**
     * Copy constructor -- creates a deep copy of the input sparse vector.
     *
     * @param v The sparse vector to copy
     */
    public SparseVector(DenseVector v)
    {
        this.n = v.elements().length;
        int nnz = v.numNonZero();
        vals = new double[nnz];
        locs = new int[nnz];
        elements = new TreeMap<Integer, Double>();
        int idx = 0;
        for (int i = 0; i < n; ++i)
        {
            double val = v.elements()[i];
            if (val != 0)
            {
                vals[idx] = val;
                locs[idx] = i;
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
        n = 0;
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
        return (vals != null) && (locs != null);
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
        vals = new double[nnz];
        locs = new int[nnz];
        int idx = 0;
        for (Map.Entry<Integer, Double> e : elements.entrySet())
        {
            locs[idx] = e.getKey();
            vals[idx] = e.getValue();
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
        for (int i = 0; i < vals.length; ++i)
        {
            elements.put(locs[i], vals[i]);
        }
        locs = null;
        vals = null;
    }

    @Override
    final public Vector clone()
    {
        return new SparseVector(this);
    }

    @Override
    final public Vector plus(Vector v)
    {
        // I need to flip this so that if it the input is a dense vector, I
        // return a dense vector.  If it's a sparse vector, then a sparse vector
        // is still returned.
        Vector ret = v.clone();
        ret.plusEquals(this);
        return ret;
    }

    @Override
    final public Vector minus(Vector v)
    {
        // I need to flip this so that if it the input is a dense vector, I
        // return a dense vector.  If it's a sparse vector, then a sparse vector
        // is still returned.
        Vector ret = v.clone();
        ret.negativeEquals();
        ret.plusEquals(this);
        return ret;
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
    private int numNonZeroAfterOp(SparseVector other,
        SparseMatrix.Combiner op)
    {
        compress();
        other.compress();
        int myidx = 0;
        int otheridx = 0;
        int nnz = 0;
        while ((myidx < locs.length) && (otheridx < other.locs.length))
        {
            if (locs[myidx] == other.locs[otheridx])
            {
                ++nnz;
                ++myidx;
                ++otheridx;
            }
            else if (locs[myidx] < other.locs[otheridx])
            {
                if (op == SparseMatrix.Combiner.OR)
                {
                    ++nnz;
                }
                ++myidx;
            }
            else if (other.locs[otheridx] < locs[myidx])
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
            nnz += locs.length - myidx;
            nnz += other.locs.length - otheridx;
        }

        return nnz;
    }

    /**
     * Helper that handles the addition (scalar = 1) and subtraction (scalar =
     * -1) logic for plusEquals(Dense) and minusEquals(Dense).
     *
     * @param other The other vector to add to this
     * @param scalar The scalar (+1/-1) for differentiating addition from
     * subtraction.
     */
    private void plusEqualsScaled(DenseVector other,
        double scalar)
    {
// TODO: Why is this in a separate helper function? Can't we just chain the main ones?
        this.assertSameDimensionality(other);
        compress();

        // Just assume that this is going to be a new dense vector.
        // Use these as the "output" and local vals and locs as current vals
        double[] valsAfter = new double[n];
        int[] locsAfter = new int[n];

        int idx = 0;
        for (int i = 0; i < n; ++i)
        {
            if ((idx < locs.length) && locs[idx] == i)
            {
                valsAfter[i] = vals[idx] + other.elements()[i] * scalar;
                ++idx;
            }
            // In this case, you only need to set it to the current value
            else
            {
                valsAfter[i] = other.elements()[i] * scalar;
            }
            locsAfter[i] = i;
        }
        vals = valsAfter;
        locs = locsAfter;
    }

    /**
     * Helper that handles the addition (scalar = 1) and subtraction (scalar =
     * -1) logic for plusEquals(Sparse) and minusEquals(Sparse).
     *
     * @param other The other vector to add to this
     * @param scalar The scalar (+1/-1) for differentiating addition from
     * subtraction.
     */
    private void plusEqualsScaled(SparseVector other,
        double scalar)
    {
// TODO: Why is this in a separate helper function? Can't we just chain the main ones?
        this.assertSameDimensionality(other);
        compress();

        int nnz = numNonZeroAfterOp(other, SparseMatrix.Combiner.OR);
        double[] valsAfter = new double[nnz];
        int[] locsAfter = new int[nnz];

        int myidx = 0;
        int otheridx = 0;
        int outidx = 0;
        while ((myidx < vals.length) && (otheridx < other.vals.length))
        {
            if (locs[myidx] == other.locs[otheridx])
            {
                valsAfter[outidx] = vals[myidx] + other.vals[otheridx] * scalar;
                locsAfter[outidx] = locs[myidx];
                ++myidx;
                ++otheridx;
            }
            else if (locs[myidx] < other.locs[otheridx])
            {
                valsAfter[outidx] = vals[myidx];
                locsAfter[outidx] = locs[myidx];
                ++myidx;
            }
            else // if (other.locs[otheridx] < locs[myidx])
            {
                valsAfter[outidx] = other.vals[otheridx] * scalar;
                locsAfter[outidx] = other.locs[otheridx];
                ++otheridx;
            }
            ++outidx;
        }
        while (myidx < vals.length)
        {
            valsAfter[outidx] = vals[myidx];
            locsAfter[outidx] = locs[myidx];
            ++myidx;
            ++outidx;
        }
        while (otheridx < other.vals.length)
        {
            valsAfter[outidx] = other.vals[otheridx] * scalar;
            locsAfter[outidx] = other.locs[otheridx];
            ++otheridx;
            ++outidx;
        }
        vals = valsAfter;
        locs = locsAfter;
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: This operation is not recommended as it is most likely to create a
     * very dense vector being stored in a sparse-vector format. This will be
     * memory inefficient.
     */
    @Override
    public void scaledPlusEquals(DenseVector other,
        double scaleFactor)
    {
        plusEqualsScaled(other, scaleFactor);
    }

    @Override
    public void scaledPlusEquals(SparseVector other,
        double scaleFactor)
    {
        plusEqualsScaled(other, scaleFactor);
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: This operation is not recommended as it is most likely to create a
     * very dense vector being stored in a sparse-vector format. This will be
     * memory inefficient.
     */
    @Override
    public final void plusEquals(DenseVector other)
    {
        plusEqualsScaled(other, 1);
    }

    @Override
    public final void plusEquals(SparseVector other)
    {
        plusEqualsScaled(other, 1);
    }

    /**
     * {@inheritDoc}
     *
     * NOTE: This operation is not recommended as it is most likely to create a
     * very dense vector being stored in a sparse-vector format. This will be
     * memory inefficient.
     */
    @Override
    public final void minusEquals(DenseVector other)
    {
        plusEqualsScaled(other, -1);
    }

    @Override
    public final void minusEquals(SparseVector other)
    {
        plusEqualsScaled(other, -1);
    }

    @Override
    public final void dotTimesEquals(DenseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        for (int i = 0; i < vals.length; ++i)
        {
            vals[i] *= other.elements()[locs[i]];
        }
    }

    @Override
    public final void dotTimesEquals(SparseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        other.compress();
        int nnz = numNonZeroAfterOp(other, SparseMatrix.Combiner.AND);
        double[] valsAfter = new double[nnz];
        int[] locsAfter = new int[nnz];

        int outidx = 0;
        int otheridx = 0;
        for (int i = 0; i < vals.length; ++i)
        {
            while (other.locs[otheridx] < locs[i])
            {
                ++otheridx;
            }
            if (other.locs[otheridx] == locs[i])
            {
                valsAfter[outidx] = vals[i] * other.vals[otheridx];
                locsAfter[outidx] = locs[i];
                ++outidx;
                ++otheridx;
            }
        }
        vals = valsAfter;
        locs = locsAfter;
    }

    @Override
    public final double euclideanDistanceSquared(DenseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        double dist = 0;
        int idx = 0;
        for (int i = 0; i < n; ++i)
        {
            double tmp = other.elements()[i];
            if ((idx < locs.length) && (locs[idx] == i))
            {
                tmp -= vals[idx];
                ++idx;
            }
            dist += tmp * tmp;
        }

        return dist;
    }

    @Override
    public final double euclideanDistanceSquared(SparseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        other.compress();
        int myidx = 0;
        int otheridx = 0;
        double dist = 0;
        while ((myidx < vals.length) && (otheridx < other.vals.length))
        {
            double tmp;
            if (locs[myidx] == other.locs[otheridx])
            {
                tmp = vals[myidx] - other.vals[otheridx];
                ++myidx;
                ++otheridx;
            }
            else if (locs[myidx] < other.locs[otheridx])
            {
                tmp = vals[myidx];
                ++myidx;
            }
            else // if (other.locs[otheridx] < locs[myidx])
            {
                tmp = other.vals[otheridx];
                ++otheridx;
            }
            dist += tmp * tmp;
        }
        // Only one of the following while loops (if either) should ever occur -- not both.
        while (myidx < vals.length)
        {
            dist += vals[myidx] * vals[myidx];
            ++myidx;
        }
        while (otheridx < other.vals.length)
        {
            dist += other.vals[otheridx] * other.vals[otheridx];
            ++otheridx;
        }

        return dist;
    }

    @Override
    public final Matrix outerProduct(DenseVector other)
    {
        compress();
        int numRows = getDimensionality();
        int numCols = other.getDimensionality();
        // This is debatable.  The issue is that each row is likely to be dense,
        // but many rows are likely to be completely empty.  My current thinking
        // is that storing the empty rows as dense vectors is a complete waste,
        // and the additional overehead of storing the dense rows as sparse
        // vectors is decreased when the sparse matrix is optimized.
        SparseMatrix ret = new SparseMatrix(numRows, numCols, true);

        int idx = 0;
        for (int i = 0; i < numRows; ++i)
        {
            SparseVector row = new SparseVector(numCols);
            if ((idx < locs.length) && (locs[idx] == i))
            {
                for (int j = 0; j < numCols; ++j)
                {
                    row.elements.put(j, vals[idx] * other.elements()[j]);
                }
                ++idx;
            }
            ret.setRowInternal(i, row);
        }

        return ret;
    }

    @Override
    public final Matrix outerProduct(SparseVector other)
    {
        compress();
        other.compress();
        int numRows = getDimensionality();
        int numCols = other.getDimensionality();

        SparseMatrix ret = new SparseMatrix(numRows, numCols, true);
        int idx = 0;
        for (int i = 0; i < numRows; ++i)
        {
            SparseVector row = new SparseVector(numCols);
            if ((idx < locs.length) && (locs[idx] == i))
            {
                for (int j = 0; j < other.locs.length; ++j)
                {
                    row.elements.put(other.locs[j], vals[idx] * other.vals[j]);
                }
                ++idx;
            }
            ret.setRowInternal(i, row);
        }

        return ret;
    }

    @Override
    public final Vector stack(DenseVector other)
    {
        compress();
        Vector ret;
        int len = n + other.elements().length;
        int nnz = numNonZero() + other.numNonZero();
        if (nnz > SPARSE_TO_DENSE_THRESHOLD * len)
        {
            ret = new DenseVector(len, true);
        }
        else
        {
            ret = new SparseVector(len);
        }
        // NOTE: The below could be faster (and I could get rid of all of the
        // "setElement"s if I wanted to write two versions of this method.  As
        // it's likely to be infrequently called, I don't want to increase code
        // complexity for a minimal gain.
        int idx = 0;
        for (int i = 0; i < n; ++i)
        {
            if ((idx < locs.length) && (locs[idx] == i))
            {
                ret.setElement(i, vals[idx]);
                ++idx;
            }
            else
            {
                ret.setElement(i, 0);
            }
        }
        for (int i = 0; i < other.elements().length; ++i)
        {
            ret.setElement(n + i, other.elements()[i]);
        }

        return ret;
    }

    @Override
    public final Vector stack(SparseVector other)
    {
        compress();
        other.compress();
        int len = n + other.n;
        int nnz = numNonZero() + other.numNonZero();
        SparseVector ret = new SparseVector(len);
        ret.vals = new double[nnz];
        ret.locs = new int[nnz];
        int idx = 0;
        for (int i = 0; i < locs.length; ++i)
        {
            ret.vals[idx] = vals[i];
            ret.locs[idx] = locs[i];
            ++idx;
        }
        for (int i = 0; i < other.locs.length; ++i)
        {
            ret.vals[idx] = other.vals[i];
            ret.locs[idx] = other.locs[i] + n;
            ++idx;
        }

        return ret;
    }

    @Override
    public final double dotProduct(SparseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        other.compress();
        double ret = 0;
        int otheridx = 0;
        final int thisLength = this.locs.length;
        final int otherLength = other.locs.length;
        for (int i = 0; i < thisLength && otheridx < otherLength; ++i)
        {
            while (other.locs[otheridx] < locs[i])
            {
                ++otheridx;
                if (otheridx >= otherLength)
                {
                    return ret;
                }
            }
            if (other.locs[otheridx] == locs[i])
            {
                ret += vals[i] * other.vals[otheridx];
                ++otheridx;
            }
        }

        return ret;
    }

    @Override
    public final double dotProduct(DenseVector other)
    {
        this.assertSameDimensionality(other);
        compress();
        double ret = 0;
        for (int i = 0; i < locs.length; ++i)
        {
            ret += vals[i] * other.elements()[locs[i]];
        }

        return ret;
    }

// TODO: This iterator should only be over the sparse entries.
    @Override
    final public Iterator<VectorEntry> iterator()
    {
        return new VectorIterator(this);
    }

    @Override
    final public int getDimensionality()
    {
        return n;
    }

    /**
     * Helper that checks that i is within the bounds of this array. Throws an
     * ArrayIndexOutOfBoundsException if not in bounds
     *
     * @param i The index to check
     * @throws ArrayIndexOutOfBoundsException if i not in bounds
     */
    private void checkBounds(int i)
    {
        if ((i < 0) || (i >= n))
        {
            throw new ArrayIndexOutOfBoundsException("Input index " + i
                + " is out of bounds for vectors of length " + n);
        }
    }

    @Override
    public double get(int index)
    {
        return getElement(index);
    }

    @Override
    final public double getElement(int index)
    {
        checkBounds(index);
        if (isCompressed())
        {
            int low = 0;
            int high = locs.length - 1;
            while (low <= high)
            {
                int mid = (int) Math.round((low + high) * .5);
                if (locs[mid] == index)
                {
                    return vals[mid];
                }
                else if (locs[mid] < index)
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
            Double v = elements.get(index);
            return (v == null) ? 0 : v.doubleValue();
        }
    }

    @Override
    public void set(int index,
        double value)
    {
        setElement(index, value);
    }

    @Override
    final public void setElement(int index,
        double value)
    {
        decompress();
        checkBounds(index);
        if (value != 0)
        {
            elements.put(index, value);
        }
        else if (elements.containsKey(index))
        {
            elements.remove(index);
        }
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
        if (minIndex < 0 || minIndex > maxIndex || maxIndex > n)
        {
            throw new ArrayIndexOutOfBoundsException("Input bounds for sub-"
                + "vector [" + minIndex + ", " + maxIndex
                + "] is not within supported bounds [0, " + n + ")");
        }
        compress();
        SparseVector ret = new SparseVector(maxIndex - minIndex + 1);
        for (int i = 0; i < locs.length; ++i)
        {
            if ((locs[i] >= minIndex) && (locs[i] <= maxIndex))
            {
                ret.elements.put(locs[i] - minIndex, vals[i]);
            }
        }

        return ret;
    }

    /**
     * Package-private helper that returns the compressed values. NOTE: If this
     * isn't compressed before this method is called, this method returns null
     * (it does not ensure this is compressed for optimization).
     *
     * @return the compressed values
     */
    final double[] getVals()
    {
        return vals;
    }

    /**
     * Package-private helper that returns the compressed locations. NOTE: If
     * this isn't compressed before this method is called, this method returns
     * null (it does not ensure this is compressed for optimization).
     *
     * @return the compressed locations
     */
    final int[] getLocs()
    {
        return locs;
    }

    /**
     * Package-private helper that tells how many non-zero entries are in this
     * sparse vector
     *
     * @return the number of non-zero entries in this sparse vector
     */
    final int numNonZero()
    {
        if (isCompressed())
        {
            int nnz = 0;
            for (double v : vals)
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
    final public Vector scale(double d)
    {
        compress();
        SparseVector ret = new SparseVector(this);
        for (int i = 0; i < ret.vals.length; ++i)
        {
            ret.vals[i] *= d;
        }

        return ret;
    }

    /**
     * Package-private helper that clears the contents of this vector
     */
    final void clear()
    {
        if (isCompressed())
        {
            vals = null;
            locs = null;
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
        return SparseVectorFactoryOptimized.INSTANCE;
    }

    @Override
    public double sum()
    {
        this.compress();
        double result = 0.0;
        for (final double value : this.vals)
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
        for (final double value : this.vals)
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
        for (final double value : this.vals)
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
        this.compress();;
        return this.vals.length;
    }

}

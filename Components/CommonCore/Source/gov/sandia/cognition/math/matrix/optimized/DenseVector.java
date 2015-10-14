
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorEntry;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Our dense vector implementation. Rather straightforward: stores all of the
 * data in an in-order array.
 *
 * @author Jeremy D. Wendt
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

    /**
     * @see Vector#clone()
     */
    @Override
    final public Vector clone()
    {
        return new DenseVector(this);
    }

    /**
     * @see
     * BaseVector#plusEquals(gov.sandia.cognition.math.matrix.optimized.DenseVector)
     */
    @Override
    void _scaledPlusEquals(DenseVector other,
        double scaleFactor)
    {
        for (int i = 0; i < vec.length; ++i)
        {
            vec[i] += other.vec[i] * scaleFactor;
        }
    }

    /**
     * @see
     * BaseVector#plusEquals(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     */
    @Override
    void _scaledPlusEquals(SparseVector other,
        double scaleFactor)
    {
        other.compress();
        int[] locs = other.getLocs();
        double[] vals = other.getVals();
        for (int i = 0; i < locs.length; ++i)
        {
            vec[locs[i]] += vals[i] * scaleFactor;
        }
    }

    /**
     * @see
     * BaseVector#plusEquals(gov.sandia.cognition.math.matrix.optimized.DenseVector)
     */
    @Override
    final void _plusEquals(DenseVector other)
    {
        for (int i = 0; i < vec.length; ++i)
        {
            vec[i] += other.vec[i];
        }
    }

    /**
     * @see
     * BaseVector#plusEquals(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     */
    @Override
    final void _plusEquals(SparseVector other)
    {
        other.compress();
        int[] locs = other.getLocs();
        double[] vals = other.getVals();
        for (int i = 0; i < locs.length; ++i)
        {
            vec[locs[i]] += vals[i];
        }
    }

    /**
     * @see
     * BaseVector#minusEquals(gov.sandia.cognition.math.matrix.optimized.DenseVector)
     */
    @Override
    final void _minusEquals(DenseVector other)
    {
        for (int i = 0; i < vec.length; ++i)
        {
            vec[i] -= other.vec[i];
        }
    }

    /**
     * @see
     * BaseVector#minusEquals(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     */
    @Override
    final void _minusEquals(SparseVector other)
    {
        other.compress();
        int[] locs = other.getLocs();
        double[] vals = other.getVals();
        for (int i = 0; i < locs.length; ++i)
        {
            vec[locs[i]] -= vals[i];
        }
    }

    /**
     * @see
     * BaseVector#dotTimesEquals(gov.sandia.cognition.math.matrix.optimized.DenseVector)
     */
    @Override
    final void _dotTimesEquals(DenseVector other)
    {
        for (int i = 0; i < vec.length; ++i)
        {
            vec[i] *= other.vec[i];
        }
    }

    /**
     * @see
     * BaseVector#dotTimesEquals(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     */
    @Override
    final void _dotTimesEquals(SparseVector other)
    {
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

    /**
     * @see
     * BaseVector#euclideanDistanceSquared(gov.sandia.cognition.math.matrix.optimized.DenseVector)
     */
    @Override
    final double _euclideanDistanceSquared(DenseVector other)
    {
        double dist = 0.0;
        double tmp;
        for (int i = 0; i < vec.length; ++i)
        {
            tmp = (vec[i] - other.vec[i]);
            dist += tmp * tmp;
        }

        return dist;
    }

    /**
     * @see
     * BaseVector#euclideanDistanceSquared(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     */
    @Override
    final double _euclideanDistanceSquared(SparseVector other)
    {
        return other._euclideanDistanceSquared(this);
    }

    /**
     * @see
     * BaseVector#outerProduct(gov.sandia.cognition.math.matrix.optimized.DenseVector)
     */
    @Override
    final Matrix _outerProduct(DenseVector other)
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

    /**
     * @see
     * BaseVector#outerProduct(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     */
    @Override
    final Matrix _outerProduct(SparseVector other)
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
            ret._setRow(i, row);
        }

        return ret;
    }

    /**
     * @see
     * BaseVector#stack(gov.sandia.cognition.math.matrix.optimized.DenseVector)
     */
    @Override
    final Vector _stack(DenseVector other)
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

    /**
     * @see
     * BaseVector#stack(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     */
    @Override
    final Vector _stack(SparseVector other)
    {
        Vector ret;
        int len = vec.length + other.getDimensionality();
        int nnz = numNonZero() + other.numNonZero();
        if (nnz > Constants.SPARSE_TO_DENSE_THRESHOLD * len)
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

    /**
     * @see
     * BaseVector#dotProduct(gov.sandia.cognition.math.matrix.optimized.DenseVector)
     */
    @Override
    final double _dotProduct(DenseVector other)
    {
        double ret = 0;
        for (int i = 0; i < vec.length; ++i)
        {
            ret += vec[i] * other.vec[i];
        }

        return ret;
    }

    /**
     * @see
     * BaseVector#dotProduct(gov.sandia.cognition.math.matrix.optimized.SparseVector)
     */
    @Override
    final double _dotProduct(SparseVector other)
    {
        return other._dotProduct(this);
    }

    /**
     * @see Vector#iterator()
     */
    @Override
    final public Iterator<VectorEntry> iterator()
    {
        return new VectorIterator(this);
    }

    /**
     * @see Vector#getDimensionality()
     */
    @Override
    final public int getDimensionality()
    {
        return vec.length;
    }

    /**
     * @see Vector#get(int)
     */
    @Override
    public double get(int index)
    {
        return getElement(index);
    }

    /**
     * @see Vector#getElement(int)
     */
    @Override
    final public double getElement(int index)
    {
        return vec[index];
    }

    /**
     * @see Vector#setElement(int, double)
     */
    @Override
    public void set(int index,
        double value)
    {
        setElement(index, value);
    }

    /**
     * @see Vector#setElement(int, double)
     */
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
    final double[] elements()
    {
        return vec;
    }

    /**
     * @see Vector#subVector(int, int)
     * @throws NegativeArraySizeException if the input bounds are in the wrong
     * order
     * @throws ArrayIndexOutOfBoundsException if the input subvector indices are
     * less than 0 or greater-than-or-equal-to the dimensionality of this.
     */
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

    /**
     * @see Vector#scale(double)
     */
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

    /**
     * @see Vector#dotTimes(gov.sandia.cognition.math.Vector)
     */
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
    final public int numNonZero()
    {
        int nnz = 0;
        for (double d : vec)
        {
            nnz += (d == 0) ? 0 : 1;
        }

        return nnz;
    }

    /**
     * @see Vector#isSparse()
     */
    @Override
    public boolean isSparse()
    {
        return false;
    }

}

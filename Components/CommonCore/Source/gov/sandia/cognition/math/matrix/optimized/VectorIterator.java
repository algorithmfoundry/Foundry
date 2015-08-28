
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.VectorEntry;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Small helper class that iterates over all values from this vector
 * 
 * @author Jeremy D. Wendt
 */
public class VectorIterator
    implements Iterator<VectorEntry>
{

    /**
     * The vector to iterate over
     */
    private BaseVector v;

    /**
     * The index of the next value to return
     */
    private int curIdx;

    /**
     * Initialize to iterate over v
     *
     * @param v The vector to iterate over
     */
    VectorIterator(final BaseVector v)
    {
        this.v = v;
        this.curIdx = 0;
    }

    /**
     * @see Iterator#hasNext()
     */
    @Override
    final public boolean hasNext()
    {
        return curIdx < v.getDimensionality();
    }

    /**
     * @see Iterator#next()
     */
    @Override
    final public VectorEntry next()
    {
        if (!hasNext())
        {
            throw new NoSuchElementException("Iterator has exceeded the "
                + "bounds of the vector");
        }
        VectorEntry ret = new OptVecEntry(curIdx, v);
        ++curIdx;

        return ret;
    }

    /**
     * Throws UnsupportedOperationException because you can't remove elements
     * from a DenseVector.
     */
    @Override
    final public void remove()
    {
        throw new UnsupportedOperationException(
            "Cannot remove eleemnts from a DenseVector.");
    }

}

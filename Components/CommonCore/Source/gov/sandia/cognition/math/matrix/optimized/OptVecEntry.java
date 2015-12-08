
package gov.sandia.cognition.math.matrix.optimized;

import gov.sandia.cognition.math.matrix.VectorEntry;

/**
 * Package private class that implements the VectorEntry interface for vector
 * iterators.
 * 
 * @author Jeremy D. Wendt
 */
class OptVecEntry
    implements VectorEntry
{

    /**
     * The index into the vector
     */
    private int index;

    /**
     * The vector being iterated over
     */
    private BaseVector v;

    /**
     * Unsupported private null constructor.
     *
     * @throws UnsupportedOperationException because it should never be called
     */
    private OptVecEntry()
    {
        throw new UnsupportedOperationException("This constructor should never "
            + "be called.");
    }

    /**
     * Creates a new optimized vector entry.
     *
     * @param index The index to the vector
     * @param v The vector to index
     */
    OptVecEntry(int index,
        BaseVector v)
    {
        this.index = index;
        this.v = v;
    }

    @Override
    final public int getIndex()
    {
        return index;
    }

    @Override
    final public void setIndex(int index)
    {
        this.index = index;
    }

    @Override
    final public double getValue()
    {
        return v.getElement(index);
    }

    @Override
    final public void setValue(double value)
    {
        this.v.setElement(index, value);
    }

}

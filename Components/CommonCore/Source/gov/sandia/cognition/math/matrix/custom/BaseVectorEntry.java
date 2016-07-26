/*
 * File:                OptVecEntry.java
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

import gov.sandia.cognition.math.matrix.VectorEntry;

/**
 * Package private class that implements the VectorEntry interface for vector
 * iterators.
 * 
 * @author Jeremy D. Wendt
 * @since   3.4.3
 */
class BaseVectorEntry
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
    private BaseVectorEntry()
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
    BaseVectorEntry(int index,
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

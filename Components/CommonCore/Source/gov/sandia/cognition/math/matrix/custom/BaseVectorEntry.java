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
 * @since 4.0.0
 */
class BaseVectorEntry
    implements VectorEntry
{

    /**
     * The vector being iterated over
     */
    private BaseVector vector;

    /**
     * The index into the vector
     */
    private int index;

    /**
     * Unsupported private empty constructor.
     *
     * @throws UnsupportedOperationException because it should never be called
     */
    private BaseVectorEntry()
    {
        throw new UnsupportedOperationException(
            "This constructor should never be called.");
    }

    /**
     * Creates a new optimized vector entry.
     *
     * @param index The index to the vector
     * @param vector The vector to index
     */
    BaseVectorEntry(
        final int index,
        final BaseVector vector)
    {
        super();
        
        this.index = index;
        this.vector = vector;
    }

    @Override
    final public int getIndex()
    {
        return this.index;
    }

    @Override
    final public void setIndex(
        final int index)
    {
        this.index = index;
    }

    @Override
    final public double getValue()
    {
        return vector.get(this.index);
    }

    @Override
    final public void setValue(
        final double value)
    {
        this.vector.setElement(this.index, value);
    }

}

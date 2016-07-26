/*
 * File:                VectorIterator.java
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
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Small helper class that iterates over all values from this vector
 * 
 * @author Jeremy D. Wendt
 * @since   3.4.3
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

    @Override
    final public boolean hasNext()
    {
        return curIdx < v.getDimensionality();
    }

    @Override
    final public VectorEntry next()
    {
        if (!hasNext())
        {
            throw new NoSuchElementException("Iterator has exceeded the "
                + "bounds of the vector");
        }
        VectorEntry ret = new BaseVectorEntry(curIdx, v);
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

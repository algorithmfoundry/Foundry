/*
 * File:                PrimitiveVector.java
 * Authors:             Jeremy D. Wendt
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 17, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.collection;

/**
 * An abstract base class for memory-dense vectors of primitive types that
 * permit adding new values, altering elements, etc. Subclass for "int",
 * "double", and other base types.
 */
abstract class PrimitiveArrayList
    implements java.io.Serializable
{

    /**
     *
     */
    private static final long serialVersionUID = 6137451942764367066L;

    /**
     * The number of used elements in this
     */
    protected int numElements;

    /**
     * The standard starting point for the array length
     */
    protected static final int DEFAULT_SIZE = 100;

    /**
     * Initializes an empty vector, validating the initial allocation size.
     *
     * @param startSize The number of positions to start the storage
     */
    protected PrimitiveArrayList(int startSize)
    {
        checkNewSizePositive(startSize);
        numElements = 0;
    }

    /**
     * Resizes the background array to the specified size. If the new size is
     * larger than the number of elements in the array at present, old values
     * are preserved. If smaller, the array shrinks to the correct size and
     * elements past that point are lost.
     *
     * @param size The new size for the array
     */
    public void reserve(int size)
    {
        checkNewSizePositive(size);
        resize(size);
        if (numElements > size)
        {
            numElements = size;
        }
    }

    /**
     * Resizes the background array to exactly fit the current number of
     * elements.
     */
    public void shrinkToFit()
    {
        resize(length());
    }

    /**
     * Resizes the background array if it is too small. Preserves current
     * values.
     *
     * @param size The new size
     */
    public void increaseTo(int size)
    {
        checkNewSizePositive(size);
        if (size < length())
        {
            throw new IllegalArgumentException(
                "Can't increase size to size smaller than current size");
        }
        resize(size);
    }

    /**
     * Effectively remove elements from the vector. Does not reallocate storage.
     *
     * @param size The new length of the array
     */
    public void decreaseTo(int size)
    {
        checkNewSizePositive(size);
        if (size > numElements)
        {
            throw new IllegalArgumentException(
                "Can't decrease size to size larger than current size");
        }
        numElements = size;
    }

    /**
     * Quickly "clears" the contents (sets numElements to 0). All old data is
     * unavailable.
     */
    public void clear()
    {
        numElements = 0;
    }

    /**
     * Returns the number of elements added to the vector (not the size of the
     * storage)
     *
     * @return the number of elements added to the vector
     */
    public int size()
    {
        return numElements;
    }

    /**
     * Simple helper that increases the size if the current vector is full
     */
    protected void ensureNotFull()
    {
        if (numElements == length())
        {
            resize(length() * 2);
        }
    }

    /**
     * Checks that idx is in the allowed elements of the vector (distinct from
     * the space in elements).
     *
     * @param idx the index into the vector
     * @throws ArrayIndexOutOfBoundsException if idx is outside the allowed
     * bounds
     */
    protected void checkBounds(int idx)
    {
        if ((idx >= numElements) || (idx < 0))
        {
            throw new ArrayIndexOutOfBoundsException("Requested elemeent " + idx
                + " out of bounds [" + 0 + ", " + numElements + ")");
        }
    }

    /**
     * Helper that ensures the input size is positive
     *
     * @param size The new size
     * @throws IllegalArgumentException if size is negative
     */
    protected static void checkNewSizePositive(int size)
    {
        if (size < 0)
        {
            throw new IllegalArgumentException(
                "Unable to change size to negative");
        }
    }

    /**
     * Replace the internal storage with a new buffer. Used primarily to grow
     * the storage when necessary.
     *
     * @param size the number of elements in the new internal array
     */
    protected abstract void resize(int size);

    /**
     * Return the number of elements in the background storage array.
     */
    protected abstract int length();

}

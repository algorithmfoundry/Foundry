/*
 * File:                DoubleVector.java
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

import java.util.Arrays;

/**
 * A memory-dense, base-type double vector that permits adding new elements,
 * altering elements, etc. Allocates a local array of double. Doubles the size
 * as needed and copies the old values into the new, larger array.
 */
public final class DoubleArrayList
    extends PrimitiveArrayList
{

    /**
     *
     */
    private static final long serialVersionUID = 7027037134983162626L;

    /**
     * The elements array. May be ~50% too large for the current data
     */
    private double[] elements;

    /**
     * Initializes an empty vector with a default allocation.
     */
    public DoubleArrayList()
    {
        this(DEFAULT_SIZE);
        elements = new double[DEFAULT_SIZE];
    }

    /**
     * Initializes an empty vector (allocating startSize locations for
     * additions). An accurate estimate of the final size will reduce storage
     * allocations.
     *
     * @param startSize The number of positions to start the storage
     */
    public DoubleArrayList(int startSize)
    {
        super(startSize);
        elements = new double[startSize];
    }

    /**
     * Copy constructor
     *
     * @param copy The vector to make a deep copy of
     */
    public DoubleArrayList(DoubleArrayList copy)
    {
        super(copy.size() + 1);
        elements = new double[copy.size() + 1];
        for (int i = 0; i < copy.size(); ++i)
        {
            add(copy.get(i));
        }
    }

    /**
     * Adds element to the end of the vector (allocating more space as needed
     *
     * @param element The element to add to the end
     */
    public void add(double element)
    {
        ensureNotFull();
        elements[numElements] = element;
        ++numElements;
    }

    /**
     * Returns the element at idx
     *
     * @param idx The index
     * @return the element at idx
     * @throws ArrayIndexOutOfBoundsException if idx is outside the allowed
     * bounds
     */
    public double get(int idx)
    {
        checkBounds(idx);
        return elements[idx];
    }

    /**
     * Sets the element at idx
     *
     * @param idx The index
     * @param val The new value at idx
     * @throws ArrayIndexOutOfBoundsException if idx is outside the allowed
     * bounds
     */
    public void set(int idx,
        double val)
    {
        checkBounds(idx);
        elements[idx] = val;
    }

    /**
     * Adds val to the element at idx
     *
     * @param idx The index
     * @param val The value to add at idx
     * @throws ArrayIndexOutOfBoundsException if idx is outside the allowed
     * bounds
     */
    public void plusEquals(int idx,
        double val)
    {
        checkBounds(idx);
        elements[idx] += val;
    }

    /**
     * Swaps the values stored at p1 and p2. Both must be within the bounds of
     * the elements in the array.
     *
     * @param p1 The first index to swap
     * @param p2 The second index to swap
     * @throws ArrayIndexOutOfBoundsException if either index is outside the
     * allowed bounds
     */
    public void swap(int p1,
        int p2)
    {
        checkBounds(p1);
        checkBounds(p2);
        double tmp = elements[p1];
        elements[p1] = elements[p2];
        elements[p2] = tmp;
    }

    /**
     * Replace the internal storage with a new buffer. Used primarily to grow
     * the storage when necessary.
     *
     * @param size the number of elements in the new internal array
     */
    @Override
    protected void resize(int size)
    {
        elements = Arrays.copyOf(elements, size);
    }

    @Override
    protected int length()
    {
        return elements.length;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 89 * hash + size();
        for (int i = 0; i < size(); ++i)
        {
            hash = 89 * hash + new Double(get(i)).hashCode();
        }
        return hash;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof DoubleArrayList))
        {
            return false;
        }
        DoubleArrayList v = (DoubleArrayList) o;
        if (v.size() != size())
        {
            return false;
        }
        for (int i = 0; i < size(); ++i)
        {
            if (v.get(i) != get(i))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Factory method that initializes a DoubleVector of the input size with
     * zeros in every entry.
     *
     * @param size The number of zeros to put in the returned DoubleVector
     * @return a DoubleVector of the input size with zeroes in every entry
     */
    public static DoubleArrayList zeros(int size)
    {
        DoubleArrayList ret = new DoubleArrayList(size);
        for (int i = 0; i < size; ++i)
        {
            ret.add(0.0);
        }

        return ret;
    }

}

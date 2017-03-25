
/*
 * File:                IntVector.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A memory-dense, base-type int vector that permits adding new elements,
 * altering elements, etc. Allocates a local array of int. Doubles the size as
 * needed and copies the old values into the new, larger array.
 */
public final class IntArrayList
    extends PrimitiveArrayList
{

    /**
     *
     */
    private static final long serialVersionUID = -423369949544940448L;

    /**
     * The elements array. May be ~50% too large for the current data
     */
    private int[] elements;

    /**
     * Initializes an empty vector with a default allocation.
     */
    public IntArrayList()
    {
        this(DEFAULT_SIZE);
        elements = new int[DEFAULT_SIZE];
    }

    /**
     * Initializes an empty vector (allocating startSize locations for
     * additions). An accurate estimate of the final size will reduce storage
     * allocations.
     *
     * @param startSize The number of positions to start the storage
     */
    public IntArrayList(int startSize)
    {
        super(startSize);
        elements = new int[startSize];
    }

    /**
     * Copy constructor
     * @param copy The vector to make a deep copy of
     */
    public IntArrayList(IntArrayList copy)
    {
        super(copy.size() + 1);
        elements = new int[copy.size() + 1];
        for (int i = 0; i < copy.size(); ++i)
        {
            add(copy.get(i));
        }
    }

    /**
     * Adds element to the end of the vector (allocating more space as needed
     *
     * @param element The element to add to the end
     * @return the number of elements in this
     */
    public int add(int element)
    {
        ensureNotFull();
        elements[numElements] = element;
        return numElements++;
    }

    /**
     * Returns the element at idx
     *
     * @param idx The index
     * @return the element at idx
     * @throws ArrayIndexOutOfBoundsException if idx is outside the allowed
     * bounds
     */
    public int get(int idx)
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
        int val)
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
        int val)
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
        int tmp = elements[p1];
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

    /**
     * Returns the number of elements in the vector
     *
     * @return the number of elements in the vector
     */
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
        if (!(o instanceof IntArrayList))
        {
            return false;
        }
        IntArrayList v = (IntArrayList) o;
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
     * Takes the elements of this and alters their order to a random order
     * (using a variant of Fisher-Yates shuffle called Durstenfeld shuffle).
     */
    @PublicationReference(type = PublicationType.WebPage, 
        title = "Fisher-Yates Suffle: The modern algorithm", year = 2016, 
        url = "https://en.wikipedia.org/wiki/Fisher%E2%80%93Yates_shuffle#The_modern_algorithm",
        author = "Wikipedia")
    public void randomizeOrder()
    {
        randomizeOrder(ThreadLocalRandom.current());
    }

    /**
     * Takes the elements of this and alters their order to a random order
     * (using a variant of Fisher-Yates shuffle called Durstenfeld shuffle).
     *
     * @param r The random number generator to be used here
     */
    public void randomizeOrder(Random r)
    {
        for (int i = size() - 1; i > 1; --i)
        {
            int j = r.nextInt(i + 1);
            swap(i, j);
        }
    }

    /**
     * Creates a new instance pre-loaded with values [0 .. max). Note that it
     * does not include max.
     *
     * @param max The number of elements to include in the result
     * @return a new instance pre-loaded with values [0 .. max)
     */
    public static IntArrayList range(int max)
    {
        return range(0, max);
    }

    /**
     * Creates a new instance pre-loaded with values [min .. max). Note that it
     * does not include max.
     *
     * @param min The number to start the range with
     * @param max The number one shy of the maximum value
     * @return a new instance pre-loaded with values [min .. max)
     */
    public static IntArrayList range(int min,
        int max)
    {
        int size = max - min;
        IntArrayList ret = new IntArrayList(size);
        for (int i = min; i < max; ++i)
        {
            ret.add(i);
        }

        return ret;
    }

    /**
     * Factory method that initializes a IntVector of the input size with zeros
     * in every entry.
     *
     * @param size The number of zeros to put in the returned DoubleVector
     * @return a IntVector of the input size with zeroes in every entry
     */
    public static IntArrayList zeros(int size)
    {
        IntArrayList ret = new IntArrayList(size);
        for (int i = 0; i < size; ++i)
        {
            ret.add(0);
        }

        return ret;
    }

}

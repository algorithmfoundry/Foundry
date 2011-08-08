/*
 * File:                FiniteCapacityBuffer.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 24, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.collection;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * A finite capacity buffer backed by a LinkedList.  One can add and remove
 * from the buffer, but the size will never be greater than the capacity.
 * The running times are equal to those of a LinkedList.
 * 
 * We use a LinkedList instead of an ArrayList because when the buffer is full,
 * we push something onto the end of the queue and remove an element from
 * the beginning.  If we used an ArrayList, this would consume a lot of
 * computation and needless memory allocation.  Whereas, a LinkedList simply
 * appends an element and pops one off in constant time.
 *
 * @param <DataType> Type of data contained in the buffer
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-02-08",
    changesNeeded=false,
    comments={
    "Cleaned up minor formatting issues.",
    "Added class documentation about running times and usage of LinkedList."
    }
)
public class FiniteCapacityBuffer<DataType>
    extends AbstractList<DataType>
    implements CloneableSerializable
{
// TODO: Should be implemented using an array instead of a linked list.
// - jdbasil (2009-07-10)

    /**
     * Maximum capacity of the buffer.
     */
    private int capacity;

    /**
     * Underlying data in the buffer
     */
    private LinkedList<DataType> data;

    /**
     * Default constructor with capacity of one.
     */
    public FiniteCapacityBuffer()
    {
        this(1);
    }

    /**
     * Creates a new instance of FiniteCapacityBuffer.
     *
     * @param   capacity
     *      Maximum capacity of the buffer.
     */
    public FiniteCapacityBuffer(
        int capacity)
    {
        this.data = new LinkedList<DataType>();
        this.setCapacity(capacity);
    }

    /**
     * Copy constructor.
     *
     * @param   other
     *      FiniteCapacityBuffer to copy.
     */
    public FiniteCapacityBuffer(
        FiniteCapacityBuffer<DataType> other)
    {
        this(other.getCapacity());
        this.addAll(other.data);
    }

    @Override
    public FiniteCapacityBuffer<DataType> clone()
    {
        try
        {
            @SuppressWarnings(value = "unchecked")
            FiniteCapacityBuffer<DataType> clone =
                (FiniteCapacityBuffer<DataType>) super.clone();
            clone.setCapacity(this.capacity);
            clone.addAll(this.data);
            return clone;
        }
        catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public int size()
    {
        return this.data.size();
    }

    @Override
    public Iterator<DataType> iterator()
    {
        return this.data.iterator();
    }

    /**
     * Appends the element to the end of the buffer, removing excess elements
     * if necessary
     * @param e 
     * DataType to addLast to the buffer
     * @return true if successful, false if unable to add
     */
    @Override
    public boolean add(
        DataType e)
    {
        return this.addLast(e);
    }

    @Override
    public boolean remove(
        Object o)
    {
        return this.data.remove(o);
    }

    @Override
    public DataType remove(
        int index)
    {
        return this.data.remove(index);
    }

    /**
     * Prepend the element to the beginning of the buffer, removing excess
     * elements if necessary
     * @param e 
     * DataType to addFirst to the buffer
     * @return true if successful, false if unable to add
     */
    public boolean addFirst(
        DataType e)
    {
        while (this.size() >= this.getCapacity())
        {
            this.data.removeLast();
        }

        this.data.addFirst(e);
        return true;
    }

    /**
     * Appends the element to the end of the buffer, removing excess elements
     * if necessary
     * @param e 
     * DataType to add to the buffer
     * @return true if successful, false if unable to add
     */
    public boolean addLast(
        DataType e)
    {
        while (this.size() >= this.getCapacity())
        {
            this.data.removeFirst();
        }

        this.data.addLast(e);
        return true;
    }

    /**
     * Returns the first element in the list.
     *
     * @return
     *      The first element in the list.
     * @throws  NoSuchElementException
     *      If the list is empty.
     */
    public DataType getFirst()
    {
        return this.data.getFirst();
    }

    /**
     * Returns the last element in the list.
     *
     * @return
     *      The last element in the list.
     * @throws  NoSuchElementException
     *      If the list is empty.
     */
    public DataType getLast()
    {
        return this.data.getLast();
    }

    /**
     * Returns true if the finite-capacity buffer is full. This means that
     * the number of elements in the buffer is equal to the capacity of the
     * buffer. Adding an element to the full buffer
     *
     * @return
     *      True if the buffer is full.
     */
    public boolean isFull()
    {
        return this.size() >= this.getCapacity();
    }

    /**
     * Gets the capacity of the buffer, which is the maximum number of elements
     * that can be stored in it. Must be greater than zero.
     *
     * @return The maximum capacity of the buffer.
     */
    public int getCapacity()
    {
        return this.capacity;
    }

    /**
     * Sets the capacity of the buffer, which is the maximum number of elements
     * that can be stored in it. Must be greater than zero.
     *
     * @param   capacity
     *      The maximum capacity of the buffer. Must be greater than zero.
     */
    public void setCapacity(
        int capacity)
    {
        if (capacity <= 0)
        {
            throw new IllegalArgumentException(
                "Capacity (" + capacity + ") must be > 0");
        }

        this.capacity = capacity;

// TODO: Should setting the capacity clear out the buffer as well or only
// grow/shrink it to the proper size? - jdbasil (2009-07-10)
        this.data = new LinkedList<DataType>();
    }

    @Override
    public DataType get(
        int index)
    {
        return this.data.get(index);
    }

}

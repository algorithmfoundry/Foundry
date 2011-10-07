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
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * A finite capacity buffer backed by a fixed array.  One can add and remove
 * from the buffer, but the size will never be greater than the capacity.
 * The running times are constant, except for removing in the middle
 * of the list, which may require a relatively expensive copy/shift.
 * This is essentially a circular array-back list.
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

    /**
     * Underlying data in the buffer
     */
    private Object[] data;

    /**
     * Location of element 0
     */
    private int head;

    /**
     * Number of items in the list
     */
    private int size;

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
    @SuppressWarnings("unchecked")
    public FiniteCapacityBuffer(
        int capacity)
    {
        this.data = new Object[ 0 ];
        this.head = 0;
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
        this.addAll(other);
    }

    @Override
    public FiniteCapacityBuffer<DataType> clone()
    {
        try
        {
            @SuppressWarnings(value = "unchecked")
            FiniteCapacityBuffer<DataType> clone =
                (FiniteCapacityBuffer<DataType>) super.clone();
            clone.data = ObjectUtil.cloneSmartArrayAndElements(this.data);
            return clone;
        }
        catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public int size()
    {
        return this.size;
    }

    @Override
    public Iterator<DataType> iterator()
    {
        return new InternalIterator();
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

        DataType retval = null;
        for( int i = 0; i < this.size; i++ )
        {
            Object vi = this.get(i);
            if( vi.equals(o) )
            {
                retval = this.remove(i);
                break;
            }
        }

        return retval != null;
    }

    @Override
    public DataType remove(
        final int index)
    {
        ArgumentChecker.assertIsInRangeInclusive("index", index, 0, this.size-1);
        final int pos = this.convert(index);

        @SuppressWarnings("unchecked")
        DataType retval = (DataType) this.data[pos];

        this.data[pos] = null;

        // We just removed the first thing in the list, so just
        // move the head forward
        if( index == 0 )
        {
            this.head = this.convert(1);
        }

        // If you removed the final thing in the list, then there's nothing
        // left to do
        else if( index == this.size-1 )
        {

        }

        // You removed something in the middle of the list, so we need to
        // move things around
        else
        {

            // If the list wraps around, then we need to recopy the entire thing
            final int tail = this.convert(this.size-1);

            // If the tail is "above" the removed element, then just copy
            // from that element onward
            if( pos < tail )
            {
                System.arraycopy( this.data, pos+1, this.data, pos, tail-pos );
            }

            // Uh-oh... the pos is "above" the tail, which means we're in a
            // wrapped around state... this is harder.
            else
            {
                for( int i = index; i < this.size-1; i++ )
                {
                    this.set(i, this.get(i+1) );
                }
            }

        }

        this.size--;

        return retval;
    }

    /**
     * Converts the given index from zero-based world to circular world
     * @param index
     * Index to convert to circular world
     * @return
     * Circular world index
     */
    protected int convert(
        int index )
    {
        final int capacity = this.getCapacity();
        int pos = (index+this.head) % this.getCapacity();
        while( pos < 0 )
        {
            pos += capacity;
        }
        return pos;
    }

    @Override
    public void clear()
    {
        Arrays.fill( this.data, null );
        this.size = 0;
        this.head = 0;
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

        // Ensure we've got at least one free slot
        final int capacity = this.getCapacity();
        if( this.size >= capacity )
        {
            this.size = capacity-1;
        }

        // We should have at least
        this.head = this.convert(-1);
        this.data[this.head] = e;
        this.size++;
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
        // Ensure we've got at least one free slot
        final int capacity = this.getCapacity();
        if( this.size >= capacity )
        {
            this.head = this.convert(1);
            this.size = capacity-1;
        }
        final int tail = this.convert(this.size);
        this.data[tail] = e;
        this.size++;
        return true;
    }

    /**
     * Returns the first element in the list.
     *
     * @return
     *      The first element in the list.
     */
    public DataType getFirst()
    {
        return this.get(0);
    }

    /**
     * Returns the last element in the list.
     *
     * @return
     *      The last element in the list.
     */
    public DataType getLast()
    {
        return this.get(this.size-1);
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
        return this.data.length;
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
        ArgumentChecker.assertIsPositive("capacity", capacity);

        Object[] newData = new Object[ capacity ];
        if( this.data != null )
        {
            // Copy over all the old elements if we're growing the buffer
            int max = Math.min( this.size, capacity );
            for( int i = 0; i < max; i++ )
            {
                // I'm sure there's a clever way to use System.arraycopy here,
                // but it doesn't seem worth the effort
                newData[i] = this.get(i);
            }
            this.size = max;
        }
        else
        {
            this.size = 0;
        }

        this.data = newData;
        this.head = 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataType get(
        int index)
    {
        ArgumentChecker.assertIsInRangeInclusive("index", index, 0, this.size-1);
        return (DataType) this.data[this.convert(index)];
    }

    @Override
    public DataType set(
        int index,
        DataType element)
    {
        ArgumentChecker.assertIsInRangeInclusive("index", index, 0, this.size-1);
        int pos = this.convert(index);
        @SuppressWarnings("unchecked")
        DataType oldValue = (DataType) this.data[pos];

        this.data[pos] = element;
        return oldValue;
    }

    /**
     * Iterator for FiniteCapacityBuffer
     */
    protected class InternalIterator
        implements ListIterator<DataType>
    {

        /**
         * Index of the last value retrieved
         */
        private int index;

        /**
         * Default constructor
         */
        public InternalIterator()
        {
            this.index = -1;
        }

        @Override
        public boolean hasNext()
        {
            return this.index < size-1;
        }

        @Override
        public DataType next()
        {
            this.index++;
            return get(this.index);
        }

        @Override
        public void remove()
        {
            FiniteCapacityBuffer.this.remove(this.index);
            this.index--;
        }

        @Override
        public boolean hasPrevious()
        {
            return (this.index > 0) && (size > 0);
        }

        @Override
        public DataType previous()
        {
            return get(this.index);
        }

        @Override
        public int nextIndex()
        {
            return this.index+1;
        }

        @Override
        public int previousIndex()
        {
            return this.index;
        }

        @Override
        public void set(
            DataType o)
        {
            FiniteCapacityBuffer.this.set(this.index+1, o);
        }

        @Override
        public void add(
            DataType o)
        {
            FiniteCapacityBuffer.this.add(this.index, o);
        }

    }

}

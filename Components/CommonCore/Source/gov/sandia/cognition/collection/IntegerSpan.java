/*
 * File:                IntegerSpan.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 23, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.collection;

import gov.sandia.cognition.util.CloneableSerializable;
import java.util.AbstractSet;
import java.util.Iterator;

/**
 * An Iterable that starts at a given Integer and goes until another, inclusive.
 * For example, if I create an IntegerSpan foo = new IntegerSpan(8,9),
 * then the for each loop will create Integers of 8 then 9.  This is useful to
 * describe the bounds on a function, for example, without having to create
 * a List or Array that allocates memory.
 * 
 * @author Kevin R. Dixon
 * @since 2.0
 */
public class IntegerSpan
    extends AbstractSet<Integer>
    implements CloneableSerializable
{

    /**
     * Starting index, inclusive
     */
    private int minValue;

    /**
     * Stopping index, inclusive
     */
    private int maxValue;

    /**
     * Creates a new instance of IntegerSpan
     * 
     * @param minValue
     * Starting index, inclusive
     * @param maxValue
     * Stopping index, inclusive
     */
    public IntegerSpan(
        final int minValue,
        final int maxValue )
    {
        if (minValue > maxValue)
        {
            throw new IllegalArgumentException( "minValue must be < maxValue" );
        }
        this.setMinValue( minValue );
        this.setMaxValue( maxValue );
    }

    @Override
    public IntegerSpan clone()
    {
        try
        {
            return (IntegerSpan) super.clone();
        }
        catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Getter for minValue
     * @return
     * Starting index, inclusive
     */
    public int getMinValue()
    {
        return this.minValue;
    }

    /**
     * Setter for minValue
     * @param minValue
     * Starting index, inclusive
     */
    protected void setMinValue(
        final int minValue )
    {
        this.minValue = minValue;
    }

    /**
     * Getter for maxValue
     * @return
     * Stopping index, inclusive
     */
    public int getMaxValue()
    {
        return this.maxValue;
    }

    /**
     * Setter for maxValue
     * @param maxValue
     * Stopping index, inclusive
     */
    protected void setMaxValue(
        final int maxValue )
    {
        this.maxValue = maxValue;
    }

    @Override
    public Iterator<Integer> iterator()
    {
        return new IntegerIterator();
    }

    @Override
    public int size()
    {
        return this.maxValue - this.minValue + 1;
    }

    /**
     * Determines if the given value is within the inclusive bounds
     * @param value
     * Value to consider
     * @return
     * True if value is within the inclusive minValue/maxValue bounds
     */
    public boolean contains(
        final int value )
    {
        return (this.minValue <= value) && (value <= this.maxValue);
    }

    @Override
    public boolean contains(
        final Object o )
    {
        if (o instanceof Integer)
        {
            return this.contains( ((Integer) o).intValue() );
        }
        return false;
    }

    /**
     * Iterator for an IntegerSpan
     */
    private class IntegerIterator
        implements Iterator<Integer>
    {

        /**
         * Current index of the iterator
         */
        private int currentIndex;

        /**
         * Default constructor
         */
        public IntegerIterator()
        {
            this.currentIndex = IntegerSpan.this.getMinValue();
        }

        @Override
        public boolean hasNext()
        {
            return (this.currentIndex <= IntegerSpan.this.getMaxValue());
        }

        @Override
        public Integer next()
        {
            int previousIndex = this.currentIndex;
            this.currentIndex++;
            return previousIndex;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException( "Cannot remove." );
        }

    }

}

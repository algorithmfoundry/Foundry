/*
 * File:                DynamicArrayMap.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.collection;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.AbstractCollection;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

/**
 * A <code>DynamicArrayList</code> is a class that implements a map from an
 * integer to an Object type on top of an expanding array. It is optimized
 * for use with continuous ranges of integer indices, so it does not do any
 * hashing and instead grows the array to fit any index that is set in it.
 *
 * The keys must be non-negative integers. Passing any negative integer into
 * the map will result in an exception being thrown.
 *
 * The running time of the operations in the class are what would be expected
 * if one were operating on an <code>ArrayList</code> where space is always
 * allocated for the highest key inserted. Access is done in constant time,
 * usually setting will be constant time though if the setting is done beyond
 * the end of the current array it will require addition so it will be time
 * proportional to the size of the array. Iteration over the collection takes
 * time proportional to the capacity of the array.
 *
 * Note that this implementation is not synchronized.
 *
 * @param  <ValueType> The value stored in the map.
 * @author Justin Basilico
 * @since  1.0
 */
@CodeReviews(
    reviews={     
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-08",
            changesNeeded=true,
            comments={
                "I like the added class comment describing the running times.  This may be sufficient.",
                "However, I would still like to see running times on each accessor method. Please review."
            },
            response=@CodeReviewResponse(
                respondent="Justin Basilico",
                date="2008-02-18",
                moreChangesNeeded=false,
                comments="Added running times to each accessor method."
            )
        )
        ,
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2006-07-18",
            changesNeeded=true,
            comments={
                "Non-standard use of direct-member access, instead of getters and setters. Please review.",
                "Please add operation running times in class comments like Java does for its LinkedList, HashMap, etc.",
                "In other news, I fixed some minor spacing and made some logical statements use parentheses to make their precedence clear."
            },
            response=@CodeReviewResponse(
                respondent="Justin Basilico",
                date="2006-09-22",
                moreChangesNeeded=false,
                comments="Added comment regarding lack of getters and setters"
            )
        )
    }
)
public class DynamicArrayMap<ValueType>
    extends AbstractMap<Integer, ValueType>
    implements CloneableSerializable, RandomAccess
{
    // Note: This class makes use of direct access to member variables instead
    // of using getters and setters because it is expected to be a
    // high-performance class that is called a lot.

    /** The default initial capacity is {@value}. */
    public static final int DEFAULT_INITIAL_CAPACITY = 10;

    /**
     * The array underneath the mapping. Null values indicate an unassigned
     *  key.
     */
    private ValueType[] array;

    /**
     * The number of non-null values in the array.
     */
    private int numValues;

    /**
     * Creates a new instance of DynamicArrayMap. The default initial capacity
     * is 10.
     */
    public DynamicArrayMap()
    {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates a new instance of DynamicArrayMap with the given initial
     * capacity.
     *
     * @param  initialCapacity The initial capacity of the underlying array. It
     *         must be positive.
     */
    @SuppressWarnings("unchecked")
    public DynamicArrayMap(
        final int initialCapacity)
    {
        super();

        if (initialCapacity <= 0)
        {
            throw new IllegalArgumentException(
                "The initialCapacity must be positive.");
        }

        this.array = (ValueType[]) new Object[initialCapacity];
        this.numValues = 0;
    }

    /**
     * Creates a new instance of DynamicArrayMap using the given mapping to copy
     * into this map.
     *
     * @param  other The other mapping to do a shallow copy of.
     */
    @SuppressWarnings("unchecked")
    public DynamicArrayMap(
        final DynamicArrayMap<? extends ValueType> other)
    {
        super();

        this.array = (ValueType[]) new Object[other.array.length];
        System.arraycopy(other.array, 0, this.array, 0, this.array.length);
        this.numValues = other.numValues;
    }

    /**
     * {@inheritDoc} Runs in O(n).
     *
     * @return {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public DynamicArrayMap<ValueType> clone()
    {
        DynamicArrayMap<ValueType> clone = null;
        try
        {
            clone = (DynamicArrayMap<ValueType>) super.clone();
        }
        catch (CloneNotSupportedException ex)
        {
            throw new RuntimeException(ex);
        }

        final int num = this.array.length;
        clone.array = (ValueType[]) new Object[num];
        System.arraycopy(this.array, 0, clone.array, 0, num);
        return clone;

    }

    /**
     * {@inheritDoc}
     *
     * Runs in O(n).
     */
    @Override
    public void clear()
    {
        for (int i = 0; i < this.array.length; i++)
        {
            this.array[i] = null;
        }

        this.numValues = 0;
    }

    /**
     * {@inheritDoc}
     * Runs in O(1).
     * 
     * @param key {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean containsKey(
        final Object key)
    {
        // Convert the key to an int.
        return (key != null 
            && key instanceof Integer
            && this.containsKey((int) ((Integer) key)));
    }

    /**
     * Returns true if this is a valid key in the mapping. Runs in O(1).
     * 
     * @return True if this is a valid key in the mapping.
     * @param key integer to search for in the mapping
     */
    public boolean containsKey(
        final int key)
    {
        return (this.get(key) != null);
    }

    /**
     * {@inheritDoc}
     * Runs in O(n).
     *
     * @param value {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean containsValue(
        final Object value)
    {
        if (value == null)
        {
            // We never contain null.
            return false;
        }

        // Go through the values and see if there is a matching one.
        for (int i = 0; i < this.array.length; i++)
        {
            final ValueType entry = this.array[i];
            if (entry != null && value.equals(entry))
            {
                return true;
            }
        }

        // No matching value found.
        return false;
    }

    /**
     * Ensures that the capacity of the underlying array can hold the given
     * minimum capacity. This means that a key up to the given value can be
     * provided without the array being reallocated. Runs in O(n).
     *
     * @param  minCapacity The minimum capacity to ensure.
     */
    public void ensureCapacity(
        final int minCapacity)
    {
        final int currentCapacity = this.array.length;
        if (currentCapacity < minCapacity)
        {
            // This formula is used because it is used in the ArrayList class
            // in java.util.
            int newCapacity = (currentCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity)
            {
                // The requested minimum is larger than our expected expansion.
                newCapacity = minCapacity;
            }

            // Create the new array from the new capacity and copy the values.
            @SuppressWarnings("unchecked")
            final ValueType[] newArray = (ValueType[]) new Object[newCapacity];
            System.arraycopy(this.array, 0, newArray, 0, currentCapacity);
            this.array = newArray;
        }
        // else - We already have that capacity.
    }

    public Set<Map.Entry<Integer, ValueType>> entrySet()
    {
        return new EntrySet();
    }

    /**
     * {@inheritDoc}  Runs in O(1).
     *
     * @param  key {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException If the key is null.
     */
    @Override
    public ValueType get(
        final Object key)
    {
        if (key == null)
        {
            throw new NullPointerException("The key cannot be null");
        }
        else if (key instanceof Integer)
        {
            return this.get((int) ((Integer) key));
        }
        else
        {
            return null;
        }
    }

    /**
     * Gets the value for the given key. If there is no value, null is returned.
     * Runs in O(1).
     *
     * @param  key The key to look up.
     * @return The value at the given key, if one exists. Otherwise null is
     *         returned.
     */
    public ValueType get(
        final int key)
    {
        // We check for overflow only since the class only accepts non-negative
        // keys.
        if (key >= this.array.length)
        {
            return null;
        }
        else
        {
            return this.array[key];
        }
    }

    /**
     * {@inheritDoc}  Runs in O(1).
     *
     * @return {@inheritDoc}
     */
    @Override
    public boolean isEmpty()
    {
        return this.numValues <= 0;
    }

    @Override
    public Set<Integer> keySet()
    {
        return super.keySet();
    }

    /**
     * {@inheritDoc}  Runs in O(1) if the key is within the range already used,
     * otherwise O(n) to expand the range.
     *
     * @param  key {@inheritDoc}
     * @param  value {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException If the key is null.
     */
    @Override
    public ValueType put(
        final Integer key,
        final ValueType value)
    {
        return this.put((int) key, value);
    }

    /**
     * Puts a value into the mapping. If the value it null, it removes the
     * the entry from the mapping. Normally this operation runs in O(1), however
     * if the value is not null and the given key is not in the current range 
     * then it will be O(n).
     *
     * @param  key The non-negative key.
     * @param  value The new value for the key.
     * @return The old value for the key. Null if no value was associated with
     *         the key.
     */
    public ValueType put(
        final int key,
        final ValueType value)
    {
        // We need to return the old value from this method.
        ValueType oldValue = null;

        if (key < this.array.length)
        {
            // This key is within the length of our array. Just set the
            // value
            oldValue = this.array[key];
            this.array[key] = value;
        }
        else if (value != null)
        {
            // Expand the array.
            this.ensureCapacity(key + 1);

            // Set the value in the array now that it can fit.
            this.array[key] = value;
        }
        else
        {
            // Trying to put null beyond the end of the array, which means
            // we don't have to do anything.
            return null;
        }

        // Update the counter for the number of values.
        final boolean oldNull = (oldValue == null);
        final boolean newNull = (value == null);
        if (oldNull && !newNull)
        {
            // We added a new non-null value.
            this.numValues += 1;
        }
        else if (!oldNull && newNull)
        {
            // We removed a non-null value.
            this.numValues -= 1;
        }
        // else - No change in the count.

        // The result is the old value at the given key.
        return oldValue;
    }

    /**
     * {@inheritDoc}  Runs in O(1).
     *
     * @param  key {@inheritDoc}
     * @return {@inheritDoc}
     * @throws NullPointerException If the key is null.
     */
    @Override
    public ValueType remove(
        final Object key)
    {
        if (key == null)
        {
            throw new NullPointerException("The key cannot be null");
        }
        else if (key instanceof Integer)
        {
            return this.remove((int) ((Integer) key));
        }
        else
        {
            return null;
        }
    }

    /**
     * Removes the value for the given key from the mapping. Runs in O(1).
     *
     * @param  key The key to remove from the mapping.
     * @return The value stored at the given key or null if no value was
     *         associated with the key.
     */
    public ValueType remove(
        final int key)
    {
        if (key < this.array.length)
        {
            // There might be a value for this key
            final ValueType result = this.array[key];
            this.array[key] = null;

            if (result != null)
            {
                // We removed an actual element.
                this.numValues -= 1;
            }

            return result;
        }
        else
        {
            // No value associated with the key.
            return null;
        }
    }

    /**
     * {@inheritDoc}  Runs in O(1).
     *
     * @return {@inheritDoc}
     */
    @Override
    public int size()
    {
        return this.numValues;
    }

    @Override
    public Collection<ValueType> values()
    {
        return new ValuesCollection();
    }

    /**
     * The ValuesIterator class implements an iterator over the values in a
     * DynamicArrayMap.
     */
    private class ValuesIterator
        extends AbstractCloneableSerializable
        implements Iterator<ValueType>
    {

        /** The location of the next value in the iterator. */
        private int cursor;

        /** The next value in the iterator. */
        private ValueType next;

        /**
         * Creates a new instance of ValuesIterator.
         */
        public ValuesIterator()
        {
            super();

            this.cursor = -1;
            this.findNextNonNull();
        }

        /**
         * Moves the cursor to the next non-null value.
         */
        private void findNextNonNull()
        {
            this.next = null;

            while (this.next == null && (this.cursor - 1) < array.length)
            {
                this.cursor++;
                this.next = get(this.cursor);
            }
        }

        public boolean hasNext()
        {
            return this.next != null;
        }

        /**
         * Returns the index of the next non-null value. It will only be a valid
         * index if hasNext() is true.
         *
         * @return The index of the next non-null value.
         */
        public int peekNextIndex()
        {
            return this.cursor;
        }

        public ValueType next()
        {
            final ValueType result = this.next;

            this.findNextNonNull();

            return result;
        }

        /**
         * Not implemented. Throws a UnsupportedOperationException.
         */
        public void remove()
        {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * The ValuesCollection class implements an Collection of the non-null
     * values of a DynamicArrayMap.
     */
    private class ValuesCollection
        extends AbstractCollection<ValueType>
    {
        /**
         * Default constructor.
         */
        private ValuesCollection()
        {
            super();
        }

        @Override
        public boolean equals(
            final Object other)
        {
            if (other == null)
            {
                return false;
            }
            else if (other == this)
            {
                return true;
            }
            else if (other instanceof Collection)
            {
                return this.equals((Collection<?>) other);
            }
            else
            {
                return false;
            }
        }

        /**
         * Determines if this collection is equal to a given one by seeing
         * if all the elements are the same.
         *
         * @param  other The collection to compare to.
         * @return True if the two collections have the same elements.
         */
        public boolean equals(
            final Collection<?> other)
        {
            if (other == null)
            {
                // Other is null, so we are not equal.
                return false;
            }
            else if (this.size() != other.size())
            {
                // Not the same size so not equal.
                return false;
            }

            // We need to iterate over both at the same time to check to
            // see that each element is equal.
            final Iterator<ValueType> thisIt = this.iterator();
            final Iterator<?> otherIt = other.iterator();

            boolean keepGoing = true;
            while (keepGoing)
            {
                final boolean thisHasNext = thisIt.hasNext();
                final boolean otherHasNext = otherIt.hasNext();

                if (thisHasNext != otherHasNext)
                {
                    // This should never happen but one iterator ended before
                    // another.
                    return false;
                }
                else if (thisHasNext)
                {
                    // Compare the two elements.
                    final ValueType thisNext = thisIt.next();
                    final Object otherNext = otherIt.next();

                    if (   (thisNext == null && otherNext == null) 
                        || (thisNext != null && thisNext.equals(otherNext)))
                    {
                        // The elements are equal.
                        keepGoing = true;
                    }
                    else
                    {
                        // The two elements are not equal.
                        return false;
                    }
                }
                else
                {
                    // Both iterators ended.
                    keepGoing = false;
                }
            }

            // All the elements are equal.
            return true;
        }

        public Iterator<ValueType> iterator()
        {
            return new ValuesIterator();
        }

        public int size()
        {
            return DynamicArrayMap.this.size();
        }

        @Override
        public int hashCode()
        {
            // TODO: This is not a good hash-code. It should be implemented
            // based on the hash codes of the elements in the collection
            // - Justin Basilico (2009-07-03)
            final int hash = 7;
            return hash;
        }

    }

    /**
     * The EntrySet class implements an Collection of the non-null
     * Map.Entry objects of a DynamicArrayMap.
     */
    private class EntrySet
        extends AbstractSet<Map.Entry<Integer, ValueType>>
    {
        /**
         * Default constructor.
         */
        private EntrySet()
        {
            super();
        }

        public Iterator<Map.Entry<Integer, ValueType>> iterator()
        {
            return new EntryIterator();
        }

        public int size()
        {
            return DynamicArrayMap.this.size();
        }

    }

    /**
     * The EntryIterator class implements an Iterator of the non-null
     * Map.Entry objects of a DynamicArrayMap.
     */
    private class EntryIterator
        extends AbstractCloneableSerializable
        implements Iterator<Map.Entry<Integer, ValueType>>
    {

        /** We make use of an Iterator over the values to do most of the work.
         */
        private ValuesIterator iterator = null;

        /**
         * Creates a new EntryIterator.
         */
        private EntryIterator()
        {
            super();

            this.iterator = new ValuesIterator();
        }

        public boolean hasNext()
        {
            return this.iterator.hasNext();
        }

        public Map.Entry<Integer, ValueType> next()
        {
            final int index = this.iterator.peekNextIndex();
            final ValueType value = this.iterator.next();

            return new Entry(index, value);
        }

        public void remove()
        {
            this.iterator.remove();
        }

    }

    /**
     * The Entry class implements a Map.Entry for the DynamicArrayMap, used
     * by the EntrySet and EntryIterator.
     */
    private class Entry
        implements Map.Entry<Integer, ValueType>
    {

        /** The index of the entry. */
        private int index;

        /** The value of the entry. */
        private ValueType value;

        /**
         * Creates a new instance of Entry.
         *
         * @param  index The index of the entry.
         * @param  value The value of the entry.
         */
        private Entry(
            final int index,
            final ValueType value)
        {
            super();

            this.index = index;
            this.value = value;
        }

        public Integer getKey()
        {
            return this.index;
        }

        public ValueType getValue()
        {
            return this.value;
        }

        public ValueType setValue(
            final ValueType value)
        {
            final ValueType oldValue = this.value;
            this.value = value;
            DynamicArrayMap.this.put(this.index, value);
            return oldValue;
        }

        @Override
        public boolean equals(
            final Object other)
        {
            return other instanceof DynamicArrayMap.Entry
                && this.equals((DynamicArrayMap.Entry) other);
        }

        /**
         * Returns true if the entries are equal, false otherwise
         * @param other Other Entry to compare against
         * @return
         * True if equal, false otherwise.
         */
        public boolean equals(
            final DynamicArrayMap.Entry other)
        {
            return this.index == other.index 
                && ObjectUtil.equalsSafe(this.value, other.value);
        }
        
        @Override
        public int hashCode()
        {
            // The documentation for Map.Entry says to compute the hash code
            // like this.
            return this.index ^ (this.value == null ? 0 : this.value.hashCode());
        }

    }

}


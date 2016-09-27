/*
 * File:                TwoWayIndex.java
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

package gov.sandia.cognition.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Performs an indexing operation for a set of objects -- objects are assigned
 * an index and the index can be used to map to the objects.
 *
 * @author jdwendt
 *
 * @param <ObjectType> The object type stored in the index
 */
public final class TwoWayIndex<ObjectType>
    implements java.io.Serializable
{

    /**
     *
     */
    private static final long serialVersionUID = 6133143574287552879L;

    /**
     * Stores the index from integer to object
     */
    private final List<ObjectType> indexToValues;

    /**
     * Stores the index from object to integer
     */
    private final Map<ObjectType, Integer> valuesToIndex;

    /**
     * Create the index
     */
    public TwoWayIndex()
    {
        indexToValues = new ArrayList<>();
        valuesToIndex = new HashMap<>();
    }

    /**
     * Create the index, reserving space for a minimum number of objects.
     *
     * @param n The number of spaces to reserve (for speed-up)
     */
    public TwoWayIndex(int n)
    {
        indexToValues = new ArrayList<>(n);
        valuesToIndex = new HashMap<>(n);
    }

    /**
     * Return the number of objects in this index.
     *
     * @return the number of objects in this index.
     */
    public int size()
    {
        return indexToValues.size();
    }

    /**
     * Return if the given object is in this index.
     *
     * @param val the value to check for
     * @return if the given object is in this index.
     */
    public boolean contains(ObjectType val)
    {
        return valuesToIndex.containsKey(val);
    }

    /**
     * Translate from index to object value. Will throw a run-time exception if
     * the index is out of range.
     *
     * @param idx The index to search for
     * @return the object at index
     */
    public ObjectType getValue(int idx)
    {
        return indexToValues.get(idx);
    }

    /**
     * Translate from the object value to its index. Will throw a run-time
     * exception if the object is not stored herein.
     *
     * @param val The value to search for
     * @return the index for an object
     */
    public int getIndex(ObjectType val)
    {
        Integer idx = valuesToIndex.get(val);
        if (idx == null)
        {
            throw new IllegalArgumentException(val + " not stored herein");
        }

        return idx.intValue();
    }

    /**
     * Add an object value and return its index. If the object is already in the
     * set, just return its index. Otherwise, add the object and assign it a new
     * index.
     *
     * @param val The value to add
     * @return the index assigned to val
     */
    public int putValue(ObjectType val)
    {
        // if the value is already there, return its known index
        if (contains(val))
        {
            return getIndex(val);
        }

        // a new object value - determine where to put it
        int n = indexToValues.size(); // number of different objects

        indexToValues.add(val);
        valuesToIndex.put(val, n);

        return n;
    }

    /**
     * Return an unmodifiable collection of the objects in the set.
     *
     * @return an unmodifiable collection of the objects in the set
     */
    public List<ObjectType> getValues()
    {
        return Collections.unmodifiableList(indexToValues);
    }

    /**
     * Empties the index
     */
    public void clear()
    {
        indexToValues.clear();
        valuesToIndex.clear();
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof TwoWayIndex))
        {
            return false;
        }
        TwoWayIndex<?> idx = (TwoWayIndex) o;
        if (idx.size() != size()) {
            return false;
        }
        List<ObjectType> mine = getValues();
        List<?> his = idx.getValues();
        for (int i = 0; i < mine.size(); ++i) {
            ObjectType obj = mine.get(i);
            if (!obj.equals(his.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 89 * hash + size();
        for (ObjectType obj : getValues())
        {
            hash = 89 * hash + obj.hashCode();
        }
        return hash;
    }

}

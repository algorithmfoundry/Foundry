/*
 * File:            DefaultIndexer.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.collection;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A default implementation of the {@link Indexer} interface that simply maps
 * objects to a range from 0 to n-1 in the order they are given. It stores the
 * list of objects plus a mapping of values to indices. This implementation is
 * not synchronized.
 *
 * @param   <ValueType>
 *      The type of the value being indexed. Must have a valid equals and
 *      hashCode implementation.
 * @author  Justin Basilico
 * @version 3.4.0
 * @see     Indexer
 */
public class DefaultIndexer<ValueType>
    extends AbstractCloneableSerializable
    implements Indexer<ValueType>
{

    /** The list of values, which can be accessed by index. */
    protected ArrayList<ValueType> valueList;

    /** The mapping of values to their indices. */
    protected LinkedHashMap<ValueType, Integer> valueToIndexMap;

    /**
     * Creates a new, empty {@link DefaultIndexer}.
     */
    public DefaultIndexer()
    {
        super();

        this.valueList = new ArrayList<ValueType>();
        this.valueToIndexMap = new LinkedHashMap<ValueType, Integer>();
    }

    /**
     * Creates a new, empty {@link DefaultIndexer} with the given initial
     * capacity.
     *
     * @param   initialCapacity
     *      The initial capacity for the indexer. Must be positive.
     */
    public DefaultIndexer(
        final int initialCapacity)
    {
        super();

        this.valueList = new ArrayList<ValueType>(initialCapacity);
        this.valueToIndexMap = new LinkedHashMap<ValueType, Integer>(
            initialCapacity);
    }

    /**
     * Creates a new {@link DefaultIndexer} and adds he given collection of
     * values to it.
     *
     * @param values
     *      The initial set of values to add to the indexer.
     */
    public DefaultIndexer(
        final Collection<? extends ValueType> values)
    {
        this(values.size());

        this.addAll(values);
    }

    @Override
    public DefaultIndexer<ValueType> clone()
    {
        @SuppressWarnings("unchecked")
        final DefaultIndexer<ValueType> clone = (DefaultIndexer<ValueType>)
            super.clone();

        clone.valueList = new ArrayList<ValueType>(this.valueList);
        clone.valueToIndexMap = new LinkedHashMap<ValueType, Integer>(
            this.valueToIndexMap);
        
        return clone;
    }

    @Override
    public Integer add(
        final ValueType value)
    {
        Integer result = this.getIndex(value);
        if (result == null)
        {
            result = Integer.valueOf(this.valueList.size());
            this.valueList.add(value);
            this.valueToIndexMap.put(value, result);
        }
        return result;
    }

    @Override
    public void addAll(
        final Iterable<? extends ValueType> values)
    {
        for (ValueType value : values)
        {
            this.add(value);
        }
    }

    @Override
    public Integer getIndex(
        final ValueType value)
    {
        return this.valueToIndexMap.get(value);
    }

    @Override
    public ValueType getValue(
        final int index)
    {
        return this.valueList.get(index);
    }

    @Override
    public ValueType getValue(
        final Integer index)
    {
        return this.valueList.get(index);
    }

    @Override
    public boolean hasValue(
        final ValueType value)
    {
        return this.valueToIndexMap.containsKey(value);
    }

    @Override
    public boolean hasIndex(
        final int index)
    {
        return index >= 0 && index < this.size();
    }

    @Override
    public boolean hasIndex(
        final Integer index)
    {
        return index != null && this.hasIndex(index.intValue());
    }

    @Override
    public boolean isEmpty()
    {
        return this.valueList.isEmpty();
    }

    @Override
    public int size()
    {
        return this.valueList.size();
    }

    @Override
    public Set<ValueType> valueSet()
    {
        return Collections.unmodifiableSet(this.valueToIndexMap.keySet());
    }

    @Override
    public List<ValueType> valueList()
    {
        return Collections.unmodifiableList(this.valueList);
    }

    @Override
    public Map<ValueType, Integer> asMap()
    {
        return Collections.unmodifiableMap(this.valueToIndexMap);
    }

}

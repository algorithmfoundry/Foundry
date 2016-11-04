/*
 * File:            Indexer.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.collection;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Defines the functionality of a collection that can map between values and
 * integer indices. Indexers are typically used in places where unique objects
 * need to be mapped to some fixed range of indices, such as 0 to n-1. The
 * indexer can be used to bidirectionally map between indices and values.
 *
 * @param <ValueType> The type of value to be indexed.
 * @author Justin Basilico
 * @since 3.3.3
 * @see DefaultIndexer
 */
public interface Indexer<ValueType>
{

    /**
     * Adds a value to the indexer and returns the index assigned to the value.
     * If the value is already in the index, then its existing index will be
     * returned.
     *
     * @param value The value to add.
     * @return The index assigned to the value.
     */
    public Integer add(
        final ValueType value);

    /**
     * Adds all of the given values to the index.
     *
     * @param values The values to add.
     */
    public void addAll(
        final Iterable<? extends ValueType> values);

    /**
     * Gets the index associated with the value.
     *
     * @param value The value to get the index of.
     * @return The index of the given value or null if there is no such index.
     */
    public Integer getIndex(
        final ValueType value);

    /**
     * Gets the value associated with the given index. Will throw an exception
     * if the index is invalid.
     *
     * @param index The index to get.
     * @return The value at the index.
     */
    public ValueType getValue(
        final int index);

    /**
     * Gets the value associated with the given index. Will throw an exception
     * if the index is invalid.
     *
     * @param index The index to get.
     * @return The value at the index.
     */
    public ValueType getValue(
        final Integer index);

    /**
     * Determines if the given value is known to the indexer and has an index.
     *
     * @param value The value.
     * @return True if the value has a valid index; otherwise, false.
     */
    public boolean hasValue(
        final ValueType value);

    /**
     * Determines if the given index is valid for this indexer, which means it
     * has a value associated with it.
     *
     * @param index The index.
     * @return True if the index has a valid value; otherwise, false.
     */
    public boolean hasIndex(
        final int index);

    /**
     * Determines if the given index is valid for this indexer, which means it
     * has a value associated with it.
     *
     * @param index The index.
     * @return True if the index has a valid value; otherwise, false.
     */
    public boolean hasIndex(
        final Integer index);

    /**
     * Returns true if this indexer is empty, which means it has no values.
     *
     * @return True if the indexer is empty, which means it has no values and
     * its size is zero.
     */
    public boolean isEmpty();

    /**
     * Gets the number of items in the index.
     *
     * @return The number of items in the index.
     */
    public int size();

    /**
     * Gets the set of values in the index.
     *
     * @return The set of values.
     */
    public Set<ValueType> valueSet();

    /**
     * Gets the list of values in the index.
     *
     * @return The list of values in the index.
     */
    public List<ValueType> valueList();

    /**
     * Gets mapping of values to indices.
     *
     * @return The mapping of values to indices.
     */
    public Map<ValueType, Integer> asMap();

    /**
     * Clears the contents of this index.
     */
    public void clear();

}

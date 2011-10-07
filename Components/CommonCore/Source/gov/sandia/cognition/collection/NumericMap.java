/*
 * File:                NumericMap.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Incremental Learning Core
 * 
 * Copyright April 27, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * 
 */

package gov.sandia.cognition.collection;

import java.util.Set;

/**
 * An interface for a mapping of keys to numeric values.
 *
 * @param   <KeyType>
 *      The type of the key in the map.
 * @author  Justin Basilico
 * @since   3.3.1
 */
public interface NumericMap<KeyType>
{
    
    /**
     * Returns true if the map is empty.
     *
     * @return
     *      True if the map is empty, which means the size is 0.
     */
    public boolean isEmpty();

    /**
     * Gets the set of unique keys in the map.
     *
     * @return
     *      The set of unique keys in the map.
     */
    public Set<KeyType> keySet();

    /**
     * Determines if this map contains the given key.
     *
     * @param   key
     *      A key.
     * @return
     *      True if the map contains the key; otherwise false.
     */
    public boolean containsKey(
        final KeyType key);

    /**
     * Gets the number of items in the map. This is equal to the number of
     * unique keys.
     *
     * @return
     *      The number of items in the map. Must be positive.
     */
    public int size();

    /**
     * Removes all elements from the map.
     */
    public void clear();

    /**
     * Gets the non-unique key associated with the maximum value in the map.
     * There can be several keys returning the maximum and this method returns
     * one of them. If the map is empty, it returns null.
     *
     * @return
     *      Non-unique key associated with the maximum value in the map.
     */
    public KeyType getMaxValueKey();

    /**
     * Gets a set of all keys associated with the maximum value in the map.
     *
     * @return
     *      Set of all keys associated with the maximum value in the map.
     */
    public Set<KeyType> getMaxValueKeys();

    /**
     * Gets the non-unique key associated with the minimum value in the map.
     * There can be several keys returning the minimum and this method returns
     * one of them. If the map is empty, it returns null.
     *
     * @return
     *      Non-unique key associated with the minimum value in the map.
     */
    public KeyType getMinValueKey();

    /**
     * Gets a set of all keys associated with the minimum value in the map.
     *
     * @return
     *      Set of all keys associated with the minimum value in the map.
     */
    public Set<KeyType> getMinValueKeys();

}

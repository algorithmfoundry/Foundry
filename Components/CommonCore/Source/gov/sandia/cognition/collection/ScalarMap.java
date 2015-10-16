/*
 * File:                ScalarMap.java
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

import java.util.Map;
import java.util.Set;

/**
 * An interface for a mapping of objects to scalar values represented as
 * doubles.
 *
 * @param   <KeyType>
 *      The type of the key in the map.
 * @author  Justin Basilico
 * @since   3.3.1
 */
public interface ScalarMap<KeyType>
    extends NumericMap<KeyType>
{

    /**
     * Gets a {@code java.util.Map} that contains the same data as in this
     * scalar map.
     *
     * @return
     *      The {@code Map} version of this data structure.
     */
    public Map<KeyType, ? extends Number> asMap();

    /**
     * Gets the value associated with a given key. If the key does not exist,
     * then 0.0 is returned.
     *
     * @param   key
     *      A key.
     * @return
     *      The value associated with the key or 0.0 if it does not exist.
     */
    public double get(
        final KeyType key);

    /**
     * Sets the value associated with a given key. In some cases if the value is
     * 0.0, the key may be removed from the map.
     *
     * @param   key
     *      A key.
     * @param   value
     *      The value to associate with the key.
     */
    public void set(
        final KeyType key,
        final double value);

    /**
     * Sets all the given keys to the given value.
     *
     * @param   keys
     *      A list of keys.
     * @param   value
     *      The value to associate with all the given keys.
     */
    public void setAll(
        final Iterable<? extends KeyType> keys,
        final double value);

    /**
     * Sets all the given keys to the given value.
     *
     * @param   other
     *      The other map.
     */
    public void setAll(
        final ScalarMap<? extends KeyType> other );
    
    /**
     * Increments the value associated with the given key by 1.0.
     *
     * @param   key
     *      A key.
     * @return
     *      The new value associated with the key.
     */
    public double increment(
        final KeyType key);

    /**
     * Increments the value associated with the given key by the given amount.
     *
     * @param   key
     *      A key.
     * @param value
     *      The amount to increment the value associated with the given key by.
     * @return
     *      The new value associated with the key.
     */
    public double increment(
        final KeyType key,
        final double value);

    /**
     * Increments the values associated all of the given keys by 1.0.
     *
     * @param   keys
     *      A list of keys.
     */
    public void incrementAll(
        final Iterable<? extends KeyType> keys);

    /**
     * Increments all the keys in this map by the values in the other one.
     *
     * @param   other
     *      The other map.
     */
    public void incrementAll(
        final ScalarMap<? extends KeyType> other);

    /**
     * Decrements the value associated with a given key by 1.0.
     *
     * @param   key
     *      A key.
     * @return
     *      The new value associated with the key.
     */
    public double decrement(
        final KeyType key);

    /**
     * Decrements the value associated with the given key by the given amount.
     *
     * @param   key
     *      A key.
     * @param value
     *      The amount to decrement the value associated with the given key by.
     * @return
     *      The new value associated with the key.
     */
    public double decrement(
        final KeyType key,
        final double value);

    /**
     * Decrements the values associated all of the given keys by 1.0.
     *
     * @param   keys
     *      A list of keys.
     */
    public void decrementAll(
        final Iterable<? extends KeyType> keys);

    /**
     * Decrements all the keys in this map by the values in the other one.
     *
     * @param   other
     *      The other map.
     */
    public void decrementAll(
        final ScalarMap<? extends KeyType> other);

    /**
     * The maximum value associated with any key in the map.
     *
     * @return
     *      The maximum value associated with any key in the map. If the map
     *      is empty, then Double.NEGATIVE_INFINITY is returned.
     */
    public double getMaxValue();

    /**
     * The minimum value associated with any key in the map.
     *
     * @return
     *      The minimum value associated with any key in the map. If the map
     *      is empty, then Double.POSITIVE_INFINITY is returned.
     */
    public double getMinValue();

    /**
     * Gets the set of entries in this scalar map.
     *
     * @return
     *      The set of entries in the scalar map.
     */
    public Set<? extends ScalarMap.Entry<KeyType>> entrySet();

    /**
     * An entry in a scalar map. Similar to a Map.Entry.
     *
     * @param   <KeyType>
     *      The type of key in the map.
     * @see     java.util.Map.Entry
     */
    public static interface Entry<KeyType>
    {
        
        /**
         * Gets the key.
         *
         * @return
         *      The key.
         */
        public KeyType getKey();

        /**
         * Gets the value associated with the key.
         *
         * @return
         *      The value.
         */
        public double getValue();

        /**
         * Sets the value associated with the key. Optional operation.
         *
         * @param   value
         *      The value.
         */
        public void setValue(
            final double value);
    }

}

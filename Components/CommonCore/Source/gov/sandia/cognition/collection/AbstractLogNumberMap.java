
/*
 * File:                AbstractLogNumberMap.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Incremental Learning Core
 *
 * Copyright July 11, 2012, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 *
 */
package gov.sandia.cognition.collection;

import gov.sandia.cognition.collection.ScalarMap.Entry;
import gov.sandia.cognition.math.LogNumber;
import gov.sandia.cognition.math.matrix.InfiniteVector;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;

/**
 * A partial implementation of a ScalarMap with a LogNumber value
 * @param <KeyType>
 * Type of key in the Map
 * @author Kevin R. Dixon
 * @since 3.3.1
 */
public class AbstractLogNumberMap<KeyType>
    extends AbstractScalarMap<KeyType,LogNumber>
{

    /** 
     * Creates a new instance of AbstractMutableDoubleMap 
     * @param   map
     *      The backing map that the data is stored in.
     */
    public AbstractLogNumberMap(
        final Map<KeyType, LogNumber> map )
    {
        super( map );
    }
 
    @Override
    public AbstractLogNumberMap<KeyType> clone()
    {
        return (AbstractLogNumberMap<KeyType>) super.clone();
    }

    @Override
    public double get(
        KeyType key)
    {
        final LogNumber value = this.map.get(key);
        if( value == null )
        {
            return 0.0;
        }
        else
        {
            return value.doubleValue();
        }
    }

    /**
     * Gets the LogNumber at the given key
     * @param key
     * Key to get from the Map
     * @return
     * Gets the LogNumber from the Map, null if no entry at the given key.
     */
    public LogNumber getLog(
        KeyType key )
    {
        return this.map.get(key);
    }
    

    @Override
    public void set(
        KeyType key,
        double value)
    {
        // This is sort of inefficient here, but if you're using
        // the logarithm-space version, setting rectangular values
        // should be more expensive, I think.
        // But when this gets overridden in LogWeightedDataDistribution,
        // you'll thank me...
        LogNumber newEntry = new LogNumber();
        newEntry.setValue(value);
        this.setLog(key, newEntry);
    }    
    
    /**
     * Sets the LogNumber at the given key
     * @param key
     * Key to set into the Map
     * @param logValue
     * Sets the LogNumber into the Map
     */
    public void setLog(
        KeyType key,
        LogNumber logValue )
    {
        final LogNumber entry = this.map.get(key);
        if( entry == null )
        {
            this.map.put( key, logValue );
        }
        else
        {
            entry.setLogValue( logValue.getLogValue() );
        }
    }
    
    @Override
    public SimpleEntrySet<KeyType> entrySet()
    {
        return new SimpleEntrySet<KeyType>( this.map );
    }

    /**
     * Simple Entry Set for DefaultInfiniteVector
     * @param <KeyType>
     *      The type of the key in the map.
     */
    protected static class SimpleEntrySet<KeyType>
        extends AbstractSet<SimpleEntry<KeyType>>
    {

        /**
         * Backing map
         */
        protected Map<KeyType,LogNumber> map;

        /**
         * Creates a new instance of SimpleEntrySet
         * @param map
         * Backing map
         */
        public SimpleEntrySet(
            Map<KeyType, LogNumber> map)
        {
            this.map = map;
        }

        @Override
        public Iterator<SimpleEntry<KeyType>> iterator()
        {
            return new SimpleIterator<KeyType>( map.entrySet().iterator() );
        }

        @Override
        public int size()
        {
            return map.size();
        }

    }

    /**
     * Simple iterator for DefaultInfiniteVector
     * @param <KeyType>
     *      The type of the key in the map.
     */
    protected static class SimpleIterator<KeyType>
        implements Iterator<SimpleEntry<KeyType>>
    {

        /**
         * Iterator that does all the work
         */
        private Iterator<? extends Map.Entry<KeyType, LogNumber>> delegate;

        /**
         * Default constructor
         * @param delegate
         * Iterator that does all the work
         */
        public SimpleIterator(
            Iterator<? extends Map.Entry<KeyType, LogNumber>> delegate )
        {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext()
        {
            return this.delegate.hasNext();
        }

        @Override
        public SimpleEntry<KeyType> next()
        {
            Map.Entry<KeyType,LogNumber> entry = this.delegate.next();
            return new SimpleEntry<KeyType>(
                entry.getKey(), entry.getValue() );
        }

        @Override
        public void remove()
        {
            this.delegate.remove();
        }

    }


    /**
     * Entry for the AbstractScalarMap
     * @param <KeyType>
     *      The type of the key in the map.
     */
    protected static class SimpleEntry<KeyType>
        implements ScalarMap.Entry<KeyType>,
        InfiniteVector.Entry<KeyType>
    {

        /**
         * Key associated with this entry
         */
        protected KeyType key;

        /**
         * Value associated with the entry
         */
        protected LogNumber value;

        /**
         * Creates a new instance of SimpleEntry
         * @param key
         * Key represented by the Entry
         * @param value
         * Value associated with the Entry
         */
        public SimpleEntry(
            final KeyType key,
            final LogNumber value )
        {
            this.key = key;
            this.value = value;
        }

        @Override
        public KeyType getKey()
        {
            return this.key;
        }

        @Override
        public double getValue()
        {
            return value.getValue();
        }

        @Override
        public void setValue(
            double value)
        {
            this.value.setValue(value);
        }

    }

}

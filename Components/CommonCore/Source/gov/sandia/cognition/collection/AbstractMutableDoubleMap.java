/*
 * File:                AbstractScalarMap.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Incremental Learning Core
 *
 * Copyright June 15, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 *
 */
package gov.sandia.cognition.collection;

import gov.sandia.cognition.math.MutableDouble;
import gov.sandia.cognition.math.matrix.InfiniteVector;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;



/**
 * A partial implementation of a ScalarMap with a MutableDouble value
 * @param <KeyType>
 * Type of key in the Map
 * @author Kevin R. Dixon
 * @since 3.2.1
 */
public class AbstractMutableDoubleMap<KeyType>
    extends AbstractScalarMap<KeyType,MutableDouble>
{

    /** 
     * Creates a new instance of AbstractMutableDoubleMap 
     * @param   map
     *      The backing map that the data is stored in.
     */
    public AbstractMutableDoubleMap(
        final Map<KeyType, MutableDouble> map )
    {
        super( map );
    }


    @Override
    public AbstractMutableDoubleMap<KeyType> clone()
    {
        return (AbstractMutableDoubleMap<KeyType>) super.clone();
    }

    @Override
    public Map<KeyType, MutableDouble> asMap()
    {
        return this.map;
    }

    /**
     * Removes entries from the map with value of 0.0
     */
    public void compact()
    {
        // We can't use entrySet to remove null/zero elements because it throws
        // a ConcurrentModificationException
        // We can't use the keySet either because that throws an Exception,
        // so we need to clone it first
        LinkedList<KeyType> removeKeys = new LinkedList<KeyType>();
        for (Map.Entry<KeyType, MutableDouble> entry : this.map.entrySet())
        {
            final MutableDouble value = entry.getValue();
            if( value.value == 0.0 )
            {
                removeKeys.add( entry.getKey() );
            }
        }
        for( KeyType key : removeKeys )
        {
            this.map.remove(key);
        }
    }

    @Override
    public double get(
        final KeyType key)
    {
        final MutableDouble entry = this.map.get(key);
        if (entry == null)
        {
            return 0.0;
        }
        else
        {
            return entry.value;
        }
    }

    @Override
    public void set(
        final KeyType key,
        final double value)
    {

        final MutableDouble entry = this.map.get(key);
        if( entry == null )
        {
            // Only need to allocate if it's not null
            if( value != 0.0 )
            {
                this.map.put( key, new MutableDouble( value ) );
            }
        }

        // I've commented this out, because I think there's the potential for
        // memory thrashing if this is left in... call compact() method instead?
        //  -- krdixon, 2011-06-27
//        else if( value == 0.0 )
//        {
//            this.map.remove(key);
//        }
        else
        {
            entry.value = value;
        }
    }

    @Override
    public double increment(
        final KeyType key,
        final double value)
    {
        final MutableDouble entry = this.map.get(key);
        double newValue;
        if( entry == null )
        {
            if( value != 0.0 )
            {
                // It's best to avoid this.set() here as it could mess up
                // our total tracker in some subclasses...
                // Also it's more efficient this way (avoid another get)
                this.map.put( key, new MutableDouble(value) );
            }
            newValue = value;
        }
        else
        {
            entry.value += value;
            newValue = entry.value;
        }
        return newValue;
    }

    @Override
    public Set<Entry<KeyType>> entrySet()
    {
        return new SimpleEntrySet<KeyType>( this.map );
    }

    /**
     * Simple Entry Set for DefaultInfiniteVector
     * @param <KeyType>
     *      The type of the key in the map.
     */
    protected static class SimpleEntrySet<KeyType>
        extends AbstractSet<Entry<KeyType>>
    {

        /**
         * Backing map
         */
        protected Map<KeyType,MutableDouble> map;

        /**
         * Creates a new instance of SimpleEntrySet
         * @param map
         * Backing map
         */
        public SimpleEntrySet(
            Map<KeyType, MutableDouble> map)
        {
            this.map = map;
        }

        @Override
        public Iterator<Entry<KeyType>> iterator()
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
        implements Iterator<Entry<KeyType>>
    {

        /**
         * Iterator that does all the work
         */
        private Iterator<? extends Map.Entry<KeyType, MutableDouble>> delegate;

        /**
         * Default constructor
         * @param delegate
         * Iterator that does all the work
         */
        public SimpleIterator(
            Iterator<? extends Map.Entry<KeyType, MutableDouble>> delegate )
        {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext()
        {
            return this.delegate.hasNext();
        }

        @Override
        public Entry<KeyType> next()
        {
            Map.Entry<KeyType,MutableDouble> entry = this.delegate.next();
            return new AbstractMutableDoubleMap.SimpleEntry<KeyType>(
                entry.getKey(), entry.getValue() );
        }

        @Override
        public void remove()
        {
            this.delegate.remove();
        }

    }

    /**
     * Interface for entries.
     * 
     * @param <KeyType>
     *      The type of the key in the map.
     */
    public interface Entry<KeyType>
        extends ScalarMap.Entry<KeyType>, InfiniteVector.Entry<KeyType>
    {
        
    }

    /**
     * Entry for the AbstractScalarMap
     * @param <KeyType>
     *      The type of the key in the map.
     */
    protected static class SimpleEntry<KeyType>
        implements Entry<KeyType>
    {

        /**
         * Key associated with this entry
         */
        protected KeyType key;

        /**
         * Value associated with the entry
         */
        protected MutableDouble value;

        /**
         * Creates a new instance of SimpleEntry
         * @param key
         * Key represented by the Entry
         * @param value
         * Value associated with the Entry
         */
        public SimpleEntry(
            final KeyType key,
            final MutableDouble value )
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
            return value.value;
        }

        @Override
        public void setValue(
            double value)
        {
            this.value.value = value;
        }

    }

}

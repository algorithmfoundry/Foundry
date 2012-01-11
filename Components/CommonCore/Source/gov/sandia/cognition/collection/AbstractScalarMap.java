/*
 * File:                AbstractScalarMap.java
 * Authors:             Justin Basilico
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

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.AbstractMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Partial implementation of ScalarMap
 * 
 * @param <KeyType>
 *      The type of the key in the map.
 * @author  Justin Basilico
 * @since   3.2.1
 */
public abstract class AbstractScalarMap<KeyType>
    extends AbstractCloneableSerializable
    implements ScalarMap<KeyType>
{

    /**
     * Default Constructor
     */
    public AbstractScalarMap()
    {
        super();
    }
    
    @Override
    public Map<KeyType, ? extends Number> asMap()
    {
        return new MapWrapper();
    }

    @Override
    public void setAll(
        final Iterable<? extends KeyType> keys,
        final double value)
    {
        for (KeyType key : keys)
        {
            this.set(key, value);
        }
    }

    @Override
    public double increment(
        final KeyType key)
    {
        return this.increment(key, 1.0);
    }

    @Override
    public double increment(
        final KeyType key,
        final double value)
    {
        final double newValue = this.get(key) + value;
        this.set(key, newValue);
        return newValue;
    }

    @Override
    public void incrementAll(
        final Iterable<? extends KeyType> keys)
    {
        for (KeyType key : keys)
        {
            this.increment(key);
        }
    }

    @Override
    public void incrementAll(
        final ScalarMap<? extends KeyType> other)
    {
        for (Entry<? extends KeyType> entry : other.entrySet())
        {
            this.increment(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public double decrement(
        final KeyType key)
    {
        return this.decrement(key, 1.0);
    }

    @Override
    public double decrement(
        final KeyType key,
        final double value)
    {
        return this.increment(key, -value);
    }

    @Override
    public void decrementAll(
        final Iterable<? extends KeyType> keys)
    {
        for (KeyType key : keys)
        {
            this.decrement(key);
        }
    }

    @Override
    public void decrementAll(
        final ScalarMap<? extends KeyType> other)
    {
        for (Entry<? extends KeyType> entry : other.entrySet())
        {
            this.decrement(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public double getMaxValue()
    {
        double maxValue = Double.NEGATIVE_INFINITY;

        for (Entry<KeyType> entry : this.entrySet())
        {
            final double value = entry.getValue();
            if (value > maxValue)
            {
                maxValue = value;
            }
        }

        return maxValue;
    }

    @Override
    public double getMinValue()
    {
        double minValue = Double.POSITIVE_INFINITY;

        for (Entry<KeyType> entry : this.entrySet())
        {
            final double value = entry.getValue();
            if (value < minValue)
            {
                minValue = value;
            }
        }

        return minValue;
    }

    @Override
    public boolean isEmpty()
    {
        return this.size() == 0;
    }

    @Override
    public KeyType getMaxValueKey()
    {
        double maxValue = Double.NEGATIVE_INFINITY;
        KeyType maxKey = null;

        for (Entry<KeyType> entry : this.entrySet())
        {
            final KeyType key = entry.getKey();
            final double value = entry.getValue();
            if (maxKey == null || value > maxValue)
            {
                maxKey = key;
                maxValue = value;
            }
        }

        return maxKey;
    }

    @Override
    public Set<KeyType> getMaxValueKeys()
    {
        double maxValue = Double.NEGATIVE_INFINITY;
        final LinkedHashSet<KeyType> maxKeys = new LinkedHashSet<KeyType>();

        for (Entry<KeyType> entry : this.entrySet())
        {
            final KeyType key = entry.getKey();
            final double value = entry.getValue();
            if (value > maxValue)
            {
                maxKeys.clear();
                maxValue = value;
                maxKeys.add(key);
            }
            else if (value == maxValue)
            {
                maxKeys.add(key);
            }
        }

        return maxKeys;
    }

    @Override
    public KeyType getMinValueKey()
    {
        double minValue = Double.POSITIVE_INFINITY;
        KeyType minKey = null;

        for (Entry<KeyType> entry : this.entrySet())
        {
            final KeyType key = entry.getKey();
            final double value = entry.getValue();
            if (minKey == null || value < minValue)
            {
                minKey = key;
                minValue = value;
            }
        }

        return minKey;
    }

    @Override
    public Set<KeyType> getMinValueKeys()
    {
        double minValue = Double.POSITIVE_INFINITY;
        final LinkedHashSet<KeyType> minKeys = new LinkedHashSet<KeyType>();

        for (Entry<KeyType> entry : this.entrySet())
        {
            final KeyType key = entry.getKey();
            final double value = entry.getValue();
            if (value < minValue)
            {
                minKeys.clear();
                minValue = value;
                minKeys.add(key);
            }
            else if (value == minValue)
            {
                minKeys.add(key);
            }
        }

        return minKeys;
    }

    /**
     * Wrapper when using the asMap method
     */
    @SuppressWarnings("unchecked")
    protected class MapWrapper
        extends AbstractMap<KeyType, Double>
        implements CloneableSerializable
    {
        /**
         * Default constructor
         */
        protected MapWrapper()
        {
            super();
        }

        @Override
        public MapWrapper clone()
        {
            try
            {
                return (MapWrapper) super.clone();
            }
            catch (CloneNotSupportedException e)
            {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int size()
        {
            return AbstractScalarMap.this.size();
        }

        @Override
        public boolean isEmpty()
        {
            return AbstractScalarMap.this.isEmpty();
        }

        @Override
        public boolean containsKey(
            final Object key)
        {
            return AbstractScalarMap.this.containsKey((KeyType) key);
        }

        @Override
        public Double get(
            final Object key)
        {
            return AbstractScalarMap.this.get((KeyType) key);
        }

        @Override
        public Double put(
            final KeyType key,
            final Double value)
        {
            if (value == null)
            {
                AbstractScalarMap.this.set(key, 0.0);
            }
            else
            {
                AbstractScalarMap.this.set(key, value);
            }
            return value;
        }

        @Override
        public Double remove(
            final Object key)
        {
            final double oldValue = AbstractScalarMap.this.get((KeyType) key);
            AbstractScalarMap.this.set((KeyType) key, 0.0);
            return oldValue;
        }

        @Override
        public void clear()
        {
            AbstractScalarMap.this.clear();
        }

        @Override
        public Set<KeyType> keySet()
        {
            return AbstractScalarMap.this.keySet();
        }

        @Override
        public Set<Map.Entry<KeyType, Double>> entrySet()
        {
            final LinkedHashSet<Map.Entry<KeyType, Double>> result =
                new LinkedHashSet<Map.Entry<KeyType, Double>>(this.size());

            for (ScalarMap.Entry<KeyType> entry
                : AbstractScalarMap.this.entrySet())
            {
                result.add(new AbstractMap.SimpleImmutableEntry<KeyType, Double>(
                    entry.getKey(), entry.getValue()));
            }
            return result;
        }

    }

}

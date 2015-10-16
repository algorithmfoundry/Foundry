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
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Partial implementation of ScalarMap
 * 
 * @param <KeyType>
 *      The type of the key in the Map.
 * @param <NumberType> 
 *      the type of Number in the Map.
 * @author  Justin Basilico
 * @since   3.2.1
 */
public abstract class AbstractScalarMap<KeyType,NumberType extends Number>
    extends AbstractCloneableSerializable
    implements ScalarMap<KeyType>
{

    /**
     * Map backing that performs the storage.
     */
    protected Map<KeyType,NumberType> map;    
    
    /** 
     * Creates a new instance of AbstractScalarMap 
     * @param   map
     *      The backing map that the data is stored in.
     */
    public AbstractScalarMap(
        final Map<KeyType,NumberType> map )
    {
        super();
        this.map = map;
    }
    
    @Override
    public AbstractScalarMap<KeyType,NumberType> clone()
    {
        @SuppressWarnings("unchecked")
        AbstractScalarMap<KeyType,NumberType> clone =
            (AbstractScalarMap<KeyType,NumberType>) super.clone();
        clone.map = new LinkedHashMap<KeyType, NumberType>( this.size() );
        clone.setAll(this);
        return clone;
    }    
    
    @Override
    public Map<KeyType,NumberType> asMap()
    {
        return this.map;
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
    public void setAll(
        final ScalarMap<? extends KeyType> other)
    {
        for (Entry<? extends KeyType> entry : other.entrySet())
        {
            this.set(entry.getKey(), entry.getValue());
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
        return this.map.isEmpty();
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

    @Override
    public void clear()
    {
        this.map.clear();
    }

    @Override
    public Set<KeyType> keySet()
    {
        return this.map.keySet();
    }

    @Override
    public boolean containsKey(
        final KeyType key)
    {
        return this.map.containsKey(key);
    }

    @Override
    public int size()
    {
        return this.map.size();
    }

}

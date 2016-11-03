/*
 * File:                DefaultKeyValuePair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright November 28, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import java.util.Objects;

/**
 * A default implementation of the {@code KeyValuePair} interface.
 *
 * @param   <KeyType>
 *      The type of the key.
 * @param   <ValueType>
 *      The type of the value.
 * @author  Justin Basilico
 * @since   3.1
 */
public class DefaultKeyValuePair<KeyType, ValueType>
    extends AbstractCloneableSerializable
    implements KeyValuePair<KeyType, ValueType>
{

    /** The key part in the pair, which is the first element. */
    protected KeyType key;

    /** The value part in the pair, which is the second element. */
    protected ValueType value;

    /**
     * Creates a new, empty {@code DefaultKeyValuePair}.
     */
    public DefaultKeyValuePair()
    {
        this(null, null);
    }

    /**
     * Creates a new {@code DefaultKeyValuePair} from the given key and value.
     *
     * @param   key
     *      The key.
     * @param   value
     *      The value.
     */
    public DefaultKeyValuePair(
        final KeyType key,
        final ValueType value)
    {
        super();

        this.setKey(key);
        this.setValue(value);
    }

    /**
     * Creates a new {@code DefaultKeyValuePair} as a shallow copy of the
     * given key-value pair.
     *
     * @param   other
     *      The key-value pair to copy.
     */
    public DefaultKeyValuePair(
        final KeyValuePair<? extends KeyType, ? extends ValueType> other)
    {
        this(other.getKey(), other.getValue());
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof DefaultKeyValuePair))
        {
            return false;
        }
        DefaultKeyValuePair<?,?> p = (DefaultKeyValuePair<?,?>)o;

        if (key == null && p.key != null)
        {
            return false;
        }
        if (!key.equals(p.key))
        {
            return false;
        }

        if (value == null && p.value != null)
        {
            return false;
        }
        return value.equals(p.value);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.key);
        hash = 61 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public String toString()
    {
        return "(key: " + this.getKey() + ", value: " + this.getValue() + ")";
    }

    @Override
    public KeyType getFirst()
    {
        return this.getKey();
    }

    @Override
    public ValueType getSecond()
    {
        return this.getValue();
    }

    @Override
    public KeyType getKey()
    {
        return this.key;
    }

    /**
     * Sets the key element of the pair.
     *
     * @param   key
     *      The key.
     */
    public void setKey(
        final KeyType key)
    {
        this.key = key;
    }

    @Override
    public ValueType getValue()
    {
        return this.value;
    }

    /**
     * Sets the value element of the pair.
     *
     * @param   value
     *      The value.
     */
    public void setValue(
        final ValueType value)
    {
        this.value = value;
    }

    /**
     * Convenience method to create a new, empty {@code DefaultKeyValuePair}.
     *
     * @param   <KeyType>
     *      The type of the key.
     * @param   <ValueType>
     *      The type of the value.
     * @return
     *      A new, empty key-value pair.
     */
    public static <KeyType, ValueType> DefaultKeyValuePair<KeyType, ValueType> create()
    {
        return new DefaultKeyValuePair<KeyType, ValueType>();
    }

    /**
     *
     * Convenience method to create a new {@code DefaultKeyValuePair} from the
     * given key and value.
     *
     * @param   <KeyType>
     *      The type of the key.
     * @param   <ValueType>
     *      The type of the value.
     * @param   key
     *      The key.
     * @param   value
     *      The value.
     * @return
     *      A new, empty key-value pair.
     */
    public static <KeyType, ValueType> DefaultKeyValuePair<KeyType, ValueType> create(
        final KeyType key,
        final ValueType value)
    {
        return new DefaultKeyValuePair<KeyType, ValueType>(key, value);
    }

}

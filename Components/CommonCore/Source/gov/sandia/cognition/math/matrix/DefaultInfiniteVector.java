/*
 * File:                DefaultInfiniteVector.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 26, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.collection.AbstractMutableDoubleMap;
import gov.sandia.cognition.collection.ScalarMap;
import gov.sandia.cognition.math.MutableDouble;
import gov.sandia.cognition.math.matrix.InfiniteVector.Entry;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;



/**
 * An implementation of an {@code InfiniteVector} backed by a
 * {@code LinkedHashMap}.
 *
 * @param   <KeyType>
 *      The type of the keys (indices) into the infinite dimensional vector.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   3.2.1
 */
public class DefaultInfiniteVector<KeyType>
    extends AbstractMutableDoubleMap<KeyType>
    implements InfiniteVector<KeyType>
{
    
    /** The default capacity is {@value}. */
    public static final int DEFAULT_INITIAL_CAPACITY = 16;

    /** 
     * Creates a new, empty instance of {@code DefaultInfiniteVector}.
     */
    public DefaultInfiniteVector()
    {
        this(DEFAULT_INITIAL_CAPACITY);
    }

    /**
     * Creates a new, empty instance of {@code DefaultInfiniteVector} with the
     * given initial capacity.
     *
     * @param   initialCapacity
     *      The initial capacity of the data structure. Must be positive.
     */
    public DefaultInfiniteVector(
        final int initialCapacity)
    {
        this(new LinkedHashMap<KeyType, MutableDouble>(initialCapacity));
    }


    /**
     * Creates a new {@code AbstractMapInfiniteVector} with the given backing
     * map.
     *
     * @param   map
     *      The backing map that the data is stored in.
     */
    protected DefaultInfiniteVector(
        final LinkedHashMap<KeyType, MutableDouble> map)
    {
        super( map );
    }

    @Override
    public DefaultInfiniteVector<KeyType> clone()
    {
        return (DefaultInfiniteVector<KeyType>) super.clone();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(
        final Object other)
    {
        return (other == this)
            || (other instanceof InfiniteVector<?>
                && this.equals((InfiniteVector<KeyType>) other, 0.0));
    }

    @Override
    public boolean equals(
        final InfiniteVector<KeyType> other,
        final double effectiveZero)
    {
        return (other != null) && this.euclideanDistance(other) <= effectiveZero;
    }

    @Override
    public double sum()
    {
        double sum = 0.0;
        for( ScalarMap.Entry<KeyType> entry : this.entrySet() )
        {
            sum += entry.getValue();
        }
        return sum;
    }

    @Override
    public double norm1()
    {
        double result = 0.0;
        for (ScalarMap.Entry<KeyType> entry : this.entrySet())
        {
            result += Math.abs(entry.getValue());
        }
        return result;
    }

    @Override
    public double norm2()
    {
        return Math.sqrt(this.norm2Squared());
    }

    @Override
    public double norm2Squared()
    {
        double result = 0.0;
        for (ScalarMap.Entry<KeyType> entry : this.entrySet())
        {
            final double value = entry.getValue();
            result += value * value;
        }
        return result;
    }

    @Override
    public double normInfinity()
    {
        double max = 0.0;
        for (ScalarMap.Entry<KeyType> entry : this.entrySet())
        {
            final double value = Math.abs(entry.getValue());
            if (value > max)
            {
                max = value;
            }

        }
        return max;
    }

    @Override
    public double norm(
        final double power)
    {
        ArgumentChecker.assertIsPositive("power", power);
        double sum = 0.0;
        for( VectorSpace.Entry entry : this )
        {
            final double value = Math.abs(entry.getValue());
            sum += Math.pow(value,power);
        }
        return Math.pow(sum,1.0/power);
    }

    @Override
    public double cosine(
        final InfiniteVector<KeyType> other)
    {
        final double dotProduct = this.dotProduct(other);
        if (dotProduct == 0.0)
        {
            return 0.0;
        }
        else
        {
            final double thisNorm = this.norm2Squared();
            final double otherNorm = other.norm2Squared();
            return dotProduct / Math.sqrt(thisNorm * otherNorm);
        }
    }

    @Override
    public double angle(
        final InfiniteVector<KeyType> other)
    {
        return Math.acos(this.cosine(other));
    }

    @Override
    public double euclideanDistance(
        final InfiniteVector<KeyType> other)
    {
        return Math.sqrt(this.euclideanDistanceSquared(other));
    }

    @Override
    public double euclideanDistanceSquared(
        final InfiniteVector<KeyType> other)
    {
        return this.minus(other).norm2Squared();
    }

    @Override
    public InfiniteVector<KeyType> unitVector()
    {
        final InfiniteVector<KeyType> result = this.clone();
        result.unitVectorEquals();
        return result;
    }

    @Override
    public void unitVectorEquals()
    {
        final double length = this.norm2();

        if (length != 0.0)
        {
            this.scaleEquals(1.0 / length);
        }
    }

    @Override
    public boolean isUnitVector()
    {
        return this.isUnitVector(0.0);
    }

    @Override
    public boolean isUnitVector(
        final double tolerance)
    {
        return Math.abs(1.0 - this.norm2()) <= tolerance;
    }

    @Override
    public double dotProduct(
        final InfiniteVector<KeyType> other)
    {
        double result = 0.0;
        for (Map.Entry<KeyType, MutableDouble> entry : this.map.entrySet())
        {
            final KeyType key = entry.getKey();
            final double thisValue = entry.getValue().value;
            final double otherValue = other.get(key);
            result += thisValue * otherValue;
        }
        return result;
    }

    @Override
    public InfiniteVector<KeyType> plus(
        final InfiniteVector<KeyType> other)
    {
        final InfiniteVector<KeyType> result = this.clone();
        result.plusEquals(other);
        return result;
    }

    @Override
    public void plusEquals(
        final InfiniteVector<KeyType> other)
    {
        for (ScalarMap.Entry<KeyType> entry : other.entrySet())
        {
            this.increment(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public InfiniteVector<KeyType> minus(
        final InfiniteVector<KeyType> other)
    {
        final InfiniteVector<KeyType> result = this.clone();
        result.minusEquals(other);
        return result;
    }
    
    @Override
    public void minusEquals(
        final InfiniteVector<KeyType> other)
    {
        if (this == other)
        {
            // If we are subtracting from self, avoid modification errors.
            this.zero();
        }
        else
        {
            for (ScalarMap.Entry<KeyType> entry : other.entrySet())
            {
                this.decrement(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public InfiniteVector<KeyType> dotTimes(
        final InfiniteVector<KeyType> other)
    {
        final InfiniteVector<KeyType> result = this.clone();
        result.dotTimesEquals(other);
        return result;
    }

    @Override
    public void dotTimesEquals(
        final InfiniteVector<KeyType> other)
    {
        for (Map.Entry<KeyType, MutableDouble> entry : this.map.entrySet())
        {
            final KeyType key = entry.getKey();
            final MutableDouble value = entry.getValue();
            final double thisValue = value.value;
            final double otherValue = other.get(key);
            value.value = thisValue * otherValue;
        }
    }

    @Override
    public InfiniteVector<KeyType> scale(
        final double scaleFactor)
    {
        final InfiniteVector<KeyType> result = this.clone();
        result.scaleEquals(scaleFactor);
        return result;
    }
    
    @Override
    public void scaleEquals(
        final double scaleFactor)
    {
        for (Map.Entry<KeyType, MutableDouble> entry : this.map.entrySet())
        {
            final MutableDouble value = entry.getValue();
            value.value *= scaleFactor;
        }
    }

    @Override
    public void zero()
    {
        this.clear();
    }

    @Override
    public boolean isZero()
    {
        return this.isZero(0.0);
    }

    @Override
    public boolean isZero(
        final double effectiveZero)
    {
        for (Map.Entry<KeyType, MutableDouble> entry : this.map.entrySet())
        {
            final double v1 = entry.getValue().value;
            if (Math.abs(v1) > effectiveZero)
            {
                return false;
            }
        }
        return true;
    }

    @Override
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
    public String toString()
    {
        return this.map.toString();
    }

    @Override
    public Iterator<InfiniteVector.Entry<KeyType>> iterator()
    {
        return new SimpleIterator();
    }

    @Override
    public InfiniteVector<KeyType> negative()
    {
        final InfiniteVector<KeyType> result = this.clone();
        result.negativeEquals();
        return result;
    }

    @Override
    public void negativeEquals()
    {
        this.scaleEquals(-1.0);
    }

    /**
     * Simple iterator for DefaultInfiniteVector
     */
    protected class SimpleIterator
        implements Iterator<InfiniteVector.Entry<KeyType>>
    {

        /**
         * Iterator that does all the work
         */
        private Iterator<Map.Entry<KeyType, MutableDouble>> delegate;

        /**
         * Default constructor
         */
        public SimpleIterator()
        {
            this.delegate = map.entrySet().iterator();
        }

        @Override
        public boolean hasNext()
        {
            return this.delegate.hasNext();
        }

        @Override
        public AbstractMutableDoubleMap.SimpleEntry<KeyType> next()
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

}

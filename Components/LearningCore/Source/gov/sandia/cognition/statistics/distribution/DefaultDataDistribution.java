/*
 * File:                DefaultDataDistribution.java
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

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.factory.Factory;
import gov.sandia.cognition.learning.algorithm.AbstractBatchAndIncrementalLearner;
import gov.sandia.cognition.statistics.DataDistribution.PMF;
import gov.sandia.cognition.math.MutableDouble;
import gov.sandia.cognition.statistics.AbstractDataDistribution;
import gov.sandia.cognition.statistics.DataDistribution;
import gov.sandia.cognition.statistics.DistributionEstimator;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.WeightedValue;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A default implementation of {@code ScalarDataDistribution} that uses a
 * backing map.
 * 
 * @param <KeyType>
 * Type of Key in the distribution
 * @author  Justin Basilico
 * @since   3.1.2
 */
public class DefaultDataDistribution<KeyType>
    extends AbstractDataDistribution<KeyType>
{

    /**
     * Default initial capacity, {@value}.
     */
    public static final int DEFAULT_INITIAL_CAPACITY = 10;

    /**
     * Total of the counts in the distribution
     */
    protected double total;

    /**
     * Default constructor
     */
    public DefaultDataDistribution()
    {
        this( DEFAULT_INITIAL_CAPACITY );
    }

    /**
     * Creates a new instance of DefaultDataDistribution
     * @param initialCapacity
     * Initial capacity of the Map
     */
    public DefaultDataDistribution(
        int initialCapacity)
    {
        this( new LinkedHashMap<KeyType, MutableDouble>( initialCapacity), 0.0 );
    }

    /**
     * Creates a new instance of DefaultDataDistribution
     * @param other
     * DataDistribution to copy
     */
    public DefaultDataDistribution(
        final DataDistribution<? extends KeyType> other)
    {
        this(new LinkedHashMap<KeyType, MutableDouble>(other.size()), 0.0);
        this.incrementAll(other);
    }

    /**
     * Creates a new instance of ScalarDataDistribution
     * @param data
     * Data to create the distribution
     */
    public DefaultDataDistribution(
        final Iterable<? extends KeyType> data )
    {
        this();
        this.incrementAll(data);
    }

    /**
     * Creates a new instance of
     * @param map
     * Backing Map that stores the data
     * @param total
     * Sum of all values in the Map
     */
    protected DefaultDataDistribution(
        final Map<KeyType, MutableDouble> map,
        final double total)
    {
        super( map );
        this.total = total;
    }

    @Override
    public DefaultDataDistribution<KeyType> clone()
    {
        DefaultDataDistribution<KeyType> clone =
            (DefaultDataDistribution<KeyType>) super.clone();
        
        // We have to manually reset "total" because super.super.clone
        // calls "incrementAll", which will, in turn, increment the total
        // So we'd end up with twice the total.
        clone.total = this.total;
        return clone;
    }

    @Override
    public double increment(
        KeyType key,
        final double value)
    {
        final MutableDouble entry = this.map.get(key);
        double newValue;
        double delta;
        if( entry == null )
        {
            if( value > 0.0 )
            {
                // It's best to avoid this.set() here as it could mess up
                // our total tracker in some subclasses...
                // Also it's more efficient this way (avoid another get)
                this.map.put( key, new MutableDouble(value) );
                delta = value;
            }
            else
            {
                delta = 0.0;
            }
            newValue = value;
        }
        else
        {
            if( entry.value+value >= 0.0 )
            {
                delta = value;
                entry.value += value;
            }
            else
            {
                delta = -entry.value;
                entry.value = 0.0;
            }
            newValue = entry.value;
        }

        this.total += delta;
        return newValue;
    }

    @Override
    public void set(
        final KeyType key,
        final double value)
    {

        // I decided not to call super.set because it would result in me
        // having to perform an extra call to this.map.get
        final MutableDouble entry = this.map.get(key);
        if( entry == null )
        {
            // Only need to allocate if it's not null
            if( value > 0.0 )
            {
                this.map.put( key, new MutableDouble( value ) );
                this.total += value;
            }
        }
        else if( value > 0.0 )
        {
            this.total += value - entry.value;
            entry.value = value;
        }
        else
        {
            entry.value = 0.0;
        }
    }

    @Override
    public double getTotal()
    {
        return this.total;
    }
    
    @Override
    public void clear()
    {
        super.clear();
        this.total = 0.0;
    }

    @Override
    public DistributionEstimator<KeyType, ? extends DataDistribution<KeyType>> getEstimator()
    {
        return new DefaultDataDistribution.Estimator<KeyType>();
    }

    @Override
    public DataDistribution.PMF<KeyType> getProbabilityFunction()
    {
        return new DefaultDataDistribution.PMF<KeyType>(this);
    }

    /**
     * Gets the average value of all keys in the distribution, that is, the
     * total value divided by the number of keys (even zero-value keys)
     * @return
     * Average value of all keys in the distribution
     */
    public double getMeanValue()
    {
        final int ds = this.getDomainSize();
        if( ds > 0 )
        {
            return this.getTotal() / ds;
        }
        else
        {
            return 0.0;
        }
    }

    /**
     * PMF of the DefaultDataDistribution
     * @param <KeyType>
     * Type of Key in the distribution
     */
    public static class PMF<KeyType>
        extends DefaultDataDistribution<KeyType>
        implements DataDistribution.PMF<KeyType>
    {

        /**
         * Default constructor
         */
        public PMF()
        {
            super();
        }

        /**
         * Copy constructor
         * @param other
         * ScalarDataDistribution to copy
         */
        public PMF(
            final DataDistribution<KeyType> other)
        {
            super(other);
        }

        /**
         * Creates a new instance of DefaultDataDistribution
         * @param initialCapacity
         * Initial capacity of the Map
         */
        public PMF(
            int initialCapacity)
        {
            super( initialCapacity );
        }

        /**
         * Creates a new instance of ScalarDataDistribution
         * @param data
         * Data to create the distribution
         */
        public PMF(
            final Iterable<? extends KeyType> data )
        {
            super( data );
        }

        @Override
        public double logEvaluate(
            KeyType input)
        {
            return this.getLogFraction(input);
        }

        @Override
        public Double evaluate(
            KeyType input)
        {
            return this.getFraction(input);
        }

        @Override
        public DefaultDataDistribution.PMF<KeyType> getProbabilityFunction()
        {
            return this;
        }

    }

    /**
     * Estimator for a DefaultDataDistribution
     * @param <KeyType>
     * Type of Key in the distribution
     */
    public static class Estimator<KeyType>
        extends AbstractBatchAndIncrementalLearner<KeyType, DefaultDataDistribution.PMF<KeyType>>
        implements DistributionEstimator<KeyType, DefaultDataDistribution.PMF<KeyType>>
    {

        /**
         * Default constructor
         */
        public Estimator()
        {
            super();
        }

        @Override
        public DefaultDataDistribution.PMF<KeyType> createInitialLearnedObject()
        {
            return new DefaultDataDistribution.PMF<KeyType>();
        }

        @Override
        public void update(
            final DefaultDataDistribution.PMF<KeyType> target,
            final KeyType data)
        {
            target.increment(data, 1.0);
        }

    }

    /**
     * A weighted estimator for a DefaultDataDistribution
     * @param <KeyType>
     * Type of Key in the distribution
     */
    public static class WeightedEstimator<KeyType>
        extends AbstractBatchAndIncrementalLearner<WeightedValue<? extends KeyType>, DefaultDataDistribution.PMF<KeyType>>
        implements DistributionWeightedEstimator<KeyType,DefaultDataDistribution.PMF<KeyType>>
    {

        /**
         * Default constructor
         */
        public WeightedEstimator()
        {
            super();
        }

        @Override
        public DefaultDataDistribution.PMF<KeyType> createInitialLearnedObject()
        {
            return new DefaultDataDistribution.PMF<KeyType>();
        }

        @Override
        public void update(
            final DefaultDataDistribution.PMF<KeyType> target,
            final WeightedValue<? extends KeyType> data)
        {
            target.increment( data.getValue(), data.getWeight() );
        }
        
    }

    /**
     * A factory for {@code DefaultDataDistribution} objects using some given
     * initial capacity for them.
     *
     * @param   <DataType>
     *      The type of data for the factory.
     */
    public static class DefaultFactory<DataType>
        extends AbstractCloneableSerializable
        implements Factory<DefaultDataDistribution<DataType>>
    {

        /** The initial domain capacity. */
        protected int initialDomainCapacity;

        /**
         * Creates a new {@code DefaultFactory} with a default
         * initial domain capacity.
         */
        public DefaultFactory()
        {
            this(DEFAULT_INITIAL_CAPACITY);
        }

        /**
         * Creates a new {@code DefaultFactory} with a given
         * initial domain capacity.
         *
         * @param   initialDomainCapacity
         *      The initial capacity for the domain. Must be positive.
         */
        public DefaultFactory(
            final int initialDomainCapacity)
        {
            super();

            this.setInitialDomainCapacity(initialDomainCapacity);
        }

        @Override
        public DefaultDataDistribution<DataType> create()
        {
            // Create the histogram.
            return new DefaultDataDistribution<DataType>(
                this.getInitialDomainCapacity());
        }

        /**
         * Gets the initial domain capacity.
         *
         * @return
         *      The initial domain capacity. Must be positive.
         */
        public int getInitialDomainCapacity()
        {
            return this.initialDomainCapacity;
        }

        /**
         * Sets the initial domain capacity.
         *
         * @param   initialDomainCapacity
         *      The initial domain capacity. Must be positive.
         */
        public void setInitialDomainCapacity(
            final int initialDomainCapacity)
        {
            ArgumentChecker.assertIsPositive("initialDomainCapacity",
                initialDomainCapacity);
            this.initialDomainCapacity = initialDomainCapacity;
        }

    }

}

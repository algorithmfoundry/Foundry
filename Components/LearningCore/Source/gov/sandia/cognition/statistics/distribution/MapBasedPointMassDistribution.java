/*
 * File:                MapBasedPointMassDistribution.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Dec 3, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.statistics.distribution;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.math.Ring;
import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.statistics.AbstractDistribution;
import gov.sandia.cognition.statistics.DistributionWeightedEstimator;
import gov.sandia.cognition.statistics.PointMassDistribution;
import gov.sandia.cognition.statistics.ProbabilityMassFunctionUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;

/**
 * A type of discrete distribution, where the data points have nonnegative,
 * real-valued mass (or "weights") associated with them.
 * @param <DataType> Type of data stored in the Map.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public class MapBasedPointMassDistribution<DataType>
    extends AbstractDistribution<DataType>
    implements PointMassDistribution<DataType>
{

    /**
     * Total mass of data points in the distribution.
     */
    private double totalMass;

    /**
     * The map of values to mass.
     */
    private Map<DataType, Entry> dataMap;

    /** 
     * Creates a new instance of MapBasedPointMassDistribution 
     */
    public MapBasedPointMassDistribution()
    {
        super();

        this.setDataMap(new LinkedHashMap<DataType, Entry>());
        this.setTotalMass(0.0);
    }

    /**
     * Creates a new MapBasedPointMassDistribution with the given expected
     * domain size.
     *
     * @param   initialDomainCapacity
     *      The initial domain capacity. Must be positive.
     */
    public MapBasedPointMassDistribution(
        final int initialDomainCapacity)
    {
        super();

        this.setDataMap(new LinkedHashMap<DataType, Entry>(initialDomainCapacity));
        this.setTotalMass(0.0);
    }

    /**
     * Creates a new instance of MapBasedPointMassDistribution
     * @param values
     * Values to add to the Map
     */
    public MapBasedPointMassDistribution(
        Collection<? extends DataType> values)
    {
        this( CollectionUtil.size(values) );
        
        for (DataType value : values)
        {
            this.add(value);
        }

    }

    /**
     * Copy constructor
     * @param other
     * MapBasedPointMassDistribution to copy
     */
    public MapBasedPointMassDistribution(
        MapBasedPointMassDistribution<DataType> other)
    {
        this( other.getDataMap().size() );

        for (DataType value : other.getDomain())
        {
            this.add(value, other.getMass(value));
        }

    }

    @Override
    public MapBasedPointMassDistribution<DataType> clone()
    {
        @SuppressWarnings("unchecked")
        MapBasedPointMassDistribution<DataType> clone =
            (MapBasedPointMassDistribution<DataType>) super.clone();

        // Make a new backing map. Initialize it to an approprite size.
        clone.dataMap = new LinkedHashMap<DataType, Entry>(this.dataMap.size());
        
        // Copy all the values into the map.
        for (Map.Entry<DataType, Entry> entry : this.dataMap.entrySet())
        {
            clone.dataMap.put(entry.getKey(), new Entry(entry.getValue()));
        }

        return clone;
    }

    @SuppressWarnings("unchecked")
    public DataType getMean()
    {

        DataType mean = null;
        DataType first = CollectionUtil.getFirst(this.getDomain());
        if (first instanceof Number)
        {
            mean = (DataType) new Double(ScalarDataDistribution.getMean(
                (MapBasedPointMassDistribution<Number>) this));
        }
        else if (first instanceof Ring)
        {
            RingAccumulator averager = new RingAccumulator();
            for (DataType value : this.getDomain())
            {
                Ring ring = (Ring) value;
                averager.accumulate(ring.scale(this.getMass(value)));
            }
            mean = (DataType) averager.scaleSum(1.0 / this.getTotalMass());

        }
        else
        {
            // We do not support the mean over the value type since we do not
            // know how to compute the mean from an arbitrary set of values.
            throw new UnsupportedOperationException("mean not supported");
        }

        return mean;
    }

    public ArrayList<DataType> sample(
        Random random,
        int numSamples)
    {
        return ProbabilityMassFunctionUtil.sample(
            this.getProbabilityFunction(), random, numSamples);
    }

    public void add(
        DataType value)
    {
        this.add(value, 1.0);
    }

    public void add(
        final DataType value,
        final double mass)
    {
        if (mass < 0.0)
        {
            throw new IllegalArgumentException("mass cannot be negative");
        }
        else if (mass == 0.0)
        {
            // Nothing to do if the point has no mass.
            return;
        }

        // Get the entry for the value.
        Entry entry = this.dataMap.get(value);
        if (entry == null)
        {
            // Add a new entry for this value.
            entry = new Entry(mass);
            this.dataMap.put(value, entry);
        }
        else
        {
            // Add to the existing entry.
            entry.mass += mass;
        }

        this.totalMass += mass;
    }

    public void setMass(
        final DataType value,
        final double mass)
    {
        if (mass < 0.0)
        {
            throw new IllegalArgumentException("mass cannot be negative");
        }

        // Get the entry for the value.
        double change = 0.0;
        Entry entry = this.dataMap.get(value);
        if (entry != null)
        {
            // Add to the existing entry.
            change = mass - entry.mass;
            entry.mass = mass;

            if (mass <= 0.0)
            {
                // There is no longer any mass in the entry.
                this.dataMap.remove(value);
            }
        }
        else if (mass > 0.0)
        {
            // Add a new entry for this value.
            entry = new Entry(mass);
            this.dataMap.put(value, entry);
            change = mass;
        }

        this.totalMass += change;
    }

    public void clear()
    {
        this.getDataMap().clear();
        this.setTotalMass(0.0);
    }

    public void remove(
        DataType value)
    {
        this.remove(value, this.getMass(value));
    }

    public void remove(
        DataType value,
        double mass)
    {
        if (mass < 0.0)
        {
            throw new IllegalArgumentException("mass cannot be negative");
        }
        else if (mass == 0.0)
        {
            // Nothing to do if the point has no mass.
            return;
        }

        // Find the existing mass
        final Entry entry = this.dataMap.get(value);
        if (entry != null)
        {
            final double currentMass = entry.mass;
            final double newMass = currentMass - mass;

            // If there's no mass left, then remove from the Map and clear
            // the previous mass from the total.
            if (newMass <= 0.0)
            {
                this.totalMass -= currentMass;
                this.dataMap.remove(value);
            }
            else
            {
                entry.mass = newMass;
                this.totalMass -= mass;
            }
        }
    }

    public double getMass(
        DataType input)
    {
        final Entry entry = this.dataMap.get(input);
        if (entry == null)
        {
            return 0.0;
        }
        else
        {
            return entry.mass;
        }
    }
    

    /**
     * Adds another point mass distribution to this one.
     *
     * @param other
     *      The other distribution to add.
     */
    public void plusEquals(
        final PointMassDistribution<DataType> other)
    {
        for (DataType value : other.getDomain())
        {
            this.add(value, other.getMass(value));
        }
    }

    /**
     * Scales this distribution by the given scale factor.
     *
     * @param scaleFactor
     *     The scale factor to use.
     */
    public void scaleEquals(
        final double scaleFactor)
    {
        if (scaleFactor < 0)
        {
            throw new IllegalArgumentException("scaleFactor cannot be negative.");
        }
        
        for (Entry entry : this.dataMap.values())
        {
            entry.mass *= scaleFactor;
        }
        this.totalMass *= scaleFactor;
    }

    /**
     * Gets the fraction of mass for a given value. It is the mass assigned to
     * the point divided by the total mass. If the total mass is zero, then
     * zero will be returned.
     *
     * @param   input
     *      The value to get the fraction of mass for.
     * @return
     *      The percent of the mass for the value, which will be a value
     *      between 0.0 (inclusive) and 1.0 (inclusive).
     */
    public double getFraction(
        final DataType input)
    {
        if (this.totalMass == 0.0)
        {
            // Don't want to divide-by-zero.
            return 0.0;
        }
        else
        {
            return this.getMass(input) / this.totalMass;
        }
    }

    /**
     * Gets the maximum mass assigned to any one point.
     *
     * @return
     *      The maximum mass. It will be zero if there is no maximum.
     */
    public double getMaximumMass()
    {
        // Go through all the counts to find the maximum.
        double max = 0.0;
        for (Entry entry : this.dataMap.values())
        {
            final double mass = entry.mass;
            if (mass > max)
            {
                max = mass;
            }
        }
        return max;
    }

    /**
     * Gets the value with the maximum mass. If there is a tie, the first such
     * value encountered is returned.
     *
     * @return
     *      The maximum value.
     */
    public DataType getMaximumValue()
    {
        // Go through all the entries to find the (first) value with the
        // maximum mass.
        DataType value = null;
        double max = 0.0;
        for (Map.Entry<DataType, Entry> entry : this.dataMap.entrySet())
        {
            final double mass = entry.getValue().mass;
            if (mass > max)
            {
                value = entry.getKey();
                max = mass;
            }
        }
        return value;
    }

    /**
     * Gets all values whose mass is exactly equal to the maximum mass.
     *
     * @return
     *      The list of all the values whose mass is exactly equal to the
     *      maximum.
     */
    public LinkedList<DataType> getMaximumValues()
    {
        return this.getMaximumValues(0.0);
    }

    /**
     * Gets all the values whose mass is equal to the maximum mass, with the
     * given double used as the effective value of zero.
     *
     * @param   effectiveZero
     *      The effective value for zero. Used to deal with issues regarding
     *      numeric roundoff. It must be greater than or equal to zero. If it
     *      is zero, then strict equality is used. It is typically either zero
     *      or a very small value that is greater than zero but much smaller
     *      than one.
     * @return
     *      The list of all the values whose mass is at least effectiveZero
     *      close to the maximum mass.
     */
    public LinkedList<DataType> getMaximumValues(
        final double effectiveZero)
    {
        // Get the maximum mass. We subtract the effective zero to do a fuzzy
        // maximum.
        final double max = this.getMaximumMass() - effectiveZero;

        // Go through all the values and get the ones whose mass equal the
        // maximum, using the effective zero.
        final LinkedList<DataType> result = new LinkedList<DataType>();
        for (Map.Entry<DataType, Entry> entry : this.dataMap.entrySet())
        {
            final double mass = entry.getValue().mass;
            if (mass >= max)
            {
                result.add(entry.getKey());
            }
        }

        return result;
    }

    public Collection<? extends DataType> getDomain()
    {
        return this.dataMap.keySet();
    }

    @Override
    public int getDomainSize()
    {
        return this.getDomain().size();
    }

    public MapBasedPointMassDistribution.PMF<DataType> getProbabilityFunction()
    {
        return new MapBasedPointMassDistribution.PMF<DataType>(this);
    }

    /**
     * Getter for dataMap.
     * @return
     * The map of values to mass.
     */
    protected Map<DataType, Entry> getDataMap()
    {
        return this.dataMap;
    }

    /**
     * Setter for dataMap
     * @param dataMap
     * The map of values to mass.
     */
    protected void setDataMap(
        Map<DataType, Entry> dataMap)
    {
        this.dataMap = dataMap;
    }

    /**
     * Setter for totalMass
     * @param totalMass
     * Total mass of data points in the distribution.
     */
    protected void setTotalMass(
        double totalMass)
    {
        this.totalMass = totalMass;
    }

    public double getTotalMass()
    {
        return this.totalMass;
    }

    @Override
    public String toString()
    {

        int N = this.getDomain().size();
        StringBuilder retval = new StringBuilder(N * 100);
        retval.append("Point mass distribution has " + N + " particles and " +
            this.getTotalMass() + " total mass:\n");
        for (DataType value : this.getDomain())
        {
            retval.append(value.toString());
            retval.append(": ");
            retval.append(this.getMass(value));
            retval.append(" (");
            retval.append(this.getFraction(value));
            retval.append(")");
            retval.append("\n");
        }

        return retval.toString();

    }

    /**
     * An entry in the map. Wraps a double so that it can be updated
     * incrementally without creating a whole bunch of new Double objects.
     */
    protected static class Entry
        extends AbstractCloneableSerializable
    {
// TODO: If we ever create a mutable double object, use that here instead of
// this special entry object.
// -- jdbasil (2010-01-30)

        /** The mass stored in the entry. */
        protected double mass;

        /**
         * Creates a new entry with zero mass.
         */
        protected Entry()
        {
            this(0.0);
        }

        /**
         * Creates a new entry with the given mass.
         *
         * @param   mass
         *      The mass.
         */
        protected Entry(
            final double mass)
        {
            super();

            this.mass = mass;
        }

        /**
         * Creates a new entry by copying the mass from the given entry.
         *
         * @param   other
         *      The other entry.
         */
        protected Entry(
            final Entry other)
        {
            this(other.mass);
        }

        /**
         * Gets the mass value.
         *
         * @return
         *      The mass value.
         */
        protected double getMass()
        {
            return this.mass;
        }

        /**
         * Sets the mass value.
         *
         * @param   mass
         *      The mass value.
         */
        protected void setMass(
            final double mass)
        {
            this.mass = mass;
        }

    }

    /**
     * PMF of the MapBasedPointMassDistribution
     * @param <DataType> Type of Data in the distribution
     */
    public static class PMF<DataType>
        extends MapBasedPointMassDistribution<DataType>
        implements PointMassDistribution.PMF<DataType>
    {

        /**
         * Default constructor.
         */
        public PMF()
        {
            super();
        }

        /**
         * Creates a new instance of PMF
         * @param initialCapacity
         *      The initial domain capacity. Must be positive.
         */
        public PMF(
            int initialCapacity )
        {
            super( initialCapacity );
        }

        /**
         * Copy constructor
         * @param other
         * MapBasedPointMassDistribution to copy.
         */
        public PMF(
            MapBasedPointMassDistribution<DataType> other)
        {
            super(other);
        }

        @Override
        public MapBasedPointMassDistribution.PMF<DataType> clone()
        {
            return (MapBasedPointMassDistribution.PMF<DataType>) super.clone();
        }

        public double getEntropy()
        {
            return ProbabilityMassFunctionUtil.getEntropy(this);
        }

        public Double evaluate(
            DataType input)
        {
            if (this.getTotalMass() > 0.0)
            {
                return this.getMass(input) / this.getTotalMass();
            }
            else
            {
                return 0.0;
            }
        }

        public double logEvaluate(
            DataType input)
        {
            return Math.log( this.evaluate(input) );
        }

        @Override
        public MapBasedPointMassDistribution.PMF<DataType> getProbabilityFunction()
        {
            return this;
        }

    }


    /**
     * Creates a MapBasedPointMassDistribution.PMF from the
     * @param <DataType>
     */
    public static class Learner<DataType>
        extends AbstractCloneableSerializable
        implements DistributionWeightedEstimator<DataType,MapBasedPointMassDistribution.PMF<DataType>>
    {

        /**
         * Default constructor.
         */
        public Learner()
        {
        }

        public MapBasedPointMassDistribution.PMF<DataType> learn(
            Collection<? extends WeightedValue<? extends DataType>> data)
        {

            MapBasedPointMassDistribution.PMF<DataType> retval =
                new MapBasedPointMassDistribution.PMF<DataType>();

            for( WeightedValue<? extends DataType> wv : data )
            {
                retval.add( wv.getValue(), wv.getWeight() );
            }

            return retval;
            
        }

    }

}

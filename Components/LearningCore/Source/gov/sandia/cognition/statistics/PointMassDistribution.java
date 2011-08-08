/*
 * File:                PointMassDistribution.java
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

package gov.sandia.cognition.statistics;

/**
 * A distribution made up of discrete data points, each with its own associated
 * real-valued mass.
 * @param <DataType> Type of data in the distribution.
 * @author Kevin R. Dixon
 * @since 3.0
 */
public interface PointMassDistribution<DataType>
    extends DiscreteDistribution<DataType>
{

    /**
     * Adds one of the given value to the distribution.
     *
     * @param  value The value to add.
     */
    void add(
        DataType value);

    /**
     * Adds the given value with the specific mass to the distribution.
     *
     * @param  value The value to add.
     * @param  mass The mass of the value to add.
     */
    void add(
        DataType value,
        double mass );

// TODO: These add and remove methods are not symmetric, as their names would
// imply. Either add should be changed to addOne or increment or remove to
// removeAll or removeFromDomain or something.
// --jdbasil (2010-01-30)
    /**
     * Removes all the mass of the given value from the distribution.
     *
     * @param  value The value to remove.
     */
    void remove(
        DataType value );

    /**
     * Removes the given value and its mass from the distribution.
     *
     * @param  value The value to remove.
     * @param  mass The mass of the value to remove.
     */
    void remove(
        DataType value,
        double mass );

    /**
     * Sets the mass for a given value. The mass cannot be negative. If the
     * mass is zero, the value may be removed from the domain, depending
     * on the implementation of the distribution.
     *
     * @param   value
     *      The value to set the mass of.
     * @param   mass
     *      The mass to assign to the value. Cannot be negative.
     */
    void setMass(
        DataType value,
        double mass);

    /**
     * Gets the mass associated with the given value.
     *
     * @param  input The value to get the mass of.
     * @return The mass associated with the input.
     */
    double getMass(
        DataType input);

    /**
     * Gets the total mass of all values in the distribution.
     *
     * @return The total mass of all values.
     */
    double getTotalMass();

    /**
     * Removes all of the elements from the Distribution.
     */
    void clear();

    
    PointMassDistribution.PMF<DataType> getProbabilityFunction();

    /**
     * A PMF defined over a PointMassDistribution.
     * @param <DataType>
     * Type of data in the PointMassDistribution.
     */
    public static interface PMF<DataType>
        extends PointMassDistribution<DataType>,
        ProbabilityMassFunction<DataType>
    {
    }

}

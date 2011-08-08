/*
 * File:                DefaultWeightedValueDiscriminant.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 03, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.util.DefaultWeightedValue;
import gov.sandia.cognition.util.WeightedValue;

/**
 * An implementation of {@code ValueDiscriminantPair} that stores a double
 * as the discriminant. It extends {@code DefaultWeightedValue} and thus also
 * is a {@code WeightedValue}. This may be the most generally used
 * implementation of the {@code ValueDiscriminantPair} since it covers the
 * common case where the discriminant is a real value.
 *
 * @param   <ValueType>
 *      The type of value to discriminate between.
 * @author  Justin Basilico
 * @since   3.1
 */
public class DefaultWeightedValueDiscriminant<ValueType>
    extends DefaultWeightedValue<ValueType>
    implements ValueDiscriminantPair<ValueType, Double>
{
    /** The default weight is {@value}. */
    public static final double DEFAULT_WEIGHT = 0.0;

    /**
     * Creates a {@code DefaultWeightedValueDiscriminant} with a null value
     * and a weight of 0.0.
     */
    public DefaultWeightedValueDiscriminant()
    {
        this(null, DEFAULT_WEIGHT);
    }

    /**
     * Creates a {@code DefaultWeightedValueDiscriminant} with the given
     * value and weight.
     *
     * @param   value
     *      The value for the pair.
     * @param   weight
     *      The weight that is to be used as the discriminant.
     */
    public DefaultWeightedValueDiscriminant(
        final ValueType value,
        final double weight)
    {
        super(value, weight);
    }

    /**
     * Creates a new {@code DefaultWeightedValueDiscriminant} whose weight
     * and value are taken from the given weighted value.
     *
     * @param   other
     *      The other weighted value to make a shallow copy of.
     */
    public DefaultWeightedValueDiscriminant(
        final WeightedValue<? extends ValueType> other)
    {
        this(other.getValue(), other.getWeight());
    }

    @Override
    public Double getDiscriminant()
    {
        return this.getWeight();
    }

    @Override
    public ValueType getFirst()
    {
        return this.getValue();
    }

    @Override
    public Double getSecond()
    {
        return this.getWeight();
    }

    /**
     * Convenience method for creating a new
     * {@code DefaultWeightedValueDiscriminant} with the given value and weight.
     *
     * @param   <ValueType>
     *      The type of value to discriminate between.
     * @param   value
     *      The value for the pair.
     * @param   weight
     *      The weight that is to be used as the discriminant.
     * @return
     *      A new discriminant object.
     */
    public static <ValueType> DefaultWeightedValueDiscriminant<ValueType> create(
        final ValueType value,
        final double weight)
    {
        return new DefaultWeightedValueDiscriminant<ValueType>(value, weight);
    }

    /**
     * Convenience method for creating a new
     * {@code DefaultWeightedValueDiscriminant} with a shallow copy of the given
     * the given value and weight.
     *
     * @param   <ValueType>
     *      The type of value to discriminate between.
     * @param   other
     *      The other value to make a shallow copy of.
     * @return
     *      A new discriminant object.
     */
    public static <ValueType> DefaultWeightedValueDiscriminant<ValueType> create(
        final WeightedValue<? extends ValueType> other)
    {
        return new DefaultWeightedValueDiscriminant<ValueType>(
            other.getValue(), other.getWeight());
    }
    
}

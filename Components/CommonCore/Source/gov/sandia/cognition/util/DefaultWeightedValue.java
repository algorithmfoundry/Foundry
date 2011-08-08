/*
 * File:                DefaultWeightedValue.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright June 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.Comparator;

/**
 * The {@code WeightedValue} class implements a simple generic container
 * that holds a value and a weight assigned to the value.
 *
 * @param   <ValueType> Type of the value contained in the class
 * @author  Justin Basilico
 * @since   3.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2007-11-25",
    changesNeeded=false,
    comments="Looks fine."
)
public class DefaultWeightedValue<ValueType>
    extends AbstractWeighted
    implements WeightedValue<ValueType>
{
    
    /** The value. */
    protected ValueType value;

    /**
     * Creates a new instance of {@code WeightedValue}. The weight defaults to
     * {@value gov.sandia.cognition.util.AbstractWeighted#DEFAULT_WEIGHT} and
     * the value defaults to null.
     */
    public DefaultWeightedValue()
    {
        this((ValueType) null);
    }

    /**
     * Creates a new instance of  {@code WeightedValue} with the given value
     * and a default weight of
     * {@value gov.sandia.cognition.util.AbstractWeighted#DEFAULT_WEIGHT}.
     *
     * @param  value The value.
     */
    public DefaultWeightedValue(
        final ValueType value)
    {
        this(value, DEFAULT_WEIGHT);
    }

    /**
     * Creates a new instance of {@code WeightedValue}.
     *
     * @param  weight The weight.
     * @param  value The value.
     */
    public DefaultWeightedValue(
        final ValueType value,
        final double weight)
    {
        super(weight);

        this.setValue(value);
    }

    /**
     * Creates a new shallow copy of a  {@code WeightedValue}.
     *
     * @param  other The  {@code WeightedValue} to shallow copy.
     */
    public DefaultWeightedValue(
        final WeightedValue<? extends ValueType> other)
    {
        this(other.getValue(), other.getWeight());
    }

    /**
     * Creates a shallow copy of the WeightedValue. The weight is copied but
     * the value is not.
     *
     * @return A new shallow copy of this  {@code WeightedValue}.
     */
    @Override
    public DefaultWeightedValue<ValueType> clone()
    {
        @SuppressWarnings("unchecked")
        final DefaultWeightedValue<ValueType> clone =
            (DefaultWeightedValue<ValueType>) super.clone();
        clone.setValue(ObjectUtil.cloneSmart(this.getValue()));
        return clone;
    }

    /**
     * Gets the value.
     *
     * @return The value.
     */
    public ValueType getValue()
    {
        return this.value;
    }

    /**
     * Sets the value.
     *
     * @param  value The value.
     */
    public void setValue(
        final ValueType value)
    {
        this.value = value;
    }

    /**
     * Convenience method to create a new {@code WeightedValue}.
     *
     * @param <ValueType>
     *      The type of the value.
     * @param value
     *      The value.
     * @param weight
     *      The weight.
     * @return
     *      A new weighted value with the given value and weight.
     */
    public static <ValueType> DefaultWeightedValue<ValueType> create(
        final ValueType value,
        final double weight)
    {
        return new DefaultWeightedValue<ValueType>(value, weight);
    }

    /**
     * A comparator for weighted values based on the weight.
     */
    public static class WeightComparator
        extends AbstractCloneableSerializable
        implements Comparator<WeightedValue<?>>
    {
        /** An instance of the class. Access through getInstance(). */
        private static WeightComparator INSTANCE = null;

        /**
         * Gets a static instance of this class.
         *
         * @return
         *      A static instance of this class.
         */
        public static WeightComparator getInstance()
        {
            if (INSTANCE == null)
            {
                INSTANCE = new WeightComparator();
            }

            return INSTANCE;
        }

        /**
         * Creates a new {@code WeightComarator}.
         */
        public WeightComparator()
        {
            super();
        }

        public int compare(
            final WeightedValue<?> first,
            final WeightedValue<?> second)
        {
            return Double.compare(first.getWeight(), second.getWeight());
        }

    }
    
}

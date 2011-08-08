/*
 * File:                DefaultValueDiscriminantPair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 02, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.util.ObjectUtil;

/**
 * A default implementation of the {@code ValueDiscriminantPair} interface.
 * Stores the value and discriminant values as fields.
 *
 * @param   <ValueType>
 *      The general value stored in the pair.
 * @param   <DiscriminantType>
 *      The discriminant comparable object used for ordering objects.
 * @author  Justin Basilico
 * @since   3.1
 */
public class DefaultValueDiscriminantPair<ValueType, DiscriminantType extends Comparable<? super DiscriminantType>>
    extends AbstractValueDiscriminantPair<ValueType, DiscriminantType>
{
    /** The value. */
    protected ValueType value;

    /** The discriminant. */
    protected DiscriminantType discriminant;

    /**
     * Creates a new {@code DefaultValueDiscriminantPair} with null value and
     * discriminant.
     */
    public DefaultValueDiscriminantPair()
    {
        this(null, null);
    }

    /**
     * Creates a new {@code DefaultValueDiscriminantPair} with the given value
     * and discriminant.
     *
     * @param   value
     *      The value.
     * @param   discriminant
     *      The discriminant.
     */
    public DefaultValueDiscriminantPair(
        final ValueType value,
        final DiscriminantType discriminant)
    {
        super();

        this.setValue(value);
        this.setDiscriminant(discriminant);
    }

    @Override
    public DefaultValueDiscriminantPair<ValueType, DiscriminantType> clone()
    {
        @SuppressWarnings("unchecked")
        final DefaultValueDiscriminantPair<ValueType, DiscriminantType> clone =
            (DefaultValueDiscriminantPair<ValueType, DiscriminantType>)
            super.clone();

        clone.value = ObjectUtil.cloneSmart(this.value);
        clone.discriminant = ObjectUtil.cloneSmart(this.discriminant);

        return clone;
    }

    @Override
    public ValueType getValue()
    {
        return this.value;
    }

    /**
     * Sets the value.
     *
     * @param   value
     *      The value.
     */
    public void setValue(
        final ValueType value)
    {
        this.value = value;
    }

    @Override
    public DiscriminantType getDiscriminant()
    {
        return this.discriminant;
    }

    /**
     * Sets the discriminant.
     *
     * @param   discriminant
     *      The discriminant.
     */
    public void setDiscriminant(
        final DiscriminantType discriminant)
    {
        this.discriminant = discriminant;
    }


    /**
     * Convenience method for creating a new
     * {@code DefaultValueDiscriminantPair} with the given value and
     * discriminant.
     *
     * @param   <ValueType>
     *      The general value stored in the pair.
     * @param   <DiscriminantType>
     *      The discriminant comparable object used for ordering objects.
     * @param   value
     *      The value.
     * @param   discriminant
     *      The discriminant.
     * @return
     *      A new {@code DefaultValueDiscriminantPair}.
     */
    public static <ValueType, DiscriminantType extends Comparable<? super DiscriminantType>> DefaultValueDiscriminantPair<ValueType, DiscriminantType> create(
        final ValueType value,
        final DiscriminantType discriminant)
    {
        return new DefaultValueDiscriminantPair<ValueType, DiscriminantType>(
            value, discriminant);
    }
}

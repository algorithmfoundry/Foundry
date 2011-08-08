/*
 * File:                DefaultIdentifiedValue.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 25, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

/**
 * A default implementation of the {@code IdentifiedValue} interface that
 * stores a value along with its identifier.
 *
 * @param   <IdentifierType>
 *      The type of identifier for the value. Must implement valid equals
 *      and hashCode methods.
 * @param   <ValueType>
 *      The type of value stored with the identifier.
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class DefaultIdentifiedValue<IdentifierType, ValueType>
    extends AbstractCloneableSerializable
    implements IdentifiedValue<IdentifierType, ValueType>
{

    /** The identifier for the value. */
    protected IdentifierType identifier;

    /** The value. */
    protected ValueType value;

    /**
     * Creates a new {@code DefaultIdentifiedValue} with null identifier and
     * value.
     */
    public DefaultIdentifiedValue()
    {
        this(null, null);
    }

    /**
     * Creates a new {@code DefaultIdentifiedValue} with the given identifier
     * and value.
     *
     * @param   identifier
     *      The identifier for the value.
     * @param   value
     *      The value.
     */
    public DefaultIdentifiedValue(
        final IdentifierType identifier,
        final ValueType value)
    {
        super();

        this.identifier = identifier;
        this.value = value;
    }

    @Override
    public IdentifierType getIdentifier()
    {
        return this.identifier;
    }

    /**
     * Sets the identifier.
     *
     * @param   identifier
     *      The identifier for the value.
     */
    public void setIdentifier(
        final IdentifierType identifier)
    {
        this.identifier = identifier;
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
     *      The value associated with the identifier.
     */
    public void setValue(
        final ValueType value)
    {
        this.value = value;
    }

    /**
     * Convenience method to create a new, empty {@code DefaultIdentifiedValue}.
     *
     * @param   <IdentifierType>
     *      The type of identifier for the value. Must implement valid equals
     *      and hashCode methods.
     * @param   <ValueType>
     *      The type of value stored.
     * @return
     *      A new, empty {@code DefaultIdentifiedValue}.
     */
    public static <IdentifierType, ValueType> DefaultIdentifiedValue<IdentifierType, ValueType> create()
    {
        return new DefaultIdentifiedValue<IdentifierType, ValueType>();
    }

    /**
     * Creates a new {@code DefaultIdentifiedValue} with the given identifier
     * and value.
     *
     * @param   <IdentifierType>
     *      The type of identifier for the value. Must implement valid equals
     *      and hashCode methods.
     * @param   <ValueType>
     *      The type of value stored.
     * @param   identifier
     *      The identifier for the value.
     * @param   value
     *      The value.
     * @return
     *      A new {@code DefaultIdentifiedValue} with the given identifier and
     *      value.
     */
    public static <IdentifierType, ValueType> DefaultIdentifiedValue<IdentifierType, ValueType> create(
        final IdentifierType identifier,
        final ValueType value)
    {
        return new DefaultIdentifiedValue<IdentifierType, ValueType>(identifier, value);
    }


}

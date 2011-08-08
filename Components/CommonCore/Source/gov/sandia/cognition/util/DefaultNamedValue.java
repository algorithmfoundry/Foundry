/*
 * File:                DefaultNamedValue.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 12, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.annotation.CodeReview;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The {@code DefaultNamedValue} class implements a container of a name-value
 * pair.
 *
 * @param <ValueType> Type of value contained in the class
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-10-02",
            changesNeeded=false,
            comments={
                "Moved previous code review to CodeReview annotation.",
                "Otherwise, looks fine."
            }
        )
        ,
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2007-11-25",
            changesNeeded=false,
            comments="Looks fine."
        )
    }
)
public class DefaultNamedValue<ValueType>
    extends AbstractNamed
    implements NamedValue<ValueType>
{

    /** The value. */
    protected ValueType value;

    /**
     * Creates a new instance of {@code DefaultNamedValue}. The name and value both 
     * default to null.
     */
    public DefaultNamedValue()
    {
        this(null, null);
    }

    /**
     * Creates a new instance of DefaultNamedValue from the given name and value.
     *
     * @param  name The name.
     * @param  value The value.
     */
    public DefaultNamedValue(
        final String name,
        final ValueType value)
    {
        super(name);

        this.setValue(value);
    }

    /**
     * Creates a shallow copy of the given DefaultNamedValue.
     *
     * @param  other The other DefaultNamedValue to shallow copy.
     */
    public DefaultNamedValue(
        final DefaultNamedValue<? extends ValueType> other)
    {
        this(other.getName(), other.getValue());
    }

    @Override
    public DefaultNamedValue<ValueType> clone()
    {
        @SuppressWarnings("unchecked")
        DefaultNamedValue<ValueType> clone =
            (DefaultNamedValue<ValueType>) super.clone();
        clone.setValue( ObjectUtil.cloneSmart( this.getValue() ) );
        return clone;
    }

    @Override
    public void setName(
        final String name)
    {
        super.setName(name);
    }

    /**
     * Gets the value stored in the name-value pair.
     *
     * @return The value.
     */
    public ValueType getValue()
    {
        return this.value;
    }

    /**
     * Sets the value stored in the name-value pair.
     *
     * @param value The value.
     */
    public void setValue(
        final ValueType value)
    {
        this.value = value;
    }

    /**
     * Convenience method for creating an new {@code DefaultNamedValue}.
     * 
     * @param   <T>
     *      The value type.
     * @param   name
     *      The name.
     * @param   value
     *      The value.
     * @return
     *      A new default named value with the given name and value.
     */
    public static <T> DefaultNamedValue<T> create(
        final String name,
        final T value)
    {
        return new DefaultNamedValue<T>(name, value);
    }

    /**
     * Creates a list of named values from a collection of named objects. This
     * is useful for cases where objects may have a name but that name is not
     * the toString representation of the object but you want to treat it as
     * such, by using {@code DefaultNamedValue}. This new list of
     * DefaultNamedValues can then be used to represent the values by their
     * name via toString. This is useful in some GUI applications.
     *
     * Note that this is not meant to be used with existing DefaultNamedValue
     * objects because it will just end up putting another DefaultNamedValue
     * wrapper around it.
     *
     * @param   <T> The value type.
     * @param   values The values to created a named-value list from.
     * @return  A new list of named-values with the name of the object as the
     *      name and the value as the value.
     */
// TODO: Move this somewhere else. -- jdbasil 
    public static <T extends Named> ArrayList<DefaultNamedValue<T>> createNamedValuesList(
        final Collection<T> values)
    {
        // The result collection will be of the same size.
        final ArrayList<DefaultNamedValue<T>> result = 
            new ArrayList<DefaultNamedValue<T>>(values.size());
        
        // Go through all the values and add them.
        for (T value : values)
        {
            result.add(new DefaultNamedValue<T>(value.getName(), value));
        }
        
        // Return the resulting list.
        return result;
    }
}

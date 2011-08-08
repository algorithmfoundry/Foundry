/*
 * File:                NamedValue.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Feb 27, 2008, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.util;

/**
 * The {@code NamedValue} class describes a name-value pair.
 *
 * @param <ValueType> Type of value contained in the class
 *
 * @author Kevin R. Dixon
 * @since 2.1
 */
public interface NamedValue<ValueType>
    extends Named
{

    /**
     * Gets the value stored in the name-value pair.
     *
     * @return The value.
     */
    public ValueType getValue();

}

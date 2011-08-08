/*
 * File:                TemporalValue.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 28, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

/**
 * The {@code TemporalValue} interface defines the interface for a container
 * that has a temporal coordinate plus a stored value.
 * 
 * @param   <ValueType> The type of the value object.
 * @author  Justin Basilico
 * @since   2.1
 */
public interface TemporalValue<ValueType>
    extends Temporal
{
    /**
     * Gets the value stored in this object.
     * 
     * @return  The value.
     */
    ValueType getValue();
}

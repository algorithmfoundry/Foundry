/*
 * File:                WeightedValue.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.util;

/**
 * Interface for a wrapper for a value that associates a weight with it.
 *
 * @param   <ValueType> Type of the value contained in the class
 * @author  Justin Basilico
 * @since   2.0
 */
public interface WeightedValue<ValueType>
    extends Weighted
{

    /**
     * Gets the value.
     *
     * @return The value.
     */
    public ValueType getValue();

}

/*
 * File:                IdentifiedValue.java
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
 * Interface for a pairing of an identifier and its associated value.
 *
 * @param   <IdentifierType>
 *      The type of the identifier.
 * @param   <ValueType>
 *      The type of the value.
 * @author  Justin Basilico
 * @since   3.3.0
 */
public interface IdentifiedValue<IdentifierType, ValueType>
    extends Identified<IdentifierType>
{

    /**
     * Gets the value associated with the identifier.
     *
     * @return
     *      The value associated with the identifier.
     */
    public ValueType getValue();
    
}

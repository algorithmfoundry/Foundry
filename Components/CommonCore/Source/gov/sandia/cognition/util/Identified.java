/*
 * File:                Identified.java
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
 * Defines functionality for an object that has some type of identifier.
 * Identifiers must have valid implementations of equals and hashCode.
 *
 * @param   <IdentifierType>
 *      The type of identifier for this object. Must have valid implementations
 *      of equals and hashCode.
 * @author  Justin Basilico
 * @since   3.1.2
 */
public interface Identified<IdentifierType>
{

    /**
     * Gets the identifier for this object.
     * 
     * @return
     *      The identifier for this object.
     */
    public IdentifierType getIdentifier();
    
}

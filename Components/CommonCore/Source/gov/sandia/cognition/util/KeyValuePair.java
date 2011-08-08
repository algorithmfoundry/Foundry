/*
 * File:                KeyValuePair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright November 28, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. Export
 * of this program may require a license from the United States Government.
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

/**
 * Represents a key-value pair. The key is the first element in the pair and
 * the value is the second.
 *
 * @param   <KeyType>
 *      The type of the key.
 * @param   <ValueType>
 *      The type of the value.
 * @author  Justin Basilico
 * @since   3.1
 */
public interface KeyValuePair<KeyType, ValueType>
    extends Pair<KeyType, ValueType>
{

    /**
     * Gets the key part of the key-value pair.
     * 
     * @return
     *      The key.
     */
    public KeyType getKey();

    /**
     * Gets the value part of the key-value pair.
     *
     * @return
     *      The value.
     */
    public ValueType getValue();

    
}

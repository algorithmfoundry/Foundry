/*
 * File:                Pair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 12, 2007, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 *
 * 
 */

package gov.sandia.cognition.util;

import java.io.Serializable;

/**
 * The {@code Pair} interface defines the functionality of an object that 
 * contains a pair of other objects inside of it.
 *
 * @param   <FirstType> Type of the first object in the pair.
 * @param   <SecondType> Type of the second object in the pair.
 * @author  Justin Basilico
 * @since   2.0
 */
public interface Pair<FirstType, SecondType>
    extends Serializable
{

    /**
     * Gets the first object.
     *
     * @return The first object.
     */
    FirstType getFirst();

    /**
     * Gets the second object.
     *
     * @return The second object.
     */
    SecondType getSecond();

}

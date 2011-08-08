/*
 * File:                Triple.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 26, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.util;

/**
 * The {@code Triple} interface defines the functionality of an object that
 * contains a three other objects inside of it. This may also be known as a 
 * triplet.
 * 
 * @param   <FirstType> Type of the first object in the triple.
 * @param   <SecondType> Type of the second object in the triple.
 * @param   <ThirdType> Type of the third object in thet triple.
 * @author  Justin Basilico
 * @since   2.1
 */
public interface Triple<FirstType, SecondType, ThirdType>
{
    
    /**
     * Gets the first object.
     * 
     * @return The first object.
     */
    public FirstType getFirst();
    
    /**
     * Gets the second object.
     * 
     * @return The second object.
     */
    public SecondType getSecond();
    
    /**
     * Gets the third object.
     * 
     * @return The third object.
     */
    public ThirdType getThird();
    
}

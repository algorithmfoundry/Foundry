/*
 * File:                ValueDiscriminantPair.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 02, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.data;

import gov.sandia.cognition.util.Pair;

/**
 * Interface for a pair of a value and a discriminant for ordering instances
 * that have the same value.
 *
 * @param   <ValueType>
 *      The general value stored in the pair.
 * @param   <DiscriminantType>
 *      The discriminant comparable object used for ordering objects.
 * @author  Justin Basilico
 * @since   3.1
 */
public interface ValueDiscriminantPair<ValueType, DiscriminantType extends Comparable<? super DiscriminantType>>
    extends Pair<ValueType, DiscriminantType>
{
    
    /**
     * Gets the value in the pair.
     *
     * @return
     *      The value in the pair.
     */
    public ValueType getValue();

    /**
     * Gets the discriminant for ordering instances of the same value.
     *
     * @return
     *      The discriminant in the pair.
     */
    public DiscriminantType getDiscriminant();
    
}

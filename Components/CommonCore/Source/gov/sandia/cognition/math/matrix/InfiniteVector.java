/*
 * File:                InfiniteVector.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Apr 15, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.collection.ScalarMap;

/**
 * A Vector that has a potentially infinite number of indices (keys), but will
 * only contain a countable number in any instance.
 *
 * @param   <KeyType>
 *      The type of the keys (indices) into the infinite dimensional vector.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   3.2.1
 */
public interface InfiniteVector<KeyType>
    extends VectorSpace<InfiniteVector<KeyType>,InfiniteVector.Entry<KeyType>>,
    ScalarMap<KeyType>
{

    /**
     * Removes the zero elements from the vector.
     */
    public void compact();

    /**
     * Entry for a InfiniteVector
     * @param <KeyType>
     */
    public static interface Entry<KeyType>
        extends ScalarMap.Entry<KeyType>,
        VectorSpace.Entry
    {
    }

}

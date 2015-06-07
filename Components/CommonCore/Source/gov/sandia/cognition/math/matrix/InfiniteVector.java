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
     * Applies the given function to each active entry in this vector. This can
     * be faster than looping over the entries using an iterator.
     * 
     * @param   consumer 
     *      The consumer for the entries. It is called for each active entry in
     *      the vector, in order by increasing index.
     */
    public void forEachEntry(
        final KeyValueConsumer<? super KeyType> consumer);
        
    /**
     * Applies the given function to each non-zero entry in this vector. This 
     * can be faster than looping over the entries using an iterator.
     * 
     * @param   consumer 
     *      The consumer for the non-zero entries. It is called for each  
     *      non-zero entry in the vector, in order by increasing index.
     */
    public void forEachNonZero(
        final KeyValueConsumer<? super KeyType> consumer);
    
    /**
     * Entry for a InfiniteVector
     * @param <KeyType>
     */
    public static interface Entry<KeyType>
        extends ScalarMap.Entry<KeyType>,
        VectorSpace.Entry
    {
    }

    /**
     * Defines the functionality for a consumer of vector entries, which are an
     * index and a value. Typically this interface is used in conjunction with
     * the Vector forEachEntry method.
     *
     * @since   3.4.2
     * @param   <KeyType> 
     *      The type of key in the infinite vector.
     */
    public interface KeyValueConsumer<KeyType>
    {

        /**
         * Consumes one entry in the infinite vector.
         * 
         * @param   key
         *      The key.
         * @param   value 
         *      The value for that key in the vector.
         */
        public void consume(
            final KeyType key,
            final double value);

    }
}

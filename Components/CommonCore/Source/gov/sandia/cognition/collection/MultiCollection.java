/*
 * File:                MultiCollection.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright May 12, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.collection;

import java.util.Collection;

/**
 * An interface for a collection that is made up of a group of subcollections.
 *
 * @param   <T> The type of the data in the collection.
 * @author  Justin Basilico
 * @since   3.0
 */
public interface MultiCollection<T>
    extends Collection<T>
{
    /**
     * Returns the sub-collections of the multi-collection.
     *
     * @return
     *      The sub-collection of the multi-collection.
     */
    public Collection<? extends Collection<T>> subCollections();

    /**
     * Gets the number of sub-collections in the multi-collection.
     * @return
     * Number of sub-collections in the multi-collection.
     */
    public int getSubCollectionsCount();
    
}

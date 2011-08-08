/*
 * File:                DefaultMultiCollection.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 24, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.collection;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The {@code DefaultMultiCollection} class implements a {@code Collection} that just
 * contains a set of internal collections inside. This allows for easy 
 * operations on as set of collections on the same object type.
 *
 * @param <EntryType> Type of item stored in this class
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReviews(
    reviews =
    {
        @CodeReview(
            reviewer = "Kevin R. Dixon",
            date = "2008-02-08",
            changesNeeded = false,
            comments = "Looks fine."
        )
        ,
        @CodeReview(
            reviewer = "Kevin R. Dixon",
            date = "2007-12-10",
            changesNeeded = false,
            comments = "Minor updates to the javadoc."
        )
        ,
        @CodeReview(
            reviewer = "Kevin R. Dixon",
            date = "2006-07-18",
            changesNeeded = false,
            comments = "Looks fine."
        )
    }
)
public class DefaultMultiCollection<EntryType>
    extends AbstractCollection<EntryType>
    implements Serializable, MultiCollection<EntryType>
{

    /** The set of collections that backs this collection. */
    private List<Collection<EntryType>> collections;

    /**
     * Creates a new instance of {@code DefaultMultiCollection}.
     *
     * @param  first The first collection to add.
     * @param  second The second collection to add.
     */
    public DefaultMultiCollection(
        final Collection<EntryType> first,
        final Collection<EntryType> second)
    {
        super();

        // Create the collections list and add the two new collections.
        this.setCollections(new ArrayList<Collection<EntryType>>());
        this.collections.add(first);
        this.collections.add(second);
    }

    /**
     * Creates a new instance of {@code DefaultMultiCollection}.
     *
     * @param  collections The Collection of Collections to add.
     */
    public DefaultMultiCollection(
        final Collection<? extends Collection<EntryType>> collections)
    {
        super();

        this.setCollections(
            new ArrayList<Collection<EntryType>>(collections) );
    }

    @Override
    public boolean contains(
        Object o)
    {
        // An object is contained in this collection if any of the internal
        // collections contain it.
        for (Collection<EntryType> collection : this.collections)
        {
            if (collection.contains(o))
            {
                return true;
            }
        }

        return false;
    }

    public int size()
    {
        // Calculate the size by summing the size of the internal collections.
        int sizeSum = 0;

        for (Collection<EntryType> collection : this.collections)
        {
            sizeSum += collection.size();
        }

        return sizeSum;
    }

    public int getSubCollectionsCount()
    {
        return this.collections.size();
    }

    public Iterator<EntryType> iterator()
    {
        return new MultiIterator<EntryType>(this.collections);
    }

    public Collection<? extends Collection<EntryType>> subCollections()
    {
        return this.collections;
    }

    /**
     * Sets the internal collections.
     *
     * @param  collections The internal collections.
     */
    private void setCollections(
        final List<Collection<EntryType>> collections)
    {
        this.collections = collections;
    }

}

/*
 * File:                MultiIterator.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
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
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.CodeReviews;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * The {@code MultiIterator} class implements an iterator that iterates over a 
 * bunch of internal iterators, exhausting one before moving to the next.
 *
 * @param  <EntryType> The type for an entry in the collection.
 * @author Justin Basilico
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-08",
            changesNeeded=false,
            comments="Interface looks fine."
        ),
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2006-07-18",
            changesNeeded=true,
            comments="Non-standard use of direct-member access, instead of getters and setters. Please review.",
            response=@CodeReviewResponse(
                respondent="Justin Basilico",
                date="2006-08-17",
                moreChangesNeeded=false,
                comments="Updated comments to indicate that it does not use getters"
            )
        )
    }
)
public class MultiIterator<EntryType>
    extends Object
    implements Iterator<EntryType>,
    Serializable
{
    // Note: This class does not make use of the getters pattern because it
    // is expected to be high-performance and instead uses direct access to
    // the member variables.
    
    /** The current iterator. */
    private Iterator<EntryType> currentIterator;

    /** The iterators themselves. */
    private LinkedList<Iterator<EntryType>> remainingIterators;

    /**
     * Creates a new instance of {@code MultiIterator}.
     *
     * @param  iterables The collection of iterables to iterate using.
     */
    public MultiIterator(
        Collection<? extends Iterable<EntryType>> iterables )
    {
        super();

        // Create the list of iterators.
        LinkedList<Iterator<EntryType>> iterators =
            new LinkedList<Iterator<EntryType>>();

        for (Iterable<EntryType> iterable : iterables)
        {
            iterators.add( iterable.iterator() );
        }

        // Set the first iterator.
        Iterator<EntryType> iterator = null;
        if (!iterators.isEmpty())
        {
            iterator = iterators.removeFirst();
        }

        this.setCurrentIterator( iterator );
        this.setRemainingIterators( iterators );

        // Initialize the iterator by finding the first valid one.
        this.findNextValidIterator();
    }

    /**
     * {@inheritDoc}
     *
     * @return  {@inheritDoc}
     */
    public boolean hasNext()
    {
        // We have a next value if the current iterator is not null.
        return this.currentIterator != null;
    }

    /**
     * {@inheritDoc}
     *
     * @return  {@inheritDoc}
     */
    public EntryType next()
    {
        if (currentIterator == null)
        {
            throw new NoSuchElementException();
        }

        EntryType result = this.currentIterator.next();

        this.findNextValidIterator();

        return result;
    }

    /**
     * {@inheritDoc}
     */
    public void remove()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Moves the iterator forward, attempting to find the next valid one.
     */
    private void findNextValidIterator()
    {
        // Loop until either we find an iterator that has a next value or
        // there are no more iterators.
        while (this.currentIterator != null && !this.currentIterator.hasNext())
        {
            if (this.remainingIterators.isEmpty())
            {
                // No more iterators, so just set it to null. This will
                // break the loop.
                this.setCurrentIterator( null );
            }
            else
            {
                // Try using the next iterator as the current one.
                this.setCurrentIterator( this.remainingIterators.removeFirst() );
            }
        }
    }

    /**
     * Set the current iterators
     *
     * @param  currentIterator The new current iterator.
     */
    private void setCurrentIterator(
        Iterator<EntryType> currentIterator )
    {
        this.currentIterator = currentIterator;
    }

    /**
     * Sets the remaining iterators.
     *
     * @param remainingIterators The new remaining iterators.
     */
    private void setRemainingIterators(
        LinkedList<Iterator<EntryType>> remainingIterators )
    {
        if (remainingIterators == null)
        {
            // Error: Bad parameter.
            throw new NullPointerException(
                "The remainingIterators cannot be null." );
        }

        this.remainingIterators = remainingIterators;
    }

}

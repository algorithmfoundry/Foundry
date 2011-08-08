/*
 * File:                NGramFilter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 30, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.text.term.DefaultTermOccurrence;
import gov.sandia.cognition.text.term.DefaultTermNGram;
import gov.sandia.cognition.text.term.Term;
import gov.sandia.cognition.text.term.TermOccurrence;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * A term filter that creates an n-gram of terms.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class NGramFilter
    extends AbstractCloneableSerializable
    implements TermFilter
{
    /** The default is a bigram. */
    public static final int DEFAULT_SIZE = 2;

    /** The size of the n-gram. Also known as the value of n. */
    protected int size;

    /**
     * Creates a new {@code NGramFilter} with the default size.
     */
    public NGramFilter()
    {
        this(DEFAULT_SIZE);
    }
    
    /**
     * Creates a new {@code NGramFilter} with the given size.
     * 
     * @param   size
     *      The size of the n-grams to create. Must be greater than 1.
     */
    public NGramFilter(
        final int size)
    {
        super();
        
        this.setSize(size);
    }

    @Override
    public NGramFilter clone()
    {
        return (NGramFilter) super.clone();
    }

    public Collection<TermOccurrence> filterTerms(
        final Iterable<? extends TermOccurrence> terms)
    {
        final LinkedList<TermOccurrence> result = 
            new LinkedList<TermOccurrence>();

        // TODO: Replace this with a circular buffer to improve efficiency.
        final LinkedList<TermOccurrence> occurrencesBuffer =
            new LinkedList<TermOccurrence>();

        // We need to keep track of the array of terms we use.
        Term[] previousTerms = new Term[this.size];

        // Iterate through the term occurrences.
        final Iterator<? extends TermOccurrence> it = terms.iterator();

        // We keep going until our buffer of n-gram occurrences is empty. That
        // buffer is fed in from the iterator.
        boolean keepGoing = it.hasNext();
        while (keepGoing)
        {
            // Copy the previous terms into our new term n-gram. We create a
            // new n-gram each time so that each n-gram has a unique array.
            final Term[] currentTerms = new Term[this.size];
            for (int i = 1; i < this.size; i++)
            {
                currentTerms[i - 1] = previousTerms[i];
            }

            // Get the term and its occurrence.
            final TermOccurrence occurrence = it.hasNext() ? it.next() : null;
            final Term term = occurrence != null ? occurrence.getTerm() : null;
            // Update the buffer.
            if (   occurrencesBuffer.size() >= this.size
                || occurrence == null)
            {
                occurrencesBuffer.removeFirst();
            }
            // Buffer length is now < this.size.

            if (occurrence != null)
            {
                // Add this occurrence onto the list of occurrences.
                occurrencesBuffer.addLast(occurrence);
            }
            // Buffer length is now <= this.size.

            currentTerms[this.size - 1] = term;

            // Create the n-gram from the terms.
            final DefaultTermNGram nGram = new DefaultTermNGram(currentTerms);

            // We look up the first and last to get the span. We look at the
            // last because the current occurrence may be null.
            final TermOccurrence first = occurrencesBuffer.getFirst();
            final TermOccurrence last  = occurrencesBuffer.getLast();
            final int start = first.getStart();
            final int end = last.getStart() + last.getLength();
            final int length = end - start;

            // Add the term occurrence.
            result.add(new DefaultTermOccurrence(nGram, start, length));

            // Swap out the previous terms with our current terms.
            previousTerms = currentTerms;

            // Keep going until we've run our of occurrences.
            keepGoing = it.hasNext() || occurrencesBuffer.size() > 1;
        }

        return result;

    }

    /**
     * Gets the size of the n-gram created by the filter. Also known as the
     * value of n.
     *
     * @return
     *      The size of the n-gram created by the filter.
     */
    public int getSize()
    {
        return this.size;
    }

    /**
     * Sets the size of the n-gram created by the filter. Also known as the
     * value of n.
     *
     * @param   size
     *      The size of the n-gram created by the filter. Must be greater than
     *      1.
     */
    public void setSize(
        final int size)
    {
        if (size <= 1)
        {
            throw new IllegalArgumentException("size must be greater than 1");
        }

        this.size = size;
    }

}

/*
 * File:                DefaultTermCounts.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 02, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.HashMap;
import java.util.Set;

/**
 * A default implementation of the {@code TermCounts} interface.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultTermCounts
    extends AbstractCloneableSerializable
    implements TermCounts
{
// TODO: This code seems very similar to the DataCountMapHistogram class...
// maybe it should be combined or something?
// -- jdbasil (2010-04-21)

    /** The total of all counts. */
    protected int totalCount;

    /** The mapping of terms to their respective counts. */
    protected HashMap<Term, Integer> termToCountMap;

    /**
     * Creates a new, empty {@code DefaultTermCounts}.
     */
    public DefaultTermCounts()
    {
        super();

        this.setTotalCount(0);
        this.setTermToCountMap(new HashMap<Term, Integer>());
    }

    /**
     * Increments the count for a given term.
     * 
     * @param   term
     *      The term to add.
     */
    public void add(
        final Termable term)
    {
        this.add(term.asTerm(), 1);
    }

    /**
     * Adds the given amount for the given term.
     *
     * @param   term
     *      The term to add.
     * @param   count
     *      The amount to add to the count of the term. Must be positive.
     */
    public void add(
        final Term term,
        final int count)
    {
        if (count < 0)
        {
            throw new IllegalArgumentException("count must be non-negative");
        }
        else if (count == 0)
        {
            // Nothing to do in this case.
            return;
        }

        int current = this.getCount(term);
        current += count;
        this.termToCountMap.put(term, current);
        this.totalCount += count;
    }

    /**
     * Adds all of the given terms to the counters; one for each term
     * occurrence.
     *
     * @param   terms
     *      The terms to add.
     */
    public void addAll(
        final Iterable<? extends Termable> terms)
    {
        for (Termable term : terms)
        {
            this.add(term);
        }
    }

    public int getCount(
        final Term term)
    {
        Integer result = this.termToCountMap.get(term);

        if (result == null)
        {
            return 0;
        }
        else
        {
            return result;
        }
    }

    public Set<Term> getTerms()
    {
        return this.termToCountMap.keySet();
    }

    public int getTotalCount()
    {
        return this.totalCount;
    }

    /**
     * Sets the total count.
     *
     * @param   totalCount
     *      The total count.
     */
    protected void setTotalCount(
        final int totalCount)
    {
        this.totalCount = totalCount;
    }

    /**
     * Gets the mapping of terms to their respective counts.
     *
     * @return
     *      The mapping of terms to their respective counts.
     */
    protected HashMap<Term, Integer> getTermToCountMap()
    {
        return this.termToCountMap;
    }

    /**
     * Gets the mapping of terms to their respective counts.
     *
     * @param termToCountMap
     *      The mapping of terms to their respective counts.
     */
    protected void setTermToCountMap(
        final HashMap<Term, Integer> termToCountMap)
    {
        this.termToCountMap = termToCountMap;
    }


}

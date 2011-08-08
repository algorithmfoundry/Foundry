/*
 * File:                StopListFilter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 10, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.text.term.TermOccurrence;

/**
 * A term filter that rejects any term that appears in a given stop list.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class StopListFilter
    extends AbstractSingleTermFilter
{
    /** The stop list for the filter to use. */
    protected StopList stopList;

    /**
     * Creates a new {@code StopListFilter}.
     */
    public StopListFilter()
    {
        this(null);
    }

    /**
     * Creates a new {@code StopListFilter} with the given stop list.
     *
     * @param   stopList
     *      The stop list to use.
     */
    public StopListFilter(
        final StopList stopList)
    {
        super();

        this.setStopList(stopList);
    }

    public TermOccurrence filterTerm(
        final TermOccurrence occurence)
    {
        if (this.stopList.contains(occurence.asTerm()))
        {
            // Reject this term.
            return null;
        }
        else
        {
            // Not a stop word.
            return occurence;
        }
    }

    /**
     * Gets the stop list used by the filter.
     *
     * @return
     *      The stop list.
     */
    public StopList getStopList()
    {
        return this.stopList;
    }

    /**
     * Sets the stop list for the filter to use.
     *
     * @param   stopList
     *      The stop list.
     */
    public void setStopList(
        final StopList stopList)
    {
        this.stopList = stopList;
    }
}

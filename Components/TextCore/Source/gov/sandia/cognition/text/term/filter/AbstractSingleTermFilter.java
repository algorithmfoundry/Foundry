/*
 * File:                AbstractSingleTermFilter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 09, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.text.term.TermOccurrence;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.LinkedList;

/**
 * An abstract implementation of the {@code SingleTermFilter} interface. It
 * makes the filterTerms method call the filterTerm on each term.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractSingleTermFilter
    extends AbstractCloneableSerializable
    implements SingleTermFilter
{
    
    /**
     * Creates a new {@code AbstractSingleTermFilter}.
     */
    public AbstractSingleTermFilter()
    {
        super();
    }

    public Iterable<TermOccurrence> filterTerms(
        final Iterable<? extends TermOccurrence> terms)
    {
        // Create the list for the result.
        final LinkedList<TermOccurrence> result =
            new LinkedList<TermOccurrence>();

        // Go through all the terms.
        for (TermOccurrence oldTerm : terms)
        {
            final TermOccurrence newTerm = this.filterTerm(oldTerm);

            if (newTerm != null)
            {
                result.add(newTerm);
            }
            // else - The filter has rejected the term.
        }
        return result;
    }

}

/*
 * File:                SingleTermFilter.java
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

/**
 * Interface for a term filter that looks at each term individually.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface SingleTermFilter
    extends TermFilter
{

    /**
     * Takes a single term occurrence and filters that occurrence into a new
     * occurrence or returns null, indicating that the filter rejects that
     * term.
     * 
     * @param   occurrence
     *      The term occurrence to filter.
     * @return
     *      A term occurrence (may be a new instance or the same as the given
     *      one) of the term to replace the given one or null to indicate that
     *      the filter has rejected the given term.
     */
    public TermOccurrence filterTerm(
        final TermOccurrence occurrence);

}

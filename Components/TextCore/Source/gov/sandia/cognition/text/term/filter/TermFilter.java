/*
 * File:                TermFilter.java
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
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Interface for an object that can filter a list of terms to create a new
 * list of terms.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface TermFilter
    extends CloneableSerializable
{

    /**
     * Filters the given list of terms into a new list of terms based on some
     * internal criteria for what constitutes a term.
     *
     * @param   terms
     *      The terms to filter.
     * @return
     *      The new list of terms.
     */
    public Iterable<TermOccurrence> filterTerms(
        final Iterable<? extends TermOccurrence> terms);

}

/*
 * File:                DictionaryFilter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.text.term.Term;
import gov.sandia.cognition.text.term.TermOccurrence;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A term filter that only allows terms in its dictionary.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DictionaryFilter
    extends AbstractSingleTermFilter
{

    /** The set of terms allowed by the filter. All other terms are discarded.
     */
    protected Set<Term> allowedTerms;

    /**
     * Creates a new {@code DictionaryFilter} with an empty set of allowed
     * terms.
     */
    public DictionaryFilter()
    {
        this(new LinkedHashSet<Term>());
    }

    /**
     * Creates a new {@code DictionaryFilter} with a given set of allowed
     * terms.
     *
     * @param   allowedTerms
     *      The set of allowed terms.
     */
    public DictionaryFilter(
        final Set<Term> allowedTerms)
    {
        this.allowedTerms = allowedTerms;
    }

    public TermOccurrence filterTerm(
        final TermOccurrence occurrence)
    {
        if (this.getAllowedTerms().contains(occurrence.getTerm()))
        {
            // The term is allowed.
            return occurrence;
        }
        else
        {
            // The term is nt allowed.
            return null;
        }
    }

    /**
     * Gets the set of allowed terms.
     *
     * @return
     *      The set of allowed terms.
     */
    public Set<Term> getAllowedTerms()
    {
        return this.allowedTerms;
    }

    /**
     * Sets the set of allowed terms.
     *
     * @param   allowedTerms
     *      The set of allowed terms.
     */
    public void setAllowedTerms(
        final Set<Term> allowedTerms)
    {
        this.allowedTerms = allowedTerms;
    }

}

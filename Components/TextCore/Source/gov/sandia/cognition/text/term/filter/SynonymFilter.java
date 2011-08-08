/*
 * File:                SynonymFilter.java
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

import gov.sandia.cognition.text.term.DefaultTermOccurrence;
import gov.sandia.cognition.text.term.Term;
import gov.sandia.cognition.text.term.TermOccurrence;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A term filter that uses a mapping of synonyms to replace a word with its
 * synonym.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class SynonymFilter
    extends AbstractSingleTermFilter
{

    /** The mapping of terms to the synonym to replace them with. */
    protected Map<Term, Term> synonyms;

    /**
     * Creates a new, empty {@code SynonymFilter}.
     */
    public SynonymFilter()
    {
        this(new LinkedHashMap<Term, Term>());
    }

    /**
     * Creates a new {@code SynonymFilter} with the given synonyms.
     * 
     * @param synonyms
     */
    public SynonymFilter(
        final Map<Term, Term> synonyms)
    {
        super();

        this.setSynonyms(synonyms);
    }

    public TermOccurrence filterTerm(
        final TermOccurrence occurrence)
    {
        // See if there is a synonym for the term.
        final Term synonym = this.getSynonyms().get(occurrence.getTerm());

        if (synonym == null)
        {
            // No synonym.
            return occurrence;
        }
        else
        {
            // There is a snynonym so use it.
            return new DefaultTermOccurrence(synonym,
                occurrence.getStart(), occurrence.getLength());
        }
    }

    /**
     * Gets the mapping of terms to their synonyms.
     *
     * @return
     *      The mapping of terms to their synonyms.
     */
    public Map<Term, Term> getSynonyms()
    {
        return this.synonyms;
    }

    /**
     * Sets the mapping of terms to their synonyms.
     *
     * @param   synonyms
     *      The mapping of terms to their synonyms.
     */
    public void setSynonyms(
        final Map<Term, Term> synonyms)
    {
        this.synonyms = synonyms;
    }

}

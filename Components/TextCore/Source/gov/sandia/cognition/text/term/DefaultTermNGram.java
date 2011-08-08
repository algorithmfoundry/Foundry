/*
 * File:                DefaultTermNGram.java
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

package gov.sandia.cognition.text.term;

import java.util.Arrays;
import java.util.List;

/**
 * A default implementation of the {@code TermNGram} interface.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultTermNGram
    extends AbstractTerm
    implements TermNGram
{

    /** The array of terms that make up the n-gram. */
    protected Term[] terms;

    /**
     * Creates a new {@code DefaultTermNGram}.
     */
    public DefaultTermNGram()
    {
        this(new Term[0]);
    }

    /**
     * Creates a new {@code DefaultTermNGram}.
     *
     * @param   terms
     *      The terms that make up the n-gram.
     */
    public DefaultTermNGram(
        final Term... terms)
    {
        super();
        
        this.setTerms(terms);
    }

    @Override
    public DefaultTermNGram clone()
    {
        final DefaultTermNGram clone = (DefaultTermNGram) super.clone();
        clone.terms = this.terms == null ? null : this.terms.clone();
        return clone;
    }

    /**
     * The name of the n-gram term is {@code [<n>-gram: <list>]} where n is the
     * size ot this n-gram and list is a comma-separated list of the internal
     * term names.
     *
     * @return
     *      The name of the term.
     */
    public String getName()
    {
        final StringBuilder result = new StringBuilder();

        result.append("[" + this.getTermCount() + "-gram:");
        for (int i = 0; i < this.terms.length; i++)
        {
            if (i > 0)
            {
                result.append(",");
            }

            final Term term = this.terms[i];
            if (term != null)
            {
                result.append(" ");
                result.append(term.getName());
            }
            // else - Null terms have no names.
        }

        result.append("]");

        return result.toString();
    }

    public int getTermCount()
    {
        return this.terms.length;
    }

    public Term getTerm(
        final int i)
    {
        return this.terms[i];
    }

    public List<Term> getTermList()
    {
        return Arrays.asList(this.terms);
    }

    /**
     * Gets the terms that make up the n-gram.
     * 
     * @return
     *      The terms that make up the n-gram.
     */
    public Term[] getTerms()
    {
        return this.terms;
    }

    /**
     * Sets the terms that make up the n-gram.
     * 
     * @param   terms
     *      The terms that make up the n-gram.
     */
    public void setTerms(
        final Term... terms)
    {
        this.terms = terms;
    }

    // TODO: Implement equals for n-gram terms.
}

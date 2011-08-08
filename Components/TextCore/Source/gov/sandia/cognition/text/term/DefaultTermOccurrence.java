/*
 * File:                DefaultTermOccurrence.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 19, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term;

import gov.sandia.cognition.text.AbstractOccurrenceInText;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * A default implementation of the {@code TermOccurrence} interface. It just
 * holds the term plus where it occurred.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultTermOccurrence
    extends AbstractOccurrenceInText<Term>
    implements TermOccurrence
{

    /** The term that occurred. */
    protected Term term;

    /**
     * Creates a new {@code DefaultTermOccurrence} with default values.
     */
    public DefaultTermOccurrence()
    {
        this(null, DEFAULT_START, DEFAULT_LENGTH);
    }

    /**
     * Creates a new {@code DefaultTermOccurrence}.
     *
     * @param   term
     *      The term that occurred.
     * @param   start
     *      The starting point of the occurrence in the source text.
     * @param length
     *      The length of the occurrence in the source text.
     */
    public DefaultTermOccurrence(
        final Term term,
        final int start,
        final int length)
    {
        super(start, length);

        this.setTerm(term);
    }

    public Term asTerm()
    {
        return this.getTerm();
    }

    public Term getData()
    {
        // The data is the term.
        return this.getTerm();
    }

    public Term getTerm()
    {
        return this.term;
    }

    /**
     * Sets the term that occurred.
     *
     * @param   term
     *      The term that occurred.
     */
    public void setTerm(
        final Term term)
    {
        this.term = term;
    }

    @Override
    public boolean equals(
        final Object other)
    {
        return other instanceof DefaultTermOccurrence
            && this.equals((DefaultTermOccurrence) other);
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 43 * hash + (this.term != null ? this.term.hashCode() : 0);
        hash = 43 * hash + this.start;
        hash = 43 * hash + this.length;
        return hash;
    }

    /**
     * Determines if this {@code DefaultTermOccurrence} equals a given
     * {@code DefaultTermOccurrence}.
     *
     * @param   other
     *      The other object.
     * @return
     *      True if the two objects are equal; otherwise, false.
     */
    public boolean equals(
        final DefaultTermOccurrence other)
    {
        return other != null
            && ObjectUtil.equalsSafe(this.term, other.term)
            && this.start == other.start
            && this.length == other.length;
    }



}

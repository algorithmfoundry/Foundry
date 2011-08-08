/*
 * File:                TermLengthFilter.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright July 27, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.text.term.TermOccurrence;

/**
 * Implements a filter based on the length of a term. The length is computed
 * from the name of the term.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class TermLengthFilter
    extends AbstractSingleTermFilter
{
    /** The default minimum length is {@value}. */
    public static final int DEFAULT_MINIMUM_LENGTH = 3;

    /** The default maximum length is {@value}. Based on the maximum known
     *  length of non-technical and non-coined English words.
     */
    @PublicationReference(
        author="Wikipedia",
        title="Longest word in English",
        year=2009,
        type=PublicationType.WebPage,
        url="http://en.wikipedia.org/wiki/Longest_word_in_English")
    public static final int DEFAULT_MAXIMUM_LENGTH = 28;

    /** The minimum allowed length. Inclusive. A null value indicates no
     *  minimum. Must be non-negative.
     */
    protected Integer minimumLength;

    /** The maximum allowed length. Inclusive. A null value indicates no
     *  maximum. Must be non-negative.
     */
    protected Integer maximumLength;

    /**
     * Creates a new {@code TermLengthFilter} with default minimum and
     * maximum values.
     */
    public TermLengthFilter()
    {
        this(DEFAULT_MINIMUM_LENGTH, DEFAULT_MAXIMUM_LENGTH);
    }

    /**
     * Creates a new {@code TermLengthFilter} with given minimum and maximum
     * values.
     *
     * @param   minimumLength
     *      The minimum allowed term length.
     * @param   maximumLength
     *      The maximum allowed term length.
     */
    public TermLengthFilter(
        final Integer minimumLength,
        final Integer maximumLength)
    {
        super();

        this.setMinimumLength(minimumLength);
        this.setMaximumLength(maximumLength);
    }

    public TermOccurrence filterTerm(
        final TermOccurrence occurrence)
    {
        // Get the length of the name of the term.
        final int length = occurrence.getTerm().getName().toString().length();

        if (this.minimumLength != null && length < this.minimumLength)
        {
            // Smaller than the minimum length.
            return null;
        }
        else if (this.maximumLength != null && length > this.maximumLength)
        {
            // Larger than the maximum length.
            return null;
        }
        else
        {
            // Within the term bounds.
            return occurrence;
        }
    }

    /**
     * Gets the minimum length allowed for a term (inclusive). A null value
     * means no minimum.
     *
     * @return
     *      The minimum length allowed for a term.
     */
    public Integer getMinimumLength()
    {
        return this.minimumLength;
    }

    /**
     * Gets the minimum length allowed for a term (inclusive). A null value
     * means no minimum.
     *
     * @param minimumLength
     *      The minimum length allowed for a term. Must be non-negative.
     */
    public void setMinimumLength(
        final Integer minimumLength)
    {
        if (minimumLength != null && minimumLength < 0)
        {
            throw new IllegalArgumentException(
                "minimumLength must be non-negative");
        }
        this.minimumLength = minimumLength;
    }

    /**
     * Gets the maximum length allowed for a term (inclusive). A null value
     * means no maximum.
     *
     * @return
     *      The maximum length allowed for a term.
     */
    public Integer getMaximumLength()
    {
        return this.maximumLength;
    }

    /**
     * Gets the maximum length allowed for a term (inclusive). A null value
     * means no maximum.
     *
     * @param maximumLength
     *      The maximum length allowed for a term. Must be positive.
     */
    public void setMaximumLength(
        final Integer maximumLength)
    {
        if (maximumLength != null && maximumLength <= 0)
        {
            throw new IllegalArgumentException(
                "maximumLength must be non-negative");
        }
        this.maximumLength = maximumLength;
    }

}

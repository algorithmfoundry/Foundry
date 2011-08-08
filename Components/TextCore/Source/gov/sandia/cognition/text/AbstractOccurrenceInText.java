/*
 * File:                AbstractOccurrenceInText.java
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

package gov.sandia.cognition.text;

import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * An abstract implementation of the {@code OccurrenceInText} interface. It
 * holds the start and length.
 * 
 * @param   <DataType>
 *      The type of data that occurred in the text.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractOccurrenceInText<DataType>
    extends AbstractCloneableSerializable
    implements OccurrenceInText<DataType>
{
    /** The default start is {@value}. */
    public static final int DEFAULT_START = 0;

    /** The default length is {@value}. */
    public static final int DEFAULT_LENGTH = 0;

    /** The starting point of the occurrence. */
    protected int start;

    /** The length of the occurrence. */
    protected int length;

    /**
     * Creates a new {@code AbstractOccurrenceInText}.
     */
    public AbstractOccurrenceInText()
    {
        this(DEFAULT_START, DEFAULT_LENGTH);
    }

    /**
     * Creates a new {@code AbstractOccurrenceInText}.
     *
     * @param   start
     *      The starting point of the occurrence.
     * @param   length
     *      The length of the occurrence.
     */
    public AbstractOccurrenceInText(
        final int start,
        final int length)
    {
        super();

        this.setStart(start);
        this.setLength(length);
    }

    public int getStart()
    {
        return this.start;
    }

    /**
     * Sets the starting point of the occurrence. Must be non-negative.
     *
     * @param   start
     *      The starting point of the occurrence.
     */
    public void setStart(
        final int start)
    {
        if (start < 0)
        {
            throw new IllegalArgumentException("start must be non-negative");
        }

        this.start = start;
    }

    public int getLength()
    {
        return this.length;
    }

    /**
     * Sets the length of the occurrence. Must be non-negative.
     *
     * @param   length
     *      The length of the occurrence.
     */
    public void setLength(
        final int length)
    {
        if (length < 0)
        {
            throw new IllegalArgumentException("length must be non-negative");
        }

        this.length = length;
    }
}

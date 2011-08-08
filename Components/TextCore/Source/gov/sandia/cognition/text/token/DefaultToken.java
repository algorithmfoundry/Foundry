/*
 * File:                DefaultToken.java
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

package gov.sandia.cognition.text.token;

import gov.sandia.cognition.text.AbstractOccurrenceInText;
import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.Term;

/**
 * A default implementation of the {@code Token} interface. It just stores the
 * text of the token as a {@code String}.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultToken
    extends AbstractOccurrenceInText<Term>
    implements Token
{
    /** The text of the token. */
    private String text;

    /**
     * Creates a new {@code Token}. The text defaults to null.
     */
    public DefaultToken()
    {
        this(null, DEFAULT_START, DEFAULT_LENGTH);
    }

    /**
     * Creates a new {@code Token} with the given text. The length is assumed
     * to be the length of the text.
     *
     * @param   text
     *      The text of the token.
     * @param   start
     *      The starting point of the token in its source.
     */
    public DefaultToken(
        final String text,
        final int start)
    {
        this(text, start, text == null ? DEFAULT_LENGTH : text.length());
    }

    /**
     * Creates a new {@code Token} with the given text, start, and length.
     *
     * @param   text
     *      The text of the token.
     * @param   start
     *      The starting point of the token in its source.
     * @param   length
     *      The length of the token in the source.
     */
    public DefaultToken(
        final String text,
        final int start,
        final int length)
    {
        super(start, length);

        this.setText(text);
    }
    
    public DefaultTerm getData()
    {
        return this.getTerm();
    }

    public DefaultTerm asTerm()
    {
        return this.getTerm();
    }

    public DefaultTerm getTerm()
    {
        if (this.text == null)
        {
            return null;
        }
        else
        {
            return new DefaultTerm(text);
        }
    }

    public String getText()
    {
        return this.text;
    }

    /**
     * Sets the text of the token.
     *
     * @param   text
     *      The text of the token.
     */
    public void setText(
        final String text)
    {
        this.text = text;
    }

    /**
     * The text of the token.
     *
     * @return
     *      The text of the token.
     */
    @Override
    public String toString()
    {
        return this.getText();
    }

    
}

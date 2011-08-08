/*
 * File:                DefaultTerm.java
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

package gov.sandia.cognition.text.term;

/**
 * A default implementation of the {@code Term} interface. It just keeps track
 * of the text of the term.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultTerm
    extends AbstractTerm
{

    /** The text of the term. */
    protected String text;

    /**
     * Creates a new {@code DefaultTerm} that contains the empty string.
     */
    public DefaultTerm()
    {
        this("");
    }

    /**
     * Creates a new {@code DefaultTerm} with the given text.
     *
     * @param   text
     *      The text for the term.
     */
    public DefaultTerm(
        final String text)
    {
        super();

        this.setText(text);
    }

    @Override
    public DefaultTerm clone()
    {
        return (DefaultTerm) super.clone();
    }

    public String getName()
    {
        return this.text;
    }


    /**
     * Gets the text of the term.
     * 
     * @return
     *      The text of the term.
     */
    public String getText()
    {
        return text;
    }

    /**
     * Sets the text of the term.
     *
     * @param   text
     *      The text of the term.
     */
    public void setText(
        final String text)
    {
        this.text = text;
    }

}

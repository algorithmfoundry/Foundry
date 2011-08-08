/*
 * File:                DefaultTextual.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Text Core
 * 
 * Copyright February 01, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.text;

import gov.sandia.cognition.util.ObjectUtil;

/**
 * A default implementation of the {@code Textual} interface that just stores
 * a string value.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public class DefaultTextual
    extends AbstractTextual
{

    /** The text value. */
    protected String text;

    /**
     * Creates a new {@code DefaultTextual} containing the empty string.
     */
    public DefaultTextual()
    {
        this("");
    }

    /**
     * Creates a new {@code DefaultTextual} containing the given text.
     *
     * @param   text
     *      The text to store in the object.
     */
    public DefaultTextual(
        final String text)
    {
        super();

        this.setText(text);
    }

    /**
     * Creates a new {@code DefaultTextual} that takes the string text value
     * from the given {@code Textual} object.
     *
     * @param   other
     *      The other object to get the text from.
     */
    public DefaultTextual(
        final Textual other)
    {
        this(other.getText());
    }

    @Override
    public DefaultTextual clone()
    {
        return (DefaultTextual) super.clone();
    }

    @Override
    public boolean equals(
        final Object other)
    {
        return (this == other)
            || (other instanceof DefaultTextual
            && ObjectUtil.equalsSafe(this.text,
                ((DefaultTextual) other).text));
    }

    @Override
    public int hashCode()
    {
        // Use the text as a hash code.
        return ObjectUtil.hashCodeSafe(this.text);
    }

    /**
     * Gets the text value in this object.
     *
     * @return
     *      The text value.
     */
    @Override
    public String getText()
    {
        return this.text;
    }

    /**
     * Sets the text value in this object.
     *
     * @param   text
     *      The text value.
     */
    public void setText(
        final String text)
    {
        this.text = text;
    }

}

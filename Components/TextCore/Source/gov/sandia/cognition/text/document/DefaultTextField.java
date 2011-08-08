/*
 * File:                DefaultTextField.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.document;

/**
 * A default implementation of the {@code Field} interface. It stores the text
 * of the field as a String.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultTextField
    extends AbstractField
{

    /** The default name for the field is the empty string. */
    public static final String DEFAULT_NAME = "";

    /** The text content of the field. */
    protected String text;

    /**
     * Creates a new {@code DefaultTextField} with default name and text, both
     * of which are the empty string.
     */
    public DefaultTextField()
    {
        this(DEFAULT_NAME, "");
    }

    /**
     * Creates a new {@code DefaultTextField} with the given name and text.
     *
     * @param   name
     *      The name of the field.
     * @param   text
     *      The text content for the field.
     */
    public DefaultTextField(
        final String name,
        final String text)
    {
        super(name);

        this.setText(text);
    }

    public String getText()
    {
        return this.text;
    }

    /**
     * Sets the text content for the field.
     *
     * @param   text
     *      The text content for the field.
     */
    public void setText(
        final String text)
    {
        this.text = text;
    }

}

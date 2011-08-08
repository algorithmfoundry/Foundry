/*
 * File:                AbstractField.java
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

import gov.sandia.cognition.util.AbstractNamed;
import java.io.Reader;
import java.io.StringReader;

/**
 * An abstract implementation of the {@code Field} interface.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractField
    extends AbstractNamed
    implements Field
{
    /**
     * Creates a new {@code AbstractField}.
     */
    public AbstractField()
    {
        super();
    }

    /**
     * Creates a new {@code AbstractField}
     *
     * @param   name
     *      The name of the field.
     */
    public AbstractField(
        final String name)
    {
        super(name);
    }

    public Reader readText()
    {
        // Turn the text into a reader.
        return new StringReader(this.getText());
    }

    @Override
    public String toString()
    {
        // The default toString is the text of the field.
        return this.getText();
    }


}

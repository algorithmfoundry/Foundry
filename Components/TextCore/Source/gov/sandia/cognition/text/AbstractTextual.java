/*
 * File:                AbstractTextual.java
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

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.io.Reader;
import java.io.StringReader;

/**
 * A default implementation of the {@code Textual} interface. It implements
 * the readText() and toString() methods both by calling getText(). Thus,
 * extending classes only need to implement the getText method to fulfill the
 * {@code Textual} interface.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public abstract class AbstractTextual
    extends AbstractCloneableSerializable
    implements Textual
{

    /**
     * Creates a new {@code AbstractTextual}.
     */
    public AbstractTextual()
    {
        super();
    }

    @Override
    public Reader readText()
    {
        return new StringReader(this.getText());
    }

    @Override
    public String toString()
    {
        return this.getText();
    }
    
}

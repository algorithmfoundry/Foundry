/*
 * File:                AbstractTokenizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 02, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.token;

import gov.sandia.cognition.text.Textual;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.io.StringReader;

/**
 * Abstract implementation of the {@code Tokenizer} interface. It turns the
 * tokenize call for a {@code String} into a {@code Reader}.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractTokenizer
    extends AbstractCloneableSerializable
    implements Tokenizer
{

    /**
     * Creates a new {@code AbstractTokenizer}.
     */
    public AbstractTokenizer()
    {
        super();
    }

    public Iterable<Token> tokenize(
        final String s)
    {
        // Read in the string using a string reader.
        return this.tokenize(new StringReader(s));
    }

    public Iterable<Token> tokenize(
        final Textual textual)
    {
        return this.tokenize(textual.readText());
    }

}

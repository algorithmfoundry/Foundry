/*
 * File:                Tokenizer.java
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

package gov.sandia.cognition.text.token;

import gov.sandia.cognition.text.Textual;
import gov.sandia.cognition.util.CloneableSerializable;
import java.io.Reader;

/**
 * Interface for a class that converts strings into tokens.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public interface Tokenizer
    extends CloneableSerializable
{

    /**
     * Convert the given string into a corresponding ordered list of tokens.
     *
     * @param   s
     *      The string to tokenize.
     * @return
     *      The ordered list of tokens.
     */
    public Iterable<Token> tokenize(
        final String s);

    /**
     * Converts the string from the given reader into an ordered list of tokens.
     *
     * @param   reader
     *      The reader to tokenize the data from.
     * @return
     *      The ordered list of tokens.
     */
    public Iterable<Token> tokenize(
        final Reader reader);


    /**
     * Convert the given string into a corresponding ordered list of tokens.
     *
     * @param   textual
     *      The object to tokenize.
     * @return
     *      The ordered list of tokens.
     */
    public Iterable<Token> tokenize(
        final Textual textual);

}

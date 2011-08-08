/*
 * File:                AbstractCharacterBasedTokenizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 03, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;

/**
 * An abstract implementation of a tokenizer that considers each character
 * individually. It takes care of most of the work and lets the subclasses
 * define what a valid token member character is.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractCharacterBasedTokenizer
    extends AbstractTokenizer
{

    /**
     * Creates a new {@code LetterNumberTokenizer}.
     */
    public AbstractCharacterBasedTokenizer()
    {
        super();
    }

    public Iterable<Token> tokenize(
        final Reader reader)
    {
        // Create a buffered reader to do the tokenization.
        final BufferedReader br = new BufferedReader(reader);
        final LinkedList<Token> result = new LinkedList<Token>();

        try
        {
            // We are going to read the text into tokens. We keep track of
            // the current index into the reader plus the starting point of
            // each token. We read the data for each token into the string
            // builder.
            int index = 0;
            int start = 0;
            StringBuilder text = new StringBuilder();

            // TODO: Is there a better way to read in the character?
            final char[] buffer = new char[1];
            while (br.read(buffer) == 1)
            {
                final char c = buffer[0];

                //
                if (!this.isTokenMember(c))
                {
                    if (text.length() > 0)
                    {
                        result.add(new DefaultToken(text.toString(), start));
                        text = new StringBuilder();
                    }

                    start = index;
                }
                else
                {
                    // This is a part of the current token, so add it.
                    text.append(c);
                }

                index++;
            }

            // Take care of the last token, if there is one.
            if (text.length() > 0)
            {
                result.add(new DefaultToken(text.toString(), start));
            }
        }
        catch (IOException e)
        {
            // Error during tokenization.
            return null;
        }

        return result;
    }

    /**
     * Determines if the given character is considered to be part of a token.
     *
     * @param   c
     *      A character.
     * @return
     *      True if the character can be part of a token; otherwise, false.
     */
    public abstract boolean isTokenMember(
        final char c);
    
}

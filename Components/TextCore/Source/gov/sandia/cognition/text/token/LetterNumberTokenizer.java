/*
 * File:                LetterNumberTokenizer.java
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

/**
 * A tokenizer that creates tokens from sequences of letters and numbers,
 * treating everything else as a delimiter.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class LetterNumberTokenizer
    extends AbstractCharacterBasedTokenizer
{

    /**
     * Creates a new {@code LetterNumberTokenizer}.
     */
    public LetterNumberTokenizer()
    {
        super();
    }
    
    public boolean isTokenMember(
        final char c)
    {
        // Valid token members are letters or digits.
        return Character.isLetterOrDigit(c);
    }
}

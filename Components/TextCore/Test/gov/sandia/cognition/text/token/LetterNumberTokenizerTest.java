/*
 * File:                LetterNumberTokenizerTest.java
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

import gov.sandia.cognition.collection.CollectionUtil;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class LetterNumberTokenizer.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class LetterNumberTokenizerTest
{
    /**
     * Creates a new test.
     */
    public LetterNumberTokenizerTest()
    {
    }

    /**
     * Test of tokenize method, of class LetterNumberTokenizer.
     */
    @Test
    public void testTokenize()
    {
        LetterNumberTokenizer instance = new LetterNumberTokenizer();

        String input = "...Mr. Taco? Help? 123\n AbC ?~!@\t Yes7.";
        String[] expected = { "Mr", "Taco", "Help", "123", "AbC", "Yes7" };
        Iterable<Token> result = instance.tokenize(input);
        assertEqualTokens(input, expected, result);

        input = "aB3";
        expected = new String[] { "aB3" };
        result = instance.tokenize(input);
        assertEqualTokens(input, expected, result);

        input = "   aB3        ";
        expected = new String[] { "aB3" };
        result = instance.tokenize(input);
        assertEqualTokens(input, expected, result);

        input = "   4    ";
        expected = new String[] { "4" };
        result = instance.tokenize(input);
        assertEqualTokens(input, expected, result);

        input = "";
        expected = new String[0];
        result = instance.tokenize(input);
        assertEqualTokens(input, expected, result);
    }

    public void assertEqualTokens(
        final String input,
        final String[] expected,
        final Iterable<? extends Token> tokens)
    {
        assertEquals(expected.length, CollectionUtil.size(tokens));
        int index = 0;
        for (Token token : tokens)
        {
            assertEquals(expected[index], token.getText());
            assertEquals(expected[index].length(), token.getLength());
            assertEquals(expected[index], input.substring(token.getStart(), token.getStart() + token.getLength()));
            index++;
        }
    }


}
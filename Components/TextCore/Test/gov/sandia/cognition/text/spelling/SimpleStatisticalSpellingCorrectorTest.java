/*
 * File:                SimpleStatisticalSpellingCorrectorTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright February 22, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.spelling;

import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @TODO    Document this.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class SimpleStatisticalSpellingCorrectorTest
{
    /**
     * Creates a new test.
     */
    public SimpleStatisticalSpellingCorrectorTest()
    {
    }

    /**
     * Test of constructors of class SimpleStatisticalSpellingCorrector.
     */
    @Test
    public void testConstructors()
    {
        char[] alphabet = SimpleStatisticalSpellingCorrector.createDefaultAlphabet();
        SimpleStatisticalSpellingCorrector instance = new SimpleStatisticalSpellingCorrector();
        assertNotNull(instance.getWordCounts());
        assertEquals(0, instance.getWordCounts().getTotalCount());
        assertArrayEquals(alphabet, instance.getAlphabet());

        alphabet = new char[] { 'a', 'b', 'c' };
        instance = new SimpleStatisticalSpellingCorrector(alphabet);
        assertNotNull(instance.getWordCounts());
        assertEquals(0, instance.getWordCounts().getTotalCount());
        assertSame(alphabet, instance.getAlphabet());

        MapBasedDataHistogram<String> wordCounts = new MapBasedDataHistogram<String>();
        wordCounts.add("awesome");
        instance = new SimpleStatisticalSpellingCorrector(wordCounts, alphabet);
        assertSame(wordCounts, instance.getWordCounts());
        assertSame(alphabet, instance.getAlphabet());
    }

    /**
     * Test of createDefaultAlphabet method, of class SimpleStatisticalSpellingCorrector.
     */
    @Test
    public void testCreateDefaultAlphabet()
    {
        char[] result = SimpleStatisticalSpellingCorrector.createDefaultAlphabet();
        assertNotSame(result, SimpleStatisticalSpellingCorrector.createDefaultAlphabet());
        assertEquals(26, result.length);

        final String alphabet = "abcdefghijklmnopqrstuvwxyz";
        for (int i = 0; i < alphabet.length(); i++)
        {
            assertEquals(alphabet.charAt(i), result[i]);
        }
    }

    /**
     * Test of add method, of class SimpleStatisticalSpellingCorrector.
     */
    @Test
    public void testAdd()
    {
        SimpleStatisticalSpellingCorrector instance =
            new SimpleStatisticalSpellingCorrector();
        instance.add("a");
        assertEquals(1, instance.getWordCounts().getCount("a"));

        instance.add("b", 4);
        assertEquals(4, instance.getWordCounts().getCount("b"));

        instance.add("b", 7);
        assertEquals(11, instance.getWordCounts().getCount("b"));
    }

    /**
     * Test of evaluate method, of class SimpleStatisticalSpellingCorrector.
     */
    @Test
    public void testEvaluate()
    {
        SimpleStatisticalSpellingCorrector instance =
            new SimpleStatisticalSpellingCorrector();

        instance.add("spelling", 10);
        instance.add("spoiling", 9);
        instance.add("corrected", 2);
        instance.add("access");
        instance.add("a");
        
        assertEquals("spelling", instance.evaluate("spelling"));
        assertEquals("spelling", instance.evaluate("speling"));
        assertEquals("corrected", instance.evaluate("corrected"));
        assertEquals("corrected", instance.evaluate("correcter"));
        assertEquals("corrected", instance.evaluate("korrecter"));
        assertEquals("access", instance.evaluate("access"));
        assertEquals("access", instance.evaluate("acess"));
        assertEquals("access", instance.evaluate("acesss"));

        // Tests handling unknown words with no known edits:
        assertEquals("wxyz", instance.evaluate("wxyz"));
        assertEquals("wxyz", instance.evaluate("wXyZ"));

        // Boundry cases.
        assertEquals("", instance.evaluate(""));
        assertEquals(null, instance.evaluate(null));
    }

    /**
     * Test of findBest method, of class SimpleStatisticalSpellingCorrector.
     */
    @Test
    public void testFindBest()
    {
        List<String> words = Arrays.asList(new String[] { "low", "high", "none" });
        String defaultBestWord = "default";

        SimpleStatisticalSpellingCorrector instance =
            new SimpleStatisticalSpellingCorrector();
        
        
        assertEquals(defaultBestWord, instance.findBest(words, defaultBestWord));

        instance.add("low", 2);
        assertEquals("low", instance.findBest(words, defaultBestWord));

        instance.add("high", 5);
        assertEquals("high", instance.findBest(words, defaultBestWord));
    }

    /**
     * Test of getWordCounts method, of class SimpleStatisticalSpellingCorrector.
     */
    @Test
    public void testGetWordCounts()
    {
        this.testSetWordCounts();
    }

    /**
     * Test of setWordCounts method, of class SimpleStatisticalSpellingCorrector.
     */
    @Test
    public void testSetWordCounts()
    {
        SimpleStatisticalSpellingCorrector instance = new SimpleStatisticalSpellingCorrector();
        assertNotNull(instance.getWordCounts());
        assertEquals(0, instance.getWordCounts().getTotalCount());

        MapBasedDataHistogram<String> wordCounts = new MapBasedDataHistogram<String>();
        wordCounts.add("awesome");
        instance.setWordCounts(wordCounts);
        assertSame(wordCounts, instance.getWordCounts());

        wordCounts = new MapBasedDataHistogram<String>();
        instance.setWordCounts(wordCounts);
        assertSame(wordCounts, instance.getWordCounts());

        wordCounts = null;
        instance.setWordCounts(wordCounts);
        assertSame(wordCounts, instance.getWordCounts());
        
        wordCounts = new MapBasedDataHistogram<String>();
        instance.setWordCounts(wordCounts);
        assertSame(wordCounts, instance.getWordCounts());
    }

    /**
     * Test of getAlphabet method, of class SimpleStatisticalSpellingCorrector.
     */
    @Test
    public void testGetAlphabet()
    {
        this.testSetAlphabet();
    }

    /**
     * Test of setAlphabet method, of class SimpleStatisticalSpellingCorrector.
     */
    @Test
    public void testSetAlphabet()
    {
        char[] alphabet = SimpleStatisticalSpellingCorrector.createDefaultAlphabet();
        SimpleStatisticalSpellingCorrector instance = new SimpleStatisticalSpellingCorrector();
        assertArrayEquals(alphabet, instance.getAlphabet());

        alphabet = new char[] { 'a', 'b', 'c' };
        instance.setAlphabet(alphabet);
        assertSame(alphabet, instance.getAlphabet());

        
        alphabet = new char[0];
        instance.setAlphabet(alphabet);
        assertSame(alphabet, instance.getAlphabet());

        alphabet = null;
        instance.setAlphabet(alphabet);
        assertSame(alphabet, instance.getAlphabet());

        alphabet = new char[] { '1', 'z', '@' };
        instance.setAlphabet(alphabet);
        assertSame(alphabet, instance.getAlphabet());
    }

}
/*
 * File:                DefaultStopListTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 09, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.text.term.filter;

import gov.sandia.cognition.text.term.DefaultTerm;
import gov.sandia.cognition.text.term.DefaultTermOccurrence;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class DefaultStopList.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class DefaultStopListTest
{
    public static final String TEST_FILE_NAME = "gov/sandia/cognition/text/term/filter/resources/testStopList.txt";
    public static final List<String> TEST_WORDS = Arrays.asList(new String[] {
        "this", "is", "a", "test",
        "a phrase", "caps",
        "duplicate"
    });


    /**
     * Creates a new test.
     */
    public DefaultStopListTest()
    {
    }


    /**
     * Test of constructors of class DefaultStopList.
     */
    @Test
    public void testConstructors()
    {
        DefaultStopList instance = new DefaultStopList();
        assertTrue(instance.getWords().isEmpty());

        instance = new DefaultStopList(TEST_WORDS);
        assertTestStopList(instance);
    }

    /**
     * Test of clone method, of class DefaultStopList.
     */
    @Test
    public void testClone()
    {
        DefaultStopList instance = new DefaultStopList(TEST_WORDS);
        DefaultStopList clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());

        assertNotSame(instance.words, clone.words);
        assertTestStopList(clone);
    }

    /**
     * Test of add method, of class DefaultStopList.
     */
    @Test
    public void testAdd()
    {
        String word1 = "test";
        String word2 = "YA";
        String word3 = "no";

        DefaultStopList instance = new DefaultStopList();
        assertEquals(0, instance.getWords().size());
        assertFalse(instance.contains(word1));
        assertFalse(instance.contains(word1.toUpperCase()));
        assertFalse(instance.contains(word2));
        assertFalse(instance.contains(word3));

        instance.add(word1);
        assertEquals(1, instance.getWords().size());
        assertTrue(instance.contains(word1));
        assertTrue(instance.contains(word1.toUpperCase()));
        assertFalse(instance.contains(word2));
        assertFalse(instance.contains(word3));
        
        instance.add(word2);
        assertEquals(2, instance.getWords().size());
        assertTrue(instance.contains(word1));
        assertTrue(instance.contains(word1.toUpperCase()));
        assertTrue(instance.contains(word2));
        assertFalse(instance.contains(word3));


        instance.add(word1.toUpperCase());
        assertEquals(2, instance.getWords().size());
        assertTrue(instance.contains(word1));
        assertTrue(instance.contains(word1.toUpperCase()));
        assertTrue(instance.contains(word2));
        assertFalse(instance.contains(word3));

    }


    /**
     * Test of addAll method, of class DefaultStopList.
     */
    @Test
    public void testAddAll()
    {
        DefaultStopList instance = new DefaultStopList();

        for (String word : TEST_WORDS)
        {
            assertFalse(instance.contains(word));
        }

        instance.addAll(TEST_WORDS);

        for (String word : TEST_WORDS)
        {
            assertTrue(instance.contains(word));
        }
    }

    /**
     * Test of contains method, of class DefaultStopList.
     */
    @Test
    public void testContains_Termable()
    {
        DefaultStopList instance = new DefaultStopList();
        
        for (String word : TEST_WORDS)
        {
            assertFalse(instance.contains(new DefaultTermOccurrence(new DefaultTerm(word), 0, 0)));
            assertFalse(instance.contains(new DefaultTermOccurrence(new DefaultTerm(word.toUpperCase()), 0, 0)));
        }

        instance.addAll(TEST_WORDS);

        for (String word : TEST_WORDS)
        {
            assertTrue(instance.contains(new DefaultTermOccurrence(new DefaultTerm(word), 0, 0)));
            assertTrue(instance.contains(new DefaultTermOccurrence(new DefaultTerm(word.toUpperCase()), 0, 0)));
        }
    }

    /**
     * Test of contains method, of class DefaultStopList.
     */
    @Test
    public void testContains_Term()
    {
        // Tested by testTermable.
    }

    /**
     * Test of contains method, of class DefaultStopList.
     */
    @Test
    public void testContains_String()
    {
        // Tested by testAdd.
    }

    /**
     * Test of getWords method, of class DefaultStopList.
     */
    @Test
    public void testGetWords()
    {
        DefaultStopList instance = new DefaultStopList();
        assertTrue(instance.getWords().isEmpty());

        instance.add("TEST");
        assertEquals(1, instance.getWords().size());
        assertTrue(instance.getWords().contains("test"));
    }


    /**
     * Test of saveAsText method, of class DefaultStopList.
     * @throws Exception
     */
    @Test
    public void testSaveAsText_File()
        throws Exception
    {
        DefaultStopList instance = new DefaultStopList(TEST_WORDS);
        File temp = File.createTempFile(this.getClass().getSimpleName(), null);
        temp.deleteOnExit();
        try
        {
            instance.saveAsText(temp);
            instance = DefaultStopList.loadFromText(temp);
            assertTestStopList(instance);
        }
        finally
        {
            temp.delete();
        }
    }

    /**
     * Test of loadFromText method, of class DefaultStopList.
     * @throws Exception
     */
    @Test
    public void testloadFromText_File()
        throws Exception
    {
        URL url = ClassLoader.getSystemResource(TEST_FILE_NAME);

        DefaultStopList result = DefaultStopList.loadFromText(new File(url.getFile()));
        assertTestStopList(result);
    }

    /**
     * Test of loadFromText method, of class DefaultStopList.
     * @throws Exception
     */
    @Test
    public void testloadFromText_URI()
        throws Exception
    {
        URL url = ClassLoader.getSystemResource(TEST_FILE_NAME);

        DefaultStopList result = DefaultStopList.loadFromText(url.toURI());
        assertTestStopList(result);
    }

    /**
     * Test of loadFromText method, of class DefaultStopList.
     * @throws Exception
     */
    @Test
    public void testloadFromText_URL()
        throws Exception
    {
        URL url = ClassLoader.getSystemResource(TEST_FILE_NAME);

        DefaultStopList result = DefaultStopList.loadFromText(url);
        assertTestStopList(result);
    }

    /**
     * Asserts that the given instance has an equal stop list to the test one.
     *
     * @param   instance
     *      An instance.
     */
    public void assertTestStopList(
        final DefaultStopList instance)
    {
        final Set<String> words = instance.getWords();
        assertEquals(TEST_WORDS.size(), words.size());

        for (String word : TEST_WORDS)
        {
            assertTrue(instance.contains(word));
            assertTrue(words.contains(word));
        }
    }

}
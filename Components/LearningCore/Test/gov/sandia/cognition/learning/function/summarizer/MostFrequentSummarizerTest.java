/*
 * File:                MostFrequentSummarizerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright April 07, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.summarizer;

import java.util.LinkedList;
import junit.framework.TestCase;

/**
 * Tests of MostFrequentSummarizer
 * @author  Justin Basilico
 * @since   3.0
 */
public class MostFrequentSummarizerTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public MostFrequentSummarizerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class MostFrequentSummarizer.
     */
    public void testConstructors()
    {
        MostFrequentSummarizer<String> instance =
            new MostFrequentSummarizer<String>();
        assertNotNull( instance );
    }

    public void testClone()
    {
        System.out.println( "Clone" );
        MostFrequentSummarizer<String> instance =
            new MostFrequentSummarizer<String>();
        @SuppressWarnings("unchecked")
        MostFrequentSummarizer<String> clone =
            (MostFrequentSummarizer<String>) instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );

    }

    /**
     * Test of summarize method, of class MostFrequentSummarizer.
     */
    public void testSummarize()
    {
        MostFrequentSummarizer<String> instance = new MostFrequentSummarizer<String>();
        LinkedList<String> data = new LinkedList<String>();

        assertNull(instance.summarize(data));

        data.add("a");
        assertEquals("a", instance.summarize(data));

        data.add("b");
        assertEquals("a", instance.summarize(data));

        data.add("b");
        assertEquals("b", instance.summarize(data));

        data.add("a");
        assertEquals("a", instance.summarize(data));

        data.add("c");
        data.add("a");
        data.add("c");
        data.add("c");
        data.add("c");
        assertEquals("c", instance.summarize(data));
    }

}

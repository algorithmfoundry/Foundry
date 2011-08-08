/*
 * File:                ReaderTokenizerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright September 21, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */
package gov.sandia.cognition.io;

import gov.sandia.cognition.io.ReaderTokenizer;
import static gov.sandia.cognition.math.matrix.VectorReaderTest.TEST_FILENAME;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;

import junit.framework.TestCase;

/**
 * Unit tests for ReaderTokenizerTest
 * 
 * @author Kevin R. Dixon
 * @since 1.0
 */
public class ReaderTokenizerTest extends TestCase
{
    private ReaderTokenizer tokenizer;
    private Reader fileReader;

    @Override
    protected void setUp() throws Exception
    {
        final URL fileURL = ClassLoader.getSystemResource(TEST_FILENAME);

        this.fileReader = new InputStreamReader(
            ClassLoader.getSystemResourceAsStream(TEST_FILENAME));
        this.tokenizer = this.getReaderTokenizer(this.fileReader);
    }

    @Override
    protected void tearDown()
    {
        this.tokenizer = null;
    }

    /**
     * Test of isValid method, of class gov.sandia.isrc.util.ReaderTokenizer.
     */
    public void testIsValid()
    {
        System.out.println("isValid");

        assertTrue("Tokenizer is not valid.", this.tokenizer.isValid());

        this.tokenizer = this.getReaderTokenizer(null);
        assertFalse("Tokenizer is valid.", this.tokenizer.isValid());
    }

    /**
     * Test of setValid method, of class gov.sandia.isrc.util.ReaderTokenizer.
     */
    public void testSetValid()
    {
        System.out.println("setValid");

        final boolean result = this.tokenizer.isValid();
        assertTrue(result == this.tokenizer.isValid());

        this.tokenizer.setValid(!result);
        assertFalse(result == this.tokenizer.isValid());
    }

    /**
     * Test of tokenizeString method, of class
     * gov.sandia.isrc.util.ReaderTokenizer.
     */
    public void testTokenizeString()
    {
        System.out.println("tokenizeString");

        final String data = "There should be 8 tokens\tin this\nstring";

        final ArrayList<String> tokens = ReaderTokenizer.tokenizeString(data);
        assertEquals(8, tokens.size());

        assertEquals(tokens.get(0), "There");
        assertEquals(tokens.get(3), "8");
        assertEquals(tokens.get(5), "in");

        final ArrayList<String> tokens2 = ReaderTokenizer.tokenizeString(data,
                tokens.size());
        assertEquals(tokens.toString(), tokens2.toString());

        final ArrayList<String> tokens3 = ReaderTokenizer.tokenizeString(data,
                100);
        assertEquals(tokens.toString(), tokens3.toString());

        final ArrayList<String> tokens4 = ReaderTokenizer.tokenizeString(data,
                0);
        assertEquals(tokens.toString(), tokens4.toString());

        try
        {
            ReaderTokenizer.tokenizeString(data, -1);
            fail("Should have thrown exception");
        } catch (final IllegalArgumentException e)
        {

            System.out.println(e.getClass() + e.getMessage());

            System.out.println("Good! Threw exception");
        }

        final String data2 = "";
        final ArrayList<String> tokens6 = ReaderTokenizer.tokenizeString(data2);
        assertEquals(0, tokens6.size());
    }

    /**
     * Test of readNextLine method, of class
     * gov.sandia.isrc.util.ReaderTokenizer.
     */
    public void testReadNextLine() throws Exception
    {
        System.out.println("readNextLine");

        ArrayList<String> tokens;
        while (this.tokenizer.isValid())
        {
            tokens = this.tokenizer.readNextLine();
        }

        tokens = this.tokenizer.readNextLine();
        assertNull(tokens);
    }

    /**
     * Test of getLastTokenNum method, of class
     * gov.sandia.isrc.util.ReaderTokenizer.
     */
    public void testGetLastTokenNum() throws Exception
    {
        System.out.println("getLastTokenNum");

        final ArrayList<String> tokens = this.tokenizer.readNextLine();

        final int expected = tokens.size();
        assertEquals(expected, this.tokenizer.getLastTokenNum());
    }

    /**
     * Test of setLastTokenNum method, of class
     * gov.sandia.isrc.util.ReaderTokenizer.
     */
    public void testSetLastTokenNum() throws Exception
    {
        System.out.println("setLastTokenNum");

        final int a = this.tokenizer.getLastTokenNum();

        final int b = (int) ((Math.random() * 100.0) + a + 1);

        this.tokenizer.setLastTokenNum(b);
        assertEquals(b, this.tokenizer.getLastTokenNum());
    }

    /**
     * Test of getBufferedReader method, of class
     * gov.sandia.isrc.util.ReaderTokenizer.
     */
    public void testGetBufferedReader() throws Exception
    {
        System.out.println("getBufferedReader");

        assertNotNull(this.tokenizer.getBufferedReader());

        this.tokenizer = new ReaderTokenizer(null);
        assertNull(this.tokenizer.getBufferedReader());
    }

    /**
     * Test of setBufferedReader method, of class
     * gov.sandia.isrc.util.ReaderTokenizer.
     */
    public void testSetBufferedReader() throws Exception
    {
        System.out.println("setBufferedReader");

        final BufferedReader bufferedReader = new BufferedReader(
                this.fileReader);

        assertNotSame(bufferedReader, this.tokenizer.getBufferedReader());

        this.tokenizer.setBufferedReader(bufferedReader);

        assertSame(bufferedReader, this.tokenizer.getBufferedReader());

    }

    private ReaderTokenizer getReaderTokenizer(final Reader reader)
    {
        return new ReaderTokenizer(reader);
    }

}

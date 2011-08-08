/*
 * File:                VectorReaderTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 3, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.math.matrix;

import gov.sandia.cognition.io.ReaderTokenizer;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * 
 * @author Kevin R. Dixon
 */
public class VectorReaderTest
    extends TestCase
{
    /** Default filename. */
    public static final String TEST_FILENAME = "gov/sandia/cognition/math/matrix/mtj/matrixTest.txt";
    
    
    /** Test with comments file name. */
    public static final String TEST_WITH_COMMENTS_FILENAME = "gov/sandia/cognition/math/matrix/mtj/matrixTestWithComments.txt";

    /** Empty filename. */
    public static final String EMPTY_FILENAME = "gov/sandia/cognition/math/matrix/mtj/matrixTestEmpty.txt";

    private Reader fileReader;
    private VectorReader vReader;

    @Override
    protected void setUp() throws Exception
    {
        this.fileReader = this.getReader(TEST_FILENAME);
        this.vReader = this.getVectorReader(this.fileReader);
    }

    @Override
    protected void tearDown() throws Exception
    {
        this.vReader = null;
    }

    /**
     * Test of read method, of class
     * gov.sandia.isrc.math.matrix.mtj.VectorReader.
     */
    public void testRead()
        throws Exception
    {
        System.out.println("read");

        try
        {
            Vector vector = this.vReader.read();
            System.out.println("Vector: " + vector);
            vector = this.vReader.read();
            System.out.println("Vector: " + vector);
        } catch (final Exception ex)
        {
            ex.printStackTrace();
            fail("Threw exception: " + ex);
        }

        try
        {
            // Null reader.
            this.getVectorReader(null);
        } catch (final Exception e)
        {
            System.out.println("Good!  Threw exception: " + e);
        }

        final VectorReader reader3 = this.getVectorReader(
            this.getReader(EMPTY_FILENAME));
        try
        {
            reader3.read();
        } catch (final Exception e)
        {
            System.out.println("Good!  Threw exception: " + e);
        }

    }

    /**
     * Test of readCollection method, of class
     * gov.sandia.isrc.math.matrix.mtj.VectorReader.
     */
    public void testReadCollection() throws Exception
    {
        System.out.println("readCollection");

        final Vector vector0 = this.vReader.read();
        final Vector vector1 = this.vReader.read();
        this.runReadCollection(TEST_FILENAME, vector0, vector1);
        this.runReadCollection(TEST_WITH_COMMENTS_FILENAME, vector0, vector1);
    }
    
    public void runReadCollection(
        final String fileName,
        final Vector vector0,
        final Vector vector1)
        throws Exception
    {

        final Reader newReader = this.getReader(fileName);
        final VectorReader reader2 = new VectorReader(newReader);
        final boolean mustBeSameSize = true;
        final List<Vector> vectors = reader2.readCollection(mustBeSameSize);

        assertEquals("The number of Vectors is not 10.", 10, vectors.size());
        assertEquals("The Vector is not vector0.", vector0, vectors.get(0));
        assertEquals("The Vector is not vector1.", vector1, vectors.get(1));
    }

    /**
     * Test of getTokenizer method, of class
     * gov.sandia.isrc.math.matrix.mtj.VectorReader.
     */
    public void testGetTokenizer() throws Exception
    {
        System.out.println("getTokenizer");

        assertNotNull("The tokenizer is null.", this.vReader.getTokenizer());
        assertTrue("The tokenizer is not valid.", this.vReader.getTokenizer()
                .isValid());
    }

    /**
     * Test of setTokenizer method, of class
     * gov.sandia.isrc.math.matrix.mtj.VectorReader.
     */
    public void testSetTokenizer() throws Exception
    {
        System.out.println("setTokenizer");

        assertNotNull("The tokenizer is null.", this.vReader.getTokenizer());

        final ReaderTokenizer tokenizer2 = new ReaderTokenizer(this
                .getReader(TEST_FILENAME));
        assertNotSame("The tokenizers are the same.", this.vReader
                .getTokenizer(), tokenizer2);

        this.vReader.setTokenizer(tokenizer2);
        assertSame("The tokenizers are not the same.", this.vReader
                .getTokenizer(), tokenizer2);
    }

    private Reader getReader(final String fileName)
            throws FileNotFoundException
    {
        return new InputStreamReader(
            ClassLoader.getSystemResourceAsStream(fileName));
    }

    private VectorReader getVectorReader(final Reader reader)
    {
        return new VectorReader(reader);
    }

    public void testParseVector()
    {
        System.out.println( "parseVector" );

        assertNull( VectorReader.parseVector(null) );
        assertNull( VectorReader.parseVector( new ArrayList<String>() ) );

    }

}

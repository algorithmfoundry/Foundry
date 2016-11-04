/*
 * File:                MatrixReaderTest.java
 * Authors:             Keven R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2006, Sandia Corporation. Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.math.matrix;

import static gov.sandia.cognition.math.matrix.VectorReaderTest.TEST_FILENAME;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

/**
 * 
 * @author Kevin R. Dixon
 */
public class MatrixReaderTest extends TestCase
{
    private MatrixReader mReader;

    @Override
    protected void setUp() throws Exception
    {
        this.mReader = this.getMatrixReader(TEST_FILENAME);
    }

    @Override
    protected void tearDown() throws Exception
    {
        this.mReader = null;
    }

    /**
     * Test of getInternalReader method, of class
     * gov.sandia.isrc.math.matrix.mtj.MatrixReader.
     */
    public void testGetInternalReader()
    {
        System.out.println("getInternalReader");

        try
        {
            assertNotNull("VectorReader is null.", this.mReader
                    .getInternalReader());
        } catch (final Exception ex)
        {
            ex.printStackTrace();
            fail("Threw exception: " + ex);
        }
    }

    /**
     * Test of setInternalReader method, of class
     * gov.sandia.isrc.math.matrix.mtj.MatrixReader.
     */
    public void testSetInternalReader()
    {
        System.out.println("setInternalReader");

        try
        {
            assertNotNull("VectorReader is null.", this.mReader
                    .getInternalReader());

            this.mReader.setInternalReader(null);
            assertNull("VectorReader is not null.", this.mReader
                    .getInternalReader());
        } catch (final Exception ex)
        {
            ex.printStackTrace();
            fail("Threw exception: " + ex);
        }
    }

    /**
     * Test of read method, of class
     * gov.sandia.isrc.math.matrix.mtj.MatrixReader.
     */
    public void testRead() throws Exception
    {
        System.out.println("read");

        try
        {
            final Matrix matrix = this.mReader.read();
            System.out.println("Matrix: " + matrix);
        } catch (final Exception ex)
        {
            ex.printStackTrace();
            fail("Threw exception: " + ex);
        }
    }

    private Reader getReader(final String fileName)
            throws FileNotFoundException
    {
        return new InputStreamReader(
            ClassLoader.getSystemResourceAsStream(fileName));
    }

    private MatrixReader getMatrixReader(final String fileName)
            throws FileNotFoundException, IOException
    {
        return new MatrixReader(this.getReader(fileName));
    }

}

/*
 * File:                ObjectSerializationHandlerTest.java
 * Authors:             Benjamin Currier
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 12, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */
package gov.sandia.cognition.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import junit.framework.TestCase;

/**
 * Test class for {@link gov.sandia.cognition.io.ObjectSerializationHandler}.
 */
public class ObjectSerializationHandlerTest
    extends TestCase
{


    public void testConstructors()
    {
        System.out.println( "Constructors" );

        ObjectSerializationHandler handler =
            new ObjectSerializationHandler();
        assertNotNull( handler );
    }

    /**
     * Test method for
     * {@link gov.sandia.cognition.io.ObjectSerializationHandler#convertToBytes(java.lang.Object)}.
     */
    public final void testConvertToBytes()
        throws IOException,
        ClassNotFoundException
    {
        final Date testDate = new Date();
        final String testString = "testString";

        final byte[] byteArrayDate = ObjectSerializationHandler
                .convertToBytes(testDate);
        final byte[] byteArrayString = ObjectSerializationHandler
                .convertToBytes(testString);
        final byte[] byteArrayNull = ObjectSerializationHandler.convertToBytes(null);

        assertTrue(new String(byteArrayDate).indexOf(Date.class.getName()) > -1);
        assertTrue(new String(byteArrayString).indexOf(testString) > -1);
        assertNull(byteArrayNull);

        final Object objectDate = ObjectSerializationHandler
                .convertFromBytes(byteArrayDate);
        final Object objectString = ObjectSerializationHandler
                .convertFromBytes(byteArrayString);

        assertTrue(objectDate instanceof Date);
        assertTrue(objectString instanceof String);

        final Date actualDate = (Date) objectDate;
        final String actualString = (String) objectString;

        assertEquals(testDate, actualDate);
        assertEquals(testString, actualString);
    }

    /**
     * Test method for
     * {@link gov.sandia.cognition.io.ObjectSerializationHandler#convertFromBytes(byte[])}.
     */
    public final void testConvertFromBytes()
        throws IOException,
            ClassNotFoundException
    {
        final Date testDate = new Date();
        final String testString = "testString";

        final byte[] byteArrayDate = ObjectSerializationHandler
                .convertToBytes(testDate);
        final byte[] byteArrayString = ObjectSerializationHandler
                .convertToBytes(testString);
        final byte[] byteArrayNull = ObjectSerializationHandler.convertToBytes(null);

        final byte[] byteArrayTwo = ObjectSerializationHandlerTest.join(
                byteArrayDate, byteArrayString);

        final Object objectNull = ObjectSerializationHandler
                .convertFromBytes(byteArrayNull);
        final Object objectDate = ObjectSerializationHandler
                .convertFromBytes(byteArrayDate);
        final Object objectString = ObjectSerializationHandler
                .convertFromBytes(byteArrayString);

        try
        {
            ObjectSerializationHandler.convertFromBytes(byteArrayTwo);
            fail("IOException was not caught!");
        } catch (final IOException ioe)
        {
            assertEquals(ioe.getMessage(),
                    "There was more than the object in the given byte array.");
        }

        assertNull(objectNull);
        assertTrue(objectDate instanceof Date);
        assertTrue(objectString instanceof String);

        final Date actualDate = (Date) objectDate;
        final String actualString = (String) objectString;

        assertEquals(testDate, actualDate);
        assertEquals(testString, actualString);
    }

    /**
     * Test method for
     * {@link gov.sandia.cognition.io.ObjectSerializationHandler#readFromFile(java.io.File)}.
     */
    public final void testReadFromFile()
        throws IOException,
            ClassNotFoundException
    {
        final File testFileDate = new File("testDate.txt");
        final File testFileString = new File("testString.txt");

        final Date testDate = new Date();
        final String testString = "testString";

        ObjectSerializationHandler.writeObjectToFile(testFileDate, testDate);
        ObjectSerializationHandler
                .writeObjectToFile(testFileString, testString);

        final Object objectDate = ObjectSerializationHandler
                .readFromFile(testFileDate);
        final Object objectString = ObjectSerializationHandler
                .readFromFile(testFileString);
        final Object objectNull = ObjectSerializationHandler.readFromFile(null);

        assertTrue(objectDate instanceof Date);
        assertTrue(objectString instanceof String);
        assertNull(objectNull);

        final Date actualDate = (Date) objectDate;
        final String actualString = (String) objectString;

        assertEquals(testDate, actualDate);
        assertEquals(testString, actualString);

        testFileDate.deleteOnExit();
        testFileString.deleteOnExit();
    }

    /**
     * Test method for
     * {@link gov.sandia.cognition.io.ObjectSerializationHandler#readFromStream(java.io.BufferedInputStream)}.
     */
    public final void testReadFromStream()
        throws IOException,
            ClassNotFoundException
    {
        final File testFileDate = new File("testDate.txt");
        final File testFileString = new File("testString.txt");

        final Date testDate = new Date();
        final String testString = "testString";

        ObjectSerializationHandler.writeObjectToFile(testFileDate, testDate);
        ObjectSerializationHandler
                .writeObjectToFile(testFileString, testString);

        final BufferedInputStream testFileDateStream = new BufferedInputStream(
                new FileInputStream(testFileDate));
        final BufferedInputStream testFileStringStream = new BufferedInputStream(
                new FileInputStream(testFileString));

        final Object objectDate = ObjectSerializationHandler
                .readFromStream(testFileDateStream);
        final Object objectString = ObjectSerializationHandler
                .readFromStream(testFileStringStream);
        final Object objectNull = ObjectSerializationHandler.readFromStream(null);

        assertTrue(objectDate instanceof Date);
        assertTrue(objectString instanceof String);
        assertNull(objectNull);

        final Date actualDate = (Date) objectDate;
        final String actualString = (String) objectString;

        assertEquals(testDate, actualDate);
        assertEquals(testString, actualString);

        testFileDate.deleteOnExit();
        testFileString.deleteOnExit();
    }

    /**
     * Test method for
     * {@link gov.sandia.cognition.io.ObjectSerializationHandler#writeObjectToFile(java.io.File, java.io.Serializable)}.
     */
    public final void testWriteObjectToFile()
        throws IOException,
            ClassNotFoundException
    {
        final File testFileDate = new File("testDate.txt");
        final File testFileString = new File("testString.txt");
        final File testFileNull = new File("testNull.txt");

        final Date testDate = new Date();
        final String testString = "testString";

        ObjectSerializationHandler.writeObjectToFile(testFileDate, testDate);
        ObjectSerializationHandler
                .writeObjectToFile(testFileString, testString);
        ObjectSerializationHandler.writeObjectToFile(testFileNull, null);

        try
        {
            ObjectSerializationHandler.writeObjectToFile(null, testString);
            fail("IOException was not caught!");
        } catch (final IOException ioe)
        {
            assertEquals(ioe.getMessage(), "File is null!");
        }

        final byte[] byteArrayDate = this.getBytesFromFile(testFileDate);
        final byte[] byteArrayString = this.getBytesFromFile(testFileString);

        try
        {
            this.getBytesFromFile(testFileNull);
            fail("FileNotFoundException was not caught!");
        } catch (final FileNotFoundException fnfe)
        {
            assertTrue(fnfe.getMessage().contains("testNull.txt"));
        }

        assertTrue(new String(byteArrayDate).indexOf(Date.class.getName()) > -1);
        assertTrue(new String(byteArrayString).indexOf(testString) > -1);

        final Object objectDate = ObjectSerializationHandler
                .readFromFile(testFileDate);
        final Object objectString = ObjectSerializationHandler
                .readFromFile(testFileString);

        assertTrue(objectDate instanceof Date);
        assertTrue(objectString instanceof String);

        final Date actualDate = (Date) objectDate;
        final String actualString = (String) objectString;

        assertEquals(testDate, actualDate);
        assertEquals(testString, actualString);

        testFileDate.deleteOnExit();
        testFileString.deleteOnExit();
        testFileNull.deleteOnExit();
    }

    private byte[] getBytesFromFile(final File file)
        throws IOException
    {

        final InputStream is = new FileInputStream(file);

        // Get the size of the file
        final long length = file.length();

        // Create the byte array to hold the data
        final byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0)
        {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length)
        {
            throw new IOException("Could not completely read file "
                    + file.getName());
        }

        // Close the input stream and return bytes
        is.close();

        return bytes;
    }

    private static byte[] join(final byte[] a, final byte[] b)
    {
        final byte[] bytes = new byte[a.length + b.length];

        System.arraycopy(a, 0, bytes, 0, a.length);
        System.arraycopy(b, 0, bytes, a.length, b.length);

        return bytes;
    }

}

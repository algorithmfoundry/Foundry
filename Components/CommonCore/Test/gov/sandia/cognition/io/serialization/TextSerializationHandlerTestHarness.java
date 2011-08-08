/*
 * File:                TextSerializationHandlerTestHarness.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 16, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.io.serialization;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Test harness for TextSerializationHandler interface.
 *
 * @param   <SerializedType>
 *      The type of object that can be serialized.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class TextSerializationHandlerTestHarness<SerializedType>
    extends StreamSerializationHandlerTestHarness<SerializedType>
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public TextSerializationHandlerTestHarness(
        String testName)
    {
        super(testName);
    }

    public abstract TextSerializationHandler<SerializedType> createInstance();

    /**
     * Test of writeObject method, of class TextSerializationHandler.
     * @throws  Exception
     */
    public void testWriteObject_Writer()
        throws Exception
    {
        final File file = File.createTempFile(
            "TEMP-" + this.getClass().getSimpleName() + "-testWriteObject_Writer",
            "temp");
        file.deleteOnExit();
        final TextSerializationHandler<SerializedType> instance =
            this.createInstance();
        for (SerializedType original : this.createTestObjectList())
        {
            // Write the object.
            FileWriter out = new FileWriter(file);
            out.write(4);
            instance.writeObject(out, original);
            out.close();

            // Read the object.
            FileReader in = new FileReader(file);
            assertEquals(4, in.read());
            Object deserialized = instance.readObject(in);
            in.close();

            assertEquals(original, deserialized);
            assertNotNull(deserialized);
        }
        file.delete();
    }

    /**
     * Test of readObject method, of class TextSerializationHandler.
     * 
     * @throws  Exception
     */
    public void testReadObject_Reader()
        throws Exception
    {
        // Tested by testWriteObject_Writer
        this.testWriteObject_Writer();
    }

    /**
     * Test of convertToString method, of class TextSerializationHandler.
     *
     * @throws  Exception
     */
    public void testConvertToString()
        throws Exception
    {
        final TextSerializationHandler<SerializedType> instance =
            this.createInstance();
        for (SerializedType original : this.createTestObjectList())
        {
            // Write to the bytes.
            final String s = instance.convertToString(original);
            Object deserialized = instance.convertFromString(s);
            assertEquals(original, deserialized);
            assertNotNull(deserialized);
        }
    }

    /**
     * Test of convertFromString method, of class TextSerializationHandler.
     *
     * @throws  Exception
     */
    public void testConvertFromString()
        throws Exception
    {
        // Tested by testConvertToString().
        this.testConvertToString();
    }

}

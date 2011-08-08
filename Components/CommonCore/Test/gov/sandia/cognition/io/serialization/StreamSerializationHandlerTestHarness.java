/*
 * File:                StreamSerializationHandlerTestHarness.java
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
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Test harness for StreamSerializationHandler interface.
 *
 * @param   <SerializedType>
 *      The type of object that can be serialized.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class StreamSerializationHandlerTestHarness<SerializedType>
    extends FileSerializationHandlerTestHarness<SerializedType>
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public StreamSerializationHandlerTestHarness(
        String testName)
    {
        super(testName);
    }

    public abstract StreamSerializationHandler<SerializedType> createInstance();

    /**
     * Test of writeObject method, of class StreamSerializationHandler.
     *
     * @throws Exception
     */
    public void testWriteObject()
        throws Exception
    {
        final File file = File.createTempFile("TEMP-" + this.getClass().getSimpleName() + "-testWriteObject", "temp");
        file.deleteOnExit();
        final StreamSerializationHandler<SerializedType> instance =
            this.createInstance();
        for (SerializedType original : this.createTestObjectList())
        {
            FileOutputStream out = new FileOutputStream(file);
            out.write(4);
            instance.writeObject(out, original);
            out.close();

            // Write to the bytes.
            FileInputStream in = new FileInputStream(file);
            assertEquals(4, in.read());
            Object deserialized = instance.readObject(in);
            in.close();
            
            assertEquals(original, deserialized);
            assertNotNull(deserialized);
        }
        file.delete();
    }

    /**
     * Test of readObject method, of class StreamSerializationHandler.
     *
     * @throws Exception
     */
    public void testReadObject()
        throws Exception
    {
        // Tested by testWriteObject.
        this.testWriteObject();
    }

    /**
     * Test of convertToBytes method, of class StreamSerializationHandler.
     *
     * @throws Exception
     */
    public void testConvertToBytes()
        throws Exception
    {
        final StreamSerializationHandler<SerializedType> instance =
            this.createInstance();
        for (SerializedType original : this.createTestObjectList())
        {
            // Write to the bytes.
            final byte[] bytes = instance.convertToBytes(original);
            Object deserialized = instance.convertFromBytes(bytes);
            assertEquals(original, deserialized);
            assertNotNull(deserialized);
        }
    }

    /**
     * Test of convertFromBytes method, of class StreamSerializationHandler.
     *
     * @throws Exception
     */
    public void testConvertFromBytes()
        throws Exception
    {
        // Tested by convertToBytes.
        this.testConvertToBytes();
    }

}

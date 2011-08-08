/*
 * File:                AbstractStreamSerializationHandler.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.io.serialization;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * An abstract implementation of {@code StreamSerializationHandler}. Handles
 * the file and byte reading aspects so all you need to implement is the write
 * and read methods.
 *
 * @param   <SerializedType>
 *      The type of object that can be serialized.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractStreamSerializationHandler<SerializedType>
    extends AbstractFileSerializationHandler<SerializedType>
    implements StreamSerializationHandler<SerializedType>
{

    /**
     * Creates a new {@code AbstractStreamSerializationHandler}.
     */
    public AbstractStreamSerializationHandler()
    {
        super();
    }

    public void writeToFile(
        final File file,
        final SerializedType object)
        throws IOException
    {
        if (file == null)
        {
            throw new IOException("file cannot be null");
        }
        else if (object == null)
        {
            throw new IOException("object cannot be null");
        }

        // Create the output stream.
        final FileOutputStream out = new FileOutputStream(file);
        try
        {
            // Write the object.
            writeObject(out, object);
        }
        finally
        {
            out.close();
        }
    }

    public Object readFromFile(
        final File file)
        throws IOException, ClassNotFoundException
    {
        final BufferedInputStream in = new BufferedInputStream(
            new FileInputStream(file));

        // Read the object.
        final Object read;
        try
        {
            read = readObject(in);
        }
        finally
        {
            in.close();
        }

        return read;
    }


    public byte[] convertToBytes(
        final SerializedType object)
        throws IOException
    {
        byte[] byteArray = null;

        if (object != null)
        {

            // We are going to write the object to a byte array.
            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            try
            {
                // Write the object to the stream.
                this.writeObject(byteStream, object);
            }
            finally
            {
                byteStream.close();
            }

            // Return the array of bytes that we have written.
            byteArray = byteStream.toByteArray();
        }

        return byteArray;
    }

    public Object convertFromBytes(
        final byte[] bytes)
        throws IOException, ClassNotFoundException
    {
        if (bytes == null)
        {
            // Error: No bytes were returned.
            return null;
        }

        // Read in an object using Java's serialization.

        // Create an input stream from the given bytes.
        final ByteArrayInputStream byteStream = new ByteArrayInputStream(
            bytes);

        final Object read;
        try
        {
            // Read the object.
            read = this.readObject(byteStream);
// This code used to be in here to warn people when they are reading from an
// array that has more than one object. However, sometimes there can be extra
// bytes available even if there is only one object, so it is now commented
// out.
//            if (byteStream.available() > 0)
//            {
//                // We didn't read all of the object.
//                throw new IOException(
//                    "There was more than the object in the given byte array.");
//            }
        }
        finally
        {
            // Close the stream we created.
            byteStream.close();
        }

        // We successfully deserialized the object.
        return read;
    }



}

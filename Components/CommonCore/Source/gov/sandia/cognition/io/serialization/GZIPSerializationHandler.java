/*
 * File:                GZIPSerializationHandler.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Implements a serialization handler that uses the GZip compression algorithm
 * on the output. Can wrap any stream serialization handler.
 *
 * @param   <SerializedType>
 *      The type of object that can be serialized.
 * @author  Justin Basilico
 * @since   3.0
 */
public class GZIPSerializationHandler<SerializedType>
    extends AbstractStreamSerializationHandler<SerializedType>
{

    /** The base handler that is being wrapped in a GZip. */
    protected StreamSerializationHandler<SerializedType> baseHandler;

    /**
     * Creates a new {@code GZIPSerializationHandler} that uses the default
     * {@code JavaBinarySerializationHandler}.
     */
    public GZIPSerializationHandler()
    {
        this(null);
    }

    /**
     * Creates a new {@code GZIPSerializationHandler} that will use the given
     * base handler.
     *
     * @param   baseHandler
     *      The base handler to use with the gzip.
     */
    public GZIPSerializationHandler(
        final StreamSerializationHandler<SerializedType> baseHandler)
    {
        super();

        this.baseHandler = baseHandler;
    }

    public void writeObject(
        final OutputStream stream,
        final SerializedType object)
        throws IOException
    {
        // Create the gzipped version of the stream.
        final GZIPOutputStream gzip = new GZIPOutputStream(stream);
        try
        {
            // Write the object to the stream.
            this.baseHandler.writeObject(gzip, object);
        }
        finally
        {
            // Finish writing to the stream.
            gzip.finish();
        }
    }

    public Object readObject(
        final InputStream stream)
        throws IOException, ClassNotFoundException
    {
        final Object result;

        // Create the gzipped version of the stream.
        final GZIPInputStream gzip = new GZIPInputStream(stream);
        try
        {
            // Read from the stream.
            result = this.baseHandler.readObject(gzip);
        }
        finally
        {
            // Close the stream since we're done with it.
            gzip.close();
        }

        // Return what we've read.
        return result;
    }

    /**
     * Gets the base handler whose output is serialized.
     *
     * @return
     *      The base handler.
     */
    public StreamSerializationHandler<SerializedType> getBaseHandler()
    {
        return baseHandler;
    }


    /**
     * Sets the base handler whose output is serialized.
     *
     * @param   baseHandler
     *      The base handler.
     */
    public void setBaseHandler(
        final StreamSerializationHandler<SerializedType> baseHandler)
    {
        this.baseHandler = baseHandler;
    }

}

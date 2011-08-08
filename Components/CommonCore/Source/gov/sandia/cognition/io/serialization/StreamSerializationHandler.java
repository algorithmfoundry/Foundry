/*
 * File:                StreamSerializationHandler.java
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

/**
 * Interface for an object that can be used to serialize and deserialize
 * objects.
 *
 * @param   <SerializedType>
 *      The type of object that can be serialized.
 * @author  Justin Basilico
 * @since   3.0
 */
public interface StreamSerializationHandler<SerializedType>
    extends FileSerializationHandler<SerializedType>
{

    /**
     * Writes an object to a given output stream.
     *
     * @param   stream
     *      The stream to write the object to.
     * @param   object
     *      The object to write.
     * @throws  IOException
     *      If there is an i/o error.
     */
    public void writeObject(
        final OutputStream stream,
        final SerializedType object)
        throws IOException;

    /**
     * Reads an object from the given stream.
     *
     * @param   stream
     *      The stream to read an object from.
     * @return
     *      The object read from the stream.
     * @throws IOException
     *      If there is an i/o error.
     * @throws ClassNotFoundException
     *      If a class cannot be found.
     */
    public Object readObject(
        final InputStream stream)
        throws IOException, ClassNotFoundException;

    /**
     * Converts the given object to bytes.
     *
     * @param   object
     *      The object to convert to bytes.
     * @return
     *      The byte representation of the object.
     * @throws  IOException
     *      If there is an i/o error.
     */
    public byte[] convertToBytes(
        final SerializedType object)
        throws IOException;

    /**
     * Converts the first given object in the given byte array.
     *
     * @param   bytes
     *      The bytes to convert the object from.
     * @return
     *      The first object represented by the given bytes.
     * @throws IOException
     *      If there is an i/o error.
     * @throws ClassNotFoundException
     *      If a class cannot be found.
     */
    public Object convertFromBytes(
        final byte[] bytes)
        throws IOException, ClassNotFoundException;
}

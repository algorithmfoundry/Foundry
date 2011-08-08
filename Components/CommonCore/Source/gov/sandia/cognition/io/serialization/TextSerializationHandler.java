/*
 * File:                TextSerializationHandler.java
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
import java.io.Reader;
import java.io.Writer;

/**
 * Interface for a serialization handler that can serialize to text. Since it
 * can do text, it is assumed that it can also stream and write to a file.
 *
 * @param   <SerializedType>
 *      The type of object that can be serialized.
 * @author  Justin Basilico
 * @since   3.0
 * @see     AbstractTextSerializationHandler
 * @see     XStreamSerializationHandler
 */
public interface TextSerializationHandler<SerializedType>
    extends StreamSerializationHandler<SerializedType>
{

    /**
     * Writes an object to the given writer.
     *
     * @param   writer
     *      Writer to write the object to.
     * @param   object
     *      The object to write.
     * @throws  IOException
     *      If there is an i/o error.
     */
    public void writeObject(
        final Writer writer,
        final SerializedType object)
        throws IOException;

    /**
     * Reads an object from the given reader.
     *
     * @param   reader
     *      The reader to read an object from.
     * @return
     *      The object read from the reader.
     * @throws IOException
     *      If there is an i/o error.
     * @throws ClassNotFoundException
     *      If a class cannot be found.
     */
    public Object readObject(
        final Reader reader)
        throws IOException, ClassNotFoundException;

    /**
     * Converts a given object to its serialized string representation.
     *
     * @param   object
     *      The object to serialized to a string.
     * @return
     *      The serialized string version of the object.
     * @throws IOException
     *      If there is an i/o error.
     */
    public String convertToString(
        final SerializedType object)
        throws IOException;

    /**
     * Converts an object from its serialized string representation.
     *
     * @param   string
     *      The string to convert an object from.
     * @return
     *      The object deserialized from the string.
     * @throws IOException
     *      If there is an i/o error.
     * @throws ClassNotFoundException
     *      If a class cannot be found.
     */
    public Object convertFromString(
        final String string)
        throws IOException, ClassNotFoundException;
}

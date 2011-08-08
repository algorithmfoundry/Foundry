/*
 * File:                FileSerializationHandler.java
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
import java.io.IOException;

/**
 * Defines the functionality of a serialization handler that can write an object
 * to a file and read an object from a file.
 *
 * @param   <SerializedType>
 *      The type of object that can be serialized.
 * @author  Justin Basilico
 * @since   3.0
 * @see     AbstractFileSerializationHandler
 * @see     StreamSerializationHandler
 * @see     TextSerializationHandler
 */
public interface FileSerializationHandler<SerializedType>
{

    /**
     * Writes an object to a given file.
     *
     * @param   fileName
     *      The name of the file to write the object to.
     * @param   object
     *      The object to write.
     * @throws  IOException
     *      If there is an i/o error.
     */
    public void writeToFile(
        final String fileName,
        final SerializedType object)
        throws IOException;

    /**
     * Reads an object from a given file.
     *
     * @param   file
     *      The file to write the object to.
     * @param   object
     *      The object to write.
     * @throws  IOException
     *      If there is an i/o error.
     */
    public void writeToFile(
        final File file,
        final SerializedType object)
        throws IOException;

    /**
     * Reads an object from the given file.
     *
     * @param   fileName
     *      The name of the file to read an object from.
     * @return
     *      The object read from the file.
     * @throws IOException
     *      If there is an i/o error.
     * @throws ClassNotFoundException
     *      If a class cannot be found.
     */
    public Object readFromFile(
        final String fileName)
        throws IOException, ClassNotFoundException;

    /**
     * Reads an object from the given file.
     *
     * @param   file
     *      The file to read an object from.
     * @return
     *      The object read from the file.
     * @throws IOException
     *      If there is an i/o error.
     * @throws ClassNotFoundException
     *      If a class cannot be found.
     */
    public Object readFromFile(
        final File file)
        throws IOException, ClassNotFoundException;

}

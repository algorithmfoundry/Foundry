/*
 * File:                SerializedModelHandler.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright April 27, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.io;

import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.io.ObjectSerializationHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * The <code>SerializedModelHandler</code> class implements some utility
 * methods for dealing with models that have been serialized using the Java
 * serialization API.
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class SerializedModelHandler
    extends Object
{
    /**
     * Creates a new instance of SerializedModelHandler
     */
    protected SerializedModelHandler()
    {
        super();
    }

    /**
     * Reads a serialized Java Object from the given File.
     *
     * @param  file The File to read from.
     * @return The Object read from the given File.
     * @throws FileNotFoundException If the file was not found.
     * @throws ClassNotFoundException If the class that the Object or
     *         one of its internal fields cannot be found.
     * @throws IOException If there is any other type of error reading from
     *         the file.
     */
    public static Object readObjectFromFile(
        File file)
        throws FileNotFoundException, ClassNotFoundException, IOException
    {
        return ObjectSerializationHandler.readFromFile(file);
    }

    /**
     * Reads a serialized CognitiveModel from the given file.
     *
     * @param  fileName The name of the file to read from.
     * @return The CognitiveModel read from the given File.
     * @throws FileNotFoundException If the file was not found.
     * @throws ClassNotFoundException If a class in the file cannot be found.
     * @throws IOException If there is any other type of error reading from
     *         the file.
     */
    public static CognitiveModel readModelFromFile(
        String fileName)
        throws FileNotFoundException, ClassNotFoundException, IOException
    {
        return readModelFromFile(new File(fileName));
    }

    /**
     * Reads a serialized CognitiveModel from the given file.
     *
     * @param  file The file to read from.
     * @return The CognitiveModel read from the given File.
     * @throws FileNotFoundException If the file was not found.
     * @throws ClassNotFoundException If a class in the file cannot be found.
     * @throws IOException If there is any other type of error reading from
     *         the file.
     */
    public static CognitiveModel readModelFromFile(
        File file)
        throws FileNotFoundException, ClassNotFoundException, IOException
    {
        // Read the object.
        final Object read = ObjectSerializationHandler.readFromFile(file);

        if ( !(read instanceof CognitiveModel) )
        {
            // Error: Not a Cognitive Model.
            throw new IOException("Not a valid Cognitive Model.");
        }
        else
        {
            return (CognitiveModel) read;
        }
    }

    /**
     * Writes the given CognitiveModel to the given file using Java object
     * serialization
     *
     * @param  fileName The name of the file to write to.
     * @param  model The model to serialize into the file.
     * @throws IOException If there was an error writing the object.
     */
    public static void writeModelToFile(
        String fileName,
        CognitiveModel model)
        throws IOException
    {
        writeModelToFile(new File(fileName), model);
    }

    /**
     * Writes the given CognitiveModel to the given file using Java object
     * serialization
     *
     * @param  file The File to write to.
     * @param  model The model to serialize into the file.
     * @throws IOException If there was an error writing the object.
     */
    public static void writeModelToFile(
        File file,
        CognitiveModel model)
        throws IOException
    {
        // Create the output stream.
        final ObjectOutputStream out = new ObjectOutputStream(
            new FileOutputStream(file));

        // Write the object.
        out.writeObject(model);

        // Make sure the stream gets cleaned up.
        out.flush();
        out.close();
    }
}


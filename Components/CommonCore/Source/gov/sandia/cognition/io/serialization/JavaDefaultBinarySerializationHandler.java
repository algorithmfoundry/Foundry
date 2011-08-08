/*
 * File:                JavaDefaultBinarySerializationHandler.java
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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * A serialization handler based on basic Java binary serialization.
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class JavaDefaultBinarySerializationHandler
    extends AbstractStreamSerializationHandler<Serializable>
{
    /** A singleton instance of this class since it has no internal data. It
     * is lazy-initialized. */
    private static JavaDefaultBinarySerializationHandler INSTANCE;

    /**
     * Gets a singleton instance of the class.
     *
     * @return
     *      A singleton instance.
     */
    public static JavaDefaultBinarySerializationHandler getInstance()
    {
        // Lazy-initialize the instance.
        synchronized (JavaDefaultBinarySerializationHandler.class)
        {
            if (INSTANCE == null)
            {
                INSTANCE = new JavaDefaultBinarySerializationHandler();
            }
        }

        return INSTANCE;
    }

    /**
     * Creates a new {@code JavaDefaultBinarySerializationHandler}.
     */
    public JavaDefaultBinarySerializationHandler()
    {
        super();
    }

    public void writeObject(
        final OutputStream stream,
        final Serializable object)
        throws IOException
    {
        // TODO: Does this handle null?
        
        // Create the output stream.
        final ObjectOutputStream out = new ObjectOutputStream(stream);
        
        // Flush the header.
        out.flush();

        // Write the object.
        out.writeObject(object);

        // Make sure the stream gets cleaned up.
        out.flush();
    }

    public Object readObject(
        final InputStream stream)
        throws IOException, ClassNotFoundException
    {
        Object read = null;

        if (stream != null)
        {
            // Create an input stream for reading objects.
            final ObjectInputStream in = new ObjectInputStream(stream);

            // Read the object.
            read = in.readObject();
        }

        return read;
    }
}

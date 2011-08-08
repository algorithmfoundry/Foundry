/*
 * File:                AbstractTextSerializationHandler.java
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
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * An abstract implementation of the {@code TextSerializationHandler} interface.
 * Converts stream calls to readers/writers so all you need to implement are
 * those versions of the methods.
 *
 * @param   <SerializedType>
 *      The type of object that can be serialized.
 * @author  Justin Basilico
 * @since   3.0
 */
public abstract class AbstractTextSerializationHandler<SerializedType>
    extends AbstractStreamSerializationHandler<SerializedType>
    implements TextSerializationHandler<SerializedType>
{

    /**
     * Creates a new {@code AbstractTextSerializationHandler}.
     */
    public AbstractTextSerializationHandler()
    {
        super();
    }

    public void writeObject(
        final OutputStream stream,
        final SerializedType object)
        throws IOException
    {
        final OutputStreamWriter writer = new OutputStreamWriter(stream);
        this.writeObject(writer, object);
        writer.flush();
        // We don't close the writer since it would close the underlying stream.
    }

    public Object readObject(
        final InputStream stream)
        throws IOException, ClassNotFoundException
    {
        final InputStreamReader reader = new InputStreamReader(stream);
        final Object result = this.readObject(reader);
        // We don't close the reader since it would close the underlying stream.
        
        return result;
    }

    public String convertToString(
        final SerializedType object)
        throws IOException
    {
        // Use a StringWriter to get the output.
        final StringWriter out = new StringWriter();
        this.writeObject(out, object);
        return out.toString();
    }

    public Object convertFromString(
        final String string)
        throws IOException, ClassNotFoundException
    {
        // Use a StringReader to read the input.
        final StringReader in = new StringReader(string);
        return this.readObject(in);
    }

}

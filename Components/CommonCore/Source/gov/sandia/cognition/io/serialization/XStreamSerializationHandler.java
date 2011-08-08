/*
 * File:                XStreamSerializationHandler.java
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

import com.thoughtworks.xstream.XStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;

/**
 * A serialization
 * 
 * @author  Justin Basilico
 * @since   3.0
 */
public class XStreamSerializationHandler
    extends AbstractTextSerializationHandler<Serializable>
{

    /** The default instance. Is only initialized when it is needed. */
    private static XStreamSerializationHandler DEFAULT;

    /**
     * Gets the default instance {@code XStreamSerializationHandler}.
     *
     * @return
     *      The default instance.
     */
    public static XStreamSerializationHandler getDefault()
    {
        // We use lazy initialization so that we don't pollute memory lots of
        // statics.
        synchronized (XStreamSerializationHandler.class)
        {
            if (DEFAULT == null)
            {
                DEFAULT = new XStreamSerializationHandler();
            }
        }

        return DEFAULT;
    }

    /** The configured XStream object to use. */
    protected transient XStream xstream;

    /**
     * Creates a new {@code XStreamSerializationHandler} using a default
     * {@code XStream} object underneath.
     */
    public XStreamSerializationHandler()
    {
        this(new XStream());
    }

    /**
     * Creates a new {@code XStreamSerializationHandler} with the given
     * {@code XStream} configuration.
     *
     * @param   xstream
     *      The XStream configuration to use. Cannot be null.
     */
    public XStreamSerializationHandler(
        final XStream xstream)
    {
        super();

        if (xstream == null)
        {
            throw new NullPointerException("xstream cannot be null.");
        }

        this.xstream = xstream;
    }

    public void writeObject(
        final Writer writer,
        final Serializable object)
        throws IOException
    {
        this.xstream.toXML(object, writer);
    }

    public Object readObject(
        final Reader reader)
        throws IOException
    {
        final Object object = this.xstream.fromXML(reader);
        
        // Close the reader since when XStream has read an object it will have
        // read until the end of a stream.
        reader.close();
        return object;
    }
}

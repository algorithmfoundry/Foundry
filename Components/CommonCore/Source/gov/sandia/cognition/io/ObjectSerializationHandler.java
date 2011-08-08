/*
 * File:                ObjectSerializationHandler.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 24, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.io;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.annotation.CodeReviews;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * The <code>ObjectSerializationHandler</code> class implements methods for
 * handling the serialization and deserialization of objects.
 * 
 * @author Justin Basilico
 * @since 1.0
 * @see   Serializable
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-02-08",
            changesNeeded=false,
            comments={
                "Added some calls to close() after Streams were done.",
                "Minor cosmetic changes.",
                "Otherwise, looks fine."
            }
        ),
        @CodeReview(
            reviewer="Jonathan McClain",
            date="2006-05-11",
            changesNeeded=true,
            comments="A few undocumented behaviors, and one bug.",
            response=@CodeReviewResponse(
                respondent="Justin Basilico",
                date="2006-05-16",
                comments="Bug fixed.",
                moreChangesNeeded=false
            )
        )
    }
)
public class ObjectSerializationHandler
{

    /**
     * Converts the given Object into an array of bytes. The object must be
     * Serializable for this to work.
     * 
     * @param object
     *            The Object to convert to bytes. If the given object is null
     *            then null is returned.
     * @return The byte array containing the serialized Object.
     * @throws IOException
     *             If there is an error in serialization. Typically from when
     *             the object or one of the objects it contains is not
     *             Serializable.
     */
    public static byte[] convertToBytes(
        final Serializable object )
        throws IOException
    {
        byte[] byteArray = null;

        if (object != null)
        {

            // We are going to write the object to a byte array.
            final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

            // Create an output stream of objects to write into the byte
            // stream.
            final ObjectOutputStream out = new ObjectOutputStream( byteStream );

            // Write the object.
            out.writeObject( object );

            // Make sure the stream gets cleaned up.
            out.flush();
            out.close();

            // Return the array of bytes that we have written.
            byteArray = byteStream.toByteArray();
        }

        return byteArray;
    }

    /**
     * Takes a byte array produced by convertToBytes and returns the Object from
     * the serialized byte array.
     * 
     * @param serialized
     *            The array of bytes containing the Object (and only the
     *            Object).
     * @return The Object deserialized from the given byte array. If the given
     *         array is null then null is returned.
     * @throws IOException
     *             If there was an error in deserialization.
     * @throws ClassNotFoundException
     *             If the class for the object could not be found.
     */
    public static Object convertFromBytes(
        final byte[] serialized )
        throws IOException, ClassNotFoundException
    {
        if (serialized == null)
        {
            // Error: No bytes were returned.
            return null;
        }

        // Read in an object using Java's serialization.

        // Create an input stream from the given bytes.
        final ByteArrayInputStream byteStream = new ByteArrayInputStream(
            serialized );

        // Create an input stream for reading objects.
        final ObjectInputStream in = new ObjectInputStream( byteStream );

        // Read the object.
        final Object read = in.readObject();

        if (byteStream.available() > 0)
        {
            // We didn't read all of the object.
            throw new IOException(
                "There was more than the object in the given byte array." );
        }
        
        in.close();
        byteStream.close();

        // We successfully deserialized the object.
        return read;
        
    }

    /**
     * Reads a Java serialized Object from the given File and returns it.
     * <p>
     * If a <code>null</code> file is passed as a parameter, a
     * <code>null</code> value is returned.
     * 
     * @param file
     *            The File to read the Object from.
     * @return The Object read from the given file.
     * @throws ClassNotFoundException
     *             If a class in the file cannot be found.
     * @throws IOException
     *             If there is any other type of error reading from the file.
     */
    public static Object readFromFile(
        final File file )
        throws ClassNotFoundException, IOException
    {
        Object read = null;

        if (file != null)
        {
            // Create an input stream
            final BufferedInputStream in = new BufferedInputStream(
                new FileInputStream( file ) );

            try
            {
                // Read the object.
                read = readFromStream( in );
            }
            finally
            {
                in.close();
            }
        }

        return read;
    }

    /**
     * Reads a Java serialized Object from the given stream and returns it.
     * <p>
     * If a <code>null</code> stream is passed as a parameter, a
     * <code>null</code> value is returned.
     * 
     * @param stream
     *            The BufferedInputStream to read the Object from.
     * @return The Object read from the given stream.
     * @throws ClassNotFoundException
     *             If a class in the stream cannot be found.
     * @throws IOException
     *             If there is any other type of error reading from the stream.
     */
    public static Object readFromStream(
        final BufferedInputStream stream )
        throws ClassNotFoundException, IOException
    {
        Object read = null;

        if (stream != null)
        {
            // Create an input stream for reading objects.
            final ObjectInputStream in = new ObjectInputStream( stream );

            // Read the object.
            read = in.readObject();
            
            in.close();
        }

        return read;
    }

    /**
     * Writes a Java serialized Object to the given file.
     * 
     * @param file
     *            File to write the object into
     * @param object
     *            Serializable object to write into file
     * @throws java.io.IOException
     *             if the object cannot be written into the file
     */
    public static void writeObjectToFile(
        final File file,
        final Serializable object )
        throws IOException
    {
        if (file == null)
        {
            throw new IOException( "File is null!" );
        }
        else if (object != null)
        {
            // Create the output stream.
            final ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream( file ) );
            try
            {

                // Write the object.
                out.writeObject( object );

                // Make sure the stream gets cleaned up.
                out.flush();
            }
            finally
            {
                out.close();
            }
        }
    }

}

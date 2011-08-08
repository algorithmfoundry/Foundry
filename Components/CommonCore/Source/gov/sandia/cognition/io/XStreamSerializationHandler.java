/*
 * File:                XMLSerializationHandler.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.io;

import com.thoughtworks.xstream.XStream;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviews;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.annotation.SoftwareLicenseType;
import gov.sandia.cognition.annotation.SoftwareReference;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Reads and writes Objects in XML format. Generally speaking, this requires
 * no extra methods for an Object (it's automagic using Reflection). Based on
 * XStream package.
 * <P><P>
 * <B>KNOWN LIMITATION<B>: The read() method will thrown an exception
 * "java.io.IOException: com.thoughtworks.xstream.io.StreamException:
 * : input contained no data" when trying to read the SECOND object from
 * an XML InputStream/Reader.  This is because XML files must have a
 * single root node (which would be the FIRST object in the file), and
 * anything after the close of the root node (the SECOND object) is
 * considered invalid or nonexistent.  To address the limitation, refer
 * to http://xstream.codehaus.org/objectstream.html
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReviews(
    reviews={
        @CodeReview(
            reviewer="Kevin R. Dixon",
            date="2008-12-02",
            changesNeeded=false,
            comments={
                "Minor cosmetic changes.",
                "Otherwise, this wrapper class is fine."
            }
        )
        ,
        @CodeReview(
            reviewer = "Kevin R. Dixon",
            date = "2008-02-08",
            changesNeeded = false,
            comments = {
                "Changed the read() method to close the stream it creates after reading.",
                "Minor formatting change, and fixed a few typos.",
                "Added PublicationReference for the Object Streams Tutorial.",
                "Otherwise, looks fine."
            }
        )
    }
)
@PublicationReference(
    author = "XStream Documentation",
    title = "Object Streams Tutorial",
    type = PublicationType.WebPage,
    year = 2008,
    url = "http://xstream.codehaus.org/objectstream.html"
)
@SoftwareReference(
    name="XStream",
    version="1.2.1",
    url="http://xstream.codehaus.org/",
    license=SoftwareLicenseType.BSD,
    licenseURL="http://xstream.codehaus.org/"
)
public class XStreamSerializationHandler
    extends Object
{

    /**
     * Writes the given object to the given OutputStream
     *
     * @param stream OutputStream to write the object to
     * @param object Object to write to the OutputStream
     * @return true on success, false otherwise
     * @throws java.io.IOException
     * If there's a problem writing
     */
    public static boolean write(
        OutputStream stream,
        Serializable object)
        throws IOException
    {
        return XStreamSerializationHandler.write(
            new OutputStreamWriter(stream), object);
    }

    /**
     * Writes the given object to the given Writer
     * 
     * @return true on success, false otherwise
     * @param writer Writer to write the object to
     * @param object Object to write to the Writer
     * @throws java.io.IOException
     * If there's a problem writing
     */
    public static boolean write(
        Writer writer,
        Serializable object)
        throws IOException
    {
        XStream xmlStream = new XStream();
        xmlStream.toXML(object, writer);
        return true;
    }
  
    /**
     * Writes the given object to the given file using XStream serialization.
     * 
     * @param   fileName
     *      The file to write to.
     * @param   object
     *      The object to write.
     * @throws  java.io.IOException
     *      If there is an error in writing.
     */
    public static void writeToFile(
        final String fileName,
        final Serializable object)
        throws IOException
    {
        writeToFile(new File(fileName), object);
    }
    
    /**
     * Writes the given object to the given file using XStream serialization.
     * 
     * @param   file
     *      The file to write to.
     * @param   object
     *      The object to write.
     * @throws  java.io.IOException
     *      If there is an error in writing.
     */
    public static void writeToFile(
        final File file,
        final Serializable object)
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
            write(out, object);
        }
        finally
        {
            out.close();
        }
    }

    /**
     * Reads an Object from the given file name.
     * <P>
     * KNOWN LIMITATION: This method will thrown an exception
     * "java.io.IOException: com.thoughtworks.xstream.io.StreamException:
     * : input contained no data" when trying to read the SECOND object from
     * an XML InputStream/Reader.  This is because XML files must have a
     * single root node (which would be the FIRST object in the file), and
     * anything after the close of the root node (the SECOND object) is
     * considered invalid or inexistent.  To address the limitation, refer
     * to http://xstream.codehaus.org/objectstream.html
     * 
     * @param fileName The name of the file to read from.
     * @return Deserialized object (null on exception)
     * @throws java.io.IOException 
     * if an Object can't be deserialized from the reader (See LIMITATION above)
     */
    public static Object readFromFile(
        String fileName)
        throws IOException
    {
        return readFromFile(new File(fileName));
    }

    /**
     * Reads an Object from the given file name.
     * <P>
     * KNOWN LIMITATION: This method will thrown an exception
     * "java.io.IOException: com.thoughtworks.xstream.io.StreamException:
     * : input contained no data" when trying to read the SECOND object from
     * an XML InputStream/Reader.  This is because XML files must have a
     * single root node (which would be the FIRST object in the file), and
     * anything after the close of the root node (the SECOND object) is
     * considered invalid or inexistent.  To address the limitation, refer
     * to http://xstream.codehaus.org/objectstream.html
     * 
     * @param file The file to read from.
     * @return Deserialized object (null on exception)
     * @throws java.io.IOException 
     * if an Object can't be deserialized from the reader (See LIMITATION above)
     */
    public static Object readFromFile(
        File file)
        throws IOException
    {
        BufferedInputStream in = new BufferedInputStream(
            new FileInputStream(file));

        // Read the object.
        Object read = null;
        try
        {
            read = read(in);
        }
        finally
        {
            in.close();
        }

        return read;
    }

    /**
     * Reads an Object from the given InputStream.
     * <P>
     * KNOWN LIMITATION: This method will thrown an exception
     * "java.io.IOException: com.thoughtworks.xstream.io.StreamException:
     * : input contained no data" when trying to read the SECOND object from
     * an XML InputStream/Reader.  This is because XML files must have a
     * single root node (which would be the FIRST object in the file), and
     * anything after the close of the root node (the SECOND object) is
     * considered invalid or inexistent.  To address the limitation, refer
     * to http://xstream.codehaus.org/objectstream.html
     * 
     * @return Deserialized object (null on exception)
     * @param stream InputStream from which to read
     * @throws java.io.IOException 
     * if an Object can't be deserialized from the reader (See LIMITATION above)
     */
    public static Object read(
        InputStream stream)
        throws IOException
    {
        InputStreamReader in = new InputStreamReader(stream);
        Object retval = XStreamSerializationHandler.read(in);
        in.close();
        return retval;
    }

    /**
     * Reads an Object from the given Reader.
     * KNOWN LIMITATION: This method will thrown an exception
     * "java.io.IOException: com.thoughtworks.xstream.io.StreamException:
     * : input contained no data" when trying to read the SECOND object from
     * an XML InputStream/Reader.  This is because XML files must have a
     * single root node (which would be the FIRST object in the file), and
     * anything after the close of the root node (the SECOND object) is
     * considered invalid or inexistent.  To address the limitation, refer
     * to http://xstream.codehaus.org/objectstream.html
     * 
     * @return Deserialized object (null on exception)
     * @param reader Reader from which to read
     * @throws java.io.IOException
     * if an Object can't be deserialized from the reader (See LIMITATION above)
     */
    public static Object read(
        Reader reader)
        throws IOException
    {

        XStream xmlStream = new XStream();
        Object object = null;
        try
        {
            object = xmlStream.fromXML(reader);
        }
        catch (Exception e)
        {
            throw new IOException(e.toString());
        }

        return object;
    }

    /**
     * Writes the given object to a String.
     *
     * @param   object Object to write to the String.
     * @return  The string containing the XML version of the object.
     * @throws  java.io.IOException If there's a problem writing.
     */
    public static String convertToString(
        final Serializable object)
        throws IOException
    {
        StringWriter out = new StringWriter();
        write(out, object);
        return out.toString();
    }

    /**
     * Attempts to read an Object from the given string.
     * 
     * @param   string The String containing the XStream version of the object.
     * @return  The Object read from the given string.
     * @throws  java.io.IOException If there is a problem reading.
     */
    public static Object convertFromString(
        final String string)
        throws IOException
    {
        StringReader in = new StringReader(string);
        return read(in);
    }

}

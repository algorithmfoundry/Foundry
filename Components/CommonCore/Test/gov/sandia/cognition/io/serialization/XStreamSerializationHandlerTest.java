/*
 * File:                XStreamSerializationHandlerTest.java
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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.Random;

/**
 * Unit tests for class XStreamSerializationHandler.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class XStreamSerializationHandlerTest
    extends TextSerializationHandlerTestHarness<Serializable>
{

    /** Random number generator. */
    protected Random random = new Random();

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public XStreamSerializationHandlerTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public XStreamSerializationHandler createInstance()
    {
        return new XStreamSerializationHandler();
    }

    @Override
    public List<Serializable> createTestObjectList()
    {
        return createSerializableTestObjectList(this.random);
    }

    /**
     * Test of constructors of class XStreamSerializationHandler.
     */
    public void testConstructors()
    {
        XStreamSerializationHandler instance =
            new XStreamSerializationHandler();
        assertNotNull(instance);

        XStream xstream = new XStream(new StaxDriver());
        instance = new XStreamSerializationHandler(xstream);
        assertNotNull(instance);
    }

    /**
     * Test of getDefault method, of class XStreamSerializationHandler.
     */
    public void testGetDefault()
    {
        XStreamSerializationHandler result =
            XStreamSerializationHandler.getDefault();
        assertNotNull(result);
        assertSame(result, XStreamSerializationHandler.getDefault());
    }

    /**
     * Tests that the serializer only allows one object written to a file.
     *
     * @throws  Exception
     */
    public void testWriteMultiple()
        throws Exception
    {
        XStreamSerializationHandler instance = this.createInstance();

        StringWriter writer = new StringWriter();
        instance.writeObject(writer, "first");
        instance.writeObject(writer, "second");
        writer.close();

        StringReader reader = new StringReader(writer.toString());
        int numRead = 0;

        // We should be able to read the first object fine.
        Object o = instance.readObject(reader);
        numRead++;
        assertEquals("first", o);

        // But we should barf on the second object in the file
        // For details about why, see http://xstream.codehaus.org/objectstream.html
        boolean exceptionThrown = false;
        try
        {
            o = instance.readObject(reader);
        }
        catch (Exception e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(
                "XStream XML files allow only ONE object written to them.",
                exceptionThrown);
        }
    }

}

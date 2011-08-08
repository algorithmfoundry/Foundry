/*
 * File:                GZIPSerializationHandlerTest.java
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

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Unit tests for class GZIPSerializationHandler.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class GZIPSerializationHandlerTest
    extends StreamSerializationHandlerTestHarness<Serializable>
{

    /** Random number generator. */
    protected Random random = new Random();

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public GZIPSerializationHandlerTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public StreamSerializationHandler<Serializable> createInstance()
    {
        return new GZIPSerializationHandler<Serializable>(
            JavaDefaultBinarySerializationHandler.getInstance());
    }

    @Override
    public List<Serializable> createTestObjectList()
    {
        return createSerializableTestObjectList(this.random);
    }

    /**
     * Test of constructors of class GZIPSerializationHandler.
     */
    public void testConstructors()
    {
        StreamSerializationHandler<Serializable> baseHandler = null;
        GZIPSerializationHandler<Serializable> instance =
            new GZIPSerializationHandler<Serializable>();
        assertSame(baseHandler, instance.getBaseHandler());

        baseHandler = new JavaDefaultBinarySerializationHandler();
        instance =
            new GZIPSerializationHandler<Serializable>(baseHandler);
        assertSame(baseHandler, instance.getBaseHandler());
    }

    /**
     * Test of getBaseHandler method, of class GZIPSerializationHandler.
     */
    public void testGetBaseHandler()
    {
        this.testSetBaseHandler();
    }

    /**
     * Test of setBaseHandler method, of class GZIPSerializationHandler.
     */
    public void testSetBaseHandler()
    {
        StreamSerializationHandler<Serializable> baseHandler = null;
        GZIPSerializationHandler<Serializable> instance =
            new GZIPSerializationHandler<Serializable>();
        assertSame(baseHandler, instance.getBaseHandler());

        baseHandler = new JavaDefaultBinarySerializationHandler();
        instance.setBaseHandler(baseHandler);
        assertSame(baseHandler, instance.getBaseHandler());

        baseHandler = null;
        instance.setBaseHandler(baseHandler);
        assertSame(baseHandler, instance.getBaseHandler());

        baseHandler = new XStreamSerializationHandler();
        instance.setBaseHandler(baseHandler);
        assertSame(baseHandler, instance.getBaseHandler());
    }

}

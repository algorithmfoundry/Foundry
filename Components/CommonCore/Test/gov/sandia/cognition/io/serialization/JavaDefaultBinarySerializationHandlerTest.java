/*
 * File:                JavaDefaultBinarySerializationHandlerTest.java
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

import java.io.Serializable;
import java.util.List;
import java.util.Random;

/**
 * Unit tests for class JavaDefaultBinarySerializationHandler.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class JavaDefaultBinarySerializationHandlerTest
    extends StreamSerializationHandlerTestHarness<Serializable>
{

    /** Random number generator. */
    protected Random random = new Random();

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public JavaDefaultBinarySerializationHandlerTest(
        String testName)
    {
        super(testName);
    }

    @Override
    public JavaDefaultBinarySerializationHandler createInstance()
    {
        return new JavaDefaultBinarySerializationHandler();
    }

    @Override
    public List<Serializable> createTestObjectList()
    {
        return createSerializableTestObjectList(this.random);
    }

    /**
     * Test of constructors of class JavaDefaultBinarySerializationHandler.
     */
    public void testConstructors()
    {
        JavaDefaultBinarySerializationHandler instance =
            new JavaDefaultBinarySerializationHandler();
        assertNotNull(instance);
    }

    /**
     * Test of getInstance method, of class JavaDefaultBinarySerializationHandler.
     */
    public void testGetInstance()
    {
        JavaDefaultBinarySerializationHandler result =
            JavaDefaultBinarySerializationHandler.getInstance();
        assertNotNull(result);
        assertSame(result, JavaDefaultBinarySerializationHandler.getInstance());
    }

}

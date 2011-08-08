/*
 * File:                SerializedModelHandlerTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 18, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */
package gov.sandia.cognition.framework.io;

import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModelFactory;
import gov.sandia.cognition.io.CSVParseException;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import junit.framework.TestCase;

/**
 * Unit tests for SerializedModelHandlerTest
 * 
 * @author Kevin R. Dixon
 * @since 1.0
 */
public class SerializedModelHandlerTest extends TestCase
{
    private String baseDir;
    public SerializedModelHandlerTest()
    {
        final URL baseURL = this.getClass().getClassLoader().getResource("gov/sandia/cognition/framework/io/testModel.csv");
        this.baseDir = new File(baseURL.getFile()).getParent() + "/";
    }

    @Override
    protected void setUp() throws Exception
    {
    }

    @Override
    protected void tearDown() throws Exception
    {
    }

    public CognitiveModel createTestModel() throws CSVParseException,
            IOException
    {
        final String fileName = baseDir + "testModel.csv";

        final CognitiveModelFactory factory = CSVDefaultCognitiveModelLiteHandler
                .parseCSVToModelFactory(fileName, false);

        return factory.createModel();
    }

    /**
     * Test of readObjectFromFile method, of class
     * gov.sandia.isrc.cognition.io.SerializedModelHandler.
     */
    public void testReadObjectFromFile() throws Exception
    {
        final String fileName = baseDir + "testModel.jcm";

        final CognitiveModel outputModel = createTestModel();

        SerializedModelHandler.writeModelToFile(fileName, outputModel);

        final Object inputObject = SerializedModelHandler
                .readObjectFromFile(new File(fileName));

        assertNotNull(inputObject);
        assertTrue(inputObject instanceof CognitiveModel);
        assertNotSame(inputObject, outputModel);
    }

    /**
     * Test of readModelFromFile method, of class
     * gov.sandia.isrc.cognition.io.SerializedModelHandler.
     */
    public void testReadModelFromFile() throws Exception
    {
        final String fileName = baseDir + "testModel.jcm";

        final CognitiveModel outputModel = createTestModel();

        SerializedModelHandler.writeModelToFile(fileName, outputModel);

        final CognitiveModel inputModel = SerializedModelHandler
                .readModelFromFile(fileName);

        assertNotNull(inputModel);
        assertNotSame(inputModel, outputModel);
    }

    /**
     * Test of writeModelToFile method, of class
     * gov.sandia.isrc.cognition.io.SerializedModelHandler.
     */
    public void testWriteModelToFile() throws Exception
    {
        System.out.println("writeModelToFile");

        final String fileName = baseDir + "testModel.jcm";

        final CognitiveModel model = createTestModel();

        SerializedModelHandler.writeModelToFile(fileName, model);
    }

}

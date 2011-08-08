/*
 * File:                CSVDefaultCognitiveModelLiteHandlerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright April 12, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.io;

import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModuleFactory;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.DefaultSemanticNetwork;
import gov.sandia.cognition.framework.SemanticNetwork;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteFactory;
import gov.sandia.cognition.framework.lite.SharedSemanticMemoryLiteFactory;
import gov.sandia.cognition.io.CSVParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import java.net.URL;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 * 
 * CSVDefaultCognitiveModelLiteHandler
 * 
 * @author Justin Basilico
 * @since 1.0
 */
public class CSVDefaultCognitiveModelLiteHandlerTest extends TestCase
{

    private String baseDir;
    
    public CSVDefaultCognitiveModelLiteHandlerTest()
    {
        final URL baseURL = this.getClass().getClassLoader().getResource("gov/sandia/cognition/framework/io/testModel.csv");
        this.baseDir = new File(baseURL.getFile()).getParent() + "/";
    }

    public void parseCSVToModuleFactoryFromFileTest(String fileName)
            throws Exception
    {
        final CognitiveModuleFactory factory = CSVDefaultCognitiveModelLiteHandler
                .parseCSVToModuleFactory(fileName, false);

        assertNotNull(factory);

        final SharedSemanticMemoryLiteFactory memory = (SharedSemanticMemoryLiteFactory) factory;

        final SemanticNetwork network = memory.getSettings().getRecognizer()
                .getNetwork();

        assertNotNull(network);

        final DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        final DefaultSemanticLabel b = new DefaultSemanticLabel("b");

        assertEquals(2, network.getNumNodes());
        assertTrue(network.isNode(a));
        assertTrue(network.isNode(b));

        assertEquals(0.0, network.getAssociation(a, a));
        assertEquals(1.0, network.getAssociation(a, b));
        assertEquals(0.0, network.getAssociation(b, a));
        assertEquals(0.5, network.getAssociation(b, b));

    }

    public void testParseCSVToModuleFactory() throws Exception
    {
        parseCSVToModuleFactoryFromFileTest(baseDir + "testModel.csv");
        parseCSVToModuleFactoryFromFileTest(baseDir + "testModelExcel.csv");

        boolean exceptionThrown = false;
        try
        {
            CSVDefaultCognitiveModelLiteHandler.parseCSVToModuleFactory(
                    "bad file name", true);
        } catch (final IOException e)
        {
            exceptionThrown = true;
        } finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of parseCSVToModuleFactory method, of class
     * gov.sandia.isrc.cognition.io.CSVDefaultCognitiveModelLiteHandler.
     */
    public void testParseCSVToModuleFactoryFromBufferedReader()
            throws Exception
    {
        final String testData = "CSVDefaultCognitiveModelLite\n"
                + "Version,1.0\n" + "\n" + "node,a\n" + "node,b\n" + "node,c\n"
                + "link,a,b,0.5\n" + "link,c,c,1.0\n" + "link,c,a,-1.0\n"
                + "\n";

        StringReader stringReader = new StringReader(testData);
        BufferedReader br = new BufferedReader(stringReader);

        CognitiveModuleFactory factory = CSVDefaultCognitiveModelLiteHandler
                .parseCSVToModuleFactory(br, false);
        assertNotNull(factory);

        final SharedSemanticMemoryLiteFactory memory = (SharedSemanticMemoryLiteFactory) factory;

        final SemanticNetwork network = memory.getSettings().getRecognizer()
                .getNetwork();

        assertNotNull(network);

        final DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        final DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        final DefaultSemanticLabel c = new DefaultSemanticLabel("c");

        assertEquals(3, network.getNumNodes());
        assertTrue(network.isNode(a));
        assertTrue(network.isNode(b));
        assertTrue(network.isNode(c));

        assertEquals(0.0, network.getAssociation(a, a));
        assertEquals(0.5, network.getAssociation(a, b));
        assertEquals(0.0, network.getAssociation(a, c));
        assertEquals(0.0, network.getAssociation(b, a));
        assertEquals(0.0, network.getAssociation(b, b));
        assertEquals(0.0, network.getAssociation(b, c));
        assertEquals(-1.0, network.getAssociation(c, a));
        assertEquals(0.0, network.getAssociation(c, b));
        assertEquals(1.0, network.getAssociation(c, c));

        boolean exceptionThrown = false;
        try
        {
            stringReader = new StringReader("bad");
            br = new BufferedReader(stringReader);
            factory = CSVDefaultCognitiveModelLiteHandler
                    .parseCSVToModuleFactory(br, true);
        } catch (final CSVParseException e)
        {
            exceptionThrown = true;
        } finally
        {
            assertTrue(exceptionThrown);
        }

    }

    /**
     * Test of write method, of class
     * gov.sandia.isrc.cognition.io.CSVDefaultCognitiveModelLiteHandler.
     */
    public void testWrite() throws Exception
    {
        final DefaultSemanticNetwork outNet = new DefaultSemanticNetwork();

        final DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        final DefaultSemanticLabel b = new DefaultSemanticLabel("b");

        outNet.addNode(a);
        outNet.addNode(b);
        outNet.setAssociation(a, b, -1.0);
        outNet.setAssociation(b, b, 0.5);

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter pw = new PrintWriter(stringWriter);

        CSVDefaultCognitiveModelLiteHandler.write(pw, outNet);
        pw.flush();
        pw.close();

        final StringReader stringReader = new StringReader(stringWriter
                .toString());
        final BufferedReader br = new BufferedReader(stringReader);

        final CognitiveModuleFactory factory = CSVDefaultCognitiveModelLiteHandler
                .parseCSVToModuleFactory(br, false);

        assertNotNull(factory);

        final SharedSemanticMemoryLiteFactory memory = (SharedSemanticMemoryLiteFactory) factory;

        final SemanticNetwork network = memory.getSettings().getRecognizer()
                .getNetwork();

        assertNotNull(network);

        assertEquals(2, network.getNumNodes());
        assertTrue(network.isNode(a));
        assertTrue(network.isNode(b));

        assertEquals(0.0, network.getAssociation(a, a));
        assertEquals(-1.0, network.getAssociation(a, b));
        assertEquals(0.0, network.getAssociation(b, a));
        assertEquals(0.5, network.getAssociation(b, b));
    }

    /**
     * Test of parseCSVToModelFactory method, of class
     * gov.sandia.isrc.cognition.io.CSVDefaultCognitiveModelLiteHandler.
     */
    public void testParseCSVToModelFactory() throws Exception
    {
        final CognitiveModelLiteFactory result = CSVDefaultCognitiveModelLiteHandler
                .parseCSVToModelFactory(baseDir + "testModel.csv", false);

        assertNotNull(result);

        final CognitiveModel model = result.createModel();

        assertNotNull(model);

        assertEquals(2, model.getModules().size());

    }
}

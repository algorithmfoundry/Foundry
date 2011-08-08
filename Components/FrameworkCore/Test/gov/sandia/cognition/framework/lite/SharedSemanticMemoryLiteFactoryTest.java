/*
 * File:                SharedSemanticMemoryLiteFactoryTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 28, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.DefaultSemanticNetwork;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *      SharedSemanticMemoryLiteFactory
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class SharedSemanticMemoryLiteFactoryTest
    extends TestCase
{
    public SharedSemanticMemoryLiteFactoryTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(SharedSemanticMemoryLiteFactoryTest.class);
        
        return suite;
    }

    /**
     * Test of createModule method, of class gov.sandia.isrc.cognition.framework.lite.SharedSemanticMemoryLiteFactory.
     */
    public void testCreateModule()
    {
        DefaultSemanticNetwork network = new DefaultSemanticNetwork();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        network.addNode(a);
        network.addNode(b);
        network.setAssociation(a, b, 1.0);
        
        SimplePatternRecognizer recognizer = 
            new SimplePatternRecognizer(network);
        
        SharedSemanticMemoryLiteFactory factory =
            new SharedSemanticMemoryLiteFactory(recognizer);
        
        CognitiveModelLite model = new CognitiveModelLite(factory);

        assertNotNull(model.getModules());
        assertEquals(1, model.getModules().size());
        CognitiveModule module = model.getModules().get(0);
        
        assertNotNull(module);
        assertTrue(module instanceof SharedSemanticMemoryLite);
    }

    /**
     * Test of getSettings method, of class gov.sandia.isrc.cognition.framework.lite.SharedSemanticMemoryLiteFactory.
     */
    public void testGetSettings()
    {
        DefaultSemanticNetwork network = new DefaultSemanticNetwork();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        network.addNode(a);
        network.addNode(b);
        network.setAssociation(a, b, 1.0);
        
        SimplePatternRecognizer recognizer = 
            new SimplePatternRecognizer(network);
        
        SharedSemanticMemoryLiteFactory factory =
            new SharedSemanticMemoryLiteFactory(recognizer);
        
        SharedSemanticMemoryLiteSettings settings = factory.getSettings();
        assertNotNull(settings);
        assertNotSame(recognizer, settings.getRecognizer());
    }
}

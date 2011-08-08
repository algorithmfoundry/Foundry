/*
 * File:                SharedSemanticMemoryLiteTest.java
 * Authors:             Justin Basilico 
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 14, 2006, Sandia Corporation.  Under the terms of Contract
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
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;

/**
 * This class implements JUnit tests for the following classes:
 *
 *    SharedSemanticMemoryLite
 *
 * @author Justin Basilico
 * @since  1.0
 */
public class SharedSemanticMemoryLiteTest 
    extends TestCase
{
    /**
     * Creates a new instance of SharedSemanticMemoryLiteTest.
     *
     * @param  testName The test name.
     */
    public SharedSemanticMemoryLiteTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(SharedSemanticMemoryLiteTest.class);
        
        return suite;
    }
    
    public SharedSemanticMemoryLite createInstance()
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
        
        return (SharedSemanticMemoryLite) module;
    }

    /**
     * Test of findInputIndexForIdentifier method, of class gov.sandia.isrc.cognition.framework.lite.SharedSemanticMemoryLite.
     */
    public void testFindInputIndexForIdentifier()
    {
        SharedSemanticMemoryLite instance = this.createInstance();
        SemanticIdentifierMap map = instance.getSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        SemanticIdentifier c = map.addLabel(new DefaultSemanticLabel("c"));
            
        assertEquals(0, instance.findInputIndexForIdentifier(a));
        assertEquals(1, instance.findInputIndexForIdentifier(b));
        assertEquals(-1, instance.findInputIndexForIdentifier(c));
        assertEquals(-1, instance.findInputIndexForIdentifier(null));
    }

    /**
     * Test of getSettings method, of class gov.sandia.isrc.cognition.framework.lite.SharedSemanticMemoryLite.
     */
    public void testGetSettings()
    {
        SharedSemanticMemoryLite instance = this.createInstance();
        SharedSemanticMemoryLiteSettings result = instance.getSettings();
        assertNotNull(result);
    }

    /**
     * Test of getName method, of class gov.sandia.isrc.cognition.framework.lite.SharedSemanticMemoryLite.
     */
    public void testGetName()
    {
        SharedSemanticMemoryLite instance = this.createInstance();
        assertEquals("Shared Semantic Memory Lite", instance.getName());
    }
}

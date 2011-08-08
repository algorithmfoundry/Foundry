/*
 * File:                MutableSemanticMemoryLiteTest.java
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
import gov.sandia.cognition.framework.SemanticLabel;

/**
 * This class implements JUnit tests for the following classes:
 *
 *    MutableSemanticMemoryLite
 *
 * @author Justin Basilico
 * @since  1.0
 */
public class MutableSemanticMemoryLiteTest 
    extends TestCase
{
    /**
     * Creates a new instance of MutableSemanticMemoryLiteTest.
     *
     * @param  testName The test name.
     */
    public MutableSemanticMemoryLiteTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(MutableSemanticMemoryLiteTest.class);
        
        return suite;
    }
    
    public MutableSemanticMemoryLite createInstance()
    {
        DefaultSemanticNetwork network = new DefaultSemanticNetwork();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        network.addNode(a);
        network.addNode(b);
        network.setAssociation(a, b, 1.0);
        
        SimplePatternRecognizer recognizer = 
            new SimplePatternRecognizer(network);
        
        MutableSemanticMemoryLiteFactory factory =
            new MutableSemanticMemoryLiteFactory(recognizer);
        
        CognitiveModelLite model = new CognitiveModelLite(factory);

        assertNotNull(model.getModules());
        assertEquals(1, model.getModules().size());
        CognitiveModule module = model.getModules().get(0);
        
        assertNotNull(module);
        assertTrue(module instanceof MutableSemanticMemoryLite);
        
        return (MutableSemanticMemoryLite) module;
    }
    
    /**
     * Test of getSettings method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLite.
     */
    public void testGetSettings()
    {
        MutableSemanticMemoryLite instance = this.createInstance();
        assertNotNull(instance.getSettings());
    }

    /**
     * Test of findInputIndexForIdentifier method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLite.
     */
    public void testFindInputIndexForIdentifier()
    {
        MutableSemanticMemoryLite instance = this.createInstance();
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
     * Test of isNode method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLite.
     */
    public void testIsNode()
    {
        MutableSemanticMemoryLite instance = this.createInstance();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        assertTrue(instance.isNode(a));
        assertTrue(instance.isNode(b));
        assertFalse(instance.isNode(c));
    }

    /**
     * Test of addNode method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLite.
     */
    public void testAddNode()
    {
        MutableSemanticMemoryLite instance = this.createInstance();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        assertFalse(instance.isNode(c));
        instance.addNode(c);
        assertTrue(instance.isNode(c));
    }

    /**
     * Test of removeNode method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLite.
     */
    public void testRemoveNode()
    {
        MutableSemanticMemoryLite instance = this.createInstance();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        assertTrue(instance.isNode(b));
        instance.removeNode(b);
        assertFalse(instance.isNode(b));
    }

    /**
     * Test of setAssociation method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLite.
     */
    public void testSetAssociation()
    {
        MutableSemanticMemoryLite instance = this.createInstance();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        assertEquals(0.0, instance.getNetwork().getAssociation(b, a));
        instance.setAssociation(b, a, 2.0);
        assertEquals(2.0, instance.getNetwork().getAssociation(b, a));
    }

    /**
     * Test of getName method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLite.
     */
    public void testGetName()
    {
        MutableSemanticMemoryLite instance = this.createInstance();
        assertEquals("Mutable Semantic Memory Lite", instance.getName());
    }

    /**
     * Test of buildInputMapping method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLite.
     */
    public void testBuildInputMapping()
    {
        MutableSemanticMemoryLite instance = this.createInstance();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        instance.buildInputMapping();
        
        instance.removeNode(b);
        
        instance.buildInputMapping();
    }

    /**
     * Test of trySetInputLabel method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLite.
     */
    public void testTrySetInputLabel()
    {
        MutableSemanticMemoryLite instance = this.createInstance();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        assertTrue(instance.trySetInputLabel(a, true));
        assertFalse(instance.trySetInputLabel(a, false));
        assertFalse(instance.trySetInputLabel(c, true));
    }

    /**
     * Test of trySetOutputLabel method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLite.
     */
    public void testTrySetOutputLabel()
    {
        MutableSemanticMemoryLite instance = this.createInstance();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        assertTrue(instance.trySetOutputLabel(a, true));
        assertFalse(instance.trySetOutputLabel(a, false));
        assertFalse(instance.trySetOutputLabel(c, true));
    }

    /**
     * Test of assertArgumentIsNode method, of class gov.sandia.isrc.cognition.framework.lite.MutableSemanticMemoryLite.
     */
    public void testAssertArgumentIsNode()
    {
        MutableSemanticMemoryLite instance = this.createInstance();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.assertArgumentIsNode(a);
        instance.assertArgumentIsNode(b);
        
        boolean exceptionThrown = false;
        try
        {
            instance.assertArgumentIsNode(c);
        }
        catch ( IllegalArgumentException iae )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }
}

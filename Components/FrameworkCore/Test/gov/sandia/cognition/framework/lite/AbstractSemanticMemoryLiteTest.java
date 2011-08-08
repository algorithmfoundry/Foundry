/*
 * File:                AbstractSemanticMemoryLiteTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import junit.framework.*;
import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.DefaultCogxel;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.DefaultSemanticNetwork;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import java.util.ArrayList;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     <!-- TO DO: Description: -->
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class AbstractSemanticMemoryLiteTest
    extends TestCase
{
    public AbstractSemanticMemoryLiteTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(AbstractSemanticMemoryLiteTest.class);
        
        return suite;
    }
    
    public AbstractSemanticMemoryLite createInstance()
    {
        CognitiveModelLite model = this.createTestModel();

        assertNotNull(model.getModules());
        assertEquals(1, model.getModules().size());
        CognitiveModule module = model.getModules().get(0);
        
        assertNotNull(module);
        assertTrue(module instanceof SharedSemanticMemoryLite);
        
        return (AbstractSemanticMemoryLite) module;
    }
    
    public CognitiveModelLite createTestModel()
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
        
        return new CognitiveModelLite(factory);
    }

    /**
     * Test of buildOutputMapping method, of class gov.sandia.isrc.cognition.framework.lite.AbstractSemanticMemoryLite.
     */
    public void testBuildOutputMapping()
    {
        AbstractSemanticMemoryLite instance = this.createInstance();
        
        instance.buildOutputMapping();
     
        assertTrue( instance.getNetwork().getNumNodes() > 0 );
        
        for( SemanticLabel label : instance.getNetwork().getNodes() )
        {
            assertNotNull(
                instance.getSemanticIdentifierMap().findIdentifier( label ) );
        }
        
    }        
        
    /**
     * Test of initializeState method, of class gov.sandia.isrc.cognition.framework.lite.AbstractSemanticMemoryLite.
     */
    public void testInitializeState()
    {
        AbstractSemanticMemoryLite instance = this.createInstance();
        
        // The pattern recognizer we are using has no state.
        assertNotNull(instance.initializeState(null));
    }

    /**
     * Test of update method, of class gov.sandia.isrc.cognition.framework.lite.AbstractSemanticMemoryLite.
     */
    public void testUpdate()
    {
        CognitiveModelLite model = this.createTestModel();
        
        SemanticIdentifierMap map = model.getSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        
        double value = Math.random();
        
        
        DefaultCogxel cogxel = new DefaultCogxel(a, value);
        model.getCurrentState().getCogxels().addCogxel(cogxel);
        
        model.update(null);
        model.update(null);
        
        double activation = 
            model.getCurrentState().getCogxels().getCogxelActivation(b);
        
        assertEquals(value, activation);

    }

    /**
     * Test of findInputIndexForIdentifier method, of class gov.sandia.isrc.cognition.framework.lite.AbstractSemanticMemoryLite.
     */
    public void testFindInputIndexForIdentifier()
    {
        AbstractSemanticMemoryLite instance = this.createInstance();
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
     * Test of getNetwork method, of class gov.sandia.isrc.cognition.framework.lite.AbstractSemanticMemoryLite.
     */
    public void testGetNetwork()
    {
        AbstractSemanticMemoryLite instance = this.createInstance();
        assertNotNull(instance.getNetwork());
    }

    /**
     * Test of getSemanticIdentifierMap method, of class gov.sandia.isrc.cognition.framework.lite.AbstractSemanticMemoryLite.
     */
    public void testGetSemanticIdentifierMap()
    {
        AbstractSemanticMemoryLite instance = this.createInstance();
        assertNotNull(instance.getSemanticIdentifierMap());
    }

    /**
     * Test of getRecognizer method, of class gov.sandia.isrc.cognition.framework.lite.AbstractSemanticMemoryLite.
     */
    public void testGetRecognizer()
    {
        AbstractSemanticMemoryLite instance = this.createInstance();
        assertNotNull(instance.getRecognizer());
    }

    /**
     * Test of getOutputIdentifiers method, of class gov.sandia.isrc.cognition.framework.lite.AbstractSemanticMemoryLite.
     */
    public void testGetOutputIdentifiers()
    {
        AbstractSemanticMemoryLite instance = this.createInstance();
        SemanticIdentifierMap map = instance.getSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        SemanticIdentifier c = map.addLabel(new DefaultSemanticLabel("c"));
        
        ArrayList<SemanticIdentifier> result = instance.getOutputIdentifiers();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains(a));
        assertTrue(result.contains(b));
        assertFalse(result.contains(c));
    }
}

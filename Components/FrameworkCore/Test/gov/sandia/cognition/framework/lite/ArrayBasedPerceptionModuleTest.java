/*
 * File:                ArrayBasedPerceptionModuleTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 29, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModelState;
import gov.sandia.cognition.framework.Cogxel;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultCogxelFactory;
import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *      ArrayBasedPerceptionModule
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class ArrayBasedPerceptionModuleTest
    extends TestCase
{
    public ArrayBasedPerceptionModuleTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(ArrayBasedPerceptionModuleTest.class);
        
        return suite;
    }

    /**
     * Test of initializeState method, of class gov.sandia.isrc.cognition.framework.lite.ArrayBasedPerceptionModule.
     */
    public void testInitializeState()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        DefaultCogxelFactory factory = DefaultCogxelFactory.INSTANCE;
        
        ArrayBasedPerceptionModule instance = 
            new ArrayBasedPerceptionModule(map, factory);
        
        // The module has no settings.
        assertNull(instance.initializeState(null));
    }

    /**
     * Test of update method, of class gov.sandia.isrc.cognition.framework.lite.ArrayBasedPerceptionModule.
     */
    public void testUpdate()
    {
        CognitiveModelLite model = 
            new CognitiveModelLite(new ArrayBasedPerceptionModuleFactory());
        
        SemanticIdentifierMap map = model.getSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        
        SemanticIdentifier[] ids = new SemanticIdentifier[] { a, b };
        double[] values = new double[] { 1.0, 2.0 };
        
        ArrayBasedCognitiveModelInput input = 
            new ArrayBasedCognitiveModelInput(ids, values, false);
        
        model.update(input);
        
        CognitiveModelState state = model.getCurrentState();
        CogxelState cogxels = state.getCogxels();
        
        Cogxel cogxelA = cogxels.getCogxel(a);
        Cogxel cogxelB = cogxels.getCogxel(b);
        
        assertNotNull(cogxelA);
        assertNotNull(cogxelB);
        assertEquals(1.0, cogxelA.getActivation());
        assertEquals(2.0, cogxelB.getActivation());
    }

    /**
     * Test of getName method, of class gov.sandia.isrc.cognition.framework.lite.ArrayBasedPerceptionModule.
     */
    public void testGetName()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        DefaultCogxelFactory factory = DefaultCogxelFactory.INSTANCE;
        
        ArrayBasedPerceptionModule instance = 
            new ArrayBasedPerceptionModule(map, factory);
        
        assertEquals("Array-based Perception Module", instance.getName());
    }

    /**
     * Test of getSettings method, of class gov.sandia.isrc.cognition.framework.lite.ArrayBasedPerceptionModule.
     */
    public void testGetSettings()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        DefaultCogxelFactory factory = DefaultCogxelFactory.INSTANCE;
        
        ArrayBasedPerceptionModule instance = 
            new ArrayBasedPerceptionModule(map, factory);
        
        // The module has no settings.
        assertNull(instance.getSettings());
    }
}

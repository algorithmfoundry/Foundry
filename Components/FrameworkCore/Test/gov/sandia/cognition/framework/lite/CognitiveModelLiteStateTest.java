/*
 * File:                CognitiveModelLiteStateTest.java
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

import gov.sandia.cognition.framework.DefaultCogxelFactory;
import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.DummyModelInput;
import junit.framework.*;
import gov.sandia.cognition.framework.CognitiveModelInput;
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.DummyModuleState;
import gov.sandia.cognition.framework.SemanticIdentifier;
import java.util.Collection;
import gov.sandia.cognition.framework.CognitiveModelState;
import java.util.Arrays;

/**
 * This class implements JUnit tests for the following classes:
 *
 *      CognitiveModelLiteState
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class CognitiveModelLiteStateTest
    extends TestCase
{
    public CognitiveModelLiteStateTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(CognitiveModelLiteStateTest.class);
        
        return suite;
    }

    /**
     * Test of clone method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testClone()
    {
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        instance.getModuleStatesArray()[0] = new DummyModuleState();
        
        CognitiveModelLiteState clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(instance, clone);
        assertEquals(instance.isInitialized(), clone.isInitialized());
        assertSame(instance.getInput(), clone.getInput());
        assertNotSame(instance.getCogxels(), clone.getCogxels());
        assertEquals(instance.getNumModuleStates(), clone.getNumModuleStates());
        assertNotSame(instance.getModuleStates(), clone.getModuleStates());
        assertNotNull(clone.getModuleStatesArray()[0]);
        assertNotSame(instance.getModuleStatesArray()[0], 
            clone.getModuleStatesArray()[0]);
    }

    /**
     * Test of clear method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testClear()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        SemanticIdentifier aID = map.addLabel(a);
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        instance.setInput(new DummyModelInput());
        instance.getCogxels().getOrCreateCogxel(aID, 
            DefaultCogxelFactory.INSTANCE);
        instance.setInitialized(true);
        instance.getModuleStatesArray()[0] = new DummyModuleState();
        
        
        instance.clear();
        assertFalse(instance.isInitialized());
        assertNull(instance.getInput());
        assertNotNull(instance.getCogxels());
        assertNull(instance.getCogxels().getCogxel(aID));
        assertNotNull(instance.getModuleStates());
        assertEquals(3, instance.getModuleStates().size());
        assertNull(instance.getModuleStatesArray()[0]);
    }

    /**
     * Test of getInput method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testGetInput()
    {
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        assertNull(instance.getInput());
        
        DummyModelInput input = new DummyModelInput();
        instance.setInput(input);
        assertSame(input, instance.getInput());
    }

    /**
     * Test of getCogxels method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testGetCogxels()
    {
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        CogxelStateLite cogxels = instance.getCogxels();
        
        assertNotNull(cogxels);
        assertEquals(0, cogxels.getCogxels().size());
        assertSame(cogxels, instance.getCogxels());
    }

    /**
     * Test of getModuleStates method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testGetModuleStates()
    {
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        
        Collection<CognitiveModuleState> result = instance.getModuleStates();
        assertNotNull(result);
        assertEquals(3, result.size());
      
        DummyModuleState moduleState = new DummyModuleState();
        instance.getModuleStatesArray()[0] = moduleState;
        assertTrue(result.contains(moduleState));
    }

    /**
     * Test of getNumModuleStates method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testGetNumModuleStates()
    {
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        assertEquals(3, instance.getNumModuleStates());
    }

    /**
     * Test of isInitialized method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testIsInitialized()
    {
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        assertEquals(false, instance.isInitialized());
        
        instance.setInitialized(true);
        assertEquals(true, instance.isInitialized());
    }

    /**
     * Test of setInitialized method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testSetInitialized()
    {
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        instance.setInitialized(false);
        assertEquals(false, instance.isInitialized());
        
        instance.setInitialized(true);
        assertEquals(true, instance.isInitialized());
        
        instance.setInitialized(false);
        assertEquals(false, instance.isInitialized());
    }

    /**
     * Test of setInput method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testSetInput()
    {
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        
        DummyModelInput input = new DummyModelInput();
        instance.setInput(input);
        assertSame(input, instance.getInput());
        
        instance.setInput(null);
        assertNull(instance.getInput());
    }

    /**
     * Test of getModuleStatesArray method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testGetModuleStatesArray()
    {
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        
        CognitiveModuleState[] states = instance.getModuleStatesArray();
        assertNotNull(states);
        assertEquals(3, states.length);
        assertSame(states, instance.getModuleStatesArray());
    }

    /**
     * Test of setModuleStatesArray method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testSetModuleStatesArray()
    {
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        
        CognitiveModuleState[] states = instance.getModuleStatesArray();
        assertSame(states, instance.getModuleStatesArray());
    }

    /**
     * Test of setCogxels method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLiteState.
     */
    public void testSetCogxels()
    {
        CognitiveModelLiteState instance = new CognitiveModelLiteState(3);
        assertNotNull(instance.getCogxels());
        
        
        CogxelStateLite cogxels = new CogxelStateLite();
        instance.setCogxels(cogxels);
        
        assertSame(cogxels, instance.getCogxels());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setCogxels(null);
        }
        catch ( NullPointerException e )
        {
            exceptionThrown = true;
        }
        finally 
        {
            assertTrue(exceptionThrown);
        }
    }

}

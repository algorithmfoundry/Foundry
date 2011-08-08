/*
 * File:                CognitiveModelLiteTest.java
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

import junit.framework.*;
import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import java.util.List;

/**
 * This class implements JUnit tests for the following classes:
 *
 *    CognitiveModelLite
 *
 * @author Justin Basilico
 * @since  1.0
 */
public class CognitiveModelLiteTest 
    extends TestCase
{
    /**
     * Creates a new instance of CognitiveModelLiteTest.
     *
     * @param  testName The test name.
     */
    public CognitiveModelLiteTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(CognitiveModelLiteTest.class);
        
        return suite;
    }
    
    public CognitiveModelLite createTestInstance()
    {
        SimplePatternRecognizer recognizer =
            new SimplePatternRecognizer();
        recognizer.addNode( new DefaultSemanticLabel("i1") );
        recognizer.addNode( new DefaultSemanticLabel("i2") );
        recognizer.addNode( new DefaultSemanticLabel("i3") );

        recognizer.addNode( new DefaultSemanticLabel("o1") );
        recognizer.addNode( new DefaultSemanticLabel("o2") );

        recognizer.setAssociation( new DefaultSemanticLabel("i1"), new DefaultSemanticLabel("o1"), Math.random() );
        recognizer.setAssociation( new DefaultSemanticLabel("i3"), new DefaultSemanticLabel("o1"), Math.random() );
        recognizer.setAssociation( new DefaultSemanticLabel("i3"), new DefaultSemanticLabel("o2"), Math.random() );

        recognizer.setAssociation( new DefaultSemanticLabel("o1"), new DefaultSemanticLabel("o1"), Math.random() );
        recognizer.setAssociation( new DefaultSemanticLabel("o1"), new DefaultSemanticLabel("o2"), Math.random() );
        
        CognitiveModelLiteFactory factory = new CognitiveModelLiteFactory();
        factory.addModuleFactory(new ArrayBasedPerceptionModuleFactory());
        factory.addModuleFactory(
            new SharedSemanticMemoryLiteFactory(recognizer));
        
        return factory.createModel();
    }
    
    public void runUpdateTest(
        CognitiveModelLite model)
    {
        SemanticIdentifierMap map = model.getSemanticIdentifierMap();
        SemanticIdentifier i1 = map.addLabel(new DefaultSemanticLabel("i1"));
        SemanticIdentifier i2 = map.addLabel(new DefaultSemanticLabel("i2"));
        SemanticIdentifier i3 = map.addLabel(new DefaultSemanticLabel("i3"));
        
        SemanticIdentifier o1 = map.addLabel(new DefaultSemanticLabel("o1"));
        SemanticIdentifier o2 = map.addLabel(new DefaultSemanticLabel("o2"));
        
        SemanticIdentifier[] identifiers = 
            new SemanticIdentifier[] { i1, i2, i3 };
        double[] values = null;
        ArrayBasedCognitiveModelInput input = null;
        CogxelState cogxels = null;
        
        values = new double[] { 1.0, 0.0, 0.0 };
        input = new ArrayBasedCognitiveModelInput(identifiers, values, false);
        model.update(input);
        cogxels =  model.getCurrentState().getCogxels();
        
        assertEquals(0.0, cogxels.getCogxelActivation(o1));
        assertEquals(0.0, cogxels.getCogxelActivation(o2));
        
        
        values = new double[] { 1.0, 0.0, 0.0 };
        input = new ArrayBasedCognitiveModelInput(identifiers, values, false);
        model.update(input);
        cogxels =  model.getCurrentState().getCogxels();
        values = new double[] { 0.0, 0.0, 0.0 };
        input = new ArrayBasedCognitiveModelInput(identifiers, values, false);
        model.update(input);
        cogxels =  model.getCurrentState().getCogxels();
        
        values = new double[] { 0.0, 0.0, 0.0 };
        input = new ArrayBasedCognitiveModelInput(identifiers, values, false);
        model.update(input);
        model.update(input);
        model.update(input);
        model.update(input);
        model.update(input);
        model.update(input);
        
        cogxels =  model.getCurrentState().getCogxels();
    }

    /**
     * Test of resetCognitiveState method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLite.
     */
    public void testResetCognitiveState()
    {
        CognitiveModelLite instance = this.createTestInstance();
        CognitiveModelLiteState prevState = instance.getCurrentState();
        instance.resetCognitiveState();
        CognitiveModelLiteState state = instance.getCurrentState();
        assertNotSame(prevState, state);
        assertTrue(state.isInitialized());
    }

    /**
     * Test of setCognitiveState method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLite.
     */
    public void testSetCognitiveState()
    {
        CognitiveModelLite instance = this.createTestInstance();
        
        CognitiveModelLiteState state = instance.getCurrentState();
        instance.setCognitiveState(state);
        assertSame(state, instance.getCurrentState());
        
        state = new CognitiveModelLiteState(
            instance.getModules().size());
        assertFalse(state.isInitialized());
        instance.setCognitiveState(state);
        assertTrue(state.isInitialized());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setCognitiveState(new CognitiveModelLiteState(47));
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

    /**
     * Test of initializeCognitiveState method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLite.
     */
    public void testInitializeCognitiveState()
    {
        CognitiveModelLite instance = this.createTestInstance();
        
        CognitiveModelLiteState state = new CognitiveModelLiteState(
            instance.getModules().size());
        
        instance.initializeCognitiveState(state);
        assertTrue(state.isInitialized());
        assertEquals(2, state.getNumModuleStates());
        assertNotNull(state.getModuleStatesArray()[1]);
        
        boolean exceptionThrown = false;
        try
        {
            instance.initializeCognitiveState(new CognitiveModelLiteState(47));
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

    /**
     * Test of update method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLite.
     */
    public void testUpdate()
    {
        CognitiveModelLite instance = this.createTestInstance();
        this.runUpdateTest(instance);
    }

    /**
     * Test of getCurrentState method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLite.
     */
    public void testGetCurrentState()
    {
        CognitiveModelLite instance = this.createTestInstance();
        CognitiveModelLiteState result = instance.getCurrentState();
        assertNotNull(result);
        assertSame(result, instance.getCurrentState());
    }

    /**
     * Test of getModules method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLite.
     */
    public void testGetModules()
    {
        CognitiveModelLite instance = this.createTestInstance();
        List<CognitiveModule> result = instance.getModules();
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.get(0) instanceof ArrayBasedPerceptionModule);
        assertTrue(result.get(1) instanceof SharedSemanticMemoryLite);
    }

    /**
     * Test of getSemanticIdentifierMap method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLite.
     */
    public void testGetSemanticIdentifierMap()
    {
        CognitiveModelLite instance = this.createTestInstance();
        SemanticIdentifierMap result = instance.getSemanticIdentifierMap();
        assertNotNull(result);
        assertSame(result, instance.getSemanticIdentifierMap());
    }
}

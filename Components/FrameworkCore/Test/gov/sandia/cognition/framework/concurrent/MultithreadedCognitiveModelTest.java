/*
 * File:                MultithreadedCognitiveModelTest.java
 * Authors:             Cognitive Framework Lite
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jan 10, 2008, Sandia Corporation.  Under the terms of Contract 
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by 
 * or on behalf of the U.S. Government. Export of this program may require a 
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */
 
package gov.sandia.cognition.framework.concurrent;

import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CognitiveModelState;
import gov.sandia.cognition.framework.CognitiveModule;
import gov.sandia.cognition.framework.CognitiveModuleFactory;
import gov.sandia.cognition.framework.CognitiveModuleSettings;
import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticIdentifier;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.lite.ArrayBasedCognitiveModelInput;
import gov.sandia.cognition.framework.lite.ArrayBasedPerceptionModule;
import gov.sandia.cognition.framework.lite.ArrayBasedPerceptionModuleFactory;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteState;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Tests of MultithreadedCognitiveModel
 *
 * @author Zachary Benz
 * @since 2.0
 */
public class MultithreadedCognitiveModelTest extends TestCase {
    
    public MultithreadedCognitiveModelTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(MultithreadedCognitiveModelTest.class);
        
        return suite;
    }        
    
    class DummyConcurrentModule 
        extends AbstractConcurrentCognitiveModule
    {
        public void readState(
            final CognitiveModelState modelState,
            final CognitiveModuleState previousModuleState)
        {
            // Nothing to do
        }

        @SuppressWarnings("empty-statement")
        public void evaluate()
        {
            // Burn processor time
            long start = System.currentTimeMillis();
            long end = start + 2 * 100;  // 0.2 seconds
            
            while (System.currentTimeMillis() < end)
            {
                try
                {
                    Thread.sleep(1);
                }
                catch (InterruptedException ex)
                {
                    Logger.getLogger(MultithreadedCognitiveModelTest.class.getName()).log(Level.SEVERE,
                        null, ex);
                }
            }
        }

        public CognitiveModuleState writeState(
            CognitiveModelState modelState)
        {
            // Nothing to do
            return null;
        }

        public CognitiveModuleState initializeState(
            final CognitiveModelState modelState)
        {
            return null;
        }

        public String getName()
        {
            return "DummyConcurrentModule";
        }

        public CognitiveModuleSettings getSettings()
        {
            return null;
        }        
    }
    
    class DummyConcurrentModuleFactory
        implements CognitiveModuleFactory
    {
        public CognitiveModule createModule(CognitiveModel model)
        {
            return new DummyConcurrentModule();
        }        
    }
    
    public MultithreadedCognitiveModel createTestInstance()
    {                         
        MultithreadedCognitiveModelFactory factory = 
            new MultithreadedCognitiveModelFactory(4);
        factory.addModuleFactory(new ArrayBasedPerceptionModuleFactory());
        factory.addModuleFactory(new DummyConcurrentModuleFactory());
        factory.addModuleFactory(new DummyConcurrentModuleFactory());
        factory.addModuleFactory(new DummyConcurrentModuleFactory());
        factory.addModuleFactory(new DummyConcurrentModuleFactory());
        factory.addModuleFactory(new DummyConcurrentModuleFactory());
        factory.addModuleFactory(new DummyConcurrentModuleFactory());
        factory.addModuleFactory(new DummyConcurrentModuleFactory());
        factory.addModuleFactory(new DummyConcurrentModuleFactory());
        
        return factory.createModel();
    }
    
    public void runUpdateTest(
        MultithreadedCognitiveModel model)
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
        MultithreadedCognitiveModel instance = this.createTestInstance();
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
        MultithreadedCognitiveModel instance = this.createTestInstance();
        
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
        MultithreadedCognitiveModel instance = this.createTestInstance();
        
        CognitiveModelLiteState state = new CognitiveModelLiteState(
            instance.getModules().size());
        
        instance.initializeCognitiveState(state);
        assertTrue(state.isInitialized());
        assertEquals(9, state.getNumModuleStates());        
        
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
        MultithreadedCognitiveModel instance = this.createTestInstance();
        this.runUpdateTest(instance);
    }

    /**
     * Test of getCurrentState method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLite.
     */
    public void testGetCurrentState()
    {
        MultithreadedCognitiveModel instance = this.createTestInstance();
        CognitiveModelLiteState result = instance.getCurrentState();
        assertNotNull(result);
        assertSame(result, instance.getCurrentState());
    }

    /**
     * Test of getModules method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLite.
     */
    public void testGetModules()
    {
        MultithreadedCognitiveModel instance = this.createTestInstance();
        List<ConcurrentCognitiveModule> result = instance.getModules();
        
        assertNotNull(result);
        assertEquals(9, result.size());
        assertTrue(result.get(0) instanceof ArrayBasedPerceptionModule);
        assertTrue(result.get(1) instanceof DummyConcurrentModule);
        assertTrue(result.get(2) instanceof DummyConcurrentModule);
        assertTrue(result.get(3) instanceof DummyConcurrentModule);
        assertTrue(result.get(4) instanceof DummyConcurrentModule);
        assertTrue(result.get(5) instanceof DummyConcurrentModule);
        assertTrue(result.get(6) instanceof DummyConcurrentModule);
        assertTrue(result.get(7) instanceof DummyConcurrentModule);
        assertTrue(result.get(8) instanceof DummyConcurrentModule);        
    }

    /**
     * Test of getSemanticIdentifierMap method, of class gov.sandia.isrc.cognition.framework.lite.CognitiveModelLite.
     */
    public void testGetSemanticIdentifierMap()
    {
        MultithreadedCognitiveModel instance = this.createTestInstance();
        SemanticIdentifierMap result = instance.getSemanticIdentifierMap();
        assertNotNull(result);
        assertSame(result, instance.getSemanticIdentifierMap());
    }

}

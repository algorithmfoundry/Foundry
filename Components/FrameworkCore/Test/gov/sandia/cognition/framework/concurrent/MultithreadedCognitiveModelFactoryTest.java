/*
 * File:                MultithreadedCognitiveModelFactoryTest.java
 * Authors:             Zachary Benz
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

import gov.sandia.cognition.framework.lite.ArrayBasedPerceptionModuleFactory;
import junit.framework.TestCase;

/**
 * Tests of MultithreadedCognitiveModelFactory
 * @author Zachary Benz
 * @since 2.0
 */
public class MultithreadedCognitiveModelFactoryTest extends TestCase {
    
    public MultithreadedCognitiveModelFactoryTest(String testName) {
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

    /**
     * Tests the MultithreadedCognitiveModelFactory class.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testMultithreadedCognitiveModelFactory()
    {
        MultithreadedCognitiveModelFactory factory = 
            new MultithreadedCognitiveModelFactory(4);
        
        MultithreadedCognitiveModel model1 = factory.createModel();
        MultithreadedCognitiveModel model2 = factory.createModel();
        assertNotNull(model1);
        assertNotNull(model2);
        assertNotSame(model1, model2);
        assertEquals(0, model1.getModules().size());
        assertEquals(0, model1.getModules().size());
        
        factory.addModuleFactory(new ArrayBasedPerceptionModuleFactory());
        MultithreadedCognitiveModel model3 = factory.createModel();
        assertEquals(1, model3.getModules().size());
        
        factory.addModuleFactory(new ArrayBasedPerceptionModuleFactory());
        MultithreadedCognitiveModel model4 = factory.createModel();
        assertEquals(2, model4.getModules().size());
    }
    
}

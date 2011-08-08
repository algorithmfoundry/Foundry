/*
 * File:                CognitiveModelLiteFactoryTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 16, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.DummyModuleFactory;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     CognitiveModelLiteFactory
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class CognitiveModelLiteFactoryTest
    extends TestCase
{
    /**
     * Creates a new instance of CognitiveModelLiteFactoryTest.
     *
     * @param  testName The test name.
     */
    public CognitiveModelLiteFactoryTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the CognitiveModelLiteFactory class.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testCognitiveModelLiteFactory()
    {
        CognitiveModelLiteFactory factory = new CognitiveModelLiteFactory();
        
        CognitiveModelLite model1 = factory.createModel();
        CognitiveModelLite model2 = factory.createModel();
        assertNotNull(model1);
        assertNotNull(model2);
        assertNotSame(model1, model2);
        assertEquals(0, model1.getModules().size());
        assertEquals(0, model1.getModules().size());
        
        factory.addModuleFactory(new DummyModuleFactory());
        CognitiveModelLite model3 = factory.createModel();
        assertEquals(1, model3.getModules().size());
        
        factory.addModuleFactory(new DummyModuleFactory());
        CognitiveModelLite model4 = factory.createModel();
        assertEquals(2, model4.getModules().size());
    }

}

/*
 * File:                ArrayBasedPerceptionModuleFactoryTest.java
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

import gov.sandia.cognition.framework.DummyCogxelFactory;
import gov.sandia.cognition.framework.DummyModuleFactory;
import junit.framework.*;
import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.DefaultCogxelFactory;

/**
 * This class implements JUnit tests for the following classes:
 *      ArrayBasedPerceptionModuleFactory
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class ArrayBasedPerceptionModuleFactoryTest
    extends TestCase
{
    public ArrayBasedPerceptionModuleFactoryTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(ArrayBasedPerceptionModuleFactoryTest.class);
        
        return suite;
    }
    
    public void testConstructors()
    {
        ArrayBasedPerceptionModuleFactory instance = 
            new ArrayBasedPerceptionModuleFactory();
        assertEquals(DefaultCogxelFactory.INSTANCE, instance.getCogxelFactory());
        
        DummyCogxelFactory dummyFactory = new DummyCogxelFactory();
        instance = new ArrayBasedPerceptionModuleFactory(dummyFactory);
        assertSame(dummyFactory, instance.getCogxelFactory());
    }
    

    /**
     * Test of createModule method, of class gov.sandia.isrc.cognition.framework.lite.ArrayBasedPerceptionModuleFactory.
     */
    public void testCreateModule()
    {
        ArrayBasedPerceptionModuleFactory instance = 
            new ArrayBasedPerceptionModuleFactory();
        
        CognitiveModelLite model = new CognitiveModelLite(
            new DummyModuleFactory());
        
        ArrayBasedPerceptionModule module1 = instance.createModule(model);
        ArrayBasedPerceptionModule module2 = instance.createModule(model);
        assertNotNull(module1);
        assertNotNull(module2);
        assertNotSame(module1, module2);
    }

    /**
     * Test of getCogxelFactory method, of class gov.sandia.isrc.cognition.framework.lite.ArrayBasedPerceptionModuleFactory.
     */
    public void testGetCogxelFactory()
    {
        CogxelFactory factory = DefaultCogxelFactory.INSTANCE;
        ArrayBasedPerceptionModuleFactory instance = 
            new ArrayBasedPerceptionModuleFactory(factory);
        assertEquals(factory, instance.getCogxelFactory());
    }
}

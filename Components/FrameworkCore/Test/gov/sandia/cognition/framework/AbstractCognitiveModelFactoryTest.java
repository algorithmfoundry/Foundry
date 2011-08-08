/*
 * File:                AbstractCognitiveModelFactoryTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 10, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */
package gov.sandia.cognition.framework;

import gov.sandia.cognition.framework.lite.ArrayBasedPerceptionModuleFactory;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteFactory;
import junit.framework.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Unit tests for AbstractCognitiveModelFactoryTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class AbstractCognitiveModelFactoryTest extends TestCase
{
    
    public AbstractCognitiveModelFactoryTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(AbstractCognitiveModelFactoryTest.class);
        
        return suite;
    }

    /**
     * Test of addModuleFactory method, of class gov.sandia.isrc.cognition.framework.AbstractCognitiveModelFactory.
     */
    public void testAddModuleFactory()
    {
        System.out.println("addModuleFactory");
        
        CognitiveModuleFactory factory = null;
        AbstractCognitiveModelFactory instance =
            new CognitiveModelLiteFactory();
        
        try
        {
            instance.addModuleFactory(factory);
            fail( "Should have thrown null-pointer exception" );
        }
        catch( Exception e )
        {
            System.out.println( "Properly caught exception " + e );
        }
        
        factory = new ArrayBasedPerceptionModuleFactory();
        instance.addModuleFactory( factory );
        
    }

    /**
     * Test of getModuleFactories method, of class gov.sandia.isrc.cognition.framework.AbstractCognitiveModelFactory.
     */
    public void testGetModuleFactories()
    {
        System.out.println("getModuleFactories");
        
        AbstractCognitiveModelFactory instance = 
            new CognitiveModelLiteFactory();
                
        ArrayList<CognitiveModuleFactory> factories =
            new ArrayList<CognitiveModuleFactory>();
        
        assertNotSame( instance.getModuleFactories(), factories );
        
        instance.setModuleFactories( factories );
        assertEquals( instance.getModuleFactories(), factories );
        
    }

    /**
     * Test of setModuleFactories method, of class gov.sandia.isrc.cognition.framework.AbstractCognitiveModelFactory.
     */
    public void testSetModuleFactories()
    {
        System.out.println("setModuleFactories");
        
        AbstractCognitiveModelFactory instance = 
            new CognitiveModelLiteFactory();
                
        ArrayList<CognitiveModuleFactory> factories =
            new ArrayList<CognitiveModuleFactory>();
        
        assertNotSame( instance.getModuleFactories(), factories );
        
        instance.setModuleFactories( factories );
        assertEquals( instance.getModuleFactories(), factories );
    }



    
}

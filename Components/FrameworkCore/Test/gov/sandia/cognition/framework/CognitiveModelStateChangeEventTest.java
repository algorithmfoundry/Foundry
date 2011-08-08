/*
 * File:                CognitiveModelStateChangeEventTest.java
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

import gov.sandia.cognition.framework.lite.CognitiveModelLite;
import gov.sandia.cognition.framework.lite.CognitiveModelLiteState;
import junit.framework.*;

/**
 * Unit tests for CognitiveModelStateChangeEventTest
 *
 * @author Kevin R. Dixon
 * @since  1.0
 */
public class CognitiveModelStateChangeEventTest extends TestCase
{
    
    public CognitiveModelStateChangeEventTest(String testName)
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
        TestSuite suite = new TestSuite(CognitiveModelStateChangeEventTest.class);
        
        return suite;
    }

    /**
     * Test of getModel method, of class gov.sandia.isrc.cognition.framework.CognitiveModelStateChangeEvent.
     */
    public void testGetModel()
    {
        System.out.println("getModel");
        
        CognitiveModel model = new CognitiveModelLite();
        CognitiveModelState state = model.getCurrentState();
        
        CognitiveModelStateChangeEvent instance =
            new CognitiveModelStateChangeEvent( model, state );
        
        assertEquals( instance.getModel(), model );

    }

    /**
     * Test of getState method, of class gov.sandia.isrc.cognition.framework.CognitiveModelStateChangeEvent.
     */
    public void testGetState()
    {
        System.out.println("getState");
        
        CognitiveModel model = new CognitiveModelLite();
        CognitiveModelState state = model.getCurrentState();
        
        CognitiveModelStateChangeEvent instance =
            new CognitiveModelStateChangeEvent( model, state );

        assertEquals( instance.getState(), state );
    }

    /**
     * Test of setModel method, of class gov.sandia.isrc.cognition.framework.CognitiveModelStateChangeEvent.
     */
    public void testSetModel()
    {
        System.out.println("setModel");
        
        CognitiveModel model = new CognitiveModelLite();
        CognitiveModelState state = model.getCurrentState();
        
        CognitiveModelStateChangeEvent instance =
            new CognitiveModelStateChangeEvent( model, state );
        
        assertEquals( instance.getModel(), model );
        
        CognitiveModelLite model2 = new CognitiveModelLite();
        assertNotSame( instance.getModel(), model2 );
        instance.setModel( model2 );
        assertEquals( instance.getModel(), model2 );
        
        
    }

    /**
     * Test of setState method, of class gov.sandia.isrc.cognition.framework.CognitiveModelStateChangeEvent.
     */
    public void testSetState()
    {
        System.out.println("setState");
        
        CognitiveModel model = new CognitiveModelLite();
        CognitiveModelState state = model.getCurrentState();
        
        CognitiveModelStateChangeEvent instance =
            new CognitiveModelStateChangeEvent( model, state );

        assertEquals( instance.getState(), state );
        
        CognitiveModelState state2 = new CognitiveModelLiteState( 0 );
        assertNotSame( instance.getState(), state2 );
        instance.setState( state2 );
        assertEquals( instance.getState(), state2 );
        
    }
    
}

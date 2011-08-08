/*
 * File:                VectorBasedPerceptionModuleTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright June 25, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.DefaultCogxelFactory;
import junit.framework.*;
import gov.sandia.cognition.framework.Cogxel;
import gov.sandia.cognition.framework.CogxelFactory;
import java.util.Random;

/**
 *
 * @author Kevin R. Dixon
 * @since 2.0
 */
public class VectorBasedPerceptionModuleTest extends TestCase
{
    
    /** The random number generator for the tests. */
    protected Random random = new Random();
    
    public VectorBasedPerceptionModuleTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public VectorBasedPerceptionModule createInstance()
    {
        return new VectorBasedPerceptionModule( new DefaultCogxelFactory() );
    }
    
    /**
     * Test of initializeState method, of class gov.sandia.cognition.framework.lite.VectorBasedPerceptionModule.
     */
    public void testInitializeState()
    {
        System.out.println("initializeState");
        
        VectorBasedPerceptionModule instance = this.createInstance();
        assertNull( instance.initializeState( null ) );
        
    }

    /**
     * Test of getName method, of class gov.sandia.cognition.framework.lite.VectorBasedPerceptionModule.
     */
    public void testGetName()
    {
        System.out.println("getName");
        
        VectorBasedPerceptionModule instance = this.createInstance();
        assertEquals( instance.getName(), VectorBasedPerceptionModule.MODULE_NAME );
        assertNotNull( instance.getName() );
    }

    /**
     * Test of getSettings method, of class gov.sandia.cognition.framework.lite.VectorBasedPerceptionModule.
     */
    public void testGetSettings()
    {
        System.out.println("getSettings");
        
        VectorBasedPerceptionModule instance = this.createInstance();
        assertNull( instance.getSettings() );
    }

    /**
     * Test of update method, of class gov.sandia.cognition.framework.lite.VectorBasedPerceptionModule.
     */
    public void testUpdate()
    {
        System.out.println("update");
        
        CognitiveModelLite model = 
            new CognitiveModelLite(new VectorBasedPerceptionModuleFactory());
        
        
        
        VectorBasedCognitiveModelInput input = VectorBasedCognitiveModelInputTest.createInstance(random);
        
        model.update( input );
        for( int i = 0; i < input.getNumInputs(); i++ )
        {
            Cogxel c = model.getCurrentState().getCogxels().getCogxel(
                input.getIdentifier( i ) );
            assertEquals( input.getValues().getElement(i), c.getActivation() );
        }

    }

    /**
     * Test of getCogxelFactory method, of class gov.sandia.cognition.framework.lite.VectorBasedPerceptionModule.
     */
    public void testGetCogxelFactory()
    {
        System.out.println("getCogxelFactory");
        
        VectorBasedPerceptionModule instance = this.createInstance();
        CogxelFactory factory = instance.getCogxelFactory();
        assertNotNull( factory );
        
    }

    /**
     * Test of setCogxelFactory method, of class gov.sandia.cognition.framework.lite.VectorBasedPerceptionModule.
     */
    public void testSetCogxelFactory()
    {
        System.out.println("setCogxelFactory");
        
        VectorBasedPerceptionModule instance = this.createInstance();
        CogxelFactory factory = instance.getCogxelFactory();
        assertNotNull( factory );
        
        instance.setCogxelFactory( null );
        assertNull( instance.getCogxelFactory() );
        
        instance.setCogxelFactory( factory );
        assertSame( factory, instance.getCogxelFactory() );
    }
    
}

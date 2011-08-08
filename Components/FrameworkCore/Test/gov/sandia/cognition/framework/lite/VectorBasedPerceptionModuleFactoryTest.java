/*
 * VectorBasedPerceptionModuleFactoryTest.java
 * JUnit based test
 *
 * Created on June 25, 2007, 2:33 PM
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.DummyModuleFactory;
import junit.framework.*;
import gov.sandia.cognition.framework.CognitiveModel;
import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.DefaultCogxelFactory;

/**
 *
 * @author Kevin R. Dixon
 */
public class VectorBasedPerceptionModuleFactoryTest extends TestCase
{
    
    public VectorBasedPerceptionModuleFactoryTest(String testName)
    {
        super(testName);
    }

    protected void setUp() throws Exception
    {
    }

    protected void tearDown() throws Exception
    {
    }

    public VectorBasedPerceptionModuleFactory createInstance()
    {
        return new VectorBasedPerceptionModuleFactory( 
            DefaultCogxelFactory.INSTANCE );
    }
    
    /**
     * Test of getCogxelFactory method, of class gov.sandia.cognition.framework.lite.VectorBasedPerceptionModuleFactory.
     */
    public void testGetCogxelFactory()
    {
        System.out.println("getCogxelFactory");
        
        VectorBasedPerceptionModuleFactory instance = this.createInstance();
        CogxelFactory factory = instance.getCogxelFactory();
        assertNotNull( factory );
    }

    /**
     * Test of setCogxelFactory method, of class gov.sandia.cognition.framework.lite.VectorBasedPerceptionModuleFactory.
     */
    public void testSetCogxelFactory()
    {
        System.out.println("setCogxelFactory");
        
        VectorBasedPerceptionModuleFactory instance = this.createInstance();
        CogxelFactory factory = instance.getCogxelFactory();
        assertNotNull( factory );
        
        instance.setCogxelFactory( null );
        assertNull( instance.getCogxelFactory() );
        
        instance.setCogxelFactory( factory );
        assertSame( factory, instance.getCogxelFactory() );
    }

    /**
     * Test of createModule method, of class gov.sandia.cognition.framework.lite.VectorBasedPerceptionModuleFactory.
     */
    public void testCreateModule()
    {
        System.out.println("createModule");
        
        VectorBasedPerceptionModuleFactory instance = this.createInstance();
        CognitiveModel model = 
            new CognitiveModelLite( new DummyModuleFactory() );
     
        VectorBasedPerceptionModule m1 = instance.createModule( model );
        VectorBasedPerceptionModule m2 = instance.createModule( model );
        
        assertNotNull( m1 );
        assertNotNull( m2 );
        assertNotSame( m1, m2 );
        
    }
    
}

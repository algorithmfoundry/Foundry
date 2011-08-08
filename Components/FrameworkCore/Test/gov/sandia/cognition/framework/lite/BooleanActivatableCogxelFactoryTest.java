/*
 * File:                BooleanActivatableCogxelFactoryTest.java
 * Authors:             Jonathan McClain
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright July 3, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import junit.framework.*;
import gov.sandia.cognition.framework.Cogxel;
import gov.sandia.cognition.framework.DefaultSemanticIdentifier;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticIdentifier;

/**
 * This class implements JUnit tests for the following classes:
 * 
 *     BooleanActivatableCogxelFactory
 * 
 * 
 * @author Jonathan McClain
 * @since 1.0
 */
public class BooleanActivatableCogxelFactoryTest extends TestCase
{
    
    /**
     * Creates a new instance of BooleanActivatableCogxelFactoryTest.
     */
    public BooleanActivatableCogxelFactoryTest(String testName)
    {
        super(testName);
    }

    /**
     * Returns the test.
     */
    public static Test suite()
    {
        TestSuite suite = new TestSuite(BooleanActivatableCogxelFactoryTest.class);
        
        return suite;
    }

    /**
     * Test of createCogxel method, of class 
     * gov.sandia.isrc.cognition.framework.lite.BooleanActivatableCogxelFactory.
     */
    public void testCreateCogxel()
    {
        System.out.println("createCogxel");
        
        BooleanActivatableCogxelFactory factory = new BooleanActivatableCogxelFactory();
        
        // We need to use a label and an identifier.
        DefaultSemanticLabel labelA = new DefaultSemanticLabel("a");
        SemanticIdentifier identifierA = 
            new DefaultSemanticIdentifier(labelA, 0);
        
        // Test normal cogxel creation of two cogxels.
        Cogxel cogxelA1 = factory.createCogxel(identifierA);
        Cogxel cogxelA2 = factory.createCogxel(identifierA);
        
        // We need to ensure that the two cogxels we created are not the
        // same.
        assertNotSame(cogxelA1, cogxelA2);
        assertSame(identifierA, cogxelA1.getSemanticIdentifier());
        assertSame(identifierA, cogxelA2.getSemanticIdentifier());
        assertSame(labelA, cogxelA1.getSemanticLabel());
        assertSame(labelA, cogxelA2.getSemanticLabel());
        assertEquals(0.0, cogxelA1.getActivation());
        assertEquals(0.0, cogxelA2.getActivation());
        
        // Test null parameter.
        assertNull(factory.createCogxel(null));
    }
    
}

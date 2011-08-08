/*
 * File:                BooleanActivatableCogxelTest.java
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

import gov.sandia.cognition.framework.DefaultSemanticIdentifier;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import junit.framework.*;
import gov.sandia.cognition.framework.SemanticIdentifier;

/**
 * This class implements JUnit tests for the following classes:
 * 
 *     BooleanActivatableCogxel
 * 
 * 
 * @author Jonathan McClain
 * @since 1.0
 */
public class BooleanActivatableCogxelTest extends TestCase
{
    
    /**
     * Creates a new instance of BooleanActivatableCogxelTest.
     */
    public BooleanActivatableCogxelTest(String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(BooleanActivatableCogxelTest.class);
        
        return suite;
    }

    /**
     * Test of activated method, of class 
     * gov.sandia.isrc.cognition.framework.lite.BooleanActivatableCogxel.
     */
    public void testIsActivated()
    {
        System.out.println("activated");
        
        DefaultSemanticLabel labelA = new DefaultSemanticLabel("a");
        SemanticIdentifier identifierA = 
                new DefaultSemanticIdentifier(labelA, 0);
        
        BooleanActivatableCogxel cogxel = 
                new BooleanActivatableCogxel(identifierA, true);
        
        assertTrue(
                "activated returned false when it should have returned true.", 
                cogxel.isActivated());
        
        cogxel.setActivated(false);
        
        assertFalse(
                "activated returned true when it should have returned false.", 
                cogxel.isActivated());
    }

    /**
     * Test of activate method, of class 
     * gov.sandia.isrc.cognition.framework.lite.BooleanActivatableCogxel.
     */
    public void testSetActivated()
    {
        System.out.println("activate");
        
        DefaultSemanticLabel labelA = new DefaultSemanticLabel("a");
        SemanticIdentifier identifierA = 
                new DefaultSemanticIdentifier(labelA, 0);
        
        BooleanActivatableCogxel cogxel = 
                new BooleanActivatableCogxel(identifierA);
        
        cogxel.setActivated(true);
        
        assertTrue(
                "activated returned false when it should have returned true.", 
                cogxel.isActivated());
        
        cogxel.setActivated(false);
        
        assertFalse(
                "activated returned true when it should have returned false.", 
                cogxel.isActivated());
    }
    
}

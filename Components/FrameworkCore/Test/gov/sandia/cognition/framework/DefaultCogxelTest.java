/*
 * File:                DefaultCogxelTest.java
 * Authors:             Justin Basilico 
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 14, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework;

import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *    DefaultCogxel
 *
 * @author Justin Basilico
 * @since  1.0
 */
public class DefaultCogxelTest 
    extends TestCase
{
    /**
     * Creates a new instance of DefaultCogxelTest.
     * 
     * 
     * @param testName The test name.
     */
    public DefaultCogxelTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the Cogxel class.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testCogxel()
    {
        // We need two labels and two identifiers in the test.
        DefaultSemanticLabel labelA = new DefaultSemanticLabel("a");
        DefaultSemanticLabel labelB = new DefaultSemanticLabel("b");
        SemanticIdentifier identifierA = new DefaultSemanticIdentifier(labelA, 0);
        SemanticIdentifier identifierB = new DefaultSemanticIdentifier(labelB, 1);
        
        // Create a Cogxel for a. It should have no activation.
        DefaultCogxel cogxelA = new DefaultCogxel(identifierA);
        assertSame(cogxelA.getSemanticIdentifier(), identifierA);
        assertSame(cogxelA.getSemanticLabel(), labelA);
        assertEquals(0.0, cogxelA.getActivation(), 0.0);
        
        // Set the activation of a to a positive number.
        cogxelA.setActivation(1.0);
        assertEquals(1.0, cogxelA.getActivation());

        // Set the activation of a to a negative number.
        cogxelA.setActivation(-1.0);
        assertEquals(-1.0, cogxelA.getActivation());
        
        // Create a Cogxel for b with a starting activation of 2.0.
        DefaultCogxel cogxelB = new DefaultCogxel(identifierB, 2.0);
        assertSame(cogxelB.getSemanticIdentifier(), identifierB);
        assertSame(cogxelB.getSemanticLabel(), labelB);
        assertEquals(2.0, cogxelB.getActivation());
        
        // Test creating a cogxel with a bad identifier as the only argument.
        boolean exceptionThrown = false;
        try
        {
            SemanticIdentifier badID = null;
            DefaultCogxel bad = new DefaultCogxel(badID);
        }
        catch (NullPointerException npe)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        // Test creating a cogxel with a bad identifier using two arguments.
        exceptionThrown = false;
        try
        {
            DefaultCogxel bad = new DefaultCogxel(null, 1.0);
        }
        catch (NullPointerException npe)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

}

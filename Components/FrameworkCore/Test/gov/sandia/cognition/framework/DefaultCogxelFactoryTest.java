/*
 * File:                DefaultCogxelFactoryTest.java
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

package gov.sandia.cognition.framework;

import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     DefaultCogxelFactory
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class DefaultCogxelFactoryTest
    extends TestCase
{
    /**
     * Creates a new instance of DefaultCogxelFactoryTest.
     *
     * @param  testName The test name.
     */
    public DefaultCogxelFactoryTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the DefaultCogxelFactory class.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testDefaultCogxelFactory()
    {
        // Create a new factory.
        DefaultCogxelFactory factory = new DefaultCogxelFactory();
        
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

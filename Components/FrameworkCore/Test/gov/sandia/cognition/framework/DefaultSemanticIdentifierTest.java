/*
 * File:                DefaultSemanticIdentifierTest.java
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
 *    DefaultSemanticIdentifier
 *
 * @author Justin Basilico
 * @since  1.0
 */
public class DefaultSemanticIdentifierTest 
    extends TestCase
{
    /**
     * Creates a new instance of DefaultSemanticIdentifierTest.
     * 
     * 
     * @param testName The test name.
     */
    public DefaultSemanticIdentifierTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the DefaultSemanticIdentifier class.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testDefaultSemanticIdentifier() 
    {
        // Create three labels.
        DefaultSemanticLabel labelA = new DefaultSemanticLabel("a");
        DefaultSemanticLabel labelB = new DefaultSemanticLabel("b");
        DefaultSemanticLabel labelC = new DefaultSemanticLabel("c");
        
        // Create a semantic identifier for each label.
        SemanticIdentifier a = new DefaultSemanticIdentifier(labelA, 0);
        SemanticIdentifier b = new DefaultSemanticIdentifier(labelB, 1);
        SemanticIdentifier c = new DefaultSemanticIdentifier(labelC, 2);
        
        // Make sure that the identifiers are set up properly.
        assertSame(a.getLabel(), labelA);
        assertSame(b.getLabel(), labelB);
        assertSame(c.getLabel(), labelC);
        assertEquals(0, a.getIdentifier());
        assertEquals(1, b.getIdentifier());
        assertEquals(2, c.getIdentifier());
        
        // Make sure the hash-code is done properly.
        assertEquals(0, a.hashCode());
        assertEquals(1, b.hashCode());
        assertEquals(2, c.hashCode());
        
        // Make sure that equality comparison is proper.
        assertTrue(a.equals(a));
        assertTrue(b.equals(b));
        assertTrue(c.equals(c));
        assertFalse(a.equals(b));
        assertFalse(b.equals(c));
        assertFalse(c.equals(a));
        assertFalse(a.equals(new Object()));
        assertFalse(a.equals(null));
        
        // Make sure that passing in null is handled properly.
        boolean exceptionThrown = false;
        try
        {
            SemanticIdentifier badIdentifier = new DefaultSemanticIdentifier(null, 0);
        }
        catch (NullPointerException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

}

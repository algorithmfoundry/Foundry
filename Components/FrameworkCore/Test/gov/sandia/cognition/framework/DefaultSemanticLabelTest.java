/*
 * File:                DefaultSemanticLabelTest.java
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
 *    DefaultSemanticLabel
 *
 * @author Justin Basilico
 * @since  1.0
 */
public class DefaultSemanticLabelTest 
    extends TestCase
{
    /**
     * Creates a new instance of DefaultSemanticLabelTest.
     *
     * @param  testName The test name.
     */
    public DefaultSemanticLabelTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the DefaultSemanticLabel class.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testDefaultSemanticLabel()
    {
        // Create three semantic labels to use.
        DefaultSemanticLabel label1 = new DefaultSemanticLabel("a");
        DefaultSemanticLabel label2 = new DefaultSemanticLabel("b");
        DefaultSemanticLabel label3 = new DefaultSemanticLabel("a");
        
        // Make sure that the name gets set properly and that the hash code is
        // proper.
        assertEquals("a", label1.getName());
        assertEquals("a", label1.toString());
        assertEquals("a".hashCode(), label1.hashCode());
        
        // Make sure equality between labels works.
        assertTrue(label1.equals(label1));
        assertFalse(label1.equals(null));
        assertFalse(label1.equals(label2));
        assertFalse(label2.equals(label1));
        assertTrue(label1.equals(label3));
        assertTrue(label3.equals(label3));   
        
        // Make sure comparison between labels works.
        assertTrue(label1.compareTo(label1) == 0);
        assertTrue(label1.compareTo(label2) < 0);
        assertTrue(label2.compareTo(label1) > 0);
        assertTrue(label1.compareTo(label3) == 0);
        assertTrue(label3.compareTo(label1) == 0);
        
        // Make sure the label deals with passing in null properly.
        boolean exceptionThrown = false;
        try
        {
            DefaultSemanticLabel badLabel = new DefaultSemanticLabel(null);
        }
        catch ( NullPointerException npe )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

}

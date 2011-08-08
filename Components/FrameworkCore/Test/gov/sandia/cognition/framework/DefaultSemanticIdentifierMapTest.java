/*
 * File:                DefaultSemanticIdentifierMapTest.java
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

import java.util.ArrayList;
import junit.framework.*;

/**
 * This class implements JUnit tests for the following classes:
 *
 *    DefaultSemanticIdentifierMap
 *
 * @author Justin Basilico
 * @since  1.0
 */
public class DefaultSemanticIdentifierMapTest 
    extends TestCase
{
    /**
     * Creates a new instance of DefaultSemanticIdentifierMapTest.
     *
     * @param  testName The test name.
     */
    public DefaultSemanticIdentifierMapTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the DefaultSemanticIdentifierMap class.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testDefaultSemanticIdentifierMap()
    {
        // Create a new semantic identifier map.
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        
        // Create three labels to use.
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        DefaultSemanticLabel d = new DefaultSemanticLabel("d");
        
        // Make sure that there is no identfiers already in the map.
        assertNull(map.findIdentifier(a));
        assertNull(map.findIdentifier(b));
        assertNull(map.findIdentifier(c));
        
        // Add the label for a to the map.
        SemanticIdentifier idA = map.addLabel(a);
        
        // Assert that it is a valid identifier.
        assertNotNull(idA);
        assertSame(idA, map.findIdentifier(a));
        assertSame(idA, map.addLabel(a));
        assertSame(idA, map.findIdentifier(new DefaultSemanticLabel("a")));
        
        // Add b and c to the map.
        SemanticIdentifier idB = map.addLabel(b);
        SemanticIdentifier idC = map.addLabel(c);
        
        // Make sure that the identifiers are unique.
        assertTrue(idA.getIdentifier() != idB.getIdentifier());
        assertTrue(idB.getIdentifier() != idC.getIdentifier());
        assertTrue(idC.getIdentifier() != idA.getIdentifier());
        
        // Make sure that the list of identifiers has all three.
        assertTrue(map.getIdentifiers().contains(idA));
        assertTrue(map.getIdentifiers().contains(idB));
        assertTrue(map.getIdentifiers().contains(idC));
        
        ArrayList<SemanticLabel> labelsCD = new ArrayList<SemanticLabel>();
        labelsCD.add(c);
        labelsCD.add(d);
        
        ArrayList<SemanticIdentifier> idsCD = map.addLabels(labelsCD);
        
        assertNotNull(idsCD);
        assertEquals(2, idsCD.size());
        assertSame(idC, idsCD.get(0));
        assertSame(d, idsCD.get(1).getLabel());
        
        // See if it responds properly to looking up null.
        assertNull(map.findIdentifier(null));
        
        // See if it responds properly to attempting to add null.
        boolean exceptionThrown = false;
        try
        {
            map.addLabel(null);
        }
        catch (IllegalArgumentException iae)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }


}

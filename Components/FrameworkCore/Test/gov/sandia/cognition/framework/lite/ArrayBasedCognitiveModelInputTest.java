/*
 * File:                ArrayBasedCognitiveModelInputTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright March 29, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import junit.framework.*;
import gov.sandia.cognition.framework.SemanticIdentifier;
import java.util.Arrays;

/**
 * This class implements JUnit tests for the following classes:
 *     ArrayBasedCognitiveModelInput
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class ArrayBasedCognitiveModelInputTest
    extends TestCase
{
    public ArrayBasedCognitiveModelInputTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(ArrayBasedCognitiveModelInputTest.class);
        
        return suite;
    }

    
    public void testConstructors()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        
        SemanticIdentifier[] ids = new SemanticIdentifier[] { a, b };
        double[] values = new double[] { 1.0, 2.0 };
        
        ArrayBasedCognitiveModelInput instance = null;
        
        // Try the constructor that does not copy.
        instance = new ArrayBasedCognitiveModelInput(ids, values, false);
        assertEquals(2, instance.getNumInputs());
        assertSame(ids, instance.getIdentifiers());
        assertSame(values, instance.getValues());
        
        // Try the constructor that does copy.
        instance = new ArrayBasedCognitiveModelInput(ids, values, true);
        assertEquals(2, instance.getNumInputs());
        assertNotSame(ids, instance.getIdentifiers());
        assertNotSame(ids, instance.getValues());
        assertTrue(Arrays.deepEquals(ids, instance.getIdentifiers()));
        assertEquals(1.0, instance.getValues()[0]);
        assertEquals(2.0, instance.getValues()[1]);
    }
    /**
     * Test of getIdentifier method, of class gov.sandia.isrc.cognition.framework.lite.ArrayBasedCognitiveModelInput.
     */
    public void testGetIdentifier()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        
        SemanticIdentifier[] ids = new SemanticIdentifier[] { a, b };
        double[] values = new double[] { 1.0, 2.0 };
        
        ArrayBasedCognitiveModelInput instance = 
            new ArrayBasedCognitiveModelInput(ids, values, false);
        assertEquals(a, instance.getIdentifier(0));
        assertEquals(b, instance.getIdentifier(1));
    }

    /**
     * Test of getValue method, of class gov.sandia.isrc.cognition.framework.lite.ArrayBasedCognitiveModelInput.
     */
    public void testGetValue()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        
        SemanticIdentifier[] ids = new SemanticIdentifier[] { a, b };
        double[] values = new double[] { 1.0, 2.0 };
        
        ArrayBasedCognitiveModelInput instance = 
            new ArrayBasedCognitiveModelInput(ids, values, false);
        assertEquals(values[0], instance.getValue(0));
        assertEquals(values[1], instance.getValue(1));
    }

    /**
     * Test of getNumInputs method, of class gov.sandia.isrc.cognition.framework.lite.ArrayBasedCognitiveModelInput.
     */
    public void testGetNumInputs()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        
        SemanticIdentifier[] ids = new SemanticIdentifier[] { a, b };
        double[] values = new double[] { 1.0, 2.0 };
        
        ArrayBasedCognitiveModelInput instance = 
            new ArrayBasedCognitiveModelInput(ids, values, false);
        assertEquals(2, instance.getNumInputs());
    }
    
    /**
     * Test of getIdentifier method, of class gov.sandia.isrc.cognition.framework.lite.ArrayBasedCognitiveModelInput.
     */
    public void testGetIdentifiers()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        
        SemanticIdentifier[] ids = new SemanticIdentifier[] { a, b };
        double[] values = new double[] { 1.0, 2.0 };
        
        ArrayBasedCognitiveModelInput instance = 
            new ArrayBasedCognitiveModelInput(ids, values, false);
        assertSame(ids, instance.getIdentifiers());
    }

    /**
     * Test of getValue method, of class gov.sandia.isrc.cognition.framework.lite.ArrayBasedCognitiveModelInput.
     */
    public void testGetValues()
    {
        DefaultSemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        SemanticIdentifier a = map.addLabel(new DefaultSemanticLabel("a"));
        SemanticIdentifier b = map.addLabel(new DefaultSemanticLabel("b"));
        
        SemanticIdentifier[] ids = new SemanticIdentifier[] { a, b };
        double[] values = new double[] { 1.0, 2.0 };
        
        ArrayBasedCognitiveModelInput instance = 
            new ArrayBasedCognitiveModelInput(ids, values, false);
        assertSame(values, instance.getValues());
    }
}

/*
 * File:                CogxelDoubleConverterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.learning.converter.CogxelDoubleConverter;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultCogxel;
import gov.sandia.cognition.framework.DefaultCogxelFactory;
import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.framework.lite.CogxelStateLite;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 * @author Justin Basilico
 * @since  2.0
 */
public class CogxelDoubleConverterTest
    extends TestCase
{
    public CogxelDoubleConverterTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.framework.learning.CogxelDoubleConverter.
     */
    public void testClone()
    {
        CogxelDoubleConverter instance = new CogxelDoubleConverter();
        CogxelDoubleConverter clone = instance.clone();
        assertNotSame(instance, clone);
        
        assertEquals(instance.getLabel(), clone.getLabel());
        assertSame(instance.getSemanticIdentifierMap(), clone.getSemanticIdentifierMap());
        assertSame(instance.getCogxelFactory(), clone.getCogxelFactory());
        
        instance = new CogxelDoubleConverter(new DefaultSemanticLabel("a"),
            new DefaultSemanticIdentifierMap());
        
        clone = instance.clone();
        assertNotSame(instance, clone);

        assertEquals(instance.getLabel(), clone.getLabel());
        assertSame(instance.getSemanticIdentifierMap(), clone.getSemanticIdentifierMap());
        assertSame(instance.getCogxelFactory(), clone.getCogxelFactory());
    }

    /**
     * Test of fromCogxels method, of class gov.sandia.cognition.framework.learning.CogxelDoubleConverter.
     */
    public void testFromCogxels()
    {
        CogxelDoubleConverter instance = new CogxelDoubleConverter();
        SemanticLabel label = new DefaultSemanticLabel("a");
        instance.setLabel(label);
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap(map);
        
        CogxelState cogxels = new CogxelStateLite();
        
        Double d = instance.fromCogxels(cogxels);
        assertEquals(0.0, d);
        
        cogxels.addCogxel(new DefaultCogxel(map.findIdentifier(label),  4.7));
        d = instance.fromCogxels(cogxels);
        assertEquals(4.7, d);
    }

    /**
     * Test of toCogxels method, of class gov.sandia.cognition.framework.learning.CogxelDoubleConverter.
     */
    public void testToCogxels()
    {
        CogxelDoubleConverter instance = new CogxelDoubleConverter();
        SemanticLabel label = new DefaultSemanticLabel("a");
        instance.setLabel(label);
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap(map);
        
        CogxelState cogxels = new CogxelStateLite();
        
        instance.toCogxels(4.7, cogxels);
        assertEquals(4.7, cogxels.getCogxelActivation(map.findIdentifier(label)));
        assertEquals(1, cogxels.getNumCogxels());
        
        instance.toCogxels(0.0, cogxels);
        assertEquals(0.0, cogxels.getCogxelActivation(map.findIdentifier(label)));
    }

    /**
     * Test of getSemanticIdentifierMap method, of class gov.sandia.cognition.framework.learning.CogxelDoubleConverter.
     */
    public void testGetSemanticIdentifierMap()
    {
        testSetSemanticIdentifierMap();
    }

    /**
     * Test of setSemanticIdentifierMap method, of class gov.sandia.cognition.framework.learning.CogxelDoubleConverter.
     */
    public void testSetSemanticIdentifierMap()
    {
        CogxelDoubleConverter instance = new CogxelDoubleConverter();
        assertNull(instance.getSemanticIdentifierMap());
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap(map);
        assertSame(instance.getSemanticIdentifierMap(), map);
        
        instance.setSemanticIdentifierMap(null);
        assertNull(instance.getSemanticIdentifierMap());
    }

    /**
     * Test of getLabel method, of class gov.sandia.cognition.framework.learning.CogxelDoubleConverter.
     */
    public void testGetLabel()
    {
        testSetLabel();
    }

    /**
     * Test of setLabel method, of class gov.sandia.cognition.framework.learning.CogxelDoubleConverter.
     */
    public void testSetLabel()
    {
        CogxelDoubleConverter instance = new CogxelDoubleConverter();
        assertNull(instance.getLabel());
        
        SemanticLabel label = new DefaultSemanticLabel("a");
        instance.setLabel(label);
        assertSame(label, instance.getLabel());
        
        instance.setLabel(null);
        assertNull(instance.getLabel());
    }

    /**
     * Test of getCogxelFactory method, of class gov.sandia.cognition.framework.learning.CogxelDoubleConverter.
     */
    public void testGetCogxelFactory()
    {
        testSetCogxelFactory();
    }

    /**
     * Test of setCogxelFactory method, of class gov.sandia.cognition.framework.learning.CogxelDoubleConverter.
     */
    public void testSetCogxelFactory()
    {
        CogxelDoubleConverter instance = new CogxelDoubleConverter();
        assertSame(instance.getCogxelFactory(), DefaultCogxelFactory.INSTANCE);
        
        CogxelFactory factory = new DefaultCogxelFactory();
        instance.setCogxelFactory(factory);
        assertSame(instance.getCogxelFactory(), factory);
        
        instance.setCogxelFactory(null);
        assertNull(instance.getCogxelFactory());
    }

    /**
     * Test of equals method, of class gov.sandia.cognition.framework.learning.CogxelDoubleConverter.
     */
    public void testEquals()
    {
        CogxelDoubleConverter instance1 = new CogxelDoubleConverter();
        assertEquals(instance1, instance1);
        assertEquals(instance1, instance1.clone());
        
        
        CogxelDoubleConverter instance2 = new CogxelDoubleConverter(
            new DefaultSemanticLabel("a"));
        
        assertEquals(instance2, instance2);
        assertEquals(instance2, instance2.clone());
        assertFalse(instance1.equals(instance2));
        assertFalse(instance2.equals(instance1));
        assertFalse(instance1.equals(null));
        assertFalse(instance2.equals(null));
        assertFalse(instance1.equals("a"));
    }
}

/*
 * File:                CogxelBooleanConverterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright March 06, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.CogxelFactory;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultCogxel;
import gov.sandia.cognition.framework.DefaultCogxelFactory;
import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.framework.lite.CogxelStateLite;
import junit.framework.TestCase;

/**
 * Tests of XogxelBooleanConverter
 * @author  Justin Basilico
 * @since   3.0
 */
public class CogxelBooleanConverterTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public CogxelBooleanConverterTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of fromCogxels method, of class CogxelBooleanConverter.
     */
    public void testFromCogxels()
    {
        CogxelBooleanConverter instance = new CogxelBooleanConverter();
        SemanticLabel label = new DefaultSemanticLabel("a");
        instance.setLabel(label);

        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap(map);

        CogxelState cogxels = new CogxelStateLite();

        boolean b = instance.fromCogxels(cogxels);
        assertEquals(false, b);

        cogxels.addCogxel(new DefaultCogxel(map.findIdentifier(label),  4.7));
        b = instance.fromCogxels(cogxels);
        assertEquals(true, b);


        cogxels.addCogxel(new DefaultCogxel(map.findIdentifier(label),  -4.7));
        b = instance.fromCogxels(cogxels);
        assertEquals(false, b);
    }

    /**
     * Test of toCogxels method, of class CogxelBooleanConverter.
     */
    public void testToCogxels()
    {
        CogxelBooleanConverter instance = new CogxelBooleanConverter();
        SemanticLabel label = new DefaultSemanticLabel("a");
        instance.setLabel(label);

        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap(map);

        CogxelState cogxels = new CogxelStateLite();

        instance.toCogxels(true, cogxels);
        assertEquals(1.0, cogxels.getCogxelActivation(map.findIdentifier(label)));
        assertEquals(1, cogxels.getNumCogxels());

        instance.toCogxels(false, cogxels);
        assertEquals(-1.0, cogxels.getCogxelActivation(map.findIdentifier(label)));
    }

    /**
     * Test of getLabel method, of class CogxelBooleanConverter.
     */
    public void testGetLabel()
    {
        this.testSetLabel();
    }

    /**
     * Test of setLabel method, of class CogxelBooleanConverter.
     */
    public void testSetLabel()
    {
        CogxelBooleanConverter instance = new CogxelBooleanConverter();
        assertNull(instance.getLabel());

        SemanticLabel label = new DefaultSemanticLabel("a");
        instance.setLabel(label);
        assertSame(label, instance.getLabel());

        instance.setLabel(null);
        assertNull(instance.getLabel());
    }

    /**
     * Test of getCogxelFactory method, of class CogxelBooleanConverter.
     */
    public void testGetCogxelFactory()
    {
        this.testSetCogxelFactory();
    }

    /**
     * Test of setCogxelFactory method, of class CogxelBooleanConverter.
     */
    public void testSetCogxelFactory()
    {
        CogxelBooleanConverter instance = new CogxelBooleanConverter();
        assertSame(instance.getCogxelFactory(), DefaultCogxelFactory.INSTANCE);

        CogxelFactory factory = new DefaultCogxelFactory();
        instance.setCogxelFactory(factory);
        assertSame(instance.getCogxelFactory(), factory);

        instance.setCogxelFactory(null);
        assertNull(instance.getCogxelFactory());
    }

    /**
     * Test of equals method, of class CogxelBooleanConverter.
     */
    public void testEquals()
    {
        CogxelBooleanConverter instance1 = new CogxelBooleanConverter();
        assertEquals(instance1, instance1);
        assertEquals(instance1, instance1.clone());
        

        CogxelBooleanConverter instance2 = new CogxelBooleanConverter(
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

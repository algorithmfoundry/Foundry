/*
 * File:                InputOutputPairCogxelConverterTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 26, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.framework.learning.converter;

import gov.sandia.cognition.framework.DefaultSemanticIdentifierMap;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.CogxelState;
import gov.sandia.cognition.framework.DefaultCogxel;
import gov.sandia.cognition.framework.SemanticIdentifierMap;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.framework.lite.CogxelStateLite;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 * @author Justin Basilico
 * @since  2.0
 */
public class CogxelInputOutputPairConverterTest
    extends TestCase
{
    public CogxelInputOutputPairConverterTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        SemanticLabel d = new DefaultSemanticLabel("d");
        SemanticLabel e = new DefaultSemanticLabel("e");
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        
        CogxelConverter<Vector> inputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { a, b, c });
        CogxelConverter<Vector> outputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { d, e });
        
        CogxelInputOutputPairConverter<Vector, Vector> instance = 
            new CogxelInputOutputPairConverter<Vector, Vector>();
        assertNull(instance.getInputConverter());
        assertNull(instance.getOutputConverter());
        assertNull(instance.getSemanticIdentifierMap());
        
        instance = new CogxelInputOutputPairConverter<Vector, Vector>(
            inputConverter, outputConverter);
        assertSame(instance.getInputConverter(), inputConverter);
        assertSame(instance.getOutputConverter(), outputConverter);
        assertNull(instance.getSemanticIdentifierMap());
        
        instance = new CogxelInputOutputPairConverter<Vector, Vector>(
            inputConverter, outputConverter, map);
        assertSame(instance.getInputConverter(), inputConverter);
        assertSame(instance.getOutputConverter(), outputConverter);
        assertSame(instance.getSemanticIdentifierMap(), map);
        
    }

    /**
     * Test of clone method, of class gov.sandia.cognition.framework.learning.InputOutputPairCogxelConverter.
     */
    public void testClone()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        SemanticLabel d = new DefaultSemanticLabel("d");
        SemanticLabel e = new DefaultSemanticLabel("e");
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        
        CogxelConverter<Vector> inputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { a, b, c });
        CogxelConverter<Vector> outputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { d, e });
        
        CogxelInputOutputPairConverter<Vector, Vector> instance = 
            new CogxelInputOutputPairConverter<Vector, Vector>(
                inputConverter, outputConverter, map);
        
        CogxelInputOutputPairConverter<Vector, Vector> clone =
            (CogxelInputOutputPairConverter<Vector, Vector>) instance.clone();
        assertNotSame(clone.getInputConverter(), instance.getInputConverter());
        assertEquals(inputConverter, clone.getInputConverter());
        assertNotSame(clone.getOutputConverter(), instance.getOutputConverter());
        assertEquals(outputConverter, clone.getOutputConverter());
        assertSame(clone.getSemanticIdentifierMap(), map);
    }

    /**
     * Test of fromCogxels method, of class gov.sandia.cognition.framework.learning.InputOutputPairCogxelConverter.
     */
    public void testFromCogxels()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        SemanticLabel d = new DefaultSemanticLabel("d");
        SemanticLabel e = new DefaultSemanticLabel("e");
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        
        CogxelConverter<Vector> inputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { a, b, c });
        CogxelConverter<Vector> outputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { d, e });
        
        CogxelInputOutputPairConverter<Vector, Vector> instance = 
            new CogxelInputOutputPairConverter<Vector, Vector>(
                inputConverter, outputConverter);
        instance.setSemanticIdentifierMap(map);
        
        CogxelState cogxels = new CogxelStateLite();
        
        cogxels.addCogxel(new DefaultCogxel(map.addLabel(a), 1.0));
        cogxels.addCogxel(new DefaultCogxel(map.addLabel(b), 2.0));
        cogxels.addCogxel(new DefaultCogxel(map.addLabel(c), 3.0));
        cogxels.addCogxel(new DefaultCogxel(map.addLabel(d), -1.0));
        cogxels.addCogxel(new DefaultCogxel(map.addLabel(e), -2.0));
        
        InputOutputPair<Vector, Vector> pair = instance.fromCogxels(cogxels);
        assertNotNull(pair);
        assertEquals(new Vector3(1.0, 2.0, 3.0), pair.getInput());
        assertEquals(new Vector2(-1.0, -2.0), pair.getOutput());
    }

    /**
     * Test of toCogxels method, of class gov.sandia.cognition.framework.learning.InputOutputPairCogxelConverter.
     */
    public void testToCogxels()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        SemanticLabel d = new DefaultSemanticLabel("d");
        SemanticLabel e = new DefaultSemanticLabel("e");
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        
        CogxelConverter<Vector> inputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { a, b, c });
        CogxelConverter<Vector> outputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { d, e });
        
        CogxelInputOutputPairConverter<Vector, Vector> instance = 
            new CogxelInputOutputPairConverter<Vector, Vector>(
                inputConverter, outputConverter);
        instance.setSemanticIdentifierMap(map);
        
        InputOutputPair<Vector, Vector> pair = 
            new DefaultInputOutputPair<Vector, Vector>(
                new Vector3(1.0, 2.0, 3.0),
                new Vector2(-1.0, -2.0));
        
        CogxelState cogxels = new CogxelStateLite();
        
        instance.toCogxels(pair, cogxels);
        
        assertEquals(1.0, cogxels.getCogxelActivation(map.findIdentifier(a)));
        assertEquals(2.0, cogxels.getCogxelActivation(map.findIdentifier(b)));
        assertEquals(3.0, cogxels.getCogxelActivation(map.findIdentifier(c)));
        assertEquals(-1.0, cogxels.getCogxelActivation(map.findIdentifier(d)));
        assertEquals(-2.0, cogxels.getCogxelActivation(map.findIdentifier(e)));
        
    }
    
    /**
     * Test of setSemanticIdentifierMap method, of class gov.sandia.cognition.framework.learning.InputOutputPairCogxelConverter.
     */
    public void testSetSemanticIdentifierMap()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        SemanticLabel d = new DefaultSemanticLabel("d");
        SemanticLabel e = new DefaultSemanticLabel("e");
        
        CogxelConverter<Vector> inputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { a, b, c });
        CogxelConverter<Vector> outputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { d, e });
        
        CogxelInputOutputPairConverter<Vector, Vector> instance = 
            new CogxelInputOutputPairConverter<Vector, Vector>(
                inputConverter, outputConverter);
        assertNull(instance.getSemanticIdentifierMap());
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap(map);
        assertSame(instance.getSemanticIdentifierMap(), map);
        assertSame(instance.getInputConverter().getSemanticIdentifierMap(), map);
        assertSame(instance.getOutputConverter().getSemanticIdentifierMap(), map);
        
        instance.setSemanticIdentifierMap(null);
        assertNull(instance.getSemanticIdentifierMap());
        assertNull(instance.getInputConverter().getSemanticIdentifierMap());
        assertNull(instance.getOutputConverter().getSemanticIdentifierMap());
        
        instance.setInputConverter(null);
        instance.setOutputConverter(null);
        instance.setSemanticIdentifierMap(map);
        assertSame(instance.getSemanticIdentifierMap(), map);
    }
    
    /**
     * Test of getSemanticIdentifierMap method, of class gov.sandia.cognition.framework.learning.InputOutputPairCogxelConverter.
     */
    public void testGetSemanticIdentifierMap()
    {
        CogxelInputOutputPairConverter<Vector, Vector> instance = 
            new CogxelInputOutputPairConverter<Vector, Vector>();
        assertNull(instance.getSemanticIdentifierMap());
        
        SemanticIdentifierMap map = new DefaultSemanticIdentifierMap();
        instance.setSemanticIdentifierMap(map);
        assertSame(instance.getSemanticIdentifierMap(), map);
    }

    /**
     * Test of getInputConverter method, of class gov.sandia.cognition.framework.learning.InputOutputPairCogxelConverter.
     */
    public void testGetInputConverter()
    {
        CogxelInputOutputPairConverter<Vector, Vector> instance = 
            new CogxelInputOutputPairConverter<Vector, Vector>();
        assertNull(instance.getInputConverter());
        
        CogxelConverter<Vector> inputConverter = new CogxelVectorConverter();
        instance.setInputConverter(inputConverter);
        assertSame(instance.getInputConverter(), inputConverter);
    }

    /**
     * Test of setInputConverter method, of class gov.sandia.cognition.framework.learning.InputOutputPairCogxelConverter.
     */
    public void testSetInputConverter()
    {
        CogxelInputOutputPairConverter<Vector, Vector> instance = 
            new CogxelInputOutputPairConverter<Vector, Vector>();
        assertNull(instance.getInputConverter());
        
        CogxelConverter<Vector> inputConverter = new CogxelVectorConverter();
        instance.setInputConverter(inputConverter);
        assertSame(instance.getInputConverter(), inputConverter);
        
        instance.setInputConverter(null);
        assertNull(instance.getInputConverter());
    }

    /**
     * Test of getOutputConverter method, of class gov.sandia.cognition.framework.learning.InputOutputPairCogxelConverter.
     */
    public void testGetOutputConverter()
    {
        CogxelInputOutputPairConverter<Vector, Vector> instance = 
            new CogxelInputOutputPairConverter<Vector, Vector>();
        assertNull(instance.getOutputConverter());
        
        CogxelConverter<Vector> outputConverter = new CogxelVectorConverter();
        instance.setOutputConverter(outputConverter);
        assertSame(instance.getOutputConverter(), outputConverter);
    }

    /**
     * Test of setOutputConverter method, of class gov.sandia.cognition.framework.learning.InputOutputPairCogxelConverter.
     */
    public void testSetOutputConverter()
    {
        CogxelInputOutputPairConverter<Vector, Vector> instance = 
            new CogxelInputOutputPairConverter<Vector, Vector>();
        assertNull(instance.getOutputConverter());
        
        CogxelConverter<Vector> outputConverter = new CogxelVectorConverter();
        instance.setOutputConverter(outputConverter);
        assertSame(instance.getOutputConverter(), outputConverter);
        
        instance.setOutputConverter(null);
        assertNull(instance.getOutputConverter());
    }

    /**
     * Test of equals method, of class gov.sandia.cognition.framework.learning.InputOutputPairCogxelConverter.
     */
    public void testEquals()
    {
        CogxelInputOutputPairConverter<Vector, Vector> instance1 = 
            new CogxelInputOutputPairConverter<Vector, Vector>();
        
        assertEquals(instance1, instance1);
        assertEquals(instance1, instance1.clone());
        
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        SemanticLabel d = new DefaultSemanticLabel("d");
        SemanticLabel e = new DefaultSemanticLabel("e");
        
        CogxelConverter<Vector> inputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { a, b, c });
        CogxelConverter<Vector> outputConverter = new CogxelVectorConverter(
            new SemanticLabel[] { d, e });
        
        CogxelInputOutputPairConverter<Vector, Vector> instance2 = 
            new CogxelInputOutputPairConverter<Vector, Vector>(
                inputConverter, outputConverter);
        
        assertEquals(instance2, instance2);
        assertEquals(instance2, instance2.clone());
        assertFalse(instance1.equals(instance2));
        assertFalse(instance2.equals(instance1));
        assertFalse(instance1.equals(null));
        assertFalse(instance2.equals(null));
        assertFalse(instance1.equals("a"));
    }
}

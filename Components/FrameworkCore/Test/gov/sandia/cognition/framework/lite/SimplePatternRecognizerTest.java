/*
 * File:                SimplePatternRecognizerTest.java
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

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.CognitiveModuleState;
import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.framework.DefaultSemanticNetwork;
import gov.sandia.cognition.framework.SemanticNetwork;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import junit.framework.*;
import gov.sandia.cognition.framework.SemanticLabel;
import java.util.Collection;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     SimplePatternRecognizer
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class SimplePatternRecognizerTest
    extends TestCase
{
    /**
     * Creates a new instance of SimplePatternRecognizerTest.
     *
     * @param  testName The test name.
     */
    public SimplePatternRecognizerTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Tests the creation of a simple pattern recognizer.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testCreation()
    {
        DefaultSemanticNetwork testNetwork = new DefaultSemanticNetwork();
        
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        testNetwork.addNode(a);
        testNetwork.addNode(b);
        testNetwork.addNode(c);
        testNetwork.setAssociation(a, b, 1.0);
        
        SimplePatternRecognizer recognizer = 
            new SimplePatternRecognizer(testNetwork);
        
        assertEquals(3, recognizer.getInputDimensionality());
        assertEquals(3, recognizer.getOutputDimensionality());
        assertEquals(3, recognizer.createEmptyInputVector().getDimensionality());
    }
    
    /**
     * Tests the getNetwork method in SimplePatternRecognizer.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testGetNetwork()
    {
        DefaultSemanticNetwork testNetwork = new DefaultSemanticNetwork();
        
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        DefaultSemanticLabel d = new DefaultSemanticLabel("d");
        testNetwork.addNode(a);
        testNetwork.addNode(b);
        testNetwork.addNode(c);
        testNetwork.setAssociation(a, b, 1.0);
        
        SimplePatternRecognizer recognizer = 
            new SimplePatternRecognizer(testNetwork);
        
        SemanticNetwork recognizerNetwork = recognizer.getNetwork();
        
        assertEquals(3, recognizerNetwork.getNumNodes());
        assertTrue(recognizerNetwork.isNode(a));
        assertTrue(recognizerNetwork.isNode(b));
        assertTrue(recognizerNetwork.isNode(c));
        
        assertFalse(recognizerNetwork.isNode(d));
        testNetwork.addNode(d);
        assertFalse(recognizerNetwork.isNode(d));
        
        assertTrue(recognizerNetwork.getNodes().contains(a));
        assertTrue(recognizerNetwork.getNodes().contains(b));
        assertTrue(recognizerNetwork.getNodes().contains(c));
        assertTrue(recognizerNetwork.getOutLinks(a).contains(b));
        assertTrue(!recognizerNetwork.getOutLinks(a).contains(c));
        assertEquals(1.0, recognizerNetwork.getAssociation(a, b));
        assertEquals(0.0, recognizerNetwork.getAssociation(b, a));
    }
    
    /**
     * Tests the recognize method in SimplePatternRecognizer.
     *
     * If this test fails, contact Justin Basilico.
     */
    public void testRecognize()
    {
        DefaultSemanticNetwork testNetwork = new DefaultSemanticNetwork();
        
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        testNetwork.addNode(a);
        testNetwork.addNode(b);
        testNetwork.addNode(c);
        testNetwork.setAssociation(a, b, 1.0);
        
        SimplePatternRecognizer recognizer = 
            new SimplePatternRecognizer(testNetwork);
        
        CognitiveModuleState state = recognizer.initialState();
        Vector inputs = recognizer.createEmptyInputVector();
        inputs.setElement(0, 1.0);
        
        Vector outputs = recognizer.recognize(state, inputs);

        assertNotNull(outputs);
        assertEquals(3, outputs.getDimensionality());
        assertEquals(1.0, outputs.getElement(0));
        assertEquals(0.0, outputs.getElement(1));
        assertEquals(0.0, outputs.getElement(2));
        
        inputs = recognizer.createEmptyInputVector();
        outputs = recognizer.recognize(state, inputs);
        assertNotNull(outputs);
        assertEquals(3, outputs.getDimensionality());
        assertEquals(0.0, outputs.getElement(0));
        assertEquals(1.0, outputs.getElement(1));
        assertEquals(0.0, outputs.getElement(2));
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(SimplePatternRecognizerTest.class);
        
        return suite;
    }

    /**
     * Test of clone method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testClone()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.addNode(a);
        instance.addNode(b);
        instance.setAssociation(a, b, 1.0);
        instance.setAssociation(b, b, 0.5);
        
        SimplePatternRecognizer clone = instance.clone();
        
        assertNotNull(clone);
        assertNotSame(clone, instance);
        
        assertTrue(clone.isLabel(a));
        assertTrue(clone.isLabel(b));
        assertEquals(1.0, clone.getAssociation(a, b));
        assertEquals(0.5, clone.getAssociation(b, b));
        
        instance.setAssociation(a, b, 2.0);
        assertEquals(2.0, instance.getAssociation(a, b));
        assertEquals(1.0, clone.getAssociation(a, b));
        
        
        instance.addNode(c);
        assertTrue(instance.isLabel(c));
        assertFalse(clone.isLabel(c));
        
        instance.removeNode(b);
        assertFalse(instance.isLabel(b));
        assertTrue(clone.isLabel(b));
    }

    /**
     * Test of initialState method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testInitialState()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
        
        instance.addNode(new DefaultSemanticLabel("a"));
        instance.addNode(new DefaultSemanticLabel("b"));
        
        SimplePatternRecognizerState state = instance.initialState();
        assertNotNull(state);
        assertEquals(2, state.getStateVector().getDimensionality());
    }

    /**
     * Test of addNode method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testAddNode()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        
        assertFalse(instance.isLabel(a));
        instance.addNode(a);
        assertTrue(instance.isLabel(a));
        
        assertTrue(instance.isInputLabel(a));
        assertTrue(instance.isOutputLabel(a));
        
        assertTrue(instance.getAllLabels().contains(a));
    }

    /**
     * Test of removeNode method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testRemoveNode()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        instance.addNode(a);
        instance.addNode(b);
        instance.setAssociation(a, b, 1.0);
        instance.setAssociation(b, b, 0.5);
        
        assertTrue(instance.isLabel(b));
        instance.removeNode(b);
        assertFalse(instance.isLabel(b));
        assertFalse(instance.getAllLabels().contains(b));        
    }
    
    public void testGetAssociation()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.addNode(a);
        instance.addNode(b);
        
        assertEquals(0.0, instance.getAssociation(a, b));
        instance.setAssociation(a, b, 1.0);
        assertEquals(1.0, instance.getAssociation(a, b));
        
        assertEquals(0.0, instance.getAssociation(a, c));
    }

    /**
     * Test of setAssociation method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testSetAssociation()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        instance.addNode(a);
        instance.addNode(b);
        instance.setAssociation(a, b, 1.0);
        assertEquals(1.0, instance.getAssociation(a, b));
        assertEquals(0.0, instance.getAssociation(b, a));
        instance.setAssociation(a, b, 2.0);
        assertEquals(2.0, instance.getAssociation(a, b));
    }

    /**
     * Test of getIndex method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testGetIndex()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        instance.addNode(a);
        instance.addNode(b);
        
        assertEquals(0, instance.getIndex(a));
        assertEquals(1, instance.getIndex(b));
    }

    /**
     * Test of isLabel method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testIsLabel()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.addNode(a);
        instance.addNode(b);
        
        assertTrue(instance.isLabel(a));
        assertTrue(instance.isLabel(b));
        assertFalse(instance.isLabel(c));
        
        instance.removeNode(b);
        assertFalse(instance.isLabel(b));
    }

    /**
     * Test of isInputLabel method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testIsInputLabel()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.addNode(a);
        instance.addNode(b);
        
        assertTrue(instance.isInputLabel(a));
        assertTrue(instance.isInputLabel(b));
        assertFalse(instance.isInputLabel(c));
        
        instance.removeNode(b);
        assertFalse(instance.isInputLabel(b));
    }

    /**
     * Test of isOutputLabel method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testIsOutputLabel()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.addNode(a);
        instance.addNode(b);
        
        assertTrue(instance.isOutputLabel(a));
        assertTrue(instance.isOutputLabel(b));
        assertFalse(instance.isOutputLabel(c));
        
        instance.removeNode(b);
        assertFalse(instance.isOutputLabel(b));
    }

    /**
     * Test of getAllLabels method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testGetAllLabels()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.addNode(a);
        instance.addNode(b);
        
        Collection<SemanticLabel> result = instance.getAllLabels();
        
        assertTrue(result.contains(a));
        assertTrue(result.contains(b));
        assertFalse(result.contains(c));
    }

    /**
     * Test of trySetInputLabel method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testTrySetInputLabel()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        instance.addNode(a);
        
        assertTrue(instance.isInputLabel(a));
        assertTrue(instance.trySetInputLabel(a, true));
        assertTrue(instance.isInputLabel(a));
        
        assertFalse(instance.trySetInputLabel(a, false));
        assertTrue(instance.isInputLabel(a));
        
        assertFalse(instance.trySetInputLabel(b, true));
    }

    /**
     * Test of trySetOutputLabel method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testTrySetOutputLabel()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        
        instance.addNode(a);
        
        assertTrue(instance.isOutputLabel(a));
        assertTrue(instance.trySetOutputLabel(a, true));
        assertTrue(instance.isOutputLabel(a));
        
        assertFalse(instance.trySetOutputLabel(a, false));
        assertTrue(instance.isOutputLabel(a));
        
        assertFalse(instance.trySetOutputLabel(b, true));
    }

    /**
     * Test of buildNodeToIDMap method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testBuildNodeToIDMap()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
        
        instance.buildNodeToIDMap();
        
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        
        instance.addNode(a);
        
        instance.buildNodeToIDMap();
    }

    /**
     * Test of createEmptyInputVector method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testCreateEmptyInputVector()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        Vector result = instance.createEmptyInputVector();
        assertNotNull(result);
        assertEquals(0, result.getDimensionality());
        assertEquals(VectorFactory.getDefault().createVector(0), result);
        
        instance.addNode(new DefaultSemanticLabel("a"));
        instance.addNode(new DefaultSemanticLabel("b"));
        
        result = instance.createEmptyInputVector();
        assertNotNull(result);
        assertEquals(2, result.getDimensionality());
        assertEquals(new Vector2(), result);
    }

    /**
     * Test of getInputDimensionality method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testGetInputDimensionality()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        assertEquals(0, instance.getInputDimensionality());
        
        instance.addNode(new DefaultSemanticLabel("a"));
        instance.addNode(new DefaultSemanticLabel("b"));
        
        assertEquals(2, instance.getInputDimensionality());
        int result = instance.getInputDimensionality();
    }

    /**
     * Test of getOutputDimensionality method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testGetOutputDimensionality()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        assertEquals(0, instance.getOutputDimensionality());
        
        instance.addNode(new DefaultSemanticLabel("a"));
        instance.addNode(new DefaultSemanticLabel("b"));
        
        assertEquals(2, instance.getOutputDimensionality());
        int result = instance.getOutputDimensionality();
    }

    /**
     * Test of getInputLabels method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testGetInputLabels()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.addNode(a);
        instance.addNode(b);
        
        Collection<SemanticLabel> result = instance.getInputLabels();
        
        assertTrue(result.contains(a));
        assertTrue(result.contains(b));
        assertFalse(result.contains(c));
    }

    /**
     * Test of getOutputLabels method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizer.
     */
    public void testGetOutputLabels()
    {
        SimplePatternRecognizer instance = new SimplePatternRecognizer();
     
        DefaultSemanticLabel a = new DefaultSemanticLabel("a");
        DefaultSemanticLabel b = new DefaultSemanticLabel("b");
        DefaultSemanticLabel c = new DefaultSemanticLabel("c");
        
        instance.addNode(a);
        instance.addNode(b);
        
        Collection<SemanticLabel> result = instance.getOutputLabels();
        
        assertTrue(result.contains(a));
        assertTrue(result.contains(b));
        assertFalse(result.contains(c));
    }
}

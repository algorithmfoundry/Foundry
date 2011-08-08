/*
 * File:                SimplePatternRecognizerStateTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Framework Lite
 *
 * Copyright May 19, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.framework.lite;

import gov.sandia.cognition.framework.DefaultSemanticLabel;
import gov.sandia.cognition.math.matrix.VectorFactory;
import junit.framework.*;
import gov.sandia.cognition.framework.SemanticLabel;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     SimplePatternRecognizerState
 *
 * @author Justin Basilico
 * @since 1.0
 */
public class SimplePatternRecognizerStateTest
    extends TestCase
{
    public SimplePatternRecognizerStateTest(
        String testName)
    {
        super(testName);
    }

    public static Test suite()
    {
        TestSuite suite = new TestSuite(SimplePatternRecognizerStateTest.class);
        
        return suite;
    }

    /**
     * Test of clone method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizerState.
     */
    public void testClone()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        ArrayList<SemanticLabel> givenLabels = new ArrayList<SemanticLabel>();
        givenLabels.add(a);
        givenLabels.add(b);
        
        SimplePatternRecognizerState instance = 
            new SimplePatternRecognizerState(givenLabels);
        
        SimplePatternRecognizerState clone = instance.clone();
        assertNotNull(clone);
        assertNotSame(instance, clone);
        assertNotSame(instance.getLabels(), clone.getLabels());
        assertNotSame(instance.getStateVector(), clone.getStateVector());
    }

    /**
     * Test of getLabels method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizerState.
     */
    public void testGetLabels()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        ArrayList<SemanticLabel> givenLabels = new ArrayList<SemanticLabel>();
        givenLabels.add(a);
        givenLabels.add(b);
        
        SimplePatternRecognizerState instance = 
            new SimplePatternRecognizerState(givenLabels);
        
        // This is to make sure that the class actually uses what it is given.
        List<SemanticLabel> labels = instance.getLabels();
        assertNotNull(labels);
        assertEquals(2, labels.size());
        assertEquals(a, labels.get(0));
        assertEquals(b, labels.get(1));
        assertSame(labels, instance.getLabels());
        
        // This test is to make sure that the getLabels method keeps its own
        // internal copy.
        givenLabels.add(c);
        labels = instance.getLabels();
        assertEquals(2, labels.size());
        assertFalse(labels.contains(c));
    }

    /**
     * Test of getStateVector method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizerState.
     */
    public void testGetStateVector()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        ArrayList<SemanticLabel> givenLabels = new ArrayList<SemanticLabel>();
        givenLabels.add(a);
        givenLabels.add(b);
        
        SimplePatternRecognizerState instance = 
            new SimplePatternRecognizerState(givenLabels);
        
        Vector stateVector = instance.getStateVector();
        assertNotNull(stateVector);
        assertEquals(2, stateVector.getDimensionality());
        assertEquals(0.0, stateVector.getElement(0));
        assertEquals(0.0, stateVector.getElement(1));
        
        stateVector.setElement(0, 1.0);
        instance = 
            new SimplePatternRecognizerState(givenLabels, stateVector, false);
        assertSame(stateVector, instance.getStateVector());
    }

    /**
     * Test of setLabels method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizerState.
     */
    public void testSetLabels()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        SemanticLabel c = new DefaultSemanticLabel("c");
        ArrayList<SemanticLabel> givenLabels = new ArrayList<SemanticLabel>();
        givenLabels.add(a);
        
        SimplePatternRecognizerState instance = 
            new SimplePatternRecognizerState(givenLabels);
        List<SemanticLabel> labels = instance.getLabels();
        assertNotNull(labels);
        assertEquals(1, labels.size());
        
        
        givenLabels.add(b);
        instance.setLabels(givenLabels);
        
        // This is to make sure that the class actually uses what it is given.
        labels = instance.getLabels();
        assertNotNull(labels);
        assertEquals(2, labels.size());
        assertSame(labels, instance.getLabels());
        
        // This test is to make sure that the setLabels method makes its own
        // internal copy.
        givenLabels.add(c);
        labels = instance.getLabels();
        assertEquals(2, labels.size());
        assertFalse(labels.contains(c));
    }

    /**
     * Test of setStateVector method, of class gov.sandia.isrc.cognition.framework.lite.SimplePatternRecognizerState.
     */
    public void testSetStateVector()
    {
        SemanticLabel a = new DefaultSemanticLabel("a");
        SemanticLabel b = new DefaultSemanticLabel("b");
        ArrayList<SemanticLabel> givenLabels = new ArrayList<SemanticLabel>();
        givenLabels.add(a);
        givenLabels.add(b);
        
        SimplePatternRecognizerState instance = 
            new SimplePatternRecognizerState(givenLabels);
        
        Vector vector = VectorFactory.getDefault().createVector(2);
        instance.setStateVector(vector);
        assertSame(vector, instance.getStateVector());
    }
}

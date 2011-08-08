/*
 * File:                CategorizationTreeLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 16, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     CategorizationTreeLearner
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class CategorizationTreeLearnerTest
    extends TestCase
{
    public CategorizationTreeLearnerTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        CategorizationTreeLearner<Vector3, String> instance = 
            new CategorizationTreeLearner<Vector3, String>();
        assertNull(instance.getDeciderLearner());
        
        VectorThresholdInformationGainLearner<String> deciderLearner = 
            new VectorThresholdInformationGainLearner<String>();
        instance = new CategorizationTreeLearner<Vector3, String>(deciderLearner);
        assertSame(deciderLearner, instance.getDeciderLearner());
    }
    
    /**
     * Test of learn method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeLearner.
     */
    public void testLearn()
    {
        
        VectorThresholdInformationGainLearner<String> deciderLearner = 
            new VectorThresholdInformationGainLearner<String>();
        CategorizationTreeLearner<Vector3, String> instance = 
            new CategorizationTreeLearner<Vector3, String>(deciderLearner);
instance.setDeciderLearner(new VectorThresholdHellingerDistanceLearner<String>());

        CategorizationTree<Vector3, String> result = instance.learn(null);
        assertNull(result);
        
        ArrayList<InputOutputPair<Vector3, String>> data = 
            new ArrayList<InputOutputPair<Vector3, String>>();
        
        result = instance.learn(data);
        assertNull(result.getRootNode());
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 4.0, 2.0), "a"));
        
        result = instance.learn(data);
        assertNotNull(result.getRootNode());
        assertTrue(result.getRootNode().isLeaf());
        assertEquals("a", result.evaluate(data.get(0).getInput()));
        assertEquals(1, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 2.0), "a"));
        
        result = instance.learn(data);
        assertNotNull(result.getRootNode());
        assertTrue(result.getRootNode().isLeaf());
        assertEquals("a", result.evaluate(data.get(0).getInput()));
        assertEquals("a", result.evaluate(data.get(1).getInput()));
        assertEquals(1, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 2.0, 3.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 4.0, 4.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 3.0, 2.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 0.0, 2.0), "a"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 5.0, 2.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 7.0, 2.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 8.0, 2.0), "b"));
        
        
        result = instance.learn(data);
        assertNotNull(result.getRootNode());
        assertFalse(result.getRootNode().isLeaf());
        for ( InputOutputPair<Vector3, String> example : data )
        {
            assertEquals(example.getOutput(), result.evaluate(example.getInput()));
        }
        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));
        assertTrue(result.getCategories().contains("b"));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 1.0), "a"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 1.0), "b"));
        
        result = instance.learn(data);
        assertEquals("a", result.evaluate(new Vector3(1.0, 1.0, 1.0)));
        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));
        assertTrue(result.getCategories().contains("b"));
        
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 1.0), "b"));
        
        result = instance.learn(data);
        assertEquals("b", result.evaluate(new Vector3(1.0, 1.0, 1.0)));
        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));
        assertTrue(result.getCategories().contains("b"));
        
        data.clear();
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 1.0), "a"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 1.0), "b"));
        result = instance.learn(data);
        assertEquals("a", result.evaluate(new Vector3(1.0, 1.0, 1.0)));
        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));
        assertTrue(result.getCategories().contains("b"));
        
        data.clear();
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(0.0, 0.0, 0.0), "a"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 0.0, 0.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(0.0, 1.0, 0.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 0.0), "a"));
        result = instance.learn(data);
        for ( InputOutputPair<Vector3, String> example : data )
        {
            assertEquals(example.getOutput(), result.evaluate(example.getInput()));
        }
        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains("a"));
        assertTrue(result.getCategories().contains("b"));
    }

    /**
     * Test of areAllOutputsEqual method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeLearner.
     */
    public void testAreAllOutputsEqual()
    {
        CategorizationTreeLearner<Vector3, String> instance = 
            new CategorizationTreeLearner<Vector3, String>();
        
        LinkedList<InputOutputPair<Vector3, String>> data = 
            new LinkedList<InputOutputPair<Vector3, String>>();
        assertTrue(instance.areAllOutputsEqual(data));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 0.0, 0.0), "a"));
        assertTrue(instance.areAllOutputsEqual(data));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(0.0, 1.0, 0.0), "a"));
        assertTrue(instance.areAllOutputsEqual(data));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(0.0, 0.0, 1.0), "a"));
        assertTrue(instance.areAllOutputsEqual(data));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 0.0, 1.0), "b"));
        assertFalse(instance.areAllOutputsEqual(data));
    }

    /**
     * Test of getOutputCounts method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeLearner.
     */
    public void testGetOutputCounts()
    {
        MapBasedDataHistogram<String> result = 
            CategorizationTreeLearner.getOutputCounts(null);
        assertEquals(0, result.getTotalCount());
        
        LinkedList<InputOutputPair<Vector3, String>> data = 
            new LinkedList<InputOutputPair<Vector3, String>>();
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(), "a"));
        result = CategorizationTreeLearner.getOutputCounts(data);
        assertEquals(1, result.getTotalCount());
        assertEquals(1, result.getCount("a"));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(), "b"));
        result = CategorizationTreeLearner.getOutputCounts(data);
        assertEquals(2, result.getTotalCount());
        assertEquals(1, result.getCount("a"));
        assertEquals(1, result.getCount("b"));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(), "b"));
        result = CategorizationTreeLearner.getOutputCounts(data);
        assertEquals(3, result.getTotalCount());
        assertEquals(1, result.getCount("a"));
        assertEquals(2, result.getCount("b"));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(), "c"));
        result = CategorizationTreeLearner.getOutputCounts(data);
        assertEquals(4, result.getTotalCount());
        assertEquals(1, result.getCount("a"));
        assertEquals(2, result.getCount("b"));
        assertEquals(1, result.getCount("c"));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(), "a"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(), "c"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(), "a"));
        result = CategorizationTreeLearner.getOutputCounts(data);
        assertEquals(9, result.getTotalCount());
        assertEquals(3, result.getCount("a"));
        assertEquals(4, result.getCount("b"));
        assertEquals(2, result.getCount("c"));
    }

    /**
     * Test of splitData method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeLearner.
     */
    public void testSplitData()
    {
        CategorizationTreeLearner<Vector3, String> instance = 
            new CategorizationTreeLearner<Vector3, String>();
        
        ArrayList<InputOutputPair<Vector3, String>> data = 
            new ArrayList<InputOutputPair<Vector3, String>>();
        
        VectorElementThresholdCategorizer decider = 
            new VectorElementThresholdCategorizer(1, 2.5);
        
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 4.0, 2.0), "a"));
        Map<Boolean, LinkedList<InputOutputPair<? extends Vector3, String>>> 
            result = instance.splitData(data, decider);
        assertEquals(1, result.size());
        assertEquals(1, result.get(true).size());
        assertTrue(result.get(true).contains(data.get(0)));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 1.0, 2.0), "a"));
        result = instance.splitData(data, decider);
        assertEquals(2, result.size());
        assertEquals(1, result.get(true).size());
        assertEquals(1, result.get(false).size());
        assertTrue(result.get(true).contains(data.get(0)));
        assertTrue(result.get(false).contains(data.get(1)));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 2.0, 3.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 4.0, 4.0), "b"));
        result = instance.splitData(data, decider);
        assertEquals(2, result.size());
        assertEquals(2, result.get(true).size());
        assertEquals(2, result.get(false).size());
        assertTrue(result.get(true).contains(data.get(0)));
        assertTrue(result.get(false).contains(data.get(1)));
        assertTrue(result.get(false).contains(data.get(2)));
        assertTrue(result.get(true).contains(data.get(3)));
        
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 3.0, 2.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 0.0, 2.0), "a"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 5.0, 2.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 7.0, 2.0), "b"));
        data.add(new DefaultInputOutputPair<Vector3, String>(new Vector3(1.0, 8.0, 2.0), "b"));
        result = instance.splitData(data, decider);
        assertEquals(2, result.size());
        assertEquals(6, result.get(true).size());
        assertEquals(3, result.get(false).size());
        assertTrue(result.get(true).contains(data.get(0)));
        assertTrue(result.get(false).contains(data.get(1)));
        assertTrue(result.get(false).contains(data.get(2)));
        assertTrue(result.get(true).contains(data.get(3)));
        assertTrue(result.get(true).contains(data.get(4)));
        assertTrue(result.get(false).contains(data.get(5)));
        assertTrue(result.get(true).contains(data.get(6)));
        assertTrue(result.get(true).contains(data.get(7)));
        assertTrue(result.get(true).contains(data.get(8)));
    }

    /**
     * Test of getDeciderLearner method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeLearner.
     */
    public void testGetDeciderLearner()
    {
        this.testSetDeciderLearner();
    }

    /**
     * Test of setDeciderLearner method, of class gov.sandia.cognition.learning.algorithm.tree.CategorizationTreeLearner.
     */
    public void testSetDeciderLearner()
    {
        CategorizationTreeLearner<Vector3, String> instance = 
            new CategorizationTreeLearner<Vector3, String>();
        assertNull(instance.getDeciderLearner());
        
        VectorThresholdInformationGainLearner<String> deciderLearner = 
            new VectorThresholdInformationGainLearner<String>();
        instance.setDeciderLearner(deciderLearner);
        assertSame(deciderLearner, instance.getDeciderLearner());
        
        instance.setDeciderLearner(null);
        assertNull(instance.getDeciderLearner());
    }

    public void testGetLeafCountThreshold()
    {
        this.testSetLeafCountThreshold();
    }

    public void testSetLeafCountThreshold()
    {
        CategorizationTreeLearner<Vector3, String> instance =
            new CategorizationTreeLearner<Vector3, String>();

        instance.getLeafCountThreshold();
    }
}

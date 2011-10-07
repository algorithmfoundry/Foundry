/*
 * File:                RegressionTreeLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.algorithm.regression.KernelBasedIterativeRegression;
import gov.sandia.cognition.learning.algorithm.regression.LinearRegression;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.CloneableSerializable;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes: RegressionTreeLearner
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class RegressionTreeLearnerTest
    extends TestCase
{
    public RegressionTreeLearnerTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstants()
    {
        assertEquals(4, RegressionTreeLearner.DEFAULT_LEAF_COUNT_THRESHOLD);
    }
    
    public void testConstructors()
    {
        RegressionTreeLearner<Vectorizable> instance = 
            new RegressionTreeLearner<Vectorizable>();
        assertNull(instance.getDeciderLearner());
        assertNull(instance.getRegressionLearner());
        assertEquals(RegressionTreeLearner.DEFAULT_LEAF_COUNT_THRESHOLD, 
            instance.getLeafCountThreshold());
        
        VectorThresholdVarianceLearner deciderLearner =
            new VectorThresholdVarianceLearner();
        KernelBasedIterativeRegression<Vectorizable> regressionLearner =
            new KernelBasedIterativeRegression<Vectorizable>();
        instance = new RegressionTreeLearner<Vectorizable>(
            deciderLearner, regressionLearner);
        
        assertSame(deciderLearner, instance.getDeciderLearner());
        assertSame(regressionLearner, instance.getRegressionLearner());
        assertEquals(RegressionTreeLearner.DEFAULT_LEAF_COUNT_THRESHOLD, 
            instance.getLeafCountThreshold());
        
        int leafCountThreshold = 
            RegressionTreeLearner.DEFAULT_LEAF_COUNT_THRESHOLD + 1;
        int maxDepth = 10;
        instance = new RegressionTreeLearner<Vectorizable>(
            deciderLearner, regressionLearner, leafCountThreshold, maxDepth);
        
        assertSame(deciderLearner, instance.getDeciderLearner());
        assertSame(regressionLearner, instance.getRegressionLearner());
        assertEquals(leafCountThreshold, instance.getLeafCountThreshold());
        assertEquals(maxDepth, instance.getMaxDepth());
    }

    /**
     * Tests of clone
     */
    public void testClone()
    {
        System.out.println( "Clone" );
        RegressionTreeLearner instance = new RegressionTreeLearner();

        CloneableSerializable clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.algorithm.tree.RegressionTreeLearner.
     */
    public void testLearn()
    {
        VectorThresholdVarianceLearner deciderLearner = 
            new VectorThresholdVarianceLearner();

        LinearRegression regressionLearner =
            new LinearRegression();

        RegressionTreeLearner<Vectorizable> instance = 
            new RegressionTreeLearner<Vectorizable>(deciderLearner,
                regressionLearner);
        
        double epsilon = 0.001;
        
        RegressionTree<Vectorizable> result = instance.learn(null);
        assertNull(result);
        
        ArrayList<InputOutputPair<Vector3, Double>> data = 
            new ArrayList<InputOutputPair<Vector3, Double>>();
        
        result = instance.learn(data);
        assertNull(result.getRootNode());
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 3.0, 2.0), 14.0));
        
        result = instance.learn(data);
        assertNotNull(result.getRootNode());
        assertTrue(result.getRootNode().isLeaf());
        assertEquals(14.0, result.evaluate(data.get(0).getInput()), epsilon);
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 1.0, 2.0), 14.1));
        
        result = instance.learn(data);
        assertNotNull(result.getRootNode());
        assertTrue(result.getRootNode().isLeaf());
        assertEquals(14.0, result.evaluate(data.get(0).getInput()), epsilon);
        assertEquals(14.1, result.evaluate(data.get(1).getInput()), epsilon);
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 10.0, 3.0), 1.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 9.0, 4.0), 0.5));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 8.0, 2.0), 0.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 0.0, 2.0), 14.2));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 7.0, 2.0), -0.5));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 6.0, 2.0), -1.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 5.0, 2.0), -1.5));
        
        result = instance.learn(data);
        assertNotNull(result.getRootNode());
        assertFalse(result.getRootNode().isLeaf());
        for ( InputOutputPair<Vector3, Double> example : data )
        {
            assertEquals(example.getOutput(), result.evaluate(example.getInput()), 0.1);
        }
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 1.0, 100.0), 4.6));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 1.0, 100.0), 4.8));
        
        result = instance.learn(data);
        
        assertEquals(4.7, result.evaluate(new Vector3(1.0, 1.0, 100.0)), 0.1);
        
        
        data.clear();
        instance.setLeafCountThreshold(1);
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 1.0, 100.0), 4.6));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 1.0, 100.0), 4.8));
        result = instance.learn(data);        
        assertEquals(4.7, result.evaluate(new Vector3(1.0, 1.0, 100.0)), 0.1);
        
        data.clear();
        // This is XOR.
        instance.setLeafCountThreshold(1);
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(0.0, 0.0, 0.0), 1.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 0.0, 0.0), -1.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(0.0, 1.0, 0.0), -1.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 1.0, 0.0), 1.0));
        result = instance.learn(data);
        for ( InputOutputPair<Vector3, Double> example : data )
        {
            assertEquals(example.getOutput(), result.evaluate(example.getInput()), 0.1);
        }
    }

    /**
     * Test of getRegressionLearner method, of class gov.sandia.cognition.learning.algorithm.tree.RegressionTreeLearner.
     */
    public void testGetRegressionLearner()
    {
        this.testSetRegressionLearner();
    }

    /**
     * Test of setRegressionLearner method, of class gov.sandia.cognition.learning.algorithm.tree.RegressionTreeLearner.
     */
    public void testSetRegressionLearner()
    {
        RegressionTreeLearner<Vectorizable> instance = 
            new RegressionTreeLearner<Vectorizable>();
        assertNull(instance.getRegressionLearner());
        
        
        KernelBasedIterativeRegression<Vectorizable> regressionLearner =
            new KernelBasedIterativeRegression<Vectorizable>();
        instance.setRegressionLearner(regressionLearner);
        assertSame(regressionLearner, instance.getRegressionLearner());
        
        instance.setRegressionLearner(null);
        assertNull(instance.getRegressionLearner());
    }

    /**
     * Test of getLeafCountThreshold method, of class gov.sandia.cognition.learning.algorithm.tree.RegressionTreeLearner.
     */
    public void testGetLeafCountThreshold()
    {
        this.testSetLeafCountThreshold();
    }

    /**
     * Test of setLeafCountThreshold method, of class gov.sandia.cognition.learning.algorithm.tree.RegressionTreeLearner.
     */
    public void testSetLeafCountThreshold()
    {
        RegressionTreeLearner<Vector3> instance = 
            new RegressionTreeLearner<Vector3>();
        assertEquals(RegressionTreeLearner.DEFAULT_LEAF_COUNT_THRESHOLD, 
            instance.getLeafCountThreshold());
        
        int leafCountThreshold = 
            RegressionTreeLearner.DEFAULT_LEAF_COUNT_THRESHOLD + 1;
        instance.setLeafCountThreshold(leafCountThreshold);
        assertEquals(leafCountThreshold, instance.getLeafCountThreshold());
        
        leafCountThreshold = 1;
        instance.setLeafCountThreshold(leafCountThreshold);
        assertEquals(leafCountThreshold, instance.getLeafCountThreshold());
        
        leafCountThreshold = 0;
        instance.setLeafCountThreshold(leafCountThreshold);
        assertEquals(leafCountThreshold, instance.getLeafCountThreshold());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setLeafCountThreshold(-1);
        }
        catch ( IllegalArgumentException e )
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }
}

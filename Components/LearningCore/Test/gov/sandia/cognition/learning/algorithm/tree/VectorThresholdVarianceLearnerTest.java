/*
 * File:                VectorThresholdVarianceLearnerTest.java
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
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import junit.framework.*;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.DefaultPair;
import java.util.LinkedList;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     VectorThresholdVarianceLearner
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class VectorThresholdVarianceLearnerTest
    extends TestCase
{
    public VectorThresholdVarianceLearnerTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Test of constructors of class VectorThresholdVarianceLearner.
     */
    public void testConstructors()
    {
        int minSplitSize = VectorThresholdVarianceLearner.DEFAULT_MIN_SPLIT_SIZE;
        int[] dimensionsToConsider = null;
        VectorThresholdVarianceLearner instance = new VectorThresholdVarianceLearner();
        assertEquals(minSplitSize, instance.minSplitSize);
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());
        
        minSplitSize = 5;
        instance = new VectorThresholdVarianceLearner(minSplitSize);
        assertEquals(minSplitSize, instance.minSplitSize);
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());
        
        minSplitSize = 11;
        dimensionsToConsider = new int[] { 3, 4 };
        instance = new VectorThresholdVarianceLearner(minSplitSize, dimensionsToConsider);
        assertEquals(minSplitSize, instance.minSplitSize);
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());
    }

    /**
     * Test of learn method, of class gov.sandia.cognition.learning.algorithm.tree.VectorThresholdVarianceLearner.
     */
    public void testLearn()
    {
        VectorThresholdVarianceLearner instance = 
            new VectorThresholdVarianceLearner();
        
        VectorElementThresholdCategorizer result = instance.learn(null);
        assertNull(result);
        
        LinkedList<InputOutputPair<Vector3, Double>> data = 
            new LinkedList<InputOutputPair<Vector3, Double>>();
        result = instance.learn(data);
        assertNull(result);
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 4.0, 2.0), 4.0));
        result = instance.learn(data);
        assertNull(result);
        
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 4.0, 2.0), 4.0));
        result = instance.learn(data);
        assertNull(result);
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 1.0, 2.0), 4.0));
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(1, result.getIndex());
        assertEquals(2.5, result.getThreshold());
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 2.0, 3.0), 1.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 4.0, 4.0), 1.5));
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(2, result.getIndex());
        assertEquals(2.5, result.getThreshold());
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 3.0, 5.0), 0.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 0.0, 2.0), 4.5));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 5.0, 2.0), 0.5));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 7.0, 2.0), 2.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 8.0, 2.0), 1.5));
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(1, result.getIndex());
        assertEquals(1.5, result.getThreshold());
        
        // Try to only use some dimensions.
        instance.setDimensionsToConsider(0, 2);
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(2, result.getIndex());
        assertEquals(2.5, result.getThreshold());
    }

    /**
     * Test of computeBestGainThreshold method, of class gov.sandia.cognition.learning.algorithm.tree.VectorThresholdVarianceLearner.
     */
    public void testComputeBestGainThreshold()
    {
        VectorThresholdVarianceLearner instance = 
            new VectorThresholdVarianceLearner();
        
        double baseVariance = 0.0;
        DefaultPair<Double, Double> result = null;
        
        LinkedList<InputOutputPair<Vector3, Double>> data = 
            new LinkedList<InputOutputPair<Vector3, Double>>();
        baseVariance = DatasetUtil.computeOutputVariance(data);
        result = instance.computeBestGainThreshold(data, 0, baseVariance);
        assertNull(result);
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 4.0, 2.0), 4.0));
        baseVariance = DatasetUtil.computeOutputVariance(data);
        result = instance.computeBestGainThreshold(data, 0,  baseVariance);
        assertNull(result);
        result = instance.computeBestGainThreshold(data, 1,  baseVariance);
        assertNull(result);
        result = instance.computeBestGainThreshold(data, 2,  baseVariance);
        assertNull(result);
        
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 1.0, 2.0), 4.0));
        baseVariance = DatasetUtil.computeOutputVariance(data);
        result = instance.computeBestGainThreshold(data, 0,  baseVariance);
        assertNull(result);
        result = instance.computeBestGainThreshold(data, 1,  baseVariance);
        assertEquals(0.0, result.getFirst());
        assertEquals(2.5, result.getSecond());
        result = instance.computeBestGainThreshold(data, 2,  baseVariance);
        assertNull(result);
        
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 2.0, 3.0), 1.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 4.0, 4.0), 0.5));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 3.0, 5.0), 0.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 0.0, 2.0), 4.5));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 5.0, 2.0), 1.5));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 7.0, 2.0), 2.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 8.0, 2.0), 1.5));
        
        baseVariance = DatasetUtil.computeOutputVariance(data);
        result = instance.computeBestGainThreshold(data, 0,  baseVariance);
        assertNull(result);
        result = instance.computeBestGainThreshold(data, 1,  baseVariance);
        assertEquals(1.307, result.getFirst(), 0.001);
        assertEquals(1.5, result.getSecond());
        result = instance.computeBestGainThreshold(data, 2,  baseVariance);
        assertEquals(1.297, result.getFirst(), 0.001);
        assertEquals(2.5, result.getSecond());
    }
    
    public void testGetDimensionsToConsider()
    {
        this.testSetDimensionsToConsider();
    }
    
    public void testSetDimensionsToConsider()
    {
        int[] dimensionsToConsider = null;
        VectorThresholdVarianceLearner instance = new VectorThresholdVarianceLearner();
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());
        
        dimensionsToConsider = new int[] {1,2,5};
        instance.setDimensionsToConsider(dimensionsToConsider);
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());

        dimensionsToConsider = new int[] {0, 9, 12};
        instance.setDimensionsToConsider(dimensionsToConsider);
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());
        
        dimensionsToConsider = null;
        instance.setDimensionsToConsider(dimensionsToConsider);
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider()); 
    }
    
    /**
     * Test of learn method with childCountThreshold.
     */
    public void testLearnChildThreshold()
    {
        
        VectorElementThresholdCategorizer result;
        
        LinkedList<InputOutputPair<Vector3, Double>> data = 
            new LinkedList<InputOutputPair<Vector3, Double>>();
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 4.0, 2.0), 10.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 3.0, 2.0), 2.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 1.0, 2.0), 3.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 1.5, 3.0), 1.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 3.0, 4.0), 2.5));

        VectorThresholdVarianceLearner instance = 
            new VectorThresholdVarianceLearner();
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(1, result.getIndex());
        assertEquals(3.5, result.getThreshold());
        
        instance = new VectorThresholdVarianceLearner(2);
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(2, result.getIndex());
        assertEquals(2.5, result.getThreshold());

        instance = new VectorThresholdVarianceLearner(3);
        result = instance.learn(data);
        assertNull(result);

        // Now create a dataset that cannot possibly be split into two children of at least two members
        data = new LinkedList<InputOutputPair<Vector3, Double>>();
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 1.0, 1.0), 2.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 2.0, 2.0), 2.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 2.0, 2.0), 3.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 2.0, 2.0), 4.0));
        data.add(new DefaultInputOutputPair<Vector3, Double>(new Vector3(1.0, 3.0, 2.0), 5.0));    

        instance = new VectorThresholdVarianceLearner(1);
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(1, result.getIndex());
        assertEquals(2.5, result.getThreshold());

        instance = new VectorThresholdVarianceLearner(2);
        result = instance.learn(data);
        assertNull(result);

        // Setting childLeafCount to zero should be overridden inside so it becomes 1.
        boolean exceptionThrown = false;
        try
        {
            instance = new VectorThresholdVarianceLearner(0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }
}
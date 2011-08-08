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
}

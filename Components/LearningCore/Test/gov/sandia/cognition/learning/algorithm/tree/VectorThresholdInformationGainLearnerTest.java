/*
 * File:                VectorThresholdInformationGainLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 12, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.util.DefaultPair;
import java.util.LinkedList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 *     VectorThresholdInformationGainLearner
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class VectorThresholdInformationGainLearnerTest
    extends TestCase
{
    public VectorThresholdInformationGainLearnerTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class VectorThresholdInformationGainLearner.
     */
    public void testConstructors()
    {
        VectorThresholdInformationGainLearner<Boolean> instance =
            new VectorThresholdInformationGainLearner<Boolean>();
        assertNotNull(instance);
    }
    
    /**
     * Test of learn method, of class VectorThresholdInformationGainLearner.
     */
    public void testLearn()
    {
        VectorThresholdInformationGainLearner<Boolean> instance = 
            new VectorThresholdInformationGainLearner<Boolean>();
        
        VectorElementThresholdCategorizer result = instance.learn(null);
        assertNull(result);
        
        LinkedList<InputOutputPair<Vector3, Boolean>> data = 
            new LinkedList<InputOutputPair<Vector3, Boolean>>();
        result = instance.learn(data);
        assertNull(result);
        
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 4.0, 2.0), true));
        result = instance.learn(data);
        assertNull(result);
        
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 4.0, 2.0), true));
        result = instance.learn(data);
        assertNull(result);
        
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 1.0, 2.0), true));
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(1, result.getIndex());
        assertEquals(2.5, result.getThreshold());
        
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 2.0, 3.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 4.0, 4.0), false));
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(2, result.getIndex());
        assertEquals(2.5, result.getThreshold());
        
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 3.0, 2.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 0.0, 2.0), true));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 5.0, 2.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 7.0, 2.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 8.0, 2.0), false));
        result = instance.learn(data);
        assertNotNull(result);
        assertEquals(1, result.getIndex());
        assertEquals(1.5, result.getThreshold());
    }

    /**
     * Test of computeBestGainAndThreshold method, of class VectorThresholdInformationGainLearner.
     */
    public void testComputeBestThreshold()
    {
        VectorThresholdInformationGainLearner<Boolean> instance = 
            new VectorThresholdInformationGainLearner<Boolean>();
        
        MapBasedDataHistogram<Boolean> baseCounts = null;
        DefaultPair<Double, Double> result = null;
        
        LinkedList<InputOutputPair<Vector3, Boolean>> data = 
            new LinkedList<InputOutputPair<Vector3, Boolean>>();
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 0, baseCounts);
        assertNull(result);
        
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 4.0, 2.0), true));
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 0, baseCounts);
        assertNull(result);
        result = instance.computeBestGainAndThreshold(data, 1, baseCounts);
        assertNull(result);
        result = instance.computeBestGainAndThreshold(data, 2, baseCounts);
        assertNull(result);
        
        
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 1.0, 2.0), true));
        
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 0, baseCounts);
        assertNull(result);
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 1, baseCounts);
        assertEquals(0.0, result.getFirst());
        assertEquals(2.5, result.getSecond());
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 2, baseCounts);
        assertNull(result);
        
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 2.0, 3.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 4.0, 4.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 3.0, 5.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 0.0, 2.0), true));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 5.0, 2.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 7.0, 2.0), false));
        data.add(new DefaultInputOutputPair<Vector3, Boolean>(new Vector3(1.0, 8.0, 2.0), false));
        
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 0, baseCounts);
        assertNull(result);
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 1, baseCounts);
        assertEquals(0.458, result.getFirst(), 0.001);
        assertEquals(1.5, result.getSecond());
        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 2, baseCounts);
        assertEquals(0.251, result.getFirst(), 0.001);
        assertEquals(2.5, result.getSecond());
    }

    /**
     * Tests a corner-case of creating a threshold where the first split is
     * the result.
     */
    public void testThresholdBug()
    {
        VectorThresholdInformationGainLearner<Boolean> instance =
            new VectorThresholdInformationGainLearner<Boolean>();

        MapBasedDataHistogram<Boolean> baseCounts = null;
        DefaultPair<Double, Double> result = null;

        LinkedList<InputOutputPair<Vector2, Boolean>> data =
            new LinkedList<InputOutputPair<Vector2, Boolean>>();
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), true));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), true));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), true));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 1.0763), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), true));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 1.0763), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), true));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), true));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 1.0763), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), true));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 1.0763), true));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));
        data.add(new DefaultInputOutputPair<Vector2, Boolean>(new Vector2(0.0, 5.5639), false));

        baseCounts = CategorizationTreeLearner.getOutputCounts(data);
        result = instance.computeBestGainAndThreshold(data, 1, baseCounts);
        assertEquals((5.5639 + 1.0763) / 2.0, result.getSecond());
    }

}

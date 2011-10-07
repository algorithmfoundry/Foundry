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
import gov.sandia.cognition.statistics.distribution.DefaultDataDistribution;
import gov.sandia.cognition.util.DefaultPair;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
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
        
        DefaultDataDistribution<Boolean> baseCounts = null;
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
     * Test computeSplitGain() when manual priors are used.
     */
    public void testManualPriors()
    {
        VectorThresholdInformationGainLearner<Integer> instance = 
            new VectorThresholdInformationGainLearner<Integer>();
        
        // Make dummy data set w/ imbalanced class frequencies.
        HashMap<Integer, Integer> trainCounts = new HashMap<Integer, Integer>();
        trainCounts.put(0, 50);
        trainCounts.put(1, 25);
        trainCounts.put(2, 20);
        trainCounts.put(3, 5);
        int numKlass = trainCounts.size();

        // Manually assign priors so examples each class has equal
        // prior probability.
        HashMap<Integer, Double> equalPrior = new HashMap<Integer, Double>();
        for (int i = 0; i < numKlass; ++i) {
            equalPrior.put(i, 0.25);
        }

        instance.configure(equalPrior, trainCounts);

        DefaultDataDistribution<Integer> baseCounts = new DefaultDataDistribution<Integer>();
        baseCounts.set(0, 10);
        baseCounts.set(1, 10);
        baseCounts.set(2, 8);
        baseCounts.set(3, 2);
        DefaultDataDistribution<Integer> leftCounts = new DefaultDataDistribution<Integer>();
        leftCounts.set(0, 10);
        leftCounts.set(1, 0);
        leftCounts.set(2, 0);
        leftCounts.set(3, 2);
        DefaultDataDistribution<Integer> rightCounts = new DefaultDataDistribution<Integer>();
        rightCounts.set(0, 0);
        rightCounts.set(1, 10);
        rightCounts.set(2, 8);
        rightCounts.set(3, 0);
        double gain = instance.computeSplitGain(baseCounts, rightCounts, leftCounts);
        assertEquals(0.98522, gain, 1e-3);
    }

    /**
     * Test configure() method.
     */
    public void testConfigure()
    {
        VectorThresholdInformationGainLearner<Integer> instance = 
            new VectorThresholdInformationGainLearner<Integer>();
        
        // Make dummy data set w/ imbalanced class frequencies.
        HashMap<Integer, Integer> trainCounts = new HashMap<Integer, Integer>();
        trainCounts.put(0, 50);
        trainCounts.put(1, 25);
        trainCounts.put(2, 20);
        trainCounts.put(3, 5);
        int numKlass = trainCounts.size();

        // Make sure configure() assigns proper defaults.  
        instance.configure(null, trainCounts);
        double[] expected = {0.5, 0.25, 0.2, 0.05};
        ArrayList<Integer> index = readKlassIndex(instance);
        double[] priors = (double[])readPrivateField(instance, "klassPriors");        
        for (int i = 0; i < numKlass; ++i) {
            int klass = index.get(i);
            assertEquals(expected[klass], priors[i], 1e-5);
        }

        // Make sure configure() assigns manual priors.
        HashMap<Integer, Double> inversePriors = new HashMap<Integer, Double>();
        double mass = 0;
        for (int i = 0; i < numKlass; ++i) {
            expected[i] = 1.0 / expected[i];
            mass += expected[i];
        }
        for (int i = 0; i < numKlass; ++i) {
            expected[i] /= mass;
            inversePriors.put(i, expected[i]);
        }
        instance.configure(inversePriors, trainCounts);
        index = readKlassIndex(instance);
        priors = (double[])readPrivateField(instance, "klassPriors");                
        for (int i = 0; i < numKlass; ++i) {
            int klass = index.get(i);
            assertEquals(expected[klass], priors[i], 1e-5);
        }
    }

    private ArrayList<Integer> readKlassIndex(VectorThresholdInformationGainLearner<Integer> instance)
    {
        @SuppressWarnings("unchecked")
        ArrayList<Integer> index = (ArrayList<Integer>)readPrivateField(instance, "klasses");
        return index;
    }

    private Object readPrivateField(Object instance, String fieldName)
    {
        Class c = instance.getClass();
        Object value = null;
        String err = null;

        try {
            Field f = c.getDeclaredField(fieldName);
            f.setAccessible(true);
            value = f.get(instance);
        }
        catch (NoSuchFieldException nsfe) {
            err = nsfe.toString();
        }
        catch (IllegalAccessException iae) {
            err = iae.toString();
        }

        if (err != null) {
            fail(err);
        }

        return value;
    }

    /**
     * Tests a corner-case of creating a threshold where the first split is
     * the result.
     */
    public void testThresholdBug()
    {
        VectorThresholdInformationGainLearner<Boolean> instance =
            new VectorThresholdInformationGainLearner<Boolean>();

        DefaultDataDistribution<Boolean> baseCounts = null;
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

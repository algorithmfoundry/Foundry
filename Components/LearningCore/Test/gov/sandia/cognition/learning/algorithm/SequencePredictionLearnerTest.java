/*
 * File:                SequencePredictionLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.collection.MultiCollection;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.nearest.KNearestNeighborExhaustive;
import gov.sandia.cognition.learning.algorithm.nearest.NearestNeighbor;
import gov.sandia.cognition.learning.data.InputOutputPair;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class SequencePredictionLearner.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class SequencePredictionLearnerTest
    extends TestCase
{
    protected Random random;

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public SequencePredictionLearnerTest(
        String testName)
    {
        super(testName);

        this.random = new Random();
    }

    public void testConstants()
    {
        assertEquals(1, SequencePredictionLearner.DEFAULT_PREDICTION_HORIZION);
    }

    public void testConstructors()
    {
        int predictionHorizon = SequencePredictionLearner.DEFAULT_PREDICTION_HORIZION;
        KNearestNeighborExhaustive.Learner<Double, Double> learner = null;
        SequencePredictionLearner<Double, Evaluator<Double, Double>> instance =
            new SequencePredictionLearner<Double, Evaluator<Double, Double>>();
        assertEquals(predictionHorizon, instance.getPredictionHorizion());
        assertSame(learner, instance.getLearner());

        predictionHorizon = 4;
        learner = new KNearestNeighborExhaustive.Learner<Double, Double>();
        instance = new SequencePredictionLearner<Double, Evaluator<Double, Double>>(
            learner, predictionHorizon);
        assertEquals(predictionHorizon, instance.getPredictionHorizion());
        assertSame(learner, instance.getLearner());
    }

    /**
     * Test of learn method, of class SequencePredictionLearner.
     */
    public void testLearn()
    {
        SequencePredictionLearner<Double, NearestNeighbor<Double, Double>> instance =
            new SequencePredictionLearner<Double, NearestNeighbor<Double, Double>>(
                new KNearestNeighborExhaustive.Learner<Double, Double>(), 1);

        ArrayList<Double> data = new ArrayList<Double>();
        for (int i = 0; i < 10; i++)
        {
            data.add(random.nextDouble());
        }

        NearestNeighbor<Double, Double> result = instance.learn(data);
        assertEquals(9, result.getData().size());

        instance.setPredictionHorizon(2);
        result = instance.learn(data);
        assertEquals(8, result.getData().size());
    }

    /**
     * Test of createPredictionDataset method, of class SequencePredictionLearner.
     */
    public void testCreatePredictionDataset()
    {
        final ArrayList<Double> data = new ArrayList<Double>();
        MultiCollection<InputOutputPair<Double, Double>> result = null;
        result = SequencePredictionLearner.createPredictionDataset(data, 1);
        assertTrue(result.isEmpty());

        data.add(random.nextDouble());
        result = SequencePredictionLearner.createPredictionDataset(data, 1);
        assertTrue(result.isEmpty());

        data.add(random.nextDouble());
        result = SequencePredictionLearner.createPredictionDataset(data, 1);
        assertEquals(1, result.size());
        assertEquals(data.get(0), CollectionUtil.getElement(result, 0).getInput());
        assertEquals(data.get(1), CollectionUtil.getElement(result, 0).getOutput());
        result = SequencePredictionLearner.createPredictionDataset(data, 2);
        assertTrue(result.isEmpty());
        assertTrue(result.isEmpty());

        data.add(random.nextDouble());
        data.add(random.nextDouble());
        data.add(random.nextDouble());
        data.add(random.nextDouble());

        result = SequencePredictionLearner.createPredictionDataset(data, 1);
        for (int i = 0; i < (data.size() - 1); i++)
        {
            assertEquals(data.get(i), CollectionUtil.getElement(result, i).getInput());
            assertEquals(data.get(i + 1), CollectionUtil.getElement(result, i).getOutput());
        }

        result = SequencePredictionLearner.createPredictionDataset(data, 3);
        for (int i = 0; i < (data.size() - 3); i++)
        {
            assertEquals(data.get(i), CollectionUtil.getElement(result, i).getInput());
            assertEquals(data.get(i + 3), CollectionUtil.getElement(result, i).getOutput());
        }
    }

    /**
     * Test of getPredictionHorizion method, of class SequencePredictionLearner.
     */
    public void testGetPredictionHorizion()
    {
        this.testSetPredictionHorizon();
    }

    /**
     * Test of setPredictionHorizon method, of class SequencePredictionLearner.
     */
    public void testSetPredictionHorizon()
    {
        int predictionHorizon = 1;
        SequencePredictionLearner<Double, Evaluator<Double, Double>> instance = new SequencePredictionLearner<Double, Evaluator<Double, Double>>();
        assertEquals(predictionHorizon, instance.getPredictionHorizion());

        predictionHorizon = 2;
        instance.setPredictionHorizon(predictionHorizon);
        assertEquals(predictionHorizon, instance.getPredictionHorizion());

        predictionHorizon = 77;
        instance.setPredictionHorizon(predictionHorizon);
        assertEquals(predictionHorizon, instance.getPredictionHorizion());

        // Ensure that bad prediction horizons don't work:
        boolean exceptionThrown = false;
        try
        {
            instance.setPredictionHorizon(0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(predictionHorizon, instance.getPredictionHorizion());

        exceptionThrown = false;
        try
        {
            instance.setPredictionHorizon(-1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(predictionHorizon, instance.getPredictionHorizion());
    }

}

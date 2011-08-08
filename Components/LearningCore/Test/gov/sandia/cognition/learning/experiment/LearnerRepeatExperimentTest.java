/*
 * File:                LearnerRepeatExperimentTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 14, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.experiment;

import java.util.Random;
import gov.sandia.cognition.learning.algorithm.perceptron.Perceptron;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.performance.MeanZeroOneErrorEvaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import gov.sandia.cognition.learning.algorithm.ensemble.BinaryBaggingLearner;
import gov.sandia.cognition.learning.data.PartitionedDataset;
import gov.sandia.cognition.learning.data.RandomDataPartitioner;
import gov.sandia.cognition.math.UnivariateStatisticsUtil;
import java.util.ArrayList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for LearnerRepeatExperiment.
 *
 * @author  Justin Basilico
 * @since   3.1.1
 */
public class LearnerRepeatExperimentTest
{
    
    /** Random number generator. */
    protected Random random = new Random(211);
    
    /**
     * Creates a new test.
     */
    public LearnerRepeatExperimentTest()
    {
    }

    /**
     * Test of constructors of class LearnerRepeatExperiment.
     */
    public void testConstructors()
    {
        int numTrials = LearnerRepeatExperiment.DEFAULT_NUM_TRIALS;
        LearnerRepeatExperiment
            <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerRepeatExperiment<InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>();
        assertEquals(numTrials, instance.getNumTrials());
        assertNull(instance.getPerformanceEvaluator());
        assertNull(instance.getSummarizer());

        numTrials = 211;
        MeanZeroOneErrorEvaluator<Vector, Boolean> measure = new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        instance = new LearnerRepeatExperiment
            <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>(
            numTrials, measure, summarizer);
        assertEquals(numTrials, instance.getNumTrials());
        assertSame(measure, instance.getPerformanceEvaluator());
        assertSame(summarizer, instance.getSummarizer());
    }

    /**
     * Test of evaluatePerformance method, of class LearnerRepeatExperiment.
     */
    @Test
    public void testEvaluatePerformance()
    {
        int numTrials = 15;

        MeanZeroOneErrorEvaluator<Vector, Boolean> measure =
            new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);

        LearnerRepeatExperiment<InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerRepeatExperiment<InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>(
                numTrials, measure, summarizer);


        Vector2[] positives = new Vector2[]
        {
            new Vector2(1.00,  1.00),
            new Vector2(1.00,  3.00),
            new Vector2(0.25,  4.00),
            new Vector2(2.00,  1.00),
            new Vector2(5.00, -3.00)
        };

        Vector2[] negatives = new Vector2[]
        {
            new Vector2(2.00,  3.00),
            new Vector2(2.00,  4.00),
            new Vector2(3.00,  2.00),
            new Vector2(4.25,  3.75),
            new Vector2(4.00,  7.00),
            new Vector2(7.00,  4.00)
        };

        ArrayList<InputOutputPair<Vector, Boolean>> examples =
            new ArrayList<InputOutputPair<Vector, Boolean>>();
        for ( Vector2 example : positives )
        {
            examples.add(new DefaultInputOutputPair<Vector, Boolean>(example, true));
        }

        for ( Vector2 example : negatives )
        {
            examples.add(new DefaultInputOutputPair<Vector, Boolean>(example, false));
        }

        PartitionedDataset<InputOutputPair<Vector, Boolean>> dataset =
            RandomDataPartitioner.createPartition(
            examples, 0.5, random);

        BinaryBaggingLearner<Vector> learner = new BinaryBaggingLearner<Vector>(
            new Perceptron(), 10, random);

        ConfidenceInterval result = instance.evaluatePerformance(learner, dataset);
        assertSame(learner, instance.getLearner());
        assertNotNull(result);
        
        assertTrue(result.getCentralValue() > 0.0);
        assertEquals(UnivariateStatisticsUtil.computeMean(instance.getStatistics()),
            result.getCentralValue(), 1e-10);

        assertEquals(numTrials, instance.getNumTrials());
        assertEquals(numTrials, instance.getStatistics().size());
        assertSame(result, instance.getSummary());
    }

    /**
     * Test of getLearner method, of class LearnerRepeatExperiment.
     */
    @Test
    public void testGetLearner()
    {
        // Tested by evaluate.
    }

    /**
     * Test of getStatistics method, of class LearnerRepeatExperiment.
     */
    @Test
    public void testGetStatistics()
    {
        // Tested by evaluate.
    }

    /**
     * Test of getSummary method, of class LearnerRepeatExperiment.
     */
    @Test
    public void testGetSummary()
    {
        // Tested by evaluate.
    }

    /**
     * Test of getPerformanceEvaluator method, of class LearnerRepeatExperiment.
     */
    @Test
    public void testGetPerformanceEvaluator()
    {
        this.testSetPerformanceEvaluator();
    }

    /**
     * Test of setPerformanceEvaluator method, of class LearnerRepeatExperiment.
     */
    @Test
    public void testSetPerformanceEvaluator()
    {
        LearnerRepeatExperiment<InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerRepeatExperiment<InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>();
        assertNull(instance.getPerformanceEvaluator());

        MeanZeroOneErrorEvaluator<Vector, Boolean> measure = new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        instance.setPerformanceEvaluator(measure);
        assertSame(measure, instance.getPerformanceEvaluator());

        instance.setPerformanceEvaluator(null);
        assertNull(instance.getPerformanceEvaluator());
    }

    /**
     * Test of getSummarizer method, of class LearnerRepeatExperiment.
     */
    @Test
    public void testGetSummarizer()
    {
        this.testSetSummarizer();
    }

    /**
     * Test of setSummarizer method, of class LearnerRepeatExperiment.
     */
    @Test
    public void testSetSummarizer()
    {
        LearnerRepeatExperiment
            <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerRepeatExperiment<InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>();
        assertNull(instance.getSummarizer());

        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        instance.setSummarizer(summarizer);
        assertSame(summarizer, instance.getSummarizer());

        instance.setSummarizer(null);
        assertNull(instance.getSummarizer());
    }

    /**
     * Test of getNumTrials method, of class LearnerRepeatExperiment.
     */
    @Test
    public void testGetNumTrials()
    {
        this.testSetNumTrials();
    }

    /**
     * Test of setNumTrials method, of class LearnerRepeatExperiment.
     */
    @Test
    public void testSetNumTrials()
    {
        int numTrials = LearnerRepeatExperiment.DEFAULT_NUM_TRIALS;
        LearnerRepeatExperiment
            <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerRepeatExperiment<InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>();
        assertEquals(numTrials, instance.getNumTrials());

        numTrials = 1;
        instance.setNumTrials(numTrials);
        assertEquals(numTrials, instance.getNumTrials());

        numTrials = 2;
        instance.setNumTrials(numTrials);
        assertEquals(numTrials, instance.getNumTrials());

        numTrials = 7;
        instance.setNumTrials(numTrials);
        assertEquals(numTrials, instance.getNumTrials());

        int[] badValues = {0, -1, -2, -(1 + random.nextInt(1000))};
        for (int badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setNumTrials(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue("" + badValue, exceptionThrown);
            }
            assertEquals(numTrials, instance.getNumTrials());
        }
    }

}
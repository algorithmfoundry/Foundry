/*
 * File:                LearnerComparisonExperimentTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright October 4, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.learning.function.kernel.PolynomialKernel;
import gov.sandia.cognition.learning.algorithm.perceptron.kernel.KernelPerceptron;
import gov.sandia.cognition.learning.algorithm.perceptron.Perceptron;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import gov.sandia.cognition.learning.performance.MeanZeroOneErrorEvaluator;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.statistics.method.ConfidenceStatistic;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.util.DefaultPair;
import java.util.ArrayList;
import junit.framework.TestCase;

/**
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class LearnerComparisonExperimentTest
    extends TestCase
{
    public LearnerComparisonExperimentTest(
        String testName)
    {
        super(testName);
    }

    public void testConstructors()
    {
        LearnerComparisonExperiment<InputOutputPair<Vector,Boolean>, InputOutputPair<Vector,Boolean>,
            Evaluator<? super Vector, Boolean>, Number, ConfidenceInterval>
            instance = new LearnerComparisonExperiment<InputOutputPair<Vector,Boolean>, InputOutputPair<Vector,Boolean>,
                Evaluator<? super Vector,Boolean>, Number, ConfidenceInterval>();

        assertNull(instance.getFoldCreator());
        assertNull(instance.getPerformanceEvaluator());
        assertNull(instance.getStatisticalTest());
        assertNull(instance.getSummarizer());
        
        LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>> foldCreator = 
            new LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>>();
        MeanZeroOneErrorEvaluator<Vector, Boolean> performance = 
            new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        StudentTConfidence test = new StudentTConfidence();
        instance = new LearnerComparisonExperiment<InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>,
                Evaluator<? super Vector, Boolean>, Number, ConfidenceInterval>(
            foldCreator,performance,test,summarizer);
        assertSame(foldCreator, instance.getFoldCreator());
        assertSame(performance, instance.getPerformanceEvaluator());
        assertSame(test, instance.getStatisticalTest());
        assertSame(summarizer, instance.getSummarizer());
    }


    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.
     */
    public void testEvaluate()
    {
        LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>> foldCreator = 
            new LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>>();
        MeanZeroOneErrorEvaluator<Vector, Boolean> performance = 
            new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        StudentTConfidence test = new StudentTConfidence();
        LearnerComparisonExperiment<InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>,
            Evaluator<? super Vector, Boolean>, Number, ConfidenceInterval>
            instance = new LearnerComparisonExperiment<InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>,
                Evaluator<? super Vector, Boolean>, Number, ConfidenceInterval>(
            foldCreator, performance, test, summarizer); 
        
        // This unit test is for the XOR problem with some (fake) noise.
        Vector2[] positives = new Vector2[]
        {
            new Vector2(1.00,  0.00),
            new Vector2(1.01,  0.01),
            new Vector2(0.00,  1.00),
            new Vector2(0.01,  1.01)
        };
        
        Vector2[] negatives = new Vector2[]
        {
            new Vector2(0.00,  0.00),
            new Vector2(0.01,  0.01),
            new Vector2(1.00,  1.00),
            new Vector2(1.01,  1.01)
        };
        
        ArrayList<InputOutputPair<Vector,Boolean>> examples =
            new ArrayList<InputOutputPair<Vector, Boolean>>();
        for ( Vector2 example : positives )
        {
            examples.add(new DefaultInputOutputPair<Vector, Boolean>(example, true));
        }
        
        for ( Vector2 example : negatives )
        {
            examples.add(new DefaultInputOutputPair<Vector, Boolean>(example, false));
        }
        
        // The perceptron can't learn the XOR problem.
        Perceptron learner1 = new Perceptron();
        
        // The kernel perceptron with a polynomial of degree 2 can learn it.
        KernelPerceptron<Vector> learner2 = new KernelPerceptron<Vector>(
            new PolynomialKernel(2));
        
        LearnerComparisonExperiment.Result<ConfidenceInterval> result =
            instance.evaluate(learner1, learner2, examples);
        assertTrue(result.getConfidence().getNullHypothesisProbability() < 0.05);
        assertTrue(result.getSummaries().getFirst().getCentralValue() > 0.0);
        assertTrue(result.getSummaries().getSecond().getCentralValue() == 0.0);
        
        assertSame(result.getConfidence(), instance.getConfidence());
        assertSame(result.getSummaries(), instance.getSummaries());
        assertNotNull(instance.getLearners());
        assertSame(learner1, instance.getLearners().getFirst());
        assertSame(learner2, instance.getLearners().getSecond());
        assertNotNull(instance.getStatistics());
        assertEquals(instance.getNumTrials(), instance.getStatistics().getFirst().size());
        assertEquals(instance.getNumTrials(), instance.getStatistics().getSecond().size());
    }

    /**
     * Test of getPerformanceEvaluator method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.
     */
    public void testGetPerformanceEvaluator()
    {
        this.testSetPerformanceEvaluator();
    }

    /**
     * Test of setPerformanceEvaluator method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.
     */
    public void testSetPerformanceEvaluator()
    {
        LearnerComparisonExperiment<InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>,
            Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerComparisonExperiment<InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>,
                Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>();

        assertNull(instance.getPerformanceEvaluator());
        
        MeanZeroOneErrorEvaluator<Vector, Boolean> performance = 
            new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        instance.setPerformanceEvaluator(performance);
        assertSame(performance, instance.getPerformanceEvaluator());
        
        instance.setPerformanceEvaluator(null);
        assertNull(instance.getPerformanceEvaluator());
    }

    /**
     * Test of getSummarizer method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.
     */
    public void testGetSummarizer()
    {
        this.testSetSummarizer();
    }

    /**
     * Test of setSummarizer method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.
     */
    public void testSetSummarizer()
    {
        LearnerComparisonExperiment<InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>,
            Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerComparisonExperiment<InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>,
                Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>();

        assertNull(instance.getSummarizer());
        
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        instance.setSummarizer(summarizer);
        assertSame(summarizer, instance.getSummarizer());
        
        instance.setSummarizer(null);
        assertNull(instance.getSummarizer());
    }

    /**
     * Test of getStatisticalTest method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.
     */
    public void testGetStatisticalTest()
    {
        this.testSetStatisticalTest();
    }

    /**
     * Test of setStatisticalTest method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.
     */
    public void testSetStatisticalTest()
    {
        LearnerComparisonExperiment<InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>,
            Evaluator<? super Vector, Boolean>, Number, ConfidenceInterval>
            instance = new LearnerComparisonExperiment<InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>,
                Evaluator<? super Vector, Boolean>, Number, ConfidenceInterval>();
        
        assertNull(instance.getStatisticalTest());
        
        StudentTConfidence test = new StudentTConfidence();
        instance.setStatisticalTest(test);
        assertSame(test, instance.getStatisticalTest());
        
        instance.setStatisticalTest(null);
        assertNull(instance.getStatisticalTest());
    }

    /**
     * Test of getLearners method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.
     */
    public void testGetLearners()
    {
        // Tested by evaluate.
    }

    /**
     * Test of getStatistics method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.
     */
    public void testGetStatistics()
    {
        // Tested by evaluate.
    }

    /**
     * Test of getConfidence method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.
     */
    public void testGetConfidence()
    {
        // Tested by evaluate.
    }

    /**
     * Test of getSummaries method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.
     */
    public void testGetSummaries()
    {
        // Tested by evaluate.
    }
    
    public static class ResultTest extends TestCase
    {

        public ResultTest(String testName)
        {
            super(testName);
        }
        
        public void testConstructors()
        {
            ConfidenceStatistic confidence = new StudentTConfidence.Statistic(
                0.5, 1.0);
            DefaultPair<ConfidenceInterval, ConfidenceInterval> summaries = 
                new DefaultPair<ConfidenceInterval, ConfidenceInterval>();
            LearnerComparisonExperiment.Result<ConfidenceInterval> instance =
                new LearnerComparisonExperiment.Result<ConfidenceInterval>(
                    confidence, summaries);
            
            assertSame(confidence, instance.getConfidence());
            assertSame(summaries, instance.getSummaries());
        }

        /**
         * Test of getConfidence method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.Result.
         */
        public void testGetConfidence()
        {
            this.testSetConfidence();
        }

        /**
         * Test of setConfidence method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.Result.
         */
        public void testSetConfidence()
        {
            LearnerComparisonExperiment.Result<ConfidenceInterval> instance =
                new LearnerComparisonExperiment.Result<ConfidenceInterval>(
                    null, null);
            assertNull(instance.getConfidence());
            
            ConfidenceStatistic confidence = new StudentTConfidence.Statistic(
                0.5, 1.0);
            instance.setConfidence(confidence);
            assertSame(confidence, instance.getConfidence());
            
            instance.setConfidence(null);
            assertNull(instance.getConfidence());
        }

        /**
         * Test of getSummaries method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.Result.
         */
        public void testGetSummaries()
        {
            this.testSetSummaries();
        }

        /**
         * Test of setSummaries method, of class gov.sandia.cognition.learning.experiment.LearnerComparisonExperiment.Result.
         */
        public void testSetSummaries()
        {
            LearnerComparisonExperiment.Result<ConfidenceInterval> instance =
                new LearnerComparisonExperiment.Result<ConfidenceInterval>(
                    null, null);
            assertNull(instance.getSummaries());
            
            DefaultPair<ConfidenceInterval, ConfidenceInterval> summaries = 
                new DefaultPair<ConfidenceInterval, ConfidenceInterval>();
            instance.setSummaries(summaries);
            assertSame(summaries, instance.getSummaries());
            
            instance.setSummaries(null);
            assertNull(instance.getSummaries());
        }
    }
}

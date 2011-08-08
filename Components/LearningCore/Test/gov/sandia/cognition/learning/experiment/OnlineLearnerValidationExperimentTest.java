/*
 * File:                OnlineLearnerValidationExperimentTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 10, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.OnlineLearner;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.ConstantEvaluator;
import gov.sandia.cognition.learning.performance.MeanZeroOneErrorEvaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.ArrayList;
import java.util.Arrays;
import junit.framework.TestCase;

/**
 * Unit tests for class OnlineLearnerValidationExperiment.
 *
 * @author Justin Basilico
 * @since  3.0
 */
public class OnlineLearnerValidationExperimentTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public OnlineLearnerValidationExperimentTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class OnlineLearnerValidationExperiment.
     */
    public void testConstructors()
    {
        OnlineLearnerValidationExperiment
            <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new OnlineLearnerValidationExperiment
                <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>();
        assertNull(instance.getPerformanceEvaluator());
        assertNull(instance.getSummarizer());

        MeanZeroOneErrorEvaluator<Vector, Boolean> measure = new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        instance = new OnlineLearnerValidationExperiment
            <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>(
            measure, summarizer);
        assertSame(measure, instance.getPerformanceEvaluator());
        assertSame(summarizer, instance.getSummarizer());
    }

    /**
     * Test of evaluatePerformance method, of class OnlineLearnerValidationExperiment.
     */
    public void testEvaluatePerformance()
    {
        MeanZeroOneErrorEvaluator<Vector, Boolean> measure =
            new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);

        OnlineLearnerValidationExperiment
            <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new OnlineLearnerValidationExperiment
                <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>(
                measure, summarizer);


        Vector[] positives = new Vector[]
        {
            new Vector2(1.00,  1.00),
            new Vector2(1.00,  3.00),
            new Vector2(0.25,  4.00),
            new Vector2(2.00,  1.00),
            new Vector2(5.00, -3.00)
        };

        Vector[] negatives = new Vector[]
        {
            new Vector2(2.00,  3.00),
            new Vector2(2.00,  4.00),
            new Vector2(3.00,  2.00),
            new Vector2(4.25,  3.75),
            new Vector2(4.00,  7.00),
            new Vector2(7.00,  4.00)
        };

        ArrayList<DefaultInputOutputPair<Vector, Boolean>> examples =
            new ArrayList<DefaultInputOutputPair<Vector, Boolean>>();
        examples.addAll(DefaultInputOutputPair.labelCollection(
            Arrays.asList(positives), true));
        examples.addAll(DefaultInputOutputPair.labelCollection(
            Arrays.asList(negatives), false));

        DummyOnlineLearner<Vector, Boolean> learner =
            new DummyOnlineLearner<Vector, Boolean>(
                new ConstantEvaluator<Boolean>(false));

        ConfidenceInterval result = instance.evaluatePerformance(learner, examples);

        assertNotNull(result);
        assertEquals((double) positives.length / examples.size(), result.getCentralValue());

        assertEquals(examples.size(), instance.getNumTrials());
        assertEquals(examples.size(), instance.getStatistics().size());
        assertSame(result, instance.getSummary());

        assertEquals(examples.size(), learner.updateCallCount);
    }

    /**
     * Test of getPerformanceEvaluator method, of class OnlineLearnerValidationExperiment.
     */
    public void testGetPerformanceEvaluator()
    {
        this.testSetPerformanceEvaluator();
    }

    /**
     * Test of setPerformanceEvaluator method, of class OnlineLearnerValidationExperiment.
     */
    public void testSetPerformanceEvaluator()
    {
        OnlineLearnerValidationExperiment
            <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new OnlineLearnerValidationExperiment
                <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>();

        assertNull(instance.getPerformanceEvaluator());

        MeanZeroOneErrorEvaluator<Vector, Boolean> measure = new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        instance.setPerformanceEvaluator(measure);
        assertSame(measure, instance.getPerformanceEvaluator());

        instance.setPerformanceEvaluator(null);
        assertNull(instance.getPerformanceEvaluator());
    }

    /**
     * Test of getSummarizer method, of class OnlineLearnerValidationExperiment.
     */
    public void testGetSummarizer()
    {
        this.testSetSummarizer();
    }

    /**
     * Test of setSummarizer method, of class OnlineLearnerValidationExperiment.
     */
    public void testSetSummarizer()
    {
        OnlineLearnerValidationExperiment
            <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new OnlineLearnerValidationExperiment
                <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>();

        assertNull(instance.getSummarizer());

        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        instance.setSummarizer(summarizer);
        assertSame(summarizer, instance.getSummarizer());

        instance.setSummarizer(null);
        assertNull(instance.getSummarizer());
    }

    /**
     * Test of getNumTrials method, of class OnlineLearnerValidationExperiment.
     */
    public void testGetNumTrials()
    {
        OnlineLearnerValidationExperiment
            <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new OnlineLearnerValidationExperiment
                <InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>();
                assertEquals(-1, instance.getNumTrials());
    }

    /**
     * Test of getStatistics method, of class OnlineLearnerValidationExperiment.
     */
    public void testGetStatistics()
    {
        // Tested by evaluatePerformance.
    }

    /**
     * Test of getSummary method, of class OnlineLearnerValidationExperiment.
     */
    public void testGetSummary()
    {
        // Tested by evaluatePerformance.
    }

    /**
     * A dummy test class of an online learner for the unit test.
     *
     * @param   <InputType>
     *      The input data type.
     * @param   <OutputType>
     *      The output data type.
     */
    private static class DummyOnlineLearner<InputType, OutputType>
        extends AbstractCloneableSerializable
        implements OnlineLearner<Object, Evaluator<? super InputType, OutputType>>
    {
        /** The result that is returned by the online learner. */
        protected Evaluator<? super InputType, OutputType> evaluator;

        /** Counts the number of times update is called. */
        protected int updateCallCount;

        /**
         * Creates a new {@code DummyOnlineEvaluator}.
         *
         * @param   evaluator
         *      The evaluator to return.
         */
        public DummyOnlineLearner(
            final Evaluator<? super InputType, OutputType> evaluator)
        {
            super();
            this.evaluator = evaluator;
        }
        
        public Evaluator<? super InputType, OutputType> createInitialLearnedObject()
        {
            return evaluator;
        }

        public void update(
            Evaluator<? super InputType, OutputType> target,
            Object data)
        {
            this.updateCallCount++;
        }

        public void update(
            final Evaluator<? super InputType, OutputType> target,
            final Iterable<? extends Object> data)
        {
            throw new UnsupportedOperationException(
                "Update with an iterable should not be called.");
        }

    }

}

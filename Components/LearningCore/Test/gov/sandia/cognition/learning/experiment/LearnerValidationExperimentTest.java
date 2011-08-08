/*
 * File:                SupervisedLearnerExperimentTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.experiment;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.algorithm.perceptron.Perceptron;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.performance.MeanZeroOneErrorEvaluator;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.statistics.method.ConfidenceInterval;
import gov.sandia.cognition.statistics.method.StudentTConfidence;
import java.util.ArrayList;
import junit.framework.TestCase;


/**
 * This class implements JUnit tests for the following classes:
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class LearnerValidationExperimentTest
    extends TestCase
{
    public static final double EPSILON = 1e-10;
    public LearnerValidationExperimentTest(
        String testName)
    {
        super(testName);
    }
    
    public void testConstructors()
    {
        LearnerValidationExperiment
            <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerValidationExperiment
                <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>();

        assertNull(instance.getFoldCreator());
        assertNull(instance.getPerformanceEvaluator());
        assertNull(instance.getSummarizer());
        
        LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>> foldCreator = 
            new LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>>();
        MeanZeroOneErrorEvaluator<Vector, Boolean> measure = new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        instance = new LearnerValidationExperiment
            <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>(
            foldCreator, measure, summarizer);
        assertSame(foldCreator, instance.getFoldCreator());
        assertSame(measure, instance.getPerformanceEvaluator());
        assertSame(summarizer, instance.getSummarizer());
    }

    /**
     * Test of evaluate method, of class SupervisedLearnerExperiment.
     */
    public void testEvaluate()
    {
        LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>> foldCreator = 
            new LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>>();
        MeanZeroOneErrorEvaluator<Vector, Boolean> measure = 
            new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        
        LearnerValidationExperiment
            <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerValidationExperiment
                <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>(
                foldCreator, measure, summarizer);
        
        
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
        
        
        Perceptron learner = new Perceptron();
        
        ConfidenceInterval result =instance.evaluatePerformance(learner, examples);
        
        assertNotNull(result);
        assertEquals(1.0 / examples.size(), result.getCentralValue(), EPSILON);
        
        assertEquals(examples.size(), instance.getNumTrials());
        assertEquals(examples.size(), instance.getStatistics().size());
        assertSame(result, instance.getSummary());
    }

    /**
     * Test of getFoldCreator method, of class SupervisedLearnerExperiment.
     */
    public void testGetFoldCreator()
    {
        this.testSetFoldCreator();
    }

    /**
     * Test of setFoldCreator method, of class SupervisedLearnerExperiment.
     */
    public void testSetFoldCreator()
    {
        LearnerValidationExperiment
            <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerValidationExperiment
                <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<Vector, Boolean>, Double, ConfidenceInterval>();

        assertNull(instance.getFoldCreator());
        
        LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>> foldCreator = new LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>>();
        instance.setFoldCreator(foldCreator);
        assertSame(foldCreator, instance.getFoldCreator());
        
        instance.setFoldCreator(null);
        assertNull(instance.getFoldCreator());
    }

    /**
     * Test of getMeasure method, of class SupervisedLearnerExperiment.
     */
    public void testGetPerformanceEvaluator()
    {
        this.testSetPerformanceEvaluator();
    }

    /**
     * Test of setMeasure method, of class SupervisedLearnerExperiment.
     */
    public void testSetPerformanceEvaluator()
    {
        LearnerValidationExperiment
            <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<Vector,Boolean>, Double, ConfidenceInterval>
            instance = new LearnerValidationExperiment
                <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<Vector,Boolean>, Double, ConfidenceInterval>();

        assertNull(instance.getPerformanceEvaluator());
        
        MeanZeroOneErrorEvaluator<Vector, Boolean> measure = new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        instance.setPerformanceEvaluator(measure);
        assertSame(measure, instance.getPerformanceEvaluator());
        
        instance.setPerformanceEvaluator(null);
        assertNull(instance.getPerformanceEvaluator());
    }

    /**
     * Test of getSummarizer method, of class SupervisedLearnerExperiment.
     */
    public void testGetSummarizer()
    {
        this.testSetSummarizer();
    }

    /**
     * Test of setSummarizer method, of class SupervisedLearnerExperiment.
     */
    public void testSetSummarizer()
    {
        LearnerValidationExperiment
            <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerValidationExperiment
                <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<Vector, Boolean>, Double, ConfidenceInterval>();

        assertNull(instance.getSummarizer());
        
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        instance.setSummarizer(summarizer);
        assertSame(summarizer, instance.getSummarizer());
        
        instance.setSummarizer(null);
        assertNull(instance.getSummarizer());
    }

    /**
     * Test of getNumTrials method, of class SupervisedLearnerExperiment.
     */
    public void testGetNumTrials()
    {
        LearnerValidationExperiment
            <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<Vector, Boolean>, Double, ConfidenceInterval>
            instance = new LearnerValidationExperiment
                <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<Vector, Boolean>, Double, ConfidenceInterval>();

        assertEquals(-1, instance.getNumTrials());
    }

    /**
     * Test of getStatistics method, of class SupervisedLearnerExperiment.
     */
    public void testGetStatistics()
    {
        // Tested by evaluate.
    }

    /**
     * Test of getSummary method, of class SupervisedLearnerExperiment.
     */
    public void testGetSummary()
    {
        // Tested by evaluate.
    }
}

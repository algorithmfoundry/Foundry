/*
 * File:                ParallelLearnerValidationExperimentTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright October 04, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
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
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import junit.framework.TestCase;

/**
 * Tests of ParallelLearnerValidationExperiment
 *
 * @author  Justin Basilico
 * @since   2.1
 */
public class ParallelLearnerValidationExperimentTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public ParallelLearnerValidationExperimentTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of testEvaluate method, of class ParallelLearnerValidationExperiment.
     */
    public void testEvaluate()
    {
        LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>> foldCreator = 
            new LeaveOneOutFoldCreator<InputOutputPair<Vector, Boolean>>();
        MeanZeroOneErrorEvaluator<Vector, Boolean> measure = 
            new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        StudentTConfidence.Summary summarizer = new StudentTConfidence.Summary(0.95);
        
        ParallelLearnerValidationExperiment
            <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>
            instance = new ParallelLearnerValidationExperiment
                <InputOutputPair<Vector,Boolean>, InputOutputPair<Vector, Boolean>, Evaluator<? super Vector, Boolean>, Double, ConfidenceInterval>(
                foldCreator, measure, summarizer);
        instance.setThreadPool((ThreadPoolExecutor) Executors.newFixedThreadPool(1));
        
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
        assertEquals(1.0 / examples.size(), result.getCentralValue());
        
        assertEquals(examples.size(), instance.getNumTrials());
        assertEquals(examples.size(), instance.getStatistics().size());
        assertSame(result, instance.getSummary());
    }

}

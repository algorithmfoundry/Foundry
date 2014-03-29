/*
 * File:            AnytimeBatchLearnerValidationPerformanceReporterTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2013 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.performance;

import gov.sandia.cognition.learning.algorithm.perceptron.Perceptron;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@link AnytimeBatchLearnerValidationPerformanceReporter}.
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class AnytimeBatchLearnerValidationPerformanceReporterTest
    extends Object
{
    protected Random random = new Random(10001);
    /**
     * Creates a new test.
     */
    public AnytimeBatchLearnerValidationPerformanceReporterTest()
    {
        super();
    }

    /**
     * Test of stepEnded method, of class AnytimeBatchLearnerValidationPerformanceReporter.
     */
    @Test
    public void testStepEnded()
    {
        List<InputOutputPair<Vector, Boolean>> training =
            new ArrayList<InputOutputPair<Vector, Boolean>>();
        List<InputOutputPair<Vector, Boolean>> testing =
            new ArrayList<InputOutputPair<Vector, Boolean>>();
        VectorFactory<?> vf = VectorFactory.getDenseDefault();
        int d = 3;
        for (int i = 0; i < 10; i++)
        {
            training.add(DefaultInputOutputPair.create(
                vf.createUniformRandom(d, -1, 1, random),
                random.nextBoolean()));
            testing.add(DefaultInputOutputPair.create(
                vf.createUniformRandom(d, -1, 1, random),
                random.nextBoolean()));
        }
        
        MeanZeroOneErrorEvaluator<Vector, Boolean> performanceEvaluator =
            new MeanZeroOneErrorEvaluator<Vector, Boolean>();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream outPrintStream = new PrintStream(out);
        AnytimeBatchLearnerValidationPerformanceReporter<List<InputOutputPair<Vector, Boolean>>, LinearBinaryCategorizer> instance = 
            new AnytimeBatchLearnerValidationPerformanceReporter<List<InputOutputPair<Vector, Boolean>>, LinearBinaryCategorizer>(
                performanceEvaluator, testing, outPrintStream);
        Perceptron algorithm = new Perceptron();
        algorithm.setMaxIterations(1);
        algorithm.addIterativeAlgorithmListener(instance);
        
        // stepEnded gets called through learn.
        algorithm.learn(training);
        
        System.out.println(out.toString());
        assertTrue(!out.toString().isEmpty());
    }
    
}

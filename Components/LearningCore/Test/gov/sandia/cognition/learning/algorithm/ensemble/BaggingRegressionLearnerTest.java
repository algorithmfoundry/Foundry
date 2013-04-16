/*
 * File:            BaggingRegressionLearnerTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.learning.function.scalar.LinearDiscriminantWithBias;
import gov.sandia.cognition.learning.algorithm.regression.LinearRegression;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;
import static org.junit.Assert.*;

/**
 * Unit tests for class BaggingRegressionLearner.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class BaggingRegressionLearnerTest
    extends TestCase
{
    protected Random random;

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public BaggingRegressionLearnerTest(
        String testName)
    {
        super(testName);

        this.random = new Random();
    }

    /**
     * Test of constructors of class BaggingRegressionLearner.
     */
    public void testConstructors()
    {
        LinearRegression learner = null;
        double percentToSample = BaggingCategorizerLearner.DEFAULT_PERCENT_TO_SAMPLE;
        int maxIterations = BaggingCategorizerLearner.DEFAULT_MAX_ITERATIONS;
        BaggingRegressionLearner<Vector> instance =
            new BaggingRegressionLearner<Vector>();
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertNotNull(instance.getRandom());

        learner = new LinearRegression();
        instance = new BaggingRegressionLearner<Vector>(learner);
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertNotNull(instance.getRandom());

        percentToSample = percentToSample / 3.4;
        maxIterations = maxIterations * 9;
        instance = new BaggingRegressionLearner<Vector>(learner, maxIterations, percentToSample, random);
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertSame(random, instance.getRandom());
    }

    /**
     * Test of learn method, of class BaggingCategorizerLearner.
     */
    public void testLearn()
    {
        BaggingRegressionLearner<Vector> instance =
            new BaggingRegressionLearner<Vector>();
        instance.setLearner(new LinearRegression());
        instance.setRandom(random);
        instance.setMaxIterations(5);
        instance.setPercentToSample(0.5);

        assertNull(instance.getResult());

        ArrayList<InputOutputPair<Vector, Double>> data =
            new ArrayList<InputOutputPair<Vector, Double>>();
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();

        for (int i = 0; i < 10; i++)
        {
            data.add(new DefaultInputOutputPair<Vector, Double>(
                vectorFactory.createUniformRandom(
                    14, 0.0, 1.0, random), random.nextDouble() + 1.0));
        }
        for (int i = 0; i < 5; i++)
        {
            data.add(new DefaultInputOutputPair<Vector, Double>(
                vectorFactory.createUniformRandom(
                    14, -1.0, 0.0, random), -(random.nextDouble() - 1.0)));
        }

        AveragingEnsemble<Vector, ?> result = instance.learn(data);
        assertSame(result, instance.getResult());

        assertEquals(5, result.getMembers().size());
        for (Object member : result.getMembers())
        {
            assertTrue(member instanceof LinearDiscriminantWithBias);
        }
    }

}

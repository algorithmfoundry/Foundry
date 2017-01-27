/*
 * File:                BaggingCategorizerLearnerTest.java
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

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.learning.algorithm.perceptron.Perceptron;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class BaggingCategorizerLearner.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class BaggingCategorizerLearnerTest
    extends TestCase
{
    protected Random random;

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public BaggingCategorizerLearnerTest(
        String testName)
    {
        super(testName);

        this.random = new Random();
    }

    /**
     * Test of constructors of class BaggingCategorizerLearner.
     */
    public void testConstructors()
    {
        Perceptron learner = null;
        double percentToSample = BaggingCategorizerLearner.DEFAULT_PERCENT_TO_SAMPLE;
        int maxIterations = BaggingCategorizerLearner.DEFAULT_MAX_ITERATIONS;
        BaggingCategorizerLearner<Vector, Boolean> instance =
            new BaggingCategorizerLearner<Vector, Boolean>();
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertNotNull(instance.getRandom());

        learner = new Perceptron();
        instance = new BaggingCategorizerLearner<Vector, Boolean>(learner);
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertNotNull(instance.getRandom());

        percentToSample = percentToSample / 3.4;
        maxIterations = maxIterations * 9;
        instance = new BaggingCategorizerLearner<Vector, Boolean>(learner, maxIterations, percentToSample, random);
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
        BaggingCategorizerLearner<Vector, Boolean> instance =
            new BaggingCategorizerLearner<Vector, Boolean>();
        instance.setLearner(new Perceptron());
        instance.setRandom(random);
        instance.setMaxIterations(5);
        instance.setPercentToSample(0.5);

        assertNull(instance.getResult());

        ArrayList<InputOutputPair<Vector, Boolean>> data =
            new ArrayList<InputOutputPair<Vector, Boolean>>();
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        
        for (int i = 0; i < 10; i++)
        {
            data.add(new DefaultInputOutputPair<Vector, Boolean>(
                vectorFactory.createUniformRandom(
                    14, 0.0, 1.0, random), true));
        }
        for (int i = 0; i < 5; i++)
        {
            data.add(new DefaultInputOutputPair<Vector, Boolean>(
                vectorFactory.createUniformRandom(
                    14, -1.0, 0.0, random), false));
        }

        WeightedVotingCategorizerEnsemble<Vector, Boolean, ?> result =
            instance.learn(data);
        assertSame(result, instance.getResult());

        assertEquals(5, result.getMembers().size());
        for (WeightedValue<?> member : result.getMembers())
        {
            assertEquals(1.0, member.getWeight(), 0.0);
            assertNotNull(member.getValue());
            assertTrue(member.getValue() instanceof LinearBinaryCategorizer);
        }
    }
    
    
    /**
     * Test of learn method, of class BaggingCategorizerLearner
     * using the out-of-bag stopping criteria.
     */
    public void testLearnWithOOB()
    {
        BaggingCategorizerLearner<Vector, Boolean> instance =
            new BaggingCategorizerLearner<Vector, Boolean>();
        instance.setLearner(new Perceptron());
        instance.setRandom(random);
        instance.setMaxIterations(5);
        instance.setPercentToSample(0.5);
        instance.addIterativeAlgorithmListener(
            new BaggingCategorizerLearner.OutOfBagErrorStoppingCriteria<>(2));

        assertNull(instance.getResult());

        ArrayList<InputOutputPair<Vector, Boolean>> data =
            new ArrayList<InputOutputPair<Vector, Boolean>>();
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        
        for (int i = 0; i < 10; i++)
        {
            data.add(new DefaultInputOutputPair<Vector, Boolean>(
                vectorFactory.createUniformRandom(
                    14, 0.0, 1.0, random), true));
        }
        for (int i = 0; i < 5; i++)
        {
            data.add(new DefaultInputOutputPair<Vector, Boolean>(
                vectorFactory.createUniformRandom(
                    14, -1.0, 0.0, random), false));
        }

        WeightedVotingCategorizerEnsemble<Vector, Boolean, ?> result =
            instance.learn(data);
        assertSame(result, instance.getResult());

        assertEquals(4, result.getMembers().size());
        for (WeightedValue<?> member : result.getMembers())
        {
            assertEquals(1.0, member.getWeight(), 0.0);
            assertNotNull(member.getValue());
            assertTrue(member.getValue() instanceof LinearBinaryCategorizer);
        }
    }

}

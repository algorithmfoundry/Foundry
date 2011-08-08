/*
 * File:                CategoryBalancedBaggingLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright June 15, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Random;
import gov.sandia.cognition.learning.algorithm.perceptron.Perceptron;
import gov.sandia.cognition.math.matrix.Vector;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test for class CategoryBalancedBaggingLearner.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class CategoryBalancedBaggingLearnerTest
{
    
    /** Random number generator to use. */
    protected Random random = new Random(211);
    
    /**
     * Creates a new test.
     */
    public CategoryBalancedBaggingLearnerTest()
    {
    }

    /**
     * Test of constructors of class CategoryBalancedBaggingLearner.
     */
    @Test
    public void testConstructors()
    {
        Perceptron learner = null;
        double percentToSample = BaggingCategorizerLearner.DEFAULT_PERCENT_TO_SAMPLE;
        int maxIterations = BaggingCategorizerLearner.DEFAULT_MAX_ITERATIONS;
        CategoryBalancedBaggingLearner<Vector, Boolean> instance =
            new CategoryBalancedBaggingLearner<Vector, Boolean>();
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertNotNull(instance.getRandom());

        learner = new Perceptron();
        instance = new CategoryBalancedBaggingLearner<Vector, Boolean>(learner);
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertNotNull(instance.getRandom());

        percentToSample = percentToSample / 3.4;
        maxIterations = maxIterations * 9;
        instance = new CategoryBalancedBaggingLearner<Vector, Boolean>(learner, maxIterations, percentToSample, random);
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertSame(random, instance.getRandom());
    }


    /**
     * Test of learn method, of class CategoryBalancedBaggingLearner.
     */
    @Test
    public void testLearn()
    {
        CategoryBalancedBaggingLearner<Vector, Boolean> instance =
            new CategoryBalancedBaggingLearner<Vector, Boolean>();
        instance.setLearner(new Perceptron());
        instance.setRandom(random);
        instance.setMaxIterations(5);
        instance.setPercentToSample(0.5);

        assertNull(instance.getResult());

        ArrayList<InputOutputPair<Vector, Boolean>> data =
            new ArrayList<InputOutputPair<Vector, Boolean>>();
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();

        for (int i = 0; i < 100; i++)
        {
            data.add(new DefaultInputOutputPair<Vector, Boolean>(
                vectorFactory.createUniformRandom(
                    14, 0.0, 1.0, random), true));
        }
        for (int i = 0; i < 2; i++)
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
}
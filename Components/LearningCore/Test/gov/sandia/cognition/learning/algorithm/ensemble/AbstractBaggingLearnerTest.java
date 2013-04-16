/*
 * File:            AbstractBaggingLearnerTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Learning Core
 * 
 * Copyright 2011 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.learning.algorithm.perceptron.Perceptron;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class AbstractBaggingLearner.
 *
 * @author  Justin Basilico
 * @version 3.4.0
 */
public class AbstractBaggingLearnerTest
    extends TestCase
{
    protected Random random;

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public AbstractBaggingLearnerTest(
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
        AbstractBaggingLearner<Vector, Boolean, ?, ?> instance =
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
     * Test of getResult method, of class BaggingCategorizerLearner.
     */
    public void testGetResult()
    {
        // Tested by testLearn.
    }

    /**
     * Test of getLearner method, of class BaggingCategorizerLearner.
     */
    public void testGetLearner()
    {
        this.testSetLearner();
    }

    /**
     * Test of setLearner method, of class BaggingCategorizerLearner.
     */
    public void testSetLearner()
    {
        Perceptron learner = null;
        BaggingCategorizerLearner<Vector, Boolean> instance =
            new BaggingCategorizerLearner<Vector, Boolean>();
        assertSame(learner, instance.getLearner());

        learner = new Perceptron();
        instance.setLearner(learner);
        assertSame(learner, instance.getLearner());

        learner = new Perceptron();
        instance.setLearner(learner);
        assertSame(learner, instance.getLearner());

        learner = null;
        instance.setLearner(learner);
        assertSame(learner, instance.getLearner());

        learner = new Perceptron();
        instance.setLearner(learner);
        assertSame(learner, instance.getLearner());
    }

    /**
     * Test of getPercentToSample method, of class BaggingCategorizerLearner.
     */
    public void testGetPercentToSample()
    {
        this.testSetPercentToSample();
    }

    /**
     * Test of setPercentToSample method, of class BaggingCategorizerLearner.
     */
    public void testSetPercentToSample()
    {
        double percentToSample = BaggingCategorizerLearner.DEFAULT_PERCENT_TO_SAMPLE;
        AbstractBaggingLearner<Vector, Boolean, ?, ?> instance =
            new BaggingCategorizerLearner<Vector, Boolean>();
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);

        percentToSample = 0.99;
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);

        percentToSample = 0.5;
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);

        percentToSample = 0.0001;
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);

        boolean exceptionThrown = false;
        try
        {
            instance.setPercentToSample(0.0);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);


        exceptionThrown = false;
        try
        {
            instance.setPercentToSample(-0.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);

    }

    /**
     * Test of getRandom method, of class BaggingCategorizerLearner.
     */
    public void testGetRandom()
    {
        this.testSetRandom();
    }

    /**
     * Test of setRandom method, of class BaggingCategorizerLearner.
     */
    public void testSetRandom()
    {
        AbstractBaggingLearner<Vector, Boolean, ?, ?> instance =
            new BaggingCategorizerLearner<Vector, Boolean>();
        assertNotNull(instance.getRandom());

        Random random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = new Random(47);
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = null;
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());
    }

}

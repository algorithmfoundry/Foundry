/*
 * File:                IVotingCategorizerLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright November 25, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.factory.Factory;
import gov.sandia.cognition.learning.algorithm.perceptron.Perceptron;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.statistics.distribution.MapBasedDataHistogram;
import gov.sandia.cognition.util.WeightedValue;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class IVotingCategorizerLearner.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class IVotingCategorizerLearnerTest
    extends TestCase
{
    protected Random random;

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public IVotingCategorizerLearnerTest(
        String testName)
    {
        super(testName);

        this.random = new Random();
    }

    /**
     * Test of constructors of class IVotingCategorizerLearner.
     */
    public void testConstructors()
    {
        Perceptron learner = null;
        int maxIterations = IVotingCategorizerLearner.DEFAULT_MAX_ITERATIONS;
        double percentToSample = IVotingCategorizerLearner.DEFAULT_PERCENT_TO_SAMPLE;
        double proportionIncorrectInSample = IVotingCategorizerLearner.DEFAULT_PROPORTION_INCORRECT_IN_SAMPLE;
        boolean voteOutOfBagOnly = IVotingCategorizerLearner.DEFAULT_VOTE_OUT_OF_BAG_ONLY;
        IVotingCategorizerLearner<Vector, Boolean> instance =
            new IVotingCategorizerLearner<Vector, Boolean>();
        assertSame(learner, instance.getLearner());
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(proportionIncorrectInSample, instance.getProportionIncorrectInSample(), 0.0);
        assertEquals(voteOutOfBagOnly, instance.isVoteOutOfBagOnly());
        assertNotNull(instance.getRandom());

        learner = new Perceptron();
        maxIterations = (int) (maxIterations * 10 * random.nextDouble());
        percentToSample = random.nextDouble();
        instance = new IVotingCategorizerLearner<Vector, Boolean>(learner,
            maxIterations, percentToSample, random);
        assertSame(learner, instance.getLearner());
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(proportionIncorrectInSample, instance.getProportionIncorrectInSample(), 0.0);
        assertEquals(voteOutOfBagOnly, instance.isVoteOutOfBagOnly());
        assertSame(random, instance.getRandom());

        proportionIncorrectInSample = random.nextDouble();
        voteOutOfBagOnly = !voteOutOfBagOnly;
        Factory<MapBasedDataHistogram<Boolean>> counterFactory =
            new MapBasedDataHistogram.DefaultFactory<Boolean>();
        instance = new IVotingCategorizerLearner<Vector, Boolean>(learner,
            maxIterations, percentToSample, proportionIncorrectInSample,
            voteOutOfBagOnly, counterFactory, random);
        assertSame(learner, instance.getLearner());
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(proportionIncorrectInSample, instance.getProportionIncorrectInSample(), 0.0);
        assertEquals(voteOutOfBagOnly, instance.isVoteOutOfBagOnly());
        assertSame(counterFactory, instance.getCounterFactory());
        assertSame(random, instance.getRandom());
    }

    /**
     * Test of learn method, of class IVotingCategorizerLearner.
     */
    public void testLearn()
    {
        IVotingCategorizerLearner<Vector, Boolean> instance =
            new IVotingCategorizerLearner<Vector, Boolean>();
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
     * Test of getResult method, of class IVotingCategorizerLearner.
     */
    public void testGetResult()
    {
        // Tested by testLearn.
    }

    /**
     * Test of getLearner method, of class IVotingCategorizerLearner.
     */
    public void testGetLearner()
    {
        this.testSetLearner();
    }

    /**
     * Test of setLearner method, of class IVotingCategorizerLearner.
     */
    public void testSetLearner()
    {
        Perceptron learner = null;
        IVotingCategorizerLearner<Vector, Boolean> instance =
            new IVotingCategorizerLearner<Vector, Boolean>();
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
     * Test of getPercentToSample method, of class IVotingCategorizerLearner.
     */
    public void testGetPercentToSample()
    {
        this.testSetPercentToSample();
    }

    /**
     * Test of setPercentToSample method, of class IVotingCategorizerLearner.
     */
    public void testSetPercentToSample()
    {
        double percentToSample = IVotingCategorizerLearner.DEFAULT_PERCENT_TO_SAMPLE;
        IVotingCategorizerLearner<Vector, Boolean> instance =
            new IVotingCategorizerLearner<Vector, Boolean>();
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);

        percentToSample = random.nextDouble();
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        
        percentToSample = random.nextDouble();
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        
        percentToSample = 1.0;
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);

        // Test to see if values > 1 are allowed.
        percentToSample = 1.1;
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);

        // Try another random value.
        percentToSample = random.nextDouble();
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);

        // Values <= 0.0 are not allowed.
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
     * Test of getProportionIncorrectInSample method, of class IVotingCategorizerLearner.
     */
    public void testGetProportionIncorrectInSample()
    {
        this.testSetProportionIncorrectInSample();
    }

    /**
     * Test of setProportionIncorrectInSample method, of class IVotingCategorizerLearner.
     */
    public void testSetProportionIncorrectInSample()
    {
        double proportionIncorrectInSample = IVotingCategorizerLearner.DEFAULT_PROPORTION_INCORRECT_IN_SAMPLE;
        IVotingCategorizerLearner<Vector, Boolean> instance =
            new IVotingCategorizerLearner<Vector, Boolean>();
        assertEquals(proportionIncorrectInSample, instance.getProportionIncorrectInSample(), 0.0);

        proportionIncorrectInSample = random.nextDouble();
        instance.setProportionIncorrectInSample(proportionIncorrectInSample);
        assertEquals(proportionIncorrectInSample, instance.getProportionIncorrectInSample(), 0.0);

        proportionIncorrectInSample = random.nextDouble();
        instance.setProportionIncorrectInSample(proportionIncorrectInSample);
        assertEquals(proportionIncorrectInSample, instance.getProportionIncorrectInSample(), 0.0);

        proportionIncorrectInSample = 0.0;
        instance.setProportionIncorrectInSample(proportionIncorrectInSample);
        assertEquals(proportionIncorrectInSample, instance.getProportionIncorrectInSample(), 0.0);

        proportionIncorrectInSample = 1.0;
        instance.setProportionIncorrectInSample(proportionIncorrectInSample);
        assertEquals(proportionIncorrectInSample, instance.getProportionIncorrectInSample(), 0.0);

        proportionIncorrectInSample = random.nextDouble();
        instance.setProportionIncorrectInSample(proportionIncorrectInSample);
        assertEquals(proportionIncorrectInSample, instance.getProportionIncorrectInSample(), 0.0);

        // Values < 0.0 are not allowed.
        boolean exceptionThrown = false;
        try
        {
            instance.setProportionIncorrectInSample(-0.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(proportionIncorrectInSample, instance.getProportionIncorrectInSample(), 0.0);

        // Values > 1.0 are not allowed.
        exceptionThrown = false;
        try
        {
            instance.setProportionIncorrectInSample(1.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(proportionIncorrectInSample, instance.getProportionIncorrectInSample(), 0.0);

    }

    /**
     * Test of isVoteOutOfBagOnly method, of class IVotingCategorizerLearner.
     */
    public void testIsVoteOutOfBagOnly()
    {
        this.testSetVoteOutOfBagOnly();
    }

    /**
     * Test of setVoteOutOfBagOnly method, of class IVotingCategorizerLearner.
     */
    public void testSetVoteOutOfBagOnly()
    {

        boolean voteOutOfBagOnly = IVotingCategorizerLearner.DEFAULT_VOTE_OUT_OF_BAG_ONLY;
        IVotingCategorizerLearner<Vector, Boolean> instance =
            new IVotingCategorizerLearner<Vector, Boolean>();
        assertEquals(voteOutOfBagOnly, instance.isVoteOutOfBagOnly());

        voteOutOfBagOnly = false;
        instance.setVoteOutOfBagOnly(voteOutOfBagOnly);
        assertEquals(voteOutOfBagOnly, instance.isVoteOutOfBagOnly());

        voteOutOfBagOnly = true;
        instance.setVoteOutOfBagOnly(voteOutOfBagOnly);
        assertEquals(voteOutOfBagOnly, instance.isVoteOutOfBagOnly());
    }

    /**
     * Test of getRandom method, of class IVotingCategorizerLearner.
     */
    public void testGetRandom()
    {
        this.testSetRandom();
    }

    /**
     * Test of setRandom method, of class IVotingCategorizerLearner.
     */
    public void testSetRandom()
    {
        IVotingCategorizerLearner instance = new IVotingCategorizerLearner();
        assertNotNull(instance.getRandom());

        Random random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = new Random();
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

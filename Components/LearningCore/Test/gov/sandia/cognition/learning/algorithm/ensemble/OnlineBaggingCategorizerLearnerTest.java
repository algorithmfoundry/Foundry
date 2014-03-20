/*
 * File:                OnlineBaggingCategorizerLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright March 23, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.ensemble;

import gov.sandia.cognition.learning.algorithm.ensemble.OnlineBaggingCategorizerLearner;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.ArrayList;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.learning.algorithm.perceptron.OnlinePerceptron;
import gov.sandia.cognition.learning.algorithm.ensemble.BaggingCategorizerLearner;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.learning.algorithm.ensemble.VotingCategorizerEnsemble;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class OnlineBaggingCategorizerLearner.
 *
 * @author  Justin Basilico
 * @since   3.1.1
 */
public class OnlineBaggingCategorizerLearnerTest
{
    protected Random random = new Random(211);

    /**
     * Creates a new test.
     */
    public OnlineBaggingCategorizerLearnerTest()
    {
        super();
    }

    /**
     * Test of constructors of class BaggingCategorizerLearner.
     */
    public void testConstructors()
    {
        OnlinePerceptron learner = null;
        double percentToSample = BaggingCategorizerLearner.DEFAULT_PERCENT_TO_SAMPLE;
        int ensembleSize = OnlineBaggingCategorizerLearner.DEFAULT_ENSEMBLE_SIZE;
        OnlineBaggingCategorizerLearner<Vector, Boolean, ?> instance =
            new OnlineBaggingCategorizerLearner<Vector, Boolean, LinearBinaryCategorizer>();
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(ensembleSize, instance.getEnsembleSize());
        assertNotNull(instance.getRandom());

        learner = new OnlinePerceptron();
        instance = new OnlineBaggingCategorizerLearner<Vector, Boolean, LinearBinaryCategorizer>(learner);
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(ensembleSize, instance.getEnsembleSize());
        assertNotNull(instance.getRandom());

        percentToSample = percentToSample / 3.4;
        ensembleSize = ensembleSize * 9;
        instance = new OnlineBaggingCategorizerLearner<Vector, Boolean, LinearBinaryCategorizer>(
            learner, ensembleSize, percentToSample, random);
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(ensembleSize, instance.getEnsembleSize());
        assertSame(random, instance.getRandom());
    }

    /**
     * Test of createInitialLearnedObject method, of class OnlineBaggingCategorizerLearner.
     */
    @Test
    public void testCreateInitialLearnedObject()
    {
        OnlinePerceptron learner = new OnlinePerceptron();
        double percentToSample = random.nextDouble();
        int ensembleSize = 1 + random.nextInt(100);
        OnlineBaggingCategorizerLearner<Vectorizable, Boolean, LinearBinaryCategorizer> instance =
            OnlineBaggingCategorizerLearner.create(
                learner, ensembleSize, percentToSample, random);

        VotingCategorizerEnsemble<Vectorizable, Boolean, LinearBinaryCategorizer> result =
            instance.createInitialLearnedObject();
        assertEquals(ensembleSize, result.getMembers().size());
        for (int i = 0; i < result.getMembers().size(); i++)
        {
            LinearBinaryCategorizer member = result.getMembers().get(i);

            for (int j = i + 1; j < result.getMembers().size(); j++)
            {
                assertNotSame(member, result.getMembers().get(j));
            }
        }
    }

    /**
     * Test of update method, of class OnlineBaggingCategorizerLearner.
     */
    @Test
    public void testUpdate()
    {
        ArrayList<InputOutputPair<Vector, Boolean>> data =
            new ArrayList<InputOutputPair<Vector, Boolean>>();
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();


        OnlinePerceptron learner = new OnlinePerceptron();
        double percentToSample = 0.5;
        int ensembleSize = 5;
        OnlineBaggingCategorizerLearner<Vectorizable, Boolean, LinearBinaryCategorizer> instance =
            OnlineBaggingCategorizerLearner.create(
                learner, ensembleSize, percentToSample, random);


        VotingCategorizerEnsemble<Vectorizable, Boolean, LinearBinaryCategorizer> result =
            instance.createInitialLearnedObject();


        for (int i = 0; i < 25; i++)
        {
            boolean actual = random.nextDouble() > 0.25;

            Vector input;
            if (actual)
            {
                input = vectorFactory.createUniformRandom(4, -1.0, 0.0, random);
            }
            else
            {
                input = vectorFactory.createUniformRandom(4, 0.0, +1.0, random);
            }

            instance.update(result, DefaultInputOutputPair.create(input, actual));
        }

        assertEquals(5, result.getMembers().size());
        LinearBinaryCategorizer first = result.getMembers().get(0);
        boolean allEqual = true;
        for (int i = 0; i < result.getMembers().size(); i++)
        {
            LinearBinaryCategorizer member = result.getMembers().get(i);

            for (int j = i + 1; j < result.getMembers().size(); j++)
            {
                assertNotSame(member, result.getMembers().get(j));
            }

            assertTrue((member.getWeights() == null)
                || (member.getWeights().norm1() != 0.0));
            
            allEqual &= ObjectUtil.equalsSafe(member.getWeights(), first.getWeights());
        }
        assertFalse(allEqual);


        int testCount = 1000;
        int testCorrect = 0;
        for (int i = 0; i < testCount; i++)
        {
            boolean actual = random.nextDouble() > 0.25;

            Vector input;
            if (actual)
            {
                input = vectorFactory.createUniformRandom(4, -1.0, 0.0, random);
            }
            else
            {
                input = vectorFactory.createUniformRandom(4, 0.0, +1.0, random);
            }

            boolean predicted = result.evaluate(input);
            if (predicted == actual)
            {
                testCorrect++;
            }
        }
        double accuracy = (double) testCorrect / testCount;
        assertTrue(accuracy > 0.95);
    }

    /**
     * Test of getLearner method, of class OnlineBaggingCategorizerLearner.
     */
    @Test
    public void testGetLearner()
    {
        this.testSetLearner();
    }

    /**
     * Test of setLearner method, of class OnlineBaggingCategorizerLearner.
     */
    @Test
    public void testSetLearner()
    {
        OnlinePerceptron learner = null;
        OnlineBaggingCategorizerLearner<Vector, Boolean, LinearBinaryCategorizer> instance =
            new OnlineBaggingCategorizerLearner<Vector, Boolean, LinearBinaryCategorizer>();
        assertSame(learner, instance.getLearner());

        learner = new OnlinePerceptron();
        instance.setLearner(learner);
        assertSame(learner, instance.getLearner());

        learner = null;
        instance.setLearner(learner);
        assertSame(learner, instance.getLearner());
        
        learner = new OnlinePerceptron();
        instance.setLearner(learner);
        assertSame(learner, instance.getLearner());
    }

    /**
     * Test of getEnsembleSize method, of class OnlineBaggingCategorizerLearner.
     */
    @Test
    public void testGetEnsembleSize()
    {
        this.testSetEnsembleSize();
    }

    /**
     * Test of setEnsembleSize method, of class OnlineBaggingCategorizerLearner.
     */
    @Test
    public void testSetEnsembleSize()
    {
        int ensembleSize = OnlineBaggingCategorizerLearner.DEFAULT_ENSEMBLE_SIZE;
        OnlineBaggingCategorizerLearner<Vector, Boolean, ?> instance =
            new OnlineBaggingCategorizerLearner<Vector, Boolean, LinearBinaryCategorizer>();
        assertEquals(ensembleSize, instance.getEnsembleSize());

        int[] goodValues = {1, 2, 10, random.nextInt(1000) + 1};
        for (int goodValue : goodValues)
        {
            ensembleSize = goodValue;
            instance.setEnsembleSize(ensembleSize);
            assertEquals(ensembleSize, instance.getEnsembleSize());
        }

        int[] badValues = {0, -1, -random.nextInt(1000)};
        for (int badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setEnsembleSize(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(ensembleSize, instance.getEnsembleSize());
        }
    }

    /**
     * Test of getPercentToSample method, of class OnlineBaggingCategorizerLearner.
     */
    @Test
    public void testGetPercentToSample()
    {
        this.testSetPercentToSample();
    }

    /**
     * Test of setPercentToSample method, of class OnlineBaggingCategorizerLearner.
     */
    @Test
    public void testSetPercentToSample()
    {
        double percentToSample = OnlineBaggingCategorizerLearner.DEFAULT_PERCENT_TO_SAMPLE;
        OnlineBaggingCategorizerLearner<Vector, Boolean, ?> instance =
            new OnlineBaggingCategorizerLearner<Vector, Boolean, LinearBinaryCategorizer>();
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);

        double[] goodValues = {0.1, 1.0, 0.5, 10.0, random.nextDouble()};
        for (double goodValue : goodValues)
        {
            percentToSample = goodValue;
            instance.setPercentToSample(percentToSample);
            assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        }

        double[] badValues = {0.0, -0.1, -1.0, -10.0, -random.nextDouble()};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setPercentToSample(badValue);
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
    }

    /**
     * Test of getRandom method, of class OnlineBaggingCategorizerLearner.
     */
    @Test
    public void testGetRandom()
    {
        this.testSetRandom();
    }

    /**
     * Test of setRandom method, of class OnlineBaggingCategorizerLearner.
     */
    @Test
    public void testSetRandom()
    {
        Random random = null;
        OnlineBaggingCategorizerLearner<Vector, Boolean, ?> instance =
            new OnlineBaggingCategorizerLearner<Vector, Boolean, LinearBinaryCategorizer>();
        assertNotNull(instance.getRandom());

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

    /**
     * Test of create method, of class OnlineBaggingCategorizerLearner.
     */
    @Test
    public void testCreate()
    {
        OnlinePerceptron learner = new OnlinePerceptron();
        double percentToSample = random.nextDouble();
        int ensembleSize = 1 + random.nextInt(1000);
        OnlineBaggingCategorizerLearner<Vector, Boolean, ?> instance = OnlineBaggingCategorizerLearner.create(
            learner, ensembleSize, percentToSample, random);
        assertSame(learner, instance.getLearner());
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);
        assertEquals(ensembleSize, instance.getEnsembleSize());
        assertSame(random, instance.getRandom());
    }

}
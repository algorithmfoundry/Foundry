/*
 * File:                ConfidenceWeightedDiagonalVarianceTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 12, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.confidence;

import gov.sandia.cognition.learning.function.categorization.DiagonalConfidenceWeightedBinaryCategorizer;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.Random;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.ArrayList;
import gov.sandia.cognition.math.matrix.Vector;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class ConfidenceWeightedDiagonalVariance.
 *
 * @author  Justin Basilico
 * @since   3.3.0
 */
public class ConfidenceWeightedDiagonalVarianceTest
{
    protected Random random = new Random(211);
    
    /**
     * Creates a new test.
     */
    public ConfidenceWeightedDiagonalVarianceTest()
    {
    }

    /**
     * Test of constructors of class ConfidenceWeightedDiagonalVariance.
     */
    @Test
    public void testConstructors()
    {

        double confidence = ConfidenceWeightedDiagonalVariance.DEFAULT_CONFIDENCE;
        double defaultVariance = ConfidenceWeightedDiagonalVariance.DEFAULT_DEFAULT_VARIANCE;
        ConfidenceWeightedDiagonalVariance instance = new ConfidenceWeightedDiagonalVariance();
        assertEquals(confidence, instance.getConfidence(), 0.0);
        assertEquals(defaultVariance, instance.getDefaultVariance(), 0.0);

        confidence = random.nextDouble();
        defaultVariance = random.nextDouble();
        instance = new ConfidenceWeightedDiagonalVariance(confidence, defaultVariance);
        assertEquals(confidence, instance.getConfidence(), 0.0);
        assertEquals(defaultVariance, instance.getDefaultVariance(), 0.0);
    }

    /**
     * Test of createInitialLearnedObject method, of class ConfidenceWeightedDiagonalVariance.
     */
    @Test
    public void testCreateInitialLearnedObject()
    {
        ConfidenceWeightedDiagonalVariance instance = new ConfidenceWeightedDiagonalVariance();
        DiagonalConfidenceWeightedBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNotNull(result);
        assertNotSame(result, instance.createInitialLearnedObject());
    }

    /**
     * Test of update method, of class ConfidenceWeightedDiagonalVariance.
     */
    @Test
    public void testUpdate()
    {
        ConfidenceWeightedDiagonalVariance instance = new ConfidenceWeightedDiagonalVariance();
        DiagonalConfidenceWeightedBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getMean());
        assertNull(result.getVariance());
        assertEquals(0.0, result.getBias(), 0.0);

        Vector input = new Vector2(2.0, 3.0);
        Boolean output = true;
        instance.update(result, DefaultInputOutputPair.create(input, output));
        assertEquals(output, result.evaluate(input));

        input = new Vector2(4.0, 4.0);
        output = true;
        instance.update(result, DefaultInputOutputPair.create(input, output));
        assertEquals(output, result.evaluate(input));

        input = new Vector2(1.0, 1.0);
        output = false;
        instance.update(result, DefaultInputOutputPair.create(input, output));
        assertEquals(output, result.evaluate(input));

        input = new Vector2(1.0, 1.0);
        output = false;
        instance.update(result, DefaultInputOutputPair.create(input, output));
        assertEquals(output, result.evaluate(input));

        input = new Vector2(2.0, 3.0);
        output = true;
        instance.update(result, DefaultInputOutputPair.create(input, output));
        assertEquals(output, result.evaluate(input));


        result = instance.createInitialLearnedObject();

        MultivariateGaussian positive = new MultivariateGaussian(2);
        positive.setMean(new Vector2(1.0, 1.0));
        positive.getCovariance().setElement(0, 0, 0.2);
        positive.getCovariance().setElement(1, 1, 2.0);

        MultivariateGaussian negative = new MultivariateGaussian(2);
        negative.setMean(new Vector2(-1.0, -1.0));
        negative.getCovariance().setElement(0, 0, 0.2);
        negative.getCovariance().setElement(1, 1, 2.0);

        for (int i = 0; i < 4000; i++)
        {
            output = random.nextBoolean();
            input = (output ? positive : negative).sample(random);

            Vector oldWeights = ObjectUtil.cloneSafe(result.getWeights());
            double prediction = result.evaluateAsDouble(input);
            assertEquals(prediction >= 0.0, result.evaluateAsBernoulli(input).getP() >= 0.5);

            instance.update(result, DefaultInputOutputPair.create(input, output));
            assertEquals(output, result.evaluate(input));

        }
    }

    /**
     * Test of getConfidence method, of class ConfidenceWeightedDiagonalVariance.
     */
    @Test
    public void testGetConfidence()
    {
        this.testSetConfidence();
    }

    /**
     * Test of setConfidence method, of class ConfidenceWeightedDiagonalVariance.
     */
    @Test
    public void testSetConfidence()
    {
        double confidence = ConfidenceWeightedDiagonalVariance.DEFAULT_CONFIDENCE;
        ConfidenceWeightedDiagonalVariance instance = new ConfidenceWeightedDiagonalVariance();
        instance.setConfidence(confidence);
        assertEquals(confidence, instance.getConfidence(), 0.0);

        double[] goodValues = { 0.5, 0.9, 0.95, 0.99, 0.999, 0.0, 1.0, random.nextDouble() };
        for (double goodValue : goodValues)
        {
            confidence = goodValue;
            instance.setConfidence(confidence);
            assertEquals(confidence, instance.getConfidence(), 0.0);
        }

        double[] badValues = { 1.1, -0.1, -1.0, -1.1, -random.nextDouble() };
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setConfidence(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(confidence, instance.getConfidence(), 0.0);
        }
    }

    /**
     * Test of getDefaultVariance method, of class ConfidenceWeightedDiagonalVariance.
     */
    @Test
    public void testGetDefaultVariance()
    {
        this.testSetDefaultVariance();
    }

    /**
     * Test of setDefaultVariance method, of class ConfidenceWeightedDiagonalVariance.
     */
    @Test
    public void testSetDefaultVariance()
    {
        double defaultVariance = ConfidenceWeightedDiagonalVariance.DEFAULT_DEFAULT_VARIANCE;
        ConfidenceWeightedDiagonalVariance instance = new ConfidenceWeightedDiagonalVariance();
        instance.setDefaultVariance(defaultVariance);
        assertEquals(defaultVariance, instance.getDefaultVariance(), 0.0);

        double[] goodValues = { 1e-10, 0.1, 0.5, 1.0, 10.0, random.nextDouble() };
        for (double goodValue : goodValues)
        {
            defaultVariance = goodValue;
            instance.setDefaultVariance(defaultVariance);
            assertEquals(defaultVariance, instance.getDefaultVariance(), 0.0);
        }

        double[] badValues = { 0.0, -0.1, -1.0, -1.1, -random.nextDouble() };
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setDefaultVariance(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(defaultVariance, instance.getDefaultVariance(), 0.0);
        }
    }



    /**
     * Test learning a linearly separable example.
     */
    @Test
    public void testLearnSeparable()
    {
        System.out.println("testLearnSeparable");
        int d = 10;
        int trainCount = 1000;
        int testCount = 100;
        LinearBinaryCategorizer real = new LinearBinaryCategorizer(
            VectorFactory.getDenseDefault().createUniformRandom(d, -1, +1, random), 0.0);

        ArrayList<InputOutputPair<Vector, Boolean>> trainData =
            new ArrayList<InputOutputPair<Vector, Boolean>>(trainCount);
        for (int i = 0; i < trainCount; i++)
        {
            Vector input = VectorFactory.getDenseDefault().createUniformRandom(
                d, -1, +1, random);
            boolean output = real.evaluate(input);
            trainData.add(DefaultInputOutputPair.create(input, output));
        }

        ArrayList<InputOutputPair<Vector, Boolean>> testData =
            new ArrayList<InputOutputPair<Vector, Boolean>>(testCount);
        for (int i = 0; i < testCount; i++)
        {
            Vector input = VectorFactory.getDenseDefault().createUniformRandom(
                d, -1, +1, random);
            boolean actual = real.evaluate(input);
            testData.add(DefaultInputOutputPair.create(input, actual));
        }

        ConfidenceWeightedDiagonalVariance instance = new ConfidenceWeightedDiagonalVariance();
        DiagonalConfidenceWeightedBinaryCategorizer learned = instance.createInitialLearnedObject();

        for (InputOutputPair<Vector, Boolean> example : trainData)
        {
            instance.update(learned, example);
        }

        int correctCount = 0;
        for (InputOutputPair<Vector, Boolean> example : testData)
        {
            boolean actual = example.getOutput();
            boolean predicted = learned.evaluate(example.getInput());
            if (actual == predicted)
            {
                correctCount++;
            }
        }
        double accuracy = (double) correctCount / testData.size();
        System.out.println("Accuracy: " + accuracy);
        assertTrue(accuracy >= 0.95);

        double cosine = learned.getMean().unitVector().cosine(real.getWeights().unitVector());
        System.out.println("Cosine: " + cosine);
        assertTrue(cosine >= 0.95);

    }


}
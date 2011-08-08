/*
 * File:                OnlinePassiveAggressivePerceptronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright January 25, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class OnlinePassiveAggressivePerceptron.
 *
 * @author  Justin Basilico
 * @since   3.1.1
 */
public class OnlinePassiveAggressivePerceptronTest
    extends TestCase
{

    protected Random random = new Random(211);
    
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public OnlinePassiveAggressivePerceptronTest(
        String testName)
    {
        super(testName);
    }


    /**
     * Test of constructors of class OnlinePassiveAggressivePerceptron.
     */
    public void testConstructors()
    {
        VectorFactory<?> factory = VectorFactory.getDefault();
        OnlinePassiveAggressivePerceptron instance = new OnlinePassiveAggressivePerceptron();
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        factory = new SparseVectorFactoryMTJ();
        instance = new OnlinePassiveAggressivePerceptron(factory);
        assertSame(factory, instance.getVectorFactory());
    }


    /**
     * Test of createInitialLearnedObject method, of class OnlinePassiveAggressivePerceptron.
     */
    public void testCreateInitialLearnedObject()
    {
        OnlinePassiveAggressivePerceptron instance = new OnlinePassiveAggressivePerceptron();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias());
        assertNotSame(result, instance.createInitialLearnedObject());
    }

    /**
     * Test learning a linearly separable example.
     */
    public void testLearnSeparable()
    {
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

        OnlinePassiveAggressivePerceptron instance = new OnlinePassiveAggressivePerceptron();
        LinearBinaryCategorizer learned = instance.createInitialLearnedObject();

        for (InputOutputPair<Vector, Boolean> example : trainData)
        {
            instance.update(learned, example);
            assertEquals(example.getOutput(), learned.evaluate(example.getInput()));
        }


        double cosine = learned.getWeights().unitVector().cosine(real.getWeights().unitVector());
        assertTrue(cosine > 0.99);

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
        assertTrue(accuracy > 0.95);

    }

    /**
     * Test of update method, of class OnlinePassiveAggressivePerceptron.
     */
    public void testUpdate()
    {
        OnlinePassiveAggressivePerceptron instance = new OnlinePassiveAggressivePerceptron();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias());

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

            double loss = 1.0 - prediction * (output ? +1 : -1);
            instance.update(result, DefaultInputOutputPair.create(input, output));
            assertEquals(output, result.evaluate(input));
            if (loss <= 0.0)
            {
                assertEquals(oldWeights, result.getWeights());
            }
        }
    }

    /**
     * Test of getVectorFactory method, of class OnlinePassiveAggressivePerceptron.
     */
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class OnlinePassiveAggressivePerceptron.
     */
    public void testSetVectorFactory()
    {
        VectorFactory<?> factory = VectorFactory.getDefault();
        OnlinePassiveAggressivePerceptron instance = new OnlinePassiveAggressivePerceptron();
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        factory = new SparseVectorFactoryMTJ();
        instance.setVectorFactory(factory);
        assertSame(factory, instance.getVectorFactory());

        factory = null;
        instance.setVectorFactory(factory);
        assertSame(factory, instance.getVectorFactory());

        factory = VectorFactory.getDenseDefault();
        instance.setVectorFactory(factory);
        assertSame(factory, instance.getVectorFactory());
    }
    
    /**
     * Test of update method, of class OnlinePassiveAggressivePerceptron.
     */
    public void testLinearSoftMargin()
    {
        OnlinePassiveAggressivePerceptron instance = new OnlinePassiveAggressivePerceptron.LinearSoftMargin();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias());

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
            Boolean output = random.nextBoolean();
            Vector input = (output ? positive : negative).sample(random);

            Vector oldWeights = ObjectUtil.cloneSafe(result.getWeights());
            double prediction = result.evaluateAsDouble(input);

            double loss = 1.0 - prediction * (output ? +1 : -1);
            instance.update(result, DefaultInputOutputPair.create(input, output));
            
            if (loss <= 0.0)
            {
                assertEquals(oldWeights, result.getWeights());
            }
        }
    }

    public void testLinearSoftMarginOnLinearlySeparable()
    {
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

        OnlinePassiveAggressivePerceptron instance = new OnlinePassiveAggressivePerceptron.LinearSoftMargin();
        LinearBinaryCategorizer learned = instance.createInitialLearnedObject();

        for (InputOutputPair<Vector, Boolean> example : trainData)
        {
            instance.update(learned, example);
        }


        double cosine = learned.getWeights().unitVector().cosine(real.getWeights().unitVector());
        assertTrue(cosine > 0.99);

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
        assertTrue(accuracy > 0.95);
    }
    
    /**
     * Test of update method, of class OnlinePassiveAggressivePerceptron.
     */
    public void testQuadraticSoftMargin()
    {
        OnlinePassiveAggressivePerceptron instance = new OnlinePassiveAggressivePerceptron.QuadraticSoftMargin();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias());

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
            Boolean output = random.nextBoolean();
            Vector input = (output ? positive : negative).sample(random);

            Vector oldWeights = ObjectUtil.cloneSafe(result.getWeights());
            double prediction = result.evaluateAsDouble(input);

            double loss = 1.0 - prediction * (output ? +1 : -1);
            instance.update(result, DefaultInputOutputPair.create(input, output));

            if (loss <= 0.0)
            {
                assertEquals(oldWeights, result.getWeights());
            }
        }
    }

    public void testQuadraticSoftMarginOnLinearlySeparable()
    {
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

        OnlinePassiveAggressivePerceptron instance = new OnlinePassiveAggressivePerceptron.QuadraticSoftMargin();
        LinearBinaryCategorizer learned = instance.createInitialLearnedObject();

        for (InputOutputPair<Vector, Boolean> example : trainData)
        {
            instance.update(learned, example);
        }


        double cosine = learned.getWeights().unitVector().cosine(real.getWeights().unitVector());
        assertTrue(cosine > 0.99);

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
        assertTrue(accuracy > 0.95);
    }

}

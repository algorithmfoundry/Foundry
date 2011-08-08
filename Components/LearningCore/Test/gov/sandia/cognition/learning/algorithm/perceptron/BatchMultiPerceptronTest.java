/*
 * File:                BatchMultiPerceptronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.ArrayList;
import java.util.Random;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.learning.function.categorization.LinearMultiCategorizer;
import gov.sandia.cognition.math.matrix.VectorFactory;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class BatchMultiPerceptron.
 *
 * @author  Justin Basilico
 * @since   3.2.0
 */
public class BatchMultiPerceptronTest
{
    /** Random number generator. */
    protected Random random = new Random(211);

    /**
     * Creates a new test.
     */
    public BatchMultiPerceptronTest()
    {
    }

    /**
     * Test of constructors of class BatchMultiPerceptron.
     */
    @Test
    public void testConstructors()
    {
        int maxIterations = BatchMultiPerceptron.DEFAULT_MAX_ITERATIONS;
        double minMargin = BatchMultiPerceptron.DEFAULT_MIN_MARGIN;
        VectorFactory<?> vectorFactory = null;
        BatchMultiPerceptron<String> instance = new BatchMultiPerceptron<String>();
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(minMargin, instance.getMinMargin(), 0.0);
        assertNotNull(instance.getVectorFactory());

        maxIterations = 1 + random.nextInt(10000);
        instance = new BatchMultiPerceptron<String>(maxIterations);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(minMargin, instance.getMinMargin(), 0.0);
        assertNotNull(instance.getVectorFactory());

        minMargin = 10.0 * random.nextDouble();
        instance = new BatchMultiPerceptron<String>(maxIterations, minMargin);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(minMargin, instance.getMinMargin(), 0.0);
        assertNotNull(instance.getVectorFactory());

        vectorFactory = new SparseVectorFactoryMTJ();
        instance = new BatchMultiPerceptron<String>(maxIterations, minMargin,
            vectorFactory);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(minMargin, instance.getMinMargin(), 0.0);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of learn method, of class BatchMultiPerceptron.
     */
    @Test
    public void testLearn()
    {
        System.out.println("testLearn");
        BatchMultiPerceptron<String> instance = new BatchMultiPerceptron<String>();
        LinearMultiCategorizer<String> learned = instance.learn(null);
        assertNull(learned);
        
        instance.learn(new ArrayList<InputOutputPair<Vector, String>>());
        assertNull(learned);

        int d = 10;
        int trainCount = 1000;
        int testCount = 100;
        String[] categories = { "a", "b", "c" };
        LinearMultiCategorizer<String> real = new LinearMultiCategorizer<String>();
        for (String category : categories)
        {
            real.getPrototypes().put(category,
                new LinearBinaryCategorizer(VectorFactory.getDenseDefault().createUniformRandom(
                        d, -1, +1,random),
                    0.0));
        }

        ArrayList<InputOutputPair<Vector, String>> trainData =
            new ArrayList<InputOutputPair<Vector, String>>(trainCount);
        for (int i = 0; i < trainCount; i++)
        {
            Vector input = VectorFactory.getDenseDefault().createUniformRandom(
                d, -1, +1, random);
            String output = real.evaluate(input);
            trainData.add(DefaultInputOutputPair.create(input, output));
        }

        ArrayList<InputOutputPair<Vector, String>> testData =
            new ArrayList<InputOutputPair<Vector, String>>(testCount);
        for (int i = 0; i < testCount; i++)
        {
            Vector input = VectorFactory.getDenseDefault().createUniformRandom(
                d, -1, +1, random);
            String actual = real.evaluate(input);
            testData.add(DefaultInputOutputPair.create(input, actual));
        }

        learned = instance.learn(trainData);
        int correctCount = 0;
        for (InputOutputPair<Vector, String> example : testData)
        {
            String actual = example.getOutput();
            String predicted = learned.evaluate(example.getInput());

            if (actual.equals(predicted))
            {
                correctCount++;
            }
        }
        double accuracy = (double) correctCount / testData.size();
        System.out.println("Accuracy: " + accuracy);
        assertTrue(accuracy >= 0.95);
    }

    /**
     * Test of learn method, of class BatchMultiPerceptron.
     */
    @Test
    public void testLearnBinarySeparable()
    {
        System.out.println("testLearnBinarySeparable");
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

        BatchMultiPerceptron<Boolean> instance = new BatchMultiPerceptron<Boolean>();
        LinearMultiCategorizer<Boolean> learned = instance.learn(trainData);

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

        double cosine = learned.getPrototypes().get(true).getWeights().unitVector().cosine(
            real.getWeights().unitVector());
        System.out.println("Cosine: " + cosine);
        assertTrue(cosine >= 0.95);
    }


    /**
     * Test of getResult method, of class BatchMultiPerceptron.
     */
    @Test
    public void testGetResult()
    {
        LinearMultiCategorizer<String> result = null;
        BatchMultiPerceptron<String> instance = new BatchMultiPerceptron<String>();
        assertSame(result, instance.getResult());

        result = new LinearMultiCategorizer<String>();
        instance.setResult(result);
        assertSame(result, instance.getResult());
    }

    /**
     * Test of getMinMargin method, of class BatchMultiPerceptron.
     */
    @Test
    public void testGetMinMargin()
    {
        this.testSetMinMargin();
    }

    /**
     * Test of setMinMargin method, of class BatchMultiPerceptron.
     */
    @Test
    public void testSetMinMargin()
    {
        double minMargin = BatchMultiPerceptron.DEFAULT_MIN_MARGIN;
        BatchMultiPerceptron<String> instance = new BatchMultiPerceptron<String>();
        assertEquals(minMargin, instance.getMinMargin(), 0.0);

        double[] goodValues = { 0.0, 0.1, 1.0, 104.1 };
        for (double goodValue : goodValues)
        {
            minMargin = goodValue;
            instance.setMinMargin(minMargin);
            assertEquals(minMargin, instance.getMinMargin(), 0.0);
        }

        double[] badValues = { -0.1, -0.5, -1.0, -2.0 };
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setMinMargin(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(minMargin, instance.getMinMargin(), 0.0);
        }
    }

    /**
     * Test of getVectorFactory method, of class BatchMultiPerceptron.
     */
    @Test
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class BatchMultiPerceptron.
     */
    @Test
    public void testSetVectorFactory()
    {
        BatchMultiPerceptron<String> instance = new BatchMultiPerceptron<String>();
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        VectorFactory<?> vectorFactory = new SparseVectorFactoryMTJ();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = null;
        instance.setVectorFactory(vectorFactory);
        assertNull(instance.getVectorFactory());

        vectorFactory = VectorFactory.getDenseDefault();
        assertNull(instance.getVectorFactory());
    }

    /**
     * Test of getErrorCount method, of class BatchMultiPerceptron.
     */
    @Test
    public void testGetErrorCount()
    {
        int errorCount = 0;
        BatchMultiPerceptron<String> instance = new BatchMultiPerceptron<String>();
        assertEquals(errorCount, instance.getErrorCount());

        errorCount = 40;
        instance.setErrorCount(errorCount);
        assertEquals(errorCount, instance.getErrorCount());
    }

    /**
     * Test of getPerformance method, of class BatchMultiPerceptron.
     */
    @Test
    public void testGetPerformance()
    {
        int errorCount = 0;
        BatchMultiPerceptron<String> instance = new BatchMultiPerceptron<String>();
        assertEquals(errorCount, instance.getPerformance().getValue(), 0.0);

        errorCount = 40;
        instance.setErrorCount(errorCount);
        assertEquals(errorCount, instance.getPerformance().getValue(), 0.0);
    }

}
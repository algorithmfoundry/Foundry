/*
 * File:            FactorizationMachineStochasticGradientTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2013 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.factor.machine;

import gov.sandia.cognition.algorithm.IterativeAlgorithm;
import gov.sandia.cognition.algorithm.event.AbstractIterativeAlgorithmListener;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.performance.MeanSquaredErrorEvaluator;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@link FactorizationMachineStochasticGradient}.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class FactorizationMachineStochasticGradientTest
    extends Object
{
    protected final NumberFormat NUMBER_FORMAT = new DecimalFormat("0.0000");
    protected Random random = new Random(47474747);
    
    /**
     * Creates a new test.
     */
    public FactorizationMachineStochasticGradientTest()
    {
        super();
    }
    
    /**
     * Test of constructors of class FactorizationMachineStochasticGradient.
     */
    @Test
    public void testConstructors()
    {
        int factorCount = FactorizationMachineStochasticGradient.DEFAULT_FACTOR_COUNT;
        double learningRate = FactorizationMachineStochasticGradient.DEFAULT_LEARNING_RATE;
        double biasRegularization = FactorizationMachineStochasticGradient.DEFAULT_BIAS_REGULARIZATION;
        double weightRegularization = FactorizationMachineStochasticGradient.DEFAULT_WEIGHT_REGULARIZATION;
        double factorRegularization = FactorizationMachineStochasticGradient.DEFAULT_FACTOR_REGULARIZATION;
        double seedScale = FactorizationMachineStochasticGradient.DEFAULT_SEED_SCALE;
        int maxIterations = FactorizationMachineStochasticGradient.DEFAULT_MAX_ITERATIONS;
        FactorizationMachineStochasticGradient instance =
            new FactorizationMachineStochasticGradient();
        assertEquals(factorCount, instance.getFactorCount());
        assertEquals(learningRate, instance.getLearningRate(), 0.0);
        assertEquals(biasRegularization, instance.getBiasRegularization(), 0.0);
        assertEquals(weightRegularization, instance.getWeightRegularization(), 0.0);
        assertEquals(factorRegularization, instance.getFactorRegularization(), 0.0);
        assertEquals(seedScale, instance.getSeedScale(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertNotNull(instance.getRandom());
        assertSame(instance.getRandom(), instance.getRandom());
        
        factorCount = 22;
        learningRate = 0.12321;
        biasRegularization = 3.33;
        weightRegularization = 44.44;
        factorRegularization = 555.55;
        seedScale = 0.6;
        maxIterations = 777;
        Random random = new Random();
        instance = new FactorizationMachineStochasticGradient(factorCount,
            learningRate, biasRegularization, weightRegularization, factorRegularization,
            seedScale, maxIterations, random);
        assertEquals(factorCount, instance.getFactorCount());
        assertEquals(biasRegularization, instance.getBiasRegularization(), 0.0);
        assertEquals(weightRegularization, instance.getWeightRegularization(), 0.0);
        assertEquals(factorRegularization, instance.getFactorRegularization(), 0.0);
        assertEquals(seedScale, instance.getSeedScale(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertSame(random, instance.getRandom());
        
        // No negative factor counts.
        boolean exceptionThrown = false;
        try
        {
            instance = new FactorizationMachineStochasticGradient(-1, learningRate,
                biasRegularization, weightRegularization, factorRegularization,
                seedScale, maxIterations, random);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        
        // No zero learning rate.
        exceptionThrown = false;
        try
        {
            instance = new FactorizationMachineStochasticGradient(factorCount, 0,
                biasRegularization, weightRegularization, factorRegularization,
                seedScale, maxIterations, random);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        // No negative bias regularization.
        exceptionThrown = false;
        try
        {
            instance = new FactorizationMachineStochasticGradient(factorCount, learningRate,
                -1.0, weightRegularization, factorRegularization,
                seedScale, maxIterations, random);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        // No negative weight regularization.
        exceptionThrown = false;
        try
        {
            instance = new FactorizationMachineStochasticGradient(factorCount, learningRate,
                biasRegularization, -1.0, factorRegularization,
                seedScale, maxIterations, random);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        // No negative factor regularization.
        exceptionThrown = false;
        try
        {
            instance = new FactorizationMachineStochasticGradient(factorCount, learningRate,
                biasRegularization, weightRegularization, -1.0,
                seedScale, maxIterations, random);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        // No negative seed scale.
        exceptionThrown = false;
        try
        {
            instance = new FactorizationMachineStochasticGradient(factorCount, learningRate,
                biasRegularization, weightRegularization, factorRegularization,
                -1.0, maxIterations, random);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        // No negative max iterations.
        exceptionThrown = false;
        try
        {
            instance = new FactorizationMachineStochasticGradient(factorCount, learningRate,
                biasRegularization, weightRegularization, factorRegularization,
                seedScale, -1, random);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
    }

    /**
     * Test of step method, of class FactorizationMachineStochasticGradient.
     */
    @Test
    public void testLearn()
    {
        System.out.println("learn");
        int n = 400;
        int d = 5;
        int k = 2;
        FactorizationMachine actual = new FactorizationMachine(d, k);
        actual.setBias(this.random.nextGaussian() * 10.0);
        actual.setWeights(VectorFactory.getDenseDefault().createUniformRandom(d,
            -1.0, 1.0, this.random));
        actual.setFactors(MatrixFactory.getDenseDefault().createUniformRandom(k,
            d, -1.0, 1.0, this.random));
        
        int trainSize = n;
        int testSize = n;
        int totalSize = trainSize + testSize;
        List<InputOutputPair<Vector, Double>> trainData = new ArrayList<InputOutputPair<Vector, Double>>();
        final List<InputOutputPair<Vector, Double>> testData = new ArrayList<InputOutputPair<Vector, Double>>();
        
        for (int i = 0; i < totalSize; i++)
        {
            Vector input = VectorFactory.getDenseDefault().createUniformRandom(
                d, -10.0, 10.0, this.random);
            final DefaultInputOutputPair<Vector, Double> example =
                DefaultInputOutputPair.create(input, actual.evaluateAsDouble(input));
            if (i < trainSize)
            {
                trainData.add(example);
            }
            else
            {
                testData.add(example);
            }
        }
        
        FactorizationMachineStochasticGradient instance =
            new FactorizationMachineStochasticGradient();
        instance.setFactorCount(k);
        instance.setSeedScale(0.2);
        instance.setBiasRegularization(0.0);
        instance.setWeightRegularization(0.01);
        instance.setFactorRegularization(0.1);
        instance.setLearningRate(0.005);
        instance.setMaxIterations(1000);
        instance.setRandom(random);
        
//        instance.addIterativeAlgorithmListener(new IterationMeasurablePerformanceReporter());
        instance.addIterativeAlgorithmListener(new AbstractIterativeAlgorithmListener()
        {

            @Override
            public void stepEnded(IterativeAlgorithm algorithm)
            {
                final FactorizationMachineStochasticGradient a = (FactorizationMachineStochasticGradient) algorithm;
                MeanSquaredErrorEvaluator<Vector> performance =
                    new MeanSquaredErrorEvaluator<Vector>();
                System.out.println("Iteration " + a.getIteration() 
                    + " RMSE: Train: " + NUMBER_FORMAT.format(Math.sqrt(performance.evaluatePerformance(a.getResult(), a.getData())))
                    + " Validation: " + NUMBER_FORMAT.format(Math.sqrt(performance.evaluatePerformance(a.getResult(), testData)))
                    + " Objective: " + NUMBER_FORMAT.format(a.getObjective())
                    + " Change: " + NUMBER_FORMAT.format(a.getTotalChange())
                    + " Error: " + NUMBER_FORMAT.format(Math.sqrt(a.getTotalError() / a.getData().size()))
                    + " Regularization: " + NUMBER_FORMAT.format(a.getRegularizationPenalty()));
            }
            
        });
        
// TODO: Figure out why this doesn't work sometimes with real factors. Is it just learning rate?
        FactorizationMachine result = instance.learn(trainData);
        assertEquals(d, result.getInputDimensionality());
        assertEquals(k, result.getFactorCount());
        
        System.out.println(actual.getBias());
        System.out.println(actual.getWeights());
        System.out.println(actual.getFactors());
        System.out.println(result.getBias());
        System.out.println(result.getWeights());
        System.out.println(result.getFactors());
        
        MeanSquaredErrorEvaluator<Vector> performance =
            new MeanSquaredErrorEvaluator<Vector>();
        System.out.println("RMSE: " + Math.sqrt(performance.evaluatePerformance(result, testData)));
        assertTrue(Math.sqrt(performance.evaluatePerformance(result, testData)) < 0.05);
    }

    /**
     * Test of getLearningRate method, of class FactorizationMachineStochasticGradient.
     */
    @Test
    public void testGetLearningRate()
    {
        this.testSetLearningRate();
    }

    /**
     * Test of setLearningRate method, of class FactorizationMachineStochasticGradient.
     */
    @Test
    public void testSetLearningRate()
    {
        double learningRate = FactorizationMachineStochasticGradient.DEFAULT_LEARNING_RATE;
        FactorizationMachineStochasticGradient instance =
            new FactorizationMachineStochasticGradient();
        assertEquals(learningRate, instance.getLearningRate(), 0.0);
        
        learningRate = 0.2;
        instance.setLearningRate(learningRate);
        assertEquals(learningRate, instance.getLearningRate(), 0.0);
        
        double[] badValues = {0.0, -0.1, -2.2, Double.NaN };
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setLearningRate(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(learningRate, instance.getLearningRate(), 0.0);
        }
    }
    
}
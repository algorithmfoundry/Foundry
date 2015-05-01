/*
 * File:            FactorizationMachineAlternatingLeastSquares.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2015 Cognitive Foundry. All rights reserved.
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
import gov.sandia.cognition.util.NamedValue;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@link FactorizationMachineAlternatingLeastSquares}.
 * 
 * @author  Justin Basilico
 * @since   3.4.1
 */
public class FactorizationMachineAlternatingLeastSquaresTest
    extends Object
{
    public static final NumberFormat NUMBER_FORMAT = new DecimalFormat("0.0000");
    protected Random random = new Random(47474747);
    
    /**
     * Creates a new test.
     */
    public FactorizationMachineAlternatingLeastSquaresTest()
    {
        super();
    }
    
    /**
     * Test of constructors, of class FactorizationMachineAlternatingLeastSquares.
     */
    @Test
    public void testConstructors()
    {
        int factorCount = FactorizationMachineAlternatingLeastSquares.DEFAULT_FACTOR_COUNT;
        double biasRegularization = FactorizationMachineAlternatingLeastSquares.DEFAULT_BIAS_REGULARIZATION;
        double weightRegularization = FactorizationMachineAlternatingLeastSquares.DEFAULT_WEIGHT_REGULARIZATION;
        double factorRegularization = FactorizationMachineAlternatingLeastSquares.DEFAULT_FACTOR_REGULARIZATION;
        double seedScale = FactorizationMachineAlternatingLeastSquares.DEFAULT_SEED_SCALE;
        int maxIterations = FactorizationMachineAlternatingLeastSquares.DEFAULT_MAX_ITERATIONS;
        double minChange = FactorizationMachineAlternatingLeastSquares.DEFAULT_MIN_CHANGE;

        FactorizationMachineAlternatingLeastSquares instance =
            new FactorizationMachineAlternatingLeastSquares();
        assertEquals(factorCount, instance.getFactorCount());
        assertEquals(biasRegularization, instance.getBiasRegularization(), 0.0);
        assertEquals(weightRegularization, instance.getWeightRegularization(), 0.0);
        assertEquals(factorRegularization, instance.getFactorRegularization(), 0.0);
        assertEquals(seedScale, instance.getSeedScale(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(minChange, instance.getMinChange(), 0.0);
        assertNotNull(instance.getRandom());
        assertSame(instance.getRandom(), instance.getRandom());

        factorCount = 22;
        biasRegularization = 3.33;
        weightRegularization = 44.44;
        factorRegularization = 555.55;
        seedScale = 0.6;
        maxIterations = 777;
        minChange = 0.88;
        Random random = new Random();
        instance = new FactorizationMachineAlternatingLeastSquares(factorCount,
            biasRegularization, weightRegularization, factorRegularization,
            seedScale, maxIterations, minChange, random);
        assertEquals(factorCount, instance.getFactorCount());
        assertEquals(biasRegularization, instance.getBiasRegularization(), 0.0);
        assertEquals(weightRegularization, instance.getWeightRegularization(), 0.0);
        assertEquals(factorRegularization, instance.getFactorRegularization(), 0.0);
        assertEquals(seedScale, instance.getSeedScale(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertEquals(minChange, instance.getMinChange(), 0.0);
        assertSame(random, instance.getRandom());
        
        
        // No negative factor counts.
        boolean exceptionThrown = false;
        try
        {
            instance = new FactorizationMachineAlternatingLeastSquares(-1,
                biasRegularization, weightRegularization, factorRegularization,
                seedScale, maxIterations, minChange, random);
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
            instance = new FactorizationMachineAlternatingLeastSquares(factorCount,
                -1.0, weightRegularization, factorRegularization,
                seedScale, maxIterations, minChange, random);
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
            instance = new FactorizationMachineAlternatingLeastSquares(factorCount,
                biasRegularization, -1.0, factorRegularization,
                seedScale, maxIterations, minChange, random);
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
            instance = new FactorizationMachineAlternatingLeastSquares(factorCount,
                biasRegularization, weightRegularization, -1.0,
                seedScale, maxIterations, minChange, random);
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
            instance = new FactorizationMachineAlternatingLeastSquares(factorCount,
                biasRegularization, weightRegularization, factorRegularization,
                -1.0, maxIterations, minChange, random);
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
            instance = new FactorizationMachineAlternatingLeastSquares(factorCount,
                biasRegularization, weightRegularization, factorRegularization,
                seedScale, -1, minChange, random);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        
        // No negative min change.
        exceptionThrown = false;
        try
        {
            instance = new FactorizationMachineAlternatingLeastSquares(factorCount,
                biasRegularization, weightRegularization, factorRegularization,
                seedScale, maxIterations, -0.1, random);
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
     * Test of learn method, of class FactorizationMachineAlternatingLeastSquares.
     */
    @Test
    public void testLearn()
    {
        System.out.println("learn");
        boolean useBias = true;
        boolean useWeights = true;
        boolean useFactors = true;
        int n = 500;
        int d = 5;
        int k = 2;
        FactorizationMachine actual = new FactorizationMachine(d, k);
        actual.setBias(this.random.nextGaussian() * 10.0 * (useBias ? 1.0 : 0.0));
        actual.setWeights(VectorFactory.getDenseDefault().createUniformRandom(d,
            -1.0, 1.0, this.random).scale(useWeights ? 1.0 : 0.0));
        actual.setFactors(MatrixFactory.getDenseDefault().createUniformRandom(k,
            d, -1.0, 1.0, this.random).scale(useFactors ? 1.0 : 0.0));
        
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
        
        FactorizationMachineAlternatingLeastSquares instance =
            new FactorizationMachineAlternatingLeastSquares();
        instance.setFactorCount(useFactors ? k : 0);
        instance.setSeedScale(0.2);
        instance.setBiasRegularization(0.0);
        instance.setWeightRegularization(0.01);
        instance.setFactorRegularization(0.1);
        instance.setMaxIterations(1000);
        instance.setMinChange(1e-4);
        instance.setWeightsEnabled(useWeights);
        instance.setBiasEnabled(useBias);
        instance.setRandom(random);
//        instance.addIterativeAlgorithmListener(new IterationMeasurablePerformanceReporter());
// TODO: Part of this may be good as a general class (printing validation metrics).
        instance.addIterativeAlgorithmListener(new AbstractIterativeAlgorithmListener()
        {

            @Override
            public void stepEnded(IterativeAlgorithm algorithm)
            {
                final FactorizationMachineAlternatingLeastSquares a = (FactorizationMachineAlternatingLeastSquares) algorithm;
                MeanSquaredErrorEvaluator<Vector> performance =
                    new MeanSquaredErrorEvaluator<>();
                System.out.println("Iteration " + a.getIteration() 
                    + " RMSE: Train: " + NUMBER_FORMAT.format(Math.sqrt(performance.evaluatePerformance(a.getResult(), a.getData())))
                    + " Validation: " + NUMBER_FORMAT.format(Math.sqrt(performance.evaluatePerformance(a.getResult(), testData)))
                    + " Objective: " + NUMBER_FORMAT.format(a.getObjective())
                    + " Change: " + NUMBER_FORMAT.format(a.getTotalChange())
                    + " Error: " + NUMBER_FORMAT.format(Math.sqrt(a.getTotalError() / a.getData().size()))
                    + " Regularization: " + NUMBER_FORMAT.format(a.getRegularizationPenalty()));
            }
            
        });
        
// TODO: Figure out why this doesn't work with real factors.
        FactorizationMachine result = instance.learn(trainData);
        System.out.println(actual.getBias());
        System.out.println(actual.getWeights());
        System.out.println(actual.getFactors());
        System.out.println(result.getBias());
        System.out.println(result.getWeights());
        System.out.println(result.getFactors());
        
        MeanSquaredErrorEvaluator<Vector> performance = new MeanSquaredErrorEvaluator<>();
        System.out.println("RMSE: " + Math.sqrt(performance.evaluatePerformance(result, testData)));
        assertTrue(Math.sqrt(performance.evaluatePerformance(result, testData)) < 0.05);
    }

    /**
     * Test of getRegularizationPenalty method, of class FactorizationMachineAlternatingLeastSquares.
     */
    @Test
    public void testGetRegularizationPenalty()
    {
        FactorizationMachineAlternatingLeastSquares instance =
            new FactorizationMachineAlternatingLeastSquares();
        assertEquals(0.0, instance.getRegularizationPenalty(), 0.0);
        
        instance.result = new FactorizationMachine(10, 5);
        assertEquals(0.0, instance.getRegularizationPenalty(), 0.0);
        
        instance.result.setBias(3);
        assertEquals(0.0, instance.getRegularizationPenalty(), 0.0);
        instance.setBiasRegularization(2.0);
        assertEquals(6.0, instance.getRegularizationPenalty(), 0.0);
        instance.setBiasRegularization(0);
        
        instance.result.getWeights().set(0, 4);
        instance.result.getWeights().set(2, 3);
        instance.setWeightRegularization(2.0);
        assertEquals(50.0, instance.getRegularizationPenalty(), 0.0);
        instance.setWeightRegularization(0.0);
        assertEquals(0.0, instance.getRegularizationPenalty(), 0.0);
        
        instance.result.getFactors().set(0, 0, 5);
        instance.result.getFactors().set(4, 2, 2);
        instance.setFactorRegularization(3.0);
        assertEquals(87.0, instance.getRegularizationPenalty(), 1e-10);
        instance.setFactorRegularization(0.0);
        assertEquals(0.0, instance.getRegularizationPenalty(), 0.0);
        
        instance.setWeightRegularization(2.0);
        instance.setFactorRegularization(3.0);
        assertEquals(137.0, instance.getRegularizationPenalty(), 1e-10);
    }

    /**
     * Test of getPerformance method, of class FactorizationMachineAlternatingLeastSquares.
     */
    @Test
    public void testGetPerformance()
    {
        FactorizationMachineAlternatingLeastSquares instance =
            new FactorizationMachineAlternatingLeastSquares();
        NamedValue<? extends Number> result = instance.getPerformance();
        assertEquals("objective", result.getName());
        assertEquals(0.0, result.getValue());
    }

    /**
     * Test of getMinChange method, of class FactorizationMachineAlternatingLeastSquares.
     */
    @Test
    public void testGetMinChange()
    {
        this.testSetMinChange();
    }

    /**
     * Test of setMinChange method, of class FactorizationMachineAlternatingLeastSquares.
     */
    @Test
    public void testSetMinChange()
    {
        double minChange = FactorizationMachineAlternatingLeastSquares.DEFAULT_MIN_CHANGE;
        FactorizationMachineAlternatingLeastSquares instance =
            new FactorizationMachineAlternatingLeastSquares();
        assertEquals(minChange, instance.getMinChange(), 0.0);
        
        minChange = 0.1;
        instance.setMinChange(minChange);
        assertEquals(minChange, instance.getMinChange(), 0.0);
        
        double[] badValues = {-0.1, -2.2, Double.NaN};
        for (double badValue : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setMinChange(badValue);
            }
            catch (IllegalArgumentException e)
            {
                exceptionThrown = true;
            }
            finally
            {
                assertTrue(exceptionThrown);
            }
            assertEquals(minChange, instance.getMinChange(), 0.0);
        }
    }
    
}
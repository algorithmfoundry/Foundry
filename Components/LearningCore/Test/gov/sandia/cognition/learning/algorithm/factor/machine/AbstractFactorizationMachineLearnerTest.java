/*
 * File:            AbstractFactorizationMachineLearnerTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2013 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.algorithm.factor.machine;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.NamedValue;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@link AbstractFactorizationMachineLearner}.
 * 
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class AbstractFactorizationMachineLearnerTest
    extends Object
{
    
    /**
     * Creates a new test.
     */
    public AbstractFactorizationMachineLearnerTest()
    {
        super();
    }
    
    /**
     * Creates a new instance.
     * 
     * @return 
     *      A new instance;
     */
    protected AbstractFactorizationMachineLearner createInstance()
    {
        return new DummyFactorizationMachineLearner();
    }

    /**
     * Test of constructors of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testConstructors()
    {
        int factorCount = AbstractFactorizationMachineLearner.DEFAULT_FACTOR_COUNT;
        double biasRegularization = AbstractFactorizationMachineLearner.DEFAULT_BIAS_REGULARIZATION;
        double weightRegularization = AbstractFactorizationMachineLearner.DEFAULT_WEIGHT_REGULARIZATION;
        double factorRegularization = AbstractFactorizationMachineLearner.DEFAULT_FACTOR_REGULARIZATION;
        double seedScale = AbstractFactorizationMachineLearner.DEFAULT_SEED_SCALE;
        int maxIterations = AbstractFactorizationMachineLearner.DEFAULT_MAX_ITERATIONS;
        AbstractFactorizationMachineLearner instance =
            new DummyFactorizationMachineLearner();
        assertEquals(factorCount, instance.getFactorCount());
        assertEquals(biasRegularization, instance.getBiasRegularization(), 0.0);
        assertEquals(weightRegularization, instance.getWeightRegularization(), 0.0);
        assertEquals(factorRegularization, instance.getFactorRegularization(), 0.0);
        assertEquals(seedScale, instance.getSeedScale(), 0.0);
        assertEquals(maxIterations, instance.getMaxIterations());
        assertNotNull(instance.getRandom());
        assertSame(instance.getRandom(), instance.getRandom());
        
        factorCount = 22;
        biasRegularization = 3.33;
        weightRegularization = 44.44;
        factorRegularization = 555.55;
        seedScale = 0.6;
        maxIterations = 777;
        Random random = new Random();
        instance = new DummyFactorizationMachineLearner(factorCount,
            biasRegularization, weightRegularization, factorRegularization,
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
            instance = new DummyFactorizationMachineLearner(-1,
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
            instance = new DummyFactorizationMachineLearner(factorCount,
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
            instance = new DummyFactorizationMachineLearner(factorCount,
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
            instance = new DummyFactorizationMachineLearner(factorCount,
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
            instance = new DummyFactorizationMachineLearner(factorCount,
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
            instance = new DummyFactorizationMachineLearner(factorCount,
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
     * Test of getResult method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testGetResult()
    {
        AbstractFactorizationMachineLearner instance =
            new DummyFactorizationMachineLearner();
        
        assertNull(instance.getResult());
        
        // Doing a learn should initialize the result.
        int k = 7;
        int d = 40;
        instance.setFactorCount(k);
        instance.learn(Collections.singletonList(
            DefaultInputOutputPair.create(
                VectorFactory.getDefault().createVector(d), 1.0)));
        FactorizationMachine result = instance.getResult();
        assertSame(result, instance.getResult());
        
        assertEquals(0.0, result.getBias(), 0.0);
        assertEquals(d, result.getInputDimensionality());
        assertEquals(0.0, result.getWeights().norm2Squared(), 0.0);
        assertEquals(k, result.getFactorCount());
    }

    /**
     * Test of getFactorCount method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testGetFactorCount()
    {
        this.testSetFactorCount();
    }

    /**
     * Test of setFactorCount method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testSetFactorCount()
    {
        int factorCount = FactorizationMachineStochasticGradient.DEFAULT_FACTOR_COUNT;
        AbstractFactorizationMachineLearner instance = this.createInstance();
        assertEquals(factorCount, instance.getFactorCount());
        
        factorCount = 2;
        instance.setFactorCount(factorCount);
        assertEquals(factorCount, instance.getFactorCount());
        
        factorCount = 0;
        instance.setFactorCount(factorCount);
        assertEquals(factorCount, instance.getFactorCount());
        
        boolean exceptionThrown = false;
        try
        {
            instance.setFactorCount(-1);
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
     * Test of isBiasEnabled method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testIsBiasEnabled()
    {
        this.testSetBiasEnabled();
    }

    /**
     * Test of setBiasEnabled method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testSetBiasEnabled()
    {
        boolean biasEnabled = AbstractFactorizationMachineLearner.DEFAULT_BIAS_ENABLED;
        AbstractFactorizationMachineLearner instance = this.createInstance();
        assertEquals(biasEnabled, instance.isBiasEnabled());
        
        biasEnabled = !biasEnabled;
        instance.setBiasEnabled(biasEnabled);
        assertEquals(biasEnabled, instance.isBiasEnabled());
        
        biasEnabled = !biasEnabled;
        instance.setBiasEnabled(biasEnabled);
        assertEquals(biasEnabled, instance.isBiasEnabled());
    }

    /**
     * Test of isWeightsEnabled method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testIsWeightsEnabled()
    {
        this.testSetWeightsEnabled();
    }

    /**
     * Test of setWeightsEnabled method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testSetWeightsEnabled()
    {
        boolean weightsEnabled = AbstractFactorizationMachineLearner.DEFAULT_WEIGHTS_ENABLED;
        AbstractFactorizationMachineLearner instance = this.createInstance();
        assertEquals(weightsEnabled, instance.isWeightsEnabled());
        
        weightsEnabled = !weightsEnabled;
        instance.setWeightsEnabled(weightsEnabled);
        assertEquals(weightsEnabled, instance.isWeightsEnabled());
        
        weightsEnabled = !weightsEnabled;
        instance.setWeightsEnabled(weightsEnabled);
        assertEquals(weightsEnabled, instance.isWeightsEnabled());
    }

    /**
     * Test of isFactorsEnabled method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testIsFactorsEnabled()
    {
        AbstractFactorizationMachineLearner instance = this.createInstance();
        assertTrue(instance.isFactorsEnabled());
        
        instance.setFactorCount(0);
        assertFalse(instance.isFactorsEnabled());
        
        instance.setFactorCount(1);
        assertTrue(instance.isFactorsEnabled());
        
        instance.setFactorCount(2);
        assertTrue(instance.isFactorsEnabled());
    }

    /**
     * Test of getBiasRegularization method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testGetBiasRegularization()
    {
        this.testSetBiasRegularization();
    }

    /**
     * Test of setBiasRegularization method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testSetBiasRegularization()
    {
        double biasRegularization = AbstractFactorizationMachineLearner.DEFAULT_BIAS_REGULARIZATION;
        AbstractFactorizationMachineLearner instance = this.createInstance();
        assertEquals(biasRegularization, instance.getBiasRegularization(), 0.0);
        
        double[] goodValues = {0.1, 0.2, 1.0, 12.1, 0.0, 111 };
        for (double value : goodValues)
        {
            biasRegularization = value;
            instance.setBiasRegularization(biasRegularization);
            assertEquals(biasRegularization, instance.getBiasRegularization(), 0.0);
        }
        
        double[] badValues = { -0.1, -1, -10};
        for (double value : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setBiasRegularization(value);
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
    }

    /**
     * Test of getWeightRegularization method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testGetWeightRegularization()
    {
        this.testSetWeightRegularization();
    }

    /**
     * Test of setWeightRegularization method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testSetWeightRegularization()
    {
        double weightRegularization = AbstractFactorizationMachineLearner.DEFAULT_WEIGHT_REGULARIZATION;
        AbstractFactorizationMachineLearner instance = this.createInstance();
        assertEquals(weightRegularization, instance.getWeightRegularization(), 0.0);
        
        double[] goodValues = {0.1, 0.2, 1.0, 12.1, 0.0, 111 };
        for (double value : goodValues)
        {
            weightRegularization = value;
            instance.setWeightRegularization(weightRegularization);
            assertEquals(weightRegularization, instance.getWeightRegularization(), 0.0);
        }
        
        double[] badValues = { -0.1, -1, -10};
        for (double value : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setWeightRegularization(value);
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
    }

    /**
     * Test of getFactorRegularization method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testGetFactorRegularization()
    {
        this.testSetFactorRegularization();
    }

    /**
     * Test of setFactorRegularization method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testSetFactorRegularization()
    {
        double factorRegularization = AbstractFactorizationMachineLearner.DEFAULT_FACTOR_REGULARIZATION;
        AbstractFactorizationMachineLearner instance = this.createInstance();
        assertEquals(factorRegularization, instance.getFactorRegularization(), 0.0);
        
        double[] goodValues = {0.1, 0.2, 1.0, 12.1, 0.0, 111 };
        for (double value : goodValues)
        {
            factorRegularization = value;
            instance.setFactorRegularization(factorRegularization);
            assertEquals(factorRegularization, instance.getFactorRegularization(), 0.0);
        }
        
        double[] badValues = { -0.1, -1, -10};
        for (double value : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setFactorRegularization(value);
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
    }

    /**
     * Test of getSeedScale method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testGetSeedScale()
    {
        this.testSetSeedScale();
    }

    /**
     * Test of setSeedScale method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testSetSeedScale()
    {
        double seedScale = AbstractFactorizationMachineLearner.DEFAULT_SEED_SCALE;
        AbstractFactorizationMachineLearner instance = this.createInstance();
        assertEquals(seedScale, instance.getSeedScale(), 0.0);
        
        double[] goodValues = {0.1, 0.2, 1.0, 12.1, 0.0, 111 };
        for (double value : goodValues)
        {
            seedScale = value;
            instance.setSeedScale(seedScale);
            assertEquals(seedScale, instance.getSeedScale(), 0.0);
        }
        
        double[] badValues = { -0.1, -1, -10};
        for (double value : badValues)
        {
            boolean exceptionThrown = false;
            try
            {
                instance.setSeedScale(value);
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
    }

    /**
     * Test of getRandom method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testGetRandom()
    {
        this.testSetRandom();
    }

    /**
     * Test of setRandom method, of class AbstractFactorizationMachineLearner.
     */
    @Test
    public void testSetRandom()
    {
        AbstractFactorizationMachineLearner instance = this.createInstance();
        assertNotNull(instance.getRandom());
        assertSame(instance.getRandom(), instance.getRandom());
        
        Random random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());

        random = null;
        instance.setRandom(random);
        assertSame(random, instance.getRandom());
        
        random = new Random();
        instance.setRandom(random);
        assertSame(random, instance.getRandom());
    }

    public class DummyFactorizationMachineLearner
        extends AbstractFactorizationMachineLearner
    {

        public DummyFactorizationMachineLearner()
        {
            super();
        }

        public DummyFactorizationMachineLearner(
            final int factorCount,
            final double biasRegularization,
            final double weightRegularization,
            final double factorRegularization,
            final double seedScale,
            final int maxIterations,
            final Random random)
        {
            super(factorCount, biasRegularization, weightRegularization,
                factorRegularization, seedScale, maxIterations, random);
        }

        
        @Override
        protected boolean step()
        {
            return false;
        }

        @Override
        protected void cleanupAlgorithm()
        {
        }

        @Override
        public NamedValue<? extends Number> getPerformance()
        {
            return null;
        }
        
    }
    
}
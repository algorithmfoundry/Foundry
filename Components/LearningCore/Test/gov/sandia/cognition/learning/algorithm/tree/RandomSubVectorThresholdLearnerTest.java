/*
 * File:                RandomSubVectorThresholdLearnerTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright December 23, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.tree;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.categorization.VectorElementThresholdCategorizer;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import java.util.ArrayList;
import java.util.Random;
import junit.framework.TestCase;

/**
 * Unit tests for class RandomSubVectorThresholdLearner.
 *
 * @author  Justin Basilico
 * @since   3.0
 */
public class RandomSubVectorThresholdLearnerTest
    extends TestCase
{
    protected Random random;

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public RandomSubVectorThresholdLearnerTest(
        String testName)
    {
        super(testName);

        this.random = new Random();
    }

    /**
     * Test of constructors of class RandomSubVectorThresholdLearner.
     */
    public void testConstructors()
    {
        VectorThresholdInformationGainLearner<String> subLearner = null;
        double percentToSample = RandomSubVectorThresholdLearner.DEFAULT_PERCENT_TO_SAMPLE;
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        int[] dimensionsToConsider = null;
        RandomSubVectorThresholdLearner<String> instance = new RandomSubVectorThresholdLearner<String>();
        assertSame(subLearner, instance.getSubLearner());
        assertEquals(percentToSample, instance.getPercentToSample());
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());
        assertNotNull(instance.getRandom());
        assertSame(vectorFactory, instance.getVectorFactory());

        subLearner = new VectorThresholdInformationGainLearner<String>();
        percentToSample = percentToSample / 2.0;
        instance = new RandomSubVectorThresholdLearner<String>(subLearner,
            percentToSample, random);
        assertSame(subLearner, instance.getSubLearner());
        assertEquals(percentToSample, instance.getPercentToSample());
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());
        assertSame(random, instance.getRandom());
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getSparseDefault();
        instance = new RandomSubVectorThresholdLearner<String>(subLearner,
            percentToSample, random, vectorFactory);
        assertSame(subLearner, instance.getSubLearner());
        assertEquals(percentToSample, instance.getPercentToSample());
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());
        assertSame(random, instance.getRandom());
        assertSame(vectorFactory, instance.getVectorFactory());
        
        dimensionsToConsider = new int[] {5, 12};
        vectorFactory = VectorFactory.getSparseDefault();
        instance = new RandomSubVectorThresholdLearner<String>(subLearner,
            percentToSample, dimensionsToConsider, random, vectorFactory);
        assertSame(subLearner, instance.getSubLearner());
        assertEquals(percentToSample, instance.getPercentToSample());
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());
        assertSame(random, instance.getRandom());
        assertSame(vectorFactory, instance.getVectorFactory());
    }

    /**
     * Test of learn method, of class RandomSubVectorThresholdLearner.
     */
    public void testLearn()
    {
        RandomSubVectorThresholdLearner<String> instance = new RandomSubVectorThresholdLearner<String>(
            new VectorThresholdInformationGainLearner<String>(),
            0.1, random);

        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        ArrayList<InputOutputPair<Vector, String>> data =
            new ArrayList<InputOutputPair<Vector, String>>();
        for (int i = 0; i < 10; i++)
        {
            data.add(new DefaultInputOutputPair<Vector, String>(vectorFactory.createUniformRandom(
                100, 1.0, 10.0, random), "a"));
        }

        for (int i = 0; i < 10; i++)
        {
            data.add(new DefaultInputOutputPair<Vector, String>(vectorFactory.createUniformRandom(
                100, 1.0, 10.0, random), "b"));
        }

        VectorElementThresholdCategorizer result = instance.learn(data);
        assertNotNull(result);
        assertTrue(result.getIndex() >= 0);
        assertTrue(result.getIndex() < 100);
        
        // Change the dimensions to consider.
        instance.setDimensionsToConsider(new int[] {10, 20, 30, 40, 50});
        instance.setPercentToSample(0.5);
        result = instance.learn(data);
        assertNotNull(result);
        assertTrue(result.getIndex() >= 10);
        assertTrue(result.getIndex() <= 50);
        assertTrue(result.getIndex() % 10 == 0);
    }
    
    /**
     * Test of learn method, of class RandomSubVectorThresholdLearner.
     */
    public void testLearnFullDimensions()
    {
        RandomSubVectorThresholdLearner<String> instance = new RandomSubVectorThresholdLearner<>(
            new VectorThresholdInformationGainLearner<String>(),
            0.9999, random);

        
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        ArrayList<InputOutputPair<Vector, String>> data = new ArrayList<>();
        for (int i = 0; i < 10; i++)
        {
            data.add(new DefaultInputOutputPair<>(vectorFactory.createUniformRandom(
                100, 1.0, 10.0, random), "a"));
        }

        for (int i = 0; i < 10; i++)
        {
            data.add(new DefaultInputOutputPair<>(vectorFactory.createUniformRandom(
                100, 1.0, 10.0, random), "b"));
        }

        VectorElementThresholdCategorizer result = instance.learn(data);
        assertTrue(result.getIndex() >= 0);
        assertTrue(result.getIndex() < 100);
        
        // Change the dimensions to consider.
        instance.setDimensionsToConsider(new int[] {10});

        result = instance.learn(data);
        assertTrue(result.getIndex() == 10);
    }

    /**
     * Test of getSubDimensionality method, of class RandomSubVectorThresholdLearner.
     */
    public void testGetSubDimensionality()
    {
        RandomSubVectorThresholdLearner<String> instance = new RandomSubVectorThresholdLearner<String>();

        instance.setPercentToSample(0.5);
        assertEquals(5, instance.getSubDimensionality(10));

        instance.setPercentToSample(0.25);
        assertEquals(2, instance.getSubDimensionality(9));

        instance.setPercentToSample(1.0);
        assertEquals(9, instance.getSubDimensionality(9));

        instance.setPercentToSample(0.0);
        assertEquals(1, instance.getSubDimensionality(9));
    }

    /**
     * Test of getSubLearner method, of class RandomSubVectorThresholdLearner.
     */
    public void testGetSubLearner()
    {
        this.testSetSubLearner();
    }

    /**
     * Test of setSubLearner method, of class RandomSubVectorThresholdLearner.
     */
    public void testSetSubLearner()
    {
        VectorThresholdInformationGainLearner<String> subLearner = null;
        RandomSubVectorThresholdLearner<String> instance = new RandomSubVectorThresholdLearner<String>();
        assertSame(subLearner, instance.getSubLearner());

        subLearner = new VectorThresholdInformationGainLearner<String>();
        instance.setSubLearner(subLearner);
        assertSame(subLearner, instance.getSubLearner());

        subLearner = new VectorThresholdInformationGainLearner<String>();
        instance.setSubLearner(subLearner);
        assertSame(subLearner, instance.getSubLearner());

        subLearner = null;
        instance.setSubLearner(subLearner);
        assertSame(subLearner, instance.getSubLearner());

        subLearner = new VectorThresholdInformationGainLearner<String>();
        instance.setSubLearner(subLearner);
        assertSame(subLearner, instance.getSubLearner());
    }

    /**
     * Test of getPercentToSample method, of class RandomSubVectorThresholdLearner.
     */
    public void testGetPercentToSample()
    {
        this.testSetPercentToSample();
    }

    /**
     * Test of setPercentToSample method, of class RandomSubVectorThresholdLearner.
     */
    public void testSetPercentToSample()
    {
        double percentToSample = RandomSubVectorThresholdLearner.DEFAULT_PERCENT_TO_SAMPLE;
        RandomSubVectorThresholdLearner<String> instance = new RandomSubVectorThresholdLearner<String>();
        assertEquals(percentToSample, instance.getPercentToSample(), 0.0);

        percentToSample = percentToSample / 2.0;
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample());
        
        percentToSample = 1.0;
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample());

        percentToSample = 0.0;
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample());

        percentToSample = 0.47;
        instance.setPercentToSample(percentToSample);
        assertEquals(percentToSample, instance.getPercentToSample());

        boolean exceptionThrown = false;
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
        assertEquals(percentToSample, instance.getPercentToSample());

        exceptionThrown = false;
        try
        {
            instance.setPercentToSample(1.1);
        }
        catch (IllegalArgumentException e)
        {
            exceptionThrown = true;
        }
        finally
        {
            assertTrue(exceptionThrown);
        }
        assertEquals(percentToSample, instance.getPercentToSample());
    }
    
    public void testGetDimensionsToConsider()
    {
        this.testSetDimensionsToConsider();
    }

    public void testSetDimensionsToConsider()
    {
        int[] dimensionsToConsider = null;
        RandomSubVectorThresholdLearner<String> instance
            = new RandomSubVectorThresholdLearner<>();
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());

        dimensionsToConsider = new int[] {1,2,5};
        instance.setDimensionsToConsider(dimensionsToConsider);
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());

        dimensionsToConsider = new int[] {0, 9, 12};
        instance.setDimensionsToConsider(dimensionsToConsider);
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider());
        
        dimensionsToConsider = null;
        instance.setDimensionsToConsider(dimensionsToConsider);
        assertSame(dimensionsToConsider, instance.getDimensionsToConsider()); 
    }

    /**
     * Test of getVectorFactory method, of class RandomSubVectorThresholdLearner.
     */
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class RandomSubVectorThresholdLearner.
     */
    public void testSetVectorFactory()
    {
        VectorFactory<?> vectorFactory = VectorFactory.getDefault();
        RandomSubVectorThresholdLearner<String> instance = new RandomSubVectorThresholdLearner<String>();
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getSparseDefault();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getDefault();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
        
        vectorFactory = null;
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());

        vectorFactory = VectorFactory.getDefault();
        instance.setVectorFactory(vectorFactory);
        assertSame(vectorFactory, instance.getVectorFactory());
    }

}

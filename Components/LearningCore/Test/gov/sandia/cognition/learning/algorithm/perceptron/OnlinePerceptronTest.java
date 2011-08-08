/*
 * File:                OnlinePerceptronTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright October 19, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.perceptron;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class OnlinePerceptron.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class OnlinePerceptronTest
    extends KernelizableBinaryCategorizerOnlineLearnerTestHarness
{
    /**
     * Creates a new test.
     */
    public OnlinePerceptronTest()
    {
    }

    /**
     * Test of computeUpdate method, of class OnlinePerceptron.
     */
    @Test
    public void testComputeUpdate()
    {
        assertEquals(0.0, OnlinePerceptron.computeUpdate(true, 1.0), 0.0);
        assertEquals(0.0, OnlinePerceptron.computeUpdate(false, -1.0), 0.0);

        assertEquals(1.0, OnlinePerceptron.computeUpdate(true, -0.1), 0.0);
        assertEquals(1.0, OnlinePerceptron.computeUpdate(false, 0.1), 0.0);
    }

    /**
     * Test of constructors of class OnlinePerceptron.
     */
    @Test
    public void testConstructors()
    {
        VectorFactory<?> factory = VectorFactory.getDefault();
        OnlinePerceptron instance = new OnlinePerceptron();
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        factory = VectorFactory.getSparseDefault();
        instance = new OnlinePerceptron(factory);
        assertSame(factory, instance.getVectorFactory());
    }

    /**
     * Test of createInitialLearnedObject method, of class OnlinePerceptron.
     */
    @Test
    public void testCreateInitialLearnedObject()
    {
        OnlinePerceptron instance = new OnlinePerceptron();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias(), 0.0);
        assertNotSame(result, instance.createInitialLearnedObject());
    }

    /**
     * Test of update method, of class OnlinePerceptron.
     */
    @Test
    public void testUpdate()
    {
        OnlinePerceptron instance = new OnlinePerceptron();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias(), 0.0);

        instance.update(result, DefaultInputOutputPair.create(new Vector2(2.0, 3.0), true));
        assertEquals(new Vector2(2.0, 3.0), result.getWeights());
        assertEquals(1.0, result.getBias(), 0.0);

        instance.update(result, DefaultInputOutputPair.create(new Vector2(4.0, 4.0), true));
        assertEquals(new Vector2(2.0, 3.0), result.getWeights());
        assertEquals(1.0, result.getBias(), 0.0);

        instance.update(result, DefaultInputOutputPair.create(new Vector2(1.0, 1.0), false));
        assertEquals(new Vector2(1.0, 2.0), result.getWeights());
        assertEquals(0.0, result.getBias(), 0.0);

        instance.update(result, DefaultInputOutputPair.create(new Vector2(1.0, 1.0), false));
        assertEquals(new Vector2(0.0, 1.0), result.getWeights());
        assertEquals(-1.0, result.getBias(), 0.0);

        instance.update(result, DefaultInputOutputPair.create(new Vector2(2.0, 3.0), true));
        assertEquals(new Vector2(0.0, 1.0), result.getWeights());
        assertEquals(-1.0, result.getBias(), 0.0);
    }

    /**
     * Test of getVectorFactory method, of class OnlinePerceptron.
     */
    @Test
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class OnlinePerceptron.
     */
    @Test
    public void testSetVectorFactory()
    {
        VectorFactory<?> factory = VectorFactory.getDefault();
        OnlinePerceptron instance = new OnlinePerceptron();
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

    @Override
    protected KernelizableBinaryCategorizerOnlineLearner createLinearInstance()
    {
        return new OnlinePerceptron();
    }

}
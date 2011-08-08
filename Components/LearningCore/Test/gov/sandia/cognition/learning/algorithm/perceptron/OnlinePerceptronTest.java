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
import gov.sandia.cognition.learning.function.categorization.LinearBinaryCategorizer;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.mtj.SparseVectorFactoryMTJ;
import gov.sandia.cognition.math.matrix.mtj.Vector2;
import junit.framework.TestCase;

/**
 * Unit tests for class OnlinePerceptron.
 *
 * @author  Justin Basilico
 * @since   3.1
 */
public class OnlinePerceptronTest
    extends TestCase
{

    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public OnlinePerceptronTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class OnlinePerceptron.
     */
    public void testConstructors()
    {
        VectorFactory<?> factory = VectorFactory.getDefault();
        OnlinePerceptron instance = new OnlinePerceptron();
        assertSame(VectorFactory.getDefault(), instance.getVectorFactory());

        factory = new SparseVectorFactoryMTJ();
        instance = new OnlinePerceptron(factory);
        assertSame(factory, instance.getVectorFactory());
    }

    /**
     * Test of createInitialLearnedObject method, of class OnlinePerceptron.
     */
    public void testCreateInitialLearnedObject()
    {
        OnlinePerceptron instance = new OnlinePerceptron();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias());
        assertNotSame(result, instance.createInitialLearnedObject());
    }

    /**
     * Test of update method, of class OnlinePerceptron.
     */
    public void testUpdate()
    {
        OnlinePerceptron instance = new OnlinePerceptron();
        LinearBinaryCategorizer result = instance.createInitialLearnedObject();
        assertNull(result.getWeights());
        assertEquals(0.0, result.getBias());

        instance.update(result, DefaultInputOutputPair.create(new Vector2(2.0, 3.0), true));
        assertEquals(new Vector2(2.0, 3.0), result.getWeights());
        assertEquals(1.0, result.getBias());

        instance.update(result, DefaultInputOutputPair.create(new Vector2(4.0, 4.0), true));
        assertEquals(new Vector2(2.0, 3.0), result.getWeights());
        assertEquals(1.0, result.getBias());

        instance.update(result, DefaultInputOutputPair.create(new Vector2(1.0, 1.0), false));
        assertEquals(new Vector2(1.0, 2.0), result.getWeights());
        assertEquals(0.0, result.getBias());
        
        instance.update(result, DefaultInputOutputPair.create(new Vector2(1.0, 1.0), false));
        assertEquals(new Vector2(0.0, 1.0), result.getWeights());
        assertEquals(-1.0, result.getBias());

        instance.update(result, DefaultInputOutputPair.create(new Vector2(2.0, 3.0), true));
        assertEquals(new Vector2(0.0, 1.0), result.getWeights());
        assertEquals(-1.0, result.getBias());
    }

    /**
     * Test of getVectorFactory method, of class OnlinePerceptron.
     */
    public void testGetVectorFactory()
    {
        this.testSetVectorFactory();
    }

    /**
     * Test of setVectorFactory method, of class OnlinePerceptron.
     */
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

}

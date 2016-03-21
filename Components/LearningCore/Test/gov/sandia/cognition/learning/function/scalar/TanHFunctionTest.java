/*
 * File:            TanHFunctionTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry
 * 
 * Copyright 2016 Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.learning.function.scalar;

import java.util.Random;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link TanHFunction}.
 * 
 * @author  Justin Basilico
 * @since   3.4.3
 */
public class TanHFunctionTest
    extends Object
{
    protected Random random = new Random(8238327);
    
    /**
     * Creates a new test.
     */
    public TanHFunctionTest()
    {
        super();
    }
    
    /**
     * Test of constructors of class TanHFunction.
     */
    @Test
    public void testConstructors()
    {
        TanHFunction instance = new TanHFunction();
        assertNotNull(instance);
    }

    /**
     * Test of clone method, of class TanHFunction.
     */
    @Test
    public void testClone()
    {
        TanHFunction instance = new TanHFunction();
        TanHFunction clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotNull(clone);
        assertNotSame(clone, instance.clone());
        assertNotSame(clone, clone.clone());
    }

    /**
     * Test of evaluate method, of class TanHFunction.
     */
    @Test
    public void testEvaluate()
    {
        TanHFunction instance = new TanHFunction();
        assertEquals(0.0, instance.evaluate(0.0), 0.0);
        assertEquals(1.0, instance.evaluate(1000.0), 0.0);
        assertEquals(-1.0, instance.evaluate(-1000.0), 0.0);
        
        for (int i = 0; i < 100; i++)
        {
            double x = 10.0 * this.random.nextGaussian();
            assertEquals(Math.tanh(x), instance.evaluate(x), 1e-10);
        }
    }

    /**
     * Test of differentiate method, of class TanHFunction.
     */
    @Test
    public void testDifferentiate()
    {
        TanHFunction instance = new TanHFunction();
        assertEquals(1.0, instance.differentiate(0.0), 0.0);
        assertEquals(0.0, instance.differentiate(1000.0), 0.0);
        assertEquals(0.0, instance.differentiate(-1000.0), 0.0);
        
        double epsilon = 1e-5;
        
        for (int i = 0; i < 100; i++)
        {
            double x = 10.0 * this.random.nextGaussian();
            double expected = (instance.evaluate(x + epsilon) - instance.evaluate(x - epsilon)) / (2 * epsilon);
            assertEquals(expected, instance.differentiate(x), 1e-10);
        }
    }
    
}

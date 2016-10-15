/*
 * File:            HardTanHFunctionTest.java
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
 * Unit tests for class {@link HardTanHFunction}.
 * 
 * @author  Justin Basilico
 * @since   3.4.4
 */
public class HardTanHFunctionTest
    extends Object
{
    protected Random random = new Random(111114);
    
    /**
     * Creates a new test.
     */
    public HardTanHFunctionTest()
    {
        super();
    }
    
    /**
     * Test of constructors of class HardTanHFunction.
     */
    @Test
    public void testConstructors()
    {
        HardTanHFunction instance = new HardTanHFunction();
        assertNotNull(instance);
    }
    
    /**
     * Test of clone method, of class HardTanHFunction.
     */
    @Test
    public void testClone()
    {
        HardTanHFunction instance = new HardTanHFunction();
        assertNotSame(instance, instance.clone());
        assertNotNull(instance.clone());
    }

    /**
     * Test of evaluate method, of class HardTanHFunction.
     */
    @Test
    public void testEvaluate()
    {
        HardTanHFunction instance = new HardTanHFunction();
        
        assertEquals(0.0, instance.evaluate(0.0), 0.0);
        assertEquals(0.5, instance.evaluate(0.5), 0.0);
        assertEquals(-0.5, instance.evaluate(-0.5), 0.0);
        assertEquals(1.0, instance.evaluate(1.0), 0.0);
        assertEquals(-1.0, instance.evaluate(-1.0), 0.0);
        assertEquals(1.0, instance.evaluate(4.0), 0.0);
        assertEquals(-1.0, instance.evaluate(-4.0), 0.0);
        
        for (int i = 0; i < 10; i++)
        {
            double x = random.nextGaussian();
            double y =  Math.max(-1, Math.min(1, x));
            assertEquals(y, instance.evaluate(x), 0.0);
        }
    }

    /**
     * Test of differentiate method, of class HardTanHFunction.
     */
    @Test
    public void testDifferentiate()
    {
        HardTanHFunction instance = new HardTanHFunction();
        
        assertEquals(1.0, instance.differentiate(0.0), 0.0);
        assertEquals(1.0, instance.differentiate(0.5), 0.0);
        assertEquals(1.0, instance.differentiate(-0.5), 0.0);
        assertEquals(0.0, instance.differentiate(1.0), 0.0);
        assertEquals(0.0, instance.differentiate(-1.0), 0.0);
        assertEquals(0.0, instance.differentiate(4.0), 0.0);
        assertEquals(0.0, instance.differentiate(-4.0), 0.0);
        
        for (int i = 0; i < 10; i++)
        {
            double x = random.nextGaussian();
            if (x >= 1 || x <= -1)
            {
                assertEquals(0.0, instance.differentiate(x), 0.0);
            }
            else
            {
                assertEquals(1.0, instance.differentiate(x), 0.0);
            }
        }
    }
    
    /**
     * Test of hardSigmoid method, of class HardTanHFunction.
     */
    @Test
    public void testHardSigmoid()
    {
        assertEquals(0.0, HardTanHFunction.hardTanH(0.0), 0.0);
        assertEquals(0.5, HardTanHFunction.hardTanH(0.5), 0.0);
        assertEquals(-0.5, HardTanHFunction.hardTanH(-0.5), 0.0);
        assertEquals(1.0, HardTanHFunction.hardTanH(1.0), 0.0);
        assertEquals(-1.0, HardTanHFunction.hardTanH(-1.0), 0.0);
        assertEquals(1.0, HardTanHFunction.hardTanH(4.0), 0.0);
        assertEquals(-1.0, HardTanHFunction.hardTanH(-4.0), 0.0);
        
        for (int i = 0; i < 10; i++)
        {
            double x = random.nextGaussian();
            double y =  Math.max(-1, Math.min(1, x));
            assertEquals(y, HardTanHFunction.hardTanH(x), 0.0);
        }
    }
}

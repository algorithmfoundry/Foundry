/*
 * File:            HardSigmoidFunctionTest.java
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
 * Unit tests for class {@link HardSigmoidFunction}.
 * 
 * @author  Justin Basilico
 * @since   4.0.0
 */
public class HardSigmoidFunctionTest
    extends Object
{
    protected Random random = new Random(111114);
    
    /**
     * Creates a new test.
     */
    public HardSigmoidFunctionTest()
    {
        super();
    }
    
    /**
     * Test of constructors of class HardSigmoidFunction.
     */
    @Test
    public void testConstructors()
    {
        HardSigmoidFunction instance = new HardSigmoidFunction();
        assertNotNull(instance);
    }
    
    /**
     * Test of clone method, of class HardSigmoidFunction.
     */
    @Test
    public void testClone()
    {
        HardSigmoidFunction instance = new HardSigmoidFunction();
        assertNotSame(instance, instance.clone());
        assertNotNull(instance.clone());
    }

    /**
     * Test of evaluate method, of class HardSigmoidFunction.
     */
    @Test
    public void testEvaluate()
    {
        HardSigmoidFunction instance = new HardSigmoidFunction();
        
        assertEquals(0.5, instance.evaluate(0.0), 0.0);
        assertEquals(1.0, instance.evaluate(4.0), 0.0);
        assertEquals(0.0, instance.evaluate(-4.0), 0.0);
        
        for (int i = 0; i < 10; i++)
        {
            double x = random.nextGaussian();
            double y =  Math.max(0, Math.min(1, (0.2 * x) + 0.5));
            assertEquals(y, instance.evaluate(x), 0.0);
        }
    }

    /**
     * Test of differentiate method, of class HardSigmoidFunction.
     */
    @Test
    public void testDifferentiate()
    {
        HardSigmoidFunction instance = new HardSigmoidFunction();
        
        assertEquals(0.2, instance.differentiate(0.0), 0.0);
        assertEquals(0.0, instance.differentiate(4.0), 0.0);
        assertEquals(0.0, instance.differentiate(-4.0), 0.0);
        
        for (int i = 0; i < 10; i++)
        {
            double x = random.nextGaussian();
            if (x > 2.5 || x < -2.5)
            {
                assertEquals(0.0, instance.differentiate(x), 0.0);
            }
            else
            {
                assertEquals(0.2, instance.differentiate(x), 0.0);
            }
        }
    }
    
    /**
     * Test of hardSigmoid method, of class HardSigmoidFunction.
     */
    @Test
    public void testHardSigmoid()
    {
        assertEquals(0.5, HardSigmoidFunction.hardSigmoid(0.0), 0.0);
        assertEquals(1.0, HardSigmoidFunction.hardSigmoid(4.0), 0.0);
        assertEquals(0.0, HardSigmoidFunction.hardSigmoid(-4.0), 0.0);
        
        for (int i = 0; i < 10; i++)
        {
            double x = random.nextGaussian();
            double y =  Math.max(0, Math.min(1, (0.2 * x) + 0.5));
            assertEquals(y, HardSigmoidFunction.hardSigmoid(x), 0.0);
        }
    }
}

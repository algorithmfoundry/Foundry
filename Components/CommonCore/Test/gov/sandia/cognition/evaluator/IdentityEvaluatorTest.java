/*
 * File:            IdentityEvaluatorTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2011 Justin Basilico. All rights reserved.
 */

package gov.sandia.cognition.evaluator;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class IdentityEvaluator.
 *
 * @author  Justin Basilico
 * @since   3.4.0
 */
public class IdentityEvaluatorTest
{

    /**
     * Creates a new test.
     */
    public IdentityEvaluatorTest()
    {
    }

    /**
     * Test of constructors of class IdentityEvaluator.
     */
    @Test
    public void testConstructors()
    {
        IdentityEvaluator<String> instance = new IdentityEvaluator<String>();
        assertNotNull(instance);
    }

    /**
     * Test of clone method, of class IdentityEvaluator.
     */
    @Test
    public void testClone()
    {
        IdentityEvaluator<String> instance = new IdentityEvaluator<String>();
        IdentityEvaluator<String> clone = instance.clone();
        assertNotSame(instance, clone);
        assertNotSame(clone, instance.clone());
    }

    /**
     * Test of evaluate method, of class IdentityEvaluator.
     */
    @Test
    public void testEvaluate()
    {
        IdentityEvaluator<String> instance = new IdentityEvaluator<String>();

        String input = "a";
        assertSame(input, instance.evaluate(input));

        input = new String("another");
        assertSame(input, instance.evaluate(input));

        input = null;
        assertSame(input, instance.evaluate(input));
    }


    /**
     * Test of reverse method, of class IdentityEvaluator.
     */
    @Test
    public void testReverse()
    {
        IdentityEvaluator<String> instance = new IdentityEvaluator<String>();
        assertSame(instance, instance.reverse());
        assertSame(instance, instance.reverse().reverse());
    }

    /**
     * Test of create method, of class IdentityEvaluator.
     */
    @Test
    public void testCreate()
    {
        IdentityEvaluator<String> instance = IdentityEvaluator.create();
        assertNotNull(instance);
        assertNotSame(instance, IdentityEvaluator.create());
    }

}

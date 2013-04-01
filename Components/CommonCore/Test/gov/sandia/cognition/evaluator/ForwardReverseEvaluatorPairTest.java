/*
 * File:            ForwardReverseEvaluatorPairTest.java
 * Authors:         Justin Basilico
 * Project:         Cognitive Foundry Common Core
 * 
 * Copyright 2012, Cognitive Foundry. All rights reserved.
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.util.AbstractCloneableSerializable;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for class {@link ForwardReverseEvaluatorPair}.
 *
 * @author  Justin Basilico
 * @since   3.3.3
 */
public class ForwardReverseEvaluatorPairTest
    extends Object
{

    /**
     * Creates a new test.
     */
    public ForwardReverseEvaluatorPairTest() 
    {
        super();
    }

    @Test
    public void testConstructors()
    {
        Evaluator<String, Integer> forward = null;
        Evaluator<Integer, String> reverse = null;
        ForwardReverseEvaluatorPair<String, Integer, Evaluator<String, Integer>, Evaluator<Integer, String>> instance =
            new ForwardReverseEvaluatorPair<String, Integer, Evaluator<String, Integer>, Evaluator<Integer, String>>();
        assertSame(forward, instance.getForward());
        assertSame(reverse, instance.getReverse());

        forward = new StringToInteger();
        reverse = new IntegerToString();
        instance = new ForwardReverseEvaluatorPair<String, Integer, Evaluator<String, Integer>, Evaluator<Integer, String>>(forward, reverse);
        assertSame(forward, instance.getForward());
        assertSame(reverse, instance.getReverse());
    }

    /**
     * Test of reverse method, of class ForwardReverseEvaluatorPair.
     */
    @Test
    public void testReverse()
    {
        Evaluator<String, Integer> forward = new StringToInteger();
        Evaluator<Integer, String> reverse = new IntegerToString();
        ForwardReverseEvaluatorPair<String, Integer, Evaluator<String, Integer>, Evaluator<Integer, String>> instance =
            ForwardReverseEvaluatorPair.create(forward, reverse);
        assertSame(forward, instance.getForward());
        assertSame(reverse, instance.getReverse());
        
        ForwardReverseEvaluatorPair<Integer, String, Evaluator<Integer, String>, Evaluator<String, Integer>> result =
            instance.reverse();
        assertSame(reverse, result.getForward());
        assertSame(forward, result.getReverse());
    }

    /**
     * Test of evaluate method, of class ForwardReverseEvaluatorPair.
     */
    @Test
    public void testEvaluate()
    {
        Evaluator<String, Integer> forward = new StringToInteger();
        Evaluator<Integer, String> reverse = new IntegerToString();
        ForwardReverseEvaluatorPair<String, Integer, Evaluator<String, Integer>, Evaluator<Integer, String>> instance =
            ForwardReverseEvaluatorPair.create(forward, reverse);
        assertEquals(4, (int) instance.evaluate("4"));
        assertEquals(5, (int) instance.evaluate("5"));
    }

    /**
     * Test of evaluateReverse method, of class ForwardReverseEvaluatorPair.
     */
    @Test
    public void testEvaluateReverse()
    {
        Evaluator<String, Integer> forward = new StringToInteger();
        Evaluator<Integer, String> reverse = new IntegerToString();
        ForwardReverseEvaluatorPair<String, Integer, Evaluator<String, Integer>, Evaluator<Integer, String>> instance =
            ForwardReverseEvaluatorPair.create(forward, reverse);
        assertEquals("4", instance.evaluateReverse(4));
        assertEquals("5", instance.evaluateReverse(5));
    }

    /**
     * Test of getForward method, of class ForwardReverseEvaluatorPair.
     */
    @Test
    public void testGetForward()
    {
        this.testSetForward();
    }

    /**
     * Test of setForward method, of class ForwardReverseEvaluatorPair.
     */
    @Test
    public void testSetForward()
    {
        Evaluator<String, Integer> forward = null;
        ForwardReverseEvaluatorPair<String, Integer, Evaluator<String, Integer>, Evaluator<Integer, String>> instance =
            new ForwardReverseEvaluatorPair<String, Integer, Evaluator<String, Integer>, Evaluator<Integer, String>>();
        assertSame(forward, instance.getForward());

        forward = new StringToInteger();
        instance.setForward(forward);
        assertSame(forward, instance.getForward());
    }

    /**
     * Test of getReverse method, of class ForwardReverseEvaluatorPair.
     */
    @Test
    public void testGetReverse()
    {
        this.testSetReverse();
    }

    /**
     * Test of setReverse method, of class ForwardReverseEvaluatorPair.
     */
    @Test
    public void testSetReverse()
    {
        Evaluator<Integer, String> reverse = null;
        ForwardReverseEvaluatorPair<String, Integer, Evaluator<String, Integer>, Evaluator<Integer, String>> instance =
            new ForwardReverseEvaluatorPair<String, Integer, Evaluator<String, Integer>, Evaluator<Integer, String>>();
        assertSame(reverse, instance.getReverse());

        reverse = new IntegerToString();
        instance.setReverse(reverse);
        assertSame(reverse, instance.getReverse());
    }

    /**
     * Test of create method, of class ForwardReverseEvaluatorPair.
     */
    @Test
    public void testCreate()
    {
        Evaluator<String, Integer> forward = new StringToInteger();
        Evaluator<Integer, String> reverse = new IntegerToString();
        ForwardReverseEvaluatorPair<String, Integer, Evaluator<String, Integer>, Evaluator<Integer, String>> instance =
            ForwardReverseEvaluatorPair.create(forward, reverse);
        assertSame(forward, instance.getForward());
        assertSame(reverse, instance.getReverse());
    }

    public static class StringToInteger
        extends AbstractCloneableSerializable
        implements Evaluator<String, Integer>
    {

        @Override
        public Integer evaluate(
            final String input)
        {
            return Integer.valueOf(input);
        }

    }

    public static class IntegerToString
        extends AbstractCloneableSerializable
        implements Evaluator<Integer, String>
    {

        @Override
        public String evaluate(
            final Integer input)
        {
            return input.toString();
        }
        
    }

}
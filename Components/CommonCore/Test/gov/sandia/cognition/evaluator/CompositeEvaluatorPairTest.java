/*
 * File:                CompositeEvaluatorPairTest.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright September 25, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 * See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.evaluator;

import junit.framework.TestCase;

/**
 * Unit test for the CompositeEvaluatorPair class.
 *
 * @author  Justin Basilico
 * @since   2.1
 */
public class CompositeEvaluatorPairTest
    extends TestCase
{
    /**
     * Creates a new test.
     *
     * @param   testName The test name.
     */
    public CompositeEvaluatorPairTest(
        String testName)
    {
        super(testName);
    }
    
    /**
     * Test of constructors of CompositeEvaluatorPair.
     */
    public void testConstructors()
    {
        Evaluator<? super Number, ? extends Number> first = null;
        Evaluator<? super Number, ? extends Number> second = null;
        CompositeEvaluatorPair<Number, Number, Number> instance = 
            new CompositeEvaluatorPair<Number, Number, Number>();
        assertSame(first, instance.getFirst());
        assertSame(second, instance.getSecond());
        
        first = new Adder();
        second = new Multiplier();
        
        instance = 
            new CompositeEvaluatorPair<Number, Number, Number>(first, second);
        assertSame(first, instance.getFirst());
        assertSame(second, instance.getSecond());
    }

    /**
     * Test of evaluate method, of class CompositeEvaluatorPair.
     */
    public void testEvaluate()
    {
        Adder adder = new Adder(4.0);
        Multiplier multiplier = new Multiplier(7.0);
        CompositeEvaluatorPair<Number, Number, Number> instance = 
            new CompositeEvaluatorPair<Number, Number, Number>(adder, multiplier);
        
        double input = 10;
        assertEquals((input + 4.0) * 7.0, instance.evaluate(input));
        
        instance.setFirst(multiplier);
        instance.setSecond(adder);
        assertEquals((input * 7.0) + 4.0, instance.evaluate(input));
        
        instance.setFirst(multiplier);
        instance.setSecond(multiplier);
        assertEquals((input * 7.0) * 7.0, instance.evaluate(input));
        
        instance.setFirst(adder);
        instance.setSecond(adder);
        assertEquals((input + 4.0) + 4.0, instance.evaluate(input));
    }

    /**
     * Test of create method, of class CompositeEvaluatorPair.
     */
    public void testCreate()
    {
        Adder first = new Adder();
        Multiplier second = new Multiplier();
        
        CompositeEvaluatorPair<Number, Double, Double> result = 
            CompositeEvaluatorPair.create(first, second);
        assertSame(first, result.getFirst());
        assertSame(second, result.getSecond());
    }

    public static class Adder
        implements Evaluator<Number, Double>
    {
        public double constant;
        public Adder()
        {
            this(0.0);
        }
        
        public Adder(
            final double constant)
        {
            this.constant = constant;
        }
        
        public Double evaluate(
            final Number input)
        {
            return input.doubleValue() + this.constant;
        }
    }
    
    public static class Multiplier
        implements Evaluator<Number, Double>
    {
        public double scalar;
        
        public Multiplier()
        {
            this(0.0);
        }
        
        public Multiplier(
            final double scalar)
        {
            this.scalar = scalar;
        }
        
        public Double evaluate(
            final Number input)
        {
            return input.doubleValue() * this.scalar;
        }
    }
}

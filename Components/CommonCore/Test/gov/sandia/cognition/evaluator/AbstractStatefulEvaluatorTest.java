/*
 * File:                AbstractStatefulEvaluatorTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jul 7, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.evaluator;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.mtj.Vector3;
import gov.sandia.cognition.util.ObjectUtil;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for AbstractStatefulEvaluatorTest.
 *
 * @author krdixon
 */
public class AbstractStatefulEvaluatorTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public final Random RANDOM = new Random( 1 );

    /**
     * Default tolerance of the regression tests, {@value}.
     */
    public final double TOLERANCE = 1e-5;

    /**
     * Tests for class AbstractStatefulEvaluatorTest.
     * @param testName Name of the test.
     */
    public AbstractStatefulEvaluatorTest(
        String testName)
    {
        super(testName);
    }

    public static class StatefulFunction
        extends AbstractStatefulEvaluator<Vector,Vector,Vector>
    {

        public StatefulFunction()
        {
            super();
        }

        public StatefulFunction(
            Vector state )
        {
            super( state );
        }

        public Vector createDefaultState()
        {
            return new Vector3(1.0,2.0,3.0);
        }

        public Vector evaluate(
            Vector input)
        {
            this.getState().plusEquals(input);
            return this.getState();
        }

    }

    public Vector createRandomInput()
    {
        return Vector3.createRandom(RANDOM);
    }

    public StatefulFunction createInstance()
    {
        return new StatefulFunction(this.createRandomInput());
    }

    /**
     * Tests the constructors of class AbstractStatefulEvaluatorTest.
     */
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        StatefulFunction f = new StatefulFunction();
        assertEquals( f.createDefaultState(), f.getState() );

        Vector x = this.createRandomInput();
        f = new StatefulFunction( x );
        assertSame( x, f.getState() );
        
    }

    /**
     * Test of clone method, of class AbstractStatefulEvaluator.
     */
    public void testClone()
    {
        System.out.println("clone");

        StatefulFunction f = this.createInstance();
        StatefulFunction clone = (StatefulFunction) f.clone();
        System.out.println( "f:\n" + ObjectUtil.toString(f) );
        System.out.println( "clone:\n" + ObjectUtil.toString(clone) );
        assertNotNull( clone );
        assertNotSame( f, clone );
        assertNotNull( clone.getState() );
        assertNotSame( f.getState(), clone.getState() );
        assertEquals( f.getState(), clone.getState() );
        assertFalse( clone.getState().equals( clone.createDefaultState() ) );

        Vector originalState = f.getState().clone();
        assertEquals( originalState, f.getState() );
        assertEquals( originalState, clone.getState() );
        clone.getState().scaleEquals(RANDOM.nextDouble());
        assertEquals( originalState, f.getState() );
        assertFalse( originalState.equals( clone.getState() ) );

    }

    /**
     * evaluate(state)
     */
    public void testEvaluateState()
    {
        System.out.println( "evaluate(State)" );

        StatefulFunction f = this.createInstance();
        Vector defaultState = f.createDefaultState();
        f.resetState();
        Vector input = this.createRandomInput();
        Vector o1 = f.evaluate(input);
        Vector o2 = f.evaluate(input, defaultState );
        assertNotSame( o1, o2 );
        assertEquals( o1, o2 );

    }

    /**
     * Test of evaluate method, of class AbstractStatefulEvaluator.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        StatefulFunction f = this.createInstance();
        Vector originalState = f.getState().clone();
        f.evaluate(this.createRandomInput());
        assertFalse( originalState.equals( f.getState() ) );

    }

    /**
     * Test of getState method, of class AbstractStatefulEvaluator.
     */
    public void testGetState()
    {
        System.out.println("getState");
        StatefulFunction f = this.createInstance();
        assertNotNull( f.getState() );

    }

    /**
     * Test of setState method, of class AbstractStatefulEvaluator.
     */
    public void testSetState()
    {
        System.out.println("setState");
        StatefulFunction f = this.createInstance();
        Vector s = this.createRandomInput();
        f.setState(s);
        assertSame( s, f.getState() );

    }

    /**
     * Test of resetState method, of class AbstractStatefulEvaluator.
     */
    public void testResetState()
    {
        System.out.println("resetState");
        StatefulFunction f = this.createInstance();
        Vector v = f.createDefaultState();
        assertFalse( v.equals( f.getState() ) );
        f.resetState();
        assertEquals( v, f.getState() );

    }



}

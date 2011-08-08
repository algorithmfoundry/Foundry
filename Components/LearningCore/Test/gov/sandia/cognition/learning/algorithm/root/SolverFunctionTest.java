/*
 * File:                SolverFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright Feb 9, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.root;

import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import java.util.Random;
import junit.framework.TestCase;

/**
 * SolverFunctionTest
 * @author krdixon
 */
public class SolverFunctionTest
    extends TestCase
{

    public Random random = new Random( 1 );

    /**
     * Creates the test
     * @param testName
     * Test name.
     */
    public SolverFunctionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of getTarget method, of class SolverFunction.
     */
    public void testGetTarget()
    {
        System.out.println("getTarget");
        SolverFunction instance = new SolverFunction();
        assertEquals( 0.0, instance.getTarget() );

        double t = random.nextGaussian();
        instance = new SolverFunction(t, null);
        assertEquals( t, instance.getTarget() );
    }

    /**
     * Test of setTarget method, of class SolverFunction.
     */
    public void testSetTarget()
    {
        System.out.println("setTarget");
        double t = random.nextGaussian();
        SolverFunction instance = new SolverFunction(t, null);
        assertEquals( t, instance.getTarget() );
        double t2 = t + random.nextGaussian();
        instance.setTarget(t2);
        assertEquals( t2, instance.getTarget() );
    }

    /**
     * Test of getInternalFunction method, of class SolverFunction.
     */
    public void testGetInternalFunction()
    {
        System.out.println("getInternalFunction");
        SolverFunction instance = new SolverFunction();
        assertNull( instance.getInternalFunction() );

        Evaluator<Double,Double> f = new AtanFunction();
        instance = new SolverFunction( random.nextGaussian(), f );
        assertSame( f, instance.getInternalFunction() );
    }

    /**
     * Test of setInternalFunction method, of class SolverFunction.
     */
    public void testSetInternalFunction()
    {
        System.out.println("setInternalFunction");
        Evaluator<Double,Double> f = new AtanFunction();
        SolverFunction instance =
            new SolverFunction( random.nextGaussian(), f );
        assertSame( f, instance.getInternalFunction() );

        Evaluator<Double,Double> f2 = new AtanFunction();
        instance.setInternalFunction(f2);
        assertSame( f2, instance.getInternalFunction() );
    }

    /**
     * Test of evaluate method, of class SolverFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        double t = random.nextGaussian();
        Evaluator<Double,Double> f = new AtanFunction();

        double x = random.nextGaussian();

        double y = f.evaluate(x) - t;

        SolverFunction instance = new SolverFunction( t, f );
        assertEquals( y, instance.evaluate(x) );
    }

}

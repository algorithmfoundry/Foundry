/*
 * File:                ElementWiseVectorFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 25, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.learning.algorithm.minimization.line.interpolator.LineBracketInterpolatorTestHarness.CosineFunction;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import gov.sandia.cognition.math.UnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import junit.framework.TestCase;
import java.util.Random;

/**
 * Unit tests for ElementWiseVectorFunctionTest.
 *
 * @author krdixon
 */
public class ElementWiseVectorFunctionTest
    extends TestCase
{

    /**
     * Random number generator to use for a fixed random seed.
     */
    public Random random = new Random( 1 );

    /**
     * Tests for class ElementWiseVectorFunctionTest.
     * @param testName Name of the test.
     */
    public ElementWiseVectorFunctionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors, of class ElementWiseVectorFunction.
     */
    public void testConstructors()
    {
        ElementWiseVectorFunction instance = new ElementWiseVectorFunction();
        assertNotNull(instance.getScalarFunction());
        assertTrue(instance.getScalarFunction() instanceof LinearFunction);

        UnivariateScalarFunction scalarFunction = new CosineFunction();
        instance = new ElementWiseVectorFunction(scalarFunction);
        assertSame(scalarFunction, instance.getScalarFunction());

        instance = new ElementWiseVectorFunction(instance);
        assertNotNull(instance.getScalarFunction());
        assertTrue(instance.getScalarFunction() instanceof CosineFunction);
    }

    /**
     * Test of clone method, of class ElementWiseVectorFunction.
     */
    public void testClone()
    {
        System.out.println("clone");
        ElementWiseVectorFunction instance = new ElementWiseVectorFunction( new AtanFunction() );
        ElementWiseVectorFunction clone = instance.clone();
        assertNotNull( clone );
        assertNotSame( instance, clone );
        assertNotNull( instance.getScalarFunction() );
        assertNotNull( clone.getScalarFunction() );
        assertNotSame( instance.getScalarFunction(), clone.getScalarFunction() );

        int M = random.nextInt( 10 ) + 1;
        double r = 2.0;
        Vector x1 = VectorFactory.getDefault().createUniformRandom(M, -r, r, random );
        Vector y1 = instance.evaluate(x1);
        Vector y1hat = clone.evaluate(x1);
        assertEquals( y1, y1hat );

        ElementWiseVectorFunction c2 = new ElementWiseVectorFunction( instance );
        assertNotSame( instance, c2 );
        assertNotNull( c2.getScalarFunction() );
        assertNotSame( instance.getScalarFunction(), c2.getScalarFunction() );

    }

    /**
     * Test of getScalarFunction method, of class ElementWiseVectorFunction.
     */
    public void testGetScalarFunction()
    {
        System.out.println("getScalarFunction");
        UnivariateScalarFunction f = new AtanFunction();
        ElementWiseVectorFunction instance = new ElementWiseVectorFunction( f );
        assertNotNull( instance.getScalarFunction() );
        assertSame( f, instance.getScalarFunction() );
    }

    /**
     * Test of setScalarFunction method, of class ElementWiseVectorFunction.
     */
    public void testSetScalarFunction()
    {
        System.out.println("setScalarFunction");
        UnivariateScalarFunction f = new AtanFunction();
        ElementWiseVectorFunction instance = new ElementWiseVectorFunction( f );
        assertNotNull( instance.getScalarFunction() );
        assertSame( f, instance.getScalarFunction() );
        instance.setScalarFunction(null);
        assertNull( instance.getScalarFunction() );
        instance.setScalarFunction(f);
        assertSame( f, instance.getScalarFunction() );
    }

    /**
     * Test of evaluate method, of class ElementWiseVectorFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");
        UnivariateScalarFunction f = new AtanFunction();
        ElementWiseVectorFunction instance = new ElementWiseVectorFunction( f );

        int M = random.nextInt( 10 ) + 1;
        double r = 2.0;
        Vector x1 = VectorFactory.getDefault().createUniformRandom(M, -r, r, random );
        Vector y1 = instance.evaluate(x1);
        assertEquals( M, y1.getDimensionality() );
        for( int i = 0; i < M; i++ )
        {
            assertEquals( f.evaluate(x1.getElement(i)), y1.getElement(i) );
        }

    }

}

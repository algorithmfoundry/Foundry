/*
 * File:                ElementWiseDifferentiableVectorFunctionTest.java
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

import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.learning.function.scalar.CosineFunction;
import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;

/**
 * Unit tests for ElementWiseDifferentiableVectorFunctionTest.
 *
 * @author krdixon
 */
public class ElementWiseDifferentiableVectorFunctionTest
    extends ElementWiseVectorFunctionTest
{

    /**
     * Tests for class ElementWiseDifferentiableVectorFunctionTest.
     * @param testName Name of the test.
     */
    public ElementWiseDifferentiableVectorFunctionTest(
        String testName)
    {
        super(testName);
    }

    /**
     * Test of constructors of class ElementWiseDifferentiableVectorFunction.
     */
    @Override
    public void testConstructors()
    {
        ElementWiseDifferentiableVectorFunction instance =
            new ElementWiseDifferentiableVectorFunction();
        assertNotNull(instance.getScalarFunction());
        assertTrue(instance.getScalarFunction() instanceof LinearFunction);

        DifferentiableUnivariateScalarFunction scalarFunction = new CosineFunction();
        instance = new ElementWiseDifferentiableVectorFunction(scalarFunction);
        assertSame(scalarFunction, instance.getScalarFunction());
    }

    /**
     * Test of differentiate method, of class ElementWiseDifferentiableVectorFunction.
     */
    public void testDifferentiate()
    {
        System.out.println("differentiate");
        DifferentiableUnivariateScalarFunction f = new AtanFunction();
        ElementWiseDifferentiableVectorFunction instance =
            new ElementWiseDifferentiableVectorFunction( f );

        int M = random.nextInt( 10 ) + 1;
        double r = 2.0;
        Vector input = VectorFactory.getDefault().createUniformRandom(M, -r, r, random);
        Matrix dydx = instance.differentiate(input);
        assertEquals( M, dydx.getNumRows() );
        assertEquals( M, dydx.getNumColumns() );
        for( int i = 0; i < M; i++ )
        {
            for( int j = 0; j < M; j++ )
            {
                double v = dydx.getElement(i, j);
                if( i != j )
                {
                    assertEquals( 0.0, v );
                }
                else
                {
                    assertEquals( f.differentiate(input.getElement(i)), v );
                }
            }
        }

    }

    /**
     * Test of getScalarFunction method, of class ElementWiseDifferentiableVectorFunction.
     */
    public void testGetScalarFunction()
    {
        System.out.println("getScalarFunction");
        DifferentiableUnivariateScalarFunction f = new AtanFunction();
        ElementWiseDifferentiableVectorFunction instance =
            new ElementWiseDifferentiableVectorFunction( f );
        assertSame( f, instance.getScalarFunction() );
    }

}

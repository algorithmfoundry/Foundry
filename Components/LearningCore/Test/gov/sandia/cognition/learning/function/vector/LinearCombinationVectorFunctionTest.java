/*
 * File:                LinearCombinationVectorFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 20, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.learning.function.LinearCombinationFunctionTestHarness;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.learning.function.scalar.PolynomialFunction;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.math.matrix.mtj.Vector3;

/**
 *
 * @author Kevin R. Dixon
 */
public class LinearCombinationVectorFunctionTest
    extends LinearCombinationFunctionTestHarness<Vector,Vector>
{

    public LinearCombinationVectorFunctionTest(String testName)
    {
        super(testName);
    }

    public LinearCombinationVectorFunction createInstance()
    {
        VectorFunction f1 =
            new ElementWiseVectorFunction(new AtanFunction());
        VectorFunction f2 =
            new ElementWiseVectorFunction(new PolynomialFunction(2.0));
        LinearCombinationVectorFunction lcvf =
            new LinearCombinationVectorFunction(f1, f2);
        Vector c = VectorFactory.getDefault().copyValues(RANDOM.nextGaussian(), RANDOM.nextGaussian());
        lcvf.setCoefficients(c);
        return lcvf;

    }

    @Override
    public Vector createRandomInput()
    {
        return Vector3.createRandom(RANDOM);
    }


    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        LinearCombinationVectorFunction f =
            new LinearCombinationVectorFunction(
                new ElementWiseVectorFunction(new AtanFunction()) );
        assertEquals( 1, f.getBasisFunctions().size() );
        assertEquals( 1.0, f.getCoefficients().getElement(0) );

    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.LinearCombinationVectorFunction.
     */
    public void testEvaluate()
    {
        System.out.println("evaluate");

        for (int i = 0; i < 100; i++)
        {
            int M = RANDOM.nextInt(10) + 1;
            Vector x = VectorFactory.getDefault().createUniformRandom(M, -1, 1, RANDOM);
            LinearCombinationVectorFunction f = this.createInstance();
            Vector y = VectorFactory.getDefault().createVector(M);
            for (int n = 0; n < f.getBasisFunctions().size(); n++)
            {
                y.plusEquals(f.getBasisFunctions().get(n).evaluate(x).scale(
                    f.getCoefficients().getElement(n)));
            }
            Vector yhat = f.evaluate(x);
            if (y.equals(yhat, 1e-5) == false)
            {
                assertEquals(y, yhat);
            }
        }
    }

}

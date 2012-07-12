/*
 * File:                LinearCombinationScalarFunctionTest.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 6, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.learning.function.LinearCombinationFunctionTestHarness;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Arrays;

/**
 *
 * @author Kevin R. Dixon
 */
public class LinearCombinationScalarFunctionTest
    extends LinearCombinationFunctionTestHarness<Double,Double>
{

    public LinearCombinationScalarFunctionTest(
        String testName )
    {
        super( testName );
    }

    /**
     * 
     * @return
     */
    public LinearCombinationScalarFunction<Double> createInstance()
    {
        @SuppressWarnings("unchecked")
        LinearCombinationScalarFunction<Double> f =
            new LinearCombinationScalarFunction<Double>(Arrays.asList(
            new PolynomialFunction( 0.0 ),
            new PolynomialFunction( 1.0 ),
            new PolynomialFunction( 2.0 ),
            new SigmoidFunction() ));
        int num = f.getBasisFunctions().size();
        Vector coefficients =
            VectorFactory.getDefault().createUniformRandom( num, -1.0, 1.0, RANDOM );
        f.setCoefficients( coefficients );
        return f;

    }

    /**
     * Test of evaluate method, of class gov.sandia.cognition.learning.util.function.LinearCombinationScalarFunction.
     */
    public void testEvaluate()
    {
        System.out.println( "evaluate" );

        for (int n = 0; n < 100; n++)
        {
            double x = RANDOM.nextGaussian();
            double y = 0.0;
            LinearCombinationScalarFunction<Double> f = createInstance();
            for (int i = 0; i < f.getBasisFunctions().size(); i++)
            {
                y += f.getBasisFunctions().get( i ).evaluate( x ) * f.getCoefficients().getElement( i );
            }

            assertEquals( y, f.evaluate( x ), TOLERANCE );
        }
    }

    @Override
    public Double createRandomInput()
    {
        return RANDOM.nextGaussian();
    }

    @Override
    public void testConstructors()
    {
        System.out.println( "Constructors" );

        @SuppressWarnings("unchecked")
        LinearCombinationScalarFunction<Double> f =
            new LinearCombinationScalarFunction<Double>( Arrays.asList(new AtanFunction()));
        assertEquals( 1, f.getBasisFunctions().size() );
        assertEquals( 1.0, f.getCoefficients().getElement(0) );

    }

}

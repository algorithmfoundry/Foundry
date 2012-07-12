/*
 * File:                LinearCombinationScalarFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 10, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.learning.function.regression.Regressor;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.LinearCombinationFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A weighted linear combination of scalar functions.
 *
 * @param <InputType> Input class for the basis function
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class LinearCombinationScalarFunction<InputType>
    extends LinearCombinationFunction<InputType, Double>
    implements Regressor<InputType>
{
    
    /**
     * Creates a new instance of LinearCombinationFunction
     * @param basisFunctions
     * Collection of basis functions to combine to produce the output
     */
    public LinearCombinationScalarFunction(
        Collection<? extends Evaluator<InputType, Double>> basisFunctions )
    {
        this( new ArrayList<Evaluator<InputType, Double>>( basisFunctions ),
            VectorFactory.getDefault().createVector( basisFunctions.size(), 1.0 ) );
    }

    /**
     * Creates a new instance of LinearCombinationFunction
     * @param basisFunctions
     * Collection of basis functions to combine to produce the output
     * @param coefficients
     * Coefficients for the basisFunctions
     */
    public LinearCombinationScalarFunction(
        ArrayList<? extends Evaluator<InputType, Double>> basisFunctions,
        Vector coefficients )
    {
        super( basisFunctions, coefficients );
    }

    @Override
    public LinearCombinationScalarFunction<InputType> clone()
    {
        return (LinearCombinationScalarFunction<InputType>) super.clone();
    }

    public Double evaluate(
        InputType input )
    {
        return this.evaluateAsDouble(input);
    }

    public double evaluateAsDouble(
        final InputType input)
    {
        double output = 0.0;
        for (int i = 0; i < this.getCoefficients().getDimensionality(); i++)
        {
            double weight = this.getCoefficients().getElement( i );
            if (weight != 0.0)
            {
                output += weight * this.getBasisFunctions().get( i ).evaluate( input );
            }
        }

        return output;

    }

}

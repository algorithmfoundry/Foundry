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

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.math.RingAccumulator;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.learning.function.LinearCombinationFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * A weighted linear combination of scalar functions.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class LinearCombinationVectorFunction
    extends LinearCombinationFunction<Vector, Vector>
    implements VectorFunction
{

    /**
     * Creates a new instance of LinearCombinationFunction
     * @param basisFunctions
     * Collection of basis functions to combine to produce the output
     */
    public LinearCombinationVectorFunction(
        VectorFunction... basisFunctions )
    {
        this( Arrays.asList( basisFunctions ) );
    }

    /**
     * Creates a new instance of LinearCombinationFunction
     * @param basisFunctions
     * Collection of basis functions to combine to produce the output
     */
    public LinearCombinationVectorFunction(
        Collection<? extends Evaluator<? super Vector, ? extends Vector>> basisFunctions )
    {
        this( new ArrayList<Evaluator<? super Vector, ? extends Vector>>( basisFunctions ),
            VectorFactory.getDefault().createVector( basisFunctions.size(), 1.0 ) );
    }

    /**
     * Creates a new instance of LinearCombinationFunction
     * @param basisFunctions
     * Collection of basis functions to combine to produce the output
     * @param coefficients
     * Coefficients for the basisFunctions
     */
    public LinearCombinationVectorFunction(
        ArrayList<? extends Evaluator<? super Vector, ? extends Vector>> basisFunctions,
        Vector coefficients )
    {
        super( basisFunctions, coefficients );
    }

    @Override
    public LinearCombinationVectorFunction clone()
    {
        return (LinearCombinationVectorFunction) super.clone();
    }

    public Vector evaluate(
        Vector input )
    {

        int num = this.getBasisFunctions().size();
        RingAccumulator<Vector> y = new RingAccumulator<Vector>();
        for (int n = 0; n < num; n++)
        {
            double weight = this.getCoefficients().getElement( n );
            if (weight != 0.0)
            {
                y.accumulate(
                    this.getBasisFunctions().get( n ).evaluate( input ).scale( weight ) );
            }
        }

        return y.getSum();

    }

}

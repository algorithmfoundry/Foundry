/*
 * File:                ElementWiseDifferentiableVectorFunction.java
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

import gov.sandia.cognition.learning.function.scalar.LinearFunction;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.UnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.DifferentiableVectorFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * An ElementWiseVectorFunction that is also a DifferentiableVectorFunction
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class ElementWiseDifferentiableVectorFunction
    extends ElementWiseVectorFunction
    implements DifferentiableVectorFunction
{
// TODO: This class really shouldn't extend the ElementWiseVectorFunction since
// it puts an extra requirement on the scalar function used, which could lead to
// problems.
// --jdbasil (2010-03-23)

    /**
     * Creates a new {@code ElementWiseDifferentiableVectorFunction} with a
     * linear scalar function as the default function (f(x_i) = x_i).
     */
    public ElementWiseDifferentiableVectorFunction()
    {
        this(new LinearFunction());
    }

    /**
     * Creates a new instance of ElementWiseDifferentiableVectorFunction
     * @param scalarFunction
     * Differentiable scalar function 
     */
    public ElementWiseDifferentiableVectorFunction(
        DifferentiableUnivariateScalarFunction scalarFunction)
    {
        super(scalarFunction);
    }

    public Matrix differentiate(
        Vector input)
    {
        int M = input.getDimensionality();
        Matrix dydx = MatrixFactory.getDefault().createMatrix(M, M);
        for (int i = 0; i < M; i++)
        {
            dydx.setElement(i, i,
                this.getScalarFunction().differentiate(input.getElement(i)));
        }

        return dydx;
    }

    @Override
    public DifferentiableUnivariateScalarFunction getScalarFunction()
    {
        // Cast the function to be differentiable.
        return (DifferentiableUnivariateScalarFunction)
            super.getScalarFunction();
    }

    @Override
    public void setScalarFunction(
        final UnivariateScalarFunction scalarFunction)
    {
        // Force the function to be differentiable.
        super.setScalarFunction(
            (DifferentiableUnivariateScalarFunction) scalarFunction);
    }

}

/*
 * File:                DifferentiableSquashedMatrixMultiplyVectorFunction.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 28, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.math.DifferentiableUnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.DifferentiableVectorFunction;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * A GradientDescenable version of a SquashedMatrixMultiplyVectorFunction, in
 * other words, a SquashedMatrixMultiplyVectorFunction where the squashing
 * function is differentiable
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class DifferentiableSquashedMatrixMultiplyVectorFunction
    extends SquashedMatrixMultiplyVectorFunction
    implements GradientDescendable, DifferentiableVectorFunction
{

    /**
     * Default Constructor
     */
    public DifferentiableSquashedMatrixMultiplyVectorFunction()
    {
        this( 1, 1, new AtanFunction() );
    }

    /**
     * Creates a new instance of SquashedMatrixMultiplyVectorFunction
     * 
     * @param numInputs
     * Number of inputs of the function (number of matrix columns)
     * @param numOutputs
     * Number of outputs of the function (number of matrix rows)
     * @param scalarFunction 
     * Function to apply to each output
     */
    public DifferentiableSquashedMatrixMultiplyVectorFunction(
        int numInputs,
        int numOutputs,
        DifferentiableUnivariateScalarFunction scalarFunction )
    {
        this( new MatrixMultiplyVectorFunction( numInputs, numOutputs ),
            new ElementWiseDifferentiableVectorFunction( scalarFunction ) );
    }
    
    
    /**
     * Creates a new instance of DifferentiableSquashedMatrixMultiplyVectorFunction
     * @param matrixMultiply 
     * GradientDescendable that multiplies an input by the internal matrix
     * @param squashingFunction 
     * VectorFunction that is applied to the output of the matrix multiply
     */
    public DifferentiableSquashedMatrixMultiplyVectorFunction(
        MatrixMultiplyVectorFunction matrixMultiply,
        DifferentiableVectorFunction squashingFunction )
    {
        super( matrixMultiply, squashingFunction );
    }

    /**
     * Creates a new instance of DifferentiableSquashedMatrixMultiplyVectorFunction
     * @param matrixMultiply 
     * GradientDescendable that multiplies an input by the internal matrix
     * @param scalarSquashingFunction 
     * scalar function that is applied to the output of the matrix multiply
     */
    public DifferentiableSquashedMatrixMultiplyVectorFunction(
        MatrixMultiplyVectorFunction matrixMultiply,
        DifferentiableUnivariateScalarFunction scalarSquashingFunction )
    {
        this( matrixMultiply, new ElementWiseDifferentiableVectorFunction(
            scalarSquashingFunction ) );
    }

    /**
     * Creates a new instance of DifferentiableSquashedMatrixMultiplyVectorFunction
     * 
     * @param other DifferentiableSquashedMatrixMultiplyVectorFunction to copy
     */
    public DifferentiableSquashedMatrixMultiplyVectorFunction(
        DifferentiableSquashedMatrixMultiplyVectorFunction other )
    {
        super( other );
    }

    @Override
    public DifferentiableVectorFunction getSquashingFunction()
    {
        return (DifferentiableVectorFunction) super.getSquashingFunction();
    }

    public Matrix computeParameterGradient(
        Vector input )
    {
        Matrix gradient =
            this.getMatrixMultiply().computeParameterGradient( input );

        Vector y = this.getMatrixMultiply().evaluate( input );
        Matrix derivative = this.getSquashingFunction().differentiate( y );

        return derivative.times( gradient );
    }

    @Override
    public DifferentiableSquashedMatrixMultiplyVectorFunction clone()
    {
        return (DifferentiableSquashedMatrixMultiplyVectorFunction) super.clone();
    }

    public Matrix differentiate(
        Vector input )
    {
        Matrix dudx = this.getMatrixMultiply().differentiate( input );

        Vector u = this.getMatrixMultiply().evaluate( input );
        Matrix dydu = this.getSquashingFunction().differentiate( u );

        return dydu.times( dudx );

    }

}

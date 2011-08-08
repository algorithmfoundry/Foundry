/*
 * File:                SquashedMatrixMultiplyVectorFunction.java
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

import gov.sandia.cognition.learning.function.scalar.AtanFunction;
import gov.sandia.cognition.math.UnivariateScalarFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.math.matrix.VectorizableVectorFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * A VectorizableVectorFunction that is a matrix multiply followed by a
 * VectorFunction... a no-hidden-layer neural network
 *
 * @author Kevin R. Dixon
 * @since  1.0
 *
 */
public class SquashedMatrixMultiplyVectorFunction
    extends AbstractCloneableSerializable
    implements VectorizableVectorFunction,
    VectorInputEvaluator<Vector,Vector>,
    VectorOutputEvaluator<Vector,Vector>
{

    /**
     * GradientDescendable that multiplies an input by the internal matrix
     */
    private MatrixMultiplyVectorFunction matrixMultiply;

    /**
     * VectorFunction that is applied to the output of the matrix multiply
     */
    private VectorFunction squashingFunction;

    /**
     * Default constructor.
     */
    public SquashedMatrixMultiplyVectorFunction()
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
    public SquashedMatrixMultiplyVectorFunction(
        int numInputs,
        int numOutputs,
        UnivariateScalarFunction scalarFunction )
    {
        this( new MatrixMultiplyVectorFunction( numInputs, numOutputs ),
            new ElementWiseVectorFunction( scalarFunction ) );
    }
    
    /**
     * Creates a new instance of SquashedMatrixMultiplyVectorFunction
     * @param matrixMultiply 
     * GradientDescendable that multiplies an input by the internal matrix
     * @param squashingFunction 
     * VectorFunction that is applied to the output of the matrix multiply
     */
    public SquashedMatrixMultiplyVectorFunction(
        MatrixMultiplyVectorFunction matrixMultiply,
        VectorFunction squashingFunction )
    {
        this.setMatrixMultiply( matrixMultiply );
        this.setSquashingFunction( squashingFunction );
    }

    /**
     * Creates a new instance of SquashedMatrixMultiplyVectorFunction
     * @param other SquashedMatrixMultiplyVectorFunction to copy
     */
    public SquashedMatrixMultiplyVectorFunction(
        SquashedMatrixMultiplyVectorFunction other )
    {
        this( other.getMatrixMultiply().clone(), other.getSquashingFunction() );
    }

    /**
     * Getter for matrixMultiply
     * @return 
     * GradientDescendable that multiplies an input by the internal matrix
     */
    public MatrixMultiplyVectorFunction getMatrixMultiply()
    {
        return this.matrixMultiply;
    }

    /**
     * Setter for matrixMultiply
     * @param matrixMultiply 
     * GradientDescendable that multiplies an input by the internal matrix
     */
    public void setMatrixMultiply(
        MatrixMultiplyVectorFunction matrixMultiply )
    {
        this.matrixMultiply = matrixMultiply;
    }

    /**
     * Getter for squashingFunction
     * @return 
     * VectorFunction that is applied to the output of the matrix multiply
     */
    public VectorFunction getSquashingFunction()
    {
        return this.squashingFunction;
    }

    /**
     * Setter for squashingFunction
     * @param squashingFunction 
     * VectorFunction that is applied to the output of the matrix multiply
     */
    public void setSquashingFunction(
        VectorFunction squashingFunction )
    {
        this.squashingFunction = squashingFunction;
    }

    public Vector convertToVector()
    {
        return this.getMatrixMultiply().convertToVector();
    }

    public void convertFromVector(
        Vector parameters )
    {
        this.getMatrixMultiply().convertFromVector( parameters );
    }

    public Vector evaluate(
        Vector input )
    {
        return this.squashingFunction.evaluate(
            this.matrixMultiply.evaluate( input ) );
    }

    @Override
    public SquashedMatrixMultiplyVectorFunction clone()
    {
        SquashedMatrixMultiplyVectorFunction clone =
            (SquashedMatrixMultiplyVectorFunction) super.clone();
        clone.setMatrixMultiply( 
            ObjectUtil.cloneSafe(this.getMatrixMultiply()) );
        return clone;
    }

    @Override
    public String toString()
    {
        String retval = "Squashing: " + this.getSquashingFunction().getClass()
            + "Weights:\n" + this.getMatrixMultiply().getInternalMatrix();
        return retval;
    }

    public int getInputDimensionality()
    {
        return this.getMatrixMultiply().getInputDimensionality();
    }

    public int getOutputDimensionality()
    {
        return this.getMatrixMultiply().getOutputDimensionality();
    }

}

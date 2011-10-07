/*
 * File:                MultivariateDiscriminantWithBias.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 3, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * A multivariate discriminant (matrix multiply) plus a constant vector
 * that gets added to the output of the discriminant.
 * @author Kevin R. Dixon
 * @since 3.2.1
 */
public class MultivariateDiscriminantWithBias 
    extends MultivariateDiscriminant
{

    /**
     * Bias term that gets added the output of the matrix multiplication.
     */
    protected Vector bias;

    /**
     * Default constructor.
     */
    public MultivariateDiscriminantWithBias()
    {
        this( 1, 1 );
    }

    /**
     * Creates a new MultivariateDiscriminantWithBias
     * @param numInputs
     * Number of inputs of the function (number of matrix columns)
     * @param numOutputs
     * Number of outputs of the function (number of matrix rows)
     */
    public MultivariateDiscriminantWithBias(
        final int numInputs,
        final int numOutputs )
    {
        this( MatrixFactory.getDefault().createIdentity(numOutputs, numInputs) );
    }

    /**
     * Creates a new instance of MultivariateDiscriminantWithBias.
     *
     * @param discriminant internal matrix to premultiply input vectors by.
     */
    public MultivariateDiscriminantWithBias(
        final Matrix discriminant )
    {
        this( discriminant,
            VectorFactory.getDefault().createVector(discriminant.getNumRows()) );
    }

    /**
     * Creates a new instance of MultivariateDiscriminantWithBias.
     *
     * @param discriminant internal matrix to premultiply input vectors by.
     * @param bias
     * Bias term that gets added the output of the matrix multiplication.
     */
    public MultivariateDiscriminantWithBias(
        final Matrix discriminant,
        final Vector bias )
    {
        super( discriminant );
        this.setBias(bias);
    }


    @Override
    public MultivariateDiscriminantWithBias clone()
    {
        MultivariateDiscriminantWithBias clone =
            (MultivariateDiscriminantWithBias) super.clone();
        clone.setBias( ObjectUtil.cloneSafe( this.getBias() ) );
        return clone;
    }

    @Override
    public Vector evaluate(
        Vector input)
    {
        Vector discriminant = super.evaluate( input );
        discriminant.plusEquals(this.bias);
        return discriminant;
    }

    /**
     * Getter for bias
     * @return
     * Bias term that gets added the output of the matrix multiplication.
     */
    public Vector getBias()
    {
        return this.bias;
    }

    /**
     * Setter for bias
     * @param bias
     * Bias term that gets added the output of the matrix multiplication.
     */
    public void setBias(
        Vector bias)
    {
        bias.assertDimensionalityEquals( this.getOutputDimensionality() );
        this.bias = bias;
    }

    @Override
    public Vector convertToVector()
    {
        Vector p = super.convertToVector();
        return p.stack( this.getBias() );
    }

    @Override
    public void convertFromVector(
        Vector parameters)
    {
        final int num =
            this.getInputDimensionality() * this.getOutputDimensionality();
        parameters.assertDimensionalityEquals(num + this.getOutputDimensionality());
        Vector mp = parameters.subVector(0,num-1);
        Vector bp = parameters.subVector(num, num+this.getOutputDimensionality()-1);
        super.convertFromVector( mp );
        this.bias.convertFromVector(bp);
    }

    @Override
    public Matrix computeParameterGradient(
        Vector input)
    {

        Matrix g = super.computeParameterGradient(input);

        // Derivation of this gradient assumes that the parameters are
        // column-stacked from the underlying matrices...
        // This makes a gradient which looks like:
        // [ x0 0   0   x1  0   0]
        // [ 0  x0  0   0   x1  0]
        // [ 0  0   x0  0   0   x1]
        // And so forth...
        //
        // Since the bias is a constant input, we append the gradient
        // with another block of
        // [ G 1 0 0 ]
        // [ G 0 1 0 ]
        // [ G 0 0 1 ]
        //
        // And so forth...
        int M = g.getNumRows();
        int N = g.getNumColumns();

        Matrix gi = MatrixFactory.getSparseDefault().createMatrix(M, N+M);
        gi.setSubMatrix(0, 0, g);
        for( int i = 0; i < M; i++ )
        {
            gi.setElement( i, N+i, 1.0 );
        }
        
        return gi;

    }



    
}

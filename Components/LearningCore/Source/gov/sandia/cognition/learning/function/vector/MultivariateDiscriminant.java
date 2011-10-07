/*
 * File:                MultivariateDiscriminant.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 26, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.vector;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.math.matrix.VectorizableDifferentiableVectorFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * Allows learning algorithms (vectorizing, differentiating) on a matrix*vector
 * multiply.
 *
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2006-10-06",
    changesNeeded=true,
    comments={
        "Can you just add a comment for why the differentiation code is correct?",
        "Otherwise, class looks fine."
    },
    response=@CodeReviewResponse(
        respondent="Kevin R. Dixon",
        date="2006-10-06",
        moreChangesNeeded=false,
        comments="Added in-code comment describing the derivation of the differentiation formulae."
    )
)
public class MultivariateDiscriminant
    extends AbstractCloneableSerializable
    implements VectorizableDifferentiableVectorFunction,
    VectorInputEvaluator<Vector,Vector>,
    VectorOutputEvaluator<Vector,Vector>,
    GradientDescendable
{

    /** Internal matrix to premultiply input vectors by. */
    private Matrix discriminant;

    /**
     * Default constructor.
     */
    public MultivariateDiscriminant()
    {
        this( 1, 1 );
    }

    /**
     * Creates a new MultivariateDiscriminant
     * @param numInputs
     * Number of inputs of the function (number of matrix columns)
     * @param numOutputs
     * Number of outputs of the function (number of matrix rows)
     */
    public MultivariateDiscriminant(
        final int numInputs,
        final int numOutputs )
    {
        this( MatrixFactory.getDefault().createIdentity(numOutputs, numInputs) );
    }
    
    /**
     * Creates a new instance of MatrixVectorMultiplyFunction.
     *
     * @param discriminant internal matrix to premultiply input vectors by.
     */
    public MultivariateDiscriminant(
        final Matrix discriminant )
    {
        this.setDiscriminant( discriminant );
    }

    /**
     * Copy constructor
     * @param other MultivariateDiscriminant to copy
     */
    public MultivariateDiscriminant(
        final MultivariateDiscriminant other )
    {
        this( other.getDiscriminant().clone() );
    }

    @Override
    public MultivariateDiscriminant clone()
    {
        MultivariateDiscriminant clone =
            (MultivariateDiscriminant) super.clone();
        clone.setDiscriminant( this.getDiscriminant().clone() );
        return clone;
    }

    /**
     * Getter for discriminant.
     *
     * @return internal matrix to premultiply input vectors by
     */
    public Matrix getDiscriminant()
    {
        return this.discriminant;
    }

    /**
     * Setter for discriminant
     *
     * @param discriminant internal matrix to premultiply input vectors by
     */
    protected void setDiscriminant(
        final Matrix discriminant )
    {
        this.discriminant = discriminant;
    }

    /**
     * Creates a row-stacked version of the discriminant.
     *
     * @return row-stacked Vector representing the discriminant
     */
    @Override
    public Vector convertToVector()
    {
        return this.discriminant.convertToVector();
    }

    /**
     * Uploads a matrix from a row-stacked vector of parameters.
     *
     * @param parameters row-stacked version of discriminant
     */
    @Override
    public void convertFromVector(
        Vector parameters )
    {
        this.discriminant.convertFromVector( parameters );
    }

    @Override
    public Vector evaluate(
        final Vector input )
    {
        return this.discriminant.times( input );
    }

    @Override
    public Matrix differentiate(
        final Vector input )
    {
        return this.getDiscriminant();
    }

    @Override
    public Matrix computeParameterGradient(
        final Vector input )
    {
        return computeParameterGradient(this.discriminant, input);
    }

    /**
     * Computes the parameter gradient of the given matrix post-multiplied
     * by the input Vector
     * @param matrix
     * Matrix to pre-multiply the input Vector.
     * @param input
     * Vector to post-multiply the Matrix.
     * @return
     * Derivative of the matrix elements with respect to the inputs.
     */
    public static Matrix computeParameterGradient(
        final Matrix matrix,
        final Vector input )
    {
        int M = matrix.getNumRows();
        int N = matrix.getNumColumns();

//        Matrix gradient = MatrixFactory.getDefault().createMatrix( M, M * N );
        Matrix gradient = MatrixFactory.getSparseDefault().createMatrix( M, M * N );

        // Derivation of this gradient assumes that the parameters are
        // column-stacked from the underlying matrices...
        // This makes a gradient which looks like:
        // [ x0 0   0   x1  0   0]
        // [ 0  x0  0   0   x1  0]
        // [ 0  0   x0  0   0   x1]
        // And so forth...
        //
        int columnIndex = 0;
        for (int j = 0; j < N; j++)
        {
            double inputValue = input.getElement( j );
            for (int i = 0; i < M; i++)
            {
                gradient.setElement( i, columnIndex, inputValue );
                columnIndex++;
            }
        }

        return gradient;

    }


    @Override
    public String toString()
    {
        return this.getDiscriminant().toString();
    }

    @Override
    public int getInputDimensionality()
    {
        return this.getDiscriminant().getNumColumns();
    }

    @Override
    public int getOutputDimensionality()
    {
        return this.getDiscriminant().getNumRows();
    }
    
}

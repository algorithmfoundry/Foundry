/*
 * File:                MatrixMultiplyVectorFunction.java
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
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.algorithm.gradient.GradientDescendable;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorOutputEvaluator;
import gov.sandia.cognition.math.matrix.VectorizableDifferentiableVectorFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

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
public class MatrixMultiplyVectorFunction
    extends AbstractCloneableSerializable
    implements VectorizableDifferentiableVectorFunction,
    VectorInputEvaluator<Vector,Vector>,
    VectorOutputEvaluator<Vector,Vector>,
    GradientDescendable
{

    /** Internal matrix to premultiply input vectors by. */
    private Matrix internalMatrix;

    /**
     * Default constructor.
     */
    public MatrixMultiplyVectorFunction()
    {
        this( 1, 1 );
    }

    /**
     * Creates a new MatrixMultiplyVectorFunction
     * @param numInputs
     * Number of inputs of the function (number of matrix columns)
     * @param numOutputs
     * Number of outputs of the function (number of matrix rows)
     */
    public MatrixMultiplyVectorFunction(
        int numInputs,
        int numOutputs )
    {
        this( MatrixFactory.getDefault().createIdentity(numOutputs, numInputs) );
    }
    
    /**
     * Creates a new instance of MatrixVectorMultiplyFunction.
     *
     * @param internalMatrix internal matrix to premultiply input vectors by.
     */
    public MatrixMultiplyVectorFunction(
        Matrix internalMatrix )
    {
        this.setInternalMatrix( internalMatrix );
    }

    /**
     * Copy constructor
     * @param other MatrixMultiplyVectorFunction to copy
     */
    public MatrixMultiplyVectorFunction(
        MatrixMultiplyVectorFunction other )
    {
        this( other.getInternalMatrix().clone() );
    }

    @Override
    public MatrixMultiplyVectorFunction clone()
    {
        MatrixMultiplyVectorFunction clone =
            (MatrixMultiplyVectorFunction) super.clone();
        clone.setInternalMatrix( this.getInternalMatrix().clone() );
        return clone;
    }

    /**
     * Getter for internalMatrix.
     *
     * @return internal matrix to premultiply input vectors by
     */
    public Matrix getInternalMatrix()
    {
        return this.internalMatrix;
    }

    /**
     * Setter for internalMatrix
     *
     * @param internalMatrix internal matrix to premultiply input vectors by
     */
    protected void setInternalMatrix(
        Matrix internalMatrix )
    {
        this.internalMatrix = internalMatrix;
    }

    /**
     * Creates a row-stacked version of the internalMatrix.
     *
     * @return row-stacked Vector representing the internalMatrix
     */
    public Vector convertToVector()
    {
        return this.internalMatrix.convertToVector();
    }

    /**
     * Uploads a matrix from a row-stacked vector of parameters.
     *
     * @param parameters row-stacked version of internalMatrix
     */
    public void convertFromVector(
        Vector parameters )
    {
        this.internalMatrix.convertFromVector( parameters );
    }

    public Vector evaluate(
        Vector input )
    {
        return this.internalMatrix.times( input );
    }

    public Matrix differentiate(
        Vector input )
    {
        return this.getInternalMatrix();
    }

    public Matrix computeParameterGradient(
        Vector input )
    {
        return computeParameterGradient(this.internalMatrix, input);
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
        Matrix matrix,
        Vector input )
    {
        int M = matrix.getNumRows();
        int N = matrix.getNumColumns();

        Matrix gradient = MatrixFactory.getDefault().createMatrix( M, M * N );

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
        return this.getInternalMatrix().toString();
    }

    public int getInputDimensionality()
    {
        return this.getInternalMatrix().getNumColumns();
    }

    public int getOutputDimensionality()
    {
        return this.getInternalMatrix().getNumRows();
    }

    /**
     * Closed-form solver for a matrix-multiply function.  Uses a least-squares
     * pseudoinverse solver from LAPACK (if available) given a Dataset of
     * input-output pairs.  This is identical to linear regression.
     * Can also be used with WeightedInputOutputPairs in the dataset.
     * This is called "Multivariate Linear Regression" by social scientists
     */
    public static class ClosedFormSolver
        extends AbstractCloneableSerializable
        implements SupervisedBatchLearner<Vector, Vector, MatrixMultiplyVectorFunction>
    {

        /**
         * Creates a MatrixMultiplyFunction from the given Dataset of
         * input-output paired Vectors.  Uses a least-squares solver from LAPACK
         * (if available) to estimate the Matrix that produces the best
         * estimates.  Generally extremely fast, but is roughly n^3 complexity
         * @param data Dataset from which to create the MatrixMultiplyFunction
         * @return Least-squares optimal MatrixMultiplyFunction, given the
         * input-output pairs.
         */
        public MatrixMultiplyVectorFunction learn(
            Collection<? extends InputOutputPair<? extends Vector, Vector>> data )
        {

            InputOutputPair<? extends Vector, Vector> first = data.iterator().next();

            int M = first.getOutput().getDimensionality();
            int N = first.getInput().getDimensionality();
            int num = data.size();

            Matrix Y = MatrixFactory.getDefault().createMatrix( M, num );
            Matrix X = MatrixFactory.getDefault().createMatrix( N, num );

            int n = 0;
            for (InputOutputPair<? extends Vector, Vector> pair : data)
            {
                Vector x = pair.getInput();
                Vector y = pair.getOutput();

                // Don't use scaleEquals() here because it will corrupt
                // the values in the Dataset, thus we have to make a copy
                // using the regular scale() method
                double weight = DatasetUtil.getWeight(pair);
                if (weight != 1.0)
                {
                    x = x.scale( weight );
                    y = y.scale( weight );
                }

                X.setColumn( n, x );
                Y.setColumn( n, y );
                n++;
            }

            return ClosedFormSolver.learn( X, Y );

        }

        /**
         * Solves for the Matrix "A" in the equation
         * Y = A*X, uses DenseMatrix.solveSingular() if the regular LAPACK
         * solver fails, which may be more computationally expensive
         * @param X
         * Matrix "X" in the equation Y = A*X
         * @param Y
         * Matrix "Y" in the equation Y = A*X
         * @return 
         * Estimate of Matrix "A" in the equation Y=A*X
         */
        public static MatrixMultiplyVectorFunction learn(
            final Matrix X, final Matrix Y )
        {

            Matrix Xt = X.transpose();
            Matrix Yt = Y.transpose();
            Matrix A = Xt.solve( Yt ).transpose();

            return new MatrixMultiplyVectorFunction( A );

        }

    }

}

/*
 * File:                MultivariateLinearRegression.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 22, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.vector.MultivariateDiscriminant;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * Performs multivariate regression, without explicitly estimating a bias term
 * and without regularization.  To use a bias term, append a constant to the
 * inputs with something like DatasetUtil.appendBias.
 * @author Kevin R. Dixon
 * @since 3.2.1
 */
public class MultivariateLinearRegression 
    extends AbstractCloneableSerializable
    implements SupervisedBatchLearner<Vector, Vector, MultivariateDiscriminant>
{

    /**
     * Tolerance for the pseudo inverse in the learn method, {@value}.
     */
    public static final double DEFAULT_PSEUDO_INVERSE_TOLERANCE = 1e-10;

    /**
     * Flag to use a pseudoinverse.  True to use the expensive, but more
     * accurate, pseudoinverse routine.  False uses a very fast, but
     * numerically less stable LU solver.  Default value is "true".
     */
    private boolean usePseudoInverse;

    /** 
     * Creates a new instance of MultivariateLinearRegression 
     */
    public MultivariateLinearRegression()
    {
        this.setUsePseudoInverse(true);
    }

    @Override
    public MultivariateLinearRegression clone()
    {
        return (MultivariateLinearRegression) super.clone();
    }

    @Override
    public MultivariateDiscriminant learn(
        Collection<? extends InputOutputPair<? extends Vector, Vector>> data)
    {
        // We need to cheat to figure out how many coefficients we need...
        // So we'll push the first sample through... wasteful, but general
        InputOutputPair<? extends Vector,Vector> first =
            CollectionUtil.getFirst(data);
        int M = first.getInput().getDimensionality();
        int N = first.getOutput().getDimensionality();
        int numSamples = data.size();

        Matrix X = MatrixFactory.getDefault().createMatrix( numSamples, M );
        Matrix Y = MatrixFactory.getDefault().createMatrix( numSamples, N );

        // The matrix equation looks like:
        // y = C*[f0(x) f1(x) ... fn(x) ], fi() is the ith basis function
        int i = 0;
        for (InputOutputPair<? extends Vector, Vector> pair : data)
        {
            Vector output = pair.getOutput();
            Vector input = pair.getInput().convertToVector();
            final double weight = DatasetUtil.getWeight(pair);
            if( weight != 1.0 )
            {
                // Can't use scaleEquals because that would modify the
                // underlying dataset
                input = input.scale(weight);
                output = output.scale(weight);
            }
            X.setRow( i, input );
            Y.setRow( i, output );
            i++;
        }

        // Solve for the coefficients
        Matrix coefficients;
        if( this.getUsePseudoInverse() )
        {
            Matrix psuedoInverse = X.pseudoInverse(DEFAULT_PSEUDO_INVERSE_TOLERANCE);
            coefficients = psuedoInverse.times(Y).transpose();
        }
        else
        {
            coefficients = X.solve( Y ).transpose();
        }
        return new MultivariateDiscriminant( coefficients );
    }

    /**
     * Getter for usePseudoInverse
     * @return
     * Flag to use a pseudoinverse.  True to use the expensive, but more
     * accurate, pseudoinverse routine.  False uses a very fast, but
     * numerically less stable LU solver.  Default value is "true".
     */
    public boolean getUsePseudoInverse()
    {
        return this.usePseudoInverse;
    }

    /**
     * Setter for usePseudoInverse
     * @param usePseudoInverse
     * Flag to use a pseudoinverse.  True to use the expensive, but more
     * accurate, pseudoinverse routine.  False uses a very fast, but
     * numerically less stable LU solver.  Default value is "true".
     */
    public void setUsePseudoInverse(
        boolean usePseudoInverse )
    {
        this.usePseudoInverse = usePseudoInverse;
    }

}

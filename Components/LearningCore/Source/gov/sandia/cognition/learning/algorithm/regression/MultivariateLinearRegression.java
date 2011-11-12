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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.SupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.vector.MultivariateDiscriminantWithBias;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.Collection;

/**
 * Performs multivariate regression with an explicit bias term, with optional
 * L2 regularization.
 * @author Kevin R. Dixon
 * @since 3.2.1
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Wikipedia",
            title="Linear regression",
            type=PublicationType.WebPage,
            year=2008,
            url="http://en.wikipedia.org/wiki/Linear_regression"
        )
        ,
        @PublicationReference(
            author="Wikipedia",
            title="Tikhonov regularization",
            type=PublicationType.WebPage,
            year=2011,
            url="http://en.wikipedia.org/wiki/Tikhonov_regularization",
            notes="Despite what Wikipedia says, this is always called Ridge Regression"
        )
    }
)
public class MultivariateLinearRegression 
    extends AbstractCloneableSerializable
    implements SupervisedBatchLearner<Vector, Vector, MultivariateDiscriminantWithBias>
{

    /**
     * Default regularization, {@value}.
     */
    public static final double DEFAULT_REGULARIZATION = 0.0;    
    
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
     * L2 ridge regularization term, must be nonnegative, a value of zero is
     * equivalent to unregularized regression.
     */
    private double regularization;    
    
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
    public MultivariateDiscriminantWithBias learn(
        Collection<? extends InputOutputPair<? extends Vector, Vector>> data)
    {
        // We need to cheat to figure out how many coefficients we need...
        // So we'll push the first sample through... wasteful, but general
        InputOutputPair<? extends Vector,Vector> first =
            CollectionUtil.getFirst(data);
        int M = first.getInput().getDimensionality();
        int N = first.getOutput().getDimensionality();
        int numSamples = data.size();

        Matrix X =  MatrixFactory.getDefault().createMatrix( M+1, numSamples );
        Matrix Xt = MatrixFactory.getDefault().createMatrix( numSamples, M+1 );
        Matrix Y =  MatrixFactory.getDefault().createMatrix( N, numSamples );
        Matrix Yt =  MatrixFactory.getDefault().createMatrix( numSamples, N );

        // The matrix equation looks like:
        // y = C*[f0(x) f1(x) ... fn(x) ], fi() is the ith basis function
        int i = 0;
        Vector one = VectorFactory.getDefault().copyValues(1.0);
        for (InputOutputPair<? extends Vector, Vector> pair : data)
        {
            Vector output = pair.getOutput();
            Vector input = pair.getInput().convertToVector().stack(one);
            final double weight = DatasetUtil.getWeight(pair);
            if( weight != 1.0 )
            {
                // We can use scaleEquals() here because of the stack() method
                input.scaleEquals(weight);
                output = output.scale(weight);
            }
            Xt.setRow( i, input );
            X.setColumn( i, input );
            Y.setColumn( i, output );
            Yt.setRow( i, output );
            i++;
        }

        // Solve for the coefficients
        Matrix coefficients;
        if( this.getUsePseudoInverse() )
        {
            Matrix pseudoInverse = Xt.pseudoInverse(DEFAULT_PSEUDO_INVERSE_TOLERANCE);
            coefficients = pseudoInverse.times( Yt ).transpose();
        }
        else
        {
            Matrix lhs = X.times( Xt );
            if( this.regularization > 0.0 )
            {
                for( i = 0; i < M+1; i++ )
                {
                    double v = lhs.getElement(i, i);
                    lhs.setElement(i, i, v + this.regularization);
                }
            }
            Matrix rhs = Y.times( Xt );
            coefficients = lhs.solve( rhs.transpose() ).transpose();
        }
        
        Matrix discriminant = coefficients.getSubMatrix(0, N-1, 0, M-1);
        Vector bias = coefficients.getColumn(M);
        
        return new MultivariateDiscriminantWithBias( discriminant, bias );
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

    /**
     * Getter for regularization
     * @return
     * L2 ridge regularization term, must be nonnegative, a value of zero is
     * equivalent to unregularized regression.
     */
    public double getRegularization()
    {
        return this.regularization;
    }

    /**
     * Setter for regularization
     * @param regularization
     * L2 ridge regularization term, must be nonnegative, a value of zero is
     * equivalent to unregularized regression.
     */
    public void setRegularization(
        double regularization)
    {
        ArgumentChecker.assertIsNonNegative("regularization", regularization);
        this.regularization = regularization;
    }    
    
}

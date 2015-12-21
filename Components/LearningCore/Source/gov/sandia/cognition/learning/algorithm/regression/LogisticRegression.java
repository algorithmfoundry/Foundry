/*
 * File:                LogisticRegression.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Nov 27, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.regression;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationReferences;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.evaluator.CompositeEvaluatorPair;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeSupervisedBatchLearner;
import gov.sandia.cognition.learning.data.DatasetUtil;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.learning.function.scalar.LinearDiscriminantWithBias;
import gov.sandia.cognition.math.ProbabilityUtil;
import gov.sandia.cognition.math.matrix.DiagonalMatrix;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.statistics.distribution.LogisticDistribution;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Performs Logistic Regression by means of the iterative reweighted least
 * squares (IRLS) algorithm, where the logistic function has an explicit bias
 * term, and a diagonal L2 regularization term.  When the regularization term
 * is zero, this is equivalent to unregularized regression.  The targets for
 * the data should be probabilities, [0,1].
 * 
 * @author Kevin R. Dixon
 * @since 2.0
 */
@PublicationReferences(
    references={
        @PublicationReference(
            author="Tommi S. Jaakkola",
            title="Machine learning: lecture 5",
            type=PublicationType.WebPage,
            year=2004,
            url="http://www.ai.mit.edu/courses/6.867-f04/lectures/lecture-5-ho.pdf",
            notes="Good formulation of logistic regression on slides 15-20"
        ),
        @PublicationReference(
            author={
                "Paul Komarek",
                "Andrew Moore"
            },
            title="Making Logistic Regression A Core Data Mining Tool With TR-IRLS",
            publication="Proceedings of the 5th International Conference on Data Mining Machine Learning",
            type=PublicationType.Conference,
            year=2005,
            url="http://www.autonlab.org/autonweb/14717.html",
            notes="Good practical overview of logistic regression"
        ),
        @PublicationReference(
            author="Christopher M. Bishop",
            title="Pattern Recognition and Machine Learning",
            type=PublicationType.Book,
            year=2006,
            pages={207,208},
            notes="Section 4.3.3"
        )
    }
)
public class LogisticRegression
    extends AbstractAnytimeSupervisedBatchLearner<Vectorizable,Double,LogisticRegression.Function>
{

    /**
     * Default number of iterations before stopping, {@value}
     */
    public static final int DEFAULT_MAX_ITERATIONS = 100;
    
    /**
     * Default tolerance change in weights before stopping, {@value}
     */
    public static final double DEFAULT_TOLERANCE = 1e-10;
    
    /**
     * Default regularization, {@value}.
     */
    public static final double DEFAULT_REGULARIZATION = 0.0;

    /**
     * The object to optimize, used as a factory on successive runs of the
     * algorithm.
     */
    private LogisticRegression.Function objectToOptimize;

    /**
     * Return value from the algorithm
     */
    private LogisticRegression.Function result;
    
    /**
     * Tolerance change in weights before stopping
     */
    private double tolerance;

    /**
     * L2 ridge regularization term, must be nonnegative, a value of zero is
     * equivalent to unregularized regression.
     */
    private double regularization;

    /**
     * Default constructor, with no regularization.
     */
    public LogisticRegression()
    {
        this( DEFAULT_REGULARIZATION );
    }
    
    /**
     * Creates a new instance of LogisticRegression
     * @param regularization
     * L2 ridge regularization term, must be nonnegative, a value of zero is
     * equivalent to unregularized regression.
     */
    public LogisticRegression(
        double regularization )
    {
        this( regularization, DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS );
    }
    
    /**
     * Creates a new instance of LogisticRegression
     * @param regularization
     * L2 ridge regularization term, must be nonnegative, a value of zero is
     * equivalent to unregularized regression.
     * @param tolerance
     * Tolerance change in weights before stopping
     * @param maxIterations
     * Maximum number of iterations before stopping
     */
    public LogisticRegression(
        double regularization,
        double tolerance,
        int maxIterations )
    {
        super( maxIterations );
        this.setRegularization(regularization);
        this.setTolerance( tolerance );
    }
    
    /**
     * Weighting for each sample
     */
    private transient DiagonalMatrix W;
    
    /**
     * Derivative of each sample's estimate
     */
    private transient DiagonalMatrix R;

    /**
     * Inverse of R
     */
    private transient DiagonalMatrix Ri;

    /**
     * Data matrix where each column is an input sample
     */
    private transient Matrix X;
    
    /**
     * Transpose of the data matrix
     */
    private transient Matrix Xt;
    
    /**
     * Target value minus the estimated value
     */
    private transient Vector err;

    @Override
    public LogisticRegression clone()
    {
        LogisticRegression clone = (LogisticRegression) super.clone();
        clone.setObjectToOptimize(
            ObjectUtil.cloneSafe( this.getObjectToOptimize() ) );
        clone.setResult(
            ObjectUtil.cloneSafe( this.getResult() ) );
        return clone;
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        
        int M = this.data.iterator().next().getInput().convertToVector().getDimensionality();
        int N = this.data.size();
        
        if( this.getObjectToOptimize() == null )
        {
            this.setObjectToOptimize( new Function( M ) );
        }
        
        this.setResult( this.getObjectToOptimize().clone() );
        
        this.R = MatrixFactory.getDiagonalDefault().createMatrix( N, N );
        this.Ri = MatrixFactory.getDiagonalDefault().createMatrix( N, N );
        this.X = MatrixFactory.getDefault().createMatrix( M+1, N );
        this.err = VectorFactory.getDefault().createVector( N );
        this.W = MatrixFactory.getDiagonalDefault().createMatrix( N, N );
        
        int n = 0;
        Vector one = VectorFactory.getDefault().copyValues(1.0);
        for( InputOutputPair<? extends Vectorizable,Double> sample : this.data )
        {
            ProbabilityUtil.assertIsProbability(sample.getOutput());
            this.X.setColumn( n, sample.getInput().convertToVector().stack(one) );
            this.W.setElement( n, DatasetUtil.getWeight(sample) );
            n++;
        }
        this.Xt = this.X.transpose();
        
        return true;
        
    }

    @Override
    protected boolean step()
    {
        
        int n = 0;
        LogisticRegression.Function f = this.getResult();
        for( InputOutputPair<? extends Vectorizable,Double> sample : this.data )
        {
            final double y = sample.getOutput();
            final double yhat = f.evaluate( sample.getInput() );
            final double r = yhat*(1.0-yhat);
            this.err.setElement( n, (y - yhat) );
            this.R.setElement( n, r );
            this.Ri.setElement( n, (r!=0.0) ? 1.0/r : 0.0 );
            n++;
        }
        
        Vector w = f.convertToVector();

        Vector z = w.times( this.X );
        z.plusEquals( this.Ri.times( this.err ) );

        this.R.timesEquals(this.W);
        Matrix lhs = this.X.times( this.R.times( this.Xt ) );
        if( this.regularization != 0.0 )
        {
            final int N = this.X.getNumRows();
            for( int i = 0; i < N; i++ )
            {
                final double v = lhs.getElement(i, i);
                lhs.setElement(i, i, v + this.regularization);
            }
        }

        Vector rhs = this.X.times( this.R.times( z ) );
        
        Vector wnew = lhs.solve( rhs );        
        f.convertFromVector( wnew );
        
        double delta = wnew.minus( w ).norm2();
        return delta > this.getTolerance();
        
    }

    @Override
    protected void cleanupAlgorithm()
    {
        this.X = null;
        this.Xt = null;
        this.err = null;
        this.R = null;
        this.Ri = null;
        this.W = null;
    }

    /**
     * Getter for objectToOptimize
     * @return
     * The object to optimize, used as a factory on successive runs of the
     * algorithm.
     */
    public LogisticRegression.Function getObjectToOptimize()
    {
        return this.objectToOptimize;
    }

    /**
     * Setter for objectToOptimize
     * @param objectToOptimize
     * The object to optimize, used as a factory on successive runs of the
     * algorithm.
     */
    public void setObjectToOptimize(
        LogisticRegression.Function objectToOptimize )
    {
        this.objectToOptimize = objectToOptimize;
    }

    @Override
    public LogisticRegression.Function getResult()
    {
        return this.result;
    }

    /**
     * Setter for result
     * @param result
     * Return value from the algorithm
     */
    public void setResult(
        LogisticRegression.Function result )
    {
        this.result = result;
    }

    /**
     * Getter for tolerance
     * @return
     * Tolerance change in weights before stopping, must be nonnegative.
     */
    public double getTolerance()
    {
        return this.tolerance;
    }

    /**
     * Setter for tolerance
     * @param tolerance
     * Tolerance change in weights before stopping, must be nonnegative.
     */
    public void setTolerance(
        double tolerance )
    {
        ArgumentChecker.assertIsNonNegative("tolerance", tolerance);
        this.tolerance = tolerance;
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
    
    /**
     * Class that is a linear discriminant, followed by a sigmoid function.
     */
    public static class Function
        extends CompositeEvaluatorPair<Vectorizable,Double,Double>
        implements Vectorizable
    {
        /**
         * Creates a new {@link LogisticRegression.Function}.
         */
        public Function()
        {
            super();
        }
        
        /**
         * Creates a new instance of Function
         * @param dimensionality
         * Dimensionality of the inputs
         */
        public Function(
            int dimensionality )
        {   
            super( new LinearDiscriminantWithBias(
                VectorFactory.getDefault().createVector( dimensionality ), 0.0 ),
                new LogisticDistribution.CDF() );
        }
        
        @Override
        public Function clone()
        {
            return (Function) super.clone();
        }

        @Override
        public Vector convertToVector()
        {
            return ((Vectorizable) this.getFirst()).convertToVector();
        }

        @Override
        public void convertFromVector(
            Vector parameters )
        {
            ((Vectorizable) this.getFirst()).convertFromVector( parameters );
        }

        @Override
        public LinearDiscriminantWithBias getFirst()
        {
            return (LinearDiscriminantWithBias) super.getFirst();
        }

        @Override
        public LogisticDistribution.CDF getSecond()
        {
            return (LogisticDistribution.CDF) super.getSecond();
        }
        
    }

}

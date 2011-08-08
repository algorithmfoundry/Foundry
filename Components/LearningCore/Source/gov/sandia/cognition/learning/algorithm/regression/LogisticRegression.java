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
import gov.sandia.cognition.learning.function.scalar.LinearDiscriminant;
import gov.sandia.cognition.learning.function.scalar.SigmoidFunction;
import gov.sandia.cognition.math.matrix.DiagonalMatrix;
import gov.sandia.cognition.math.matrix.Matrix;
import gov.sandia.cognition.math.matrix.MatrixFactory;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFactory;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Performs Logistic Regression by means of the iterative reweighted least
 * squares (IRLS) algorithm.
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
    extends AbstractAnytimeSupervisedBatchLearner<Vector,Double,LogisticRegression.Function>
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
     * Default constructor
     */
    public LogisticRegression()
    {
        this( DEFAULT_TOLERANCE );
    }
    
    /**
     * Creates a new instance of LogisticRegression
     * @param tolerance
     * Tolerance change in weights before stopping
     */
    public LogisticRegression(
        double tolerance )
    {
        this( tolerance, DEFAULT_MAX_ITERATIONS );
    }
    
    /**
     * Creates a new instance of LogisticRegression
     * @param tolerance 
     * Tolerance change in weights before stopping
     * @param maxIterations
     * Maximum number of iterations before stopping
     */
    public LogisticRegression(
        double tolerance,
        int maxIterations )
    {
        super( maxIterations );
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
        
        int M = this.data.iterator().next().getInput().getDimensionality();
        int N = this.data.size();
        
        if( this.getObjectToOptimize() == null )
        {
            this.setObjectToOptimize( new Function( M ) );
        }
        
        this.setResult( this.getObjectToOptimize().clone() );
        
        this.R = MatrixFactory.getDiagonalDefault().createMatrix( N, N );
        this.X = MatrixFactory.getDefault().createMatrix( M, N );
        this.err = VectorFactory.getDefault().createVector( N );
        this.W = MatrixFactory.getDiagonalDefault().createMatrix( N, N );
        
        int n = 0;
        for( InputOutputPair<? extends Vector,Double> sample : this.data )
        {
            this.X.setColumn( n, sample.getInput() );
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
        for( InputOutputPair<? extends Vector,Double> sample : this.data )
        {
            double yhat = f.evaluate( sample.getInput() );
            double y = sample.getOutput();
            this.err.setElement( n, (y - yhat) );
            this.R.setElement( n, yhat*(1.0-yhat) );
            n++;
        }
        
        Vector w = f.convertToVector();
        Vector z = w.times( this.X ).plus( this.R.inverse().times( this.err ) );
  
        Matrix lhs = this.X.times( this.W.times( this.R.times( this.Xt ) ) );
        Vector rhs = this.X.times( this.W.times( this.R.times( z ) ) );
        
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
     * Tolerance change in weights before stopping
     */
    public double getTolerance()
    {
        return this.tolerance;
    }

    /**
     * Setter for tolerance
     * @param tolerance
     * Tolerance change in weights before stopping
     */
    public void setTolerance(
        double tolerance )
    {
        this.tolerance = tolerance;
    }
    
    /**
     * Class that is a linear discriminant, followed by a sigmoid function.
     */
    public static class Function
        extends CompositeEvaluatorPair<Vector,Double,Double>
        implements Vectorizable
    {
        
        /**
         * Creates a new instance of Function
         * @param dimensionality
         * Dimensionality of the inputs
         */
        public Function(
            int dimensionality )
        {   
            super( new LinearDiscriminant( VectorFactory.getDefault().createVector( dimensionality ) ),
                new SigmoidFunction() );
        }
        
        @Override
        public Function clone()
        {
            return (Function) super.clone();
        }

        public Vector convertToVector()
        {
            return ((LinearDiscriminant) this.getFirst()).convertToVector();
        }

        public void convertFromVector(
            Vector parameters )
        {
            ((LinearDiscriminant) this.getFirst()).convertFromVector( parameters );
        }
        
    }
    
}

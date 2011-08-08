/*
 * File:                FunctionMinimizerGradientDescent.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 8, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 *
 */

package gov.sandia.cognition.learning.algorithm.minimization;

import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.Vector;

/**
 * This is an implementation of the classic Gradient Descent algorithm, also
 * known as Steepest Descent, Backpropagation (for neural nets), or Hill 
 * Climbing.  This algorithm takes a small step in the direction indicated by
 * the gradient.  This implementation is "efficient" in that it only uses
 * gradient calls during minimization (not function calls).  We also use a
 * momentum term to mimic "heavy ball" optimization to speed up learning and
 * avoid local minima.
 *<BR><BR>
 * A few words of advice: Don't use this algorithm.  I'm not one of those
 * hard-core "gradient descent sucks" people, but there are uniformly better
 * algorithms out there, such as BFGS and conjugate gradient. It's really here
 * for illustrative purposes and essentially contains absolutely no advantage
 * over BFGS or conjugate gradient minimization, except its simplicity.  If 
 * you're looking for something quick and dirty, then be my guest.  However, 
 * please consider using BFGS or CG instead.  (CG is like GD, but where the 
 * momentum and step size are optimally selected for each step.)  In my 
 * experience, non-derivative algorithms, like Powell's method, are more 
 * efficient and have better convergence than GD.  
 * <BR><BR>
 * Oh, yeah.  The other minimization algorithms don't require you to guess
 * parameters either.
 *
 * @author Kevin R. Dixon
 * @since  2.0
 *
 */
public class FunctionMinimizerGradientDescent 
    extends AbstractAnytimeFunctionMinimizer<Vector, Double, DifferentiableEvaluator<? super Vector, Double, Vector>>    
{
    
    /**
     * The learning rate (or step size), must be (0,1], typically ~0.1
     */
    private double learningRate;

    /**
     * The momentum rate, must be [0,1), typically ~0.8
     */
    private double momentum;

    /**
     * Default learning rate
     */
    public static final double DEFAULT_LEARNING_RATE = 0.1;

    /**
     * Default momentum
     */
    public static final double DEFAULT_MOMENTUM = 0.8;

    /**
     * Default tolerance
     */
    public static final double DEFAULT_TOLERANCE = 1e-7;

    /**
     * Default max iterations
     */
    public static final int DEFAULT_MAX_ITERATIONS = 1000000;

    /**
     * Creates a new instance of FunctionMinimizerGradientDescent
     */
    public FunctionMinimizerGradientDescent()
    {
        this( DEFAULT_LEARNING_RATE, DEFAULT_MOMENTUM );
    }
    
    /**
     * Creates a new instance of FunctionMinimizerGradientDescent
     * @param learningRate 
     * The learning rate (or step size), must be (0,1], typically ~0.1
     * @param momentum 
     * The momentum rate, must be [0,1), typically ~0.8
     */
    public FunctionMinimizerGradientDescent(
        double learningRate,
        double momentum )
    {
        this( learningRate, momentum, null,
            DEFAULT_TOLERANCE, DEFAULT_MAX_ITERATIONS );
    }

    /**
     * Creates a new instance of FunctionMinimizerGradientDescent
     * @param learningRate 
     * The learning rate (or step size), must be (0,1], typically ~0.1
     * @param momentum 
     * The momentum rate, must be [0,1), typically ~0.8
     * @param initialGuess 
     * Initial guess of the minimum
     * @param tolerance 
     * Tolerance of the algorithm, must be >=0.0, typically 1e-5
     * @param maxIterations 
     * Maximum number of iterations before stopping, must be >0, typically ~1000
     */
    public FunctionMinimizerGradientDescent(
        double learningRate,
        double momentum,
        Vector initialGuess,
        double tolerance,
        int maxIterations )
    {
        super( initialGuess, tolerance, maxIterations );
        this.setLearningRate( learningRate );
        this.setMomentum( momentum );
    }

    /**
     * Previous input change, used for adding momentum
     */
    private Vector previousDelta;

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    protected boolean initializeAlgorithm()
    {
        this.previousDelta = null;
        this.result = new DefaultInputOutputPair<Vector, Double>(
            this.initialGuess.clone(), null );

        return true;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    protected boolean step()
    {

        Vector xhat = this.result.getInput();
        
        // Compute the gradient and scale it by the learningRate
        Vector gradient = this.data.differentiate( xhat );
        Vector delta = gradient.scale( -this.learningRate );

        // See if we should add a momentum term
        if (this.previousDelta != null)
        {
            if (this.momentum != 0.0)
            {
                delta.plusEquals( this.previousDelta.scale( this.momentum ) );
            }
        }
        this.previousDelta = delta;

        xhat.plusEquals( delta );

        return !MinimizationStoppingCriterion.convergence(
            xhat, null, gradient, delta, this.getTolerance() );
    }

    /**
     * {@inheritDoc}
     */
    protected void cleanupAlgorithm()
    {
        double yhat = this.data.evaluate( this.result.getInput() );
        this.result = DefaultInputOutputPair.create(
            this.result.getInput(), yhat);
    }

    /**
     * Getter for learningRate
     * @return 
     * The learning rate (or step size), must be (0,1], typically ~0.1
     */
    public double getLearningRate()
    {
        return this.learningRate;
    }

    /**
     * Setter for learningRate
     * @param learningRate 
     * The learning rate (or step size), must be (0,1], typically ~0.1
     */
    public void setLearningRate(
        double learningRate )
    {
        if ((learningRate <= 0.0) ||
            (learningRate > 1.0))
        {
            throw new IllegalArgumentException(
                "Learning rate " + learningRate + " must be (0,1]." );
        }

        this.learningRate = learningRate;
    }

    /**
     * Setter for momentum
     * @return 
     * The momentum rate, must be [0,1), typically ~0.8
     */
    public double getMomentum()
    {
        return this.momentum;
    }

    /**
     * Getter for momentum
     * @param momentum 
     * The momentum rate, must be [0,1), typically ~0.8
     */
    public void setMomentum(
        double momentum )
    {
        if ((momentum < 0.0) ||
            (momentum >= 1.0))
        {
            throw new IllegalArgumentException(
                "momentum must be 0.0 <= momentum < 1.0" );
        }

        this.momentum = momentum;
    }    

}

/*
 * File:                RootFinderNewtonsMethod.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Mar 13, 2010, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.root;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.math.DifferentiableEvaluator;
import gov.sandia.cognition.math.matrix.NumericalDifferentiator;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * Newton's method, sometimes called Newton-Raphson method, uses first-order
 * derivative information to iteratively locate a root.  The algorithm is
 * extremely efficient but can be unstable when the derivative approaches zero.
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Wikipedia",
    title="Newton's Method",
    type=PublicationType.WebPage,
    year=2010,
    url="http://en.wikipedia.org/wiki/Newton%27s_method"
)
public class RootFinderNewtonsMethod 
    extends AbstractRootFinder
{

    /**
     * Resulting estimated location of the root.
     */
    public DefaultInputOutputPair<Double,Double> result;

    /**
     * Creates a new instance of RootFinderNewtonsMethod
     */
    public RootFinderNewtonsMethod()
    {
    }

    @Override
    public RootFinderNewtonsMethod clone()
    {
        RootFinderNewtonsMethod clone = (RootFinderNewtonsMethod) super.clone();
        clone.result = ObjectUtil.cloneSafe( this.getResult() );
        return clone;
    }

    /**
     * Internal Function variable from which we will pull the derivative.
     * If the Evaluator is a DifferentiableEvaluator, then we will use that.
     * Otherwise, we set up an approximation to the derivative.
     */
    private DifferentiableEvaluator<Double,Double,Double> dfdx;
    
    /**
     * Multiplier of the current step.
     */
    private double stepMultiplier;

    /**
     * Scale value to multiply the stepMultiplier on an unsuccessful step.
     */
    private static final double STEP_SCALE = 0.5;

    @Override
    @SuppressWarnings("unchecked")
    protected boolean initializeAlgorithm()
    {
        double x = this.getInitialGuess();
        double fx = this.data.evaluate( x );

        if( this.data instanceof DifferentiableEvaluator )
        {
            this.dfdx = (DifferentiableEvaluator<Double,Double,Double>) this.data;
        }
        else
        {
            this.dfdx = new NumericalDifferentiator.DoubleJacobian(
                this.data, this.getTolerance() );
        }
        this.stepMultiplier = 1.0;
        
        this.result = new DefaultInputOutputPair<Double, Double>( x, fx );
        return true;
    }

    @Override
    protected boolean step()
    {

        double x = this.result.getInput();
        double fx = this.result.getOutput();
        double slope = this.dfdx.differentiate(x);

        double xhat;
//        if( Math.abs(slope) >= this.getTolerance() )
        if( Math.abs(slope) >= Double.MIN_VALUE )
        {
            double step = this.stepMultiplier * -fx/slope;
            xhat = x + step;
        }
        else
        {
            double step = this.stepMultiplier * -fx;
           xhat = x + step;
        }

        double fxhat = this.data.evaluate( xhat );
        if( Math.abs(fxhat) < Math.abs(fx) )
        {
            this.stepMultiplier = 1.0;
            this.result.setInput(xhat);
            this.result.setOutput(fxhat);
        }
        else
        {
            this.stepMultiplier *= STEP_SCALE;
        }

        return Math.abs(this.result.getOutput()) > this.getTolerance();

    }

    @Override
    protected void cleanupAlgorithm()
    {
    }

    public DefaultInputOutputPair<Double,Double> getResult()
    {
        return this.result;
    }
    
}

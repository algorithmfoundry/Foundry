/*
 * File:                LinearVectorScalarFunction.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright November 30, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.learning.function.regression.AbstractRegressor;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The <code>LinearVectorScalarFunction</code> class implements a scalar
 * function that is implemented by a linear function. More formally, the
 * scalar function is parameterized by a weight vector (w) and a bias (b) and 
 * computes the output for a given input (x) as:
 * 
 *     f(x) = w * x + b
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-06",
    changesNeeded=false,
    comments={
        "Made clone() call super.clone().",
        "Otherwise, class looks fine."
    }
)
public class LinearVectorScalarFunction
    extends AbstractRegressor<Vectorizable>
{
    /** The default bias is 0.0. */
    public static final double DEFAULT_BIAS = 0.0;
    
    /** The weight vector. */
    private Vector weights;
    
    /** The bias term. */
    private double bias;
    
    /**
     * Creates a new instance of LinearVectorScalarFunction.
     */
    public LinearVectorScalarFunction()
    {
        this((Vector) null);
    }
    
    /**
     * Creates a new instance of LinearVectorScalarFunction.
     * @param  weights The weight vector.
     */
    public LinearVectorScalarFunction(
        final Vector weights)
    {
        this(weights, DEFAULT_BIAS);
    }
    
    /**
     * Creates a new instance of LinearVectorScalarFunction with the given 
     * weights and bias.
     *
     * @param  weights The weight vector.
     * @param  bias The bias term.
     */
    public LinearVectorScalarFunction(
        final Vector weights,
        final double bias)
    {
        super();
        
        this.setWeights(weights);
        this.setBias(bias);
    }
    
    /**
     * Creates a new copy of a LinearVectorScalarFunction.
     *
     * @param  other The LinearVectorScalarFunction to copy.
     */
    public LinearVectorScalarFunction(
        final LinearVectorScalarFunction other)
    {
        this(ObjectUtil.cloneSafe(other.getWeights()), other.getBias());
    }
    
    @Override
    public LinearVectorScalarFunction clone()
    {
        LinearVectorScalarFunction clone =
            (LinearVectorScalarFunction) super.clone();
        clone.setWeights( ObjectUtil.cloneSafe(this.getWeights()) );
        return clone;
    }
    
    /**
     * Evaluate the given input vector as a double by:
     * 
     *     weights * input + bias
     *
     * @param  input The input vector to evaluate.
     * @return Evaluated input.
     */
    @Override
    public double evaluateAsDouble(
        final Vectorizable input)
    {
        return this.evaluateAsDouble(input.convertToVector());
    }

    /**
     * A convenience method for evaluating a Vector object as a double, thus
     * avoiding the convertToVector call from Vectorizable. It calculates:
     *
     *     weights * input + bias
     *
     * @param   input
     *      The input value to convert to a vector.
     * @return
     *      The double result of multiplying the weight vector times the given
     *      vector and adding the bias. If the weight vector is null, bias is
     *      returned.
     */
    public double evaluateAsDouble(
        final Vector input)
    {
        if (this.weights == null)
        {
            // In the case the weights are uninitialized the result is the bias.
            return this.bias;
        }
        else
        {
            return input.dotProduct(this.weights) + this.bias;
        }
    }

    /**
     * Gets the weight vector.
     *
     * @return The weight vector.
     */
    public Vector getWeights()
    {
        return this.weights;
    }
    
    /**
     * Sets the weight vector.
     *
     * @param  weights The weight vector.
     */
    public void setWeights(
        final Vector weights)
    {
        this.weights = weights;
    }

    /**
     * Gets the bias term.
     *
     * @return The bias term.
     */
    public double getBias()
    {
        return this.bias;
    }
    
    /**
     * Sets the bias term.
     *
     * @param  bias The bias term.
     */
    public void setBias(
        final double bias)
    {
        this.bias = bias;
    }
    
    @Override
    public String toString()
    {
        return "Linear Vector Scalar Function "
            + "(weights = " + this.getWeights() + ", "
            + "bias = " + this.getBias() + ")";
    }
}

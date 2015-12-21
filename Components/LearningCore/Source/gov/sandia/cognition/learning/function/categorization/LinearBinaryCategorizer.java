/*
 * File:                LinearBinaryCategorizer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.categorization;

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorInputEvaluator;
import gov.sandia.cognition.math.matrix.VectorUtil;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The <code>LinearBinaryCategorizer</code> class implements a binary
 * categorizer that is implemented by a linear function. More formally, the
 * classifier is parameterized by a weight vector (w) and a bias (b) and 
 * computes the output for a given input (x) as:
 * 
 *     f(x) = w * x + b
 *
 * The categorization is done by:
 *
 *     f(x) >= 0
 *
 * @author Justin Basilico
 * @since  2.0
 */
public class LinearBinaryCategorizer
    extends AbstractDiscriminantBinaryCategorizer<Vectorizable>
    implements VectorInputEvaluator<Vectorizable, Boolean>,
        ThresholdBinaryCategorizer<Vectorizable>
{
    /** The default bias is {@value}. */
    public static final double DEFAULT_BIAS = 0.0;
    
    /** The weight vector. */
    protected Vector weights;
    
    /** The bias term. */
    protected double bias;
    
    /**
     * Creates a new instance of LinearBinaryCategorizer.
     */
    public LinearBinaryCategorizer()
    {
        this(null, DEFAULT_BIAS);
    }
    
    /**
     * Creates a new instance of LinearBinaryCategorizer with the given weights
     * and bias.
     *
     * @param  weights The weight vector.
     * @param  bias The bias term.
     */
    public LinearBinaryCategorizer(
        final Vector weights,
        final double bias)
    {
        super();
        
        this.setWeights(weights);
        this.setBias(bias);
    }
    
    /**
     * Creates a new copy of a LinearBinaryCategorizer.
     *
     * @param  other The LinearBinaryCategorizer to copy.
     */
    public LinearBinaryCategorizer(
        final LinearBinaryCategorizer other)
    {
        this(ObjectUtil.cloneSafe(other.getWeights()), other.getBias());
    }
    
    @Override
    public LinearBinaryCategorizer clone()
    {
        LinearBinaryCategorizer clone = (LinearBinaryCategorizer) super.clone();
        clone.weights = ObjectUtil.cloneSafe(this.weights);
        return clone;
    }
    
    /**
     * Categorizes the given input vector as a double by:
     * 
     *     weights * input + bias
     *
     * @param  input The input vector to categorize.
     * @return The categorization of the input vector where the sign is the
     *         categorization.
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
     *      returned. The sign is treated as the categorization.
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

    @Override
    public int getInputDimensionality()
    {
        return VectorUtil.safeGetDimensionality(this.weights);
    }

    /**
     * Gets the threshold, which is the negative of the bias.
     *
     * @return
     *      The threshold, which is the negative of the bias.
     */
    @Override
    public double getThreshold()
    {
        return -this.getBias();
    }

    /**
     * Sets the threshold, which is the negative of the bias.
     *
     * @param   threshold
     *      the threshold, which is the negative of the bias.
     */
    @Override
    public void setThreshold(
        final double threshold)
    {
        this.setBias(-threshold);
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
        return "Linear Binary Categorizer "
            + "(weights = " + this.getWeights() + ", "
            + "bias = " + this.getBias() + ")";
    }

}

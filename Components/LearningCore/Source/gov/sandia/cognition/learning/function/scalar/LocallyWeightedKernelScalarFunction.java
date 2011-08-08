/*
 * File:                LocallyWeightedKernelScalarFunction.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 29, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.scalar;

import gov.sandia.cognition.learning.function.kernel.Kernel;
import gov.sandia.cognition.util.WeightedValue;
import java.util.Collection;

/**
 * The {@code LocallyWeightedKernelScalarFunction} class implements a scalar
 * function that uses kernels and does local weighting on them to get the 
 * result value. It also includes the ability to bias the function towards a
 * constant when the kernel values are very small.
 *
 * @param <InputType> Input type to the Kernel 
 * @author Justin Basilico
 * @since  2.0
 */
public class LocallyWeightedKernelScalarFunction<InputType>
    extends KernelScalarFunction<InputType>
{
    /** The default constant weight is {@value}. */
    public static final double DEFAULT_CONSTANT_WEIGHT = 0.0;
    
    /** The default constant value is {@value}. */
    public static final double DEFAULT_CONSTANT_VALUE = 0.0;
    
    /** The constant weight is used as a weight for the constant value that is
     *  added to the result to bias the function to the constant value. */
    protected double constantWeight;
    
    /** The constant value is what the constant weight biases the function
     *  toward when there is near zero weight. */
    protected double constantValue;
    
    /**
     * Creates a new instance of LocallyWeightedKernelScalarFunction.
     */
    public LocallyWeightedKernelScalarFunction()
    {
        super();
    }
    
    /**
     * Creates a new instance of LocallyWeightedKernelScalarFunction with the 
     * given kernel.
     *
     * @param  kernel The kernel to use.
     */
    public LocallyWeightedKernelScalarFunction(
        final Kernel<? super InputType> kernel)
    {
        super(kernel);
        
        this.setConstantWeight(DEFAULT_CONSTANT_WEIGHT);
        this.setConstantValue(DEFAULT_CONSTANT_VALUE);
    }
        
    /**
     * Creates a new instance of LocallyWeightedKernelScalarFunction with the 
     * given kernel and weighted examples.
     *
     * @param  kernel The kernel to use.
     * @param  examples The weighted examples.
     */
    public LocallyWeightedKernelScalarFunction(
        final Kernel<? super InputType> kernel,
        final Collection<? extends WeightedValue<? extends InputType>> examples)
    {
        this(kernel, examples, DEFAULT_BIAS);
    }
    
    /**
     * Creates a new instance of LocallyWeightedKernelScalarFunction with the 
     * given kernel, weighted examples, and bias.
     *
     * @param  kernel The kernel to use.
     * @param  examples The weighted examples.
     * @param  bias The bias.
     */
    public LocallyWeightedKernelScalarFunction(
        final Kernel<? super InputType> kernel,
        final Collection<? extends WeightedValue<? extends InputType>> examples,
        final double bias)
    {
        this(kernel, examples, bias, DEFAULT_CONSTANT_WEIGHT, 
            DEFAULT_CONSTANT_VALUE);
    }
    
    /**
     * Creates a new instance of LocallyWeightedKernelScalarFunction with the 
     * given kernel, weighted examples, and bias.
     *
     * @param  kernel The kernel to use.
     * @param  examples The weighted examples.
     * @param  bias The bias.
     * @param  constantWeight The constant bias weight to use.
     * @param  constantValue The constant bias value to use.
     */
    public LocallyWeightedKernelScalarFunction(
        final Kernel<? super InputType> kernel,
        final Collection<? extends WeightedValue<? extends InputType>> examples,
        final double bias,
        final double constantWeight,
        final double constantValue)
    {
        super(kernel, examples, bias);
        
        this.setConstantWeight(constantWeight);
        this.setConstantValue(constantValue);
    }
    
    /**
     * Creates a new instance copy of LocallyWeightedKernelScalarFunction.
     *
     * @param  other The object to copy.
     */
    public LocallyWeightedKernelScalarFunction(
        final LocallyWeightedKernelScalarFunction<InputType> other)
    {
        super(other);
        
        this.setConstantWeight(other.getConstantWeight());
        this.setConstantValue(other.getConstantValue());
    }
    
    /**
     * Categorizes the given input vector as a double by:
     * 
     *     (sum w_i * k(input, x_i)) / (sum k(input, x_i))
     *
     * @param  input The input to categorize.
     * @return The output categorization as a double where the sign is the
     *         categorization.
     */
    @Override
    public Double evaluate(
        final InputType input)
    {
        // The sum starts out with the constant term.
        double sum = this.constantWeight * this.constantValue;
        double denominator = this.constantValue;

        // Loop over all the examples.
        for ( WeightedValue<? extends InputType> example : this.examples )
        {
            final double weight = example.getWeight();

            // Evaluate the kernel between the example and the input.
            final double value = 
                this.kernel.evaluate(input, example.getValue());

            // Updatre the sum.
            sum += weight * value;
            denominator += value;
        }
        
        if ( denominator == 0.0 )
        {
            return this.bias;
        }
        else
        {
            return sum / denominator + this.bias;
        }
    }

    /**
     * Gets the constant weight.
     *
     * @return The constant weight.
     */
    public double getConstantWeight()
    {
        return this.constantWeight;
    }

    /**
     * Sets the constant weight.
     *
     * @param  constantWeight The constant weight.
     */
    public void setConstantWeight(
        final double constantWeight)
    {
        this.constantWeight = constantWeight;
    }
    
    /**
     * Gets the constant value.
     *
     * @return The constant value.
     */
    public double getConstantValue()
    {
        return this.constantValue;
    }
    
    /**
     * Sets the constant value.
     *
     * @param constantValue The constant value.
     */
    public void setConstantValue(
        final double constantValue)
    {
        this.constantValue = constantValue;
    }
}

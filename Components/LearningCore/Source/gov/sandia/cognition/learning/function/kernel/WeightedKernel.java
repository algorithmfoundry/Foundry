/*
 * File:                WeightedKernel.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 19, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.kernel;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Weighted;

/**
 * The <code>WeightedKernel</code> class implements a kernel that takes another 
 * kernel, evaluates it, and then the result is rescaled by a given weight.
 *
 * @param  <InputType> The type of the input to the Kernel. For example, Vector.
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-08",
    changesNeeded=false,
    comments={
        "Made clone call super.clone.",
        "Now implements Weighted interface.",
        "Looks fine otherwise."
    }
)
public class WeightedKernel<InputType>
    extends DefaultKernelContainer<InputType>
    implements Kernel<InputType>, Weighted
{
    /** The default weight is {@value}. */
    public static final double DEFAULT_WEIGHT = 1.0;
    
    /** The weight on the kernel. Must be non-negative. */
    protected double weight;
    
    /**
     * Creates a new instance of WeightedKernel with a default weight of 1.0
     * and a null kernel.
     */
    public WeightedKernel()
    {
        this(DEFAULT_WEIGHT, null);
    }
    
    /**
     * Creates a new instance of WeightedKernel from the given weight and
     * kernel.
     *
     * @param  weight The weight to apply to the kernel. Must be non-negative.
     * @param  kernel The actual kernel to evaluate.
     */
    public WeightedKernel(
        final double weight,
        final Kernel<? super InputType> kernel)
    {
        super(kernel);
        
        this.setWeight(weight);
    }
    
    /**
     * Creates a copy of this kernel.
     *
     * @return A copy of this kernel.
     */
    @Override
    public WeightedKernel<InputType> clone()
    {
        WeightedKernel<InputType> clone =
            (WeightedKernel<InputType>) super.clone();
        clone.setKernel( ObjectUtil.cloneSafe(this.getKernel()) );
        return clone;
    }
    
    /**
     * The weighted kernel just passes the kernel evaluation to its own 
     * internal kernel and then multiplies it by the weight.
     *
     * @param  x The first item.
     * @param  y The second item.
     * @return The kernel evaluated on the two given objects.
     */
    public double evaluate(
        final InputType x,
        final InputType y)
    {
        return this.weight * this.kernel.evaluate(x, y);
    }
    
    /**
     * Gets the weight used to rescale the kernel's results.
     *
     * @return The kernel's weight.
     */
    public double getWeight()
    {
        return this.weight;
    }

    /**
     * Sets the weight used to rescale the kernel's results.
     *
     * @param  weight The kernel's weight. Must be non-negative.
     */
    public void setWeight(
        final double weight)
    {
        if ( weight < 0.0 )
        {
            // Error: Bad value for the weight.
            throw new IllegalArgumentException("weight must be non-negative");
        }
        this.weight = weight;
    }
}

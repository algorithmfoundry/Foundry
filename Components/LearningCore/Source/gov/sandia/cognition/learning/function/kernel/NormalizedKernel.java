/*
 * File:                NormalizedKernel.java
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

/**
 * The <code>NormalizedKernel</code> class implements an <code>Kernel</code>
 * that returns a normalized value between 0.0 and 1.0 by normalizing the 
 * results of a given kernel. The normalization is done by:
 * <BR>
 *     k(x, y) / sqrt( k(x, x) * k(y, y))
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
        "Looks fine otherwise."
    }
)
public class NormalizedKernel<InputType>
    extends DefaultKernelContainer<InputType>
    implements Kernel<InputType>
{
    /**
     * Creates a new instance of NormalizedKernel.
     */
    public NormalizedKernel()
    {
        super();
    }
    
    /**
     * Creates a new instance of NormalizedKernel using the given kernel.
     *
     * @param  kernel The kernel to use.
     */
    public NormalizedKernel(
        final Kernel<? super InputType> kernel)
    {
        super(kernel);
    }
    
    /**
     * Creates a new copy of a NormalizedKernel.
     *
     * @param  other The NormalizedKernel to copy.
     */
    public NormalizedKernel(
        final NormalizedKernel<? super InputType> other)
    {
        super((DefaultKernelContainer<? super InputType>) other);
    }
    
    @Override
    public NormalizedKernel<InputType> clone()
    {
        return (NormalizedKernel<InputType>) super.clone();
    }
    
    /**
     * Evaluates the normalized kernel by passing the evaluation off to the 
     * internal kernel and then normalizing the results. The kernel is computed 
     * as:
     * <BR>
     *     k(x, y) / sqrt( k(x, x) * k(y, y))
     *
     * @param  x The first item.
     * @param  y The second item.
     * @return The kernel evaluated on the two given objects. The value will
     *         be between 0.0 and 1.0 since the value is normalized.
     */
    public double evaluate(
        final InputType x,
        final InputType y)
    {
        // Compute the numerator which is k(x, y).
        final double kxy = this.kernel.evaluate(x, y);

        if ( kxy == 0.0 )
        {
            // The upper part is zero so the whole thing is zero. There
            // is no need to evaluate the denominators.
            return 0.0;
        }

        // Compute the two values for the denominator k(x, x) and k(y, y).
        final double kxx = this.kernel.evaluate(x, x);
        final double kyy = this.kernel.evaluate(y, y);

        if ( kxx == 0.0 || kyy == 0.0 )
        {
            // These are bad denominator values so just return zero.
            // In this case kxy should already be zero unless the Kernel
            // is ill-defined.
            return 0.0;
        }

        // Return the normalized value.
        return kxy / Math.sqrt(kxx * kyy);
    }
}

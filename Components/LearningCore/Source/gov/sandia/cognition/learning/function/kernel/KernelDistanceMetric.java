/*
 * File:                KernelDistanceMetric.java
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
import gov.sandia.cognition.math.Metric;

/**
 * The <code>KernelDistanceMetric</code> class implements a distance metric that
 * utilizes an underlying <code>Kernel</code> for computing the distance. The
 * distance is computed as:
 * <BR>
 *     d(x, y) = k(x, x) + k(y, y) - 2 * k(x, y)
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
public class KernelDistanceMetric<InputType>
    extends DefaultKernelContainer<InputType>
    implements Metric<InputType>
{
    /**
     * Creates a new instance of KernelDistanceMetric. The kernel is initialized
     * to null.
     */
    public KernelDistanceMetric()
    {
        super();
    }
    
    /**
     * Creates a new instance of KernelDistanceMetric using the given kernel.
     *
     * @param  kernel The kernel to use.
     */
    public KernelDistanceMetric(
        final Kernel<? super InputType> kernel)
    {
        super(kernel);
    }
    
    /**
     * Creates a new copy of a KernelDistanceMetric.
     *
     * @param  other The KernelDistanceMetric to copy.
     */
    public KernelDistanceMetric(
        final KernelDistanceMetric<InputType> other)
    {
        super(other);
    }
    
    @Override
    public KernelDistanceMetric<InputType> clone()
    {
        return (KernelDistanceMetric<InputType>) super.clone();
    }

    /**
     * Computes the distance between the two given objects using the Kernel it 
     * was given. The distance is computed as:
     * <BR>
     *     d(x, y) = k(x, x) + k(y, y) - 2 * k(x, y)
     * 
     * @param  first The first value.
     * @param  second The second value.
     * @return The distance between the two given objects as computed using
     *         the kernel.
     */
    public double evaluate(
        final InputType first, 
        final InputType second)
    {
        return this.kernel.evaluate(first, first)
            +  this.kernel.evaluate(second, second)
            -  2.0 * this.kernel.evaluate(first, second);
    }
}

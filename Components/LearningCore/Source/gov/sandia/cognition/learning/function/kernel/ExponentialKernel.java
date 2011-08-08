/*
 * File:                ExponentialKernel.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright September 18, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.function.kernel;

import gov.sandia.cognition.annotation.CodeReview;

/**
 * The {@code ExponentialKernel} class implements a kernel that applies the
 * exponential function to the result of another kernel.
 *
 * @author Justin Basilico
 * @since  2.0
 * @param <InputType> Input class to the Kernel 
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
public class ExponentialKernel<InputType>
    extends DefaultKernelContainer<InputType>
    implements Kernel<InputType>
{

    /**
     * Creates a new instance of ExponentialKernel.
     */
    public ExponentialKernel()
    {
        super();
    }

    /**
     * Creates a new instance of ExponentialKernel.
     *
     * @param  kernel The kernel to use.
     */
    public ExponentialKernel(
        final Kernel<? super InputType> kernel )
    {
        super( kernel );
    }

    @Override
    public ExponentialKernel<InputType> clone()
    {
        return (ExponentialKernel<InputType>) super.clone();
    }

    /**
     * The exponential kernel takes the exponential of applying another kernel 
     * to the two given inputs.
     *
     * @param  x The first item.
     * @param  y The second item.
     * @return The kernel evaluated on the two given objects.
     */
    public double evaluate(
        final InputType x,
        final InputType y )
    {
        return Math.exp( this.kernel.evaluate( x, y ) );
    }

}

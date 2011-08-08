/*
 * File:                SumKernel.java
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
import java.util.Collection;

/**
 * The {@code SumKernel} class implements a kernel that adds together
 * the result of applying multiple kernels to the same pair of inputs.
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
public class SumKernel<InputType>
    extends DefaultKernelsContainer<InputType>
    implements Kernel<InputType>
{
    /**
     * Creates a new instance of SumKernel.
     */
    public SumKernel()
    {
        super();
    }
    
    /**
     * Creates a new instance of SumKernel with the given collection of
     * kernels.
     *
     * @param  kernels The collection of kernels to use.
     */
    public SumKernel(
        final Collection<? extends Kernel<? super InputType>> kernels)
    {
        super(kernels);
    }
    
    @Override
    public SumKernel<InputType> clone()
    {
        return (SumKernel<InputType>) super.clone();
    }

    /**
     * The addition kernel applies multiple kernels to the given inputs and
     * returns their sum.
     *
     * @param  x The first item.
     * @param  y The second item.
     * @return The kernel evaluated on the two given objects.
     */
    public double evaluate(
        final InputType x, 
        final InputType y)
    {
        double result = 0.0;
        
        for ( Kernel<? super InputType> kernel : this.getKernels() )
        {
            result += kernel.evaluate(x, y);
        }
        
        return result;
    }
}

/*
 * File:                DefaultKernelContainer.java
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
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The <code>DefaultKernelContainer</code> class implements an object that 
 * contains a kernel inside. It is extended by various other classes, including
 * Kernels that contain an internal kernel that is used.
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
public class DefaultKernelContainer<InputType>
    extends AbstractCloneableSerializable
    implements KernelContainer<InputType>
{
    /** The internal kernel. */
    protected Kernel<? super InputType> kernel;
    
    /**
     * Creates a new instance of KernelContainer. The kernel defaults to 
     * null.
     */
    public DefaultKernelContainer()
    {
        super();
        
        this.setKernel(null);
    }
    
    /**
     * Creates a new instance of KernelContainer with the given kernel.
     *
     * @param  kernel The kernel to put in the container.
     */
    public DefaultKernelContainer(
        final Kernel<? super InputType> kernel)
    {
        super();
        
        this.setKernel(kernel);
    }
    
    /**
     * Creates a new copy of a KernelContainer and the kernel inside.
     *
     * @param  other The KernelContainer to copy.
     */
    public DefaultKernelContainer(
        final DefaultKernelContainer<? super InputType> other)
    {
        this(ObjectUtil.cloneSmart(other.getKernel()));
    }
    
    @Override
    public DefaultKernelContainer<InputType> clone()
    {
        @SuppressWarnings("unchecked")
        final DefaultKernelContainer<InputType> result = 
            (DefaultKernelContainer<InputType>) super.clone();
        result.kernel = ObjectUtil.cloneSmart(this.kernel);
        return result;
    }
    
    /**
     * Gets the internal kernel.
     *
     * @return The internal kernel.
     */
    public Kernel<? super InputType> getKernel()
    {
        return this.kernel;
    }

    /**
     * Sets the internal kernel.
     *
     * @param  kernel The internal kernel.
     */
    public void setKernel(
        final Kernel<? super InputType> kernel)
    {
        this.kernel = kernel;
    }
    
}

/*
 * File:                VectorFunctionKernel.java
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
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.VectorFunction;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The {@code VectorFunctionKernel} implements a kernel that makes use of a
 * vector function plus a kernel that operates on vectors. If no kernel is
 * specified the linear kernel (dot product) is used.
 *
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
public class VectorFunctionKernel
    extends DefaultKernelContainer<Vector>
    implements Kernel<Vectorizable>
{
    /** The vector function to use. */
    protected VectorFunction function;
    
    /**
     * Creates a new instance of VectorFunctionKernel.
     */
    public VectorFunctionKernel()
    {
        this(null);
    }
    
    /**
     * Creates a new VectorFunctionKernel from the given function. The default
     * linear kernel is used.
     *
     * @param  function The vector function to use.
     */
    public VectorFunctionKernel(
        VectorFunction function)
    {
        this(function, null);
    }
    
    /**
     * Creates a new VectorFunctionKernel from the given function and kernel.
     *
     * @param  function The vector function to use.
     * @param  kernel The kernel to use.
     */
    public VectorFunctionKernel(
        VectorFunction function,
        Kernel<? super Vector> kernel)
    {
        super(kernel);
        
        this.setFunction(function);
    }
    
    @Override
    public VectorFunctionKernel clone()
    {
        VectorFunctionKernel clone = (VectorFunctionKernel) super.clone();
        clone.setFunction( ObjectUtil.cloneSmart( this.getFunction() ) );
        return clone;
    }

    /**
     * Evaluates the kernel on the given inputs by first applying the vector
     * function to each input vector and then evaluating the kernel on the
     * results of the vector function. If no kernel is specified then the 
     * linear kernel (dot product) is used.
     *
     * @param  x The first item.
     * @param  y The second item.
     * @return The kernel evaluated on the two given objects.
     */
    public double evaluate(
        final Vectorizable x, 
        final Vectorizable y)
    {
        final Vector fx = this.function.evaluate(x.convertToVector());
        final Vector fy = this.function.evaluate(y.convertToVector());
        
        if ( this.kernel == null )
        {
            return fx.dotProduct(fy);
        }
        else
        {
            return this.kernel.evaluate(fx, fy);
        }
    }

    /**
     * Gets the vector function the kernel is using.
     *
     * @return The vector function the kernel is using.
     */
    public VectorFunction getFunction()
    {
        return this.function;
    }

    /**
     * Sets the vector function for the kernel to use.
     *
     * @param  function The vector function for the kernel to use.
     */
    public void setFunction(
        final VectorFunction function)
    {
        this.function = function;
    }
}

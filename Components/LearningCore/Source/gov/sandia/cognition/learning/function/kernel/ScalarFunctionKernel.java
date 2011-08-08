/*
 * File:                RealFunctionKernel.java
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
import gov.sandia.cognition.evaluator.Evaluator;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;

/**
 * The {@code ScalarFunctionKernel} class implements a kernel that applies a
 * scalar function two the two inputs to the kernel and then returns their
 * product.
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
public class ScalarFunctionKernel<InputType>
    extends AbstractCloneableSerializable
    implements Kernel<InputType>
{
    /** The scalar function for the kernel to use. */
    protected Evaluator<? super InputType, Double> function;
    
    /**
     * Creates a new instance of RealFunctionKernel.
     */
    public ScalarFunctionKernel()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of RealFunctionKernel.
     *
     * @param  function The scalar function for the kernel to use.
     */
    public ScalarFunctionKernel(
        final Evaluator<? super InputType, Double> function)
    {
        super();
        
        this.setFunction(function);
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public ScalarFunctionKernel<InputType> clone()
    {
        ScalarFunctionKernel<InputType> clone =
            (ScalarFunctionKernel<InputType>) super.clone();
        clone.setFunction( ObjectUtil.cloneSmart( this.getFunction() ) );
        return clone;
    }
    
    /**
     * Evaluates the kernel on the given inputs by first applying the scalar
     * function to each input and then taking the product of the two returned
     * scalar values.     
     *
     * @param  x The first item.
     * @param  y The second item.
     * @return The kernel evaluated on the two given objects.
     */
    public double evaluate(
        final InputType x, 
        final InputType y)
    {
        return this.function.evaluate(x) * this.function.evaluate(y);
    }

    /**
     * Gets the scalar function the kernel is using.
     *
     * @return The scalar function the kernel is using.
     */
    public Evaluator<? super InputType, Double> getFunction()
    {
        return this.function;
    }

    /**
     * Sets the scalar function for the kernel to use.
     *
     * @param  function The scalar function for the kernel to use.
     */
    public void setFunction(
        final Evaluator<? super InputType, Double> function)
    {
        this.function = function;
    }
}

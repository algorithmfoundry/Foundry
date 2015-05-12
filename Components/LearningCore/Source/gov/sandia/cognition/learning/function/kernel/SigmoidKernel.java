/*
 * File:                SigmoidKernel.java
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
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.AbstractCloneableSerializable;

/**
 * The <code>SigmoidKernel</code> class implements a sigmoid kernel based on the
 * hyperbolic tangent. The kernel it computes is:
 *    
 *     tanh(kappa * (x dot y) + c)
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
public class SigmoidKernel
    extends AbstractCloneableSerializable
    implements Kernel<Vectorizable>
{
    /** The default value for kappa is {@value}. */
    public static final double DEFAULT_KAPPA = 1.0;
    
    /** The default value for the constant is {@value}. */
    public static final double DEFAULT_CONSTANT = 0.0;
    
    /** The kappa value to multiply times the dot product. */
    protected double kappa;

    /** The constant used in the sigmoid. */
    protected double constant;
        
    /**
     * Creates a new instance of SigmoidKernel with default values of 1.0 for
     * kappa and 0.0 for the constant.
     */
    public SigmoidKernel()
    {
        this(DEFAULT_KAPPA, DEFAULT_CONSTANT);
    }
    
    /**
     * Creates a new instance of SigmoidKernel from its two needed parameters: 
     * kappa and a constant. The kernel it evaluates is:
     *
     *     tanh(kappa * (x dot y) + c)
     * 
     * @param  kappa The value multiplied by the dot product of the two vectors
     *         before it is passed to the hyperbolic tangent function.
     * @param  constant The constant inside of the sigmoid kernel.
     */
    public SigmoidKernel(
        final double kappa,
        final double constant)
    {
        super();
        
        this.setKappa(kappa);
        this.setConstant(constant);
    }
    
    /**
     * Creates a new copy of a SigmoidKernel.
     *
     * @param  other The SigmoidKernel to copy.
     */
    public SigmoidKernel(
        final SigmoidKernel other)
    {
        this(other.getKappa(), other.getConstant());
    }
    
    @Override
    public SigmoidKernel clone()
    {
        return (SigmoidKernel) super.clone();
    }

    /**
     * Evaluates the sigmoid kernel between the two given vectors, which is: 
     *
     *     tanh(kappa * (x dot y) + c)
     *
     * @param  x The first vector. 
     * @param  y The second vector.
     * @return The result of the sigmoid kernel: tanh(kappa * (x dot y) + c)
     */
    public double evaluate(
        final Vectorizable x,
        final Vectorizable y)
    {
        final double product = 
            x.convertToVector().dotProduct(y.convertToVector());
        return Math.tanh(this.kappa * product + this.constant);
    }
    
    /** 
     * Gets kappa, the value multiplied by the dot product of the two vectors
     * before it is passed to the hyperbolic tangent function.
     *
     * @return The kappa value for the sigmoid kernel.
     */
    public double getKappa()
    {
        return this.kappa;
    }
    
    /** 
     * Sets kappa, the value multiplied by the dot product of the two vectors
     * before it is passed to the hyperbolic tangent function.
     *
     * @param  kappa The kappa value for the sigmoid kernel.
     */
    public void setKappa(
        final double kappa)
    {
        this.kappa = kappa;
    }
    
    /**
     * Gets the constant inside of the sigmoid kernel.
     *
     * @return The constant term used for the sigmoid kernel.
     */
    public double getConstant()
    {
        return this.constant;
    }
    
    /**
     * Sets the constant inside of the sigmoid kernel.
     *
     * @param  constant The constant term used for the sigmoid kernel.
     */
    public void setConstant(
        final double constant)
    {
        this.constant = constant;
    }
}

/*
 * File:                PolynomialKernel.java
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
 * The <code>PolynomialKernel</code> class implements a kernel for two given 
 * vectors that is the polynomial function:
 * <BR>
 *     (x dot y + c)^d
 * <BR>
 * d is the degree of the polynomial, which must be a positive integer. c is 
 * the constant that is used, which should be a non-negative number. Normally c 
 * is either 0.0 or 1.0.
 *
 * @author Justin Basilico
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2009-07-08",
    changesNeeded=false,
    comments="Looks fine."
)
public class PolynomialKernel
    extends AbstractCloneableSerializable
    implements Kernel<Vectorizable>
{
    /** The default degree is {@value}. */
    public static final int DEFAULT_DEGREE = 2;
    
    /** The default constant is {@value}. */
    public static final double DEFAULT_CONSTANT = 1.0;
    
    /** The degree of the polynomial. Must be positive. */
    protected int degree;
    
    /** The constant for the polynomial. */
    protected double constant;
    
    /**
     * Creates a new instance of PolynomialKernel with a degree of 2 and a
     * constant of 1.0.
     */
    public PolynomialKernel()
    {
        this(DEFAULT_DEGREE);
    }
    
    /**
     * Creates a new instance of PolynomialKernel with the given degree and
     * a constant of 1.0.
     *
     * @param  degree The degree of the kernel. Must be positive.
     */ 
    public PolynomialKernel(
        final int degree)
    {
        this(degree, DEFAULT_CONSTANT);
    }
    
    /**
     * Creates a new instance of PolynomialKernel with the given degree and
     * constant.
     *
     * @param  degree The degree of the kernel. Must be positive.
     * @param  constant The constant of the kernel. Must be non-negative.
     */ 
    public PolynomialKernel(
        final int degree,
        final double constant)
    {
        super();
        
        this.setDegree(degree);
        this.setConstant(constant);
    }
    
    /**
     * Creates a new copy of a PolynomialKernel.
     *
     * @param  other The PolynomialKernel to copy.
     */
    public PolynomialKernel(
        final PolynomialKernel other)
    {
        this(other.getDegree(), other.getConstant());
    }
    
    @Override
    public PolynomialKernel clone()
    {
        return (PolynomialKernel) super.clone();
    }
    
    /**
     * This kernel just evaluates the polynomial kernel between the two given 
     * vectors, which is: (x dot y + c)^d.
     *
     * @param  x The first vector.
     * @param  y The second vector.
     * @return The result of the polynomial kernel: (x dot y + c)^d
     */
    public double evaluate(
        final Vectorizable x,
        final Vectorizable y)
    {
        final double product = 
            x.convertToVector().dotProduct(y.convertToVector());

        return Math.pow(product + this.constant, this.degree);
    }

    /**
     * Gets the degree of the polynomial.
     *
     * @return The degree of the polynomial.
     */
    public int getDegree()
    {
        return this.degree;
    }

    /**
     * Sets the degree of the polynomial.
     *
     * @param  degree The degree of the polynomial. Must be positive.
     */
    public void setDegree(
        final int degree)
    {
        if ( degree <= 0 )
        {
            // Error: Bad value for the degree.
            throw new IllegalArgumentException("degree must be positive");
        }
            
        this.degree = degree;
    }

    /**
     * Gets the constant of the polynomial.
     *
     * @return The constant of the polynomial.
     */
    public double getConstant()
    {
        return this.constant;
    }

    /**
     * Gets the constant of the polynomial.
     *
     * @param constant The constant of the polynomial. Must be non-negative.
     */
    public void setConstant(
        final double constant)
    {
        if ( constant < 0.0 )
        {
            // Error: Bad value for the constant.
            throw new IllegalArgumentException("constant must be non-negative");
        }
        
        this.constant = constant;
    }
}

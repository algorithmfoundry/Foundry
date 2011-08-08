/*
 * File:                RadialBasisKernel.java
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
 * The <code>RadialBasisKernel</code> implements the standard radial basis 
 * kernel, which is:
 * <BR>
 *     exp( -||x - y||^2 / (2 * sigma^2) )
 * <BR>
 * where sigma is the parameter that controls the bandwidth of the kernel.
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
public class RadialBasisKernel
    extends AbstractCloneableSerializable
    implements Kernel<Vectorizable>
{
    /** The default value for sigma is {@value}. */
    public static final double DEFAULT_SIGMA = 1.0;

    /** Sigma is the parameter that controls the bandwidth of the radial basis 
     *  kernel. */
    private double sigma = 0.0;

    /** This value is cached so it doesn't have to be computed each time. It is 
     *  -2.0 * sigma * sigma. */
    private double negativeTwoSigmaSquared = 0.0;
    
    /**
     * Creates a new instance of RadialBasisKernel with a default value for
     * sigma of 1.0.
     */
    public RadialBasisKernel()
    {
        this(DEFAULT_SIGMA);
    }

    /**
     * Creates a new instance of RadialBasisKernel with the given value for
     * sigma. Sigma is the parameter that controls the bandwidth of the kernel.
     *
     * @param  sigma The sigma parameter that controls the bandwidth of the 
     *         kernel.
     */
    public RadialBasisKernel(
        final double sigma)
    {
        super();
        
        this.setSigma(sigma);
    }
    
    /**
     * Creates a new copy of a RadialBasisKernel.
     *
     * @param  other The RadialBasisKernel to copy.
     */
    public RadialBasisKernel(
        final RadialBasisKernel other)
    {
        this(other.getSigma());
    }
    
    @Override
    public RadialBasisKernel clone()
    {
        return (RadialBasisKernel) super.clone();
    }
    
    /**
     * Evaluates the following kernel between the two given vectors: 
     * 
     *    exp( -||x - y||^2 / (2 * sigma^2))
     *
     * @param  x The first vector.
     * @param  y The second vector.
     * @return The kernel evaluated on the two given vectors.
     */
    public double evaluate(
        final Vectorizable x,
        final Vectorizable y)
    {
        final double distance = x.convertToVector().euclideanDistanceSquared(
            y.convertToVector());

        return Math.exp(distance / this.negativeTwoSigmaSquared);
    }

    /**
     * Gets the sigma value that controls the bandwidth of the kernel.
     *
     * @return The sigma value that returns the bandwidth of the kernel.
     */
    public double getSigma()
    {
        return this.sigma;
    }
    
    /**
     * Sets the sigma value that controls the bandwidth of the kernel.
     *
     * @param  sigma The sigma value for the kernel. Must be positive.
     */
    public void setSigma(
        final double sigma)
    {
        if ( sigma <= 0.0 )
        {
            // Error: Bad value for sigma.
            throw new IllegalArgumentException("sigma must be positive");
        }

        this.sigma = sigma;
        this.negativeTwoSigmaSquared = -2.0 * sigma * sigma;
    }
        
}

/*
 * File:                GaussianCluster.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 22, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.statistics.distribution.MultivariateGaussian;
import gov.sandia.cognition.math.matrix.Vector;
import java.util.Collection;

/**
 * The {@code GaussianCluster} class implements a cluster of {@code Vector} 
 * objects that has a {@code MultivariateGaussian} object representing the
 * cluster.
 *
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments="Code generally looks fine."
)
public class GaussianCluster
    extends DefaultCluster<Vector>
{
    /** The Gaussian distribution that the cluster represents. */
    private MultivariateGaussian.PDF gaussian;
    
    /**
     * Creates a new instance of GaussianCluster.
     */
    public GaussianCluster()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of GaussianCluster.
     *
     * @param  gaussian The MultivariateGaussian representing the data.
     */
    public GaussianCluster(
        final MultivariateGaussian.PDF gaussian)
    {
        super();
        
        this.setGaussian(gaussian);
    }
    
    /**
     * Creates a new instance of GaussianCluster.
     *
     * @param  members The members of the cluster.
     * @param  gaussian The MultivariateGaussian representing the data.
     */
    public GaussianCluster(
        final Collection<Vector> members,
        final MultivariateGaussian.PDF gaussian)
    {
        super(members);
        
        this.setGaussian(gaussian);
    }
    
    /**
     * Creates a new instance of GaussianCluster.
     *
     * @param  index The index of the cluster.
     * @param  members The members of the cluster.
     * @param  gaussian The MultivariateGaussian representing the data.
     */
    public GaussianCluster(
        final int index,
        final Collection<Vector> members,
        final MultivariateGaussian.PDF gaussian)
    {
        super(index, members);
        
        this.setGaussian(gaussian);
    }
    
    /**
     * Gets the Gaussian that represents the cluster.
     *
     * @return The Gaussian representing the cluster.
     */
    public MultivariateGaussian.PDF getGaussian()
    {
        return this.gaussian;
    }
    
    /**
     * Sets the Gaussian representing the cluster.
     *
     * @param  gaussian The new Gaussian to represent the cluster.
     */
    public void setGaussian(
        final MultivariateGaussian.PDF gaussian)
    {
        this.gaussian = gaussian;
    }
}

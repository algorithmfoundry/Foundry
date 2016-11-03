/*
 * File:                CentroidCluster.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 22, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.annotation.CodeReview;
import java.util.Collection;

/**
 * The CentroidCluster class extends the default cluster to contain a central
 * element. This type of cluster is useful in many clustering algorithms.
 *
 * @param  <ClusterType> The type of data stored in the cluster.
 *
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments="Code generally looks fine."
)
public class CentroidCluster<ClusterType>
    extends DefaultCluster<ClusterType>
{
    /** The center of the cluster. */
    protected ClusterType centroid;
    
    /**
     * Creates a new instance of CentroidCluster.
     */
    public CentroidCluster()
    {
        super();
        
        this.setCentroid(null);
    }
    
    /**
     * Creates a new instance of CentroidCluster.
     *
     * @param centroid The centroid of the cluster.
     */
    public CentroidCluster(
        final ClusterType centroid)
    {
        super();
        
        this.setCentroid(centroid);
    }
    
    /**
     * Creates a new instance of CentroidCluster.
     *
     * @param index The index of the cluster.
     * @param centroid The centroid of the cluster.
     */
    public CentroidCluster(
        final int index,
        final ClusterType centroid)
    {
        super(index);
        
        this.setCentroid(centroid);
    }
    
    /**
     * Creates a new instance of CentroidCluster.
     *
     * @param centroid The centroid of the cluster.
     * @param members The members of the cluster.
     */
    public CentroidCluster(
        final ClusterType centroid,
        final Collection<? extends ClusterType> members)
    {
        super(members);
        
        this.setCentroid(centroid);
    }
    
    /**
     * Creates a new instance of CentroidCluster.
     *
     * @param index The index of the cluster.
     * @param centroid The centroid of the cluster.
     * @param members The members of the cluster.
     */
    public CentroidCluster(
        final int index,
        final ClusterType centroid,
        final Collection<? extends ClusterType> members)
    {
        super(index, members);
        
        this.setCentroid(centroid);
    }
    
    /**
     * Gets the centroid of the cluster.
     *
     * @return The centroid of the cluster.
     */
    public ClusterType getCentroid()
    {
        return this.centroid;
    }
    
    /**
     * Sets the centroid of the cluster.
     *
     * @param centroid The centroid of the cluster.
     */
    public void setCentroid(
        final ClusterType centroid)
    {
        this.centroid = centroid;
    }
}

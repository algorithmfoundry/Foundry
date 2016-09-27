/*
 * File:                NormalizedCentroidCluster.java
 * Authors:             Natasha Singh-Miller
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright 2011, Sandia Corporation. Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import java.util.Collection;

/**
 * Add the ability to store the centroid of the normalized vectors belonging to
 * a centroid cluster.
 *
 * @author nsingh
 */
public class NormalizedCentroidCluster<ClusterType>
    extends CentroidCluster<ClusterType>
{

    /**
     * The normalized center of the cluster.
     */
    protected ClusterType normalizedCentroid;

    /**
     * Creates a new instance of NormalizedCentroidCluster.
     */
    public NormalizedCentroidCluster()
    {
        super();

        this.setNormalizedCentroid(null);
    }

    /**
     * Creates a new instance of NormalizedCentroidCluster.
     *
     * @param centroid The centroid of the cluster.
     * @param normalizedCentroid The normalized centroid of the cluster.
     */
    public NormalizedCentroidCluster(
        final ClusterType centroid,
        final ClusterType normalizedCentroid)
    {
        super(centroid);

        this.setNormalizedCentroid(normalizedCentroid);
    }

    /**
     * Creates a new instance of NormalizedCentroidCluster.
     *
     * @param index The index of the cluster.
     * @param centroid The centroid of the cluster.
     * @param normalizedCentroid The normalized centroid of the cluster.
     */
    public NormalizedCentroidCluster(
        final int index,
        final ClusterType centroid,
        final ClusterType normalizedCentroid)
    {
        super(index, centroid);

        this.setNormalizedCentroid(normalizedCentroid);
    }

    /**
     * Creates a new instance of NormalizedCentroidCluster.
     *
     * @param centroid The centroid of the cluster
     * @param normalizedCentroid The normalized centroid of the cluster.
     * @param members The members of the cluster.
     */
    public NormalizedCentroidCluster(
        final ClusterType centroid,
        final ClusterType normalizedCentroid,
        final Collection<? extends ClusterType> members)
    {
        super(centroid, members);

        this.setNormalizedCentroid(normalizedCentroid);
    }

    /**
     * Creates a new instance of NormalizedCentroidCluster.
     *
     * @param index The index of the cluster.
     * @param centroid The centroid of the cluster.
     * @param normalizedCentroid The normalized centroid of the cluster.
     * @param members The members of the cluster.
     */
    public NormalizedCentroidCluster(
        final int index,
        final ClusterType centroid,
        final ClusterType normalizedCentroid,
        final Collection<? extends ClusterType> members)
    {
        super(index, centroid, members);

        this.setNormalizedCentroid(normalizedCentroid);
    }

    /**
     * Gets the normalized centroid of the cluster.
     *
     * @return The normalized centroid of the cluster.
     */
    public ClusterType getNormalizedCentroid()
    {
        return this.normalizedCentroid;
    }

    /**
     * Sets the normalized centroid of the cluster.
     *
     * @param normalizedCentroid The normalized centroid of the cluster.
     */
    public void setNormalizedCentroid(
        final ClusterType normalizedCentroid)
    {
        this.normalizedCentroid = normalizedCentroid;
    }

}

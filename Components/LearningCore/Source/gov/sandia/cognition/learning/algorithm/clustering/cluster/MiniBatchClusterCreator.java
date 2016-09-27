/*
 * File:                MiniBatchClusterCreator.java
 * Authors:             Jeff Piersol
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright April 28, 2016, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import java.util.Collection;
import java.util.List;

/**
 * A cluster creator that can be used with mini-batch k-means. This cluster
 * creator never assigns points to a cluster; it only updates the cluster
 * prototypes with the given data.
 *
 * @param <ClusterType> The type of Cluster created by this object.
 * @param <DataType> The data type from which to create a new cluster.
 * @author Jeff Piersol
 * @since 3.4.4
 */
public interface MiniBatchClusterCreator<ClusterType extends Cluster<DataType>, DataType>
    extends ClusterCreator<ClusterType, DataType>
{

    /**
     * Initialize the cluster by incorporating the effect of the given points.
     *
     * @param initialPoints
     * @return
     */
    @Override
    public ClusterType createCluster(Collection<? extends DataType> initialPoints);

    /**
     * Update the cluster prototype by incorporating the effect of the given
     * data point.
     *
     * @param cluster the cluster to add a member to
     * @param dataPoint the member to add to the cluster
     */
    public void updatePrototype(
        final ClusterType cluster,
        final DataType dataPoint);

    /**
     * Batch update the cluster prototype by incorporating the effect of the
     * given data points.
     *
     * @param cluster the cluster to add a member to
     * @param dataPoints the members to add to the cluster
     */
    public void updatePrototype(
        final ClusterType cluster,
        final List<DataType> dataPoints);

}

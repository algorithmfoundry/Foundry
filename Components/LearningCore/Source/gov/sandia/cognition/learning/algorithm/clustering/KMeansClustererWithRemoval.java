/*
 * File:                KMeansClustererRemoval.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright May 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.FixedClusterInitializer;

/**
 * Creates a k-means clustering algorithm that removes clusters that do
 * not have sufficient membership to pass a simple statistical significance
 * test.
 *
 * @param   <DataType> The type of the data to cluster. This is typically 
 *          defined by the divergence function used.
 * @param   <ClusterType> The type of {@code Cluster} created by the algorithm.
 *          This is typically defined by the cluster creator function used.
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments={
        "Made setRemovalThreshold check to ensure removalThreshold is < 1.0",
        "Cleaned up javadoc.",
        "Code generally looks fine."
    }
)
public class KMeansClustererWithRemoval
    <DataType, ClusterType extends Cluster<DataType>>
    extends KMeansClusterer<DataType, ClusterType>
{

    /**
     * fraction of the expected number of data points
     * assigned to a cluster below which the cluster will be removed.  (Suppose
     * there are 1000 datapoint, 10 clusters, and removalThreshold=0.1.  A
     * cluster may be removed only if is has membership less than
     * 0.1*1000/10= 10 elements assigned to it.)
     */
    private double removalThreshold;

    /**
     * Default constructor
     */
    public KMeansClustererWithRemoval()
    {
        super();
    }
    
    /**
     * Creates a new instance of KMeansClusterer using the given parameters.
     *
     * @param numRequestedClusters The number of clusters requested (k).
     * @param maxIterations Number of iterations before stopping
     * @param initializer The initializer for the clusters.
     * @param divergenceFunction The divergence function.
     * @param creator The cluster creator.
     * @param removalThreshold fraction of the expected number of data points
     * assigned to a cluster below which the cluster will be removed.  (Suppose
     * there are 1000 datapoint, 10 clusters, and removalThreshold=0.1.  A
     * cluster may be removed only if is has membership less than
     * 0.1*1000/10= 10 elements assigned to it.)
     */
    public KMeansClustererWithRemoval(
        int numRequestedClusters,
        int maxIterations,
        FixedClusterInitializer<ClusterType, DataType> initializer,
        ClusterDivergenceFunction<ClusterType, DataType> divergenceFunction,
        ClusterCreator<ClusterType, DataType> creator,
        double removalThreshold)
    {
        super(numRequestedClusters, maxIterations, initializer, 
            divergenceFunction, creator);
        
        this.setRemovalThreshold(removalThreshold);
    }

    /**
     * Getter for removalThreshold
     * @return fraction of the expected number of data points
     * assigned to a cluster below which the cluster will be removed.  (Suppose
     * there are 1000 datapoint, 10 clusters, and removalThreshold=0.1.  A
     * cluster may be removed only if is has membership less than
     * 0.1*1000/10= 10 elements assigned to it.)
     */
    public double getRemovalThreshold()
    {
        return this.removalThreshold;
    }

    /**
     * Setter for removalThreshold
     * @param removalThreshold fraction of the expected number of data points
     * assigned to a cluster below which the cluster will be removed.  (Suppose
     * there are 1000 datapoint, 10 clusters, and removalThreshold=0.1.  A
     * cluster may be removed only if is has membership less than
     * 0.1*1000/10= 10 elements assigned to it.)  Must be less than 1.0.
     */
    public void setRemovalThreshold(
        double removalThreshold)
    {
        if( removalThreshold >= 1.0 )
        {
            throw new IllegalArgumentException(
                "removalThreshold must be < 1.0" );
        }
        this.removalThreshold = removalThreshold;
    }

    /**
     * Removes the cluster at the specified index, and does the internal
     * bookkeeping as well
     * @param clusterIndex zero-based cluster index to remove 
     */
    protected void removeCluster(
        int clusterIndex)
    {
        this.getClusters().remove(clusterIndex);

        // Loop over the assignments and set the elements assigned to
        // clusterIndex = -1, and decrement the indices above clusterIndex
        // so that, on the next clusterStep, the numChanged value makes sense
        int[] assigns = this.getAssignments();
        for (int i = 0; i < this.getNumElements(); i++)
        {
            if (assigns[i] == clusterIndex)
            {
                this.setAssignment(i, -1);
            }
            else if (assigns[i] > clusterIndex)
            {
                this.setAssignment(i, assigns[i] - 1);
            }
        }

    }

    @Override
    protected boolean step()
    {

        // do normal k-means clustering, but then check to see if we need to
        // remove any clusters
        boolean superStepReturn = super.step();

        // Now see if any clusters are candidates for removal
        int removalNumber = (int) Math.floor(
            (this.getRemovalThreshold() * this.getNumElements()) /
            this.getNumClusters());

        // Each iteration, we only remove the cluster with the
        // lowest number of elements assigned to it (if it's below
        // the removalNumber)
        int minCount = this.getNumElements();
        int minIndex = -1;
        for (int i = 0; i < this.getNumClusters(); i++)
        {
            int clusterCount = this.getClusterCounts()[i];
            if (minCount > clusterCount)
            {
                minCount = clusterCount;
                minIndex = i;
            }
        }
        
        for (int i = this.getNumClusters() - 1; i >= 0; i--)
        {
            int clusterCount = this.clusterCounts[i];
            if (clusterCount <= removalNumber)
            {
                this.removeCluster(i);
                this.setNumChanged(this.getNumChanged() + clusterCount + 1);
            }
        }

        return superStepReturn;
    }

}

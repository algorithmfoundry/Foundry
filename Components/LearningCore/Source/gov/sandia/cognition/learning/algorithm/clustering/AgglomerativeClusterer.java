/*
 * File:                AgglomerativeClusterer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 28, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.CodeReviewResponse;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.BatchHierarchicalClusterer;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.BinaryClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.ClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.DefaultClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterToClusterDivergenceFunction;
import gov.sandia.cognition.learning.function.distance.DivergenceFunctionContainer;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

/**
 * The {@code AgglomerativeClusterer} implements an agglomerative clustering
 * algorithm, which is a type of hierarchical clustering algorithm. 
 * Such a clustering algorithm works by initially creating one 
 * cluster for each element in the collection to cluster and then 
 * repeatedly merging the two closest clusters until the stopping
 * condition is met or there is only one cluster remaining. This
 * implementation supports multiple methods for determining the
 * distance between two clusters by supplying an 
 * {@code ClusterToClusterDivergenceFunction} object. There are two stopping 
 * conditions for the algorithm, which are parameters that can be set. The first
 * is that the clustering will stop when some minimum number of 
 * clusters is reached, which defaults to 1. The second criteria is
 * that the clustering will stop when the distance between the two 
 * closest clusters is larger than a given value. This threshold can
 * be used to create clusters when the number of clusters is not 
 * known ahead of time.
 *
 * @param   <DataType> The type of the data to cluster. This is typically 
 *          defined by the divergence function used.
 * @param   <ClusterType> The type of {@code Cluster} created by the algorithm.
 *          This is typically defined by the cluster creator function used.
 * @author  Justin Basilico
 * @since   1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=true,
    comments={
        "I *really* don't like the use of 'continue', but I will defer.",
        "Please implement the sections previously marked as 'to do'"
    },
    response=@CodeReviewResponse(
        respondent="Justin Basilico",
        date="2008-10-07",
        moreChangesNeeded=false,
        comments="The clusterer now supports hierarchical clustering."
    )
)
public class AgglomerativeClusterer
    <DataType, ClusterType extends Cluster<DataType>>
    extends AbstractAnytimeBatchLearner
        <Collection<? extends DataType>, Collection<ClusterType>>
    implements BatchClusterer<DataType, ClusterType>, 
        BatchHierarchicalClusterer<DataType, ClusterType>,
        DivergenceFunctionContainer<ClusterType, ClusterType>
{

    /** The default minimum number of clusters is {@value}. */
    public static final int DEFAULT_MIN_NUM_CLUSTERS = 1;

    /** The default maximum distance is {@value}. */
    public static final double DEFAULT_MAX_DISTANCE = Double.MAX_VALUE;

    /** The default maximum minimum distance is {@value}. */
    @Deprecated
    public static final double DEFAULT_MAX_MIN_DISTANCE = DEFAULT_MAX_DISTANCE;

    /** The default maximum number of iterations {@value} */
    public static final int DEFAULT_MAX_ITERATIONS = Integer.MAX_VALUE;

    /**
     * The divergence function used to find the distance between two clusters. 
     */
    protected ClusterToClusterDivergenceFunction<? super ClusterType, ? super DataType>
        divergenceFunction;

    /** The merger used to merge two clusters into one element. */
    protected ClusterCreator<ClusterType, DataType> creator;

    /** The minimum number of clusters allowed. */
    protected int minNumClusters;

    /** The maximum distance between clusters allowed. */
    protected double maxDistance;

    /** The current set of clusters. */
    protected ArrayList<ClusterType> clusters;
    
    /** The current set of hierarchical clusters. */
    protected ArrayList<HierarchyNode<DataType, ClusterType>> 
        clustersHierarchy;

    /**
     * An array list mapping the cached minimum distance from the cluster with
     * the given index to any other clusters.
     */
    protected transient ArrayList<Double> minDistances;

    /**
     * The array of indexes that maps the cluster index to the closest cluster.
     */
    protected transient ArrayList<Integer> minClusters;

    /**
     * Creates a new instance of AgglomerativeClusterer.
     */
    public AgglomerativeClusterer()
    {
        this(null, null);
    }

    /**
     * Initializes the clustering to use the given metric between
     * clusters, and the given cluster creator. The minimum number of
     * clusters will be set to 1.
     * 
     * @param  divergenceFunction The distance metric between clusters.
     * @param  creator The method for creating clusters.
     */
    public AgglomerativeClusterer(
        final ClusterToClusterDivergenceFunction<? super ClusterType, ? super DataType>
            divergenceFunction,
        final ClusterCreator<ClusterType, DataType> creator)
    {
        this(divergenceFunction, creator, DEFAULT_MIN_NUM_CLUSTERS);
    }

    /**
     * Initializes the clustering to use the given metric between
     * clusters, the given cluster creator, and the minimum number of
     * clusters to allow.
     *
     * @param  divergenceFunction The distance metric between clusters.
     * @param  creator The method for creating clusters.
     * @param  minNumClusters The minimum number of clusters to allow. Must 
     *         be greater than zero.
     */
    public AgglomerativeClusterer(
        final ClusterToClusterDivergenceFunction<? super ClusterType, ? super DataType>
            divergenceFunction,
        ClusterCreator<ClusterType, DataType> creator,
        int minNumClusters)
    {
        this(divergenceFunction, creator, minNumClusters,
            DEFAULT_MAX_DISTANCE);
    }

    /** 
     * Initializes the clustering to use the given metric between
     * clusters, the given cluster merger, and the maximum 
     * distance between clusters to allow when merging.
     *
     * @param  divergenceFunction The distance metric between clusters.
     * @param  creator The method for creating clusters.
     * @param  maxDistance The maximum distance between clusters to allow when
     *      merging them.
     */
    public AgglomerativeClusterer(
        ClusterToClusterDivergenceFunction<? super ClusterType, ? super DataType>
            divergenceFunction,
        ClusterCreator<ClusterType, DataType> creator,
        double maxDistance)
    {
        this(divergenceFunction, creator, 1, maxDistance);
    }

    /**
     * Initializes the clustering to use the given metric between
     * clusters, the given cluster merger, the minimum number of
     * clusters to allow, and the maximum minimum distance between 
     * clusters to allow.
     *
     * @param  divergenceFunction The distance metric between clusters.
     * @param  creator The method for creating clusters.
     * @param  minNumClusters The minimum number of clusters to allow. Must 
     *         be greater than zero.
     * @param  maxDistance The maximum distance between clusters to allow when
     *      merging them.
     */
    public AgglomerativeClusterer(
        ClusterToClusterDivergenceFunction<? super ClusterType, ? super DataType>
            divergenceFunction,
        ClusterCreator<ClusterType, DataType> creator,
        int minNumClusters,
        double maxDistance)
    {
        super(DEFAULT_MAX_ITERATIONS);

        this.setDivergenceFunction(divergenceFunction);
        this.setCreator(creator);

        this.setMinNumClusters(minNumClusters);
        this.setMaxDistance(maxDistance);

        this.setClusters(null);
        this.setClustersHierarchy(null);
        this.setMinDistances(null);
        this.setMinClusters(null);
    }
    
    @Override
    public AgglomerativeClusterer<DataType, ClusterType> clone()
    {
        @SuppressWarnings("unchecked")
        final AgglomerativeClusterer<DataType, ClusterType> result =
            (AgglomerativeClusterer<DataType, ClusterType>) super.clone();
        
        result.divergenceFunction = ObjectUtil.cloneSmart(this.divergenceFunction);
        result.creator = ObjectUtil.cloneSmart(this.creator);
        
        result.clusters = null;
        result.clustersHierarchy = null;
        result.minDistances = null;
        result.minClusters = null;
        
        return result;
    }

    public ClusterHierarchyNode<DataType, ClusterType> clusterHierarchically(
        Collection<? extends DataType> data)
    {
        // Turn off the stopping criteria to do with the minimum number of
        // clusters or the maximum minimum distance.
        final int tempMinNumClusters = this.getMinNumClusters();
        final double tempMaxMinDistance = this.getMaxDistance();
        this.setMinNumClusters(1);
        this.setMaxDistance(Double.MAX_VALUE);
        
        this.learn(data);
        
        this.setMinNumClusters(tempMinNumClusters);
        this.setMaxDistance(tempMaxMinDistance);
        
        
        if (CollectionUtil.isEmpty(this.clustersHierarchy))
        {
            // No clusters.
            return null;
        }
        else if (this.clustersHierarchy.size() == 1)
        {
            // Get the root of the hierarchy.
            return this.clustersHierarchy.get(0);
        }
        else
        {
            // This should really never happen, but it is possible that
            // clustering got stopped early. If that is the case, we bind 
            // together all the clusters into one root node.
            final DefaultClusterHierarchyNode<DataType, ClusterType> root =
                new DefaultClusterHierarchyNode<DataType, ClusterType>();
            
            // Set the children.
            root.setChildren(
                new ArrayList<ClusterHierarchyNode<DataType, ClusterType>>(
                    this.clustersHierarchy));
            return root;
            
        }
    }

    protected boolean initializeAlgorithm()
    {
        // Create the arrays to store the cluster information.
        int numElements = this.data.size();

        // Initialize our data structures.
        this.setClusters(new ArrayList<ClusterType>(numElements));
        this.setClustersHierarchy(
            new ArrayList<HierarchyNode<DataType, ClusterType>>(
                numElements));
        this.setMinDistances(new ArrayList<Double>(numElements));
        this.setMinClusters(new ArrayList<Integer>(numElements));

        // Initialize one cluster for each element.
        for (DataType element : this.data)
        {
            // Create the cluster object for the element.

            LinkedList<DataType> singleton = new LinkedList<DataType>();
            singleton.add(element);

            ClusterType cluster = this.creator.createCluster(singleton);

            // Add the cluster.
            this.clusters.add(cluster);
            this.clustersHierarchy.add(
                new HierarchyNode<DataType, ClusterType>(cluster));
            this.minDistances.add(Double.MAX_VALUE);
            this.minClusters.add(-1);
        }

        // Initialize the minimum distance calculation for each cluster.
        for (int i = 0; i < this.getNumClusters(); i++)
        {
            this.updateMinDistance(i);
        }

        return true;
    }

    protected boolean step()
    {
        if (this.getNumClusters() <= this.minNumClusters)
        {
            // Make sure we haven't violated the minimum number of clusters.
            return false;
        }

        // Find the two clusters that are the closest together.
        double minDistance = Double.MAX_VALUE;
        int minFrom = -1;
        int minTo = -1;

        for (int i = 0; i < this.getNumClusters(); i++)
        {
            // Get the distance to the closest cluster for this 
            // cluster.
            // ClusterType cluster = this.clusters.get(i);
            double distance = (double) minDistances.get(i);

            if (minFrom < 0 || distance < minDistance)
            {
                // This is the smallest distance seen so far so store
                // the information about the cluster.
                minDistance = distance;
                minFrom = i;
                minTo = this.minClusters.get(i);
            }
        }

        if (minDistance > this.maxDistance)
        {
            // The minimum distance clusters are too far apart
            // to merge.
            return false;
        }

        // Merge the two clusters into one.
        int mergedIndex = this.mergeClusters(minFrom, minTo, minDistance);
        ClusterType merged = this.clusters.get(mergedIndex);

        // Update the minimum distance for the merged cluster.
        this.updateMinDistance(mergedIndex);

        // Update the cached minimum distances for the other
        // clusters.
        for (int i = 0; i < this.getNumClusters(); i++)
        {
            ClusterType other = this.clusters.get(i);
            if (other == merged)
            {
                // Don't update the new cluster.
                continue;
            }

            // Get the current minimum cluster.
            int minClusterIndex = this.minClusters.get(i);
            // ClusterType minCluster = this.clusters.get(minClusterIndex);

            if (minClusterIndex == minTo || minClusterIndex == minFrom)
            {
                // The minimum distance was to the cluster we just
                // merged, so we need to do a complete update on the
                // distances for it.
                this.updateMinDistance(i);
            }
            else
            {
                // Get the current minimum distance.
                double distance =
                    this.divergenceFunction.evaluate(other, merged);

                if (distance < (double) minDistances.get(i))
                {
                    // The new cluster is the closest.
                    this.minDistances.set(i, distance);
                    this.minClusters.set(i, mergedIndex);
                }
            }
        }

        return this.getNumClusters() > this.minNumClusters;
    }

    protected void cleanupAlgorithm()
    {
        this.setMinDistances(null);
        this.setMinClusters(null);
    }

    /**
     * Updates the cached minimum distance for this cluster by 
     * comparing it to all the other clusters.
     * 
     * @param  index The cluster to update.
     */
    protected void updateMinDistance(
        int index)
    {
        // Search for the closest cluster to this cluster.
        ClusterType cluster = this.clusters.get(index);
        double minDistance = Double.MAX_VALUE;
        int minCluster = -1;

        for (int i = 0; i < this.getNumClusters(); i++)
        {
            ClusterType other = this.clusters.get(i);

            if (cluster == other)
            {
                // Don't compute the distance to self, since it will be
                // zero for a valid distance metric.
                continue;
            }

            // Compute the distance.
            double distance = this.divergenceFunction.evaluate(cluster, other);

            if (minCluster < 0 || distance < minDistance)
            {
                // This is the closest one found so far so save it.
                minDistance = distance;
                minCluster = i;
            }
        }

        // Save the closest cluster found to this one.
        this.minDistances.set(index, minDistance);
        this.minClusters.set(index, minCluster);
    }

    /**
     * Merges two clusters together, creating a new BinaryTreeCluster
     * and updating the internal state.
     * 
     * @param  firstIndex The first cluster.
     * @param  secondIndex The second cluster.
     * @param  distance The distance between the clusters.
     * @return The new, merged cluster.
     */
    protected int mergeClusters(
        int firstIndex,
        int secondIndex,
        double distance)
    {
        // Get the two clusters.
        ClusterType first = this.clusters.get(firstIndex);
        ClusterType second = this.clusters.get(secondIndex);

        // Figure out the larger and smaller indices of the two given 
        // clusters.
        int minIndex = Math.min(firstIndex, secondIndex);
        int maxIndex = Math.max(firstIndex, secondIndex);

        // Create a list of all the members of the clusters.
        ArrayList<DataType> members = new ArrayList<DataType>();
        members.addAll(first.getMembers());
        members.addAll(second.getMembers());

        // If we have the ability to merge the clusters, merge them.
        ClusterType merged = this.creator.createCluster(members);
        
        // Create the new parent cluster for the two that are merged.
        HierarchyNode<DataType, ClusterType> firstChild = 
            this.clustersHierarchy.get(firstIndex);
        HierarchyNode<DataType, ClusterType> secondChild = 
            this.clustersHierarchy.get(secondIndex);
        HierarchyNode<DataType, ClusterType> parent =
            new HierarchyNode<DataType, ClusterType>(
                merged, firstChild, secondChild, distance);


        // Move the cluster at the end of the list to the larger index of 
        // the two clusters to merge in order to remove one element from 
        // the list.
        int endClusterNum = this.clusters.size() - 1;

        if (endClusterNum != maxIndex)
        {
            ClusterType endCluster = this.clusters.get(endClusterNum);
            this.clusters.set(maxIndex, endCluster);
            this.minDistances.set(maxIndex, this.minDistances.get(endClusterNum));
            this.minClusters.set(maxIndex, this.minClusters.get(endClusterNum));

// TODO: Make the minClusters array not store an index but instead a pointer so 
// that we don't have to do this update step.
            // Move all the pointers to the end cluster.
            for (int i = 0; i < this.getNumClusters(); i++)
            {
                if (endClusterNum == this.minClusters.get(i))
                {
                    this.minClusters.set(i, maxIndex);
                }
            }
        }
        // else - The end cluster is the one we are removing.

        // Store the information about the parent.
        int newIndex = minIndex;
        this.clusters.set(newIndex, merged);
        this.clustersHierarchy.set(newIndex, parent);
        this.minDistances.set(newIndex, Double.MAX_VALUE);
        this.minClusters.set(newIndex, null);

        // Remove the last element from the list.
        this.clusters.remove(endClusterNum);
        this.clustersHierarchy.remove(endClusterNum);
        this.minDistances.remove(endClusterNum);
        this.minClusters.remove(endClusterNum);

        // Return the new cluster that we just created.
        return newIndex;
    }

    public ArrayList<ClusterType> getResult()
    {
        return this.clusters;
    }

    /**
     * Gets the number of clusters.
     *
     * @return The number of clusters.
     */
    public int getNumClusters()
    {
        if (this.clusters == null)
        {
            return 0;
        }
        else
        {
            return this.clusters.size();
        }
    }

    /**
     * Gets the divergence function used in clustering.
     *
     * @return The divergence function.
     */
    public ClusterToClusterDivergenceFunction<? super ClusterType, ? super DataType>
        getDivergenceFunction()
    {
        return this.divergenceFunction;
    }

    /**
     * Sets the divergence function.
     *
     * @param divergenceFunction The divergence function.
     */
    public void setDivergenceFunction(
        ClusterToClusterDivergenceFunction<? super ClusterType, ? super DataType>
            divergenceFunction)
    {
        this.divergenceFunction = divergenceFunction;
    }

    /**
     * Gets the cluster creator.
     *
     * @return The cluster creator.
     */
    public ClusterCreator<ClusterType, DataType> getCreator()
    {
        return this.creator;
    }

    /**
     * Sets the cluster creator.
     *
     * @param creator The creator for clusters.
     */
    public void setCreator(
        ClusterCreator<ClusterType, DataType> creator)
    {
        this.creator = creator;
    }

    /**
     * The minimum number of clusters to allow. To create a cluster tree,
     * set this value to 1. If the number of clusters drops to this number
     * (or below) then the clustering will stop. Must be greater than
     * zero.
     *
     * @return The minimum number of clusters allowed.
     */
    public int getMinNumClusters()
    {
        return this.minNumClusters;
    }

    /**
     * The minimum number of clusters to allow. To create a cluster tree,
     * set this value to 1. If the number of clusters drops to this number
     * (or below) then the clustering will stop. Must be greater than
     * zero.
     *
     * @param  minNumClusters The new minimum number of clusters.
     */
    public void setMinNumClusters(
        int minNumClusters)
    {
        this.minNumClusters = Math.max(1, minNumClusters);
    }

    /**
     * Gets the maximum distance.
     * 
     * @return The maximum distance.
     * @deprecated Use getMaxDistance
     */
    @Deprecated
    public double getMaxMinDistance()
    {
        return this.getMaxDistance();
    }
    
    @Deprecated
    public void setMaxMinDistance(
        final double maxMinDistance)
    {
        this.setMaxDistance(maxMinDistance);
    }

    /**
     * The maximum distance between clusters that is allowed 
     * for the two clusters to be merged. If there are no clusters 
     * that remain that have a distance between them less than or 
     * equal to this value, then the clustering will halt. To not
     * have this value factored into the clustering, set it to 
     * something such as Double.MAX_VALUE.
     *
     * @return The maximum distance between clusters to merge.
     */
    public double getMaxDistance()
    {
        return this.maxDistance;
    }

    /**
     * The maximum distance between clusters that is allowed 
     * for the two clusters to be merged. If there are no clusters 
     * that remain that have a distance between them less than or 
     * equal to this value, then the clustering will halt. To not
     * have this value factored into the clustering, set it to 
     * something such as Double.MAX_VALUE.
     *
     * @param  maxDistance The new maximum distance between clusters to merge.
     */
    public void setMaxDistance(
        final double maxDistance)
    {
        this.maxDistance = maxDistance;
    }

    /**
     * Sets the clusters.
     *
     * @param clusters The clusters.
     */
    protected void setClusters(
        ArrayList<ClusterType> clusters)
    {
        this.clusters = clusters;
    }
    
    /**
     * Gets the hierarchy of clusters.
     * 
     * @return The hierarchy of clusters.
     */
    public ArrayList<HierarchyNode<DataType, ClusterType>> 
        getClustersHierarchy()
    {
        return clustersHierarchy;
    }

    /**
     * Sets the hierarchy of clusters.
     * 
     * @param   clustersHierarchy The hierarchy of clusters.
     */
    protected void setClustersHierarchy(
        final ArrayList<HierarchyNode<DataType, ClusterType>> 
            clustersHierarchy)
    {
        this.clustersHierarchy = clustersHierarchy;
    }

    /**
     * Sets the minimum distances for each clusters.
     *
     * @param  minDistances The array of minimum distances.
     */
    protected void setMinDistances(
        ArrayList<Double> minDistances)
    {
        this.minDistances = minDistances;
    }

    /**
     * Sets the index of the closest cluster.
     *
     * @param  minClusters The array of closest cluster indices.
     */
    protected void setMinClusters(
        ArrayList<Integer> minClusters)
    {
        this.minClusters = minClusters;
    }
    
    /**
     * Holds the hierarchy information for the agglomerative clusterer. It
     * is a binary node that also keeps track of the divergence between 
     * children.
     * 
     * @param   <DataType>
     *      The type of the data being clustered.
     * @param   <ClusterType>
     *      The type of the clusters being created.
     */
    public static class HierarchyNode<DataType, ClusterType extends Cluster<DataType>>
        extends BinaryClusterHierarchyNode<DataType, ClusterType>
    {
        /** The divergence between the two children, if they exist. */
        protected double childrenDivergence;
        
        /**
         * Creates a new {@code HierarchyNode}.
         */
        public HierarchyNode()
        {
            this(null);
        }
        
        /**
         * Creates a new {@code HierarchyNode}.
         * 
         * @param   cluster
         *      The cluster associated with the node.
         */
        public HierarchyNode(
            final ClusterType cluster)
        {
            this(cluster, null, null, 0.0);
        }
        
        /**
         * Creates a new {@code HierarchyNode}.
         * 
         * @param   cluster
         *      The cluster associated with the node.
         * @param   firstChild
         *      The first child.
         * @param   secondChild
         *      The second child.
         * @param   childrenDivergence
         *      The divergence between the children.
         */
        public HierarchyNode(
            final ClusterType cluster,
            final HierarchyNode<DataType, ClusterType> firstChild,
            final HierarchyNode<DataType, ClusterType> secondChild,
            final double childrenDivergence)
        {
            super(cluster, firstChild, secondChild);
            
            this.setChildrenDivergence(childrenDivergence);
        }
        
        /**
         * Gets the divergence between the two children, if they exist; 
         * otherwise, 0.0.
         * 
         * @return  The divergence between the two children, if they exist.
         */
        public double getChildrenDivergence()
        {
            return this.childrenDivergence;
        }
        
        /**
         * Sets the divergence between the two children.
         * 
         * @param   childrenDivergence
         *      The divergence between the two children.
         */
        public void setChildrenDivergence(
            final double childrenDivergence)
        {
            this.childrenDivergence = childrenDivergence;
        }
    }

}

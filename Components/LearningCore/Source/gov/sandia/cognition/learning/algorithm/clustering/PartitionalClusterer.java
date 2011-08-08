/*
 * File:                PartitionClusterer.java
 * Authors:             Natasha Singh-Miller
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 25, 2011, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.BatchHierarchicalClusterer;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.BinaryClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.IncrementalClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.ClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterDivergenceFunction;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.Randomized;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * The {@code PartitionClusterer} implements a partitional clustering
 * algorithm, which is a type of hierarchical clustering algorithm.
 * Such a clustering algorithm works by initially creating one
 * cluster for all elements in the collection and then repeatedly
 * partitioning each cluster into two clusters until the stopping
 * condition is met or each leaf cluster has only one element. This
 * implementation supports multiple methods for determining the
 * distance between two clusters by supplying an
 * {@code ClusterToClusterDivergenceFunction} object. There are two stopping
 * conditions for the algorithm, which are parameters that can be set. The first
 * is that the clustering will stop when some minimum number of elements per
 * leaf cluster is reached, which defaults to 1. The second criteria is
 * that the partitioning will stop when the improvement in the criterion
 * function is below some threshold greater than one.
 *
 * @param   <DataType> The type of the data to cluster. This is typically
 *          defined by the divergence function used.
 * @param   <ClusterType> The type of {@code Cluster} created by the algorithm.
 *          This is typically defined by the cluster creator function used.
 * @author  Natasha Singh-Miller
 * @since   3.1.1
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2011-03-09",
    comments={
        "Should make do a greedy splitting prioritization.",
        "Should make an interface for incremental cluster creation for this to use."},
    changesNeeded=true)
@PublicationReference(
    author={"Ying Zhao", "George Karypis"},
    title="Hierarchical Clustering Algorithms for Document Datasets",
    type=PublicationType.Journal,
    year=2005,
    publication="Data Mining and Knowledge Discovery",
    pages={141, 168},
    url="http://www.springerlink.com/index/jx3825j42x4333m5.pdf")
public class PartitionalClusterer
    <DataType, ClusterType extends Cluster<DataType>>
    extends AbstractAnytimeBatchLearner
        <Collection<? extends DataType>, Collection<ClusterType>>
    implements BatchClusterer<DataType, ClusterType>,
        BatchHierarchicalClusterer<DataType, ClusterType>,
        Randomized
{

    /** The default minimum number of elements per cluster is {@value}. */
    public static final int DEFAULT_MIN_CLUSTER_SIZE = 1;

    /** The default maximum decrease in the training criterion is {@value}. */
    public static final double DEFAULT_MAX_CRITERION_DECREASE = 1.0;

    /** The default maximum number of iterations {@value} */
    public static final int DEFAULT_MAX_ITERATIONS = Integer.MAX_VALUE;

    /**
     * The divergence function used to find the distance between two clusters.
     */
    protected ClusterDivergenceFunction<ClusterType, DataType>
        divergenceFunction;

    /** The merger used to merge two clusters into one element. */
    protected IncrementalClusterCreator<ClusterType, DataType> creator;

    /** The minimum number of elements per cluster allowed. */
    protected int minClusterSize;

    /** The maximum decrease in training criterion allowed. */
    protected double maxCriterionDecrease;

    /** The source of randomness used during initial partitioning. */
    protected Random random;

    /** The current set of clusters. */
    protected transient ArrayList<ClusterType> clusters;

    /** The current set of hierarchical clusters. */
    protected transient ArrayList<BinaryClusterHierarchyNode<DataType, ClusterType>>
        clustersHierarchy;

    /**
     * Creates a new instance of PartitionalClusterer.
     */
    public PartitionalClusterer()
    {
        this(null, null, new Random());
    }

    /**
     * Initializes the clustering to use the given metric between
     * clusters, and the given cluster creator. 
     *
     * @param  divergenceFunction The distance metric between clusters.
     * @param  creator The method for creating clusters.
     * @param  random
     *      The random number generator to use.
     */
    public PartitionalClusterer(
        final ClusterDivergenceFunction<ClusterType, DataType>
            divergenceFunction,
        final IncrementalClusterCreator<ClusterType, DataType> creator,
        final Random random)
    {
        this(divergenceFunction, creator, DEFAULT_MIN_CLUSTER_SIZE,
            DEFAULT_MAX_CRITERION_DECREASE, random);
    }

    /**
     * Initializes the clustering to use the given metric between
     * clusters, the given cluster creator, and the minimum number of
     * elements per cluster to allow.
     *
     * @param  divergenceFunction The distance metric between clusters.
     * @param  creator The method for creating clusters.
     * @param  minClusterSize The minimum number of elements per cluster to
     *         allow. Must be greater than zero.
     * @param  maxCriterionDecrease The maximum decrease in the training
     *         criterion to allow.
     * @param  random
     *      The random number generator to use.
     */
    public PartitionalClusterer(
        final ClusterDivergenceFunction<ClusterType, DataType>
            divergenceFunction,
        final IncrementalClusterCreator<ClusterType, DataType> creator,
        final int minClusterSize,
        final double maxCriterionDecrease,
        final Random random)
    {
        this(divergenceFunction, creator, DEFAULT_MAX_ITERATIONS, 
            minClusterSize, maxCriterionDecrease, random);
    }

    /**
     * Initializes the clustering to use the given metric between
     * clusters, the given cluster creator, the minimum number of elements per
     * cluster to allow, and the maximum decrease in the training criterion
     * during partition to allow.
     *
     * @param  divergenceFunction The distance metric between clusters.
     * @param  creator The method for creating clusters.
     * @param  maxIterations The maximum number of iterations.
     * @param  minClusterSize The minimum number of clusters to allow. Must
     *         be greater than zero.
     * @param  maxCriterionDecrease The maximum decrease in the training
     *         criterion to allow.
     * @param  random
     *      The random number generator to use.
     */
    public PartitionalClusterer(
        final ClusterDivergenceFunction<ClusterType, DataType>
            divergenceFunction,
        final IncrementalClusterCreator<ClusterType, DataType> creator,
        final int maxIterations,
        final int minClusterSize,
        final double maxCriterionDecrease,
        final Random random)
    {
        super(maxIterations);

        this.setDivergenceFunction(divergenceFunction);
        this.setCreator(creator);

        this.setMinClusterSize(minClusterSize);
        this.setMaxCriterionDecrease(maxCriterionDecrease);

        this.setClusters(null);
        this.setClustersHierarchy(null);

        this.setRandom(random);
    }

    @Override
    public PartitionalClusterer<DataType, ClusterType> clone()
    {
        @SuppressWarnings("unchecked")
        final PartitionalClusterer<DataType, ClusterType> result =
            (PartitionalClusterer<DataType, ClusterType>) super.clone();

        result.divergenceFunction = ObjectUtil.
                cloneSafe(this.divergenceFunction);
        result.creator = ObjectUtil.cloneSafe(this.creator);

        result.clusters = null;
        result.clustersHierarchy = null;
        
        return result;
    }

    @Override
    public ClusterHierarchyNode<DataType, ClusterType> clusterHierarchically(
        final Collection<? extends DataType> data)
    {
        this.learn(data);

        if (CollectionUtil.isEmpty(this.clustersHierarchy))
        {
            // No clusters.
            return null;
        }

        // Get the root of the hierarchy.
        return this.clustersHierarchy.get(0);
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        // Initialize our data structures.
        this.setClusters(new ArrayList<ClusterType>());
        this.setClustersHierarchy(
            new ArrayList<BinaryClusterHierarchyNode<DataType, ClusterType>>());

        // Initialize one cluster for all elements.
        final ArrayList<DataType> dataList = new ArrayList<DataType>(this.data);

        final ClusterType cluster = this.creator.createCluster(dataList);
        this.clusters.add(cluster);
        this.clustersHierarchy.add(
            new BinaryClusterHierarchyNode<DataType, ClusterType>(cluster));

        return true;
    }

    @Override
    protected boolean step()
    {
        // Find the index of the next cluster to split.
        final int clusterIndex = this.iteration - 1;

        // Make sure there are still clusters to split.
        if (clusterIndex >= this.getClusterCount())
        {
            return false;
        }

        // Get the cluster to split.
        final ClusterType clusterToSplit = this.clusters.get(clusterIndex);
        
        // Check to see if current cluster is too small. If it is too small it
        // will move to the next one on the next step.
        if (clusterToSplit.getMembers().size() <= this.getMinClusterSize())
        {
            return true;
        }

        // Attempt to split the next cluster.
        final BinaryClusterHierarchyNode<DataType, ClusterType>
            clusterToSplitNode = this.clustersHierarchy.get(clusterIndex);

        // Randomly Partition the cluster.
        final DefaultPair<ClusterType, ClusterType> clusterChildren =
                this.randomPartition(clusterToSplit);
        final ClusterType leftCluster = clusterChildren.getFirst();
        final ClusterType rightCluster = clusterChildren.getSecond();

        // Greedily Swap the elements of the two clusters
        this.greedySwap(clusterToSplit.getMembers(), leftCluster, rightCluster);

        if (    (leftCluster.getMembers().size() >= this.minClusterSize)
             && (rightCluster.getMembers().size() >= this.minClusterSize))
        {
            final double clusterCriteria = this.evaluateCriterion(clusterToSplit);
            final double leftCriteria = this.evaluateCriterion(leftCluster);
            final double rightCriteria = this.evaluateCriterion(rightCluster);
            final boolean improved = 
                  (clusterCriteria * this.maxCriterionDecrease)
                > (leftCriteria + rightCriteria);

            if (improved)
            {
                this.clusters.add(leftCluster);
                this.clusters.add(rightCluster);
                final BinaryClusterHierarchyNode<DataType,ClusterType>
                    leftNode =
                        new BinaryClusterHierarchyNode<DataType,ClusterType>(
                            leftCluster);
                final BinaryClusterHierarchyNode<DataType,ClusterType>
                        rightNode =
                        new BinaryClusterHierarchyNode<DataType,ClusterType>(
                            rightCluster);

                this.clustersHierarchy.add(leftNode);
                this.clustersHierarchy.add(rightNode);
                clusterToSplitNode.setFirstChild(leftNode);
                clusterToSplitNode.setSecondChild(rightNode);
            }

        }

        return true;
    }

    @Override
    protected void cleanupAlgorithm()
    {
        // Nothing to clean up.
    }

    @Override
    public ArrayList<ClusterType> getResult()
    {
        return this.clusters;
    }

    /**
     * Randomly partitions the input cluster into two clusters, where each
     * cluster will contain at least one element
     *
     * @param clusterToSplit Cluster that will be split into two clusters
     * @return ArrayList containing two clusters representing a random split
     *         of the input cluster
     */
    private DefaultPair<ClusterType, ClusterType> randomPartition(
        final ClusterType clusterToSplit)
    {
        final ArrayList<DataType> leftMembers = new ArrayList<DataType>();
        final ArrayList<DataType> rightMembers = new ArrayList<DataType>();

        for (DataType member : clusterToSplit.getMembers())
        {
            // Randomly add to one cluster
            if(this.random.nextBoolean())
            {
                leftMembers.add(member);
            }
            else
            {
                rightMembers.add(member);
            }
        }

// TODO: You could have these random index removals faster by swapping
// the last index into the one that was removed to prevent shifting data in the
// ArrayList
// -- jdbasil (2011-03-09)
        // Add to first cluster if it is empty
        if (leftMembers.isEmpty())
        {
            int rightSize = rightMembers.size();
            int randomIndex = this.random.nextInt(rightSize);
            leftMembers.add(rightMembers.remove(randomIndex));
        }

        if (rightMembers.isEmpty())
        {
            int leftSize = rightMembers.size();
            int randomIndex = this.random.nextInt(leftSize);
            rightMembers.add(leftMembers.remove(randomIndex));
        }

        // Initialize list of two clusters
        return DefaultPair.create(
            this.creator.createCluster(leftMembers),
            this.creator.createCluster(rightMembers));
    }

    /**
     * Greedily swaps elements between two clusters to increase the optimization
     * criterion until no further improvement is seen
     *
     * @param data
     *      The list of all the data elements in the two clusters. Must equal
     *      the sizes of the elements of both clusters.
     * @param   leftCluster
     *      The left cluster for swapping.
     * @param   rightCluster
     *      The right cluster for swapping.
     */
    private void greedySwap(
        Collection<DataType> data,
        ClusterType leftCluster,
        ClusterType rightCluster)
    {
        // Indicates if any improvements were made during an iteration
        boolean improved = true;

        // Evaluate criterion
        double criterion =
              this.evaluateCriterion(leftCluster)
            + this.evaluateCriterion(rightCluster);

        // Greedy swap until no improvement
        while (improved)
        {
            improved = false;

            for (DataType member : data)
            {
                // Try out doing a swap.
                this.swapElement(leftCluster, rightCluster, member);
                double testCriterion =
                      this.evaluateCriterion(leftCluster)
                    + this.evaluateCriterion(rightCluster);

                // If swapping member improves the criterion, keep the swap
                if (testCriterion < criterion)
                {
                    criterion = testCriterion;
                    improved = true;
                }
                else
                {
                    // Not an improvement, so swap it back.
                    this.swapElement(leftCluster, rightCluster, member);
                }
            }
        }

    }

    /*
     * If left cluster contains element, it is removed from the left cluster
     * and added to the right. Else if the right cluster contains the element,
     * it is removed from the right cluster and added to the left. If neither
     * cluster originally contains the element or if a cluster would become
     * empty because of the swap, nothing is changed.
     *
     * @param   leftCluster
     *      The left cluster.
     * @param   rightCluster
     *      The right cluster.
     * @param   element Member to be swapped from left to right if in the left,
     * else right to left if in the right, or added to neither cluster is not
     * originally present in either
     */
    private void swapElement(
        ClusterType leftCluster,
        ClusterType rightCluster,
        DataType element)
    {
        if (leftCluster.getMembers().contains(element) &&
                leftCluster.getMembers().size() > 1)
        {
            this.creator.removeClusterMember(leftCluster, element);
            this.creator.addClusterMember(rightCluster, element);
        }
        else if (rightCluster.getMembers().contains(element) &&
                rightCluster.getMembers().size() > 1)
        {
            this.creator.removeClusterMember(rightCluster, element);
            this.creator.addClusterMember(leftCluster, element);
        }
    }

    /*
     * Evaluates the optimization criterion on a single cluster.
     *
     * @param   cluster
     *      The cluster to evaluate the optimization criterion on.
     */
    private double evaluateCriterion(
        final ClusterType cluster)
    {
        double total = 0.0;
        for (DataType member : cluster.getMembers())
        {
            total += this.divergenceFunction.evaluate(cluster, member);
        }
        return total;
    }

    /**
     * Gets the number of clusters.
     *
     * @return The number of clusters.
     */
    public int getClusterCount()
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
    public ClusterDivergenceFunction<ClusterType, DataType>
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
        ClusterDivergenceFunction<ClusterType, DataType>
            divergenceFunction)
    {
        this.divergenceFunction = divergenceFunction;
    }

    /**
     * Gets the cluster creator.
     *
     * @return The cluster creator.
     */
    public IncrementalClusterCreator<ClusterType, DataType> getCreator()
    {
        return this.creator;
    }

    /**
     * Sets the cluster creator.
     *
     * @param creator The creator for clusters.
     */
    public void setCreator(
        IncrementalClusterCreator<ClusterType, DataType> creator)
    {
        this.creator = creator;
    }

    @Override
    public Random getRandom()
    {
        return this.random;
    }
    
    @Override
    public void setRandom(
        Random random)
    {
        this.random = random;
    }
    
    /**
     * Returns the minimum number of elements per cluster to allow. If the
     * number of elements in a cluster is less than or equal to this number, it
     * will not be bisected. Must be greater than zero.
     *
     * @return The minimum number of elements per cluster allowed.
     */
    public int getMinClusterSize()
    {
        return this.minClusterSize;
    }

    /**
     * Sets the minimum number of elements per cluster to allow. If the
     * number of elements in a cluster is less than or equal to this number, it
     * will not be bisected.
     *
     * @param  minClusterSize
     *      The new minimum number of elements per cluster allowed. Must be
     *      greater than zero.
     */
    public void setMinClusterSize(
        int minClusterSize)
    {
        ArgumentChecker.assertIsPositive("minClusterSize", minClusterSize);
        this.minClusterSize = minClusterSize;
    }

    /**
     * Returns the maximum decrease in the training criterion allowed following
     * a bisection. If the decrease in criterion of a bisection step is greater
     * than this value, partitioning will stop. To turn off this stopping
     * criterion set this to 1.0 or greater.
     *
     * @return The maximum decrease in the training criterion following a
     * bisection.
     */
    public double getMaxCriterionDecrease()
    {
        return this.maxCriterionDecrease;
    }

    /**
     * Sets the maximum decrease in the training criterion allowed following
     * a bisection. If the decrease in criterion of a bisection step is greater
     * than this value, partitioning will stop. To turn off this stopping
     * criterion set this to 1.0 or greater.
     *
     * @param  maxCriterionDecrease The new maximum minimum distance.
     */
    public void setMaxCriterionDecrease(
        double maxCriterionDecrease)
    {
        ArgumentChecker.assertIsPositive("maxCriterionDecrease", maxCriterionDecrease);
        this.maxCriterionDecrease = maxCriterionDecrease;
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
    public ArrayList<BinaryClusterHierarchyNode<DataType, ClusterType>>
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
        final ArrayList<BinaryClusterHierarchyNode<DataType, ClusterType>>
            clustersHierarchy)
    {
        this.clustersHierarchy = clustersHierarchy;
    }


}

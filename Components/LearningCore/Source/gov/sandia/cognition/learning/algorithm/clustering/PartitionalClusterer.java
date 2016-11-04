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
 * Revision History:
 *
 * Revision 1.2  2016/6/15 4.46 anfishe
 * Revision 1.1  2011/02/25 14:57:38  nsingh
 * Initial creation of partition clustering code.
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.BatchHierarchicalClusterer;
import gov.sandia.cognition.collection.CollectionUtil;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.BinaryClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.IncrementalClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.NormalizedCentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.NormalizedCentroidClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.VectorMeanCentroidClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.hierarchy.ClusterHierarchyNode;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterDivergenceFunction;
import gov.sandia.cognition.learning.function.distance.DivergenceFunctionContainer;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.WithinClusterDivergence;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.WithinClusterDivergenceWrapper;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.WithinNormalizedCentroidClusterCosineDivergence;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.DefaultPair;
import gov.sandia.cognition.util.Randomized;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

/**
 * The {@code PartitionalClusterer} implements a partitional clustering
 * algorithm, which is a type of hierarchical clustering algorithm. Consider
 * using {@link #create(int)} or {@link #createSpherical(int)} to instantiate
 * this clusterer.
 * <p>
 * Partitional clustering works by creating one cluster for all elements in the
 * collection and then repeatedly partitioning each cluster into two clusters
 * until one of the stopping criteria is satisfied:
 * <ul>
 * <li>The number of requested clusters is met.</li>
 * <li>The minimum number of elements in a each leaf cluster is met.</li>
 * <li>The improvement in the criterion function is below some threshold.</li>
 * <li>The maximum number of iterations is met.</li>
 * </ul>
 * Note that since there are multiple stopping conditions, it is possible to
 * return fewer than the requested number of clusters.
 * <p>
 * This implementation supports multiple methods for determining the distance
 * between two clusters. The methods can be supplied by providing a
 * {@link WithinClusterDivergence} object or by providing a
 * {@link ClusterDivergenceFunction} which is automatically wrapped by
 * {@link WithinClusterDivergenceWrapper} into a
 * {@link WithinClusterDivergence}.
 *
 * @param <DataType> The type of the data to cluster. This is typically defined
 * by the divergence function used.
 * @param <ClusterType> The type of {@link Cluster} created by the algorithm.
 * This is typically defined by the cluster creator function used.
 * @author Natasha Singh-Miller
 * @since 3.1.1
 */
@CodeReview(
    reviewer = "Justin Basilico",
    date = "2011-03-09",
    comments =
    {
        "Should make do a greedy splitting prioritization.",
        "Should make an interface for incremental cluster creation for this to use."
    },
    changesNeeded = true)
@PublicationReference(
    author =
    {
        "Ying Zhao", "George Karypis"
    },
    title = "Hierarchical Clustering Algorithms for Document Datasets",
    type = PublicationType.Journal,
    year = 2005,
    publication = "Data Mining and Knowledge Discovery",
    pages =
    {
        141, 168
    },
    url = "http://www.springerlink.com/index/jx3825j42x4333m5.pdf")
public class PartitionalClusterer<DataType, ClusterType extends Cluster<DataType>>
    extends AbstractAnytimeBatchLearner<Collection<? extends DataType>, Collection<ClusterType>>
    implements BatchClusterer<DataType, ClusterType>,
    BatchHierarchicalClusterer<DataType, ClusterType>,
    Randomized, DivergenceFunctionContainer<ClusterType, DataType>
{

    /**
     * The default minimum number of elements per cluster is {@value}.
     */
    public static final int DEFAULT_MIN_CLUSTER_SIZE = 1;

    /**
     * The default maximum decrease in the training criterion is {@value}.
     */
    public static final double DEFAULT_MAX_CRITERION_DECREASE = 1.0;

    /**
     * The default maximum number of iterations is {@value}.
     */
    public static final int DEFAULT_MAX_ITERATIONS = Integer.MAX_VALUE;

    /**
     * The default number of requested clusters is {@value}.
     */
    public static final int DEFAULT_NUM_REQUESTED_CLUSTERS = Integer.MAX_VALUE;

    /**
     * The divergence function used to find the distance between two clusters.
     */
    protected WithinClusterDivergence<? super ClusterType, ? super DataType> clusterDivergenceFunction;

    /**
     * An optional DivergenceFunction that is used to create a {@link
     * WithinClusterDivergence} function via a
     * {@link WithinClusterDivergenceWrapper}.
     *
     */
    protected DivergenceFunction<? super ClusterType, ? super DataType> divergenceFunction;

    /**
     * Tolerance for determining when improvement has been made. The default
     * value is {@value}.
     */
    protected double tolerance = 1e-10;

    /**
     * The merger used to merge two clusters into one cluster.
     */
    protected IncrementalClusterCreator<ClusterType, DataType> creator;

    /**
     * The minimum number of elements per cluster allowed.
     */
    protected int minClusterSize;

    /**
     * The maximum decrease in training criterion allowed.
     */
    protected double maxCriterionDecrease;

    /**
     * The index of the next cluster to split.
     */
    private int clusterIndex;

    /**
     * The source of randomness used during initial partitioning.
     */
    protected Random random;

    /**
     * The current set of clusters all clusters created.
     */
    protected transient ArrayList<ClusterType> clusters;

    /**
     * The current set of hierarchical clusters created.
     */
    protected transient ArrayList<BinaryClusterHierarchyNode<DataType, ClusterType>> clustersHierarchy;

    /**
     * The number of clusters requested.
     */
    protected int numRequestedClusters;

    /**
     * Whether or not the current learning is using cached cluster results.
     */
    protected boolean useCachedClusters;

    private PartitionalClusterer(final int numRequestedClusters,
        final IncrementalClusterCreator<ClusterType, DataType> creator)
    {
        super(DEFAULT_MAX_ITERATIONS);

        this.setNumRequestedClusters(numRequestedClusters);
        this.setCreator(creator);

        this.setMinClusterSize(DEFAULT_MIN_CLUSTER_SIZE);
        this.setMaxCriterionDecrease(DEFAULT_MAX_CRITERION_DECREASE);

        this.setClusters(null);
        this.setClustersHierarchy(null);

        this.setRandom(new Random());
    }

    /**
     * Creates a new partitional clusterer.
     *
     * @param numRequestedClusters The number of clusters to create. Note that
     * fewer than the requested number of cluster can be returned due to the
     * algorithm stopping due to one of the other stopping criteria.
     * @param divergenceFunction The distance metric between a cluster and a
     * point. The metric is wrapped by a {@link WithinClusterDivergenceWrapper}.
     * @param creator The method for creating clusters
     */
    public PartitionalClusterer(final int numRequestedClusters,
        final ClusterDivergenceFunction<ClusterType, DataType> divergenceFunction,
        final IncrementalClusterCreator<ClusterType, DataType> creator)
    {
        this(numRequestedClusters, creator);
        this.setDivergenceFunction(divergenceFunction);
    }

    /**
     * Creates a new partitional clusterer.
     *
     * @param numRequestedClusters The number of clusters to create. Note that
     * fewer than the requested number of cluster can be returned due to the
     * algorithm stopping due to one of the other stopping criteria.
     * @param divergenceFunction The distance metric between a cluster and a
     * point. The metric is wrapped by a {@link WithinClusterDivergenceWrapper}.
     * @param creator The method for creating clusters
     */
    public PartitionalClusterer(final int numRequestedClusters,
        final WithinClusterDivergence<ClusterType, DataType> divergenceFunction,
        final IncrementalClusterCreator<ClusterType, DataType> creator)
    {
        this(numRequestedClusters, creator);
        this.setWithinClusterDivergenceFunction(divergenceFunction);
    }

    /**
     * Create a partitional clusterer, using Euclidean distance and a vector
     * mean centroid cluster creator. This is probably what you want.
     *
     * @param numRequestedClusters
     * @return
     */
    public static PartitionalClusterer<Vector, CentroidCluster<Vector>> create(
        final int numRequestedClusters)
    {
        CentroidClusterDivergenceFunction<Vector> divergenceFunction
            = new CentroidClusterDivergenceFunction<>(
                EuclideanDistanceMetric.INSTANCE);
        return new PartitionalClusterer<>(numRequestedClusters,
            divergenceFunction, VectorMeanCentroidClusterCreator.INSTANCE);
    }

    /**
     * Create a spherical partitional clusterer, using Cosine distance and a
     * vector mean centroid cluster creator. If you need spherical clustering,
     * this is probably what you want.
     *
     * @param numRequestedClusters
     * @return
     */
    public static PartitionalClusterer<Vectorizable, NormalizedCentroidCluster<Vectorizable>> createSpherical(
        final int numRequestedClusters)
    {
        WithinNormalizedCentroidClusterCosineDivergence<Vectorizable> divergenceFunction
            = new WithinNormalizedCentroidClusterCosineDivergence<>();
        NormalizedCentroidClusterCreator creator
            = new NormalizedCentroidClusterCreator();
        return new PartitionalClusterer<>(numRequestedClusters,
            divergenceFunction, creator);
    }

    @Override
    public PartitionalClusterer<DataType, ClusterType> clone()
    {
        @SuppressWarnings("unchecked")
        final PartitionalClusterer<DataType, ClusterType> result
            = (PartitionalClusterer<DataType, ClusterType>) super.clone();

        result.clusterDivergenceFunction = ObjectUtil.cloneSmart(
            this.clusterDivergenceFunction);
        result.divergenceFunction = ObjectUtil.cloneSmart(
            this.divergenceFunction);
        result.creator = ObjectUtil.cloneSmart(this.creator);

        result.clusters = null;
        result.clustersHierarchy = null;

        return result;
    }

    @Override
    public ClusterHierarchyNode<DataType, ClusterType> clusterHierarchically(
        final Collection<? extends DataType> data)
    {
        this.learn(data);

        // Get the root of the hierarchy, or null if no clusters.
        return CollectionUtil.isEmpty(this.clustersHierarchy) ? null
            : this.clustersHierarchy.get(0);
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        if (!useCachedClusters)
        {
            // Initialize our data structures.
            this.setClusters(new ArrayList<>());
            this.setClustersHierarchy(new ArrayList<>());

            // Initialize one cluster for all elements.
            final ArrayList<DataType> dataList = new ArrayList<>(this.data);

            final ClusterType cluster = this.creator.createCluster(dataList);
            this.clusters.add(cluster);
            this.clustersHierarchy.add(new BinaryClusterHierarchyNode<>(cluster));
            this.clusterIndex = 0;
        }

        return true;
    }

    @Override
    protected boolean step()
    {
        // Make sure there are still clusters to split.
        if (clusterIndex >= this.getClusterCount() || this.getClusterCount()
            >= 2 * this.getNumRequestedClusters() - 1)
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
        final BinaryClusterHierarchyNode<DataType, ClusterType> clusterToSplitNode
            = this.clustersHierarchy.get(clusterIndex);

        // Randomly Partition the cluster.
        final DefaultPair<ClusterType, ClusterType> clusterChildren
            = this.randomPartition(clusterToSplit);
        final ClusterType leftCluster = clusterChildren.getFirst();
        final ClusterType rightCluster = clusterChildren.getSecond();

        // Greedily Swap the elements of the two clusters
        this.greedySwap(clusterToSplit.getMembers(), leftCluster, rightCluster);

        if ((leftCluster.getMembers().size() >= this.minClusterSize)
            && (rightCluster.getMembers().size() >= this.minClusterSize))
        {
            final double clusterCriteria
                = this.clusterDivergenceFunction.evaluate(clusterToSplit);
            final double leftCriteria = this.clusterDivergenceFunction.evaluate(
                leftCluster);
            final double rightCriteria
                = this.clusterDivergenceFunction.evaluate(rightCluster);
            final boolean improved = Math.abs((clusterCriteria
                * this.maxCriterionDecrease)
                - (leftCriteria + rightCriteria)) > this.tolerance;

            if (improved)
            {
                this.clusters.add(leftCluster);
                this.clusters.add(rightCluster);
                final BinaryClusterHierarchyNode<DataType, ClusterType> leftNode
                    = new BinaryClusterHierarchyNode<>(leftCluster);
                final BinaryClusterHierarchyNode<DataType, ClusterType> rightNode
                    = new BinaryClusterHierarchyNode<>(rightCluster);

                this.clustersHierarchy.add(leftNode);
                this.clustersHierarchy.add(rightNode);
                clusterToSplitNode.setFirstChild(leftNode);
                clusterToSplitNode.setSecondChild(rightNode);
            }
        }

        clusterIndex++;

        return true;
    }

    @Override
    protected void cleanupAlgorithm()
    {
        if (!useCachedClusters)
        {
            // Any clean up would go here.
        }
    }

    @Override
    public ArrayList<ClusterType> getResult()
    {

        ArrayList<NodeCriterionPair<DataType, ClusterType>> queue
            = new ArrayList<>();

        //contains all terminal nodes so far
        ArrayList<NodeCriterionPair<DataType, ClusterType>> terminals
            = new ArrayList<>();
        BinaryClusterHierarchyNode<DataType, ClusterType> root
            = this.clustersHierarchy.get(0);
        NodeCriterionPair<DataType, ClusterType> rootNodePair
            = new NodeCriterionPair<>(root, this.clusterDivergenceFunction);
        queue.add(rootNodePair);
        terminals.add(rootNodePair);
        while ((queue.size() > 0) && (terminals.size()
            < this.getNumRequestedClusters()))
        {
            NodeCriterionPair<DataType, ClusterType> nodeCriterion
                = queue.get(0);
            ClusterHierarchyNode<DataType, ClusterType> node
                = nodeCriterion.node;
            if (node.hasChildren())
            {
                for (ClusterHierarchyNode<DataType, ClusterType> child
                    : node.getChildren())
                {
                    NodeCriterionPair<DataType, ClusterType> np
                        = new NodeCriterionPair<>(child,
                            this.clusterDivergenceFunction);
                    int index = Collections.binarySearch(queue, np, np);
                    if (index < 0)
                    {
                        queue.add(-1 * (index + 1), np);
                        terminals.add(np);
                    }
                    else
                    {
                        queue.add(index, np);
                        terminals.add(np);
                    }
                }
                terminals.remove(nodeCriterion);
            }
            queue.remove(0);
        }

        //set final cluster data
        ArrayList<ClusterType> results = new ArrayList<>();
        for (NodeCriterionPair<DataType, ClusterType> np : terminals)
        {
            results.add(np.node.getCluster());
        }

        return results;
    }

    /**
     * Perform clustering by extending the existing cluster hierarchy, if one
     * exists. This provides an efficient way to compare clusterings with
     * different numbers of clusters, since the hierarchy does not need to be
     * recreated upon every call to {@code learn()}.
     *
     * @param data The data to cluster. If the existing clustering was created
     * using a data set other than {@code data}, these results will likely be
     * useless.
     * @return The requested clusters. Note that the number of clusters returned
     * may be less than the requested number of clusters if it is not possible
     * to create the number of clusters requested.
     */
    public Collection<ClusterType> learnUsingCachedClusters(
        Collection<? extends DataType> data)
    {
        if (this.clusters == null || this.clusters.size() <= 0)
        {
            // Start from scratch
            learn(data);
        }
        else if (this.clusters.size() / 2 + 1 < this.numRequestedClusters)
        {
            // Start from current clustering
            useCachedClusters = true;
            learn(data);
            useCachedClusters = false;
        }
        else
        {
            // Clustering already computed!
        }

        return getResult();
    }

    /**
     * Randomly partitions the input cluster into two clusters, where each
     * cluster will contain at least one element
     *
     * @param clusterToSplit The cluster that will be split into two clusters.
     * @return An ArrayList containing two clusters representing a random split
     * of the input cluster.
     */
    private DefaultPair<ClusterType, ClusterType> randomPartition(
        final ClusterType clusterToSplit)
    {
        final ArrayList<DataType> leftMembers = new ArrayList<>();
        final ArrayList<DataType> rightMembers = new ArrayList<>();

        for (DataType member : clusterToSplit.getMembers())
        {
            // Randomly add to one cluster
            if (this.random.nextBoolean())
            {
                leftMembers.add(member);
            }
            else
            {
                rightMembers.add(member);
            }
        }

        // Add to first cluster if it is empty
        if (leftMembers.isEmpty())
        {
            int rightSize = rightMembers.size();
            int randomIndex = this.random.nextInt(rightSize);
            DataType randomValue
                = rightMembers.set(randomIndex, rightMembers.get(rightSize - 1));
            rightMembers.remove(rightSize - 1);
            leftMembers.add(randomValue);
        }

        if (rightMembers.isEmpty())
        {
            int leftSize = leftMembers.size();
            int randomIndex = this.random.nextInt(leftSize);
            DataType randomValue
                = leftMembers.set(randomIndex, leftMembers.get(leftSize - 1));
            leftMembers.remove(leftSize - 1);
            rightMembers.add(randomValue);
        }

        // Initialize list of two clusters
        return DefaultPair.create(
            this.creator.createCluster(leftMembers),
            this.creator.createCluster(rightMembers));
    }

    /**
     * Greedily swaps elements between two clusters to increase the optimization
     * criterion until no further improvement is seen.
     *
     * @param data The list of all the data elements in the two clusters. Must
     * equal the sizes of the elements of both clusters.
     * @param leftCluster The left cluster for swapping.
     * @param rightCluster The right cluster for swapping.
     */
    private void greedySwap(
        Collection<DataType> data,
        ClusterType leftCluster,
        ClusterType rightCluster)
    {
        // Indicates if any improvements were made during an iteration
        boolean improved = true;

        // Evaluate criterion
        double criterion = this.clusterDivergenceFunction.evaluate(leftCluster)
            + this.clusterDivergenceFunction.evaluate(rightCluster);

        // Greedy swap until no improvement
        while (improved)
        {
            improved = false;

            for (DataType member : data)
            {
                // Try out doing a swap.
                this.swapElement(leftCluster, rightCluster, member);
                double testCriterion = this.clusterDivergenceFunction.evaluate(
                    leftCluster)
                    + this.clusterDivergenceFunction.evaluate(rightCluster);

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
        if (leftCluster.getMembers().contains(element)
            && leftCluster.getMembers().size() > 1)
        {
            this.creator.removeClusterMember(leftCluster, element);
            this.creator.addClusterMember(rightCluster, element);
        }
        else if (rightCluster.getMembers().contains(element)
            && rightCluster.getMembers().size() > 1)
        {
            this.creator.removeClusterMember(rightCluster, element);
            this.creator.addClusterMember(leftCluster, element);
        }
    }

    /**
     * Gets the total number of clusters created.
     *
     * @return The total number of clusters created.
     */
    public int getClusterCount()
    {
        return this.clusters == null ? 0 : this.clusters.size();
    }

    /**
     * Gets the metric on clusters used for partitioning.
     *
     * @return The metric on clusters.
     */
    public WithinClusterDivergence<? super ClusterType, ? super DataType>
        getWithinClusterDivergenceFunction()
    {
        return this.clusterDivergenceFunction;
    }

    /**
     * Gets the stored metric between a cluster and a point. If not null, this
     * metric is wrapped by a {@link WithinClusterDivergenceWrapper}.
     *
     * @return A {@link DivergenceFunction} providing a distance between a
     * cluster and a point.
     */
    @Override
    public DivergenceFunction<? super ClusterType, ? super DataType>
        getDivergenceFunction()
    {
        return this.divergenceFunction;
    }

    /**
     * Use a metric between a cluster and a point to update the metric on
     * clusters.
     *
     * @param divergenceFunction The metric between a point and a point used to
     * update the metric on clusters.
     */
    public void setDivergenceFunction(
        DivergenceFunction<? super ClusterType, ? super DataType> divergenceFunction)
    {
        ArgumentChecker.assertIsNotNull("divergenceFunction", divergenceFunction);
        this.setWithinClusterDivergenceFunction(
            new WithinClusterDivergenceWrapper<>(divergenceFunction));
        this.divergenceFunction = divergenceFunction;
    }

    /**
     * Sets the metric on clusters used for partitioning.
     *
     * @param clusterDivergenceFunction The metric on clusters
     */
    public void setWithinClusterDivergenceFunction(
        WithinClusterDivergence<? super ClusterType, ? super DataType> clusterDivergenceFunction)
    {
        ArgumentChecker.assertIsNotNull("clusterDivergenceFunction",
            clusterDivergenceFunction);
        this.clusterDivergenceFunction = clusterDivergenceFunction;
        this.divergenceFunction = null;
    }

    /**
     * Gets the current tolerance value. The tolerance is used for the error
     * bounds when determining if two floating point numbers are the same.
     *
     * @return The current tolerance.
     */
    public double getTolerance()
    {
        return this.tolerance;
    }

    /**
     * Sets the tolerance value. The tolerance is used for the error bounds when
     * determining if two floating point numbers are the same.
     *
     * @param tolerance The new tolerance to use.
     */
    public void setTolerance(double tolerance)
    {
        this.tolerance = tolerance;
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
        ArgumentChecker.assertIsNotNull("creator", creator);
        this.creator = creator;
    }

    @Override
    public Random getRandom()
    {
        return this.random;
    }

    @Override
    public void setRandom(Random random)
    {
        ArgumentChecker.assertIsNotNull("random", random);
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
     * Sets the minimum number of elements per cluster to allow. If the number
     * of elements in a cluster is less than or equal to this number, it will
     * not be bisected.
     *
     * @param minClusterSize The new minimum number of elements per cluster
     * allowed. Must be greater than zero.
     */
    public void setMinClusterSize(int minClusterSize)
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
     * Sets the maximum decrease in the training criterion allowed following a
     * bisection. If the decrease in criterion of a bisection step is greater
     * than this value, partitioning will stop. To turn off this stopping
     * criterion set this to 1.0 or greater.
     *
     * @param maxCriterionDecrease The new maximum minimum distance.
     */
    public void setMaxCriterionDecrease(double maxCriterionDecrease)
    {
        ArgumentChecker.assertIsPositive("maxCriterionDecrease",
            maxCriterionDecrease);
        this.maxCriterionDecrease = maxCriterionDecrease;
    }

    /**
     * Gets all clusters created.
     *
     * @return A list of the flatten hierarchy of clusters created.
     */
    public ArrayList<ClusterType> getClusters()
    {
        return this.clusters;
    }

    /**
     * Sets the clusters cache to the provided value. Usually used to clear the
     * list by setting the value to null.
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
     * @param clustersHierarchy The hierarchy of clusters.
     */
    protected void setClustersHierarchy(
        final ArrayList<BinaryClusterHierarchyNode<DataType, ClusterType>> clustersHierarchy)
    {
        this.clustersHierarchy = clustersHierarchy;
    }

    /**
     * Gets the current number of clusters requested. Note that fewer clusters
     * may be returned by partitioning algorithms since partitioning may end due
     * to one of the other stopping criterion.
     *
     * @return The current number of clusters requested.
     */
    public int getNumRequestedClusters()
    {
        return this.numRequestedClusters;
    }

    /**
     * Sets the number of clusters requested. Note that fewer clusters may be
     * returned by partitioning algorithms since partitioning may end due to one
     * of the other stopping criterion.
     *
     * @param numRequestedClusters The number of clusters requested.
     */
    public void setNumRequestedClusters(int numRequestedClusters)
    {
        this.numRequestedClusters = numRequestedClusters;
    }

    /**
     * Wraps a ClusterHierarchy node with criterion information. The primary
     * purpose of the class is to provide an order for clusters.
     */
    private class NodeCriterionPair<DataType, ClusterType extends Cluster<DataType>>
        implements Comparator<NodeCriterionPair<DataType, ClusterType>>
    {

        ClusterHierarchyNode<DataType, ClusterType> node;

        double criterion;

        /**
         * Update the criterion information by applying a function to the
         * cluster members.
         *
         * @param node The cluster to apply the function to and set this node
         * to.
         * @param function The function to apply to the cluster members to
         * update the criterion.
         */
        public NodeCriterionPair(
            ClusterHierarchyNode<DataType, ClusterType> node,
            WithinClusterDivergence<? super ClusterType, ? super DataType> function)
        {
            this.node = node;
            this.criterion = function.evaluate(this.node.getCluster());
        }

        /**
         * Compares two nodes criterion values.
         *
         * @param a The first node to compare.
         * @param b The second node to compare.
         * @return -1, 0, 1 provided a's criterion is greater than, equal to or
         * less than b's cirterion, respectively.
         */
        @Override
        public int compare(NodeCriterionPair<DataType, ClusterType> a,
            NodeCriterionPair<DataType, ClusterType> b)
        {
            //high cluster divergence is considered worse or smaller
            if (a.criterion > b.criterion)
            {
                return -1;
            }
            if (a.criterion == b.criterion)
            {
                return 0;
            }
            return 1;
        }

    }

}

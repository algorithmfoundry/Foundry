/*
 * File:                KMeansFactory.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Jun 22, 2009, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government.
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.algorithm.ParallelUtil;
import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.factory.Factory;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.VectorMeanCentroidClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.FixedClusterInitializer;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.GreedyClusterInitializer;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.Semimetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.util.AbstractRandomized;
import java.util.Random;

/**
 * Creates a parallelized version of the k-means clustering algorithm for the
 * typical use: clustering vector data with a Euclidean distance metric.
 *
 * @author  Kevin R. Dixon
 * @since   3.0
 */
@CodeReview(
    reviewer="Justin Basilico",
    date="2009-06-29",
    changesNeeded=false,
    comments="Changed it to not have a default random, otherwise its good."
)
public class KMeansFactory
    extends AbstractRandomized
    implements Factory<ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>>>
{

    /** The default number of clusters is {@value}. */
    public static final int DEFAULT_NUM_CLUSTERS = 
        KMeansClusterer.DEFAULT_NUM_REQUESTED_CLUSTERS;

    /**
     * Number of clusters to use. Must be positive.
     */
    private int numClusters;

    /** 
     * Creates a new instance of KMeansFactory 
     */
    public KMeansFactory()
    {
        this(DEFAULT_NUM_CLUSTERS);
    }

    /**
     * Creates a new instance of KMeansFactory.
     *
     * @param   numClusters
     *      The number of clusters to use.
     */
    public KMeansFactory(
        final int numClusters)
    {
        this(numClusters, new Random());
    }

    /**
     * Creates a new instance of KMeansFactory.
     *
     * @param   numClusters
     *      The number of clusters to use.
     * @param   random
     *      The random number generator.
     */
    public KMeansFactory(
        final int numClusters,
        final Random random)
    {
        super(random);

        this.setNumClusters(numClusters);
    }

    /**
     * Creates a new parallelized k-means clustering algorithm for vector data
     * with the given number of clusters (k) and random number generator.
     *
     * @param   numClusters
     *      Number of clusters to use (k).
     * @param   random
     *      Random number generator. Used in the cluster initialization.
     * @return
     *      k-means clustering algorithm for Vector-based clustering.
     */
    public static ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>> create(
        final int numClusters,
        final Random random)
    {
        return create(numClusters, EuclideanDistanceMetric.INSTANCE, random);
    }

    /**
     * Creates a new parallelized k-means clustering algorithm for vector data
     * with the given number of clusters (k), distance metric, and random
     * number generator.
     *
     * @param   numClusters
     *      Number of clusters to use (k).
     * @param   distanceMetric
     *      The distance metric for vectors to use.
     * @param   random
     *      Random number generator. Used in the cluster initialization.
     * @return
     *      k-means clustering algorithm for Vector-based clustering.
     */
    public static ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>> create(
        final int numClusters,
        final Semimetric<? super Vector> distanceMetric,
        final Random random)
    {
        // Use a mean cluster creator.
        final VectorMeanCentroidClusterCreator clusterCreator =
            new VectorMeanCentroidClusterCreator();

        // Use a greedy cluster initializer.
        final FixedClusterInitializer<CentroidCluster<Vector>, Vector> initializer =
            new GreedyClusterInitializer<CentroidCluster<Vector>, Vector>(
                distanceMetric, clusterCreator, random);
        
        // Use centroid clusters.
        final CentroidClusterDivergenceFunction<Vector> clusterDivergence =
            new CentroidClusterDivergenceFunction<Vector>(distanceMetric);

        // Create the k-means algorithm.
        return new ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>>(
            numClusters, KMeansClusterer.DEFAULT_MAX_ITERATIONS,
            ParallelUtil.createThreadPool(), initializer, clusterDivergence,
                clusterCreator);
    }

    public ParallelizedKMeansClusterer<Vector, CentroidCluster<Vector>> create()
    {
        return create(this.getNumClusters(), this.getRandom());
    }

    /**
     * Gets the number of clusters for the algorithm to attempt to create.
     *
     * @return
     *      The number of clusters.
     */
    public int getNumClusters()
    {
        return this.numClusters;
    }

    /**
     * Sets the number of clusters for the algorithm to attempt to create. Must
     * be positive.
     *
     * @param   numClusters
     *      The number of clusters for the algorithm to attempt to create.
     */
    public void setNumClusters(
        final int numClusters)
    {
        if (numClusters <= 0)
        {
            throw new IllegalArgumentException("numClusters must be positive.");
        }
        
        this.numClusters = numClusters;
    }

}

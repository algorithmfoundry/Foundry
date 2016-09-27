/*
 * File:                MiniBatchKMeansClusterer.java
 * Authors:             Jeff Piersol
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright April 1, 2016, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.MiniBatchClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.VectorMeanMiniBatchCentroidClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.FixedClusterInitializer;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.GreedyClusterInitializer;
import gov.sandia.cognition.learning.function.distance.EuclideanDistanceMetric;
import gov.sandia.cognition.math.Semimetric;
import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.statistics.DiscreteSamplingUtil;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.RandomAccess;
import static java.util.stream.Collectors.*;
import java.util.stream.IntStream;

/**
 * Approximates <i>k</i>-means clustering by working on random subsets of the
 * data. This method is particularly useful for large data sets.
 *
 * For spherical <i>k</i>-means, use a Cosine distance semimetric for the
 * divergence function, normalize all input data, and normalize each centroid
 * after the algorithm converges.
 *
 * @param <DataType> The type of the data to cluster. This is typically defined
 * by the divergence function used.
 * @author Jeff Piersol
 * @since 3.4.4
 */
@PublicationReference(
    author = "Jeff Piersol",
    title = "Parallel Mini-Batch k-means Clustering",
    type = PublicationType.Conference,
    year = 2016,
    publication
    = "to appear",
    url
    = "to appear"
)
public class MiniBatchKMeansClusterer<DataType extends Vector>
    extends KMeansClusterer<DataType, CentroidCluster<DataType>>
    implements Randomized
{

    private static final long serialVersionUID = 2587013040037999607L;

    /**
     * The default maximum number of iterations is {@value}.
     */
    public static final int DEFAULT_MAX_ITERATIONS = 100000;

    /**
     * The random number generator to use for initialization and subset
     * selection.
     */
    protected Random random;

    /**
     * The size of the mini-batches.
     */
    private int minibatchSize;

    /**
     * Indices of the data. This should be a range of ints= [0, data.size)
     */
    protected List<Integer> dataIndices;

    /**
     * Indicates if the iteration process should stop early. If the fraction of
     * samples that changed assignment is lower than this number, iteration
     * stops.
     */
    private double stoppingCriterion = 0.01;

    /**
     * Create a clusterer with the default parameters. This is a 'vanilla'
     * mini-batch k-means, using Euclidean distance for the semimetric.
     *
     * @param numClusters the number of clusters to output
     */
    @SuppressWarnings("unchecked")
    public MiniBatchKMeansClusterer(int numClusters)
    {
        this(numClusters, new Random(),
            (MiniBatchClusterCreator) new VectorMeanMiniBatchCentroidClusterCreator());
    }

    private MiniBatchKMeansClusterer(int numClusters,
        Random random,
        MiniBatchClusterCreator<CentroidCluster<DataType>, DataType> creator)
    {
        this(
            numClusters, DEFAULT_MAX_ITERATIONS,
            new GreedyClusterInitializer<>(EuclideanDistanceMetric.INSTANCE,
                creator, random),
            EuclideanDistanceMetric.INSTANCE, creator, random
        );
    }

    /**
     * Creates a new {@link MiniBatchKMeansClusterer}.
     *
     * @param numClusters the number of clusters to create
     * @param maxIterations the number of iterations before stopping
     * @param initializer sets the initial centroids
     * @param metric the metric to use
     * @param creator the cluster creator to use
     * @param random the random number generator to use
     */
    public MiniBatchKMeansClusterer(
        int numClusters,
        int maxIterations,
        FixedClusterInitializer<CentroidCluster<DataType>, DataType> initializer,
        Semimetric<? super DataType> metric,
        MiniBatchClusterCreator<CentroidCluster<DataType>, DataType> creator,
        Random random)
    {
        super(numClusters, maxIterations, initializer,
            new CentroidClusterDivergenceFunction<>(metric), creator);
        this.setRandom(random);
    }

    @Override
    @SuppressWarnings("unchecked")
    public MiniBatchKMeansClusterer<DataType> clone()
    {
        final MiniBatchKMeansClusterer<DataType> result
            = (MiniBatchKMeansClusterer<DataType>) super.clone();
        random = ObjectUtil.cloneSmart(random); //TODO: is this really what we want to do?
        return result;
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        boolean superReturn = super.initializeAlgorithm();
        if (superReturn)
        {
            minibatchSize = getNumClusters() < 1 ? 0 : minibatchSize <= 0
                ? Math.min(getNumElements(), 10000)
                : minibatchSize; // I totally made up this heuristic
        }
        return superReturn;
    }

    /**
     * Do a step of the clustering algorithm.
     *
     * @return true means keep going, false means stop clustering.
     */
    @Override
    protected boolean step()
    {
        // First, assign each data point to a cluster, given the current
        // location of the clusters
        List<? extends DataType> data = getData();

        ArrayList<Integer> sampleIndices
            = DiscreteSamplingUtil.sampleWithReplacement(random, dataIndices,
                minibatchSize);

        List<DataType> samples
            = sampleIndices.stream().map(data::get).collect(toList());

        int[] sampleAssignments = this.assignDataToClusters(samples);

        MiniBatchClusterCreator<CentroidCluster<DataType>, DataType> creator
            = getCreator();

        // Bin all of the samples into their clusters
        Map<CentroidCluster<DataType>, List<DataType>> samplesInCluster
            = IntStream.range(0, sampleIndices.size()).parallel()
            .mapToObj(Integer::valueOf)
            .collect(groupingByConcurrent(idx -> clusters.get(
                sampleAssignments[idx]), mapping(idx -> samples.get(
                idx), toList())));

        // Update centroids
        samplesInCluster.entrySet().stream().parallel().forEach(
            (Map.Entry<CentroidCluster<DataType>, List<DataType>> clusterAndSamples)
            -> creator.updatePrototype(clusterAndSamples.getKey(),
                clusterAndSamples.getValue()));

        int numChanged = 0;

        for (int i = 0; i < sampleAssignments.length; i++)
        {
            int assignment = sampleAssignments[i];
            if (this.setAssignment(sampleIndices.get(i), assignment))
            {
                numChanged++;
            }
        }

        this.setNumChanged(numChanged);

        // Continue iterating if a significant number of points changed assignment
        return this.getNumChanged() / (double) minibatchSize > stoppingCriterion;
    }

    /**
     * Saves the final clustering for each data point.
     */
    protected void saveFinalClustering()
    {
        if (clusters.size() > 0)
        {
            List<? extends DataType> data = getData();
            assignments = assignDataToClusters(data);

            clusters.forEach(cluster -> cluster.getMembers().clear());
            IntStream.range(0, assignments.length).parallel()
                .mapToObj(Integer::valueOf)
                .collect(
                    groupingByConcurrent(idx -> assignments[idx]))
                .forEach((clusterIdx, clusterPoints)
                    -> clusters.get(clusterIdx).getMembers()
                    .addAll(
                        clusterPoints.stream()
                        .map(idx -> data.get(idx))
                        .collect(toList())));
        }
    }

    @Override
    protected void cleanupAlgorithm()
    {
        saveFinalClustering();
    }

    @Override
    public Random getRandom()
    {
        return random;
    }

    @Override
    public final void setRandom(Random random)
    {
        this.random = random;
    }

    @Override
    public List<? extends DataType> getData()
    {
        return (List<? extends DataType>) super.getData();
    }

    /**
     * Set the data to be clustered. If the data is not a
     * {@link RandomAccess} {@link List}, it will be copied into one.
     *
     * @param data
     */
    @Override
    public void setData(Collection<? extends DataType> data)
    {
        if (data == null)
        {
            data = new ArrayList<>();
        }
        super.setData(data instanceof List && data instanceof RandomAccess
            ? data
            : new ArrayList<>(data));
        this.dataIndices = IntStream.range(0, data.size()).boxed().collect(
            toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public CentroidClusterDivergenceFunction<DataType> getDivergenceFunction()
    {
        return (CentroidClusterDivergenceFunction) super.getDivergenceFunction();
    }

    /**
     * Get the stopping criterion for this clusterer. See
     * {@link #setStoppingCriterion(double)} for details on the criterion.
     *
     * @return
     */
    public double getStoppingCriterion()
    {
        return stoppingCriterion;
    }

    /**
     * Set the stopping criterion for this clusterer.
     *
     * @param stoppingCriterion if the fraction of samples that changed
     * assignment is lower than this number, iteration stops. Set this to a
     * negative value to disable early stopping
     */
    public void setStoppingCriterion(double stoppingCriterion)
    {
        this.stoppingCriterion = stoppingCriterion;
    }

    /**
     * Get the size of the mini-batches used.
     *
     * @return
     */
    public int getMinibatchSize()
    {
        return minibatchSize;
    }

    /**
     * Set the size of the mini-batches. If the size is &le;0, a heuristic will
     * be used to compute the size before clustering.
     *
     * @param minibatchSize
     */
    public void setMinibatchSize(int minibatchSize)
    {
        this.minibatchSize = minibatchSize;
    }

    @Override
    public MiniBatchClusterCreator<CentroidCluster<DataType>, DataType> getCreator()
    {
        return (MiniBatchClusterCreator<CentroidCluster<DataType>, DataType>) super.getCreator();
    }

    /**
     * {@inheritDoc}
     *
     * The given creator must be a {@link MiniBatchClusterCreator}, or this
     * method call will fail.
     *
     * @param creator
     */
    @Override
    public void setCreator(
        ClusterCreator<CentroidCluster<DataType>, DataType> creator)
    {
        if (!(creator instanceof MiniBatchClusterCreator))
        {
            throw new IllegalArgumentException(
                "For mini-batch clustering, the cluster creator must be of type "
                + MiniBatchClusterCreator.class.getName());
        }
        MiniBatchClusterCreator<CentroidCluster<DataType>, DataType> mbcc
            = (MiniBatchClusterCreator<CentroidCluster<DataType>, DataType>) creator;
        this.setCreator(mbcc);
    }

    public void setCreator(
        MiniBatchClusterCreator<CentroidCluster<DataType>, DataType> creator)
    {
        super.setCreator(creator);
    }

    /**
     * Can be used to create custom {@link MiniBatchKMeansClusterer}s without
     * using the big constructor.
     *
     * @param <DataType>
     */
    public static class Builder<DataType extends Vector>
    {

        private int numClusters, maxIterations, minibatchSize;

        private FixedClusterInitializer<CentroidCluster<DataType>, DataType> initializer;

        private Semimetric<? super DataType> metric;

        private MiniBatchClusterCreator<CentroidCluster<DataType>, DataType> creator;

        private Random random;

        /**
         * Create a mini-batch <i>k</i>-means clusterer builder and set it to
         * the given number of clusters. Centroids will be initialized using a
         * {@link GreedyClusterInitializer}.
         *
         * @param numClusters
         */
        @SuppressWarnings("unchecked")
        public Builder(int numClusters)
        {
            this(numClusters, EuclideanDistanceMetric.INSTANCE);
        }

        /**
         * Create a mini-batch <i>k</i>-means clusterer builder and set it to
         * the given number of clusters. Centroids will be initialized using a
         * {@link GreedyClusterInitializer}, and the given metric will be used
         * to measure all distances.
         *
         * @param numClusters
         * @param metric the semimetric to use to measure distances
         */
        @SuppressWarnings("unchecked")
        public Builder(int numClusters,
            Semimetric<? super DataType> metric)
        {
            this.numClusters = numClusters;
            this.maxIterations = DEFAULT_MAX_ITERATIONS;
            this.random = new Random();
            this.creator
                = (MiniBatchClusterCreator) new VectorMeanMiniBatchCentroidClusterCreator();
            this.metric = metric;
            this.initializer = new GreedyClusterInitializer<>(this.metric,
                creator,
                random);
        }

        public MiniBatchKMeansClusterer<DataType> build()
        {
            MiniBatchKMeansClusterer<DataType> clusterer
                = new MiniBatchKMeansClusterer<>(numClusters, maxIterations,
                    initializer, metric, creator, random);
            clusterer.setMinibatchSize(minibatchSize);
            return clusterer;
        }

        /**
         * @param numClusters the number of clusters to create
         * @return the builder
         */
        public Builder<DataType> setNumClusters(int numClusters)
        {
            this.numClusters = numClusters;
            return this;
        }

        /**
         * @param maxIterations the number of iterations before stopping
         * @return the builder
         */
        public Builder<DataType> setMaxIterations(int maxIterations)
        {
            this.maxIterations = maxIterations;
            return this;
        }

        /**
         * @param minibatchSize the mini-batch size
         * @return the builder
         * @see MiniBatchKMeansClusterer#setMinibatchSize(int)
         */
        public Builder<DataType> setMinibatchSize(int minibatchSize)
        {
            this.minibatchSize = minibatchSize;
            return this;
        }

        /**
         * @param initializer sets the initial centroids
         * @return the builder
         */
        public Builder<DataType> setInitializer(
            FixedClusterInitializer<CentroidCluster<DataType>, DataType> initializer)
        {
            this.initializer = initializer;
            return this;
        }

        /**
         * @param creator the cluster creator to use
         * @return the builder
         */
        public Builder<DataType> setCreator(
            MiniBatchClusterCreator<CentroidCluster<DataType>, DataType> creator)
        {
            this.creator = creator;
            return this;
        }

        /**
         * @param random the random number generator to use
         * @return the builder
         */
        public Builder<DataType> setRandom(Random random)
        {
            this.random = random;
            return this;
        }

    }

}

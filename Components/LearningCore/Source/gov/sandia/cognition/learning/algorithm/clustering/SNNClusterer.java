/*
 * File:                SNNClusterer.java
 * Authors:             Melissa Bain
 * Compnay:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 */

package gov.sandia.cognition.learning.algorithm.clustering;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.AbstractAnytimeBatchLearner;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.learning.data.DefaultInputOutputPair;
import gov.sandia.cognition.learning.data.InputOutputPair;
import gov.sandia.cognition.math.Metric;
import gov.sandia.cognition.math.Semimetric;
import gov.sandia.cognition.math.geometry.KDTree;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.RandomAccess;

/**
 * The Shared Nearest Neighbor algorithm is a variation of DBSCAN that clusters
 * points with a high number of mutual near neighbors. SNN requires a distance
 * metric, a cluster creator, an integer k (representing the number of nearest
 * neighbors to use), an epsilon value (signifying the minimum number of shared
 * neighbors two points need to be considered strongly connected), and a min
 * points integer (serving as a minimum threshold of strongly connected
 * neighbors for a point to be considered a core point). The algorithm uses the
 * distance metric to calculate nearest neighbors, but then clusters based on
 * mutual neighbors. This is done iteratively across all points, with any points
 * that do not have sufficient strongly connected neighbors nor neighbors that
 * are core points becoming noise points.
 *
 * @author mbain
 * @param <DataType> The type of the data to cluster.
 * @param <ClusterType> The type of {@code Cluster} created by the algorithm.
 */
@PublicationReference(
    author =
    {
        "Levent Ertoz",
        "Michael Steinbach",
        "Vipin Kumar"
    },
    title = "Finding Clusters of Different Sizes, Shapes and Densities in "
    + "Noisy, High Dimensional Data.",
    type = PublicationType.Conference,
    publication
    = "Proceedings of Second SIAM Interantional Conference on Data Mining",
    year = 2003,
    url = "http://www-users.cs.umn.edu/~kumar/papers/SIAM_snn.pdf"
)
public class SNNClusterer<DataType extends Vectorizable, ClusterType extends Cluster<DataType>>
    extends AbstractAnytimeBatchLearner<Collection<? extends DataType>, Collection<ClusterType>>
    implements BatchClusterer<DataType, ClusterType>
{

    /**
     * The default k is {@value}.
     */
    public static final int DEFAULT_K = 5;

    /**
     * The default eps is {@value}.
     */
    public static final int DEFAULT_EPS = 2;

    /**
     * The default min points is {@value}.
     */
    public static final int DEFAULT_MINPTS = 3;

    /**
     * The default maximum number of iterations {@value}
     */
    public static final int DEFAULT_MAX_ITERATIONS = Integer.MAX_VALUE;

    /**
     * The number of nearest neighbors.
     */
    private int k;

    /**
     * The similarity (shared nearest neighbors) score.
     */
    private int eps;

    /**
     * The minimum number of neighbors with a similarity score greater than or
     * equal to eps to be considered a core point.
     */
    private int minPts;

    /**
     * The distance metric to use.
     */
    private Semimetric<? super DataType> metric;

    /**
     * A map of each point to its nearest k neighbors
     */
    private HashMap<DataType, Set<DataType>> knnMap;

    /**
     * Number of clusters created so far.
     */
    private int clusterCount;

    /**
     * Set of points that have been clustered so far.
     */
    private HashSet<DataType> clustered;

    /**
     * Set of points that have been visited so far.
     */
    private HashSet<DataType> visited;

    /**
     * All the data in an indexible structure.
     */
    private List<? extends DataType> points;

    /**
     * The cluster creator for creating clusters.
     */
    private ClusterCreator<ClusterType, DataType> creator;

    /**
     * The current set of clusters.
     */
    private ArrayList<ClusterType> clusters;

    /**
     * The current cluster.
     */
    private ArrayList<DataType> currentCluster;

    /**
     * The cluster for points marked as noise.
     */
    private ArrayList<DataType> noiseCluster;

    /**
     * Index of the next point to process in step().
     */
    private int pointIndex;

    /**
     * Creates a new instance of SNNClusterer using the default settings.
     *
     * @param metric
     * @param creator
     */
    public SNNClusterer(Semimetric<? super DataType> metric,
        ClusterCreator<ClusterType, DataType> creator)
    {
        this(DEFAULT_K, DEFAULT_EPS, DEFAULT_MINPTS, metric, creator);
    }

    /**
     * Creates a new instance of SNNClusterer using manual settings.
     *
     * @param k The number of nearest neighbors to include
     * @param eps The minimum required similarity score (shared neighbors)
     * between two points
     * @param minPts The number of neighbors with a similarity score greater
     * than or equal to eps needed for the point to be considered a core point.
     * This parameter will control the number of total clusters
     * @param metric The distance semimetric to use when calculating nearest
     * neighbors.
     * @param creator The cluster creator.
     */
    public SNNClusterer(
        int k,
        int eps,
        int minPts,
        Semimetric<? super DataType> metric,
        ClusterCreator<ClusterType, DataType> creator)
    {
        super(DEFAULT_MAX_ITERATIONS);

        this.setNumNeighbors(k);
        this.setSimilarity(eps);
        this.setMinPts(minPts);
        this.setMetric(metric);
        this.setCreator(creator);
    }

    /**
     * Clones an SNN object
     *
     * @return an cloned version of the clusterer.
     */
    @Override
    public SNNClusterer<DataType, ClusterType> clone()
    {
        final SNNClusterer<DataType, ClusterType> result
            = (SNNClusterer<DataType, ClusterType>) super.clone();
        result.metric = ObjectUtil.cloneSmart(this.metric);
        result.clusters = null;

        return result;
    }

    /**
     * Initializes the SNN learner by checking there is data, building a tree of
     * nearest neighbors and using this tree to make a KNN map. This method also
     * initializes the noise cluster and other data structures used in the
     * clustering process. Here we deviate from the algorithm described by Ertoz
     * et. al. in their paper by considering a point to be its own neighbor. It
     * was deemed logical since if epsilon counts shared neighbors, direct ties
     * should be at least as strong of indicator of neighbor-ness.
     *
     * @return boolean whether the clusterer is ready to learn.
     */
    @Override
    protected boolean initializeAlgorithm()
    {
        if (this.getData() == null || this.getData().size() <= 0)
        {
            // Make sure that the data is valid.
            return false;
        }
        // Copy data into a data structure that can be indexed
        this.points = this.getData() instanceof List
            && this.getData() instanceof RandomAccess
            ? (List<? extends DataType>)this.getData()
            : new ArrayList<>(this.getData());
        
        // Create Nearest Neighbor Map
        this.knnMap = new HashMap<>();

        if (this.metric instanceof Metric)
        {
            // Construct a new KDTree with the data points
            List<InputOutputPair<DataType, Double>> pts = this.points.stream()
                .map(pt -> new DefaultInputOutputPair<>(pt, 0.0))
                .collect(Collectors.toList());
            KDTree<DataType, Double, InputOutputPair<DataType, Double>> nnSpatialIndex
                = new KDTree<>(pts);

            // Populate KNN map
            for (DataType point : this.points)
            {
                Set<DataType> neighbors = nnSpatialIndex.findNearest(point,
                    this.k,
                    (Metric<? super DataType>) this.metric)
                    .stream()
                    .map(pair -> pair.getFirst())
                    .collect(Collectors.toSet());

                this.knnMap.put(point, neighbors);
            }
        }
        else
        {
            double distance;
            InputOutputPair<DataType, Double> pointToQueue;

            for (DataType point : this.points)
            {
                Set<DataType> nearestNeighbors = new HashSet<>();

                // Make comparator to sort neighbors
                SemimetricPointComparator comparator
                    = new SemimetricPointComparator();

                // Make priority queue to sort neighbors for each point.
                PriorityQueue<InputOutputPair<DataType, Double>> neighbors
                    = new PriorityQueue<>(this.k, comparator);

                // Populate queue
                for (DataType otherPoint : this.points)
                {
                    distance = this.metric.evaluate(point, otherPoint);
                    pointToQueue = new DefaultInputOutputPair<>(otherPoint,
                        distance);
                    neighbors.add(pointToQueue);
                }

                // Get k nearest neighbors
                for (int i = 0; i < this.k; i++)
                {
                    nearestNeighbors.add(neighbors.remove().getInput());
                }

                // Add nearest neighbors to knn map
                this.knnMap.put(point, nearestNeighbors);
            }

        }

        // Initialize the main data structures for the algorithm
        this.clusters = new ArrayList<>();
        this.clustered = new HashSet<>();
        this.visited = new HashSet<>();
        this.currentCluster = new ArrayList<>();
        this.noiseCluster = new ArrayList<>();
        this.pointIndex = 0;

        // Noise cluster will be 0th cluster, start core clusters index at 1
        this.clusters.add(0, this.creator.createCluster(this.noiseCluster));
        this.clusterCount = 1;

        // Ready to learn.
        return true;
    }

    /**
     * Processes the next point by checking if the point has been visited and if
     * not, calling the method to determine the number of neighbors with at
     * least eps shared neighbors. If this is larger than min points, the expand
     * cluster method is called. Otherwise the point is added to the noise
     * cluster.
     *
     * @return boolean representing whether there are more points left to
     * process.
     */
    @Override
    protected boolean step()
    {
        // Retrieve the point to process
        DataType point = this.points.get(this.pointIndex);
        if (!this.visited.contains(point))
        {
            // Mark point as visited
            this.visited.add(point);

            // Get neighbors with at least eps common neighbors
            List<DataType> neighbors = this.getConnectedNeighbors(point);

            // If number of neighbors with at least eps shared neighbors is below 
            // the min points threshold, add point to noise cluster
            if (neighbors.size() < this.minPts)
            {
                this.noiseCluster.add(point);
                this.clustered.add(point);
            }
            // If there are at least minpts neighbors with at least eps shared 
            // neighbors consider the point to be a core point
            else
            {
                // Expand Cluster around the core point
                this.expandCluster(point, neighbors);

                // Add expanded cluster to list of clusters
                this.clusters.add(this.clusterCount, this.creator.createCluster(
                    this.currentCluster));

                // Increase cluster count and make new empty current cluster
                this.clusterCount++;
                this.currentCluster = new ArrayList<>();
            }
        }

        // Increase the point index and determine whether there are more points 
        // to process
        this.pointIndex++;
        return (this.pointIndex < this.points.size());
    }

    /**
     * Returns a list of all the points with at least eps shared nearest
     * neighbors.
     *
     * @param point The point to get the neighbors list for.
     * @return The list of all points with >= eps shared neighbors
     */
    private List<DataType> getConnectedNeighbors(DataType point)
    {
        List<DataType> result = new ArrayList<>();
        Set<DataType> neighbors = this.knnMap.get(point);

        // Loop through other points to look for mutual neighbors
        for (DataType otherPoint : this.points)
        {
            if (otherPoint != point)
            {
                Set<DataType> otherNeighbors = this.knnMap.get(otherPoint);
                Set<DataType> sharedNeighbors = new HashSet<>(neighbors);
                sharedNeighbors.retainAll(otherNeighbors);

                // If more than eps neighbors, add it to results
                if (sharedNeighbors.size() >= this.eps)
                {
                    result.add(otherPoint);
                }
            }
        }
        return result;
    }

    /**
     * Takes a core point and adds its neighbors to the cluster. Also merges any
     * neighboring clusters by iterating through neighbors and seeing if they
     * are core points. Records any processed points as "clustered".
     *
     * @param point The core point to expand the cluster around
     * @param neighbors The core point's neighbors.
     */
    private void expandCluster(DataType point,
        List<DataType> neighbors)
    {
        LinkedList<DataType> neighborsLinkedList = new LinkedList<>(neighbors);
        this.currentCluster.add(point);
        this.clustered.add(point);

        // Loop through each neighbor and process it
        while (!neighborsLinkedList.isEmpty())
        {
            DataType neighbor = neighborsLinkedList.remove();

            if (!this.visited.contains(neighbor))
            {
                this.visited.add(neighbor);
                List<DataType> neighborNeighbors = this.getConnectedNeighbors(
                    neighbor);

                // If neighbor is a core point, process its neighbors too
                if (neighborNeighbors.size() >= this.minPts)
                {
                    neighborsLinkedList.addAll(neighborNeighbors);
                }
            }
            // Mark neighbors as clustered and add them to current cluster
            if (!this.clustered.contains(neighbor))
            {
                this.currentCluster.add(neighbor);
                this.clustered.add(neighbor);
            }
        }
    }

    /**
     * Adds noise cluster to list of clusters.
     */
    @Override
    protected void cleanupAlgorithm()
    {
        this.clusters.set(0, this.creator.createCluster(this.noiseCluster));
    }

    /**
     * Gets a list of the clusters.
     *
     * @return a list of the clusters
     */
    @Override
    public Collection<ClusterType> getResult()
    {
        return this.getClusters();
    }

    /**
     * Sets the number of nearest neighbors to use.
     *
     * @param k The number of nearest neighbors k.
     */
    public void setNumNeighbors(int k)
    {
        this.k = k;
    }

    /**
     * Gets the number of nearest neighbors.
     *
     * @return k the number of nearest neighbors.
     */
    public int getNumNeighbors()
    {
        return this.k;
    }

    /**
     * Gets the number of clusters.
     *
     * @return the cluster count.
     */
    public int getClusterCount()
    {
        return this.clusterCount;
    }

    /**
     * Sets the similarity score (mutually shared nearest neighbors) threshold.
     *
     * @param eps The minimum similarity score required.
     */
    public void setSimilarity(int eps)
    {
        this.eps = eps;
    }

    /**
     * Gets the similarity score threshold (eps).
     *
     * @return eps
     */
    public int getSimiliarity()
    {
        return this.eps;
    }

    /**
     * Sets the minimum neighboring points to have a similarity score of at
     * least eps for the given point to be considered a core point.
     *
     * @param minPts The minimum points threshold.
     */
    public void setMinPts(int minPts)
    {
        this.minPts = minPts;
    }

    /**
     * Gets the minPts threshold to be considered a core point.
     *
     * @return minPts
     */
    public int getMinPts()
    {
        return this.minPts;
    }

    /**
     * Sets the distance metric the clustering uses.
     *
     * @param metric The metric.
     */
    public void setMetric(Semimetric<? super DataType> metric)
    {
        this.metric = metric;
    }

    /**
     * Gets the distance metric the clustering uses.
     *
     * @return The metric.
     */
    public Semimetric<? super DataType> getMetric()
    {
        return this.metric;
    }

    /**
     * Gets the current clusters, which is a sparse mapping of exemplar
     * identifier to cluster object.
     *
     * @return The current clusters.
     */
    protected ArrayList<ClusterType> getClusters()
    {
        return this.clusters;
    }

    /**
     * Get the cluster at this index.
     *
     * @param i The index of the cluster.
     * @return
     */
    public ClusterType getCluster(int i)
    {
        return this.getClusters().get(i);
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
     * Gets the cluster creator.
     *
     * @return The cluster creator.
     */
    public ClusterCreator<ClusterType, DataType> getCreator()
    {
        return this.creator;
    }

    /**
     * SemimetricPointComparator acts as a comparator for pairs of points where
     * each point also has the distance (a double given by the semimetric
     * evaluate method) between that point and a reference point.
     */
    private class SemimetricPointComparator
        implements Comparator<InputOutputPair<DataType, Double>>
    {

        /**
         * Compares two pairs of data points based on their distance to a common
         * third point (as given as the Output double).
         *
         * @param o1 an InputOutputPair
         * @param o2 another InputOutputPair
         * @return -1 if the first is smaller than the second, and 1 if the
         * second is smaller than the first. Returns 0 if equal
         */
        @Override
        public int compare(InputOutputPair<DataType, Double> o1,
            InputOutputPair<DataType, Double> o2)
        {
            if (o1.getOutput() < o2.getOutput())
            {
                return -1;
            }
            if (o1.getOutput() > o2.getOutput())
            {
                return 1;
            }
            return 0;
        }

    }

}

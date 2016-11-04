/*
 * File:                DBSCANClusterer.java
 * Authors:             Quinn McNamara
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright August 16, 2016, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
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
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The <code>DBSCAN</code> algorithm requires three parameters: a distance
 * metric, a value for neighborhood radius, and a value for the minimum number
 * of surrounding neighbors for a point to be considered non-noise. It clusters
 * by iterating point-by-point and grouping points that are close together (in
 * the same neighborhood). Points that are not in any neighborhood are labeled
 * as noise. Noise points are grouped into the first resultant cluster.
 * <BR><BR>
 * This implementation conditionally uses a KD tree to store the data points and
 * perform efficient queries for neighborhoods. The KD tree is only used when
 * the metric is a {@link Metric} (not a {@link Semimetric} like
 * CosineDistanceMetric). When one of these conditions is not met, neighborhood
 * querying has O(n) complexity, giving the overall algorithm O(n^2) time
 * complexity. If a KD tree is used, queries have O(logn) complexity, giving the
 * overall algorithm O(n logn) complexity.
 *
 * @param <DataType> The type of the data to cluster. This is typically defined
 * by the metric used.
 * @param <ClusterType> The type of {@code Cluster} created by the algorithm.
 * This is typically defined by the cluster creator function used.
 * @author Quinn McNamara
 * @since 3.4.4
 */
@PublicationReference(
    author =
    {
        "Martin Ester",
        "Hans-Peter Kriegel",
        "Jiirg Sander",
        "Xiaowei Xu"
    },
    title = "A Density-Based Algorithm for Discovering Clusters in "
    + "Large Spatial Databases with Noise.",
    type = PublicationType.Journal,
    publication = "AAAI Press",
    pages =
    {
        226 - 231
    },
    year = 1996,
    url
    = "https://www.aaai.org/Papers/KDD/1996/KDD96-037.pdf"
)
public class DBSCANClusterer<DataType extends Vectorizable, ClusterType extends Cluster<DataType>>
    extends AbstractAnytimeBatchLearner<Collection<? extends DataType>, Collection<ClusterType>>
    implements BatchClusterer<DataType, ClusterType>
{

    /**
     * The default eps is {@value}.
     */
    public static final double DEFAULT_EPS = 0.5;

    /**
     * The default minimum samples is {@value}.
     */
    public static final int DEFAULT_MIN_SAMPLES = 5;

    /**
     * The default maximum number of iterations {@value}
     */
    public static final int DEFAULT_MAX_ITERATIONS = Integer.MAX_VALUE;

    /**
     * The radius of a neighborhood.
     */
    private double eps;

    /**
     * The minimum number of samples in the neighborhood for the point to be
     * considered a core point.
     */
    private int minSamples;

    /**
     * The distance metric to use.
     */
    private Semimetric<? super DataType> metric;

    /**
     * A spatial index of the points to improve neighborhood querying.
     */
    private KDTree<DataType, Double, InputOutputPair<DataType, Double>> spatialIndex;

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
    private ArrayList<DataType> points;

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
     * Creates a new instance of DBSCANClusterer.
     *
     * @param metric
     * @param creator
     */
    public DBSCANClusterer(
        Semimetric<? super DataType> metric,
        ClusterCreator<ClusterType, DataType> creator)
    {
        this(DEFAULT_EPS, DEFAULT_MIN_SAMPLES, metric, creator);
    }

    /**
     * Creates a new instance of AffinityPropagation.
     *
     * @param eps The divergence function to use to determine the divergence
     * between two examples.
     * @param minSamples The value for self-divergence to use, which controls
     * the number of clusters created.
     * @param metric The damping factor (lambda). Must be between 0.0 and 1.0.
     * @param creator The cluster creator.
     */
    public DBSCANClusterer(
        double eps,
        int minSamples,
        Semimetric<? super DataType> metric,
        ClusterCreator<ClusterType, DataType> creator)
    {
        super(DEFAULT_MAX_ITERATIONS);

        this.setNeighborhoodRadius(eps);
        this.setMinSamples(minSamples);
        this.setMetric(metric);
        this.setCreator(creator);
    }

    @Override
    public DBSCANClusterer<DataType, ClusterType> clone()
    {
        @SuppressWarnings("unchecked")
        final DBSCANClusterer<DataType, ClusterType> result
            = (DBSCANClusterer<DataType, ClusterType>) super.clone();
        result.metric = ObjectUtil.cloneSmart(this.metric);
        result.clusters = null;

        return result;
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        if (this.getData() == null || this.getData().size() <= 0)
        {
            // Make sure that the data is valid.
            return false;
        }

        // Copy data into a data structure that can be indexed
        this.points = new ArrayList<DataType>(this.getData());

        if (this.metric instanceof Metric)
        {
            // Construct a new KDTree with the data points
            List<InputOutputPair<DataType, Double>> pts = this.points.stream()
                .map(pt -> new DefaultInputOutputPair<>(pt, 0.0))
                .collect(Collectors.toList());
            this.spatialIndex = new KDTree<>(pts);
        }

        // Initialize the main data for the algorithm
        this.clusters = new ArrayList<ClusterType>();
        this.clustered = new HashSet<DataType>();
        this.visited = new HashSet<DataType>();
        this.currentCluster = new ArrayList<DataType>();
        this.noiseCluster = new ArrayList<DataType>();

        // Noise cluster will be 0th cluster, start core clusters index at 1
        this.clusters.add(0, this.creator.createCluster(this.noiseCluster));
        this.clusterCount = 1;

        // Ready to learn.
        return true;
    }

    protected boolean step()
    {
        // Retrieve the point to process
        DataType point = this.points.get(this.pointIndex);
        if (!this.visited.contains(point))
        {
            // Mark point as visited
            this.visited.add(point);

            // Get all points in this point's neighborhood
            List<DataType> neighbors = this.regionQuery(point);
            if (neighbors.size() < this.minSamples)
            {
                // Assign this point to the noise cluster
                this.noiseCluster.add(point);
                this.clustered.add(point);
            }
            else
            {
                // Expand this cluster
                this.expandCluster(point, neighbors);

                // Add expanded cluster to set of clusters
                this.clusters.add(this.clusterCount, this.creator.createCluster(
                    this.currentCluster));

                // Create the next cluster and set it as the current cluster
                this.clusterCount++;
                this.currentCluster = new ArrayList<DataType>();
            }
        }

        // Move to the next point, complete if we have processed every point
        this.pointIndex++;
        return (this.pointIndex < this.points.size());
    }

    /**
     * Expands the current cluster to include the point given and all
     * neighboring points that are not clustered. Repeats this process for the
     * neighbors of neighboring points, etc.
     *
     * @param point The base point to expand the current cluster from.
     * @param neighborsLinkedList The list of neighbors of the base point.
     */
    private void expandCluster(
        DataType point,
        List<DataType> neighbors)
    {
        LinkedList<DataType> neighborsLinkedList = new LinkedList<>(neighbors);
        this.currentCluster.add(point);
        this.clustered.add(point);
        while (!neighborsLinkedList.isEmpty())
        {
            DataType p = neighborsLinkedList.remove();
            if (!this.visited.contains(p))
            {
                this.visited.add(p);
                List<DataType> neighbors2 = regionQuery(p);
                if (neighbors2.size() >= this.minSamples)
                {
                    neighborsLinkedList.addAll(neighbors2);
                }
            }
            if (!this.clustered.contains(p))
            {
                this.currentCluster.add(p);
                this.clustered.add(p);
            }
        }
    }

    /**
     * Gets all the points neighboring the given point. These will be points
     * that are at most eps (radius) away from the given point. Uses the spatial
     * index (KD tree) if the metric is a Metric and points are Vectorizable.
     * Otherwise, performs a brute-force search.
     *
     * @param point The point to get the neighborhood for.
     * @return
     */
    private List<DataType> regionQuery(DataType point)
    {
        List<DataType> result;
        if (this.metric instanceof Metric)
        {
            result = this.spatialIndex.findNearestWithinRadius(
                point, this.eps, (Metric<? super DataType>) this.metric)
                .stream()
                .map(pair -> pair.getFirst())
                .collect(Collectors.toList());
        }
        else
        {
            result = this.points.stream()
                .filter((p) -> (this.metric.evaluate(p, point) <= this.eps))
                .collect(Collectors.toList());
        }
        return result;
    }

    protected void cleanupAlgorithm()
    {
    }

    public ArrayList<ClusterType> getResult()
    {
        return this.getClusters();
    }

    /**
     * Gets the neighborhood radius.
     *
     * @return The eps.
     */
    public double getNeighborhoodRadius()
    {
        return this.eps;
    }

    /**
     * Sets the neighborhood radius.
     *
     * @param eps The eps.
     */
    public void setNeighborhoodRadius(double eps)
    {
        if (eps < 0.0 || eps > 1.0)
        {
            throw new IllegalArgumentException(
                "The eps must be between 0.0 and 1.0.");
        }
        this.eps = eps;
    }

    /**
     * Gets the minimum number of samples.
     *
     * @return The minSamples.
     */
    public double getMinSamples()
    {
        return this.minSamples;
    }

    /**
     * Sets the minimum number of samples.
     *
     * @param minSamples The minSamples.
     */
    public void setMinSamples(int minSamples)
    {
        this.minSamples = minSamples;
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
     * Sets the distance metric the clustering uses.
     *
     * @param metric The metric.
     */
    public void setMetric(Semimetric<? super DataType> metric)
    {
        this.metric = metric;
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
     * Sets the current clusters, which is a sparse mapping of exemplar
     * identifier to cluster object.
     *
     * @param clusters The current clusters.
     */
    protected void setClusters(
        final ArrayList<ClusterType> clusters)
    {
        this.clusters = clusters;
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
     * Gets the spatial index.
     *
     * @return The spatial index.
     */
    public KDTree<DataType, Double, InputOutputPair<DataType, Double>> getSpatialIndex()
    {
        return this.spatialIndex;
    }

    /**
     * Sets the spatial index.
     *
     * @param spatialIndex The spatial index (speeds up neighborhood queries).
     */
    public void setCreator(
        KDTree<DataType, Double, InputOutputPair<DataType, Double>> spatialIndex)
    {
        this.spatialIndex = spatialIndex;
    }

    /**
     * Gets the list of points.
     *
     * @return The points being clustered.
     */
    public ArrayList<DataType> getPoints()
    {
        return this.points;
    }

    /**
     * Sets the list of points.
     *
     * @param points The points to be clustered.
     */
    public void setPoints(
        ArrayList<DataType> points)
    {
        this.points = points;
    }

    /**
     * Gets the number of clusters.
     *
     * @return The number of clusters.
     */
    public int getClusterCount()
    {
        return this.clusterCount;
    }

    /**
     * Sets the number of clusters.
     *
     * @param count The number of clusters.
     */
    public void setClusterCount(
        int count)
    {
        this.clusterCount = count;
    }

    /**
     * Gets the point index.
     *
     * @return The point index.
     */
    public int getPointIndex()
    {
        return this.pointIndex;
    }

    /**
     * Sets the point index.
     *
     * @param index The point index.
     */
    public void setPointIndex(
        int index)
    {
        this.pointIndex = index;
    }

}

/*
 * File:                OptimizedKMeansClusterer.java
 * Authors:             Justin Basilico and Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright February 21, 2006, Sandia Corporation.  Under the terms of Contract
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
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.CentroidClusterDivergenceFunction;
import gov.sandia.cognition.learning.algorithm.clustering.initializer.FixedClusterInitializer;
import gov.sandia.cognition.math.Metric;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class implements an optimized version of the k-means algorithm that
 * makes use of the triangle inequality to compute the same answer as k-means
 * while using less distance calculations. The only restriction that the
 * algorithm places is that the divergence function it is given must be a
 * metric because it must obey the triangle inequality.
 *
 * @param   <DataType> The type of the data to cluster. This is typically 
 *          defined by the divergence function used.
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments={
        "Added PublicationReference",
        "Code generally looks fine."
    }
)
@PublicationReference(
    author="C. Elkan",
    title="Using the Triangle Inequality to Accelerate k-Means",
    type=PublicationType.Conference,
    year=2003,
    publication="Proceedings of the Twentieth International Conference on Machine Learning",
    pages={147,153},
    url="www-cse.ucsd.edu/~elkan/kmeansicml03.pdf"
)
public class OptimizedKMeansClusterer<DataType>
    extends KMeansClusterer<DataType, CentroidCluster<DataType>>
{

    /** The metric being used. */
    private Metric<? super DataType> metric;

    /** The lower bounds on the distances to the clusters. */
    protected double[][] lowerBounds;

    /** The upper bounds on the distance to the current assigned cluster. */
    protected double[] upperBounds;

    /** The distances between clusters. */
    protected double[][] clusterDistances;

    /**
     * Creates a new instance of OptimizedKMeansClusterer.
     *
     * @param numClusters The number of clusters to create.
     * @param maxIterations Number of iterations before stopping
     * @param initializer The cluster initializer.
     * @param metric The metric to use.
     * @param creator The cluster creator to use.
     */
    public OptimizedKMeansClusterer(
        int numClusters,
        int maxIterations,
        FixedClusterInitializer<CentroidCluster<DataType>, DataType> initializer,
        Metric<? super DataType> metric,
        ClusterCreator<CentroidCluster<DataType>, DataType> creator)
    {
        super(numClusters, maxIterations, initializer,
            new CentroidClusterDivergenceFunction<DataType>(metric), creator);

        this.setMetric(metric);

        this.setLowerBounds(null);
        this.setUpperBounds(null);
        this.setClusterDistances(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public OptimizedKMeansClusterer<DataType> clone()
    {
        final OptimizedKMeansClusterer<DataType> result = 
            (OptimizedKMeansClusterer<DataType>) super.clone();
        
        result.metric = (Metric<? super DataType>) result.divergenceFunction;
        
        result.lowerBounds = null;
        result.upperBounds = null;
        result.clusterDistances = null;
        
        return result;
    }

    @Override
    protected boolean initializeAlgorithm()
    {
        boolean superReturn = super.initializeAlgorithm();

        // Initialize the bounds.
        int N = this.getNumElements();
        int k = this.getNumClusters();
        this.setLowerBounds(new double[N][k]);
        this.setUpperBounds(new double[N]);
        this.setClusterDistances(new double[k][k]);

        return superReturn;
    }

    /**
     * Computes the distances between the clusters.
     */
    protected void computeClusterDistances()
    {
        // Go through all the clusters and compute their distances.
        for (int i = 0; i < this.getNumClusters(); i++)
        {
            // Get the i-th cluster.
            DataType clusterI = this.getClusterCentroid(i);

            // We only compute the distance once since it must be symmetric.
            // We hit the diagonal first so we do not assign to it twice.
            clusterDistances[i][i] =
                this.metric.evaluate(clusterI, clusterI);

            // Now do the off-diagonal component of the distances.
            for (int j = i + 1; j < this.getNumClusters(); j++)
            {
                // Get the j-th cluster.
                DataType clusterJ = this.getClusterCentroid(j);

                // Compute the distance between the two clusters.
                double distance = this.metric.evaluate(clusterI, clusterJ);

                // Save the cluster distance.
                clusterDistances[i][j] = distance;
                clusterDistances[j][i] = distance;
            }
        }
    }

    @Override
    protected boolean step()
    {
        // Note: The comments in this function refer to the paper
        // C. Elkan. "Using the Triangle Inequality to Accelerate k-Means". In 
        // Proceedings of the Twentieth International Conference on Machine 
        // Learning, 2003, pp. 147-153.
        // Please use that paper as a guide if you need to follow the code
        // in more detail.

        // Keep track of the number of assignments that changed.
        this.setNumChanged(0);

        if (this.getNumClusters() <= 0)
        {
            // No clusters.
            return false;
        }

        // Step 1: Computer distances between all centers.
        computeClusterDistances();

        // Compute the s values, which are based on the minimum distance to
        // between clusters.
        double[] s = new double[this.getNumClusters()];
        for (int i = 0; i < this.getNumClusters(); i++)
        {
            double minDistance = Double.MAX_VALUE;
            for (int j = 0; j < this.getNumClusters(); j++)
            {
                double distance = clusterDistances[i][j];
                if (i != j && distance < minDistance)
                {
                    minDistance = distance;
                }
            }

            s[i] = 0.5 * minDistance;
        }

        // Step 2 & 3: Identify points... and Compute...
        // This is the big loop that does all the real work by looping over
        // all the points.
        Iterator<? extends DataType> iterator = this.data.iterator();
        for (int i = 0; i < this.getNumElements(); i++)
        {
            // Evaluate the i-th point.
            DataType element = iterator.next();
            int assignment = this.assignments[i];

            if (assignment < 0)
            {
                // Assignments not initialized.
                double minDistance = Double.MAX_VALUE;
                for (int j = 0; j < this.getNumClusters(); j++)
                {
// TO DO: Use Lemma 1 to avoid redundant distance calculations.
                    double distance = this.metric.evaluate(element,
                        this.getClusterCentroid(j));

                    this.lowerBounds[i][j] = distance;

                    if (assignment < 0 || distance < minDistance)
                    {
                        assignment = j;
                        minDistance = distance;
                    }
                }

                this.setAssignment(i, assignment);
                this.upperBounds[i] = minDistance;
                this.setNumChanged(this.getNumChanged() + 1);
                continue;
            }

            // See if we need to update this element at all.
            if (this.upperBounds[i] <= s[assignment])
            {
                // Step 2: u(x) <= s(c(x))
                continue;
            }

            // We may need to update the cluster so keep this information.
            int oldAssignment = assignment;
            double distanceToCluster = 0.0;
            boolean distanceToClusterComputed = false;
            for (int j = 0; j < this.getNumClusters(); j++)
            {
                if (j == this.assignments[i])
                {
                    // Condition (i): c != c(x)
                    continue;
                }
                else if (this.upperBounds[i] <= this.lowerBounds[i][j])
                {
                    // Condition (ii): u(x) > l(x, c)
                    continue;
                }
                else if (this.upperBounds[i] 
                    <= 0.5 * this.clusterDistances[assignment][j])
                {
                    // Condition (iii): u(x) > 0.5 * d(c(x), c))
                    continue;
                }

                // See if we need to recompute the distance to the current
                // assigned cluster.
                if (!distanceToClusterComputed)
                {
                    distanceToCluster = this.metric.evaluate(element,
                        this.getClusterCentroid(assignment));
                    this.lowerBounds[i][assignment] = distanceToCluster;
                    distanceToClusterComputed = true;
                }

                // See if we need to compute the distance to the j-th cluster.
                if (distanceToCluster > this.lowerBounds[i][j] 
                     || distanceToCluster > 0.5 * this.clusterDistances[assignment][j])
                {
                    double distance = this.metric.evaluate(element,
                        this.getClusterCentroid(j));
                    this.lowerBounds[i][j] = distance;

                    if (distance < distanceToCluster)
                    {
                        distanceToCluster = distance;
                        assignment = j;
                        this.setAssignment(i, j);
                        this.upperBounds[i] = distance;
                    }
                }
            }

            // If ew changed the assignment keep track of that in the counter.
            if (oldAssignment != assignment)
            {
                this.setNumChanged(this.getNumChanged() + 1);
            }
        }

        // Step 4: For each center, find the means.
        ArrayList<DataType> oldCentroids =
            new ArrayList<DataType>(this.getNumClusters());
        for (int i = 0; i < this.getNumClusters(); i++)
        {
            oldCentroids.add(this.getClusterCentroid(i));
        }
        this.createClustersFromAssignments();

        // Step 5: Evaluate the amount each cluster has changed and update
        //         the lower bounds.
        double[] clusterDeltas = new double[this.getNumClusters()];
        for (int j = 0; j < this.getNumClusters(); j++)
        {
            // Get the old and new centroids for this cluster.
            DataType oldCentroid = oldCentroids.get(j);
            DataType newCentroid = this.getClusterCentroid(j);

            // Compute the change in the centroid.
            double meanChange = this.metric.evaluate(oldCentroid, newCentroid);
            clusterDeltas[j] = meanChange;

            // Update the lower bounds for each element.
            for (int i = 0; i < this.getNumElements(); i++)
            {
                this.lowerBounds[i][j] =
                    Math.max(0.0, this.lowerBounds[i][j] - meanChange);
            }
        }

        // Step 6: Update the upper bounds on the distance.
        for (int i = 0; i < this.getNumElements(); i++)
        {
            this.upperBounds[i] += clusterDeltas[this.assignments[i]];
        }

        return (this.getNumChanged() > 0);

    }

    /**
     * Gets the centroid for the given cluster index.
     *
     * @param clusterIndex The index of the cluster to get the centroid for.
     * @return The centroid for the given cluster.
     */
    public DataType getClusterCentroid(
        int clusterIndex)
    {
        // Attempt to get the cluster.
        CentroidCluster<DataType> cluster = this.clusters.get(clusterIndex);

        if (cluster == null)
        {
            // Error: The cluster was null so the centroid must also be null.
            return null;
        }
        else
        {
            // Return the centroid.
            return cluster.getCentroid();
        }
    }

    /**
     * Gets the metric being used by the algorithm.
     *
     * @return The metric being used.
     */
    public Metric<? super DataType> getMetric()
    {
        return this.metric;
    }

    /**
     * Sets the metric to use in cluster.
     *
     * @param metric The metric being used.
     */
    private void setMetric(
        Metric<? super DataType> metric)
    {
        if (metric == null)
        {
            // Error: Bad metric.
            throw new NullPointerException("The metric cannot be null.");
        }

        this.metric = metric;
    }

    /**
     * Sets the lower bounds of the distances to the cluster.
     *
     * @param  lowerBounds The new lower bounds.
     */
    private void setLowerBounds(
        double[][] lowerBounds)
    {
        this.lowerBounds = lowerBounds;
    }

    /**
     * Sets the upper bounds of the distances to the cluster.
     *
     * @param  upperBounds The new upper bounds.
     */
    private void setUpperBounds(
        double[] upperBounds)
    {
        this.upperBounds = upperBounds;
    }

    /**
     * Sets the distances between clusters
     *
     * @param  clusterDistances The new distances between clusters.
     */
    private void setClusterDistances(
        double[][] clusterDistances)
    {
        this.clusterDistances = clusterDistances;
    }

}

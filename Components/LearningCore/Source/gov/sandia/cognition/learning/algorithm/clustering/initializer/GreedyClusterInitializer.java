/*
 * File:                FixedClusterInitializer.java
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

package gov.sandia.cognition.learning.algorithm.clustering.initializer;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.util.ArgumentChecker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Implements a FixedClusterInitializer that greedily attempts to create the
 * initial clusters. The algorithm works by first selecting a random member to
 * be the first cluster and then successively finding the member that is
 * furthest from the selected clusters. This method of initialization can work
 * well with the K-means algorithm.
 *
 * @param   <ClusterType>
 *      Type of {@code Cluster<DataType>} used in theaceous {@code learn()}
 *      method.
 * @param   <DataType>
 *      The algorithm operates on a {@code Collection<DataType>}, so
 *      {@code DataType} will be something like Vector or String.
 * @author  Justin Basilico
 * @author  Kevin R. Dixon
 * @since   1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Now extends AbstractRandomized",
        "Cleaned up javadoc a little bit with code annotations.",
        "Otherwise, looks fine."
    }
)
public class GreedyClusterInitializer<ClusterType extends Cluster<DataType>, DataType>
    extends AbstractMinDistanceFixedClusterInitializer<ClusterType, DataType>
{

    /**
     * Creates a new, empty instance of {@code GreedyClusterInitializer}.
     */
    public GreedyClusterInitializer()
    {
        this(null, null, new Random());
    }

    /**
     * Creates a new instance of {@code GreedyClusterInitializer}.
     *
     * @param   divergenceFunction
     *      The divergence function to use.
     * @param   creator
     *      The cluster creator to use.
     * @param   random
     *      The random number generator to use.
     */
    public GreedyClusterInitializer(
        final DivergenceFunction<? super DataType, ? super DataType> divergenceFunction,
        final ClusterCreator<ClusterType, DataType> creator,
        final Random random)
    {
        super(divergenceFunction, creator, random);
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public GreedyClusterInitializer<ClusterType, DataType> clone()
    {
        return (GreedyClusterInitializer<ClusterType, DataType>) super.clone();
    }

    /**
     * Initializes a given number of clusters from the given elements using the
     * greedy initialization algorithm.
     *
     * @param   numClusters
     *      The number of clusters to create.
     * @param   elements
     *      The elements to create the clusters from.
     * @return
     *      The initial clusters to use.
     * @throws  IllegalArgumentException
     *      If numClusters is less than 0.
     * @throws  NullPointerException
     *      If elements is null.
     */
    public ArrayList<ClusterType> blah(
        int numClusters,
        final Collection<? extends DataType> elements)
    {
        ArgumentChecker.assertIsNonNegative("numClusters", numClusters);
        if (numClusters == 0 || elements.size() == 0)
        {
            // No clusters to create.
            return new ArrayList<ClusterType>();
        }

        // Create an array list of the elements.
        final int numElements = elements.size();
        final ArrayList<DataType> elementsList =
            new ArrayList<DataType>(elements);

        if (numClusters > numElements)
        {
            // Too many clusters given. Use a smaller number.
            numClusters = numElements;
        }

        // Initialize the cluster objects.
        final ArrayList<DataType> clusterList =
            new ArrayList<DataType>(numClusters);

        // Pick the first cluster randomly.
        final int firstIndex = this.random.nextInt(numElements);
        final DataType firstCluster = elementsList.get(firstIndex);
        clusterList.add(firstCluster);

        // Create an array of whether or not a point has been selected
        // along with an array of the minimum distance to a cluster center.
        final boolean[] selected = new boolean[numElements];
        final double[] minDistances = new double[numElements];
        for (int i = 0; i < numElements; i++)
        {
            if (i == firstIndex)
            {
                // This is the first index we selected so we set its
                // selected to true.
                selected[i] = true;
                minDistances[i] = 0.0;
            }
            else
            {
                // This point was not the first one so it was not yet 
                // selected.
                selected[i] = false;

                // Compute the distance to the first selected cluster.
                minDistances[i] = this.divergenceFunction.evaluate(
                    elementsList.get(i), firstCluster);
            }
        }

        // Select the rest of the clusters.
        for (int clusterNum = 1; clusterNum < numClusters; clusterNum++)
        {
            // We want to find the maximum minimum distance.
            double maxMinDistance = Double.MAX_VALUE;
            int maxIndex = 0;

            for (int i = 0; i < numElements; i++)
            {
                if (selected[i])
                {
                    // Skip clusters we've already selected.
                    continue;
                }

                // Get the current minimum distance for the element.
                double minDistance = minDistances[i];

                if (maxIndex == 0 || minDistance > maxMinDistance)
                {
                    // This is the minium seen so far, so keep track of 
                    // it.
                    maxIndex = i;
                    maxMinDistance = minDistance;
                }
            }

            if (maxMinDistance <= 0.0)
            {
                // There are not enough points to prevent selecting two
                // that are on top of each other.
                break;
            }

            // We have selected the point with the maximum minimum 
            // distance, choose it as a cluster.
            selected[maxIndex] = true;
            minDistances[maxIndex] = 0.0;
            final DataType cluster = elementsList.get(maxIndex);
            clusterList.add(cluster);

            // Go through all the remaining elements and update their
            // minium distances.
            for (int i = 0; i < numElements; i++)
            {
                if (!selected[i])
                {
                    minDistances[i] = Math.min(minDistances[i],
                        this.divergenceFunction.evaluate(
                            cluster, elementsList.get(i)));
                }
            }
        }

        // Create the actual clusters.
        numClusters = clusterList.size();
        final ArrayList<ClusterType> clusters =
            new ArrayList<ClusterType>(numClusters);

        for (int i = 0; i < numClusters; i++)
        {
            // This is an array list of length 1 that is used to initialize 
            // the cluster.
            final ArrayList<DataType> singletonCluster =
                new ArrayList<DataType>(1);
            singletonCluster.add(clusterList.get(i));

            // Create the cluster.
            ClusterType cluster = this.creator.createCluster(singletonCluster);

            // Add the cluster to the list.
            clusters.add(cluster);
        }

        return clusters;
    }

    @Override
    protected int selectNextClusterIndex(
        final double[] minDistances,
        final boolean[] selected)
    {
        // We want to find the maximum minimum distance.
        double maxMinDistance = Double.MAX_VALUE;
        int maxIndex = 0;

        final int elementCount = minDistances.length;
        for (int i = 0; i < elementCount; i++)
        {
            if (selected[i])
            {
                // Skip clusters we've already selected.
                continue;
            }

            // Get the current minimum distance for the element.
            double minDistance = minDistances[i];

            if (maxIndex == 0 || minDistance > maxMinDistance)
            {
                // This is the minium seen so far, so keep track of
                // it.
                maxIndex = i;
                maxMinDistance = minDistance;
            }
        }

        if (maxMinDistance <= 0.0)
        {
            // There are not enough points to prevent selecting two
            // that are on top of each other.
            return -1;
        }
        else
        {
            return maxIndex;
        }
    }

}

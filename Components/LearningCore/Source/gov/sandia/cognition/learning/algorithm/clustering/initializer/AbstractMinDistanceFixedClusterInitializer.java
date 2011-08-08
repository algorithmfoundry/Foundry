/*
 * File:                AbstractMinDistanceFixedClusterInitializer.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry Learning Core
 * 
 * Copyright February 21, 2011, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive 
 * license for use of this work by or on behalf of the U.S. Government. Export 
 * of this program may require a license from the United States Government. 
 */

package gov.sandia.cognition.learning.algorithm.clustering.initializer;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.learning.function.distance.DefaultDivergenceFunctionContainer;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.util.ArgumentChecker;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Implements an abstract FixedClusterInitializer that works by using the
 * minimum distance from a point to the cluster.
 *
 * @param   <ClusterType>
 *      Type of {@code Cluster<DataType>} used in theaceous {@code learn()}
 *      method.
 * @param   <DataType>
 *      The algorithm operates on a {@code Collection<DataType>}, so
 *      {@code DataType} will be something like Vector or String.
 * 
 * @author  Justin Basilico
 * @since   3.1
 */
public abstract class AbstractMinDistanceFixedClusterInitializer<ClusterType extends Cluster<DataType>, DataType>
    extends DefaultDivergenceFunctionContainer<DataType, DataType>
    implements FixedClusterInitializer<ClusterType, DataType>, Randomized
{

    /** The ClusterCreator to create the initial clusters from. */
    protected ClusterCreator<ClusterType, DataType> creator;

    /** The random number generator to use. */
    protected Random random;

    /**
     * Creates a new, empty instance of {@code AbstractMinDistanceFixedClusterInitializer}.
     */
    public AbstractMinDistanceFixedClusterInitializer()
    {
        this(null, null, new Random());
    }

    /**
     * Creates a new instance of {@code AbstractMinDistanceFixedClusterInitializer}.
     *
     * @param   divergenceFunction
     *      The divergence function to use.
     * @param   creator
     *      The cluster creator to use.
     * @param   random
     *      The random number generator to use.
     */
    public AbstractMinDistanceFixedClusterInitializer(
        final DivergenceFunction<? super DataType, ? super DataType> divergenceFunction,
        final ClusterCreator<ClusterType, DataType> creator,
        final Random random)
    {
        super(divergenceFunction);

        this.setCreator(creator);
        this.setRandom(random);
    }

    @Override
    public AbstractMinDistanceFixedClusterInitializer<ClusterType, DataType> clone()
    {
        @SuppressWarnings("unchecked")
        final AbstractMinDistanceFixedClusterInitializer<ClusterType, DataType> clone =
            (AbstractMinDistanceFixedClusterInitializer<ClusterType, DataType>) super.clone();
        clone.creator = ObjectUtil.cloneSafe(this.creator);
        return clone;
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
    public ArrayList<ClusterType> initializeClusters(
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
            // Select the next index.
            int selectedIndex = this.selectNextClusterIndex(
                minDistances, selected);
            if (selectedIndex < 0)
            {
                // Nothing was selected so stop making clusters. May happen if
                // all the remaining data points are on top of cluster centers.
                break;
            }

            // We have selected the point with the maximum minimum
            // distance, choose it as a cluster.
            selected[selectedIndex] = true;
            minDistances[selectedIndex] = 0.0;
            final DataType cluster = elementsList.get(selectedIndex);
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

    /**
     * Select the index for the next cluster based on the given minimum
     * distances and array indicating which clusters have already been selected.
     *
     * @param   minDistances
     *      The array of minimum distances.
     * @param   selected
     *      The array corresponding to whether or not an item has already
     *      been selected.
     * @return
     *      The index of the next cluster to include. -1 means that there is
     *      nothing left to include.
     */
    protected abstract int selectNextClusterIndex(
        final double[] minDistances,
        final boolean[] selected);

    /**
     * Gets the cluster creator used to create the initial clusters.
     *
     * @return The cluster creator.
     */
    public ClusterCreator<ClusterType, DataType> getCreator()
    {
        return this.creator;
    }

    /**
     * Sets the cluster creator used to create the initial clusters.
     *
     * @param   creator
     *      The new cluster creator.
     */
    public void setCreator(
        ClusterCreator<ClusterType, DataType> creator)
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
    public void setRandom(
        final Random random)
    {
        this.random = random;
    }

}

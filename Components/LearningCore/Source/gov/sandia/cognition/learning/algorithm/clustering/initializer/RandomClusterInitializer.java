/*
 * File:                RandomClusterInitializer.java
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

package gov.sandia.cognition.learning.algorithm.clustering.initializer;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.statistics.DiscreteSamplingUtil;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import gov.sandia.cognition.util.ObjectUtil;
import gov.sandia.cognition.util.Randomized;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Creates initial clusters by selecting random data points as singleton
 * clusters.
 *
 * @param <ClusterType> Type of {@link Cluster<DataType>} used in this
 * {@code learn()} method.
 * @param <DataType> The algorithm operates on a {@link Collection<DataType>},
 * so {@code DataType} will be something like Vector or String.
 * @author Jeff Piersol
 * @since 4.0.0
 */
public class RandomClusterInitializer<ClusterType extends Cluster<DataType>, DataType>
    extends AbstractCloneableSerializable
    implements FixedClusterInitializer<ClusterType, DataType>, Randomized
{

    protected ClusterCreator<ClusterType, DataType> creator;

    protected Random random;

    /**
     * Creates a new random cluster creator.
     *
     * @param creator the cluster creator to use
     * @param random the random number generator to use
     */
    public RandomClusterInitializer(
        final ClusterCreator<ClusterType, DataType> creator,
        final Random random)
    {
        this.creator = creator;
        this.random = random;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RandomClusterInitializer<ClusterType, DataType> clone()
    {
        RandomClusterInitializer<ClusterType, DataType> clone
            = (RandomClusterInitializer<ClusterType, DataType>) super.clone();
        clone.creator = ObjectUtil.cloneSmart(this.creator);
        return clone;
    }

    @Override
    public ArrayList<ClusterType> initializeClusters(int numClusters,
        Collection<? extends DataType> elements)
    {
        List<? extends DataType> elementsList = elements instanceof List
            ? (List<? extends DataType>) elements
            : new ArrayList<>(elements);
        ArrayList<DataType> representativePoints
            = DiscreteSamplingUtil.sampleWithReplacement(random, elementsList,
                numClusters);
        ArrayList<ClusterType> clusters = representativePoints.stream()
            .map(point -> creator.createCluster(Arrays.asList(point)))
            .collect(Collectors.toCollection(ArrayList::new));
        return clusters;
    }

    @Override
    public Random getRandom()
    {
        return random;
    }

    @Override
    public void setRandom(Random random)
    {
        this.random = random;
    }

}

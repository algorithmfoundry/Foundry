/*
 * File:                MinDistanceSamplingClusterInitializer.java
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

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.ClusterCreator;
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.statistics.DiscreteSamplingUtil;
import java.util.Random;

/**
 * Implements {@code FixedClusterInitializer} that initializes clusters by
 * first selecting a random point for the first cluster and then randomly
 * sampling each successive cluster based on the squared minimum distance from
 * the point to the existing selected clusters. This is also known as the
 * K-means++ initialization algorithm.
 *
 * @param   <ClusterType>
 *      Type of {@code Cluster<DataType>} used in theaceous {@code learn()}
 *      method.
 * @param   <DataType>
 *      The algorithm operates on a {@code Collection<DataType>}, so
 *      {@code DataType} will be something like Vector or String.
 * @author  Justin Basilico
 * @since   3.1
 */
@PublicationReference(
    author={"David Arthur", "Sergei Vassilvitskii"},
    title="k-means++: the advantages of careful seeding",
    year=2007,
    type=PublicationType.Conference,
    publication="Proceedings of the eighteenth annual ACM-SIAM Symposium on Discrete algorithms (SODA)",
    url="http://portal.acm.org/citation.cfm?id=1283383.1283494")
public class DistanceSamplingClusterInitializer<ClusterType extends Cluster<DataType>, DataType>
    extends AbstractMinDistanceFixedClusterInitializer<ClusterType, DataType>
{

    /**
     * Creates a new, empty instance of {@code MinDistanceSamplingClusterInitializer}.
     */
    public DistanceSamplingClusterInitializer()
    {
        this(null, null, new Random());
    }

    /**
     * Creates a new instance of {@code MinDistanceSamplingClusterInitializer}.
     *
     * @param   divergenceFunction
     *      The divergence function to use.
     * @param   creator
     *      The cluster creator to use.
     * @param   random
     *      The random number generator to use.
     */
    public DistanceSamplingClusterInitializer(
        final DivergenceFunction<? super DataType, ? super DataType> divergenceFunction,
        final ClusterCreator<ClusterType, DataType> creator,
        final Random random)
    {
        super(divergenceFunction, creator, random);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DistanceSamplingClusterInitializer<ClusterType, DataType> clone()
    {
        return (DistanceSamplingClusterInitializer<ClusterType, DataType>)
            super.clone();
    }

    @Override
    protected int selectNextClusterIndex(
        final double[] minDistances,
        final boolean[] selected)
    {
        final int elementCount = minDistances.length;

        // Build up the cumulative distribution of weights from which we will
        // sample. We build up the cumulative distribution to make it easy
        // to sample from by using a binary search.
        final double[] cumulativeDistribution = new double[elementCount];
        double sum = 0.0;
        for (int i = 0; i < elementCount; i++)
        {
            // The weight is based on the minmum distance. Note that clusters
            // that have already been selected will have a minimum distance of
            // zero, which means it will have no weight.
            final double minDistance = minDistances[i];
            final double weight = minDistance * minDistance;
            sum += weight;
            cumulativeDistribution[i] = sum;
        }

        if (sum <= 0.0)
        {
            // All of the data points are distance 0.0 from a cluster center,
            // Cannot sample anything.
            return -1;
        }
        else
        {
            // Randomly select an index from cumulative proportions.
            return DiscreteSamplingUtil.sampleIndexFromCumulativeProportions(
                this.getRandom(), cumulativeDistribution);
        }
    }


}

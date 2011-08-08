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

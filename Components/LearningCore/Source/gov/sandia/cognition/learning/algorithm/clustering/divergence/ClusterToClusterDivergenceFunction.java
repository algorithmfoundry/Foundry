/*
 * File:                ClusterToClusterDivergenceFunction.java
 * Authors:             Justin Basilico
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 28, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.divergence;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.math.DivergenceFunction;

/**
 * The ClusterToClusterDivergenceFunction defines a DivergenceFunction between
 * two clusters of the same data type. This represents the divergence between
 * the two clusters. It is useful in conjunction with
 * {@code AgglomerativeClustering}.
 *
 * @param <ClusterType> type of {@code Cluster<DataType>} used in the
 * {@code learn()} method
 * @param <DataType> The algorithm operates on a {@code Collection<DataType>},
 * so {@code DataType} will be something like Vector or String
 * @author Justin Basilico
 * @since  1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments={
        "Cleaned up javadoc a little bit with code annotations.",
        "Otherwise, looks fine."
    }
)
public interface ClusterToClusterDivergenceFunction
    <ClusterType extends Cluster<DataType>, DataType>
    extends DivergenceFunction<ClusterType, ClusterType>
{
}

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
import gov.sandia.cognition.util.CloneableSerializable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

/**
 * The FixedClusterInitializer interface defines the functionality of a class
 * that can initialize a given number of clusters from a set of elements.
 * Such an initializer is used in some clustering algorithms to provide the
 * starting clusters for the algorithm.
 *
 * @param <ClusterType> Type of {@code Cluster<DataType>} used in the
 * {@code learn()} method.
 * @param <DataType> The algorithm operates on a {@code Collection<DataType>},
 * so {@code DataType} will be something like Vector or String
 * @author Justin Basilico
 * @author Kevin R. Dixon
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
public interface FixedClusterInitializer<ClusterType extends Cluster<DataType>, DataType>
{

    /**
     * Initializes a given number of clusters from the given elements.
     *
     * @param numClusters The number of clusters to create.
     * @param elements The elements to create the clusters from.
     * @return The initial clusters to use.
     * @throws IllegalArgumentException If numClusters is less than 0.
     * @throws NullPointerException If elements is null.
     */
    public ArrayList<ClusterType> initializeClusters(
        int numClusters,
        Collection<? extends DataType> elements);

}

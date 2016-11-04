/*
 * File:                WithinClusterDivergenceWrapper.java
 * Authors:             Andrew Fisher
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright July 12, 2016, Sandia Corporation. Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 */

package gov.sandia.cognition.learning.algorithm.clustering.divergence;

import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.util.CloneableSerializable;
import java.io.Serializable;

/**
 * Defines a function that computes the divergence of the elements in a cluster.
 *
 * @param <ClusterType> type of {@code Cluster<DataType>} used in the
 * {@code evalute()} method.
 * @param <DataType> type of data in the cluster.
 *
 * @author Andrew N. Fisher &lt;anfishe@sandia.gov&gt;
 */
public interface WithinClusterDivergence<ClusterType extends Cluster<DataType>, DataType>
    extends Cloneable, CloneableSerializable, Serializable
{

    public double evaluate(ClusterType cluster);

}

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

import gov.sandia.cognition.math.matrix.Vector;
import gov.sandia.cognition.math.matrix.Vectorizable;
import gov.sandia.cognition.util.CloneableSerializable;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.NormalizedCentroidCluster;

/**
 *
 * This class calculates the total cosine divergence between all members of a
 * cluster and the cluster's centroid
 *
 * @author Andrew N. Fisher &lt;anfishe@sandia.gov&gt;
 * @param <V> The type of data in the clusters.
 */
public class WithinNormalizedCentroidClusterCosineDivergence<V extends Vectorizable>
    implements
    WithinClusterDivergence<NormalizedCentroidCluster<V>, V>
{

    /**
     * Evaluate the this function on the provided cluster.
     *
     * @param cluster The cluster to calculate the function on.
     * @return The result of applying this function to the cluster.
     */
    public double evaluate(NormalizedCentroidCluster<V> cluster)
    {
        double total = 1.0;

        Vector centroid = cluster.getCentroid().convertToVector();
        Vector normalizedCentroid
            = cluster.getNormalizedCentroid().convertToVector();

        //if centroid is 0.0, cosine measure returns 0.0
        if (centroid.norm2() != 0.0)
        {
            total -= centroid.dotProduct(normalizedCentroid) / centroid.norm2();
        }

        total *= cluster.getMembers().size();

        return total;
    }

    @Override
    public CloneableSerializable clone()
    {
        return new WithinNormalizedCentroidClusterCosineDivergence<>();
    }

}

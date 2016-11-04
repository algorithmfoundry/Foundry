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
import gov.sandia.cognition.math.DivergenceFunction;
import gov.sandia.cognition.util.CloneableSerializable;

/**
 * Accumulates the results of a {@link ClusterDivergenceFunction} by summing the
 * divergence of each point to its cluster.
 *
 * @author Andrew N. Fisher &lt;anfishe@sandia.gov&gt;
 * @param <ClusterType>
 * @param <DataType>
 */
public class WithinClusterDivergenceWrapper<ClusterType extends Cluster<DataType>, DataType extends Object>
    implements WithinClusterDivergence<ClusterType, DataType>
{

    protected DivergenceFunction<? super ClusterType, ? super DataType> divergenceFunction;

    public WithinClusterDivergenceWrapper(
        DivergenceFunction<? super ClusterType, ? super DataType> divergenceFunction)
    {
        this.divergenceFunction = divergenceFunction;
    }

    @Override
    public double evaluate(ClusterType cluster)
    {
        double total = 0.0;
        for (DataType member : cluster.getMembers())
        {
            total += this.divergenceFunction.evaluate(cluster, member);
        }
        return total;
    }

    @Override
    public CloneableSerializable clone()
    {
        return new WithinClusterDivergenceWrapper<>(this.divergenceFunction);
    }

}

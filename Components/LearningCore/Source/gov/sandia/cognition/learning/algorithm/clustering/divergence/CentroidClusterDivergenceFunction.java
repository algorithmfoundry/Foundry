/*
 * File:                CentroidClusterDivergenceFunction.java
 * Authors:             Justin Basilico and Kevin R. Dixon.
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright March 1, 2006, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.divergence;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.CentroidCluster;
import gov.sandia.cognition.learning.function.distance.DefaultDivergenceFunctionContainer;
import gov.sandia.cognition.math.DivergenceFunction;

/**
 * The CentroidClusterDivergenceFunction class implements a divergence function
 * between a cluster and an object by computing the divergence between the
 * center of the cluster and the object.
 *
 * @param <DataType> The algorithm operates on a {@code Collection<DataType>},
 * so {@code DataType} will be something like Vector or String
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since 1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments="Interface looks fine."
)
public class CentroidClusterDivergenceFunction<DataType>
    extends DefaultDivergenceFunctionContainer<DataType,DataType>
    implements ClusterDivergenceFunction<CentroidCluster<DataType>, DataType>
{
    
    /**
     * Creates a new instance of CentroidClusterDivergenceFunction.
     *
     * @param divergenceFunction
     * The divergenceFunction to use to compute distances.
     */
    public CentroidClusterDivergenceFunction(
        final DivergenceFunction<? super DataType, ? super DataType>
            divergenceFunction )
    {
        super( divergenceFunction );
    }
    
    @Override
    public CentroidClusterDivergenceFunction<DataType> clone()
    {
        @SuppressWarnings("unchecked")
        CentroidClusterDivergenceFunction<DataType> clone =
            (CentroidClusterDivergenceFunction<DataType>) super.clone();
        return clone;
    }

    /**
     * Evaluates the divergence between the cluster centroid and the given
     * object. 
     *
     * @param cluster The cluster to compute distance from.
     * @param other The object to compute the distance to.
     * @return The divergence between the center of the cluster and the given
     *         object.
     */
    public double evaluate(
        final CentroidCluster<DataType> cluster, 
        final DataType other)
    {
        return this.divergenceFunction.evaluate(cluster.getCentroid(), other);
    }
    
}

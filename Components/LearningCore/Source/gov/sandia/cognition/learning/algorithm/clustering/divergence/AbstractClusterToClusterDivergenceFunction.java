/*
 * File:                AbstractClusterToClusterDivergenceFunction.java
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
import gov.sandia.cognition.learning.function.distance.DefaultDivergenceFunctionContainer;
import gov.sandia.cognition.math.DivergenceFunction;

/**
 * The AbstractClusterToClusterDivergenceFunction class is an abstract class
 * that helps out implementations of ClusterToClusterDivergenceFunction
 * implementations by holding a DivergenceFunction between elements of a
 * cluster.
 *
 * @param <ClusterType> type of {@code Cluster<DataType>} used in the
 * {@code learn()} method
 * @param <DataType> The algorithm operates on a {@code Collection<DataType>},
 * so {@code DataType} will be something like Vector or String
 * @author Justin Basilico
 * @since 1.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-23",
    changesNeeded=false,
    comments="Looks fine."
)
public abstract class AbstractClusterToClusterDivergenceFunction
        <ClusterType extends Cluster<DataType>, DataType>
    extends DefaultDivergenceFunctionContainer<DataType,DataType>
    implements ClusterToClusterDivergenceFunction<ClusterType, DataType>
{
    
    /**
     * Creates a new instance of AbstractClusterToClusterDivergenceFunction
     *
     * @param  divergenceFunction The divergence function to use between 
     *         elements.
     */
    public AbstractClusterToClusterDivergenceFunction(
        final DivergenceFunction<? super DataType, ? super DataType> 
            divergenceFunction)
    {
        super( divergenceFunction );
    }
    
}

/*
 * File:                MedoidClusterCreator.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 *
 * Copyright June 21, 2007, Sandia Corporation.  Under the terms of Contract
 * DE-AC04-94AL85000, there is a non-exclusive license for use of this work by
 * or on behalf of the U.S. Government. Export of this program may require a
 * license from the United States Government. See CopyrightHistory.txt for
 * complete details.
 *
 */

package gov.sandia.cognition.learning.algorithm.clustering.cluster;

import gov.sandia.cognition.annotation.CodeReview;
import gov.sandia.cognition.learning.function.distance.DefaultDivergenceFunctionContainer;
import gov.sandia.cognition.math.DivergenceFunction;
import java.util.Collection;

/**
 * The <code>MedoidClusterCreator</codE> class creates a 
 * <code>CentroidCluster</code> at the sample that minimizes the sum 
 * of the divergence to the objects assigned to the cluster. 
 *
 * @param <DataType> The algorithm operates on a Collection of DataType, so 
 * DataType will be something like Vector or String
 * @author Justin Basilico
 * @author Kevin R. Dixon
 * @since  2.0
 */
@CodeReview(
    reviewer="Kevin R. Dixon",
    date="2008-07-22",
    changesNeeded=false,
    comments="Code generally looks fine."
)
public class MedoidClusterCreator<DataType>
    extends DefaultDivergenceFunctionContainer<DataType,DataType>
    implements ClusterCreator<CentroidCluster<DataType>, DataType>
{
    
    /**
     * Creates a new instance of MedoidClusterCreator
     */
    public MedoidClusterCreator()
    {
        this(null);
    }
    
    /**
     * Creates a new instance of MedoidClusterCreator
     *
     * @param  divergenceFunction 
     *         Divergence function used to evaluate the dissimilarity between
     *         two data points
     */
    public MedoidClusterCreator(
        final DivergenceFunction<? super DataType, ? super DataType> 
            divergenceFunction)
    {
        super( divergenceFunction );
    }

    /**
     * Creates a CentroidCluster at the member that minimizes the sum of
     * divergence between all members
     *
     * @param  members 
     *         Data points that have been assigned to the cluster
     * @return 
     *         CentroidCluster that minimizes the sum of divergence between all
     *         assigned members
     */
    public CentroidCluster<DataType> createCluster(
        final Collection<DataType> members)
    {
        double minTotalDivergence = Double.POSITIVE_INFINITY;
        DataType medoid = null;
        
        // TODO: This code could be made faster by caching divergence function
        // values. Since divergence functions must be symmetric, there's no need
        // to compute both f(x,y) and f(y,x).  However, this could use a
        // large amount of memory and may not be worth it.
        for ( DataType candidate : members )
        {
            double totalDivergence = 0.0;
            for ( DataType member : members )
            {
                // Divergence functions must obey f(x,x) = 0, so we don't
                // need to compute the divergence between two identical objects
                if ( candidate != member )
                {
                    totalDivergence += 
                        this.divergenceFunction.evaluate(candidate, member); 
                }
                
                // Divergence functions are nonnegative, so if we're already
                // above the minimum, we can just stop counting
                if ( minTotalDivergence < totalDivergence )
                {
                    break;
                }
            }
            
            if ( medoid == null || minTotalDivergence > totalDivergence )
            {
                minTotalDivergence = totalDivergence;
                medoid = candidate;
            }
            
        }
        
        return new CentroidCluster<DataType>(medoid, members);
    }

}

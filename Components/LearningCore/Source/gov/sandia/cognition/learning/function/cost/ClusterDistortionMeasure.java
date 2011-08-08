/*
 * File:                ClusterDistortionMeasure.java
 * Authors:             Kevin R. Dixon
 * Company:             Sandia National Laboratories
 * Project:             Cognitive Foundry
 * 
 * Copyright Oct 14, 2008, Sandia Corporation.
 * Under the terms of Contract DE-AC04-94AL85000, there is a non-exclusive
 * license for use of this work by or on behalf of the U.S. Government. 
 * Export of this program may require a license from the United States
 * Government. See CopyrightHistory.txt for complete details.
 * 
 */

package gov.sandia.cognition.learning.function.cost;

import gov.sandia.cognition.annotation.PublicationReference;
import gov.sandia.cognition.annotation.PublicationType;
import gov.sandia.cognition.learning.algorithm.clustering.cluster.Cluster;
import gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterDivergenceFunction;
import gov.sandia.cognition.util.AbstractCloneableSerializable;
import java.util.Collection;

/**
 * Computes the objective measure for a clustering algorithm, based on the
 * internal "distortion" of each cluster.  The individual cluster measures
 * are based on a ClusterDivergenceFunction and summed over each cluster.
 * @see gov.sandia.cognition.learning.algorithm.clustering.divergence.ClusterDivergenceFunction
 * @param <DataType> Type of data to compute the distortion over
 * @param <ClusterType> Type of clusters to use
 * @author Kevin R. Dixon
 * @since 3.0
 */
@PublicationReference(
    author="Christopher M. Bishop",
    title="Pattern Recognition and Machine Learning",
    type=PublicationType.Book,
    year=2006,
    pages={424,428},
    notes="Section 9.1",
    url="http://research.microsoft.com/~cmbishop/PRML/"
)
public class ClusterDistortionMeasure<DataType,ClusterType extends Cluster<DataType>>
    extends AbstractCloneableSerializable
    implements CostFunction<Collection<? extends ClusterType>,ClusterDivergenceFunction<? super ClusterType, ? super DataType>>
{
    
    /**
     * Divergence function that defines the cost function
     */
    private ClusterDivergenceFunction<? super ClusterType, ? super DataType> costParameters;
    
    
    /** 
     * Creates a new instance of ClusterDistortionMeasure 
     */
    public ClusterDistortionMeasure()
    {
        this( null );
    }
    
    /**
     * Creates a new instance of ClusterDistortionMeasure 
     * @param costParameters
     * Divergence function that defines the cost function
     */
    public ClusterDistortionMeasure(
        ClusterDivergenceFunction<ClusterType, DataType> costParameters )
    {
        this.setCostParameters( costParameters );
    }
    

    @Override
    @SuppressWarnings("unchecked")
    public ClusterDistortionMeasure<DataType,ClusterType> clone()
    {
        return (ClusterDistortionMeasure<DataType,ClusterType>) super.clone();
    }    
    
    public Double evaluate(
        Collection<? extends ClusterType> target )
    {
        
        double sum = 0.0;
        for (ClusterType cluster : target)
        {
            sum += this.evaluate( cluster );
        }
        
        return sum;
        
    }

    
    /**
     * Evaluates the distortion for a single cluster
     * @param cluster
     * Cluster to consider
     * @return
     * Distortion for a single member
     */
    public Double evaluate(
        ClusterType cluster )
    {
        
        double sum = 0.0;
        for( DataType p : cluster.getMembers() )
        {
            this.costParameters.evaluate( cluster, p);
            sum += this.costParameters.evaluate( cluster, p );
        }
        
        return sum;
        
    }

    public ClusterDivergenceFunction<? super ClusterType, ? super DataType> getCostParameters()
    {
        return this.costParameters;
    }

    public void setCostParameters(
        final ClusterDivergenceFunction<? super ClusterType, ? super DataType> costParameters )
    {
        this.costParameters = costParameters;
    }

}
